package br.com.zalf.prolog.webservice.frota.pneu.v3.service.servico;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.PneuTipoServicoEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface PneuTipoServicoV3Service {

    @NotNull
    PneuTipoServicoEntity getInitialTipoServicoForVidaIncrementada();
}
