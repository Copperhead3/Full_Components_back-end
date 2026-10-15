package com.microservicio.pago.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ProblemDetail noEncontrado(RecursoNoEncontradoException ex) {
        return problema(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ProblemDetail reglaNegocio(ReglaNegocioException ex) {
        return problema(HttpStatus.CONFLICT, "Regla de negocio incumplida", ex.getMessage());
    }

    @ExceptionHandler(PasarelaException.class)
    public ProblemDetail pasarela(PasarelaException ex) {
        return problema(HttpStatus.BAD_GATEWAY, "Error en la pasarela de pago", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errores.putIfAbsent(e.getField(), e.getDefaultMessage()));
        ProblemDetail pd = problema(HttpStatus.BAD_REQUEST, "Solicitud invalida", "Hay campos con errores de validacion");
        pd.setProperty("errores", errores);
        return pd;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail cuerpoIlegible(HttpMessageNotReadableException ex) {
        return problema(HttpStatus.BAD_REQUEST, "Solicitud invalida", "El cuerpo de la solicitud no es un JSON valido");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail tipoInvalido(MethodArgumentTypeMismatchException ex) {
        return problema(HttpStatus.BAD_REQUEST, "Parametro invalido",
                "Valor invalido para el parametro '" + ex.getName() + "'");
    }

    private ProblemDetail problema(HttpStatus status, String titulo, String detalle) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detalle);
        pd.setTitle(titulo);
        return pd;
    }
}
