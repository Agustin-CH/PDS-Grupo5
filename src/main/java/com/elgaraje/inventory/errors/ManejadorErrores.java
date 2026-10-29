package com.elgaraje.inventory.errors;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.*;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ManejadorErrores {
  private static final Logger LOG = LoggerFactory.getLogger(ManejadorErrores.class);

  @ExceptionHandler(NoEncontradoException.class)
  public ProblemDetail noEncontrado(NoEncontradoException e) {
    return problema(HttpStatus.NOT_FOUND, e.getMessage());
  }

  @ExceptionHandler(NegocioException.class)
  public ProblemDetail negocio(NegocioException e) {
    return problema(HttpStatus.CONFLICT, e.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail validacion(MethodArgumentNotValidException e) {
    ProblemDetail p = problema(HttpStatus.BAD_REQUEST, "Solicitud inválida");
    p.setProperty(
        "errores",
        e.getBindingResult().getFieldErrors().stream()
            .map(f -> f.getField() + ": " + f.getDefaultMessage())
            .toList());
    return p;
  }

  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    MethodArgumentTypeMismatchException.class
  })
  public ProblemDetail formato(Exception e) {
    return problema(HttpStatus.BAD_REQUEST, "JSON, identificador o valor inválido");
  }

  @ExceptionHandler({DataIntegrityViolationException.class, ConcurrencyFailureException.class})
  public ProblemDetail conflicto(Exception e) {
    return problema(
        HttpStatus.CONFLICT,
        "Conflicto de persistencia: registro duplicado o modificado por otra operación");
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> inesperado(Exception e) {
    if (e instanceof ErrorResponse respuesta) {
      HttpStatus estado = HttpStatus.valueOf(respuesta.getStatusCode().value());
      return ResponseEntity.status(estado)
          .headers(respuesta.getHeaders())
          .body(problema(estado, estado.getReasonPhrase()));
    }
    String referencia = UUID.randomUUID().toString();
    LOG.error("Error interno. Referencia: {}", referencia, e);
    ProblemDetail p = problema(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    p.setProperty("referencia", referencia);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(p);
  }

  private ProblemDetail problema(HttpStatus estado, String mensaje) {
    ProblemDetail p = ProblemDetail.forStatusAndDetail(estado, mensaje);
    p.setTitle(estado.getReasonPhrase());
    return p;
  }
}
