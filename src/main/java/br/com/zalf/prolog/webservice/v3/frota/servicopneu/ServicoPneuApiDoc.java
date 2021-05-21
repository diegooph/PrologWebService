package br.com.zalf.prolog.webservice.v3.frota.servicopneu;

import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuListagemDto;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuStatus;

import java.util.List;

/**
 * Created on 2021-05-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface ServicoPneuApiDoc {

    List<ServicoPneuListagemDto> getServicosByUnidadeAndStatus(final List<Long> codUnidades,
                                                               final ServicoPneuStatus status,
                                                               final Long codVeiculo,
                                                               final Long codPneu,
                                                               final int limit,
                                                               final int offset);
}
