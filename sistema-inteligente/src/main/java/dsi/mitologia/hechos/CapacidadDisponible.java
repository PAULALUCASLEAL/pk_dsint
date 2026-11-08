package dsi.mitologia.hechos;

import java.util.Objects;

import dsi.mitologia.modelo.Capacidad;
import dsi.mitologia.modelo.EntidadMitologica;
import dsi.mitologia.modelo.Objeto;
import dsi.mitologia.modelo.Personaje;
import dsi.mitologia.modelo.enums.OrigenCapacidad;

/**
 * Un personaje dispone de una capacidad gracias a una fuente concreta.
 * Propiedad OWL: tiene_capacidad (dominio Personaje, rango Capacidad), ampliada con el origen.
 *
 * <p>Hay un hecho por cada fuente: si Perseo tiene casco y anillo, hay dos hechos de Invisibilidad.
 * Al perder el casco solo desaparece el suyo y la capacidad sigue disponible por el anillo.
 * Las reglas preguntan por la capacidad sin mirar la fuente:
 * {@code CapacidadDisponible(personaje == $p, capacidad == $vuelo)}.</p>
 *
 * <p>Uso recomendado en Drools: {@code insertLogical(CapacidadDisponible.deObjeto($p, $c, $o))}
 * en una regla que exija {@code Posesion(poseedor == $p, objeto == $o)}; cuando la posesion se
 * retira o cambia, Drools retira solo la capacidad (mantenimiento de la verdad).</p>
 *
 * <p>Inmutable; igualdad por valor (personaje, capacidad, origen, fuente).</p>
 */
public final class CapacidadDisponible {

    private final Personaje personaje;
    private final Capacidad capacidad;
    private final OrigenCapacidad origen;
    private final EntidadMitologica fuente;

    private CapacidadDisponible(Personaje personaje, Capacidad capacidad,
                                OrigenCapacidad origen, EntidadMitologica fuente) {
        if (personaje == null || capacidad == null || origen == null) {
            throw new IllegalArgumentException("CapacidadDisponible necesita personaje, capacidad y origen");
        }
        if ((origen == OrigenCapacidad.PROPIA) != (fuente == null)) {
            throw new IllegalArgumentException("Solo las capacidades propias carecen de fuente");
        }
        this.personaje = personaje;
        this.capacidad = capacidad;
        this.origen = origen;
        this.fuente = fuente;
    }

    /** Capacidad propia del personaje, sin fuente externa. */
    public static CapacidadDisponible propia(Personaje personaje, Capacidad capacidad) {
        return new CapacidadDisponible(personaje, capacidad, OrigenCapacidad.PROPIA, null);
    }

    /** Capacidad dada por un objeto poseido. */
    public static CapacidadDisponible deObjeto(Personaje personaje, Capacidad capacidad, Objeto objeto) {
        return new CapacidadDisponible(personaje, capacidad, OrigenCapacidad.OBJETO, objeto);
    }

    /** Capacidad dada por un conjuro poseido (el conjuro se modela como Objeto). */
    public static CapacidadDisponible deConjuro(Personaje personaje, Capacidad capacidad, Objeto conjuro) {
        return new CapacidadDisponible(personaje, capacidad, OrigenCapacidad.CONJURO, conjuro);
    }

    /** Capacidad dada por una criatura capturada que hace de montura (Pegaso). */
    public static CapacidadDisponible deMontura(Personaje personaje, Capacidad capacidad, Personaje montura) {
        return new CapacidadDisponible(personaje, capacidad, OrigenCapacidad.MONTURA, montura);
    }

    public Personaje getPersonaje() {
        return personaje;
    }

    public Capacidad getCapacidad() {
        return capacidad;
    }

    public OrigenCapacidad getOrigen() {
        return origen;
    }

    /** Objeto, conjuro o montura que da la capacidad; {@code null} si es propia. */
    public EntidadMitologica getFuente() {
        return fuente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CapacidadDisponible)) return false;
        CapacidadDisponible otro = (CapacidadDisponible) o;
        return personaje.equals(otro.personaje) && capacidad.equals(otro.capacidad)
                && origen == otro.origen && Objects.equals(fuente, otro.fuente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personaje, capacidad, origen, fuente);
    }

    @Override
    public String toString() {
        return "CapacidadDisponible(" + personaje.getId() + " tiene " + capacidad.getId()
                + (fuente == null ? " propia" : " por " + fuente.getId()) + ")";
    }
}
