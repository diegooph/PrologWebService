package br.com.zalf.prolog.webservice.integracao.webfinatto.data;

import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface SistemaWebFinattoRequester {
    @NotNull
    List<VeiculoWebFinatto> getVeiculosByFiliais(@NotNull final ApiAutenticacaoHolder autenticacaoHolder,
                                                 @NotNull final String codFiliais,
                                                 @Nullable final String placaVeiculo);

    @NotNull
    VeiculoWebFinatto getVeiculoByPlaca(@NotNull final ApiAutenticacaoHolder autenticacaoHolder,
                                        @NotNull final String codFilial,
                                        @NotNull final String placaSelecionada);

    @NotNull
    List<PneuWebFinatto> getPneusByFiliais(@NotNull final ApiAutenticacaoHolder autenticacaoHolder,
                                           @NotNull final String codFiliais,
                                           @Nullable final String statusPneus,
                                           @Nullable final String codPneu);

    @NotNull
    PneuWebFinatto getPneusByCodigo(@NotNull final ApiAutenticacaoHolder autenticacaoHolder,
                                    @NotNull final String codFilial,
                                    @NotNull final String codPneuSelecionado);

    @NotNull
    ResponseAfericaoWebFinatto insertAfericaoPlaca(@NotNull final ApiAutenticacaoHolder autenticacaoHolder,
                                                   @NotNull final AfericaoPlacaWebFinatto afericaoPlaca);

    @NotNull
    ResponseAfericaoWebFinatto insertAfericaoPlaca(@NotNull final ApiAutenticacaoHolder autenticacaoHolder,
                                                   @NotNull final AfericaoPneuWebFinatto afericaoPneu);
}
