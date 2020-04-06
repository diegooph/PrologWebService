package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created on 2020-03-17
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class MotivoMovimentoInsercao {
    @NotNull(message = "O código da empresa é obrigatório.")
    private final Long codEmpresaMotivoMovimento;
    @NotBlank(message = "A descrição do motivo não pode estar vazia.")
    private final String descricaoMotivoMovimento;
    @Nullable
    private final String codAuxiliarMotivoMovimento;
}
