# Guía de defensa de patrones

Ejecutar scripts/demo.ps1 con la aplicación iniciada, o las pruebas con mvnw.cmd test. Los valores comerciales del ejemplo se pasan como parámetros y no son reglas de la galería.

| Patrón | Clases identificables | Responsabilidad | Evidencia funcional |
|---|---|---|---|
| State | EstadoDeObra, Disponible, EnExhibicion, Reservada, Vendida, EnRestauracion, CicloObra | Encapsular transiciones válidas por estado | Reservar cambia a RESERVADA; vender desde restauración se rechaza |
| Strategy | ComisionStrategy, ComisionPorcentaje, ComisionFija, CalculadoraComision | Seleccionar cálculo por contrato | 10% sobre 1000 da 100; condición fija de 70 da 70 |
| Decorator | CostoVenta, PrecioBase, AdicionalDecorator, Enmarcado, Envio, Seguro | Componer servicios sin subclases por combinación | 1000 + 50 + 30 + 20 produce 1100 y tres servicios |
| Command | ComandoObra, ReservarObra, CancelarReserva, VenderObra, TrasladarObra, CambiarEstado, ContextoOperacion, EjecutorComandos | Encapsular acciones y registrar trazabilidad común | El guion deja traslado, reserva y venta en historial |
| Observer | EventoObra, NotificadorObra, ApplicationEventPublisher de Spring | Reaccionar a eventos sin acoplar cada comando al almacenamiento de avisos | Artista recibe tres avisos; comprador recibe reserva y venta |
| Factory Method | CreadorVenta, CreadorVentaConsignada | Centralizar el proceso de creación y delegar la construcción de Venta al método redefinido crearVenta | Venta resultante apunta a consignación, obra y comprador correctos |

## Recorrido para exponer

1. Mostrar el modelo y la herencia original. Registrar artista con comisión explícita, comprador, obra, ubicación, consignación y certificado.
2. Trasladar y reservar. Mostrar que cada acción tiene un comando y que State controla la transición.
3. Cotizar con los tres decoradores. La cotización no registra historial ni notificaciones.
4. Concretar la venta. OperacionesServicio delega en CreadorVenta; su método preparar llama al Factory Method protegido crearVenta, redefinido por CreadorVentaConsignada. El resultado conserva las relaciones de la consignación.
5. Mostrar el cálculo de comisión mediante la estrategia y la copia de condiciones en Venta. Cambiar una estrategia no requiere modificar los decoradores.
6. Consultar historial y avisos. EjecutorComandos publica un evento; NotificadorObra es un suscriptor mediante @EventListener.
7. Intentar vender de nuevo: 409, sin otra venta ni acciones espurias.

Factory Method no se reduce a un switch ni a una función estática que hace new. Existe una clase creadora abstracta con un proceso común que invoca un método de creación redefinido por la subclase. El prototipo tiene un tipo de venta consignada; no introduce tipos artificiales para demostrar más fábricas.

Observer usa la infraestructura de Spring para publicación/suscripción, con un observador persistente. Es sincrónico por elección transaccional; no promete entrega externa ni ejecución posterior al commit.

## Pruebas que respaldan la explicación

- PatronesTest: transiciones válidas e inválidas; redondeo y comisiones inválidas; decoradores; factory con contrato habitual y particular; comando sobre reserva ajena.
- PersistenciaTest: relaciones JPA y rechazo de una segunda venta por unicidad.
- CircuitoRestTest: APIs del circuito, altas, certificado, historial, notificaciones, reserva ajena, cancelación, errores HTTP, rollback al fallar el observador y dos ventas simultáneas.

Los comandos dejan auditoría persistida; no implementan undo/redo. El historial no guarda objetos ejecutables ni serializaciones de comandos. Las ubicaciones anterior y nueva son IDs históricos, para conservar el movimiento.
