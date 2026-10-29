package com.elgaraje.inventory.dtos;

import com.elgaraje.inventory.models.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class Solicitudes {
  private Solicitudes() {}

  public record ArtistaAlta(
      @NotBlank @Size(max = 255) String dni,
      @NotBlank @Size(max = 255) String nombre,
      @NotBlank @Size(max = 255) String apellido,
      String biografia,
      @NotNull TipoComision tipoComision,
      @NotNull @DecimalMin("0") @Digits(integer = 17, fraction = 2) BigDecimal valorComision) {}

  public record CompradorAlta(
      @NotBlank @Size(max = 255) String dni,
      @NotBlank @Size(max = 255) String nombre,
      @NotBlank @Size(max = 255) String apellido) {}

  public record UbicacionAlta(
      @NotBlank @Size(max = 255) String nombre, @Size(max = 255) String direccion) {}

  public record ObraAlta(
      @NotBlank @Size(max = 255) String titulo,
      @Positive Integer anio,
      @Size(max = 255) String dimensiones,
      String descripcion,
      @NotNull @Positive Long artistaId,
      @NotNull @Positive Long ubicacionId,
      @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) BigDecimal precio,
      List<@NotBlank @Size(max = 255) String> fotos) {}

  public record ConsignacionAlta(
      @NotNull @Positive Long obraId,
      @NotNull LocalDate fecha,
      LocalDate vencimiento,
      TipoComision tipoComision,
      @DecimalMin("0") @Digits(integer = 17, fraction = 2) BigDecimal valorComision) {}

  public record CertificadoAlta(
      @NotNull @Positive Long obraId,
      @NotBlank @Size(max = 255) String numero,
      @NotNull LocalDate fechaEmision,
      @NotBlank @Size(max = 255) String emisor,
      @NotBlank @Size(max = 255) String firma) {}

  public record Reserva(@NotNull @Positive Long compradorId) {}

  public record Traslado(@NotNull @Positive Long ubicacionId) {}

  public record Transicion(@NotNull EstadoObra estado) {}

  public enum TipoAdicional {
    ENMARCADO,
    ENVIO,
    SEGURO
  }

  public record Adicional(
      @NotNull TipoAdicional tipo,
      @NotNull @DecimalMin("0") @Digits(integer = 17, fraction = 2) BigDecimal importe) {}

  public record VentaAlta(
      @NotNull @Positive Long compradorId, List<@NotNull @Valid Adicional> adicionales) {}
}
