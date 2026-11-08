package dsi.mitologia.modelo;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import dsi.mitologia.modelo.enums.CategoriaObjeto;

/**
 * Clase OWL {@code Objeto} y sus subclases (Arma, Proteccion, ArtefactoMagico, ParteAnatomica).
 *
 * <p>Inmutable: es conocimiento del dominio y se comparte entre escenarios. Quien lo posee lo dice
 * el hecho {@code Posesion}; que capacidad da lo dice el hecho {@code ProporcionaCapacidad}.</p>
 */
public final class Objeto implements EntidadMitologica {

    private final String id;
    private final String nombre;
    private final Set<CategoriaObjeto> categorias;

    public Objeto(String id, String nombre, Set<CategoriaObjeto> categorias) {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("El objeto necesita id");
        this.id = id;
        this.nombre = nombre == null ? id : nombre;
        this.categorias = categorias == null || categorias.isEmpty()
                ? Collections.<CategoriaObjeto>emptySet()
                : Collections.unmodifiableSet(EnumSet.copyOf(categorias));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public Set<CategoriaObjeto> getCategorias() {
        return categorias;
    }

    public boolean esDeCategoria(CategoriaObjeto categoria) {
        return categorias.contains(categoria);
    }

    public boolean isArma() {
        return esDeCategoria(CategoriaObjeto.ARMA);
    }

    public boolean isProteccion() {
        return esDeCategoria(CategoriaObjeto.PROTECCION);
    }

    public boolean isArtefactoMagico() {
        return esDeCategoria(CategoriaObjeto.ARTEFACTO_MAGICO);
    }

    public boolean isParteAnatomica() {
        return esDeCategoria(CategoriaObjeto.PARTE_ANATOMICA);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Objeto && id.equals(((Objeto) o).id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return nombre + categorias;
    }
}
