package dsi.mitologia.eventos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lista ordenada de los eventos de UN escenario. Se pasa a Drools como global {@code registro}.
 *
 * <p>No es estatico: cada escenario tiene el suyo. Sirve despues para escribir el fichero
 * RespuestaFx_y.txt sin depender de {@code System.out}.</p>
 */
public class RegistroEventos {

    private final List<EventoMitologico> eventos = new ArrayList<EventoMitologico>();

    /** Asigna el siguiente numero de orden (1, 2, 3...) y guarda el evento. */
    public EventoMitologico registrar(EventoMitologico.Constructor constructor) {
        EventoMitologico evento = constructor.construir(eventos.size() + 1);
        eventos.add(evento);
        return evento;
    }

    public List<EventoMitologico> getEventos() {
        return Collections.unmodifiableList(eventos);
    }

    /** Evento por numero de orden (empieza en 1). */
    public EventoMitologico getEvento(int orden) {
        return eventos.get(orden - 1);
    }

    public int size() {
        return eventos.size();
    }
}
