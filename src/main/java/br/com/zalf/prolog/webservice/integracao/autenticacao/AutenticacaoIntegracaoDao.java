package br.com.zalf.prolog.webservice.integracao.autenticacao;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 13/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface AutenticacaoIntegracaoDao {

    /**
     * Método utilizado para verificar se o token recebido é válido.
     * Para um token ser considerado válido é necessário que ele exista na tabela
     * de Tokens de Empresas integradas, no Banco de Dados do ProLog.
     *
     * @param tokenIntegracao Token recebido na requisição, que será validado.
     * @return <code>TRUE</code> se o token for válido, <code>FALSE</code> caso contrário.
     * @throws Throwable Se algum erro ocorrer durante a verificação do Token.
     */
    boolean verifyIfTokenIntegracaoExists(@NotNull final String tokenIntegracao) throws Throwable;

    /**
     * Método utilizado para verificar se o token recebido está ativo.
     *
     * @param tokenIntegracao Token recebido na requisição, que será validado.
     * @return <code>TRUE</code> se o token for válido, <code>FALSE</code> caso contrário.
     * @throws Throwable Se algum erro ocorrer durante a verificação do Token.
     */
    boolean verifyIfTokenIsActive(@NotNull final String tokenIntegracao) throws Throwable;
}
