package dsi.mitologia.modelo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.EnumSet;

import org.junit.Before;
import org.junit.Test;

import dsi.mitologia.escenario.DominioMitologico;
import dsi.mitologia.escenario.EscenarioMitologico;
import dsi.mitologia.escenario.EstadoAventura;
import dsi.mitologia.escenario.Objetivo;
import dsi.mitologia.escenario.ResultadoMision;
import dsi.mitologia.escenario.TipoObjetivo;
import dsi.mitologia.eventos.EventoMitologico;
import dsi.mitologia.eventos.RegistroEventos;
import dsi.mitologia.eventos.TipoAccion;
import dsi.mitologia.hechos.CapacidadDisponible;
import dsi.mitologia.hechos.Cautiverio;
import dsi.mitologia.hechos.Posesion;
import dsi.mitologia.modelo.enums.EstadoLibertad;
import dsi.mitologia.modelo.enums.EstadoVital;
import dsi.mitologia.modelo.enums.TipoPersonaje;

/** Pruebas 1-11 del modelo Java (sin Drools). */
public class ModeloMitologicoTest {

    private DominioMitologico dominio;
    private EstadoAventura estado;
    private Personaje perseo;
    private Personaje hades;
    private Objeto casco;
    private Capacidad invisibilidad;

    @Before
    public void preparar() {
        dominio = DominioDePrueba.crear();
        estado = EstadoAventura.inicial(dominio);
        perseo = estado.getPersonaje("Perseo");
        hades = estado.getPersonaje("Hades");
        casco = estado.getObjeto("CascoHades");
        invisibilidad = estado.getCapacidad("Invisibilidad");
    }

    // Prueba 1
    @Test
    public void perseoEsHeroeYSemidios() {
        Personaje p = new Personaje("Perseo", "Perseo", EnumSet.of(TipoPersonaje.HEROE, TipoPersonaje.SEMIDIOS));
        assertTrue(p.isHeroe());
        assertTrue(p.isSemidios());
        assertFalse(p.isDios());
        assertEquals(EstadoVital.VIVO, p.getEstadoVital());
        assertEquals(EstadoLibertad.LIBRE, p.getEstadoLibertad());
    }

    // Prueba 2
    @Test
    public void medusaPasaDeVivaAMuerta() {
        Personaje medusa = estado.getPersonaje("Medusa");
        assertEquals(EstadoVital.VIVO, medusa.getEstadoVital());
        estado.matar(perseo, medusa);
        assertEquals(EstadoVital.MUERTO, medusa.getEstadoVital());
        assertTrue(estado.haMatado("Perseo", "Medusa"));
        try {
            estado.matar(perseo, medusa);
            fail("No se puede matar dos veces");
        } catch (IllegalStateException esperado) {
            // correcto
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void elEstadoVitalNoPuedeQuedarVacio() {
        perseo.setEstadoVital(null);
    }

    // Prueba 3
    @Test
    public void transferirElCascoDejaUnSoloPropietario() {
        assertSame(hades, estado.poseedorDe(casco));
        Posesion antes = estado.getPosesiones().get(0);
        estado.transferirObjeto(hades, perseo, casco);
        assertSame(perseo, estado.poseedorDe(casco));
        assertTrue(estado.posee(perseo, casco));
        assertFalse(estado.posee(hades, casco));
        int posesionesDelCasco = 0;
        for (Posesion p : estado.getPosesiones()) {
            if (p.getObjeto().equals(casco)) posesionesDelCasco++;
        }
        assertEquals(1, posesionesDelCasco);
        assertSame("Se modifica el mismo hecho", antes, estado.getPosesiones().get(0));
    }

    @Test(expected = IllegalStateException.class)
    public void noSeTransfiereLoQueNoSePosee() {
        estado.transferirObjeto(perseo, hades, casco);
    }

    @Test(expected = IllegalStateException.class)
    public void noSeDuplicaUnaPosesion() {
        estado.otorgarObjeto(perseo, casco);
    }

    // Prueba 4
    @Test
    public void perseoObtieneInvisibilidadCuandoTieneElCasco() {
        assertTrue(estado.proporciona(casco, invisibilidad));
        try {
            estado.concederCapacidad(CapacidadDisponible.deObjeto(perseo, invisibilidad, casco));
            fail("Sin el casco no puede obtener la capacidad del casco");
        } catch (IllegalStateException esperado) {
            // correcto
        }
        estado.transferirObjeto(hades, perseo, casco);
        assertFalse("Java no infiere la capacidad: la concede una regla", estado.tieneCapacidad(perseo, invisibilidad));
        assertTrue(estado.concederCapacidad(CapacidadDisponible.deObjeto(perseo, invisibilidad, casco)));
        assertTrue(estado.tieneCapacidad(perseo, invisibilidad));
        assertFalse("No se duplica", estado.concederCapacidad(CapacidadDisponible.deObjeto(perseo, invisibilidad, casco)));
    }

    // Prueba 5
    @Test
    public void alPerderElCascoPierdeLaInvisibilidad() {
        estado.transferirObjeto(hades, perseo, casco);
        estado.concederCapacidad(CapacidadDisponible.deObjeto(perseo, invisibilidad, casco));
        estado.transferirObjeto(perseo, hades, casco);
        assertFalse(estado.tieneCapacidad(perseo, invisibilidad));
    }

    // Prueba 6
    @Test
    public void conCascoYAnilloSigueInvisibleAlPerderElCasco() {
        Objeto anillo = estado.getObjeto("AnilloInvisibilidad");
        estado.otorgarObjeto(perseo, anillo);
        estado.transferirObjeto(hades, perseo, casco);
        estado.concederCapacidad(CapacidadDisponible.deObjeto(perseo, invisibilidad, casco));
        estado.concederCapacidad(CapacidadDisponible.deObjeto(perseo, invisibilidad, anillo));
        assertEquals(2, estado.fuentesDe(perseo, invisibilidad).size());

        estado.retirarObjeto(perseo, casco);
        assertTrue(estado.tieneCapacidad(perseo, invisibilidad));
        assertEquals(anillo, estado.fuentesDe(perseo, invisibilidad).get(0).getFuente());
    }

    @Test
    public void elVueloPuedeVenirDeUnConjuroODeUnaMontura() {
        Capacidad vuelo = estado.getCapacidad("Vuelo");
        Objeto conjuro = estado.getObjeto("ConjuroVuelo");
        Personaje pegaso = estado.getPersonaje("Pegaso");
        estado.otorgarObjeto(perseo, conjuro);
        estado.concederCapacidad(CapacidadDisponible.deConjuro(perseo, vuelo, conjuro));
        estado.apresar(perseo, pegaso);
        estado.concederCapacidad(CapacidadDisponible.deMontura(perseo, vuelo, pegaso));
        estado.liberar(perseo, pegaso);
        assertEquals("Liberar la montura retira solo esa fuente", 1, estado.fuentesDe(perseo, vuelo).size());
        assertTrue(estado.tieneCapacidad(perseo, vuelo));
    }

    // Prueba 7
    @Test
    public void cetoApresaAAndromeda() {
        Personaje ceto = estado.getPersonaje("Ceto");
        Personaje andromeda = estado.getPersonaje("Andromeda");
        estado.apresar(ceto, andromeda);
        assertEquals(EstadoLibertad.PRESO, andromeda.getEstadoLibertad());
        assertSame(ceto, estado.captorDe(andromeda));
        assertTrue(estado.getCautiverios().contains(new Cautiverio(ceto, andromeda)));
    }

    // Prueba 8
    @Test
    public void liberarAAndromedaQuitaElCautiverio() {
        Personaje ceto = estado.getPersonaje("Ceto");
        Personaje andromeda = estado.getPersonaje("Andromeda");
        estado.apresar(ceto, andromeda);
        estado.matar(perseo, ceto);
        estado.liberar(perseo, andromeda);
        assertEquals(EstadoLibertad.LIBRE, andromeda.getEstadoLibertad());
        assertNull(estado.captorDe(andromeda));
        assertTrue(estado.getCautiverios().isEmpty());
        assertTrue(estado.haLiberado("Perseo", "Andromeda"));
        try {
            estado.liberar(perseo, andromeda);
            fail("No se libera a quien ya esta libre");
        } catch (IllegalStateException esperado) {
            // correcto
        }
    }

    // Prueba 9
    @Test
    public void elEventoGuardaActorAccionYJustificacion() {
        RegistroEventos registro = new RegistroEventos();
        EventoMitologico favor = registro.registrar(EventoMitologico.nuevo(TipoAccion.OTORGAR_FAVOR, hades)
                .destinatario(perseo).justificacion("condicion inicial"));
        EventoMitologico entrega = registro.registrar(EventoMitologico.nuevo(TipoAccion.OBTENER_OBJETO, perseo)
                .destinatario(hades).objeto(casco)
                .justificacion("tener el favor de Hades").regla("R-prueba").causas(favor));
        assertEquals(2, entrega.getOrden());
        assertSame(perseo, entrega.getActor());
        assertEquals(TipoAccion.OBTENER_OBJETO, entrega.getAccion());
        assertEquals("tener el favor de Hades", entrega.getJustificacion());
        assertEquals("R-prueba", entrega.getRegla());
        assertEquals(casco, entrega.getObjeto());
        assertEquals(1, (int) entrega.getCausas().get(0));
        assertEquals(2, registro.size());
    }

    // Prueba 10
    @Test
    public void dosEscenariosNoSeContaminan() {
        Objetivo liberar = new Objetivo(TipoObjetivo.LIBERAR, "Perseo", "Andromeda");
        EscenarioMitologico uno = new EscenarioMitologico("F2-1", "?", liberar, dominio);
        EscenarioMitologico dos = new EscenarioMitologico("F2-2", "?", liberar, dominio);
        EstadoAventura e1 = uno.getEstado();
        e1.transferirObjeto(e1.getPersonaje("Hades"), e1.getPersonaje("Perseo"), e1.getObjeto("CascoHades"));
        e1.matar(e1.getPersonaje("Perseo"), e1.getPersonaje("Medusa"));
        uno.getRegistro().registrar(EventoMitologico.nuevo(TipoAccion.MATAR, e1.getPersonaje("Perseo")));

        EstadoAventura e2 = dos.getEstado();
        assertTrue(e2.posee("Hades", "CascoHades"));
        assertEquals(EstadoVital.VIVO, e2.getPersonaje("Medusa").getEstadoVital());
        assertEquals(0, dos.getRegistro().size());
        assertEquals("El dominio tampoco cambia", EstadoVital.VIVO, dominio.getPersonaje("Medusa").getEstadoVital());

        EstadoAventura copia = e1.copia();
        copia.morir(copia.getPersonaje("Perseo"));
        assertEquals(EstadoVital.VIVO, e1.getPersonaje("Perseo").getEstadoVital());
    }

    // Prueba 11
    @Test
    public void unPersonajePuedeTenerVariasCategorias() {
        assertTrue(perseo.isHeroe() && perseo.isSemidios());
        assertEquals(EnumSet.of(TipoPersonaje.HEROE, TipoPersonaje.SEMIDIOS), perseo.getTipos());
        assertFalse(estado.getPersonaje("Hades").isHeroe());
    }

    @Test
    public void elObjetivoSeEvaluaSobreLosHechos() {
        EscenarioMitologico e = new EscenarioMitologico("F1-1", "Puede Perseo liberar a Andromeda",
                new Objetivo(TipoObjetivo.LIBERAR, "Perseo", "Andromeda"), dominio);
        assertEquals(ResultadoMision.PENDIENTE, e.getResultado());
        assertEquals(ResultadoMision.NO_CONSEGUIDO, e.evaluarResultado());
        EstadoAventura s = e.getEstado();
        s.apresar(s.getPersonaje("Ceto"), s.getPersonaje("Andromeda"));
        s.liberar(s.getPersonaje("Perseo"), s.getPersonaje("Andromeda"));
        assertEquals(ResultadoMision.CONSEGUIDO, e.evaluarResultado());
    }

    @Test(expected = IllegalStateException.class)
    public void elFavorSoloPuedeVenirDeUnDios() {
        estado.agregarFavor(perseo, estado.getPersonaje("Medusa"));
    }

    @Test(expected = IllegalStateException.class)
    public void unMuertoNoPuedeActuar() {
        estado.morir(perseo);
        estado.apresar(perseo, estado.getPersonaje("Andromeda"));
    }

    @Test
    public void transformarElEscudoDeBronceEnEscudoEspejo() {
        Personaje atenea = estado.getPersonaje("Atenea");
        Objeto bronce = estado.getObjeto("EscudoBronce");
        Objeto espejo = estado.getObjeto("EscudoEspejo");
        estado.transferirObjeto(atenea, perseo, bronce);
        estado.transformarObjeto(perseo, bronce, espejo);
        assertTrue(estado.posee(perseo, espejo));
        assertNull(estado.poseedorDe(bronce));
    }
}
