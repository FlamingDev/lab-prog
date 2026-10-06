package infra.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroRespostaDto(
        Instant timestamp,
        int status,
        String erro,
        String mensagem,
        List<ValidacaoErroDto> validacoes
) {
    public ErroRespostaDto(Instant timestamp, int status, String erro, String mensagem) {
        this(timestamp, status, erro, mensagem, null);
    }
}
