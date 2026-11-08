package dsi.mitologia.modelo.enums;

/**
 * De donde procede una capacidad disponible.
 *
 * La ontologia solo tiene {@code tiene_capacidad} (personaje -> capacidad) y
 * {@code proporciona_capacidad} (objeto -> capacidad); el origen es una decision del
 * modelo Java para poder retirar la capacidad cuando desaparece su fuente.
 */
public enum OrigenCapacidad {
    /** Capacidad propia del personaje (p. ej. la fuerza de Heracles). Sin fuente. */
    PROPIA,
    /** La da un objeto que el personaje posee (CascoHades -> Invisibilidad). */
    OBJETO,
    /** La da un conjuro que el personaje posee (se representa como un Objeto). */
    CONJURO,
    /** La da una criatura que el personaje tiene capturada y monta (Pegaso -> Vuelo). */
    MONTURA
}
