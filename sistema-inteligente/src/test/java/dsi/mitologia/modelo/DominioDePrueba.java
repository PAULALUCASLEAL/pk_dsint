package dsi.mitologia.modelo;

import java.util.EnumSet;

import dsi.mitologia.escenario.DominioMitologico;
import dsi.mitologia.modelo.enums.CategoriaObjeto;
import dsi.mitologia.modelo.enums.TipoPersonaje;

/**
 * Dominio pequeno construido a mano para las pruebas (no depende del OWL).
 * Incluye dos objetos que no estan en el OWL (AnilloInvisibilidad, ConjuroVuelo) para probar
 * fuentes alternativas de una misma capacidad.
 */
public final class DominioDePrueba {

    private DominioDePrueba() {
    }

    public static DominioMitologico crear() {
        DominioMitologico d = new DominioMitologico();
        d.registrarPersonaje(new Personaje("Perseo", "Perseo", EnumSet.of(TipoPersonaje.HEROE, TipoPersonaje.SEMIDIOS)));
        d.registrarPersonaje(new Personaje("Hades", "Hades", EnumSet.of(TipoPersonaje.DIOS)));
        d.registrarPersonaje(new Personaje("Atenea", "Atenea", EnumSet.of(TipoPersonaje.DIOS)));
        d.registrarPersonaje(new Personaje("Medusa", "Medusa", EnumSet.of(TipoPersonaje.CRIATURA)));
        d.registrarPersonaje(new Personaje("Ceto", "Ceto", EnumSet.of(TipoPersonaje.CRIATURA)));
        d.registrarPersonaje(new Personaje("Andromeda", "Andromeda", EnumSet.of(TipoPersonaje.MORTAL)));
        d.registrarPersonaje(new Personaje("Pegaso", "Pegaso", EnumSet.of(TipoPersonaje.CRIATURA)));

        d.registrarObjeto(new Objeto("CascoHades", "Casco de Hades", EnumSet.of(CategoriaObjeto.PROTECCION)));
        d.registrarObjeto(new Objeto("EscudoBronce", "Escudo de Bronce", EnumSet.of(CategoriaObjeto.PROTECCION)));
        d.registrarObjeto(new Objeto("EscudoEspejo", "Escudo-Espejo", EnumSet.of(CategoriaObjeto.PROTECCION)));
        d.registrarObjeto(new Objeto("AnilloInvisibilidad", "Anillo de Invisibilidad", EnumSet.of(CategoriaObjeto.ARTEFACTO_MAGICO)));
        d.registrarObjeto(new Objeto("ConjuroVuelo", "Conjuro de Vuelo", EnumSet.of(CategoriaObjeto.ARTEFACTO_MAGICO)));

        d.registrarCapacidad(new Capacidad("Invisibilidad", "Invisibilidad"));
        d.registrarCapacidad(new Capacidad("Vuelo", "Vuelo"));
        d.registrarCapacidad(new Capacidad("Reflejo", "Reflejo"));

        d.registrarProporciona("CascoHades", "Invisibilidad");
        d.registrarProporciona("AnilloInvisibilidad", "Invisibilidad");
        d.registrarProporciona("EscudoEspejo", "Reflejo");
        d.registrarProporciona("ConjuroVuelo", "Vuelo");

        d.registrarPosesion("Hades", "CascoHades");
        d.registrarPosesion("Atenea", "EscudoBronce");
        return d;
    }
}
