package dsi.mitologia.modelo.enums;

/**
 * Subclases de {@code Objeto} declaradas en mitologia.owl.
 * Tampoco son disjuntas en la ontologia, asi que un objeto puede tener varias.
 */
public enum CategoriaObjeto {
    ARMA("Arma"),
    PROTECCION("Proteccion"),
    ARTEFACTO_MAGICO("ArtefactoMagico"),
    PARTE_ANATOMICA("ParteAnatomica");

    private final String claseOwl;

    CategoriaObjeto(String claseOwl) {
        this.claseOwl = claseOwl;
    }

    public String getClaseOwl() {
        return claseOwl;
    }

    public static CategoriaObjeto desdeClaseOwl(String nombre) {
        for (CategoriaObjeto c : values()) {
            if (c.claseOwl.equals(nombre)) return c;
        }
        return null;
    }
}
