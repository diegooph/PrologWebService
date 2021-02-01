package br.com.zalf.prolog.webservice.messaging.email._model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created on 2020-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@AllArgsConstructor
@Getter
public enum EmailTemplate {
    ABERTURA_SOCORRO_ROTA(1255647L);

    private final Long templateId;
}
