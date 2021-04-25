package br.com.zalf.prolog.webservice.integracao.newrouter;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDaoV2;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.Afericao;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created on 2021-04-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public final class SistemaProlog implements SistemaIntegrado, AfericaoIntegrada {
    private static final String TAG = SistemaProlog.class.getSimpleName();
    @NotNull
    private final AfericaoDaoV2 afericaoDao;

    @NotNull
    @Override
    public SistemaKey getKey() {
        return SistemaKey.PROLOG;
    }

    @NotNull
    @Override
    public Long insertAfericao(@NotNull final Long codUnidade,
                               @NotNull final Afericao afericao,
                               final boolean deveAbrirServico) throws Throwable {
        Log.d(TAG, String.format("insertAfericao(...)\n" +
                                         "codUnidade -> %d\n" +
                                         "afericao -> %s\n" +
                                         "deveAbrirServico -> %b\n" +
                                         "afericaoDao -> %s", codUnidade, afericao, deveAbrirServico, afericaoDao));
        return -1L;
    }
}
