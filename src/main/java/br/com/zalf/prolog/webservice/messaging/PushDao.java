package br.com.zalf.prolog.webservice.messaging;

import br.com.zalf.prolog.webservice.messaging._model.PushColaboradorCadastro;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface PushDao {

    void salvarTokenPushColaborador(@NotNull final PushColaboradorCadastro pushColaborador,
                                    @NotNull final String userToken) throws Throwable;
}
