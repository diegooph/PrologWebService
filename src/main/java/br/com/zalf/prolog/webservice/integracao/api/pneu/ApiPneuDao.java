package br.com.zalf.prolog.webservice.integracao.api.pneu;

import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiPneuAlteracaoStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 16/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ApiPneuDao {
    void atualizaStatusPneus(@NotNull final String tokenIntegracao,
                             @NotNull final List<ApiPneuAlteracaoStatus> pneusAtualizacaoStatus) throws Throwable;
}
