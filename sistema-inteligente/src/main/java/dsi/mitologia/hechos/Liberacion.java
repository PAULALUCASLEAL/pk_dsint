package dsi.mitologia.hechos;

import java.util.Objects;

import dsi.mitologia.modelo.Personaje;

/**
 * Registro de que un personaje ha liberado a otro de su cautiverio.
 *
 * <p>Propiedad OWL: libera_a / es_liberado_por. Si la propiedad tiene inversa, un unico hecho representa las dos direcciones.
 * Inmutable: si deja de ser cierto se retira ({@code delete}) y no se modifica.
 * Igualdad por valor (dos hechos con los mismos extremos son el mismo hecho).</p>
 */
public final class Liberacion {

    private final Personaje libertador;
    private final Personaje liberado;

    public Liberacion(Personaje libertador, Personaje liberado) {
        if (libertador == null || liberado == null) throw new IllegalArgumentException("Liberacion necesita libertador y liberado");
        this.libertador = libertador;
        this.liberado = liberado;
    }

    public Personaje getLibertador() {
        return libertador;
    }

    public Personaje getLiberado() {
        return liberado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Liberacion)) return false;
        Liberacion otro = (Liberacion) o;
        return libertador.equals(otro.libertador) && liberado.equals(otro.liberado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(libertador, liberado);
    }

    @Override
    public String toString() {
        return "Liberacion(" + libertador.getId() + " -> " + liberado.getId() + ")";
    }
}
