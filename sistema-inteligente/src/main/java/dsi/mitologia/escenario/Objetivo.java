package dsi.mitologia.escenario;

/**
 * Lo que pregunta el escenario: "Puede &lt;protagonista&gt; &lt;tipo&gt; &lt;destino&gt;?".
 *
 * <p>Ejemplos: (LIBERAR, Perseo, Andromeda), (OBTENER_OBJETO, Perseo, BridaDorada),
 * (OBTENER_CAPACIDAD, Perseo, Vuelo). Usa identificadores para que el futuro lector de
 * ficheros .txt lo pueda crear directamente. Inmutable; se puede insertar en Drools.</p>
 */
public final class Objetivo {

    private final TipoObjetivo tipo;
    private final String protagonistaId;
    private final String destinoId;

    public Objetivo(TipoObjetivo tipo, String protagonistaId, String destinoId) {
        if (tipo == null || protagonistaId == null || destinoId == null) {
            throw new IllegalArgumentException("El objetivo necesita tipo, protagonista y destino");
        }
        this.tipo = tipo;
        this.protagonistaId = protagonistaId;
        this.destinoId = destinoId;
    }

    public TipoObjetivo getTipo() {
        return tipo;
    }

    public String getProtagonistaId() {
        return protagonistaId;
    }

    public String getDestinoId() {
        return destinoId;
    }

    /**
     * Comprueba (sin inferir nada) si el estado final contiene el hecho que pide el objetivo.
     * Solo mira hechos; las reglas son las que los habran producido.
     */
    public boolean seCumpleEn(EstadoAventura estado) {
        switch (tipo) {
            case MATAR:
                return estado.haMatado(protagonistaId, destinoId);
            case APRESAR:
                return estado.tienePreso(protagonistaId, destinoId);
            case LIBERAR:
                return estado.haLiberado(protagonistaId, destinoId);
            case OBTENER_OBJETO:
                return estado.posee(protagonistaId, destinoId);
            case OBTENER_CAPACIDAD:
                return estado.tieneCapacidad(protagonistaId, destinoId);
            case LOCALIZAR:
                return estado.haLocalizado(protagonistaId, destinoId);
            default:
                throw new IllegalStateException("Tipo de objetivo sin comprobar: " + tipo);
        }
    }

    @Override
    public String toString() {
        return "Objetivo(" + protagonistaId + " " + tipo + " " + destinoId + ")";
    }
}
