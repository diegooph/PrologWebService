package br.com.zalf.prolog.webservice.integracao.logger._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-09-02
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface RequestResponseLog {
    @NotNull
    String toJson();

    int getStatusCode();
}
