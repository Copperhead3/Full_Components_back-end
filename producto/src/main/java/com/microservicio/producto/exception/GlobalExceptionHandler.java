package com.microservicio.producto.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Campos que no cumplen las validaciones del model o DTO.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidaciones(
            MethodArgumentNotValidException ex) {

        Map<String, String> errores = new LinkedHashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errores.putIfAbsent(
                                error.getField(),
                                error.getDefaultMessage()));

        ex.getBindingResult()
                .getGlobalErrors()
                .forEach(error ->
                        errores.putIfAbsent(
                                error.getObjectName(),
                                error.getDefaultMessage()));

        return ResponseEntity
                .badRequest()
                .body(errores);
    }

    // Producto, categoría u otro registro inexistente.
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> manejarNoEncontrado(
            NoSuchElementException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", ex.getMessage()));
    }

    // Datos que incumplen una regla de negocio.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarDatosInvalidos(
            IllegalArgumentException ex) {

        return ResponseEntity
                .badRequest()
                .body(Map.of("mensaje", ex.getMessage()));
    }

    // Una operación incompatible con el estado actual del producto.
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> manejarConflicto(
            IllegalStateException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("mensaje", ex.getMessage()));
    }

    // JSON incorrecto o un parámetro con tipo inválido.
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<Map<String, String>> manejarEntradaInvalida(
            Exception ex) {

        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "mensaje",
                        "Revisa el JSON y los tipos de los parámetros enviados"));
    }
}