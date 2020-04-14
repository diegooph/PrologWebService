package br.com.zalf.prolog.webservice.interno.apresentacao;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 13/04/20.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface ApresentacaoDao {

    /**
     * Método para resetar uma empresa de apresentação e clonar dados da empresa base.
     *
     * @param username   usuário cadastrado.
     * @param codEmpresaBase código de empresa a qual vai servir de base para realizar a clonagem.
     * @param codEmpresaUsuario código da empresa do usuário que será resetada.
     * @throws Throwable Se algum erro ocorrer.
     */
    void getResetaClonaEmpresaApresentacao(@NotNull final String username,
                                           @NotNull final Long codEmpresaBase,
                                           @NotNull final Long codEmpresaUsuario) throws Throwable;
}
