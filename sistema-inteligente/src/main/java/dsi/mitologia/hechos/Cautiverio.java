package dsi.mitologia.hechos;

import java.util.Objects;

import dsi.mitologia.modelo.Personaje;

/**
 * Relacion dinamica: el captor tiene preso al personaje. Se mantiene sincronizada con Personaje.estadoLibertad.
 *
 * <p>Propiedad OWL: apresa_a / esta_preso_por. Si la propiedad tiene inversa, un unico hecho representa las dos direcciones.
 * Inmutable: si deja de ser cierto se retira ({@code delete}) y no se modifica.
 * Igualdad por valor (dos hechos con los mismos extremos son el mismo hecho).</p>
 */
public final class Cautiverio {

    private final Personaje captor;
    private final Personaje preso;

    public Cautiverio(Personaje captor, Personaje preso) {
        if (captor == null || preso == null) throw new IllegalArgumentException("Cautiverio necesita captor y preso");
        this.captor = captor;
        this.preso = preso;
    }

    public Personaje getCaptor() {
        return captor;
    }

    public Personaje getPreso() {
        return preso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cautiverio)) return false;
        Cautiverio otro = (Cautiverio) o;
        return captor.equals(otro.captor) && preso.equals(otro.preso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(captor, preso);
    }

    @Override
    public String toString() {
        return "Cautiverio(" + captor.getId() + " -> " + preso.getId() + ")";
    }
}
