package dsi.mitologia.modelo;

/**
 * Clase OWL {@code Capacidad}. Sus individuos actuales son Vuelo, Invisibilidad, Reflejo y Petrificacion.
 *
 * <p>Es una clase con instancias (y no un enum) para poder incorporar capacidades nuevas en la fase 2
 * (p. ej. la fuerza de Heracles) sin tocar el codigo. Inmutable y compartida entre escenarios.
 * Independiente de los objetos: la misma capacidad puede venir de fuentes distintas.</p>
 */
public final class Capacidad implements EntidadMitologica {

    private final String id;
    private final String nombre;

    public Capacidad(String id, String nombre) {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("La capacidad necesita id");
        this.id = id;
        this.nombre = nombre == null ? id : nombre;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Capacidad && id.equals(((Capacidad) o).id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return nombre;
    }
}
