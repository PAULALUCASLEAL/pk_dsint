package dsi.mitologia.motor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

import dsi.mitologia.escenario.EscenarioMitologico;
import dsi.mitologia.escenario.EstadoAventura;
import dsi.mitologia.escenario.Objetivo;
import dsi.mitologia.escenario.ResultadoMision;
import dsi.mitologia.escenario.TipoObjetivo;
import dsi.mitologia.eventos.EventoMitologico;
import dsi.mitologia.hechos.Posesion;
import dsi.mitologia.modelo.DominioDePrueba;

/** Prueba 12 y comprobacion del mantenimiento de la verdad con hechos del modelo. Solo tecnicas. */
public class IntegracionDroolsModeloTest {

    private KieSession sesion;
    private EscenarioMitologico escenario;

    @Before
    public void preparar() {
        KieBase base = new KieHelper()
                .addResource(ResourceFactory.newClassPathResource("dsi/mitologia/pruebas/prueba-tecnica-modelo.drl"),
                        ResourceType.DRL)
                .build();
        sesion = base.newKieSession();
        escenario = new EscenarioMitologico("tecnico", "Puede Perseo obtener Invisibilidad",
                new Objetivo(TipoObjetivo.OBTENER_CAPACIDAD, "Perseo", "Invisibilidad"), DominioDePrueba.crear());
        EstadoAventura e = escenario.getEstado();
        e.transferirObjeto(e.getPersonaje("Hades"), e.getPersonaje("Perseo"), e.getObjeto("CascoHades"));
    }

    @After
    public void cerrar() {
        sesion.dispose();
    }

    // Prueba 12
    @Test
    public void droolsReconoceLosHechosDelModelo() {
        MotorReglas.ejecutar(sesion, escenario);
        assertEquals(1, escenario.getRegistro().size());
        EventoMitologico ev = escenario.getRegistro().getEvento(1);
        assertEquals("Perseo", ev.getActor().getId());
        assertEquals("CascoHades", ev.getObjeto().getId());
        assertEquals("Prueba tecnica: heroe vivo que posee un objeto", ev.getRegla());
    }

    @Test
    public void laCapacidadLogicaDesapareceAlPerderElObjeto() {
        MotorReglas.ejecutar(sesion, escenario);
        assertEquals(ResultadoMision.CONSEGUIDO, escenario.getResultado());
        assertTrue(escenario.getEstado().tieneCapacidad("Perseo", "Invisibilidad"));

        // Se retira la posesion del casco desde fuera (como haria un delete en una regla)
        for (Object h : sesion.getObjects()) {
            if (h instanceof Posesion && ((Posesion) h).getObjeto().getId().equals("CascoHades")) {
                FactHandle fh = sesion.getFactHandle(h);
                sesion.delete(fh);
            }
        }
        sesion.fireAllRules();
        escenario.getEstado().sincronizarDesde(sesion.getObjects());
        assertFalse(escenario.getEstado().tieneCapacidad("Perseo", "Invisibilidad"));
        assertEquals(ResultadoMision.NO_CONSEGUIDO, escenario.evaluarResultado());
    }
}
