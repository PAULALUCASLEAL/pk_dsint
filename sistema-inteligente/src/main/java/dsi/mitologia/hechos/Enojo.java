package dsi.mitologia.hechos;

import java.util.Objects;

import dsi.mitologia.modelo.Personaje;

/**
 * Un personaje tiene el enojo de un dios: "Casiopea tiene el enojo de Poseidon".
 *
 * <p>Propiedad OWL: tiene_enojo (dominio Personaje, rango Dios). Si la propiedad tiene inversa, un unico hecho representa las dos direcciones.
 * Inmutable: si deja de ser cierto se retira ({@code delete}) y no se modifica.
 * Igualdad por valor (dos hechos con los mismos extremos son el mismo hecho).</p>
 */
public final class Enojo {

    private final Personaje personaje;
    private final Personaje dios;

    public Enojo(Personaje personaje, Personaje dios) {
        if (personaje == null || dios == null) throw new IllegalArgumentException("Enojo necesita personaje y dios");
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
        if (!(o instanceof Enojo)) return false;
        Enojo otro = (Enojo) o;
        return personaje.equals(otro.personaje) && dios.equals(otro.dios);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personaje, dios);
    }

    @Override
    public String toString() {
        return "Enojo(" + personaje.getId() + " -> " + dios.getId() + ")";
    }
}
