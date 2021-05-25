package br.com.zalf.prolog.webservice.v3.frota.servicopneu;

import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuListagemDto;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 2021-05-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface ServicoPneuApiDoc {

    List<ServicoPneuListagemDto> getServicosByUnidadeAndStatus(final @NotNull List<Long> codUnidades,
                                                               final @NotNull ServicoPneuStatus status,
                                                               final @Nullable Long codVeiculo,
                                                               final @Nullable Long codPneu,
                                                               final int limit,
                                                               final int offset);
}
