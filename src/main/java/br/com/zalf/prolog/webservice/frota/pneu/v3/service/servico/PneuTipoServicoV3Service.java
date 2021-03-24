package br.com.zalf.prolog.webservice.frota.pneu.v3.service.servico;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.PneuTipoServicoEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3.dao.servico.PneuTipoServicoV3Dao;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Service
public class PneuTipoServicoV3Service {

    private final PneuTipoServicoV3Dao dao;

    @Autowired
    public PneuTipoServicoV3Service(@NotNull final PneuTipoServicoV3Dao dao) {
        this.dao = dao;
    }

    @NotNull
    @Transactional
    public PneuTipoServicoEntity getInitialTipoServicoForVidaIncrementada() {
        return this.dao.getInitialTipoServicoForVidaIncrementada();
    }
}
