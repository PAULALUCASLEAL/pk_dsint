package dsi.mitologia.hechos;

import java.util.Objects;

import dsi.mitologia.modelo.Capacidad;
import dsi.mitologia.modelo.Objeto;

/**
 * Conocimiento estatico: un objeto proporciona una capacidad a quien lo posee.
 * Propiedad OWL: proporciona_capacidad (dominio Objeto, rango Capacidad).
 *
 * <p>Ejemplos del OWL: CascoHades -> Invisibilidad, SandaliasAladas -> Vuelo,
 * EscudoEspejo -> Reflejo, CabezaMedusa -> Petrificacion. Varias fuentes pueden dar la misma
 * capacidad (AnilloInvisibilidad -> Invisibilidad) sin crear clases nuevas.</p>
 */
public final class ProporcionaCapacidad {

    private final Objeto objeto;
    private final Capacidad capacidad;

    public ProporcionaCapacidad(Objeto objeto, Capacidad capacidad) {
        if (objeto == null || capacidad == null) {
            throw new IllegalArgumentException("ProporcionaCapacidad necesita objeto y capacidad");
        }
        this.objeto = objeto;
        this.capacidad = capacidad;
    }

    public Objeto getObjeto() {
        return objeto;
    }

    public Capacidad getCapacidad() {
        return capacidad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProporcionaCapacidad)) return false;
        ProporcionaCapacidad otro = (ProporcionaCapacidad) o;
        return objeto.equals(otro.objeto) && capacidad.equals(otro.capacidad);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objeto, capacidad);
    }

    @Override
    public String toString() {
        return "ProporcionaCapacidad(" + objeto.getId() + " -> " + capacidad.getId() + ")";
    }
}
