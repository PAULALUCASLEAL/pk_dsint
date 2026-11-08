package dsi.mitologia.modelo.enums;

/**
 * Subclases de {@code Personaje} declaradas en mitologia.owl.
 *
 * No son disjuntas en la ontologia: un personaje puede tener varios tipos a la vez
 * (Perseo es Heroe y Semidios). Por eso {@code Personaje} guarda un conjunto de tipos
 * en lugar de usar una jerarquia de clases Java.
 */
public enum TipoPersonaje {
    DIOS("Dios"),
    SEMIDIOS("Semidios"),
    HEROE("Heroe"),
    MORTAL("Mortal"),
    CRIATURA("Criatura");

    private final String claseOwl;

    TipoPersonaje(String claseOwl) {
        this.claseOwl = claseOwl;
    }

    /** Nombre local de la clase OWL equivalente. */
    public String getClaseOwl() {
        return claseOwl;
    }

    /** Devuelve el tipo cuya clase OWL se llama asi, o {@code null} si no es un tipo de personaje. */
    public static TipoPersonaje desdeClaseOwl(String nombre) {
        for (TipoPersonaje t : values()) {
            if (t.claseOwl.equals(nombre)) return t;
        }
        return null;
    }
}
