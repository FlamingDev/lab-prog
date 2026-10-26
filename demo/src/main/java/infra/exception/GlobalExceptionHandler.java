package infra.exception;

import com.example.demo.restaurant.internal.exception.RestauranteException;
import com.example.demo.restaurant.internal.exception.RestauranteNaoEncontradoException;
import com.example.demo.usuario.internal.exception.CadastroUsuarioException;
import com.example.demo.usuario.internal.exception.UsuarioAuthorizationException;
import jakarta.persistence.EntityNotFoundException;
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

    @ExceptionHandler(RestauranteNaoEncontradoException.class)
    public ResponseEntity<ErroRespostaDto> handleRestauranteNaoEncontrado(RestauranteNaoEncontradoException ex) {
        ErroRespostaDto corpo = new ErroRespostaDto(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpo);
    }

    @ExceptionHandler(RestauranteException.class)
    public ResponseEntity<ErroRespostaDto> handleRestauranteException(RestauranteException ex) {
        ErroRespostaDto corpo = new ErroRespostaDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErroRespostaDto> handleEntityNotFound(EntityNotFoundException ex) {
        ErroRespostaDto corpo = new ErroRespostaDto(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpo);
    }

    @ExceptionHandler(CadastroUsuarioException.class)
    public ResponseEntity<ErroRespostaDto> handleCadastroUsuario(CadastroUsuarioException ex) {
        ErroRespostaDto corpo = new ErroRespostaDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    @ExceptionHandler(UsuarioAuthorizationException.class)
    public ResponseEntity<ErroRespostaDto> handleUsuarioAuthorization(UsuarioAuthorizationException ex) {
        ErroRespostaDto corpo = new ErroRespostaDto(
                Instant.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(corpo);
    }
}

