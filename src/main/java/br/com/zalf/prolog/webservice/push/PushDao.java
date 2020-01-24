package br.com.zalf.prolog.webservice.push;

import br.com.zalf.prolog.webservice.push._model.PushColaboradorCadastro;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface PushDao {

    void salvarTokenPushColaborador(@NotNull final PushColaboradorCadastro pushColaborador) throws Throwable;
}
