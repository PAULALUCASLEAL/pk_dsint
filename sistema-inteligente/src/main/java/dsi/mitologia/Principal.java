package dsi.mitologia;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * Punto de entrada del sistema inteligente DSI_Mitologia.
 *
 * ESTADO: esqueleto. Todavia no lee escenarios ni contiene reglas mitologicas;
 * solo lanza la regla de prueba de integracion para comprobar que Drools funciona.
 */
public class Principal {

    public static void main(String[] args) {
        // Directorios por defecto, tal como exige el enunciado (apartado 6)
        String escenarios = "escenarios";
        String soluciones = "soluciones";

        if (args.length >= 1) escenarios = args[0];
        if (args.length >= 2) soluciones = args[1];

        System.out.println("Directorio de escenarios: " + escenarios);
        System.out.println("Directorio de soluciones: " + soluciones);

        List<String> registro = ejecutarPruebaIntegracion();
        System.out.println("Resultado de la prueba: " + registro);
    }

    /** Crea una sesion Drools, inserta un hecho de prueba y devuelve lo que anotan las reglas. */
    static List<String> ejecutarPruebaIntegracion() {
        KieServices ks = KieServices.Factory.get();
        KieContainer contenedor = ks.getKieClasspathContainer();
        KieSession sesion = contenedor.newKieSession("mitologiaSesion");
        List<String> registro = new ArrayList<>();
        try {
            sesion.setGlobal("registro", registro);
            sesion.insert("ping");
            sesion.fireAllRules();
        } finally {
            sesion.dispose();
        }
        return registro;
    }
}
