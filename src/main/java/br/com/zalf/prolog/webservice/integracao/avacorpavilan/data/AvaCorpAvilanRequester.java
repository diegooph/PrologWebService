package br.com.zalf.prolog.webservice.integracao.avacorpavilan.data;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.OrdemServicoAvaCorpAvilan;
import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-08-31
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface AvaCorpAvilanRequester {
    void insertChecklistOs(@NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder,
                           @NotNull final OrdemServicoAvaCorpAvilan ordemServicoAvaCorpAvilan) throws Throwable;
}
