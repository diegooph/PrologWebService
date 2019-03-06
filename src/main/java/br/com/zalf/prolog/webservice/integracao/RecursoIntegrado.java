package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.frota.checklist.ChecklistResource;
import com.google.common.base.Preconditions;
import com.sun.istack.internal.NotNull;

/**
 * Constantes definindo quais recursos são possíveis de serem integrados com o ProLog. Caso uma empresa integre o
 * {@link RecursoIntegrado#CHECKLIST}, por exemplo, isso quer dizer que ela possui ao menos algum método dos disponíveis
 * no {@link ChecklistResource} integrado com seu ERP (ou qualquer outro sistema de controle).
 */
public enum RecursoIntegrado {
    CHECKLIST("CHECKLIST"),
    CHECKLIST_ORDEM_SERVICO("CHECKLIST_ORDEM_SERVICO"),
    VEICULOS("VEICULOS"),
    AFERICAO("AFERICAO");

    @NotNull
    private final String key;

    RecursoIntegrado(String key) {
        this.key = key;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    public static RecursoIntegrado fromString(@NotNull final String key) {
        Preconditions.checkNotNull(key, "key não pode ser nula!");

        final RecursoIntegrado[] recursosIntegrados = RecursoIntegrado.values();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < recursosIntegrados.length; i++) {
            if (recursosIntegrados[i].key.equals(key)) {
                return recursosIntegrados[i];
            }
        }

        throw new IllegalArgumentException("Nenhum recurso integrado encontrado com a chave: " + key);
    }
}