package br.com.zalf.prolog.webservice.messaging.email._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum EmailTemplate {
    ABERTURA_SOCORRO_ROTA("1255647");

    @NotNull
    private final String templateId;

    EmailTemplate(@NotNull final String templateId) {
        this.templateId = templateId;
    }

    @NotNull
    public String getTemplateId() {
        return templateId;
    }
}
