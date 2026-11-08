package dsi.mitologia.escenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dsi.mitologia.hechos.CapacidadDisponible;
import dsi.mitologia.hechos.Cautiverio;
import dsi.mitologia.hechos.Enojo;
import dsi.mitologia.hechos.Favor;
import dsi.mitologia.hechos.Filiacion;
import dsi.mitologia.hechos.Liberacion;
import dsi.mitologia.hechos.Localizacion;
import dsi.mitologia.hechos.Muerte;
import dsi.mitologia.hechos.Posesion;
import dsi.mitologia.hechos.ProporcionaCapacidad;
import dsi.mitologia.modelo.Capacidad;
import dsi.mitologia.modelo.EntidadMitologica;
import dsi.mitologia.modelo.Objeto;
import dsi.mitologia.modelo.Personaje;
import dsi.mitologia.modelo.enums.EstadoLibertad;
import dsi.mitologia.modelo.enums.EstadoVital;
import dsi.mitologia.modelo.enums.OrigenCapacidad;

/**
 * Estado de UNA aventura: copias propias de los personajes y todos los hechos dinamicos.
 *
 * <p>Responsabilidades:</p>
 * <ol>
 *   <li>Preparar la memoria de trabajo inicial (estado por defecto + condiciones del escenario).</li>
 *   <li>Ofrecer operaciones de cambio de estado que comprueban la integridad
 *       (sin posesiones duplicadas, sin matar a un muerto, sin liberar a un libre...).
 *       Estas operaciones NO deciden cuando ocurre algo: eso lo deciden las reglas.</li>
 *   <li>Entregar los hechos a Drools ({@link #getHechos()}) y recoger el estado final
 *       ({@link #sincronizarDesde(Collection)}).</li>
 * </ol>
 *
 * <p>Errores de integridad: {@link IllegalStateException}. Altas repetidas (un favor que ya
 * existe) devuelven {@code false} sin duplicar.</p>
 *
 * <p>No hay nada estatico: dos escenarios no comparten ningun objeto mutable.</p>
 */
public class EstadoAventura {

    private final DominioMitologico dominio;
    private final Map<String, Personaje> personajes = new LinkedHashMap<String, Personaje>();

    private final List<Posesion> posesiones = new ArrayList<Posesion>();
    private final List<CapacidadDisponible> capacidades = new ArrayList<CapacidadDisponible>();
    private final List<Favor> favores = new ArrayList<Favor>();
    private final List<Enojo> enojos = new ArrayList<Enojo>();
    private final List<Filiacion> filiaciones = new ArrayList<Filiacion>();
    private final List<Localizacion> localizaciones = new ArrayList<Localizacion>();
    private final List<Cautiverio> cautiverios = new ArrayList<Cautiverio>();
    private final List<Muerte> muertes = new ArrayList<Muerte>();
    private final List<Liberacion> liberaciones = new ArrayList<Liberacion>();

    private EstadoAventura(DominioMitologico dominio) {
        if (dominio == null) throw new IllegalArgumentException("Falta el dominio");
        this.dominio = dominio;
    }

    /** Estado inicial de un escenario: copia los personajes y aplica el estado por defecto del dominio. */
    public static EstadoAventura inicial(DominioMitologico dominio) {
        EstadoAventura e = new EstadoAventura(dominio);
        for (Personaje prototipo : dominio.getPersonajes()) {
            e.personajes.put(prototipo.getId(), prototipo.copia());
        }
        for (String[] f : dominio.getFiliaciones()) {
            e.filiaciones.add(new Filiacion(e.getPersonaje(f[0]), e.getPersonaje(f[1])));
        }
        for (String[] p : dominio.getPosesiones()) {
            e.otorgarObjeto(e.getPersonaje(p[0]), e.getObjeto(p[1]));
        }
        for (String[] f : dominio.getFavores()) {
            e.agregarFavor(e.getPersonaje(f[0]), e.getPersonaje(f[1]));
        }
        for (String[] f : dominio.getEnojos()) {
            e.agregarEnojo(e.getPersonaje(f[0]), e.getPersonaje(f[1]));
        }
        for (String[] c : dominio.getCautiverios()) {
            // Por defecto el preso puede venir ya marcado como preso en el OWL: no se usa apresar().
            Personaje preso = e.getPersonaje(c[1]);
            preso.setEstadoLibertad(EstadoLibertad.PRESO);
            e.cautiverios.add(new Cautiverio(e.getPersonaje(c[0]), preso));
        }
        for (String[] c : dominio.getCapacidadesPropias()) {
            e.concederCapacidad(CapacidadDisponible.propia(e.getPersonaje(c[0]), e.getCapacidad(c[1])));
        }
        return e;
    }

    /** Copia profunda independiente (para comparar alternativas desde un mismo punto). */
    public EstadoAventura copia() {
        EstadoAventura e = new EstadoAventura(dominio);
        for (Personaje p : personajes.values()) e.personajes.put(p.getId(), p.copia());
        for (Posesion p : posesiones) {
            e.posesiones.add(new Posesion(e.getPersonaje(p.getPoseedor().getId()), p.getObjeto()));
        }
        for (CapacidadDisponible c : capacidades) e.capacidades.add(e.recrear(c));
        for (Favor f : favores) e.favores.add(new Favor(e.mio(f.getPersonaje()), e.mio(f.getDios())));
        for (Enojo f : enojos) e.enojos.add(new Enojo(e.mio(f.getPersonaje()), e.mio(f.getDios())));
        for (Filiacion f : filiaciones) e.filiaciones.add(new Filiacion(e.mio(f.getHijo()), e.mio(f.getProgenitor())));
        for (Localizacion l : localizaciones) e.localizaciones.add(e.recrear(l));
        for (Cautiverio c : cautiverios) e.cautiverios.add(new Cautiverio(e.mio(c.getCaptor()), e.mio(c.getPreso())));
        for (Muerte m : muertes) e.muertes.add(new Muerte(e.mio(m.getAsesino()), e.mio(m.getVictima())));
        for (Liberacion l : liberaciones) e.liberaciones.add(new Liberacion(e.mio(l.getLibertador()), e.mio(l.getLiberado())));
        return e;
    }

    // =====================================================================
    // Consultas de entidades
    // =====================================================================

    public DominioMitologico getDominio() {
        return dominio;
    }

    /** Copia del personaje propia de este escenario. */
    public Personaje getPersonaje(String id) {
        Personaje p = personajes.get(id);
        if (p == null) throw new IllegalArgumentException("No existe el personaje '" + id + "'");
        return p;
    }

    public Objeto getObjeto(String id) {
        return dominio.getObjeto(id);
    }

    public Capacidad getCapacidad(String id) {
        return dominio.getCapacidad(id);
    }

    public Collection<Personaje> getPersonajes() {
        return Collections.unmodifiableCollection(personajes.values());
    }

    // =====================================================================
    // Posesiones (fuente de verdad: los hechos Posesion)
    // =====================================================================

    /** Quien posee el objeto, o {@code null} si no lo tiene nadie. */
    public Personaje poseedorDe(Objeto objeto) {
        Posesion p = posesionDe(objeto);
        return p == null ? null : p.getPoseedor();
    }

    public boolean posee(Personaje personaje, Objeto objeto) {
        Posesion p = posesionDe(objeto);
        return p != null && p.getPoseedor().equals(personaje);
    }

    public boolean posee(String personajeId, String objetoId) {
        return posee(getPersonaje(personajeId), getObjeto(objetoId));
    }

    public List<Objeto> objetosDe(Personaje personaje) {
        List<Objeto> lista = new ArrayList<Objeto>();
        for (Posesion p : posesiones) {
            if (p.getPoseedor().equals(personaje)) lista.add(p.getObjeto());
        }
        return lista;
    }

    /** Da un objeto que no tiene nadie (estado inicial, objeto que aparece, objeto creado). */
    public Posesion otorgarObjeto(Personaje receptor, Objeto objeto) {
        Personaje actual = poseedorDe(objeto);
        if (actual != null) {
            throw new IllegalStateException(objeto.getId() + " ya lo posee " + actual.getId());
        }
        Posesion p = new Posesion(mio(receptor), objeto);
        posesiones.add(p);
        return p;
    }

    /**
     * Pasa el objeto de {@code origen} a {@code destino} modificando el MISMO hecho Posesion.
     * El origen pierde las capacidades que le daba ese objeto.
     */
    public Posesion transferirObjeto(Personaje origen, Personaje destino, Objeto objeto) {
        Posesion p = posesionDe(objeto);
        if (p == null || !p.getPoseedor().equals(origen)) {
            throw new IllegalStateException(origen.getId() + " no posee " + objeto.getId());
        }
        if (origen.equals(destino)) {
            throw new IllegalStateException(origen.getId() + " ya posee " + objeto.getId());
        }
        exigirVivo(destino, "recibir " + objeto.getId());
        p.setPoseedor(mio(destino));
        retirarCapacidadesPorFuente(origen, objeto);
        return p;
    }

    /** El poseedor deja de tener el objeto y nadie lo recibe ("Atenea no tiene Escudo de Bronce"). */
    public void retirarObjeto(Personaje poseedor, Objeto objeto) {
        Posesion p = posesionDe(objeto);
        if (p == null || !p.getPoseedor().equals(poseedor)) {
            throw new IllegalStateException(poseedor.getId() + " no posee " + objeto.getId());
        }
        posesiones.remove(p);
        retirarCapacidadesPorFuente(poseedor, objeto);
    }

    /** Sustituye un objeto por otro (Hefesto pule el EscudoBronce y queda EscudoEspejo). */
    public Posesion transformarObjeto(Personaje poseedor, Objeto original, Objeto resultado) {
        if (poseedorDe(resultado) != null) {
            throw new IllegalStateException(resultado.getId() + " ya existe en posesion de alguien");
        }
        retirarObjeto(poseedor, original);
        return otorgarObjeto(poseedor, resultado);
    }

    // =====================================================================
    // Capacidades
    // =====================================================================

    public boolean proporciona(Objeto objeto, Capacidad capacidad) {
        return dominio.getProporciona().contains(new ProporcionaCapacidad(objeto, capacidad));
    }

    /**
     * Registra que un personaje dispone de una capacidad por una fuente concreta.
     * Comprueba que la fuente es valida en ese momento (posee el objeto y el objeto da esa capacidad,
     * o tiene capturada la montura viva). Devuelve {@code false} si ya existia.
     */
    public boolean concederCapacidad(CapacidadDisponible c) {
        Personaje p = mio(c.getPersonaje());
        exigirVivo(p, "obtener " + c.getCapacidad().getId());
        OrigenCapacidad origen = c.getOrigen();
        if (origen == OrigenCapacidad.OBJETO || origen == OrigenCapacidad.CONJURO) {
            Objeto o = (Objeto) c.getFuente();
            if (!posee(p, o)) {
                throw new IllegalStateException(p.getId() + " no posee " + o.getId());
            }
            if (!proporciona(o, c.getCapacidad())) {
                throw new IllegalStateException(o.getId() + " no proporciona " + c.getCapacidad().getId());
            }
        } else if (origen == OrigenCapacidad.MONTURA) {
            Personaje montura = mio((Personaje) c.getFuente());
            if (!tienePreso(p, montura) || montura.getEstadoVital() != EstadoVital.VIVO) {
                throw new IllegalStateException(p.getId() + " no tiene capturada a " + montura.getId());
            }
        }
        if (capacidades.contains(c)) return false;
        capacidades.add(recrear(c));
        return true;
    }

    public boolean retirarCapacidad(CapacidadDisponible c) {
        return capacidades.remove(c);
    }

    /** Dispone de la capacidad por cualquier fuente? */
    public boolean tieneCapacidad(Personaje personaje, Capacidad capacidad) {
        return !fuentesDe(personaje, capacidad).isEmpty();
    }

    public boolean tieneCapacidad(String personajeId, String capacidadId) {
        return tieneCapacidad(getPersonaje(personajeId), getCapacidad(capacidadId));
    }

    public List<CapacidadDisponible> fuentesDe(Personaje personaje, Capacidad capacidad) {
        List<CapacidadDisponible> lista = new ArrayList<CapacidadDisponible>();
        for (CapacidadDisponible c : capacidades) {
            if (c.getPersonaje().equals(personaje) && c.getCapacidad().equals(capacidad)) lista.add(c);
        }
        return lista;
    }

    // =====================================================================
    // Vida y muerte
    // =====================================================================

    /** {@code asesino} mata a {@code victima}. El asesino puede ser {@code null} (muerte sin autor). */
    public void matar(Personaje asesino, Personaje victima) {
        Personaje v = mio(victima);
        if (v.getEstadoVital() == EstadoVital.MUERTO) {
            throw new IllegalStateException(v.getId() + " ya esta muerto");
        }
        if (asesino != null) {
            exigirVivo(asesino, "matar");
            if (asesino.equals(v)) throw new IllegalStateException("Un personaje no se mata a si mismo con matar()");
        }
        v.setEstadoVital(EstadoVital.MUERTO);
        if (asesino != null) muertes.add(new Muerte(mio(asesino), v));
        retirarCapacidadesDeMontura(v);
    }

    /** Muerte sin autor (p. ej. Icaro al caer, o el heroe que toca la cabeza de Medusa sin zurron). */
    public void morir(Personaje personaje) {
        matar(null, personaje);
    }

    public boolean haMatado(String asesinoId, String victimaId) {
        return muertes.contains(new Muerte(getPersonaje(asesinoId), getPersonaje(victimaId)));
    }

    // =====================================================================
    // Cautiverio y liberacion (Cautiverio sincronizado con estadoLibertad)
    // =====================================================================

    public Cautiverio apresar(Personaje captor, Personaje preso) {
        Personaje c = mio(captor);
        Personaje p = mio(preso);
        exigirVivo(c, "apresar");
        exigirVivo(p, "ser apresado");
        if (c.equals(p)) throw new IllegalStateException("Nadie se apresa a si mismo");
        if (p.getEstadoLibertad() == EstadoLibertad.PRESO) {
            throw new IllegalStateException(p.getId() + " ya esta preso");
        }
        p.setEstadoLibertad(EstadoLibertad.PRESO);
        Cautiverio cautiverio = new Cautiverio(c, p);
        cautiverios.add(cautiverio);
        return cautiverio;
    }

    /** Libera a un preso: retira todos sus Cautiverio, lo deja LIBRE y registra la Liberacion. */
    public Liberacion liberar(Personaje libertador, Personaje preso) {
        Personaje l = mio(libertador);
        Personaje p = mio(preso);
        exigirVivo(l, "liberar");
        if (p.getEstadoLibertad() != EstadoLibertad.PRESO) {
            throw new IllegalStateException(p.getId() + " ya esta libre");
        }
        for (Iterator<Cautiverio> it = cautiverios.iterator(); it.hasNext();) {
            if (it.next().getPreso().equals(p)) it.remove();
        }
        p.setEstadoLibertad(EstadoLibertad.LIBRE);
        retirarCapacidadesDeMontura(p);
        Liberacion liberacion = new Liberacion(l, p);
        if (!liberaciones.contains(liberacion)) liberaciones.add(liberacion);
        return liberacion;
    }

    /** Captor actual del preso, o {@code null}. */
    public Personaje captorDe(Personaje preso) {
        for (Cautiverio c : cautiverios) {
            if (c.getPreso().equals(preso)) return c.getCaptor();
        }
        return null;
    }

    public boolean tienePreso(Personaje captor, Personaje preso) {
        return cautiverios.contains(new Cautiverio(captor, preso));
    }

    public boolean tienePreso(String captorId, String presoId) {
        return tienePreso(getPersonaje(captorId), getPersonaje(presoId));
    }

    public boolean haLiberado(String libertadorId, String liberadoId) {
        return liberaciones.contains(new Liberacion(getPersonaje(libertadorId), getPersonaje(liberadoId)));
    }

    // =====================================================================
    // Localizacion, favores y enojos
    // =====================================================================

    public boolean registrarLocalizacion(Personaje localizador, Personaje localizado) {
        return anadirSiFalta(localizaciones, new Localizacion(mio(localizador), mio(localizado)));
    }

    public boolean registrarLocalizacion(Personaje localizador, Objeto localizado) {
        return anadirSiFalta(localizaciones, new Localizacion(mio(localizador), localizado));
    }

    public boolean haLocalizado(String localizadorId, String entidadId) {
        for (Localizacion l : localizaciones) {
            if (l.getLocalizador().getId().equals(localizadorId) && l.getLocalizado().getId().equals(entidadId)) {
                return true;
            }
        }
        return false;
    }

    /** El rango OWL de tiene_favor es Dios: se rechaza un "dios" que no lo sea. */
    public boolean agregarFavor(Personaje personaje, Personaje dios) {
        exigirDios(dios);
        return anadirSiFalta(favores, new Favor(mio(personaje), mio(dios)));
    }

    public boolean retirarFavor(Personaje personaje, Personaje dios) {
        return favores.remove(new Favor(personaje, dios));
    }

    public boolean tieneFavor(Personaje personaje, Personaje dios) {
        return favores.contains(new Favor(personaje, dios));
    }

    public boolean agregarEnojo(Personaje personaje, Personaje dios) {
        exigirDios(dios);
        return anadirSiFalta(enojos, new Enojo(mio(personaje), mio(dios)));
    }

    public boolean retirarEnojo(Personaje personaje, Personaje dios) {
        return enojos.remove(new Enojo(personaje, dios));
    }

    public boolean tieneEnojo(Personaje personaje, Personaje dios) {
        return enojos.contains(new Enojo(personaje, dios));
    }

    // =====================================================================
    // Acceso de solo lectura a los hechos
    // =====================================================================

    public List<Posesion> getPosesiones() {
        return Collections.unmodifiableList(posesiones);
    }

    public List<CapacidadDisponible> getCapacidades() {
        return Collections.unmodifiableList(capacidades);
    }

    public List<Favor> getFavores() {
        return Collections.unmodifiableList(favores);
    }

    public List<Enojo> getEnojos() {
        return Collections.unmodifiableList(enojos);
    }

    public List<Filiacion> getFiliaciones() {
        return Collections.unmodifiableList(filiaciones);
    }

    public List<Localizacion> getLocalizaciones() {
        return Collections.unmodifiableList(localizaciones);
    }

    public List<Cautiverio> getCautiverios() {
        return Collections.unmodifiableList(cautiverios);
    }

    public List<Muerte> getMuertes() {
        return Collections.unmodifiableList(muertes);
    }

    public List<Liberacion> getLiberaciones() {
        return Collections.unmodifiableList(liberaciones);
    }

    // =====================================================================
    // Puente con Drools (sin depender de la API de Drools)
    // =====================================================================

    /**
     * Todos los hechos que hay que insertar en una KieSession nueva: personajes, objetos,
     * capacidades, conocimiento estatico y hechos dinamicos de este escenario.
     */
    public List<Object> getHechos() {
        List<Object> hechos = new ArrayList<Object>();
        hechos.addAll(personajes.values());
        hechos.addAll(dominio.getObjetos());
        hechos.addAll(dominio.getCapacidades());
        hechos.addAll(dominio.getProporciona());
        hechos.addAll(filiaciones);
        hechos.addAll(posesiones);
        hechos.addAll(capacidades);
        hechos.addAll(favores);
        hechos.addAll(enojos);
        hechos.addAll(localizaciones);
        hechos.addAll(cautiverios);
        hechos.addAll(muertes);
        hechos.addAll(liberaciones);
        return hechos;
    }

    /**
     * Recoge el estado final desde la memoria de trabajo (p. ej. {@code kieSession.getObjects()}).
     * Las reglas habran insertado, modificado o retirado hechos; aqui solo se copian.
     */
    public void sincronizarDesde(Collection<?> hechosSesion) {
        posesiones.clear();
        capacidades.clear();
        favores.clear();
        enojos.clear();
        localizaciones.clear();
        cautiverios.clear();
        muertes.clear();
        liberaciones.clear();
        for (Object h : hechosSesion) {
            if (h instanceof Personaje) personajes.put(((Personaje) h).getId(), (Personaje) h);
            else if (h instanceof Posesion) posesiones.add((Posesion) h);
            else if (h instanceof CapacidadDisponible) capacidades.add((CapacidadDisponible) h);
            else if (h instanceof Favor) favores.add((Favor) h);
            else if (h instanceof Enojo) enojos.add((Enojo) h);
            else if (h instanceof Localizacion) localizaciones.add((Localizacion) h);
            else if (h instanceof Cautiverio) cautiverios.add((Cautiverio) h);
            else if (h instanceof Muerte) muertes.add((Muerte) h);
            else if (h instanceof Liberacion) liberaciones.add((Liberacion) h);
        }
    }

    // =====================================================================
    // Auxiliares privados
    // =====================================================================

    private Posesion posesionDe(Objeto objeto) {
        for (Posesion p : posesiones) {
            if (p.getObjeto().equals(objeto)) return p;
        }
        return null;
    }

    /** Devuelve la copia de este escenario (evita mezclar personajes de otros escenarios). */
    private Personaje mio(Personaje p) {
        return getPersonaje(p.getId());
    }

    private void retirarCapacidadesPorFuente(Personaje personaje, Objeto objeto) {
        for (Iterator<CapacidadDisponible> it = capacidades.iterator(); it.hasNext();) {
            CapacidadDisponible c = it.next();
            if (c.getPersonaje().equals(personaje) && objeto.equals(c.getFuente())) it.remove();
        }
    }

    private void retirarCapacidadesDeMontura(Personaje montura) {
        for (Iterator<CapacidadDisponible> it = capacidades.iterator(); it.hasNext();) {
            CapacidadDisponible c = it.next();
            if (c.getOrigen() == OrigenCapacidad.MONTURA && montura.equals(c.getFuente())) it.remove();
        }
    }

    private CapacidadDisponible recrear(CapacidadDisponible c) {
        Personaje p = mio(c.getPersonaje());
        switch (c.getOrigen()) {
            case PROPIA:
                return CapacidadDisponible.propia(p, c.getCapacidad());
            case OBJETO:
                return CapacidadDisponible.deObjeto(p, c.getCapacidad(), (Objeto) c.getFuente());
            case CONJURO:
                return CapacidadDisponible.deConjuro(p, c.getCapacidad(), (Objeto) c.getFuente());
            default:
                return CapacidadDisponible.deMontura(p, c.getCapacidad(), mio((Personaje) c.getFuente()));
        }
    }

    private Localizacion recrear(Localizacion l) {
        EntidadMitologica e = l.getLocalizado();
        return e instanceof Personaje
                ? new Localizacion(mio(l.getLocalizador()), mio((Personaje) e))
                : new Localizacion(mio(l.getLocalizador()), (Objeto) e);
    }

    private void exigirVivo(Personaje p, String accion) {
        if (mio(p).getEstadoVital() != EstadoVital.VIVO) {
            throw new IllegalStateException(p.getId() + " esta muerto y no puede " + accion);
        }
    }

    private void exigirDios(Personaje dios) {
        if (!mio(dios).isDios()) {
            throw new IllegalStateException(dios.getId() + " no es un Dios (rango de tiene_favor/tiene_enojo)");
        }
    }

    private static <T> boolean anadirSiFalta(List<T> lista, T hecho) {
        if (lista.contains(hecho)) return false;
        lista.add(hecho);
        return true;
    }
}
