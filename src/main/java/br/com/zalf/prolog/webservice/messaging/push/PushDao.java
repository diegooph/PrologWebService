package br.com.zalf.prolog.webservice.messaging.push;

import br.com.zalf.prolog.webservice.messaging.push._model.PushColaboradorCadastro;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface PushDao {

    void salvarTokenPushColaborador(@NotNull final String userToken,
                                    @NotNull final PushColaboradorCadastro pushColaborador) throws Throwable;
}
