package dsi.mitologia.modelo.enums;

/** Valores de la propiedad de datos {@code estado_libertad} (rango oneOf {"libre", "preso"}, cardinalidad 1). */
public enum EstadoLibertad {
    LIBRE("libre"),
    PRESO("preso");

    private final String valorOwl;

    EstadoLibertad(String valorOwl) {
        this.valorOwl = valorOwl;
    }

    public String getValorOwl() {
        return valorOwl;
    }

    public static EstadoLibertad desdeOwl(String valor) {
        for (EstadoLibertad e : values()) {
            if (e.valorOwl.equals(valor)) return e;
        }
        throw new IllegalArgumentException("estado_libertad desconocido: " + valor);
    }
}
