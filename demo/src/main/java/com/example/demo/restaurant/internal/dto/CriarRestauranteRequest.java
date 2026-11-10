package com.example.demo.restaurant.internal.dto;

import com.example.demo.common.Cep;
import com.example.demo.common.EnderecoEmbeddable;
import com.example.demo.restaurant.internal.entity.RestauranteEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CriarRestauranteRequest(

        @NotBlank(message = "O nome do restaurante é obrigatório")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
        String nome,

        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
        String descricao,

        @NotBlank(message = "O telefone é obrigatório")
        @Pattern(regexp = "^\\d{10,11}$", message = "O telefone deve conter 10 ou 11 dígitos numéricos")
        String telefone,

        @PositiveOrZero(message = "O valor mínimo do pedido não pode ser negativo")
        BigDecimal valorMinimoPedido,

        @NotNull(message = "O endereço é obrigatório")
        @Valid
        EnderecoRequestDto endereco,

        @NotBlank(message = "A logo do restaurante é obrigatória")
        @Size(max = 500, message = "O caminho da logo deve ter no máximo 500 caracteres")
        String logoUrl,

        @NotBlank(message = "O banner do restaurante é obrigatório")
        @Size(max = 500, message = "O caminho do banner deve ter no máximo 500 caracteres")
        String bannerUrl
) {

    public record EnderecoRequestDto(
            @NotBlank(message = "O CEP é obrigatório")
            @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "Formato de CEP inválido")
            String cep,

            @NotBlank(message = "O logradouro é obrigatório")
            String logradouro,

            @NotBlank(message = "O número é obrigatório")
            String numero,

            @NotBlank(message = "O bairro é obrigatório")
            String bairro,

            String complemento
    ) {
        public EnderecoEmbeddable toEmbeddable() {
            return EnderecoEmbeddable.builder()
                    .cep(new Cep(this.cep))
                    .logradouro(this.logradouro)
                    .numero(this.numero)
                    .bairro(this.bairro)
                    .complemento(this.complemento)
                    .build();
        }
    }

    public RestauranteEntity toEntity() {
        var enderecoEmbeddable = this.endereco != null ? this.endereco.toEmbeddable() : null;
        var valorMinimo = this.valorMinimoPedido != null ? this.valorMinimoPedido : BigDecimal.ZERO;

        var restaurante = new RestauranteEntity(
                this.nome,
                this.telefone,
                enderecoEmbeddable,
                valorMinimo,
                this.logoUrl,
                this.bannerUrl
        );
        restaurante.setDescricao(this.descricao);
        return restaurante;
    }
}
