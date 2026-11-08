---
name: owl-ontology-expert
description: Analiza, revisa y explica ontologias OWL 2 / RDF(S) y su modelado en Protege (clases, individuos, propiedades de objeto y de datos, dominio y rango, inversas, cardinalidades, disyuncion, mundo abierto, razonamiento con HermiT). Usar cuando se lea o se discuta ontologia/mitologia.owl, se haga inventario de la ontologia, se detecten inconsistencias o se prepare el informe de la fase 1.a de DSINT.
---

# owl-ontology-expert (skill propia del proyecto DSINT)

> Skill creada por el equipo para el proyecto pk_dsint. **No es un recurso oficial**
> de Anthropic, del W3C ni de Stanford. Resume documentacion publica (ver Referencias).

## Cuando activarla

- Leer, inventariar o explicar `ontologia/mitologia.owl`.
- Decidir si un concepto debe ser clase, individuo, propiedad de objeto o de datos.
- Revisar restricciones (cardinalidad, `allValuesFrom`, `someValuesFrom`), inversas y disyunciones.
- Preparar el informe de la fase 1.a o justificar cambios de la ontologia.

## Principios

1. **El fichero OWL es la fuente de verdad** de lo que esta declarado. Nunca atribuir a la
   ontologia algo que no aparece en el fichero; si falta, decir «no esta declarado».
2. **Mundo abierto (OWA):** que no se afirme `Perseo tiene_objeto CascoHades` no significa que sea
   falso. Las reglas de produccion (Drools) trabajan con mundo cerrado: lo que no esta en la
   memoria de trabajo no existe. Documentar siempre esa diferencia.
3. **Sin nombre unico (no UNA):** dos individuos con distinto IRI pueden ser el mismo salvo
   `owl:differentFrom` / `owl:AllDifferent`.
4. **Herencia multiple:** un individuo puede tener varios `rdf:type` (p. ej. `Heroe` y `Semidios`).
   Las subclases no son disjuntas salvo que se declare `owl:disjointWith`.
5. **Dominio y rango no son restricciones de comprobacion**, son axiomas de inferencia: si
   `tiene_favor` tiene rango `Dios`, un razonador *deduce* que el destino es `Dios`.
6. `allValuesFrom` (∀) no obliga a que exista ningun valor; `someValuesFrom` (∃) si.
   `cardinality 1` en una propiedad de datos = exactamente un valor (funcional + obligatoria).

## Procedimiento de trabajo

1. Leer el RDF/XML completo (no fiarse de una sola serializacion: Protege 3 y 5 escriben distinto,
   p. ej. `owl:DataRange` frente a `rdfs:Datatype`, `owl:NamedIndividual` frente a `rdf:Description`).
2. Inventario exacto: clases y jerarquia, disyunciones, propiedades de objeto (dominio, rango,
   inversa, caracteristicas), propiedades de datos (dominio, rango, `oneOf`), restricciones, individuos
   con tipos y asertos.
3. Detectar problemas: inversas declaradas solo en un sentido, propiedades sin dominio/rango,
   clases sin instancia (el enunciado exige al menos una por clase concreta), clases sin
   `rdfs:comment`, valores de datos fuera del `oneOf`, contradicciones entre asertos y estado inicial.
4. Si se dispone de Protege, comprobar con HermiT: Reasoner → HermiT → Start reasoner; revisar
   clases en rojo (insatisfacibles) y «Explain inconsistency».
5. Documentar discrepancias **antes** de proponer cambios. No modificar el OWL sin permiso explicito.

## Reglas de calidad

- Nombres sin tildes ni enes (lo exige el enunciado).
- Cada clase con `rdfs:comment` (metadatos) y, si es posible, referencia a la fuente.
- Al menos 3 restricciones en la ontologia (requisito del enunciado).
- Distinguir conocimiento estatico (tipos, parentesco, que objeto da que capacidad) de estado
  (vivo/muerto, preso/libre, quien posee que).

## Errores frecuentes

- Crear una clase por individuo (`ClasePerseo`).
- Usar `subClassOf` para relaciones parte-todo o de posesion.
- Confundir `allValuesFrom` con «tiene al menos uno».
- Pensar que la ausencia de un aserto lo hace falso (OWA).
- Duplicar asertos inversos a mano y que queden incoherentes.

## Ejemplo (Manchester syntax)

```
Class: Personaje
    SubClassOf: estado_vital exactly 1, tiene_favor only Dios
Individual: Perseo
    Types: Heroe, Semidios
    Facts: es_hijo_de Zeus, es_hijo_de Danae, estado_vital "vivo"
```

## Checklist

- [ ] Inventario completo y verificado contra el fichero.
- [ ] Inversas, dominios y rangos revisados.
- [ ] Restricciones de cardinalidad identificadas y explicadas.
- [ ] Discrepancias con el enunciado documentadas.
- [ ] Ninguna afirmacion sobre la ontologia sin respaldo en el fichero.

## Referencias

- W3C, *OWL 2 Web Ontology Language Primer*: https://www.w3.org/TR/owl2-primer/
- W3C, *OWL 2 Structural Specification and Functional-Style Syntax*: https://www.w3.org/TR/owl2-syntax/
- W3C, *RDF Schema 1.1*: https://www.w3.org/TR/rdf-schema/
- Protege: https://protege.stanford.edu/
- Noy y McGuinness, *Ontology Development 101*: https://protege.stanford.edu/publications/ontology_development/ontology101.pdf
