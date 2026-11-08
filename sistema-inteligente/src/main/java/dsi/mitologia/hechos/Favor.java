package dsi.mitologia.hechos;

import java.util.Objects;

import dsi.mitologia.modelo.Personaje;

/**
 * Un personaje tiene el favor de un dios: "Perseo tiene el favor de Atenea".
 *
 * <p>Propiedad OWL: tiene_favor (dominio Personaje, rango Dios). Si la propiedad tiene inversa, un unico hecho representa las dos direcciones.
 * Inmutable: si deja de ser cierto se retira ({@code delete}) y no se modifica.
 * Igualdad por valor (dos hechos con los mismos extremos son el mismo hecho).</p>
 */
public final class Favor {

    private final Personaje personaje;
    private final Personaje dios;

    public Favor(Personaje personaje, Personaje dios) {
        if (personaje == null || dios == null) throw new IllegalArgumentException("Favor necesita personaje y dios");
        this.personaje = personaje;
        this.dios = dios;
    }

    public Personaje getPersonaje() {
        return personaje;
    }

    public Personaje getDios() {
        return dios;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Favor)) return false;
        Favor otro = (Favor) o;
        return personaje.equals(otro.personaje) && dios.equals(otro.dios);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personaje, dios);
    }

    @Override
    public String toString() {
        return "Favor(" + personaje.getId() + " -> " + dios.getId() + ")";
    }
}
