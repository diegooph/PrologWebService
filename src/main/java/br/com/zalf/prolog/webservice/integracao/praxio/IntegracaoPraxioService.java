package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 12/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
class IntegracaoPraxioService {

    @NotNull
    List<AfericaoIntegracaoPraxio> getDummy() {
        final List<AfericaoIntegracaoPraxio> afericoes = new ArrayList<>();
        afericoes.add(AfericaoIntegracaoPraxio.createDummyAfericaoPlacaSulcoPressao());
        afericoes.add(AfericaoIntegracaoPraxio.createDummyAfericaoPlacaSulco());
        afericoes.add(AfericaoIntegracaoPraxio.createDummyAfericaoPlacaPressao());
        afericoes.add(AfericaoIntegracaoPraxio.createDummyAfericaoPneuAvulsoSulco());
        return afericoes;
    }

    void getAfericoesRealizadas(@NotNull final String tokenIntegracao,
                                @NotNull final Long codUltimaAfericao) throws ProLogException {
    }
}
