package br.com.zalf.prolog.webservice.integracao.autenticacao;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 13/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface AutenticacaoIntegracaoDao {

    boolean verifyIfTokenIntegracaoExists(@NotNull final String tokenIntegracao) throws Throwable;

    boolean verifyIfTokenIsActive(@NotNull final String tokenIntegracao) throws Throwable;
}
