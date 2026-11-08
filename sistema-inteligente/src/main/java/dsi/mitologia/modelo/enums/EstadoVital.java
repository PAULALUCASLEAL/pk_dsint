package dsi.mitologia.modelo.enums;

/** Valores de la propiedad de datos {@code estado_vital} (rango oneOf {"vivo", "muerto"}, cardinalidad 1). */
public enum EstadoVital {
    VIVO("vivo"),
    MUERTO("muerto");

    private final String valorOwl;

    EstadoVital(String valorOwl) {
        this.valorOwl = valorOwl;
    }

    public String getValorOwl() {
        return valorOwl;
    }

    /** Convierte el literal del OWL; lanza excepcion si no esta en el oneOf. */
    public static EstadoVital desdeOwl(String valor) {
        for (EstadoVital e : values()) {
            if (e.valorOwl.equals(valor)) return e;
        }
        throw new IllegalArgumentException("estado_vital desconocido: " + valor);
    }
}
