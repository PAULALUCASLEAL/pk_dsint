package dsi.mitologia.escenario;

/** Resultado de un escenario tras ejecutar las reglas. */
public enum ResultadoMision {
    /** Todavia no se ha evaluado. */
    PENDIENTE,
    /** El objetivo se cumple en el estado final. */
    CONSEGUIDO,
    /** Las reglas terminaron y el objetivo no se cumple. */
    NO_CONSEGUIDO
}
