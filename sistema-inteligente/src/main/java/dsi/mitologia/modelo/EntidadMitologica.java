package dsi.mitologia.modelo;

/**
 * Cualquier individuo de la ontologia con identificador y nombre: personajes, objetos y capacidades.
 *
 * Existe para los hechos que admiten mas de un tipo de destino, como {@code localiza_a}
 * (rango Personaje U Objeto) o la fuente de una capacidad (objeto o montura).
 */
public interface EntidadMitologica {

    /** Identificador estable: el nombre local del IRI en mitologia.owl (p. ej. "CascoHades"). */
    String getId();

    String getNombre();
}
