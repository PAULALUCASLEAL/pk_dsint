package dsi.mitologia.hechos;

import java.util.Objects;

import dsi.mitologia.modelo.Personaje;

/**
 * Relacion estatica de parentesco: "Perseo es_hijo_de Zeus". No cambia durante la aventura.
 *
 * <p>Propiedad OWL: es_hijo_de / tiene_hijo. Si la propiedad tiene inversa, un unico hecho representa las dos direcciones.
 * Inmutable: si deja de ser cierto se retira ({@code delete}) y no se modifica.
 * Igualdad por valor (dos hechos con los mismos extremos son el mismo hecho).</p>
 */
public final class Filiacion {

    private final Personaje hijo;
    private final Personaje progenitor;

    public Filiacion(Personaje hijo, Personaje progenitor) {
        if (hijo == null || progenitor == null) throw new IllegalArgumentException("Filiacion necesita hijo y progenitor");
        this.hijo = hijo;
        this.progenitor = progenitor;
    }

    public Personaje getHijo() {
        return hijo;
    }

    public Personaje getProgenitor() {
        return progenitor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Filiacion)) return false;
        Filiacion otro = (Filiacion) o;
        return hijo.equals(otro.hijo) && progenitor.equals(otro.progenitor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hijo, progenitor);
    }

    @Override
    public String toString() {
        return "Filiacion(" + hijo.getId() + " -> " + progenitor.getId() + ")";
    }
}
