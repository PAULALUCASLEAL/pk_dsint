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

Las cuatro se escribieron a partir de documentacion publica y del enunciado; **no se han descargado**.

### Skills de terceros: descargadas, revisadas e instaladas

Al principio de la sesion no habia conexion; despues volvio y se clonaron los repositorios con
`git clone --depth 1` en una carpeta temporal. Se leyo cada `SKILL.md` y se listaron sus ficheros antes de
copiarlos. **No se ha ejecutado nada de terceros** (ni `npx skills`, ni `/plugin`, ni scripts).
Solo se han copiado ficheros `.md`.

| Skill | Repositorio (commit revisado) | Licencia | Ubicacion | Estado / observaciones |
|---|---|---|---|---|
| `java-development` | https://github.com/github/awesome-copilot (`797337f`, 7-nov-2026) | MIT | `.claude/skills/java-development/` | Instalada. Elige idiomas segun la version de Java del `pom.xml` y prohibe subirla: compatible con Java 8 |
| `java-debugging` | github/awesome-copilot (`797337f`) | MIT | `.claude/skills/java-debugging/` | Instalada. Partes de Spring Boot/Quarkus no aplican |
| `refactor` | github/awesome-copilot (`797337f`) | MIT | `.claude/skills/refactor/` | Instalada. Ejemplos en varios lenguajes: aplicar solo idiomas de Java 8 |
| `test-driven-development` | https://github.com/obra/superpowers (`22f0b46`, 3-nov-2026) | MIT | `.claude/skills/test-driven-development/` | Instalada. Ejemplos con `npm test`: aqui equivale a `mvn test` |
| `systematic-debugging` | obra/superpowers (`22f0b46`) | MIT | `.claude/skills/systematic-debugging/` | Instalada sin `find-polluter.sh` (script que llama a `npm test`; revisado, inofensivo, pero no aplica) ni el ejemplo `.ts` |
| `verification-before-completion` | obra/superpowers (`22f0b46`) | MIT | `.claude/skills/verification-before-completion/` | Instalada |
| `writing-plans` | obra/superpowers (`22f0b46`) | MIT | `.claude/skills/writing-plans/` | Instalada |
| `architecture-designer` | https://github.com/Jeffallan/claude-skills (`078a25c`, 30-oct-2026) | MIT | `.claude/skills/architecture-designer/` | Instalada. Orientada a sistemas distribuidos: usar solo para decisiones y ADR |
| `code-reviewer` | Jeffallan/claude-skills (`078a25c`) | MIT | `.claude/skills/code-reviewer/` | Instalada |
| `test-master` | Jeffallan/claude-skills (`078a25c`) | MIT | `.claude/skills/test-master/` | Instalada |
| `skill-creator` | https://github.com/anthropics/skills | Apache 2.0 | — | **Ya disponible** en Claude Code como `anthropic-skills:skill-creator`; no se copia |

No se instalo el plugin completo de Jeffallan (mas de 60 skills, casi todas de otras tecnologias) ni
`java-architect`/`spring-boot-engineer` (enfocadas a Spring).

**Las skills de terceros no se suben al repositorio** (`.gitignore`): son contenido descargado. Para
reinstalarlas, clonar los repositorios en esos commits y copiar las carpetas indicadas a `.claude/skills/`.
Solo se versionan las cuatro skills propias.

## 2. Para que sirve cada skill en el proyecto

| Tarea | Skills |
|---|---|
| Inventario y revision de `mitologia.owl` | `owl-ontology-expert` |
| Diseno de clases Java (fase 1.b) | `owl-java-mapping`, `java-development`, `refactor`, `architecture-designer` |
| Extraccion de reglas del mito y revision del catalogo de 48 reglas | `knowledge-engineering` |
| Implementacion de `.drl`, sesiones y agendas | `drools-7-expert` |
| Pruebas y depuracion | `drools-7-expert`, `test-driven-development`, `test-master`, `systematic-debugging`, `java-debugging` |
| Revision antes de entregar | `code-reviewer`, `verification-before-completion` |
| Planificacion de tareas largas | `writing-plans` |

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
- Las propias fijan Java 8, Drools 7.74.1 y que Java no infiere. Posible conflicto con las de terceros: ejemplos con Java moderno o `npm`; manda la version del `pom.xml` (lo dice la propia `java-development`).
- Sin duplicados: ninguna skill instalada repite otra.
- No se ha tocado `ontologia/mitologia.owl` ni el codigo Java en esta rama.
- No se ha ejecutado ningun script de terceros; el catalogo de 48 reglas (Google Drive) es privado (HTTP 401) y no se ha podido leer.
- Que Claude Code las reconozca se comprueba abriendo una sesion en `pk_dsint` y pidiendo «lista tus
  skills»; no se ha podido comprobar desde esta sesion porque se abrio en la carpeta superior.
