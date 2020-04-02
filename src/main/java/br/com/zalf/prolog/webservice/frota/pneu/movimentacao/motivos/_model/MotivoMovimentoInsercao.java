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
    private final long codEmpresaMotivoRetirada;
    @NotBlank(message = "A descrição do motivo não pode ser branca.")
    private final String descricaoMotivoRetirada;
    @NotNull(message = "O campo ativo é obrigatório.")
    private final boolean ativoMotivoRetirada;
    @Nullable
    private final String codAuxiliarMotivoRetirada;
}
