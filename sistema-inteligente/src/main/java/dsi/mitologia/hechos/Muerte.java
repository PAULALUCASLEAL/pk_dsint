package dsi.mitologia.hechos;

import java.util.Objects;

import dsi.mitologia.modelo.Personaje;

/**
 * Registro de que un personaje ha matado a otro. Se conserva tras la muerte (es historia).
 *
 * <p>Propiedad OWL: mata_a / es_asesinado_por. Si la propiedad tiene inversa, un unico hecho representa las dos direcciones.
 * Inmutable: si deja de ser cierto se retira ({@code delete}) y no se modifica.
 * Igualdad por valor (dos hechos con los mismos extremos son el mismo hecho).</p>
 */
public final class Muerte {

    private final Personaje asesino;
    private final Personaje victima;

    public Muerte(Personaje asesino, Personaje victima) {
        if (asesino == null || victima == null) throw new IllegalArgumentException("Muerte necesita asesino y victima");
        this.asesino = asesino;
        this.victima = victima;
    }

    public Personaje getAsesino() {
        return asesino;
    }

    public Personaje getVictima() {
        return victima;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Muerte)) return false;
        Muerte otro = (Muerte) o;
        return asesino.equals(otro.asesino) && victima.equals(otro.victima);
    }

    @Override
    public int hashCode() {
        return Objects.hash(asesino, victima);
    }

    @Override
    public String toString() {
        return "Muerte(" + asesino.getId() + " -> " + victima.getId() + ")";
    }
}
