package br.com.zalf.prolog.webservice.integracao.webfinatto.data;

import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface SistemaWebFinattoRequester {
    @NotNull
    List<EmpresaWebFinatto> getFiltrosClientes(
            @NotNull final ApiAutenticacaoHolder autenticacaoHolder) throws Throwable;

    @NotNull
    List<VeiculoWebFinatto> getVeiculosByFiliais(@NotNull final ApiAutenticacaoHolder autenticacaoHolder,
                                                 @NotNull final String codFiliais,
                                                 @Nullable final String placaVeiculo) throws Throwable;

    @NotNull
    VeiculoWebFinatto getVeiculoByPlaca(@NotNull final ApiAutenticacaoHolder autenticacaoHolder,
                                        @NotNull final String codFilial,
                                        @NotNull final String placaSelecionada) throws Throwable;

    @NotNull
    List<PneuWebFinatto> getPneusByFiliais(@NotNull final ApiAutenticacaoHolder autenticacaoHolder,
                                           @NotNull final String codFiliais,
                                           @Nullable final String statusPneus,
                                           @Nullable final String codPneu) throws Throwable;

    @NotNull
    PneuWebFinatto getPneuByCodigo(@NotNull final ApiAutenticacaoHolder autenticacaoHolder,
                                   @NotNull final String codFilial,
                                   @NotNull final String codPneuSelecionado) throws Throwable;

    @NotNull
    ResponseAfericaoWebFinatto insertAfericaoPlaca(
            @NotNull final ApiAutenticacaoHolder autenticacaoHolder,
            @NotNull final AfericaoPlacaWebFinatto afericaoPlaca)
            throws Throwable;

    @NotNull
    ResponseAfericaoWebFinatto insertAfericaoAvulsa(
            @NotNull final ApiAutenticacaoHolder autenticacaoHolder,
            @NotNull final AfericaoPneuWebFinatto afericaoPneu) throws Throwable;
}
