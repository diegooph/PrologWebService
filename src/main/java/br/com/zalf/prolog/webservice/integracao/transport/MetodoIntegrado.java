package br.com.zalf.prolog.webservice.integracao.transport;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/30/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public enum MetodoIntegrado {
    INSERT_MOVIMENTACAO("INSERT_MOVIMENTACAO"),
    GET_LOCAIS_DE_MOVIMENTO("GET_LOCAIS_DE_MOVIMENTO"),
    GET_AUTENTICACAO("GET_AUTENTICACAO");

    @NotNull
    private final String key;

    MetodoIntegrado(@NotNull final String key) {
        this.key = key;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    @NotNull
    public static MetodoIntegrado fromString(@NotNull final String key) {
        final MetodoIntegrado[] recursosIntegrados = MetodoIntegrado.values();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < recursosIntegrados.length; i++) {
            if (recursosIntegrados[i].key.equals(key)) {
                return recursosIntegrados[i];
            }
        }
        throw new IllegalArgumentException("Nenhum método integrado encontrado com a chave: " + key);
    }
}
