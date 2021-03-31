package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3.service;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.busca.FiltroAfericaoAvulsa;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.busca.FiltroAfericaoPlaca;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.projections.AfericaoAvulsaProjection;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.projections.AfericaoPlacaProjection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2021-02-09
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface AfericaoV3Service {


    @NotNull
    List<AfericaoPlacaProjection> getAfericoesPlacas(@NotNull final FiltroAfericaoPlaca filtro);

    @NotNull
    List<AfericaoAvulsaProjection> getAfericoesAvulsas(@NotNull final FiltroAfericaoAvulsa filtro);
}
