package dsi.mitologia.escenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dsi.mitologia.hechos.ProporcionaCapacidad;
import dsi.mitologia.modelo.Capacidad;
import dsi.mitologia.modelo.Objeto;
import dsi.mitologia.modelo.Personaje;

/**
 * Conocimiento del dominio: que personajes, objetos y capacidades existen, que objeto da que
 * capacidad, el parentesco y el estado POR DEFECTO con el que empieza cualquier escenario
 * (quien posee que, favores, enojos, cautiverios).
 *
 * <p>Se rellena una vez (normalmente con {@code CargadorOntologia} desde mitologia.owl) y no lo
 * modifica ninguna ejecucion: cada escenario crea su propio {@link EstadoAventura} copiando los
 * personajes. Los personajes guardados aqui son prototipos y nunca se insertan en Drools.</p>
 *
 * <p>Las relaciones por defecto se guardan como pares de identificadores para que cada
 * escenario construya hechos nuevos con sus propias copias.</p>
 */
public class DominioMitologico {

    private final Map<String, Personaje> personajes = new LinkedHashMap<String, Personaje>();
    private final Map<String, Objeto> objetos = new LinkedHashMap<String, Objeto>();
    private final Map<String, Capacidad> capacidades = new LinkedHashMap<String, Capacidad>();
    private final List<ProporcionaCapacidad> proporciona = new ArrayList<ProporcionaCapacidad>();

    private final List<String[]> filiaciones = new ArrayList<String[]>();
    private final List<String[]> posesiones = new ArrayList<String[]>();
    private final List<String[]> favores = new ArrayList<String[]>();
    private final List<String[]> enojos = new ArrayList<String[]>();
    private final List<String[]> cautiverios = new ArrayList<String[]>();
    private final List<String[]> capacidadesPropias = new ArrayList<String[]>();

    // ---- Entidades ----

    public Personaje registrarPersonaje(Personaje p) {
        comprobarIdLibre(p.getId());
        personajes.put(p.getId(), p);
        return p;
    }

    public Objeto registrarObjeto(Objeto o) {
        comprobarIdLibre(o.getId());
        objetos.put(o.getId(), o);
        return o;
    }

    public Capacidad registrarCapacidad(Capacidad c) {
        comprobarIdLibre(c.getId());
        capacidades.put(c.getId(), c);
        return c;
    }

    // ---- Conocimiento estatico ----

    public void registrarProporciona(String objetoId, String capacidadId) {
        ProporcionaCapacidad pc = new ProporcionaCapacidad(getObjeto(objetoId), getCapacidad(capacidadId));
        if (!proporciona.contains(pc)) proporciona.add(pc);
    }

    public void registrarFiliacion(String hijoId, String progenitorId) {
        anadirPar(filiaciones, getPersonaje(hijoId).getId(), getPersonaje(progenitorId).getId());
    }

    // ---- Estado por defecto ----

    public void registrarPosesion(String personajeId, String objetoId) {
        getPersonaje(personajeId);
        getObjeto(objetoId);
        for (String[] par : posesiones) {
            if (par[1].equals(objetoId) && !par[0].equals(personajeId)) {
                throw new IllegalStateException(objetoId + " ya pertenece por defecto a " + par[0]);
            }
        }
        anadirPar(posesiones, personajeId, objetoId);
    }

    public void registrarFavor(String personajeId, String diosId) {
        anadirPar(favores, getPersonaje(personajeId).getId(), getPersonaje(diosId).getId());
    }

    public void registrarEnojo(String personajeId, String diosId) {
        anadirPar(enojos, getPersonaje(personajeId).getId(), getPersonaje(diosId).getId());
    }

    public void registrarCautiverio(String captorId, String presoId) {
        anadirPar(cautiverios, getPersonaje(captorId).getId(), getPersonaje(presoId).getId());
    }

    public void registrarCapacidadPropia(String personajeId, String capacidadId) {
        anadirPar(capacidadesPropias, getPersonaje(personajeId).getId(), getCapacidad(capacidadId).getId());
    }

    // ---- Consultas ----

    public Personaje getPersonaje(String id) {
        return buscar(personajes, id, "personaje");
    }

    public Objeto getObjeto(String id) {
        return buscar(objetos, id, "objeto");
    }

    public Capacidad getCapacidad(String id) {
        return buscar(capacidades, id, "capacidad");
    }

    public boolean existePersonaje(String id) {
        return personajes.containsKey(id);
    }

    public boolean existeObjeto(String id) {
        return objetos.containsKey(id);
    }

    public boolean existeCapacidad(String id) {
        return capacidades.containsKey(id);
    }

    /** Prototipos (no modificar ni insertar en Drools: usar las copias de EstadoAventura). */
    public Collection<Personaje> getPersonajes() {
        return Collections.unmodifiableCollection(personajes.values());
    }

    public Collection<Objeto> getObjetos() {
        return Collections.unmodifiableCollection(objetos.values());
    }

    public Collection<Capacidad> getCapacidades() {
        return Collections.unmodifiableCollection(capacidades.values());
    }

    public List<ProporcionaCapacidad> getProporciona() {
        return Collections.unmodifiableList(proporciona);
    }

    List<String[]> getFiliaciones() {
        return filiaciones;
    }

    List<String[]> getPosesiones() {
        return posesiones;
    }

    List<String[]> getFavores() {
        return favores;
    }

    List<String[]> getEnojos() {
        return enojos;
    }

    List<String[]> getCautiverios() {
        return cautiverios;
    }

    List<String[]> getCapacidadesPropias() {
        return capacidadesPropias;
    }

    // ---- Auxiliares ----

    private void comprobarIdLibre(String id) {
        if (personajes.containsKey(id) || objetos.containsKey(id) || capacidades.containsKey(id)) {
            throw new IllegalStateException("Identificador repetido: " + id);
        }
    }

    private static void anadirPar(List<String[]> lista, String a, String b) {
        for (String[] par : lista) {
            if (par[0].equals(a) && par[1].equals(b)) return;
        }
        lista.add(new String[] {a, b});
    }

    private static <T> T buscar(Map<String, T> mapa, String id, String que) {
        T t = mapa.get(id);
        if (t == null) throw new IllegalArgumentException("No existe el " + que + " '" + id + "'");
        return t;
    }
}
