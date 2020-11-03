package br.com.zalf.prolog.webservice.frota.checklist.model.alteracao_logica;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created on 2020-10-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */

@AllArgsConstructor
@Getter
public enum ChecklistAlteracaoAcao {
    DELETAR("DELETADO"), REVERTER_DELECAO("NAO_DELETADO");

    private final String value;
}
