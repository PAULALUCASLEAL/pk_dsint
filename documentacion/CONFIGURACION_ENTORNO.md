# Configuración del entorno: Java 8 + Eclipse 2020-12 + Drools 7.74.1 (macOS Apple Silicon)

Guía para dejar listo el entorno de DSINT desde cero, pensada para quien no ha usado Drools nunca.
Sigue el documento del profesor **«Instalar Drools.pdf»**; donde nos apartamos de él, se explica por qué.

## 0. Qué es cada pieza

| Pieza | Para qué sirve |
|---|---|
| **Drools** | Motor de reglas: le das hechos («Perseo tiene el favor de Atenea») y reglas («si alguien tiene el favor de Atenea, recibe el escudo») y él deduce lo que pasa. |
| **Drools Runtime** (`drools-distribution-7.74.1.Final.zip`) | Los jars del motor. Eclipse los añade al proyecto como «Drools Library». |
| **Plugin de Drools para Eclipse** (7.46) | Añade a Eclipse el asistente «Drools Project», el editor de `.drl` con colores y la vista *Outline* de reglas. No es el motor: el motor es el runtime. |
| **Java 8** | La versión de Java con la que compilamos y ejecutamos el proyecto (`compliance 1.8`). |
| **Maven** | Descarga las librerías declaradas en `pom.xml`. Lo usa `sistema-inteligente`. El runtime de Eclipse es otra forma de conseguir Drools (la usan los proyectos Drools «clásicos»); no se mezclan en un mismo proyecto. |

## 1. Comprobaciones previas del ordenador (resultados reales, 8-nov-2026)

| Comprobación | Comando | Resultado |
|---|---|---|
| Arquitectura | `uname -m` | `arm64` (Apple Silicon), macOS 26.2 |
| Rosetta 2 | `pgrep oahd` | Instalado y activo |
| Java por defecto | `java -version` | Temurin 25.0.2 (no se toca) |
| JDK 8 | `/usr/libexec/java_home -V` | Azul Zulu 8.98 (1.8.0_512, arm64) en `~/Library/Java/JavaVirtualMachines/zulu-8.jdk` — ya estaba instalado |
| Maven | `mvn -version` | 3.9.11 |
| Git | `git --version` | 2.51.0 |
| GitHub | `git ls-remote https://github.com/PAULALUCASLEAL/pk_dsint.git` | Acceso correcto; repositorio vacío |
| Eclipse | `/Applications/Eclipse.app` | Eclipse 2025-09 (4.37): **no** es la versión del profesor y se deja intacto |
| Drools | `~/.m2` | Solo había jars de Drools 8 en la caché de Maven, de una prueba anterior; no los usa este proyecto |

**Decisión:** no se cambia el Java global. Para compilar con Java 8 desde la terminal se usa
`JAVA_HOME=$(/usr/libexec/java_home -v 1.8)` solo en ese comando.

## 2. Carpeta de software de DSINT

Como recomienda el profesor, todo el software de la asignatura va en una carpeta propia:

```text
~/DSINT/
├── software/
│   ├── Eclipse.app                          Eclipse 2020-12 (solo para DSINT)
│   ├── drools-distribution-7.74.1.Final/    Drools Runtime (binaries/ = 103 jars)
│   ├── eclipse-java-2020-12-R-macosx-cocoa-x86_64.dmg
│   └── drools-distribution-7.74.1.Final.zip
└── eclipse-workspace/                       workspace exclusivo de DSINT
```

Nada de esto se sube a GitHub (está en `.gitignore`).

## 3. Eclipse 2020-12 en Apple Silicon

- Descarga: `eclipse-java-2020-12-R-macosx-cocoa-x86_64.dmg` (338 MB) de
  <https://archive.eclipse.org/technology/epp/downloads/release/2020-12/R/>
  (es el mismo paquete que enlaza la página del profesor; los paquetes antiguos se sirven desde el archivo de Eclipse).
- **Por qué x86_64:** Eclipse 2020-12 no tiene versión para Apple Silicon (llegó en 2021-12).
  El paquete Intel funciona con **Rosetta 2**. Es la opción fiel a la versión del profesor.
- **Alternativa si diera problemas:** Eclipse 2021-12 para aarch64. En la diapositiva 23 se ve
  que el propio profesor lo usa en su Mac con Drools 7.74.1. No se ha usado porque la versión indicada funciona.
- **Dos Java:** Eclipse 2020-12 se ejecuta con el JRE 15 que trae dentro (JustJ, x86_64).
  El proyecto se compila con Java 8 (Zulu 8 arm64). Son cosas independientes.

### Abrir Eclipse

1. Abre `~/DSINT/software/Eclipse.app` (la primera vez: botón derecho → Abrir).
2. En «Select a directory as workspace» escribe `~/DSINT/eclipse-workspace` y pulsa **Launch**.
3. Registra el JDK 8: Eclipse → Settings… → Java → Installed JREs → Add… → Standard VM →
   Directory: `/Users/<tu-usuario>/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home` → Finish.
   Después, en Java → Installed JREs → Execution Environments, asocia **JavaSE-1.8** a ese JDK.

## 4. Plugin de Drools 7.46 (solo dos componentes)

Repositorio: <https://download.jboss.org/drools/release/7.46.0.Final/org.drools.updatesite/>

Como pide la diapositiva 9, solo se instalan:

| Componente en la ventana de Eclipse | Identificador técnico |
|---|---|
| JBoss Drools Core | `org.drools.eclipse.feature` |
| JBoss Drools Core Developer Resources | `org.drools.eclipse.feature.source` |

No se instalan Guvnor, jBPM, Kie Navigator ni el Runtime Detector.

**Por la interfaz** (el método del PDF): Help → Install New Software… → Add… →
Name `Repositorio Drools`, Location = la URL de arriba → Add → marca solo los dos componentes →
Next → acepta la licencia → Finish → «Install anyway» → **Restart Now**.

**Por terminal** (equivalente, con Eclipse cerrado):

```bash
~/DSINT/software/Eclipse.app/Contents/MacOS/eclipse -nosplash -consoleLog \
  -application org.eclipse.equinox.p2.director \
  -repository https://download.jboss.org/drools/release/7.46.0.Final/org.drools.updatesite/,https://download.eclipse.org/releases/2020-12 \
  -installIU org.drools.eclipse.feature.feature.group,org.drools.eclipse.feature.source.feature.group
```

## 5. Drools Runtime 7.74.1

1. Descarga `drools-distribution-7.74.1.Final.zip` (72 MB) de
   <https://download.jboss.org/drools/release/7.74.1.Final/> y descomprímelo en `~/DSINT/software/`.
2. En Eclipse: Eclipse → Settings… → Drools → **Installed Drools Runtime** → **Add…** → **Browse…** →
   elige `~/DSINT/software/drools-distribution-7.74.1.Final/binaries` → debe aparecer
   la versión `7.74.1.<fecha>` → **OK**.
3. **Marca la casilla** del runtime (será el predeterminado) → **Apply and Close**.
   Vuelve a entrar para comprobar que sigue marcado: a veces se desmarca (diapositiva 24).

Esto es distinto de declarar dependencias en Maven: aquí le dices a Eclipse dónde están los jars
para los proyectos Drools «clásicos», como el de prueba del apartado 6.

## 6. Prueba con el proyecto de ejemplo del profesor (`drools-prueba`)

Para comprobar la instalación se usó el proyecto de ejemplo del asistente del plugin
(File → New → Project → Drools → Drools Project → «Create a project and populate it with some example files»
→ nombre `drools-prueba` → solo «Add a sample HelloWorld rule file»): una clase `DroolsTest.java`,
un `Sample.drl` con las reglas «Hello World» y «GoodBye», y un `kmodule.xml`.

Se ejecutó en Eclipse 2020-12 (DroolsTest → Run As → Java Application) el 9-nov-2026 con este resultado:

```text
[main] INFO ... Found kmodule: file:/…/drools-prueba/target/classes/META-INF/kmodule.xml
[main] INFO ... Start creation of KieBase: rules
[main] INFO ... End creation of KieBase: rules
Hello World
Goodbye cruel world
```

Superada la prueba, **el proyecto se retiró del repositorio** para dejar solo nuestro trabajo.
Si hace falta repetirla, está en el historial de Git: `git checkout c18f0e3 -- drools-prueba`,
o se vuelve a generar con el asistente (cuando salga «Marketplace solutions available», pulsa **Cancel**).

## 7. Logging (SLF4J)

- Sin proveedor de logging aparecen tres líneas `SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder"`.
  No impiden ejecutar, pero ocultan errores de carga de reglas.
- El PDF dice que `slf4j-simple-1.7.26.jar` está en la distribución de Drools. **En la 7.74.1 no está**
  (solo viene `slf4j-api-1.7.30.jar`). Se ha usado el mismo jar 1.7.26 de Maven Central
  (compatible con la API 1.7.30) para la prueba del apartado 6.
- En `sistema-inteligente` se declara la dependencia Maven `org.slf4j:slf4j-simple:1.7.26`.
- Solo hay un proveedor en cada proyecto (no se añade logback ni log4j).

## 8. Proyecto real `sistema-inteligente` (DSI_Mitologia)

- Maven, `drools-bom` 7.74.1.Final (kie-api, drools-core, drools-compiler, drools-mvel), Java 1.8.
- `Principal.java` ya tiene las variables `escenarios` y `soluciones` que exige el enunciado (apartado 6).
- Contiene **solo** una regla de prueba de integración (`reglas/prueba-integracion.drl`) y su test JUnit.
  No hay reglas mitológicas todavía.
- En Eclipse: File → Import → Maven → Existing Maven Projects → `pk_dsint/sistema-inteligente`.

## 9. Pruebas realizadas (8 y 9-nov-2026)

| # | Prueba | Cómo | Resultado |
|---|---|---|---|
| 1 | Java funciona | `java -version` / `$(/usr/libexec/java_home -v 1.8)/bin/java -version` | OK: Temurin 25 por defecto; Zulu 1.8.0_512 arm64 para el proyecto |
| 2 | Maven funciona con Java 8 | `JAVA_HOME=…zulu-8… mvn -v` | OK: Maven 3.9.11, «Java version: 1.8.0_512» |
| 3 | Eclipse reconoce los proyectos | Importar en Eclipse 2020-12 | OK: `drools-prueba` importado y compilado por Eclipse. `sistema-inteligente`: se importa como proyecto Maven |
| 4 | Drools 7.74.1 configurado | Plugin: `p2.director -listInstalledRoots`. Runtime: Settings → Drools | Plugin OK: `org.drools.eclipse.feature` y `.source` 7.46.0.Final. Runtime en Eclipse: OK, «Drools 7.74.1.202307131542» marcado como predeterminado. Distribución 7.74.1 descomprimida y verificada (`unzip -t` sin errores) |
| 5 | `Sample.drl` se carga | Log de Drools al ejecutar | OK: «Found kmodule … drools-prueba», «Start/End creation of KieBase: rules» |
| 6 | `DroolsTest` ejecuta las reglas | `javac`/`java` 1.8 con `binaries/*` (los mismos jars que la «Drools Library» de Eclipse) | OK: imprime `Hello World` y `Goodbye cruel world`. En Eclipse (9-nov): OK, misma salida |
| 7 | Compilación Java 8 | `javap -v` sobre las clases | OK: `major version: 52` (Java 8) |
| 8 | Sin errores que impidan ejecutar | `mvn test exec:java` en `sistema-inteligente` | OK: `Tests run: 1, Failures: 0, Errors: 0`, `BUILD SUCCESS`, «Drools 7.74.1 funcionando» |
| 9 | Git reconoce los archivos | `git status` | OK: sin `target/` ni software |
| 10 | Subido a `pk_dsint` | `git push` + comprobación con `git ls-remote` | OK: ramas en `origin` |

Comando para repetir la prueba 8 desde terminal (en `pk_dsint/sistema-inteligente`):

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 1.8) mvn test exec:java
```

## 10. Decisiones que se apartan del PDF

| Decisión | Motivo |
|---|---|
| Eclipse 2020-12 **x86_64** con Rosetta 2 | No existe Eclipse 2020-12 para Apple Silicon. Se respeta la versión del profesor. |
| Plugin instalado por terminal (p2 director) | Es exactamente la misma instalación (mismos dos componentes, mismo repositorio) sin pasos gráficos. El método gráfico del PDF sigue siendo válido. |
| `slf4j-simple-1.7.26.jar` desde Maven Central | La distribución 7.74.1 no lo trae. |
| `drools-prueba` retirado del repositorio tras la prueba | Solo servía para comprobar la instalación; queda en el historial (commit `c18f0e3`). |
| `sistema-inteligente` con Maven en lugar de la «Drools Library» | Versiones fijadas en `pom.xml` (todas 7.74.1) y se compila igual en Eclipse, en terminal y en el ordenador de cualquier compañero. |
