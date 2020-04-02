package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.origemdestino._model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created on 2020-03-25
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class MotivoMovimentoOrigemDestinoInsercao {
    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final List<MotivoMovimentoOrigemDestinoMotivosResumido> origensDestinos;
}
