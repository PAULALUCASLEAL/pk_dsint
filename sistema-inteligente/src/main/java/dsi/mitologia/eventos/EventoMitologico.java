package dsi.mitologia.eventos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dsi.mitologia.modelo.Capacidad;
import dsi.mitologia.modelo.EntidadMitologica;
import dsi.mitologia.modelo.Objeto;
import dsi.mitologia.modelo.Personaje;

/**
 * Un acontecimiento de la historia, con su explicacion ("... debido a ...").
 *
 * <p>Inmutable. Lo crea {@link RegistroEventos#registrar(Constructor)}, que le asigna el orden.
 * Uso desde una regla:</p>
 * <pre>
 * registro.registrar(EventoMitologico.nuevo(TipoAccion.OBTENER_OBJETO, $h)
 *     .objeto($o).justificacion("tener el favor de Atenea")
 *     .regla(drools.getRule().getName()));
 * </pre>
 * <p>{@code causas} guarda el orden de los eventos que lo provocaron (relacion causal).</p>
 */
public final class EventoMitologico {

    private final int orden;
    private final TipoAccion accion;
    private final Personaje actor;
    private final EntidadMitologica destinatario;
    private final Objeto objeto;
    private final Capacidad capacidad;
    private final String descripcion;
    private final String justificacion;
    private final String regla;
    private final List<Integer> causas;

    private EventoMitologico(int orden, Constructor c) {
        this.orden = orden;
        this.accion = c.accion;
        this.actor = c.actor;
        this.destinatario = c.destinatario;
        this.objeto = c.objeto;
        this.capacidad = c.capacidad;
        this.descripcion = c.descripcion;
        this.justificacion = c.justificacion;
        this.regla = c.regla;
        this.causas = Collections.unmodifiableList(new ArrayList<Integer>(c.causas));
    }

    /** Empieza a construir un evento. El actor puede ser {@code null} (p. ej. "Ceto queda libre"). */
    public static Constructor nuevo(TipoAccion accion, Personaje actor) {
        return new Constructor(accion, actor);
    }

    public int getOrden() {
        return orden;
    }

    /** Identificador estable dentro del escenario: "E1", "E2"... */
    public String getId() {
        return "E" + orden;
    }

    public TipoAccion getAccion() {
        return accion;
    }

    public Personaje getActor() {
        return actor;
    }

    public EntidadMitologica getDestinatario() {
        return destinatario;
    }

    public Objeto getObjeto() {
        return objeto;
    }

    public Capacidad getCapacidad() {
        return capacidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getJustificacion() {
        return justificacion;
    }

    public String getRegla() {
        return regla;
    }

    public List<Integer> getCausas() {
        return causas;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getId()).append(' ').append(accion);
        if (actor != null) sb.append(" actor=").append(actor.getId());
        if (destinatario != null) sb.append(" destinatario=").append(destinatario.getId());
        if (objeto != null) sb.append(" objeto=").append(objeto.getId());
        if (capacidad != null) sb.append(" capacidad=").append(capacidad.getId());
        if (justificacion != null) sb.append(" debido a ").append(justificacion);
        if (regla != null) sb.append(" [").append(regla).append(']');
        return sb.toString();
    }

    /** Constructor por pasos (patron builder) para no tener un constructor con diez parametros. */
    public static final class Constructor {
        private final TipoAccion accion;
        private final Personaje actor;
        private EntidadMitologica destinatario;
        private Objeto objeto;
        private Capacidad capacidad;
        private String descripcion;
        private String justificacion;
        private String regla;
        private final List<Integer> causas = new ArrayList<Integer>();

        private Constructor(TipoAccion accion, Personaje actor) {
            if (accion == null) throw new IllegalArgumentException("El evento necesita una accion");
            this.accion = accion;
            this.actor = actor;
        }

        public Constructor destinatario(EntidadMitologica destinatario) {
            this.destinatario = destinatario;
            return this;
        }

        public Constructor objeto(Objeto objeto) {
            this.objeto = objeto;
            return this;
        }

        public Constructor capacidad(Capacidad capacidad) {
            this.capacidad = capacidad;
            return this;
        }

        public Constructor descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public Constructor justificacion(String justificacion) {
            this.justificacion = justificacion;
            return this;
        }

        public Constructor regla(String regla) {
            this.regla = regla;
            return this;
        }

        /** Eventos anteriores que causan este. */
        public Constructor causas(EventoMitologico... previos) {
            for (EventoMitologico e : previos) {
                if (e != null) causas.add(e.getOrden());
            }
            return this;
        }

        EventoMitologico construir(int orden) {
            return new EventoMitologico(orden, this);
        }
    }
}
