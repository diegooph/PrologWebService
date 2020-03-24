package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created on 2020-03-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public @Data
class MotivoRetiradaOrigemDestinoInsercao {

    @NotNull
    private final Long codMotivo;

    @NotNull
    private final Long codEmpresa;

    @NotNull
    private final Long codUnidade;

    @NotNull
    private final OrigemDestinoEnum origemMovimentacao;

    @NotNull
    private final OrigemDestinoEnum destinoMovimentacao;

    @NotNull
    private final boolean obrigatorioMotivo;

}
