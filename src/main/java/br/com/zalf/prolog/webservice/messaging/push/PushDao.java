package br.com.zalf.prolog.webservice.messaging.push;

import br.com.zalf.prolog.webservice.messaging.push._model.PushColaboradorCadastro;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface PushDao {
    /**
     * Método responsável por vincular as credenciais de Push do colaborador, vinculando ao token de acesso ao app.
     *
     * @param userToken       Token utilizado para validar o login do usuário.
     * @param pushColaborador Credenciais de Push para o colaborador que realizou o login.
     * @throws Throwable Se algo errado acontecer.
     */
    void salvarTokenPushColaborador(@NotNull final String userToken,
                                    @NotNull final PushColaboradorCadastro pushColaborador) throws Throwable;
}
