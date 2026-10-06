package infra.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaDto> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<ValidacaoErroDto> erros = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ValidacaoErroDto(
                        fieldError.getField(),
                        fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Campo inválido"
                ))
                .toList();

        ErroRespostaDto corpo = new ErroRespostaDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Erro de validação nos campos da requisição",
                erros
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroRespostaDto> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ErroRespostaDto corpo = new ErroRespostaDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Corpo da requisição ausente ou mal formatado"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroRespostaDto> handleIllegalArgument(IllegalArgumentException ex) {
        ErroRespostaDto corpo = new ErroRespostaDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }
}
