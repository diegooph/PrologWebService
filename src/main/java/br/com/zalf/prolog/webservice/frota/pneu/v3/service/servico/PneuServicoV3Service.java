package br.com.zalf.prolog.webservice.frota.pneu.v3.service.servico;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.PneuServicoEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface PneuServicoV3Service {

      @NotNull
      PneuServicoEntity createServicoByPneu(@NotNull final PneuEntity pneuEntity);
}
