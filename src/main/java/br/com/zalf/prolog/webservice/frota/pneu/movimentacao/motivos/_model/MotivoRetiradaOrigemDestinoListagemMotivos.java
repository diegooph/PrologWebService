package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2020-03-23
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public @Data
class MotivoRetiradaOrigemDestinoListagemMotivos {

    @NotNull
    private final OrigemDestinoEnum origemMovimento;

    @NotNull
    private final OrigemDestinoEnum destinoMovimento;

    @NotNull
    private List<MotivoRetiradaListagem> motivosRetirada;

    @NotNull
    private boolean obrigatorioMotivoRetirada;

}
