package br.com.zalf.prolog.webservice.frota.pneu.v3.service.servico;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.PneuTipoServicoEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3.dao.servico.PneuTipoServicoV3Dao;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Service
public class PneuTipoServicoV3ServiceImpl implements PneuTipoServicoV3Service {

    private final PneuTipoServicoV3Dao dao;

    @Autowired
    public PneuTipoServicoV3ServiceImpl(@NotNull final PneuTipoServicoV3Dao dao) {
        this.dao = dao;
    }

    @Override
    @NotNull
    public PneuTipoServicoEntity getInitialTipoServicoForVidaIncrementada() {
        throw new NotImplementedException("metodo não implementado");
    }
}
