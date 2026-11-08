package dsi.mitologia.escenario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dsi.mitologia.eventos.RegistroEventos;

/**
 * Una ejecucion: pregunta + objetivo + condiciones iniciales + estado propio + eventos + resultado.
 *
 * <p>Cada escenario crea su propio {@link EstadoAventura} a partir del dominio y su propio
 * {@link RegistroEventos}, asi que ejecutar uno no altera a los demas.</p>
 *
 * <p>El futuro lector de EscenarioF2-x.txt creara el escenario, guardara cada linea en
 * {@link #agregarCondicion(String)} y aplicara cada condicion con las operaciones del estado
 * (p. ej. {@code getEstado().agregarFavor(...)} o {@code getEstado().retirarObjeto(...)}).</p>
 */
public class EscenarioMitologico {

    private final String id;
    private final String pregunta;
    private final Objetivo objetivo;
    private final EstadoAventura estado;
    private final RegistroEventos registro = new RegistroEventos();
    private final List<String> condiciones = new ArrayList<String>();
    private ResultadoMision resultado = ResultadoMision.PENDIENTE;

    public EscenarioMitologico(String id, String pregunta, Objetivo objetivo, DominioMitologico dominio) {
        if (id == null || objetivo == null) throw new IllegalArgumentException("El escenario necesita id y objetivo");
        this.id = id;
        this.pregunta = pregunta;
        this.objetivo = objetivo;
        this.estado = EstadoAventura.inicial(dominio);
    }

    public String getId() {
        return id;
    }

    public String getPregunta() {
        return pregunta;
    }

    public Objetivo getObjetivo() {
        return objetivo;
    }

    public EstadoAventura getEstado() {
        return estado;
    }

    public RegistroEventos getRegistro() {
        return registro;
    }

    /** Texto original de una condicion inicial (para mostrarla en la respuesta). */
    public void agregarCondicion(String condicion) {
        condiciones.add(condicion);
    }

    public List<String> getCondiciones() {
        return Collections.unmodifiableList(condiciones);
    }

    public ResultadoMision getResultado() {
        return resultado;
    }

    /** Hechos para la KieSession: los del estado mas el propio objetivo. */
    public List<Object> getHechosParaDrools() {
        List<Object> hechos = estado.getHechos();
        hechos.add(objetivo);
        return hechos;
    }

    /** Evalua el objetivo sobre el estado actual y guarda el resultado. */
    public ResultadoMision evaluarResultado() {
        resultado = objetivo.seCumpleEn(estado) ? ResultadoMision.CONSEGUIDO : ResultadoMision.NO_CONSEGUIDO;
        return resultado;
    }
}
