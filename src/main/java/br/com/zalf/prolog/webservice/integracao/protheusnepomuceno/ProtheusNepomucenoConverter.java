package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.AfericaoAvulsa;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.AfericaoPlaca;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.model.AfericaoAvulsaProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.model.AfericaoPlacaProtheusNepomuceno;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 3/10/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class ProtheusNepomucenoConverter {

    private ProtheusNepomucenoConverter() {
        throw new IllegalStateException(ProtheusNepomucenoConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static AfericaoPlacaProtheusNepomuceno convert(@NotNull final Long codUnidade,
                                                          @NotNull final AfericaoPlaca afericaoPlaca) {
        return null;
    }

    @NotNull
    public static AfericaoAvulsaProtheusNepomuceno convert(@NotNull final Long codUnidade,
                                                           @NotNull final AfericaoAvulsa afericaoAvulsa) {
        return null;
    }
}
