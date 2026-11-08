---
name: owl-java-mapping
description: Disena el modelo Java (Java 8, JavaBeans) que representa una ontologia OWL para usarlo como hechos de Drools 7. Usar al crear o revisar las clases de sistema-inteligente (Personaje, Objeto, Capacidad, hechos de relacion), al traducir propiedades OWL a Java, al resolver herencia multiple OWL frente a herencia simple Java o al comprobar que el codigo respeta mitologia.owl.
---

# owl-java-mapping (skill propia del proyecto DSINT)

> Skill creada por el equipo. **No es un recurso oficial.** Se apoya en la especificacion OWL 2,
> la documentacion de Java 8 y el enunciado de DSINT (notas H-O).

## Cuando activarla

- Crear o modificar clases en `sistema-inteligente/src/main/java/dsi/mitologia/`.
- Decidir como se representa en Java una clase, individuo o propiedad del OWL.
- Revisar si un cambio de la ontologia obliga a cambiar el modelo.

## Principios

1. **No convertir automaticamente cada clase OWL en una clase Java.** Justificar cada decision.
2. **Herencia multiple:** OWL permite `Perseo : Heroe, Semidios`; Java no permite
   `extends Heroe, Semidios`. Opciones: (a) conjunto de tipos (`EnumSet<TipoPersonaje>`),
   (b) interfaces marcadoras, (c) clase concreta por combinacion (explota combinatoriamente: evitar).
   Para DSINT se prefiere (a) por sencillez y porque Drools filtra bien `Personaje(heroe)`.
3. **Individuos OWL → instancias Java** con identificador estable (el fragmento del IRI, p. ej. `Perseo`),
   nunca una clase por individuo.
4. **Propiedades de datos → atributos.** Si el rango es `oneOf`, usar `enum`. `cardinality 1` → campo
   no nulo con un unico valor.
5. **Propiedades de objeto → hechos de relacion** (`Posesion`, `Favor`, `Cautiverio`...), una clase
   pequena por relacion con significado propio. Asi las reglas usan patrones planos, sin navegar
   colecciones (nota K del enunciado).
6. **Inversas → un solo hecho.** `apresa_a` / `esta_preso_por` son el mismo `Cautiverio(captor, preso)`.
   Nunca guardar las dos direcciones por separado.
7. **Estatico frente a dinamico:** lo que no cambia (tipos, parentesco, que objeto da que capacidad)
   se comparte; lo que cambia (vida, libertad, posesiones) se copia por escenario.
8. **Una sola fuente de verdad por hecho mutable.** La posesion actual vive solo en `Posesion`.
9. **Java no infiere** (nota O): Java carga datos y comprueba integridad; las reglas deciden.

## Procedimiento

1. Inventariar la ontologia (skill `owl-ontology-expert`).
2. Tabla de correspondencia OWL → Java (clase/enum/atributo/hecho) con justificacion.
3. Implementar POJOs: constructor, getters, `equals/hashCode` por identificador (o por valor si el hecho
   es inmutable), `toString` legible. Sin tildes ni enes en identificadores.
4. Separar dominio (inmutable) de estado de aventura (copiable).
5. Pruebas JUnit 4 para cada invariante (sin duplicados, sin propietarios incompatibles, aislamiento).
6. Documentar en `documentacion/MODELO_JAVA.md` lo que aun no se ha incorporado.

## Reglas de calidad (Java 8)

- Nada posterior a Java 8: sin `var`, `record`, `List.of`, `switch` con flechas, text blocks.
- Colecciones expuestas como `Collections.unmodifiable...`.
- Nada mutable en `static`.
- Getters JavaBean (`getX`, `isX`) para que Drools/MVEL los lean.

## Errores frecuentes

- Un objeto «Dios» con un `List<Objeto>` y otro objeto con `propietario` → dos verdades.
- Booleanos especificos (`perseoTieneCasco`).
- Calcular en Java capacidades o muertes (eso es inferencia: corresponde a las reglas).
- Modificar en Drools un hecho sin `modify`/`update` (la red RETE no se entera).

## Ejemplo

```java
Personaje perseo = new Personaje("Perseo", "Perseo", EnumSet.of(TipoPersonaje.HEROE, TipoPersonaje.SEMIDIOS));
Posesion p = new Posesion(hades, cascoHades);   // tiene_objeto / esta_en_posesion_de
```

## Checklist

- [ ] Cada clase Java tiene una justificacion frente al OWL.
- [ ] Ninguna relacion inversa duplicada.
- [ ] Estados con `enum`, nunca cadenas sueltas.
- [ ] Compila con `-source 1.8 -target 1.8`.
- [ ] Pruebas de aislamiento entre escenarios.

## Referencias

- W3C OWL 2 Primer: https://www.w3.org/TR/owl2-primer/
- OWL API (si en algun momento se carga el OWL con una libreria): https://github.com/owlcs/owlapi
- Java SE 8 API: https://docs.oracle.com/javase/8/docs/api/
- JavaBeans spec: https://www.oracle.com/java/technologies/javase/javabeans-spec.html
