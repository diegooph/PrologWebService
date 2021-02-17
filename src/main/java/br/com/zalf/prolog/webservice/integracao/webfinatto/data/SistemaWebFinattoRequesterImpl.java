package br.com.zalf.prolog.webservice.integracao.webfinatto.data;

import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SistemaWebFinattoRequesterImpl implements SistemaWebFinattoRequester {
    @Override
    @NotNull
    public List<VeiculoWebFinatto> getVeiculosByFiliais(@NotNull final ApiAutenticacaoHolder autenticacaoHolder,
                                                        @NotNull final String codFiliais,
                                                        @Nullable final String placaVeiculo) {
        return null;
    }

    @Override
    @NotNull
    public VeiculoWebFinatto getVeiculoByPlaca(@NotNull final ApiAutenticacaoHolder autenticacaoHolder,
                                               @NotNull final String codFilial,
                                               @NotNull final String placaSelecionada) {
        return null;
    }

    @Override
    @NotNull
    public List<PneuWebFinatto> getPneusByFiliais(@NotNull final ApiAutenticacaoHolder autenticacaoHolder,
                                                  @NotNull final String codFiliais,
                                                  @Nullable final String statusPneus,
                                                  @Nullable final String codPneu) {
        return null;
    }

    @Override
    @NotNull
    public PneuWebFinatto getPneusByCodigo(@NotNull final ApiAutenticacaoHolder autenticacaoHolder,
                                           @NotNull final String codFilial,
                                           @NotNull final String codPneuSelecionado) {
        return null;
    }

    @Override
    @NotNull
    public ResponseAfericaoWebFinatto insertAfericaoPlaca(@NotNull final ApiAutenticacaoHolder autenticacaoHolder,
                                                          @NotNull final AfericaoPlacaWebFinatto afericaoPlaca) {
        return null;
    }

    @Override
    @NotNull
    public ResponseAfericaoWebFinatto insertAfericaoPlaca(@NotNull final ApiAutenticacaoHolder autenticacaoHolder,
                                                          @NotNull final AfericaoPneuWebFinatto afericaoPneu) {
        return null;
    }
}
