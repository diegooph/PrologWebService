package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.AfericaoAvulsaDto;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.AfericaoPlacaDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 2021-02-10
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface AfericaoV3ResourceApiDoc {


   List<AfericaoPlacaDto> getAfericoesPlacas(@NotNull final List<Long> codUnidades,
                                             @Nullable final String placaVeiculo,
                                             @Nullable final Long codTipoVeiculo,
                                             @NotNull final String dataInicial,
                                             @NotNull final String dataFinal,
                                             final int limit,
                                             final int offset);

   List<AfericaoAvulsaDto> getAfericoesAvulsas(@NotNull final List<Long> codUnidades,
                                               @NotNull final String dataInicial,
                                               @NotNull final String dataFinal,
                                               final int limit,
                                               final int offset);

}
