package dsi.mitologia.hechos;

import java.util.Objects;

import dsi.mitologia.modelo.EntidadMitologica;
import dsi.mitologia.modelo.Objeto;
import dsi.mitologia.modelo.Personaje;

/**
 * Un personaje ha localizado (descubierto) a otro personaje o a un objeto.
 *
 * <p>Propiedad OWL: localiza_a (rango Personaje U Objeto). Si la propiedad tiene inversa, un unico hecho representa las dos direcciones.
 * Inmutable: si deja de ser cierto se retira ({@code delete}) y no se modifica.
 * Igualdad por valor (dos hechos con los mismos extremos son el mismo hecho).</p>
 */
public final class Localizacion {

    private final Personaje localizador;
    private final EntidadMitologica localizado;

    /** Localizar a un personaje (p. ej. Perseo localiza a las Grayas). */
    public Localizacion(Personaje localizador, Personaje localizado) {
        this(localizador, (EntidadMitologica) localizado);
    }

    /** Localizar un objeto. */
    public Localizacion(Personaje localizador, Objeto localizado) {
        this(localizador, (EntidadMitologica) localizado);
    }

    // Privado: el rango OWL es Personaje U Objeto, asi que no se admite una Capacidad.
    private Localizacion(Personaje localizador, EntidadMitologica localizado) {
        if (localizador == null || localizado == null) throw new IllegalArgumentException("Localizacion necesita localizador y localizado");
        this.localizador = localizador;
        this.localizado = localizado;
    }

    public Personaje getLocalizador() {
        return localizador;
    }

    public EntidadMitologica getLocalizado() {
        return localizado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Localizacion)) return false;
        Localizacion otro = (Localizacion) o;
        return localizador.equals(otro.localizador) && localizado.equals(otro.localizado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localizador, localizado);
    }

    @Override
    public String toString() {
        return "Localizacion(" + localizador.getId() + " -> " + localizado.getId() + ")";
    }
}
