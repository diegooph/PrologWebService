package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.frota.checklist.ChecklistResource;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

/**
 * Constantes definindo quais recursos são possíveis de serem integrados com o ProLog. Caso uma empresa integre o
 * {@link RecursoIntegrado#CHECKLIST}, por exemplo, isso quer dizer que ela possui ao menos algum método dos disponíveis
 * no {@link ChecklistResource} integrado com seu ERP (ou qualquer outro sistema de controle).
 */
public enum RecursoIntegrado {
    CHECKLIST("CHECKLIST"),
    CHECKLIST_MODELO("CHECKLIST_MODELO"),
    CHECKLIST_ORDEM_SERVICO("CHECKLIST_ORDEM_SERVICO"),
    VEICULOS("VEICULOS"),
    TIPO_VEICULO("TIPO_VEICULO"),
    VEICULO_TRANSFERENCIA("VEICULO_TRANSFERENCIA"),
    AFERICAO("AFERICAO"),
    AFERICAO_SERVICO("AFERICAO_SERVICO"),
    PNEUS("PNEUS"),
    MOVIMENTACAO("MOVIMENTACAO"),
    PNEU_TRANSFERENCIA("PNEU_TRANSFERENCIA");

    @NotNull
    private final String key;

    RecursoIntegrado(@NotNull final String key) {
        this.key = key;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    @NotNull
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