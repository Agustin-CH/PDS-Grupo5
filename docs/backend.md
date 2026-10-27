# Backend El Garaje

## Modelo

Usuario conserva la herencia JOINED de Artista y Comprador. Obra mantiene artista y comprador, y agrega precio BigDecimal, fotos, ubicación actual, comprador de reserva y @Version.

Consignacion enlaza obra y artista, fecha inicial, vencimiento opcional y condiciones particulares opcionales de comisión. El prototipo admite una consignación por obra, sin renovaciones. Venta enlaza consignación, obra y comprador; conserva precio, adicionales, total, comisión, liquidación y copia de las condiciones aplicadas.

Ubicacion identifica cada lugar. AccionObra conserva ubicación anterior/nueva y estados anterior/nuevo. CertificadoAutenticidad contiene número único, obra, fecha, emisor y firma textual; no valida una firma digital. La relación con certificado se obtiene desde la obra y no se duplica en consignación.

Notificacion guarda destinatario, obra, evento, mensaje y fecha. No se envían mensajes externos. La liquidación se calcula y persiste; no representa una transferencia bancaria.

## Condiciones de demostración

No hay porcentajes, importes ni plazos comerciales precargados. El alta del artista exige indicar tipo y valor de comisión. Se admiten PORCENTAJE e IMPORTE_FIJO. La consignación puede sobrescribir ambos valores juntos o heredar los del artista.

Convención del prototipo: comisión sobre el precio de la obra; adicionales cobrados por separado; liquidación del artista igual al precio menos comisión. BigDecimal evita errores binarios y HALF_UP redondea la comisión porcentual a dos decimales. Una comisión no puede superar el precio. Se rechazan importes negativos y más de dos decimales en la API.

El cliente informa explícitamente el importe de cada adicional. Se permite una instancia de cada servicio por venta. Los importes del guion de demostración son ejemplos reemplazables.

El prototipo usa fecha UTC para evaluar vigencia. La consignación debe haber comenzado y, si tiene vencimiento, ese día todavía permite vender. No hay duración predeterminada ni alertas de vencimiento programadas.

## State y reservas

| Origen | Destinos |
|---|---|
| DISPONIBLE | EN_EXHIBICION, EN_RESTAURACION, RESERVADA, VENDIDA |
| EN_EXHIBICION | DISPONIBLE, EN_RESTAURACION, RESERVADA, VENDIDA |
| RESERVADA | DISPONIBLE al cancelar, VENDIDA para su comprador |
| EN_RESTAURACION | DISPONIBLE |
| VENDIDA | Ninguno |

La API de cambio de estado no permite saltarse reserva, cancelación o venta. Las reservas no vencen automáticamente y al cancelarlas la obra queda disponible. Se permiten traslados antes de la venta; cada movimiento queda registrado.

## Transacciones y concurrencia

Cada escritura de una obra obtiene un bloqueo PESSIMISTIC_WRITE dentro de la transacción del servicio. @Version detecta escrituras obsoletas y las restricciones únicas de Venta impiden duplicaciones por obra y consignación.

EjecutorComandos ejecuta el comando, registra historial y publica EventoObra. Spring entrega el evento al observador sincrónico NotificadorObra. Las notificaciones participan en la misma transacción: si el observador o la persistencia fallan, se revierten estado, historial, venta y notificaciones.

Cotizar reutiliza el cálculo de venta sin persistir ni cambiar estados; la venta vuelve a validar todo bajo bloqueo. No hay cola ni entrega externa. Las pruebas de concurrencia usan dos transacciones independientes contra H2.

## REST

Base: http://localhost:8080/api. POST de altas y venta devuelve 201; consultas y operaciones restantes, 200. Errores ProblemDetail: 400 formato/validación, 404 recurso ausente, 409 regla de negocio o conflicto de persistencia.

Los errores inesperados devuelven 500 con una referencia de diagnóstico y un mensaje genérico, sin trazas ni detalles internos en HTTP. Se conservan los estados propios del framework, por ejemplo 404 para rutas ausentes y 405 para métodos no permitidos. Los detalles quedan en logs locales.

El arranque requiere DB_USER y DB_PASSWORD explícitos, usa ddl-auto=validate y escucha solo en 127.0.0.1. La inicialización con update se reserva para un esquema nuevo de demostración, elegido expresamente. No hay creación automática de bases por JDBC.

| Método | Ruta | Entrada / propósito |
|---|---|---|
| POST / GET | /artistas | dni, nombre, apellido, biografia opcional, tipoComision, valorComision |
| POST / GET | /compradores | dni, nombre, apellido |
| POST / GET | /ubicaciones | nombre, direccion opcional |
| POST / GET | /obras | titulo, artistaId, ubicacionId, precio; anio, dimensiones, descripcion y fotos opcionales |
| GET | /obras/{id} | Obra y ubicación actual |
| POST / GET | /consignaciones | obraId, fecha; vencimiento y condiciones particulares opcionales |
| POST | /certificados | obraId, numero, fechaEmision, emisor, firma |
| GET | /obras/{id}/certificado | Certificado |
| POST | /obras/{id}/reservas | compradorId |
| DELETE | /obras/{id}/reserva | Cancelación |
| POST | /obras/{id}/traslados | ubicacionId |
| PATCH | /obras/{id}/estado | estado |
| POST | /obras/{id}/cotizacion | compradorId, adicionales opcionales |
| POST | /obras/{id}/ventas | compradorId, adicionales opcionales |
| GET | /ventas y /ventas/{id} | Venta y liquidación |
| GET | /obras/{id}/historial | Acciones en orden cronológico |
| GET | /usuarios/{id}/notificaciones | Avisos del destinatario |

Ejemplo de cotización o venta (reemplazar compradorId por un ID obtenido del alta):

```json
{
  "compradorId": 2,
  "adicionales": [
    {"tipo": "ENMARCADO", "importe": 50},
    {"tipo": "ENVIO", "importe": 30},
    {"tipo": "SEGURO", "importe": 20}
  ]
}
```

El script scripts/demo.ps1 captura los IDs reales y realiza el circuito completo. Los DTOs devuelven IDs de relaciones para evitar grafos recursivos y exposición de entidades JPA.

## Alcance académico

No se implementaron autenticación, pagos, edición/borrado de catálogos, renovación de consignaciones, firmas digitales ni integraciones de correo. Los registros antiguos incompletos se conservan y las operaciones que requieran datos faltantes se rechazan.

Se aplican Controller e Information Expert de GRASP: controladores REST delegan, servicios coordinan y especialistas de estado/costo/comisión resuelven sus reglas. Interfaces y clases concretas separan responsabilidades y permiten extender estrategias o decoradores sin reescribir la venta.

Referencia: [Actividad 2 del grupo](https://docs.google.com/document/d/1qUEkwLAhKpSM8fGV7y9KzStly4dX3irC/edit). Factory Method está aplicado a ventas consignadas, no a notificaciones.
