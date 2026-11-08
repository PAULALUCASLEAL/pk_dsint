package dsi.mitologia;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

/** Prueba de integracion: Drools carga kmodule.xml y ejecuta la regla de prueba. */
public class PruebaIntegracionDroolsTest {

    @Test
    public void laReglaDePruebaSeEjecuta() {
        List<String> registro = Principal.ejecutarPruebaIntegracion();
        assertEquals(Collections.singletonList("pong"), registro);
    }
}
