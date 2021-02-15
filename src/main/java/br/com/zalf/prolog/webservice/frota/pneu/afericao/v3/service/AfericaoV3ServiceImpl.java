package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3.service;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.busca.FiltroAfericaoAvulsa;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.busca.FiltroAfericaoPlaca;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.projections.AfericaoAvulsaProjection;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.projections.AfericaoPlacaProjection;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3.dao.AfericaoV3Dao;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created on 2021-02-09
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Service
public class AfericaoV3ServiceImpl implements AfericaoV3Service {

    @NotNull
    private final AfericaoV3Dao dao;

    @Autowired
    AfericaoV3ServiceImpl(@NotNull final AfericaoV3Dao dao) {
        this.dao = dao;
    }

    @Override
    @NotNull
    public List<AfericaoPlacaProjection> getAfericoesPlacas(@NotNull final FiltroAfericaoPlaca filtro) {

        return dao.getAfericoes(filtro.getDadosGerais().getCodUnidade(),
                                filtro.getCodTipoVeiculo(),
                                filtro.getPlacaVeiculo(),
                                filtro.getDadosGerais().getDataInicial(),
                                filtro.getDadosGerais().getDataFinal(),
                                filtro.getDadosGerais().getLimit(),
                                filtro.getDadosGerais().getOffset());
    }

    @Override
    @NotNull
    public List<AfericaoAvulsaProjection> getAfericoesAvulsas(@NotNull final FiltroAfericaoAvulsa filtro) {

        return this.dao.getAfericoes(filtro.getDadosGerais().getCodUnidade(),
                                     filtro.getDadosGerais().getDataInicial(),
                                     filtro.getDadosGerais().getDataFinal(),
                                     filtro.getDadosGerais().getLimit(),
                                     filtro.getDadosGerais().getOffset());
    }
}
