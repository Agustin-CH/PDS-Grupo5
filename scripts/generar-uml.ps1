$ErrorActionPreference = "Stop"
$raiz = Split-Path -Parent $PSScriptRoot
$fuentes = Join-Path $raiz "src/main/java"
$tipos = @{}
$grupos = @{}
foreach ($archivo in (Get-ChildItem $fuentes -Recurse -Filter *.java | Sort-Object FullName)) {
    $texto = Get-Content $archivo.FullName -Raw -Encoding UTF8
    $paquete = [regex]::Match($texto, 'package\s+com\.elgaraje\.inventory(?:\.([\w.]+))?;').Groups[1].Value
    if (-not $paquete) { $paquete = "application" }
    $principal = $archivo.BaseName
    foreach ($tipo in [regex]::Matches($texto, '\b(public\s+)?(abstract\s+)?(class|interface|enum|record)\s+(\w+)')) {
        $nombre = $tipo.Groups[4].Value
        $clase = $tipo.Groups[3].Value
        $etiqueta = $nombre
        if ($nombre -ne $principal) { $etiqueta = "$principal.$nombre" }
        if ($tipos.ContainsKey($nombre)) { throw "Tipo ambiguo: $nombre" }
        $tipos[$nombre] = $true
        if (-not $grupos.ContainsKey($paquete)) { $grupos[$paquete] = [Collections.Generic.List[string]]::new() }
        $declaracion = "class"
        if ($clase -in @("interface", "enum")) { $declaracion = $clase }
        elseif ($tipo.Groups[2].Success) { $declaracion = "abstract class" }
        $estereotipo = ""
        if ($clase -eq "record") { $estereotipo = " <<record>>" }
        $linea = '  ' + $declaracion + ' "' + $etiqueta + '" as ' + $nombre + $estereotipo
        if ($paquete -eq "models" -and $clase -eq "class") {
            $linea += " {"
            $grupos[$paquete].Add($linea)
            foreach ($campo in [regex]::Matches($texto, 'private\s+([\w.$<>]+)\s+(\w+)\s*[;=]')) {
                $grupos[$paquete].Add("    -$($campo.Groups[2].Value) : $($campo.Groups[1].Value)")
            }
            $grupos[$paquete].Add("  }")
        } else { $grupos[$paquete].Add($linea) }
    }
}
# Relaciones reales del dominio, patrones, servicios y adaptadores REST.
$relaciones = @'
Usuario <|-- Artista
Usuario <|-- Comprador
Obra "*" --> "0..1" Artista : artista
Obra "*" --> "0..1" Comprador : comprador
Obra "*" --> "0..1" Comprador : reservadoPor
Obra "*" --> "0..1" Ubicacion : ubicacion
Artista "0..1" --> "*" Obra : obras
Comprador "0..1" --> "*" Obra : obrasAdquiridas
Consignacion "0..1" --> "1" Obra
Consignacion "*" --> "1" Artista
Venta "0..1" --> "1" Obra
Venta "0..1" --> "1" Consignacion
Venta "*" --> "1" Comprador
CertificadoAutenticidad "0..1" --> "1" Obra
AccionObra "*" --> "1" Obra
Notificacion "*" --> "1" Obra
Notificacion "*" --> "1" Usuario : destinatario
EstadoDeObra <|.. Disponible
EstadoDeObra <|.. EnExhibicion
EstadoDeObra <|.. Reservada
EstadoDeObra <|.. Vendida
EstadoDeObra <|.. EnRestauracion
CicloObra --> EstadoDeObra
CicloObra ..> Obra
ComisionStrategy <|.. ComisionPorcentaje
ComisionStrategy <|.. ComisionFija
CalculadoraComision --> ComisionStrategy
CostoVenta <|.. PrecioBase
CostoVenta <|.. AdicionalDecorator
AdicionalDecorator --> CostoVenta : base
AdicionalDecorator <|-- Enmarcado
AdicionalDecorator <|-- Envio
AdicionalDecorator <|-- Seguro
CreadorVenta <|-- CreadorVentaConsignada
CreadorVenta --> CalculadoraComision
CreadorVenta ..> CostoVenta
CreadorVenta ..> Venta : preparar()
CreadorVentaConsignada ..> Venta : crearVenta()
ComandoObra <|.. ReservarObra
ComandoObra <|.. CancelarReserva
ComandoObra <|.. VenderObra
ComandoObra <|.. TrasladarObra
ComandoObra <|.. CambiarEstado
ComandoObra ..> ContextoOperacion
ContextoOperacion --> Obra
ContextoOperacion --> Comprador
ContextoOperacion --> Ubicacion
ContextoOperacion --> CicloObra
EjecutorComandos ..> ComandoObra
EjecutorComandos --> AccionObraRepository
EjecutorComandos ..> EventoObra : publica
NotificadorObra ..> EventoObra : @EventListener
NotificadorObra --> NotificacionRepository
NotificadorObra --> ObraRepository
NotificadorObra --> UsuarioRepository
CatalogoController --> CatalogoServicio
OperacionesController --> OperacionesServicio
CatalogoServicio ..> Solicitudes
CatalogoServicio ..> Respuestas
OperacionesServicio ..> Solicitudes
OperacionesServicio ..> Respuestas
OperacionesServicio --> EjecutorComandos
OperacionesServicio --> CicloObra
OperacionesServicio --> CreadorVenta
OperacionesServicio ..> CostoVenta
OperacionesServicio --> ObraRepository
OperacionesServicio --> CompradorRepository
OperacionesServicio --> UbicacionRepository
OperacionesServicio --> ConsignacionRepository
OperacionesServicio --> VentaRepository
OperacionesServicio --> AccionObraRepository
CatalogoServicio --> ArtistaRepository
CatalogoServicio --> CompradorRepository
CatalogoServicio --> ObraRepository
CatalogoServicio --> UbicacionRepository
CatalogoServicio --> ConsignacionRepository
CatalogoServicio --> CertificadoAutenticidadRepository
CatalogoServicio --> UsuarioRepository
CatalogoServicio --> NotificacionRepository
CatalogoServicio --> CalculadoraComision
ManejadorErrores ..> NegocioException
ManejadorErrores ..> NoEncontradoException
'@ -split "\r?\n"
$salida = [Collections.Generic.List[string]]::new()
$salida.Add("@startuml")
$salida.Add("!pragma layout smetana")
$salida.Add("title El Garaje - Clases implementadas")
$salida.Add("hide empty members")
$salida.Add("skinparam packageStyle rectangle")
foreach ($grupo in ($grupos.Keys | Sort-Object)) {
    $salida.Add('package "' + $grupo + '" {')
    $salida.AddRange($grupos[$grupo])
    $salida.Add("}")
}
foreach ($relacion in $relaciones) {
    $extremos = [regex]::Matches($relacion, '^([A-Za-z]\w*)|(?:-->|\.\.>|<\|--|<\|\.\.)\s+(?:"[^"]+"\s+)?([A-Za-z]\w*)')
    foreach ($extremo in $extremos) {
        $nombre = $extremo.Groups[1].Value + $extremo.Groups[2].Value
        if (-not $tipos.ContainsKey($nombre)) { throw "Clase inexistente en UML: $nombre" }
    }
    $salida.Add($relacion)
}
foreach ($nombre in ($tipos.Keys | Where-Object { $_ -like "*Repository" } | Sort-Object)) {
    $entidad = $nombre -replace 'Repository$', ''
    if ($tipos.ContainsKey($entidad)) { $salida.Add("$nombre ..> $entidad : persiste") }
}
$salida.Add('legend bottom')
$salida.Add('Tipos generados desde src/main/java; se omiten bibliotecas externas y getters/setters.')
$salida.Add('Las relaciones del dominio y patrones se mantienen en scripts/generar-uml.ps1.')
$salida.Add('endlegend')
$salida.Add("@enduml")
$carpeta = Join-Path $raiz "docs"
$null = New-Item -ItemType Directory -Path $carpeta -Force
$destino = Join-Path $carpeta "el-garaje.puml"
[IO.File]::WriteAllLines($destino, $salida, [Text.UTF8Encoding]::new($false))
Write-Output "UML generado: $($tipos.Count) tipos en $destino"
