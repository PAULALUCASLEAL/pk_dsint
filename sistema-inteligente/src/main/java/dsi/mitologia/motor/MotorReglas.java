package dsi.mitologia.motor;

import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Global;
import org.kie.api.runtime.KieSession;

import dsi.mitologia.escenario.EscenarioMitologico;

/**
 * Unico punto del modelo que conoce la API de Drools: vuelca un escenario en una KieSession,
 * dispara las reglas y recoge el estado final.
 *
 * <p>Una sesion por escenario. El llamador crea la sesion y la cierra ({@code dispose()}).</p>
 */
public final class MotorReglas {

    /** Nombre del global DRL donde las reglas registran los eventos. */
    public static final String GLOBAL_REGISTRO = "registro";

    private MotorReglas() {
    }

    /** Inserta los hechos del escenario, dispara las reglas y sincroniza el estado. Devuelve las reglas disparadas. */
    public static int ejecutar(KieSession sesion, EscenarioMitologico escenario) {
        if (declaraGlobal(sesion, GLOBAL_REGISTRO)) {
            sesion.setGlobal(GLOBAL_REGISTRO, escenario.getRegistro());
        }
        for (Object hecho : escenario.getHechosParaDrools()) {
            sesion.insert(hecho);
        }
        int disparadas = sesion.fireAllRules();
        escenario.getEstado().sincronizarDesde(sesion.getObjects());
        escenario.evaluarResultado();
        return disparadas;
    }

    private static boolean declaraGlobal(KieSession sesion, String nombre) {
        for (KiePackage p : sesion.getKieBase().getKiePackages()) {
            for (Global g : p.getGlobalVariables()) {
                if (nombre.equals(g.getName())) return true;
            }
        }
        return false;
    }
}
