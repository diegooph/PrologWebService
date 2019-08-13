package br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 13/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ApiCadastroPneuDao {
    Long insertPneuCadastro(@NotNull final String tokenIntegracao,
                            @NotNull final ApiPneuCadastro pneuCadastro) throws Throwable;
}
