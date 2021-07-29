package br.com.zalf.prolog.webservice.v3.fleet.tire.pneuservico.tiposervico;

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
public class PneuTipoServicoService {
    @NotNull
    private final PneuTipoServicoDao dao;

    @Autowired
    public PneuTipoServicoService(@NotNull final PneuTipoServicoDao dao) {
        this.dao = dao;
    }

    @NotNull
    @Transactional
    public PneuTipoServicoEntity getTipoServicoIncrementaVidaCadastroPneu() {
        return dao.getTipoServicoIncrementaVidaCadastroPneu();
    }
}
