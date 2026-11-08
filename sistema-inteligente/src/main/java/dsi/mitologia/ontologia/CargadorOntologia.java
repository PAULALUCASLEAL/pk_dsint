package dsi.mitologia.ontologia;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dsi.mitologia.escenario.DominioMitologico;
import dsi.mitologia.modelo.Capacidad;
import dsi.mitologia.modelo.Objeto;
import dsi.mitologia.modelo.Personaje;
import dsi.mitologia.modelo.enums.CategoriaObjeto;
import dsi.mitologia.modelo.enums.EstadoLibertad;
import dsi.mitologia.modelo.enums.EstadoVital;
import dsi.mitologia.modelo.enums.TipoPersonaje;

/**
 * Lee los INDIVIDUOS de mitologia.owl (RDF/XML) y construye un {@link DominioMitologico}.
 *
 * <p>Es procesamiento de datos, no razonamiento: no aplica dominios, rangos ni restricciones
 * (eso lo haria un razonador como HermiT). Solo traduce los asertos que estan escritos.
 * No modifica el fichero. Usa el DOM estandar de Java 8 (sin OWL API) para no incorporar dependencias.</p>
 *
 * <p>Admite las serializaciones de Protege 5 ({@code owl:NamedIndividual} / {@code rdf:Description}
 * con {@code rdf:type}) y la de Protege 3 (elemento con el nombre de la clase).</p>
 *
 * <p>Lo que no sabe traducir lo anota en {@link #getAvisos()} en lugar de inventarlo.</p>
 */
public class CargadorOntologia {

    private static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String OWL = "http://www.w3.org/2002/07/owl#";

    private final List<String> avisos = new ArrayList<String>();

    /** Nombres locales de propiedades que ya se traducen. */
    private static final Set<String> PROPIEDADES_CONOCIDAS = new LinkedHashSet<String>();
    static {
        Collections.addAll(PROPIEDADES_CONOCIDAS, "type", "comment", "label", "estado_vital", "estado_libertad",
                "tiene_objeto", "esta_en_posesion_de", "proporciona_capacidad", "es_hijo_de", "tiene_hijo",
                "tiene_favor", "tiene_enojo", "apresa_a", "esta_preso_por", "tiene_capacidad");
    }

    public DominioMitologico cargar(File owl) {
        avisos.clear();
        Map<String, Element> individuos = new LinkedHashMap<String, Element>();
        Map<String, Set<String>> tipos = new LinkedHashMap<String, Set<String>>();
        leerIndividuos(owl, individuos, tipos);

        DominioMitologico d = new DominioMitologico();
        // 1.a pasada: entidades
        for (Map.Entry<String, Set<String>> e : tipos.entrySet()) {
            crearEntidad(d, e.getKey(), e.getValue(), individuos.get(e.getKey()));
        }
        // 2.a pasada: relaciones
        for (Map.Entry<String, Element> e : individuos.entrySet()) {
            leerRelaciones(d, e.getKey(), e.getValue());
        }
        return d;
    }

    public List<String> getAvisos() {
        return Collections.unmodifiableList(avisos);
    }

    // ------------------------------------------------------------------

    private void leerIndividuos(File owl, Map<String, Element> individuos, Map<String, Set<String>> tipos) {
        Element raiz;
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setNamespaceAware(true);
            DocumentBuilder b = f.newDocumentBuilder();
            raiz = b.parse(owl).getDocumentElement();
        } catch (Exception ex) {
            throw new IllegalArgumentException("No se puede leer la ontologia " + owl, ex);
        }
        for (Node n = raiz.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (!(n instanceof Element)) continue;
            Element el = (Element) n;
            String about = el.getAttributeNS(RDF, "about");
            if (about.isEmpty() || esDeclaracion(el)) continue;
            String id = local(about);
            Set<String> ts = new LinkedHashSet<String>();
            if (!esNodoGenerico(el)) ts.add(el.getLocalName());
            for (Element hijo : hijos(el)) {
                if (RDF.equals(hijo.getNamespaceURI()) && "type".equals(hijo.getLocalName())) {
                    String t = local(hijo.getAttributeNS(RDF, "resource"));
                    if (!"NamedIndividual".equals(t)) ts.add(t);
                }
            }
            individuos.put(id, el);
            tipos.put(id, ts);
        }
    }

    private void crearEntidad(DominioMitologico d, String id, Set<String> clases, Element el) {
        Set<TipoPersonaje> tp = EnumSet.noneOf(TipoPersonaje.class);
        Set<CategoriaObjeto> co = EnumSet.noneOf(CategoriaObjeto.class);
        boolean personaje = false, objeto = false, capacidad = false;
        for (String c : clases) {
            TipoPersonaje t = TipoPersonaje.desdeClaseOwl(c);
            CategoriaObjeto o = CategoriaObjeto.desdeClaseOwl(c);
            if (t != null) { tp.add(t); personaje = true; }
            else if ("Personaje".equals(c)) personaje = true;
            else if (o != null) { co.add(o); objeto = true; }
            else if ("Objeto".equals(c)) objeto = true;
            else if ("Capacidad".equals(c)) capacidad = true;
            else avisos.add(id + ": clase no traducida " + c);
        }
        int n = (personaje ? 1 : 0) + (objeto ? 1 : 0) + (capacidad ? 1 : 0);
        if (n != 1) {
            avisos.add(id + ": no se carga (tipos " + clases + "; Personaje, Objeto y Capacidad son disjuntas)");
            return;
        }
        if (personaje) {
            String vital = texto(el, "estado_vital");
            String libertad = texto(el, "estado_libertad");
            if (vital == null || libertad == null) {
                avisos.add(id + ": falta estado_vital o estado_libertad (cardinalidad 1); se asume vivo/libre");
            }
            d.registrarPersonaje(new Personaje(id, id, tp,
                    vital == null ? EstadoVital.VIVO : EstadoVital.desdeOwl(vital),
                    libertad == null ? EstadoLibertad.LIBRE : EstadoLibertad.desdeOwl(libertad)));
        } else if (objeto) {
            d.registrarObjeto(new Objeto(id, id, co));
        } else {
            d.registrarCapacidad(new Capacidad(id, id));
        }
    }

    private void leerRelaciones(DominioMitologico d, String id, Element el) {
        boolean esPersonaje = d.existePersonaje(id);
        boolean esObjeto = d.existeObjeto(id);
        if (!esPersonaje && !esObjeto && !d.existeCapacidad(id)) return;
        for (Element h : hijos(el)) {
            String prop = h.getLocalName();
            String destino = local(h.getAttributeNS(RDF, "resource"));
            if (!PROPIEDADES_CONOCIDAS.contains(prop)) {
                avisos.add(id + ": propiedad no traducida " + prop + (destino.isEmpty() ? "" : " " + destino));
                continue;
            }
            try {
                if ("tiene_objeto".equals(prop)) d.registrarPosesion(id, destino);
                else if ("esta_en_posesion_de".equals(prop)) d.registrarPosesion(destino, id);
                else if ("proporciona_capacidad".equals(prop)) d.registrarProporciona(id, destino);
                else if ("es_hijo_de".equals(prop)) d.registrarFiliacion(id, destino);
                else if ("tiene_hijo".equals(prop)) d.registrarFiliacion(destino, id);
                else if ("tiene_favor".equals(prop)) d.registrarFavor(id, destino);
                else if ("tiene_enojo".equals(prop)) d.registrarEnojo(id, destino);
                else if ("apresa_a".equals(prop)) d.registrarCautiverio(id, destino);
                else if ("esta_preso_por".equals(prop)) d.registrarCautiverio(destino, id);
                else if ("tiene_capacidad".equals(prop)) d.registrarCapacidadPropia(id, destino);
            } catch (RuntimeException ex) {
                avisos.add(id + ": " + prop + " " + destino + " no se carga: " + ex.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------

    private static boolean esDeclaracion(Element el) {
        if (!OWL.equals(el.getNamespaceURI())) return false;
        String n = el.getLocalName();
        return !"NamedIndividual".equals(n);
    }

    private static boolean esNodoGenerico(Element el) {
        return (OWL.equals(el.getNamespaceURI()) && "NamedIndividual".equals(el.getLocalName()))
                || (RDF.equals(el.getNamespaceURI()) && "Description".equals(el.getLocalName()));
    }

    private static List<Element> hijos(Element el) {
        List<Element> lista = new ArrayList<Element>();
        NodeList nl = el.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i) instanceof Element) lista.add((Element) nl.item(i));
        }
        return lista;
    }

    private static String texto(Element el, String propiedad) {
        for (Element h : hijos(el)) {
            if (propiedad.equals(h.getLocalName())) return h.getTextContent().trim();
        }
        return null;
    }

    private static String local(String iri) {
        int i = iri.lastIndexOf('#');
        return i >= 0 ? iri.substring(i + 1) : iri;
    }
}
