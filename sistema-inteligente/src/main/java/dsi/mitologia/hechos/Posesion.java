package dsi.mitologia.hechos;

import dsi.mitologia.modelo.Objeto;
import dsi.mitologia.modelo.Personaje;

/**
 * Quien posee actualmente un objeto. Propiedades OWL: tiene_objeto / esta_en_posesion_de.
 *
 * <p>Es la UNICA fuente de verdad de las posesiones: ni Personaje tiene una lista de objetos ni
 * Objeto tiene un propietario. Hay como mucho una Posesion por objeto (lo garantiza
 * {@code EstadoAventura}); un objeto sin Posesion no lo tiene nadie.</p>
 *
 * <p>Transferir = cambiar el poseedor del mismo hecho. En Drools:
 * {@code modify($pos) { setPoseedor($heroe) }}. Por eso el objeto es fijo y solo el poseedor
 * cambia, y la igualdad es por identidad (el hecho conserva su identidad al transferirse).</p>
 */
public class Posesion {

    private Personaje poseedor;
    private final Objeto objeto;

    public Posesion(Personaje poseedor, Objeto objeto) {
        if (objeto == null) throw new IllegalArgumentException("Posesion necesita objeto");
        setPoseedor(poseedor);
        this.objeto = objeto;
    }

    public Personaje getPoseedor() {
        return poseedor;
    }

    public void setPoseedor(Personaje poseedor) {
        if (poseedor == null) {
            throw new IllegalArgumentException("Para que nadie posea el objeto se retira la Posesion");
        }
        this.poseedor = poseedor;
    }

    public Objeto getObjeto() {
        return objeto;
    }

    @Override
    public String toString() {
        return "Posesion(" + poseedor.getId() + " tiene " + objeto.getId() + ")";
    }
}
