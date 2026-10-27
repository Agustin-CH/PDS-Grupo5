# Distribución aprobada de commits locales

Rama: desarrollo-tpo-final. Lista de 79 archivos revisada y aprobada para cuatro commits locales el 27/10/2026; no se incluyen archivos ignorados ni datos de la instancia MySQL de prueba.

Revisión de credenciales: solo variables de entorno, valores ficticios documentados y clave vacía de H2 exclusivamente en pruebas. No se detectaron contraseñas reales, claves privadas ni tokens en los archivos candidatos.

## 1. Completar dominio y persistencia de El Garaje

Archivos: 26.

- .gitignore
- mvnw.cmd
- pom.xml
- src/main/java/com/elgaraje/inventory/ElGarajeApplication.java
- src/main/java/com/elgaraje/inventory/models/AccionObra.java
- src/main/java/com/elgaraje/inventory/models/Artista.java
- src/main/java/com/elgaraje/inventory/models/CertificadoAutenticidad.java
- src/main/java/com/elgaraje/inventory/models/Comprador.java
- src/main/java/com/elgaraje/inventory/models/Consignacion.java
- src/main/java/com/elgaraje/inventory/models/EstadoObra.java
- src/main/java/com/elgaraje/inventory/models/Notificacion.java
- src/main/java/com/elgaraje/inventory/models/Obra.java
- src/main/java/com/elgaraje/inventory/models/TipoComision.java
- src/main/java/com/elgaraje/inventory/models/Ubicacion.java
- src/main/java/com/elgaraje/inventory/models/Usuario.java
- src/main/java/com/elgaraje/inventory/models/Venta.java
- src/main/java/com/elgaraje/inventory/repositories/AccionObraRepository.java
- src/main/java/com/elgaraje/inventory/repositories/ArtistaRepository.java
- src/main/java/com/elgaraje/inventory/repositories/CertificadoAutenticidadRepository.java
- src/main/java/com/elgaraje/inventory/repositories/CompradorRepository.java
- src/main/java/com/elgaraje/inventory/repositories/ConsignacionRepository.java
- src/main/java/com/elgaraje/inventory/repositories/NotificacionRepository.java
- src/main/java/com/elgaraje/inventory/repositories/ObraRepository.java
- src/main/java/com/elgaraje/inventory/repositories/UbicacionRepository.java
- src/main/java/com/elgaraje/inventory/repositories/UsuarioRepository.java
- src/main/java/com/elgaraje/inventory/repositories/VentaRepository.java

## 2. Integrar patrones de diseño y circuitos REST

Archivos: 40.

- src/main/java/com/elgaraje/inventory/config/Configuracion.java
- src/main/java/com/elgaraje/inventory/controllers/CatalogoController.java
- src/main/java/com/elgaraje/inventory/controllers/OperacionesController.java
- src/main/java/com/elgaraje/inventory/dtos/Respuestas.java
- src/main/java/com/elgaraje/inventory/dtos/Solicitudes.java
- src/main/java/com/elgaraje/inventory/errors/ManejadorErrores.java
- src/main/java/com/elgaraje/inventory/errors/NegocioException.java
- src/main/java/com/elgaraje/inventory/errors/NoEncontradoException.java
- src/main/java/com/elgaraje/inventory/patterns/command/CambiarEstado.java
- src/main/java/com/elgaraje/inventory/patterns/command/CancelarReserva.java
- src/main/java/com/elgaraje/inventory/patterns/command/ComandoObra.java
- src/main/java/com/elgaraje/inventory/patterns/command/ContextoOperacion.java
- src/main/java/com/elgaraje/inventory/patterns/command/EjecutorComandos.java
- src/main/java/com/elgaraje/inventory/patterns/command/ReservarObra.java
- src/main/java/com/elgaraje/inventory/patterns/command/TrasladarObra.java
- src/main/java/com/elgaraje/inventory/patterns/command/VenderObra.java
- src/main/java/com/elgaraje/inventory/patterns/decorator/AdicionalDecorator.java
- src/main/java/com/elgaraje/inventory/patterns/decorator/CostoVenta.java
- src/main/java/com/elgaraje/inventory/patterns/decorator/Enmarcado.java
- src/main/java/com/elgaraje/inventory/patterns/decorator/Envio.java
- src/main/java/com/elgaraje/inventory/patterns/decorator/PrecioBase.java
- src/main/java/com/elgaraje/inventory/patterns/decorator/Seguro.java
- src/main/java/com/elgaraje/inventory/patterns/factory/CreadorVenta.java
- src/main/java/com/elgaraje/inventory/patterns/factory/CreadorVentaConsignada.java
- src/main/java/com/elgaraje/inventory/patterns/observer/EventoObra.java
- src/main/java/com/elgaraje/inventory/patterns/observer/NotificadorObra.java
- src/main/java/com/elgaraje/inventory/patterns/state/CicloObra.java
- src/main/java/com/elgaraje/inventory/patterns/state/Disponible.java
- src/main/java/com/elgaraje/inventory/patterns/state/EnExhibicion.java
- src/main/java/com/elgaraje/inventory/patterns/state/EnRestauracion.java
- src/main/java/com/elgaraje/inventory/patterns/state/EstadoDeObra.java
- src/main/java/com/elgaraje/inventory/patterns/state/Reservada.java
- src/main/java/com/elgaraje/inventory/patterns/state/Vendida.java
- src/main/java/com/elgaraje/inventory/patterns/strategy/CalculadoraComision.java
- src/main/java/com/elgaraje/inventory/patterns/strategy/ComisionFija.java
- src/main/java/com/elgaraje/inventory/patterns/strategy/ComisionPorcentaje.java
- src/main/java/com/elgaraje/inventory/patterns/strategy/ComisionStrategy.java
- src/main/java/com/elgaraje/inventory/services/CatalogoServicio.java
- src/main/java/com/elgaraje/inventory/services/OperacionesServicio.java
- src/main/resources/application.properties

## 3. Verificar reglas de negocio y circuitos del TPO

Archivos: 4.

- src/test/java/com/elgaraje/inventory/CircuitoRestTest.java
- src/test/java/com/elgaraje/inventory/PatronesTest.java
- src/test/java/com/elgaraje/inventory/PersistenciaTest.java
- src/test/resources/application-test.properties

## 4. Documentar el TPO y su verificación con MySQL

Archivos: 9.

- docs/backend.md
- docs/commits-propuestos.md
- docs/defensa-patrones.md
- docs/el-garaje.puml
- docs/el-garaje.svg
- docs/verificacion.md
- README.md
- scripts/demo.ps1
- scripts/generar-uml.ps1

## Exclusiones verificadas

- target/: binarios portables, datos MySQL, credenciales efímeras, respuestas de demo, logs, JAR y herramientas.
- .vscode/: configuración local ajena a la entrega, preservada.
- .env, .env.* y *.local.properties: configuración y secretos locales.
- *.log y caché del wrapper: artefactos locales regenerables.

Total: 79 archivos. No usar git add . ni incluir archivos fuera de esta lista.
