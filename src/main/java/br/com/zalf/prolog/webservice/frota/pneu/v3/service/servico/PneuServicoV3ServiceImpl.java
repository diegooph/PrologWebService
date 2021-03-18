package br.com.zalf.prolog.webservice.frota.pneu.v3.service.servico;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.PneuServicoEntity;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Service
public class PneuServicoV3ServiceImpl implements PneuServicoV3Service {

    @Override
    @NotNull
    public PneuServicoEntity createServicoByPneu(@NotNull final PneuEntity pneuEntity) {
        throw new NotImplementedException("metodo não implementado");
    }
}
