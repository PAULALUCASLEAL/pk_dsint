---
name: drools-7-expert
description: Escribe, revisa, depura y prueba reglas DRL para Drools 7.74.1.Final (KieServices, KieContainer, KieSession, insert/modify/update/delete, insertLogical, agenda-group, salience, no-loop, lock-on-active, globals, queries). Usar al crear o tocar ficheros .drl, kmodule.xml o codigo que use la API KIE en sistema-inteligente, y al disenar pruebas de reglas. No usar APIs de Drools 8 o posteriores.
---

# drools-7-expert (skill propia del proyecto DSINT)

> Skill creada por el equipo. **No es un recurso oficial de Red Hat/KIE.** Resume la documentacion
> oficial de Drools 7.74.1.Final y las notas del enunciado de DSINT.

## Cuando activarla

- Crear o editar `src/main/resources/reglas/*.drl` o `META-INF/kmodule.xml`.
- Escribir codigo Java que cree sesiones o inserte hechos.
- Depurar reglas que no se disparan, se disparan en bucle o en mal orden.

## Restricciones de version

- Version fija: **7.74.1.Final** (pom con `drools-bom`). El profesor desaconseja < 7.43 y >= 8.
- No usar: `drools-ruleunits`, Rule Units de Drools 8, `org.drools.model.*` como API principal,
  DRL `unit`, ni la sintaxis OOPath obligatoria. Java 8.

## Conceptos clave

- **Memoria de trabajo (WM):** hechos insertados. **Agenda:** activaciones pendientes.
  **RETE/Phreak:** red que empareja patrones de forma incremental.
- Encadenamiento hacia delante: `fireAllRules()` dispara hasta que la agenda queda vacia.
- `insert(o)` añade hecho. `modify($h){ setX(..) }` cambia y notifica (preferido).
  `update($h)` notifica tras cambiarlo con setters. `delete($h)` retira.
  **Cambiar un hecho con setters sin `modify`/`update` deja la red desincronizada.**
- `insertLogical(o)`: mantenimiento de la verdad; el hecho desaparece solo cuando deja de cumplirse
  el antecedente que lo justifico. Ideal para capacidades derivadas de objetos.
- `==` entre objetos en DRL usa `equals`.
- Resolucion de conflictos: `salience`, luego reciente/orden. `agenda-group` + `setFocus` para fases
  (el enunciado pide una agenda «Informacion Inicial»). `no-loop` evita que la propia regla se reactive;
  `lock-on-active` evita reactivaciones por cualquier causa dentro del grupo activo.

## Estilo exigido por el enunciado (notas H-O)

- Consecuente = solo `insert` / `modify` / `delete` (+ registrar evento). **Sin bucles ni algoritmos.**
- Antecedente = patrones simples sobre los conceptos, sin navegar referencias encadenadas.
- Reglas genericas por capacidad (`Vuelo`), no por objeto concreto (`SandaliasAladas`), y sin nombres de
  personajes cuando la regla puede generalizarse.
- Java solo carga datos; la inferencia esta en las reglas.

## Plantilla

```
rule "Un dios entrega un objeto a quien tiene su favor"
    agenda-group "historia"
    no-loop
when
    $h   : Personaje(heroe, estadoVital == EstadoVital.VIVO)
    $d   : Personaje(dios)
           Favor(personaje == $h, dios == $d)
    $pos : Posesion(poseedor == $d, $o : objeto)
then
    modify($pos) { setPoseedor($h) }
    registro.registrar(EventoMitologico.nuevo(TipoAccion.OBTENER_OBJETO, $h)
        .objeto($o).justificacion("tener el favor de " + $d.getNombre())
        .regla(drools.getRule().getName()));
end
```

## Depuracion

- Mensajes de compilacion: `KieBuilder.getResults().getMessages()`.
- `AgendaEventListener` / `RuleRuntimeEventListener` (o `DebugAgendaEventListener`) para ver activaciones.
- Bucle infinito → revisar si el RHS modifica algo de su LHS; añadir `no-loop` o una condicion de guarda.
- Una regla no se dispara → ¿se inserto el hecho? ¿se uso `modify`? ¿esta en el agenda-group con foco?

## Pruebas

- JUnit 4: crear una KieBase pequena con `KieHelper` o el `KieClasspathContainer`, insertar hechos,
  `fireAllRules()`, comprobar hechos/eventos resultantes; `dispose()` en `finally`.
- Una sesion por escenario: nunca reutilizar sesiones ni guardar estado en `static`.

## Checklist

- [ ] Compila sin errores en `KieBuilder`.
- [ ] Ningun RHS con bucles.
- [ ] `modify`/`update` en cada cambio de hecho.
- [ ] Sin bucles infinitos (probado).
- [ ] Reglas generales, no especificas de Perseo, cuando sea posible.

## Referencias

- Documentacion oficial 7.74.1.Final: https://docs.drools.org/7.74.1.Final/drools-docs/html_single/index.html
- Descargas 7.74.1.Final: https://download.jboss.org/drools/release/7.74.1.Final/
- no-loop vs lock-on-active (citado en el enunciado): https://stackoverflow.com/questions/17042437/what-is-the-difference-between-no-loop-and-lock-on-active-in-drools
