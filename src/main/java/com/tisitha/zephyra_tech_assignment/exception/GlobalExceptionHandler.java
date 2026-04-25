package com.tisitha.zephyra_tech_assignment.exception;

import com.tisitha.zephyra_tech_assignment.dto.ErrorDTO;
import com.tisitha.zephyra_tech_assignment.dto.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail ResourceNotFoundExceptionHandler(ResourceNotFoundException ex){
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,ex.getMessage());
    }

    @ExceptionHandler(EmailTakenException.class)
    public ProblemDetail EmailTakenExceptionHandler(EmailTakenException ex){
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,ex.getMessage());
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ProblemDetail OtpExpiredExceptionHandler(OtpExpiredException ex){
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,ex.getMessage());
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    public ProblemDetail PasswordNotMatchExceptionHandler(PasswordNotMatchException ex){
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,ex.getMessage());
    }

    @ExceptionHandler(ServiceFailureException.class)
    public ProblemDetail ServiceFailureExceptionHandler(ServiceFailureException ex){
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,ex.getMessage());
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ProblemDetail UnauthorizeAccessExceptionHandler(AuthenticationFailedException ex){
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        var errors = fieldErrors.stream().map(err -> new ErrorDTO(
                err.getField(),
                err.getDefaultMessage()
        )).collect(Collectors.toList());

        return new ResponseEntity<>(new ValidationErrorResponse(errors), HttpStatus.BAD_REQUEST);
    }
}
