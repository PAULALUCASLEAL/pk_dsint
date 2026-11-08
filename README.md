# pk_dsint — Sistema inteligente sobre mitología griega (DSINT, UMU 2026/2027)

## Objetivo

Proyecto de la asignatura **Desarrollo de Sistemas Inteligentes** (Universidad de Murcia).
Construimos un sistema basado en reglas que responde preguntas sobre mitos griegos
(«¿puede Perseo liberar a Andrómeda si …?») a partir de unas condiciones iniciales.

- **Fase 1.a:** ontología del dominio con Protégé → `ontologia/mitologia.owl`.
- **Fase 1.b:** sistema inteligente para el mito de Perseo y Andrómeda → `sistema-inteligente/`.
- **Fase 2:** resto de mitos (Laberinto de Creta, Ícaro y Dédalo, Heracles).

## Tecnologías y versiones

| Tecnología | Versión | Notas |
|---|---|---|
| Eclipse IDE for Java Developers | 2020-12 R (4.18) | Paquete macOS x86_64, se ejecuta con Rosetta 2 en Apple Silicon |
| Java del proyecto | 1.8 (compliance y bytecode 52) | JDK usado: Azul Zulu 8 (arm64) |
| Java que ejecuta Eclipse | JRE 15 que trae Eclipse (JustJ, x86_64) | No es el mismo que compila el proyecto |
| Drools Runtime | 7.74.1.Final | `drools-distribution-7.74.1.Final.zip` |
| Plugin Drools para Eclipse | 7.46.0.Final | Solo «JBoss Drools Core» + «Developer Resources» |
| Logging | slf4j-simple 1.7.26 | Un único proveedor de logging |
| Maven | 3.9.x | Solo para `sistema-inteligente/` |
| Protégé | 3.4.8 (original) / 5.6.9 (edición actual) | |

El profesor desaconseja Drools anterior a 7.43 y Drools 8 o superior: **no actualizar Drools a 8.x**.

## Estructura de carpetas

```text
pk_dsint/
├── ontologia/
│   └── mitologia.owl            ontología (fase 1.a) — no se modifica en esta tarea
├── drools-prueba/               proyecto de prueba del profesor (Hello World de Drools)
│   ├── src/main/java/com/sample/DroolsTest.java
│   ├── src/main/resources/com/sample/rules/Sample.drl
│   ├── src/main/resources/META-INF/kmodule.xml
│   └── lib/slf4j-simple-1.7.26.jar
├── sistema-inteligente/         proyecto real DSI_Mitologia (Maven, Drools 7.74.1, Java 8)
│   ├── pom.xml
│   └── src/
│       ├── main/java/dsi/mitologia/Principal.java
│       ├── main/resources/META-INF/kmodule.xml
│       ├── main/resources/reglas/prueba-integracion.drl
│       └── test/java/dsi/mitologia/PruebaIntegracionDroolsTest.java
├── escenarios/                  ficheros EscenarioF2-x.txt (vacía de momento)
├── documentacion/
│   └── CONFIGURACION_ENTORNO.md guía paso a paso del entorno y pruebas realizadas
└── README.md
```

## Requisitos previos

1. Eclipse 2020-12 con el plugin de Drools 7.46 (ver `documentacion/CONFIGURACION_ENTORNO.md`).
2. `drools-distribution-7.74.1.Final.zip` descomprimido (en nuestro caso en `~/DSINT/software/`).
3. Un JDK 8 registrado en Eclipse (Preferences → Java → Installed JREs).
4. En Apple Silicon, Rosetta 2 instalado (`softwareupdate --install-rosetta`).

## Cómo abrir los proyectos en Eclipse

Usa un workspace solo para DSINT (por ejemplo `~/DSINT/eclipse-workspace`).

- **drools-prueba:** File → Import → General → *Existing Projects into Workspace* →
  carpeta `pk_dsint/drools-prueba` → Finish (sin marcar «Copy projects into workspace»).
- **sistema-inteligente:** File → Import → Maven → *Existing Maven Projects* →
  carpeta `pk_dsint/sistema-inteligente` → Finish.

Si aparece «Marketplace solutions available», pulsa **Cancel** (lo indica el profesor).

## Cómo configurar Drools Runtime

Eclipse → Settings… (Window → Preferences en Windows) → Drools → **Installed Drools Runtime** →
Add… → Browse… → carpeta `drools-distribution-7.74.1.Final/binaries` → OK →
marca el runtime (7.74.1) → **Apply and Close**.

`drools-prueba` usa este runtime (la «Drools Library» del build path).
`sistema-inteligente` no lo necesita: Maven descarga Drools 7.74.1 por su cuenta.

## Cómo ejecutar y comprobar que funciona

**drools-prueba** — botón derecho en `DroolsTest.java` → Run As → Java Application. Debe salir:

```text
Hello World
Goodbye cruel world
```

(precedido de varias líneas `[main] INFO org.drools...`, que son el log de Drools).

**sistema-inteligente** — botón derecho en `Principal.java` → Run As → Java Application, o en terminal:

```bash
cd sistema-inteligente
JAVA_HOME=$(/usr/libexec/java_home -v 1.8) mvn test exec:java
```

Debe salir `Drools 7.74.1 funcionando: regla de prueba ejecutada` y `Tests run: 1, Failures: 0`.

## Problemas conocidos en macOS

- **No hay Eclipse 2020-12 nativo para Apple Silicon.** El soporte arm64 para macOS llegó en 2021-12.
  Usamos el paquete x86_64, que funciona con Rosetta 2. La primera vez tarda algo más en abrir.
- **Dos Java distintos.** Eclipse se ejecuta con su JRE 15 incluido; el proyecto se compila
  y ejecuta con el JDK 8. Si Eclipse marca «JavaSE-1.8 unbound», registra el JDK 8 en Installed JREs.
- **Permisos.** Si macOS bloquea Eclipse al abrirlo: botón derecho sobre la app → Abrir.
- **Ya hay otro Eclipse instalado** (`/Applications/Eclipse.app`, 2025-09). No se toca;
  el de DSINT vive aparte, en `~/DSINT/software/Eclipse.app`.
- **La distribución de Drools no trae `slf4j-simple-1.7.26.jar`** (solo `slf4j-api-1.7.30.jar`).
  Lo hemos incluido en `drools-prueba/lib/`, sacado de Maven Central.

## Estado actual

- [x] Ontología fase 1.a incluida (`ontologia/mitologia.owl`, sin cambios).
- [x] Drools 7.74.1 + Java 8 probados: `drools-prueba` imprime `Hello World` / `Goodbye cruel world`.
- [x] `sistema-inteligente` compila con Java 8 y pasa la prueba de integración.
- [x] Modelo Java de hechos (paquetes `modelo`, `hechos`, `eventos`, `escenario`, `ontologia`, `motor`) con 26 pruebas JUnit — ver `documentacion/MODELO_JAVA.md`.
- [ ] Reglas del mito de Perseo y Andrómeda (fase 1.b) — **no empezadas**.

## Próximos pasos (fase 1.b)

1. Definir las clases de hechos a partir de la ontología (Personaje, Objeto, Capacidad, relaciones).
2. Leer los ficheros `escenarios/EscenarioF2-x.txt` (pregunta + condiciones) e insertar los hechos.
3. Conocimiento por defecto (quién tiene cada objeto al empezar) y agenda «Informacion Inicial».
4. Primeras reglas del mito de Andrómeda y la escritura de `soluciones/RespuestaF1_x.txt`.
5. Borrar `prueba-integracion.drl` cuando haya reglas reales.
