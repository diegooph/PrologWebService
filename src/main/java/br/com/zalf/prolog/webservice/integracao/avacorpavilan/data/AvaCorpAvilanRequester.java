package br.com.zalf.prolog.webservice.integracao.avacorpavilan.data;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.InfosEnvioOsIntegracao;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist.os._model.OsAvilan;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-08-31
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface AvaCorpAvilanRequester {
    void insertChecklistOs(@NotNull final InfosEnvioOsIntegracao infosEnvioOsIntegracao,
                           @NotNull final OsAvilan osAvilan) throws Throwable;
}
