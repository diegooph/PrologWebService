package br.com.zalf.prolog.webservice.interno.apresentacao;

/**
 * Created on 13/04/20.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface ApresentacaoDao {
    void getResetaClonaEmpresaApresentacao(String username, Long codEmpresaBase, Long codEmpresaUsuario) throws Throwable;
}
