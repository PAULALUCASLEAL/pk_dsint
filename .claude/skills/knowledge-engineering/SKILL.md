---
name: knowledge-engineering
description: Transforma textos narrativos (mitos griegos, el enunciado de DSINT) en hechos, conceptos, relaciones, precondiciones, acciones, consecuencias, reglas reutilizables y explicaciones, distinguiendo lo que dice el texto de lo que supone el programador. Usar al extraer reglas de un mito, revisar el catalogo de reglas, disenar escenarios EscenarioF2-x o justificar decisiones en los informes.
---

# knowledge-engineering (skill propia del proyecto DSINT)

> Skill creada por el equipo. **No es un recurso oficial.** Basada en metodologia clasica de
> sistemas basados en conocimiento (adquisicion, modelado, validacion).

## Cuando activarla

- Leer un resumen de mito y convertirlo en reglas.
- Comparar el catalogo de reglas del equipo con el enunciado.
- Diseñar escenarios de la fase 2 y su cadena causal.
- Redactar justificaciones para los informes.

## Procedimiento

1. **Extraer** del texto, frase a frase:
   - Entidades (personajes, objetos, capacidades) → candidatas a individuos de la ontologia.
   - Estados (vivo/muerto, preso/libre, posee).
   - Acciones (matar, apresar, liberar, localizar, obtener, intercambiar, robar).
   - Precondiciones («para matar a Medusa necesita vuelo, invisibilidad, reflejo y una hoz»).
   - Consecuencias («al morir Medusa aparece su cabeza y Pegaso»).
2. **Etiquetar el origen de cada regla:**
   - `[TEXTO]` aparece expresamente en el enunciado/mito (citar seccion).
   - `[EJEMPLO]` aparece en la salida de ejemplo del enunciado.
   - `[SUPUESTO]` decision del equipo (debe justificarse en el informe).
   El enunciado avisa de que sus ideas contienen informacion superflua o contradictoria a proposito.
3. **Generalizar:** sustituir objetos concretos por la capacidad que aportan (Vuelo, no Sandalias),
   personajes concretos por sus tipos/relaciones (un hijo de quien enoja a Poseidon), salvo cuando el
   mito exige el individuo.
4. **Formalizar** cada regla como: nombre, origen, SI (patrones), ENTONCES (insert/modify/delete),
   evento narrativo y su justificacion.
5. **Validar:** recorrer el escenario de ejemplo a mano y comprobar el orden parcial; probar
   variantes (sin escudo, con anillo, con conjuro) y casos negativos (sin zurron → el heroe muere).
6. **Explicar:** cada accion debe producir un evento con «debido a ...» que cite los hechos usados.

## Reglas de calidad

- Ninguna regla con respuesta fija («Perseo siempre libera a Andromeda»).
- No inventar contenido de documentos no leidos.
- Separar conocimiento por defecto (quien posee que al empezar) de condiciones del escenario.
- Vigilar reglas demasiado generales que hagan actuar a personajes no deseados (lo advierte el enunciado).

## Errores frecuentes

- Mezclar en una regla varias acciones encadenadas (pierde la explicacion paso a paso).
- Usar la pregunta como si fuera un hecho.
- Olvidar consecuencias de sentido comun (quien mata se queda las posesiones; el muerto no actua).

## Ejemplo

Texto: «Perseo obtiene Capacidad Invisibilidad debido a tener el casco de Hades» `[EJEMPLO]`.
Regla general: SI X posee O y O proporciona C ENTONCES X tiene C (fuente O) — `insertLogical`.

## Checklist

- [ ] Cada regla tiene origen etiquetado.
- [ ] Cada supuesto esta justificado.
- [ ] La cadena causal del escenario se puede leer en los eventos.
- [ ] Hay al menos un caso negativo probado.

## Referencias

- Enunciado DSINT 2026-2027, apartados 8-11 (fichero `PROYECTO - CURSO 2026-2027.pdf`).
- Schreiber et al., *Knowledge Engineering and Management: The CommonKADS Methodology*, MIT Press (referencia bibliografica, no incluida).
- Documentacion Drools 7.74.1 (explicaciones y TMS): https://docs.drools.org/7.74.1.Final/drools-docs/html_single/index.html
