package br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 13/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ApiCadastroPneuDao {
    @NotNull
    List<ApiPneuCargaInicialResponse> inserirCargaInicialPneu(
            @NotNull final String tokenIntegracao,
            @NotNull final List<ApiPneuCargaInicial> pneusCargaInicial) throws Throwable;

    @NotNull
    Long inserirPneuCadastro(@NotNull final String tokenIntegracao,
                             @NotNull final ApiPneuCadastro pneuCadastro) throws Throwable;

    @NotNull
    Long atualizarPneuEdicao(@NotNull final String tokenIntegracao,
                             @NotNull final ApiPneuEdicao pneuEdicao) throws Throwable;

    @NotNull
    Long transferirPneu(@NotNull final String tokenIntegracao,
                        @NotNull final ApiPneuTransferencia pneuTransferencia) throws Throwable;
}
