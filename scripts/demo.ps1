param([string]$BaseUrl = "http://localhost:8080/api")
$ErrorActionPreference = "Stop"
$peticiones = [Collections.Generic.List[object]]::new()

function Solicitar([string]$Metodo, [string]$Ruta, $Datos, [int]$Esperado = 200) {
    $parametros = @{Uri="$BaseUrl$Ruta"; Method=$Metodo; UseBasicParsing=$true}
    if ($null -ne $Datos) {
        $parametros.ContentType = "application/json; charset=utf-8"
        $parametros.Body = [Text.Encoding]::UTF8.GetBytes(($Datos | ConvertTo-Json -Depth 8))
    }
    $respuesta = Invoke-WebRequest @parametros
    if ([int]$respuesta.StatusCode -ne $Esperado) { throw "HTTP inesperado: $Metodo $Ruta" }
    $peticiones.Add([pscustomobject]@{metodo=$Metodo; ruta=$Ruta; estado=[int]$respuesta.StatusCode})
    $cuerpoRespuesta = [Text.Encoding]::UTF8.GetString($respuesta.RawContentStream.ToArray()) | ConvertFrom-Json
    foreach ($elemento in $cuerpoRespuesta) { Write-Output $elemento }
}

function Enviar([string]$Ruta, $Datos, [int]$Esperado = 201) {
    Solicitar "Post" $Ruta $Datos $Esperado
}

function Afirmar([bool]$Condicion, [string]$Mensaje) {
    if (-not $Condicion) { throw "Verificacion fallida: $Mensaje" }
}

# Valores exclusivamente de ejemplo; no son condiciones comerciales reales.
$sufijo = [Guid]::NewGuid().ToString("N")
$artista = Enviar "/artistas" @{dni="A-$sufijo"; nombre="Ana"; apellido="Luz"; tipoComision="PORCENTAJE"; valorComision=10}
$comprador = Enviar "/compradores" @{dni="C-$sufijo"; nombre="Juan"; apellido="Sol"}
$origen = Enviar "/ubicaciones" @{nombre="Deposito de ejemplo"}
$destino = Enviar "/ubicaciones" @{nombre="Sala de ejemplo"}
$obra = Enviar "/obras" @{titulo="Luz - $sufijo"; anio=2026; artistaId=$artista.id; ubicacionId=$origen.id; precio=1000; fotos=@()}
$hoy = [DateTime]::UtcNow.ToString("yyyy-MM-dd")
$consignacion = Enviar "/consignaciones" @{obraId=$obra.id; fecha=$hoy}
$certificado = Enviar "/certificados" @{obraId=$obra.id; numero="CERT-$sufijo"; fechaEmision=$hoy; emisor="Ana Luz"; firma="Firma textual de demostracion"}

# Command registra traslado y reserva; State controla la transicion.
$traslado = Enviar "/obras/$($obra.id)/traslados" @{ubicacionId=$destino.id} 200
$reserva = Enviar "/obras/$($obra.id)/reservas" @{compradorId=$comprador.id} 200
Afirmar ($traslado.ubicacionId -eq $destino.id) "ubicacion de destino"
Afirmar ($reserva.estado -eq "RESERVADA") "estado de reserva"
$pedido = @{compradorId=$comprador.id; adicionales=@(
    @{tipo="ENMARCADO"; importe=50},
    @{tipo="ENVIO"; importe=30},
    @{tipo="SEGURO"; importe=20}
)}
# Decorator compone el total y Strategy calcula la comision.
$cotizacion = Enviar "/obras/$($obra.id)/cotizacion" $pedido 200
# Factory Method crea la venta consignada; Observer guarda notificaciones.
$venta = Enviar "/obras/$($obra.id)/ventas" $pedido
$persistida = Solicitar "Get" "/ventas/$($venta.id)" $null
$obraFinal = Solicitar "Get" "/obras/$($obra.id)" $null
$certificadoFinal = Solicitar "Get" "/obras/$($obra.id)/certificado" $null
$historial = @(Solicitar "Get" "/obras/$($obra.id)/historial" $null)
$avisos = @(Solicitar "Get" "/usuarios/$($comprador.id)/notificaciones" $null)
$avisosArtista = @(Solicitar "Get" "/usuarios/$($artista.id)/notificaciones" $null)
Afirmar ($cotizacion.monto -eq 1100 -and $persistida.monto -eq 1100) "total"
Afirmar ($persistida.comisionGaleria -eq 100 -and $persistida.liquidacionArtista -eq 900) "liquidacion"
Afirmar ($persistida.consignacionId -eq $consignacion.id -and $persistida.servicios.Count -eq 3) "venta consignada y adicionales"
Afirmar ($obraFinal.estado -eq "VENDIDA" -and $obraFinal.compradorId -eq $comprador.id -and $null -eq $obraFinal.reservadoPorId) "obra vendida"
Afirmar ($certificadoFinal.id -eq $certificado.id) "certificado persistido"
Afirmar ($historial.Count -eq 3 -and ($historial.tipo -join ",") -eq "TRASLADO,RESERVA,VENTA") "historial"
Afirmar ($avisos.Count -eq 2 -and $avisosArtista.Count -eq 3) "notificaciones"

# La segunda venta debe responder 409 y no agregar historial.
try {
    $null = Invoke-WebRequest -UseBasicParsing -Uri "$BaseUrl/obras/$($obra.id)/ventas" -Method Post -ContentType "application/json" -Body ($pedido | ConvertTo-Json -Depth 8)
    throw "La segunda venta fue aceptada"
} catch {
    if ($null -eq $_.Exception.Response -or [int]$_.Exception.Response.StatusCode -ne 409) { throw }
    $peticiones.Add([pscustomobject]@{metodo="Post"; ruta="/obras/$($obra.id)/ventas"; estado=409})
}
$historialFinal = @(Solicitar "Get" "/obras/$($obra.id)/historial" $null)
Afirmar ($historialFinal.Count -eq 3) "sin efectos de venta duplicada"

[pscustomobject]@{
    verificado=$true; obraId=$obra.id; artistaId=$artista.id; compradorId=$comprador.id
    consignacionId=$consignacion.id; certificadoId=$certificado.id
    cotizacion=$cotizacion; venta=$persistida; historial=$historialFinal
    notificaciones=$avisos; notificacionesArtista=$avisosArtista; http=$peticiones.ToArray()
} | ConvertTo-Json -Depth 10
