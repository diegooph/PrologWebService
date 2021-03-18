package br.com.zalf.prolog.webservice.frota.pneu.v3.service.servico;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.PneuTipoServicoEntity;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Service
public class PneuTipoServicoV3ServiceImpl implements PneuTipoServicoV3Service {

    @Override
    @NotNull
    public PneuTipoServicoEntity getInitialTipoServicoForVidaIncrementada() {
        throw new NotImplementedException("metodo não implementado");
    }
}
