package dsi.mitologia.ontologia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import dsi.mitologia.escenario.DominioMitologico;
import dsi.mitologia.escenario.EstadoAventura;
import dsi.mitologia.modelo.Personaje;
import dsi.mitologia.modelo.enums.CategoriaObjeto;
import dsi.mitologia.modelo.enums.EstadoLibertad;

/** Comprueba que el modelo cargado coincide con lo que dice ../ontologia/mitologia.owl. */
public class CargadorOntologiaTest {

    private static DominioMitologico dominio;
    private static CargadorOntologia cargador;

    @BeforeClass
    public static void cargar() {
        cargador = new CargadorOntologia();
        dominio = cargador.cargar(new File("../ontologia/mitologia.owl"));
    }

    @Test
    public void cargaTodosLosIndividuos() {
        assertEquals(15, dominio.getPersonajes().size());
        assertEquals(11, dominio.getObjetos().size());
        assertEquals(4, dominio.getCapacidades().size());
        assertTrue("Sin avisos: " + cargador.getAvisos(), cargador.getAvisos().isEmpty());
    }

    @Test
    public void perseoTieneLosTiposDelOwl() {
        Personaje perseo = dominio.getPersonaje("Perseo");
        assertTrue(perseo.isHeroe());
        assertTrue(perseo.isSemidios());
        assertTrue(dominio.getPersonaje("Ninfas").isDios());
        assertTrue(dominio.getObjeto("HozAcero").esDeCategoria(CategoriaObjeto.ARMA));
    }

    @Test
    public void posesionesYCapacidadesDelOwl() {
        EstadoAventura e = EstadoAventura.inicial(dominio);
        assertTrue(e.posee("Hades", "CascoHades"));
        assertTrue(e.posee("Ninfas", "SandaliasAladas"));
        assertTrue(e.posee("Grayas", "OjoGrayas"));
        assertEquals(null, e.poseedorDe(e.getObjeto("EscudoEspejo")));
        assertTrue(e.proporciona(e.getObjeto("SandaliasAladas"), e.getCapacidad("Vuelo")));
        assertTrue(e.proporciona(e.getObjeto("CabezaMedusa"), e.getCapacidad("Petrificacion")));
        assertEquals(4, dominio.getProporciona().size());
    }

    @Test
    public void relacionesDinamicasYEstadosDelOwl() {
        EstadoAventura e = EstadoAventura.inicial(dominio);
        Personaje andromeda = e.getPersonaje("Andromeda");
        assertEquals(EstadoLibertad.PRESO, andromeda.getEstadoLibertad());
        assertEquals("Ceto", e.captorDe(andromeda).getId());
        assertTrue(e.tieneEnojo(e.getPersonaje("Casiopea"), e.getPersonaje("Poseidon")));
        assertEquals(3, e.getFiliaciones().size());
        assertFalse(e.getFavores().iterator().hasNext());
    }
}
