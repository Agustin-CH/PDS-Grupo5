# Verificación final del TPO El Garaje

Fecha: 27/10/2026. Rama: desarrollo-tpo-final.

## Resultado de la revisión final

- Maven: package terminó con BUILD SUCCESS; 22 pruebas, 0 fallos, 0 errores, 0 omitidas.
- CircuitoRestTest: 9 casos. PatronesTest: 12 casos. PersistenciaTest: 1 caso.
- Se ejecutó el JAR real con MySQL Community Server 8.4.0 y conexión TLS.
- scripts/demo.ps1 completó sus 19 comprobaciones HTTP: 8 respuestas 201, 10 respuestas 200 y 1 respuesta 409 esperada.
- Se consultaron las filas con el cliente mysql, además de las lecturas REST.
- Se reinició la aplicación usando ddl-auto=validate y se comprobó que la información persistía.
- Dos ventas HTTP concurrentes contra MySQL devolvieron 201 y 409, con una sola venta y una sola acción.
- El UML de 82 tipos fue regenerado, validado con PlantUML y renderizado a SVG.
- Se autorizó crear cuatro commits locales en desarrollo-tpo-final con los mensajes y archivos de [commits-propuestos.md](commits-propuestos.md). Su registro se comprueba con git log -4 --oneline y el estado final con git status. No se autorizó push, Pull Requests, cambios de rama ni modificaciones de main.

El último registro regenerable de compilación y pruebas está en target/revision-final.log. Los reportes de las suites están en target/surefire-reports. El JAR ejecutable está en target/inventory-0.0.1-SNAPSHOT.jar. Estos artefactos no se incluyen en los commits.

## Reproducibilidad y exclusión de temporales

Toda la carpeta target/ queda excluida, incluidos los scripts de target/revision-tools/. Esos auxiliares sirvieron únicamente para preparar y comprobar una instancia portable en el equipo de revisión; no son dependencias de la aplicación, de Maven ni de los scripts compartidos.

Para reproducir las pruebas automatizadas, ejecutar mvnw.cmd -B test. Para repetir el circuito MySQL, preparar un esquema nuevo siguiendo README.md, iniciar el backend y ejecutar scripts/demo.ps1 con su BaseUrl. El script crea datos ficticios con IDs nuevos, comprueba 19 respuestas HTTP y devuelve los identificadores necesarios para consultar las filas o releer la venta tras reiniciar con validate. No requiere archivos de target/revision-tools/.

Los únicos scripts compartidos son scripts/demo.ps1 y scripts/generar-uml.ps1. No contienen rutas a la instalación portable, claves efímeras ni dependencia de configuraciones locales. No fue necesario trasladar auxiliares temporales. Se revisaron los 79 archivos aprobados y sus exclusiones antes de preparar los commits.

## MySQL: disponibilidad y aislamiento

La consulta de comandos, programas instalados, servicios Windows y puertos no encontró una instalación MySQL/MariaDB ni un servidor en 3306. Se dejó intacto el proceso que ya ocupaba 8080.

Para verificar la aplicación se descargó la distribución portable oficial de MySQL Community 8.4.0 desde Oracle, dentro de target/revision-tools. No se instalaron servicios ni se modificaron bases o instalaciones existentes.

Se creó una instancia con directorio de datos exclusivo, escucha en 127.0.0.1:33227 y un esquema nuevo llamado elgaraje_revision_f77ab77f. El backend escuchó en 127.0.0.1:18085 con una cuenta de aplicación limitada al esquema de prueba. Las claves fueron aleatorias y efímeras; no se imprimieron ni se incorporaron a código o documentación.

La creación de tablas se habilitó con update solamente para el primer arranque de ese esquema nuevo y vacío. Los siguientes arranques usaron validate. La conexión SQL confirmó TLS_AES_128_GCM_SHA256. El primer arranque de la aplicación terminó correctamente en aproximadamente 7 segundos.

## Evidencia funcional y SQL

La primera ejecución encontró un defecto en el script al enumerar arrays JSON con PowerShell 5; la consulta independiente confirmó que el backend ya había registrado correctamente las acciones. Se corrigió el script y se repitió sin borrar los datos anteriores.

En la ejecución completa verificada, obra 2, consignación 2 y venta 2:

| Dato comprobado por REST y SQL | Resultado |
|---|---|
| Total de venta | 1100.00 |
| Comisión de galería | 100.00 |
| Liquidación del artista | 900.00 |
| Estado de obra | VENDIDA |
| Comprador | ID 4 |
| Historial | TRASLADO, RESERVA, VENTA |
| Notificaciones al artista ID 3 | 3 |
| Notificaciones al comprador ID 4 | 2 |
| Certificado de la obra | 1 |

La segunda venta respondió 409 y el historial se mantuvo en tres acciones. Los registros de respuestas y la salida SQL quedaron únicamente en target/revision-tools/demo-mysql.json y evidencia-sql.txt.

Después de reiniciar con validate, se leyeron nuevamente venta, obra, historial y avisos. La prueba concurrente utilizó una obra nueva (ID 3) y registró una sola acción de venta; su evidencia local es target/revision-tools/reinicio-mysql.json. Los valores monetarios y usuarios son exclusivamente datos de demostración.

## Revisión de patrones, UML y alcance

| Patrón | Conclusión de la revisión |
|---|---|
| State | Clases concretas por estado; CicloObra controla transiciones y permite validar sin mutar para cotizar. Se rechazaron estados nulos y transiciones inválidas. |
| Strategy | Cálculo por porcentaje o importe fijo; validación común para el contrato habitual y condiciones de consignación. Se verificaron redondeo y límites monetarios. |
| Decorator | PrecioBase implementa CostoVenta; Enmarcado, Envio y Seguro extienden AdicionalDecorator y componen costo y servicios en ejecución. |
| Command | Operaciones encapsuladas en ComandoObra; EjecutorComandos registra historial y publica eventos dentro de la transacción. La validación de venta se reutiliza en cotización y ejecución. |
| Observer | Publicación de EventoObra con Spring y suscripción de NotificadorObra; avisos persistidos sin mensajes externos. El fallo del observador revierte la operación. |
| Factory Method | CreadorVenta.preparar invoca crearVenta, redefinido por CreadorVentaConsignada; el producto Venta conserva obra, consignación y comprador. No se aplica a notificaciones. |

Se revisaron las clases y funcionalidades de obras, artistas, compradores, consignaciones, ventas, estados, comisiones, adicionales, historial y notificaciones. También se verificaron ubicación y certificado, previstos por el modelo de la Actividad 2. La guía de defensa identifica clases, responsabilidades y ejemplos.

El UML se regeneró con scripts/generar-uml.ps1. Se corrigieron las multiplicidades opcionales de artista y comprador para coincidir con el modelo compatible con registros antiguos, y se agregó la dependencia de CatalogoServicio hacia CalculadoraComision. No se agregaron tipos ficticios al diagrama.

## Correcciones realizadas en esta revisión

- Se centralizó la validación de condiciones de comisión, eliminando la copia en CatalogoServicio.
- Cotización y venta reutilizan la validación del comando y de State, sin duplicar listas de estados ni controles de reserva ajena.
- Estados nulos, estrategias ausentes y datos incompletos de destinatarios producen excepciones controladas.
- Los errores inesperados se convierten en ProblemDetail 500 genérico con referencia de diagnóstico. No se exponen trazas ni mensajes internos. Se preservan los estados y encabezados del framework, incluido Allow para 405.
- DB_USER y DB_PASSWORD son obligatorios; se retiraron root y clave vacía como valores predeterminados.
- Se deshabilitaron la creación automática de bases y la modificación implícita del esquema: validate es ahora el valor predeterminado.
- La escucha HTTP predeterminada es 127.0.0.1 y se desactiva la exposición de detalles de error.
- demo.ps1 usa fecha UTC, decodifica respuestas como UTF-8, enumera arrays de forma compatible con PowerShell 5 y comprueba estados HTTP, cálculos, relaciones y persistencia por nuevas lecturas.
- .gitignore excluye .vscode, .env, propiedades locales y los artefactos de target.
- Se actualizaron README y documentación de arranque para reflejar la inicialización explícita de un esquema nuevo.

Se repitieron las pruebas afectadas y el empaquetado. Las excepciones simuladas y la violación de unicidad en los logs son esperadas por pruebas negativas, no fallos de la suite.

## Incidencias del entorno de revisión

La extracción portable funcionó sin instalar software en el sistema. Las primeras comprobaciones requirieron corregir el argumento de host para PowerShell y usar TLS para la autenticación caching_sha2_password de MySQL. Los intentos fallidos se limitaron a la instancia nueva de prueba.

La protección DPAPI de PowerShell no estaba disponible en el contexto del proceso de revisión. Las credenciales efímeras se mantuvieron en configuración local bajo target, excluida de Git, y se retiraron al cerrar la prueba. No forman parte de la lista de archivos propuestos para commits.

## Límites pendientes

Se compiló con JDK 21 y --release 17; no se ejecutó sobre un runtime JDK 17. H2 permanece limitado a pruebas y fuera del JAR. La prueba MySQL valida una instalación nueva; no acredita una migración de bases antiguas.

El prototipo sigue teniendo el alcance académico documentado: sin autenticación, pagos, firmas digitales ni alertas programadas. No se presenta como un servicio público de producción.

## Historial de la primera implementación

Lo que sigue documenta la verificación inicial y sus límites en ese momento. La revisión final anterior incorpora las pruebas posteriores con MySQL y sustituye los puntos que originalmente estaban pendientes.


Fecha: 27/10/2026. Rama local: desarrollo-tpo-final.

## Resultado inicial

Comando ejecutado: mvnw.cmd -B clean package.

BUILD SUCCESS. 20 pruebas ejecutadas, 0 fallos, 0 errores, 0 omitidas:

| Suite | Casos | Cobertura |
|---|---:|---|
| PatronesTest | 11 | Transiciones, redondeo, estrategias, decoradores, Factory Method y comandos |
| PersistenciaTest | 1 | Mapeos JPA y unicidad de venta |
| CircuitoRestTest | 8 | REST, alta de catálogos, circuito completo, certificado, historial, notificaciones, reserva ajena, cancelación, validación, rollback y concurrencia |

El registro local completo está en verificacion-final.log (ignorado por Git). Los reportes regenerables están en target/surefire-reports.

Se generó target/inventory-0.0.1-SNAPSHOT.jar. La inspección del JAR confirmó mysql-connector-j y ausencia de H2. javap informó major version 61, correspondiente al objetivo Java 17.

## Etapas completadas

| Etapa | Archivos agregados o modificados | Verificación realizada |
|---|---|---|
| Dominio y persistencia | models/Usuario.java, Artista.java, Comprador.java, Obra.java, EstadoObra.java; nuevos TipoComision, Ubicacion, CertificadoAutenticidad, Consignacion, Venta, AccionObra y Notificacion; repositories/ObraRepository.java y seis repositorios nuevos; pom.xml; PersistenciaTest y application-test.properties | Compilación y primera prueba H2 correctas: 1 prueba |
| Patrones y servicios | patterns/state, strategy, decorator, command, observer, factory; services/CatalogoServicio.java y OperacionesServicio.java; dtos/Solicitudes.java y Respuestas.java; config/Configuracion.java; excepciones de negocio; PatronesTest | Compilación y pruebas correctas: 12 pruebas acumuladas |
| REST e integración | controllers/CatalogoController.java y OperacionesController.java; errors/ManejadorErrores.java; application.properties; CircuitoRestTest | Primer circuito completo correcto: 18 pruebas acumuladas |
| Documentación y cierre | README.md; docs/backend.md, defensa-patrones.md, verificacion.md, el-garaje.puml y el-garaje.svg; scripts/demo.ps1 y generar-uml.ps1; mvnw.cmd; ampliación de CircuitoRestTest | Compilación limpia, 20 pruebas, empaquetado, validación/render del UML y revisión del wrapper |

Los archivos Java originales restantes recibieron solamente formato uniforme. El formato se verificó con Google Java Format; git diff --check no reportó errores.

El UML contiene los 82 tipos de producción, incluidos records anidados. Se generó desde las fuentes y sus relaciones explícitas, pasó el checkonly de PlantUML y se renderizó a SVG con el motor Java Smetana. No fue necesario enviar código a un servidor externo.

## Problemas de entorno y resolución

- El primer intento de Maven dentro del sandbox falló con AccessDeniedException sobre C:\.m2. Se volvió a ejecutar con el permiso de ejecución fuera del sandbox. Las compilaciones posteriores terminaron correctamente.
- No hay Maven global en PATH. Se utilizó el wrapper incluido, que descarga Maven 3.9.6 y dependencias cuando hace falta.
- Solo se encontró JDK 21 en la instalación disponible. El compilador se ejecutó con release 17 y se comprobó la versión de bytecode. No se probó un runtime JDK 17.
- El wrapper original exigía JAVA_HOME pese al mensaje del lanzador. Ahora usa Java desde PATH si no está definido. Se verificó mvnw.cmd -v sin JAVA_HOME.
- La política local de PowerShell impide ejecutar archivos ps1 directamente. Los scripts se invocaron mediante powershell -NoProfile -ExecutionPolicy Bypass -File, sin modificar la política persistente del equipo. Se verificó sintaxis de demo.ps1; el generador UML se ejecutó correctamente.
- README.md estaba codificado en UTF-16. Se convirtió a UTF-8 conservando el contenido útil y actualizando las instrucciones.
- JDK 21 emite un aviso sobre el descubrimiento automático del procesador Lombok. No impidió compilar. Las pruebas negativas de unicidad generan mensajes SQL ERROR esperados; las suites verifican la excepción y terminan sin errores.

Las herramientas temporales de formato y PlantUML se descargaron dentro de target y fueron eliminadas por la compilación limpia. No se agregaron a las dependencias de producción.

## Límites de lo verificado

Las pruebas de integración usaron Spring Boot, MockMvc y H2 en memoria, incluidos el bloqueo transaccional y la venta concurrente. No se ejecutó la aplicación contra MySQL, ni se verificó una migración sobre datos existentes. Las diferencias de bloqueo o DDL de MySQL requieren una comprobación en el entorno de entrega.

El guion demo.ps1 tiene sintaxis verificada y reproduce el circuito cubierto con MockMvc; no se ejecutó contra un servidor MySQL en vivo. Para demostrarlo, configurar credenciales, iniciar la aplicación y ejecutarlo sobre una base de demostración.

No se implementaron servicios externos, pagos, firmas digitales, autenticación ni alertas programadas. Estos límites están descritos en backend.md y no se presentan como funcionalidades terminadas.

## Control de versiones

Se trabajó en desarrollo-tpo-final. No se cambió de rama, no se modificó main, no se hicieron commits, pushes ni Pull Requests. Los cambios quedan en el árbol de trabajo para revisión. Se preservó la carpeta .vscode observada durante el trabajo, sin modificarla ni incluirla en operaciones de versionado.
