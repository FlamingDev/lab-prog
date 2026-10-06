package infra.exception;

public record ValidacaoErroDto(
        String campo,
        String mensagem
) {}
