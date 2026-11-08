package dsi.mitologia.modelo;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import dsi.mitologia.modelo.enums.EstadoLibertad;
import dsi.mitologia.modelo.enums.EstadoVital;
import dsi.mitologia.modelo.enums.TipoPersonaje;

/**
 * Clase OWL {@code Personaje} y sus subclases (Dios, Semidios, Heroe, Mortal, Criatura).
 *
 * <p>Los tipos son un conjunto porque OWL permite que un individuo pertenezca a varias clases
 * (Perseo es Heroe y Semidios) y Java no tiene herencia multiple de clases.</p>
 *
 * <p>Estado mutable: solo {@code estadoVital} y {@code estadoLibertad}, que en el OWL son
 * propiedades de datos con cardinalidad exactamente 1. Un enum por propiedad hace imposible estar
 * vivo y muerto a la vez. Las posesiones, favores, cautiverios, etc. NO se guardan aqui: son hechos
 * aparte (paquete {@code hechos}), para tener una sola fuente de verdad.</p>
 *
 * <p>En Drools, cambia el estado con {@code modify($p) { setEstadoVital(EstadoVital.MUERTO) }}.
 * La igualdad es por identificador.</p>
 */
public class Personaje implements EntidadMitologica {

    private final String id;
    private final String nombre;
    private final Set<TipoPersonaje> tipos;
    private EstadoVital estadoVital;
    private EstadoLibertad estadoLibertad;

    /** Crea un personaje vivo y libre (estado por defecto). */
    public Personaje(String id, String nombre, Set<TipoPersonaje> tipos) {
        this(id, nombre, tipos, EstadoVital.VIVO, EstadoLibertad.LIBRE);
    }

    public Personaje(String id, String nombre, Set<TipoPersonaje> tipos,
                     EstadoVital estadoVital, EstadoLibertad estadoLibertad) {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("El personaje necesita id");
        this.id = id;
        this.nombre = nombre == null ? id : nombre;
        this.tipos = tipos == null || tipos.isEmpty()
                ? Collections.<TipoPersonaje>emptySet()
                : Collections.unmodifiableSet(EnumSet.copyOf(tipos));
        setEstadoVital(estadoVital);
        setEstadoLibertad(estadoLibertad);
    }

    /** Copia independiente (mismo id y tipos, mismo estado) para otro escenario. */
    public Personaje copia() {
        return new Personaje(id, nombre, tipos, estadoVital, estadoLibertad);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public Set<TipoPersonaje> getTipos() {
        return tipos;
    }

    public boolean esDeTipo(TipoPersonaje tipo) {
        return tipos.contains(tipo);
    }

    // Getters booleanos para patrones Drools sencillos: Personaje(heroe), Personaje(dios)...
    // Los tipos no cambian, asi que estos getters son seguros con la reactividad de Drools.

    public boolean isDios() {
        return esDeTipo(TipoPersonaje.DIOS);
    }

    public boolean isSemidios() {
        return esDeTipo(TipoPersonaje.SEMIDIOS);
    }

    public boolean isHeroe() {
        return esDeTipo(TipoPersonaje.HEROE);
    }

    public boolean isMortal() {
        return esDeTipo(TipoPersonaje.MORTAL);
    }

    public boolean isCriatura() {
        return esDeTipo(TipoPersonaje.CRIATURA);
    }

    public EstadoVital getEstadoVital() {
        return estadoVital;
    }

    public void setEstadoVital(EstadoVital estadoVital) {
        if (estadoVital == null) throw new IllegalArgumentException("estado_vital tiene cardinalidad 1");
        this.estadoVital = estadoVital;
    }

    public EstadoLibertad getEstadoLibertad() {
        return estadoLibertad;
    }

    public void setEstadoLibertad(EstadoLibertad estadoLibertad) {
        if (estadoLibertad == null) throw new IllegalArgumentException("estado_libertad tiene cardinalidad 1");
        this.estadoLibertad = estadoLibertad;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Personaje && id.equals(((Personaje) o).id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return nombre + tipos + "(" + estadoVital.getValorOwl() + ", " + estadoLibertad.getValorOwl() + ")";
    }
}
