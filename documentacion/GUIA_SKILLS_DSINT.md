# Guia de skills de Claude Code para DSINT

Fecha: 8-nov-2026. Rama: `feature/skills-dsint`.

## 1. Estado de la instalacion

Las skills viven en `pk_dsint/.claude/skills/`. Claude Code las detecta cuando se abre una sesion
**con `pk_dsint` como carpeta de trabajo** (en la carpeta superior `PROYECTO FINAL` no se ven).

### Skills creadas por el equipo (propias, no oficiales)

| Skill | Especialidad | Documentacion en la que se basa | Ubicacion | Estado |
|---|---|---|---|---|
| `owl-ontology-expert` | OWL 2, RDF(S), Protege, HermiT, mundo abierto | W3C OWL 2 Primer y Syntax, RDF Schema, Protege, *Ontology Development 101* | `.claude/skills/owl-ontology-expert/SKILL.md` | Creada; frontmatter valido |
| `owl-java-mapping` | Traduccion OWL → modelo Java 8 para Drools | W3C OWL 2 Primer, OWL API, Java SE 8 API, notas H-O del enunciado | `.claude/skills/owl-java-mapping/SKILL.md` | Creada; frontmatter valido |
| `drools-7-expert` | DRL, KIE API, TMS, agendas, pruebas, Drools 7.74.1 | docs.drools.org 7.74.1.Final, download.jboss.org | `.claude/skills/drools-7-expert/SKILL.md` | Creada; frontmatter valido |
| `knowledge-engineering` | Extraer reglas de los mitos, origen texto/supuesto, validacion | Enunciado DSINT §8-11, CommonKADS (referencia) | `.claude/skills/knowledge-engineering/SKILL.md` | Creada; frontmatter valido |

Las cuatro se escribieron a partir de documentacion publica conocida y del enunciado; **no se han
descargado** de ningun repositorio. Las URL de referencia **no se han podido abrir hoy** (ver 1.2),
asi que conviene revisarlas cuando haya conexion.

### Skills de terceros solicitadas: NO instaladas

El 8-nov-2026 el ordenador no tenia conexion a Internet durante la sesion (`Could not resolve host:
github.com`; tambien fallaban `docs.google.com` y la descarga de paquetes). Por eso **no se ha
descargado, revisado ni instalado** ninguna skill de terceros. No se afirma nada sobre su contenido actual.

| Repositorio | Skills pedidas | Estado | Como instalarlas cuando haya red |
|---|---|---|---|
| https://github.com/github/awesome-copilot | `java-development`, `java-debugging`, `refactor` | Pendiente (sin red) | Clonar, **leer** cada `SKILL.md` y copiarlo a `.claude/skills/<nombre>/`. Alternativa: `npx -y skills add github/awesome-copilot --skill <nombre> --agent claude-code` (ejecuta un paquete npm de terceros: revisarlo antes) |
| https://github.com/Jeffallan/claude-skills | arquitectura, POO, revision de codigo, pruebas | Pendiente (sin red) | Desde una terminal `claude`: `/plugin marketplace add jeffallan/claude-skills`; o copiar solo las skills pertinentes |
| https://github.com/obra/superpowers | `test-driven-development`, `systematic-debugging`, `verification-before-completion`, `writing-plans` | Pendiente (sin red) | Seguir su README actual (normalmente via `/plugin`) |
| https://github.com/anthropics/skills | `skill-creator` | **Ya disponible** en Claude Code como `anthropic-skills:skill-creator` (no hace falta copiarla) | — |

Al instalarlas, comprobar que sus ejemplos no usan Java > 8 (`var`, `record`, `List.of`, `switch` con
flechas, text blocks) y no recomiendan Drools 8+.

## 2. Para que sirve cada skill en el proyecto

| Tarea | Skills |
|---|---|
| Inventario y revision de `mitologia.owl` | `owl-ontology-expert` |
| Diseno de clases Java (fase 1.b) | `owl-java-mapping` (+ `java-development`/`refactor` cuando se instalen) |
| Extraccion de reglas del mito y revision del catalogo de 48 reglas | `knowledge-engineering` |
| Implementacion de `.drl`, sesiones y agendas | `drools-7-expert` |
| Pruebas y depuracion | `drools-7-expert` (+ `systematic-debugging`, `test-driven-development` cuando se instalen) |

## 3. Como se comprobara que el codigo respeta la ontologia

1. Inventario del OWL (clases, propiedades, individuos) en `documentacion/MODELO_JAVA.md`, con una
   tabla OWL → Java.
2. Los identificadores Java de personajes, objetos y capacidades son los fragmentos del IRI del OWL
   (`Perseo`, `CascoHades`, `Invisibilidad`).
3. Un cargador lee `ontologia/mitologia.owl` y una prueba JUnit comprueba que los tipos, posesiones y
   capacidades cargados coinciden con el fichero, de modo que si el OWL cambia, la prueba avisa.
4. El OWL no se modifica desde el codigo ni en estas tareas.

## 4. Como evitar reglas demasiado especificas para Perseo

- Las reglas comprueban **capacidades** (`Vuelo`, `Invisibilidad`, `Reflejo`) y no objetos concretos.
- Las condiciones usan **tipos y relaciones** (`Personaje(heroe)`, `Favor(...)`) en lugar de nombres.
- Los objetos concretos y sus duenos son **conocimiento por defecto + condiciones del escenario**, no
  codigo.
- Cada regla del catalogo se etiqueta `[TEXTO]`, `[EJEMPLO]` o `[SUPUESTO]` (skill `knowledge-engineering`).
- Se probaran variantes del enunciado (anillo de invisibilidad, espejo de mano, conjuro de vuelo).

## 5. SOLID sin complicar el proyecto

- **Responsabilidad unica:** cada hecho representa una sola relacion; las operaciones de estado estan
  separadas de las reglas que deciden cuando usarlas.
- **Abierto/cerrado:** nuevas capacidades, objetos o personajes se añaden como datos, sin clases nuevas.
- **Sustitucion / segregacion de interfaces:** solo una interfaz pequena (`EntidadMitologica`) donde
  aporta algo real (localizar personas u objetos).
- **Inversion de dependencias:** el modelo no depende de Drools; solo el lanzador del motor lo hace.
- Sin capas extra, fabricas abstractas ni inyeccion de dependencias: es un proyecto universitario.

## 6. Comprobaciones realizadas

- Los cuatro `SKILL.md` existen y tienen frontmatter `name` / `description`.
- No hay contradicciones entre ellas: todas fijan Java 8 y Drools 7.74.1, y la regla de que Java no infiere.
- No se ha tocado `ontologia/mitologia.owl` ni el codigo Java en esta rama.
- No se ha ejecutado ningun script de terceros.
- Que Claude Code las reconozca se comprueba abriendo una sesion en `pk_dsint` y pidiendo «lista tus
  skills»; no se ha podido comprobar desde esta sesion porque se abrio en la carpeta superior.
