package br.com.zalf.prolog.webservice.contato;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 21/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface EntreEmContatoDao {

    /**
     * Método utilizado para salvar as mensagens de futuros clientes que entraram em contato a partir do formulário
     * de contato presente no aplicativo.
     *
     * @param contato - Um objeto {@link MensagemContato} contendo as informações do cliente.
     * @return - O código da mensagem inserida no Banco de dados.
     * @throws Throwable - Se algum erro ocorrer no processo de inserção de informções no banco de dados.
     */
    @NotNull
    Long insertNovaMensagemContato(@NotNull final MensagemContato contato) throws Throwable;
}