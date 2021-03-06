package br.com.zalf.prolog.webservice.integracao;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/30/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public enum MetodoIntegrado {
    INSERT_MOVIMENTACAO("INSERT_MOVIMENTACAO"),
    INSERT_AFERICAO_PLACA("INSERT_AFERICAO_PLACA"),
    INSERT_AFERICAO_AVULSA("INSERT_AFERICAO_AVULSA"),
    INSERT_OS("INSERT_OS"),
    GET_PNEUS_AFERICAO_AVULSA("GET_PNEUS_AFERICAO_AVULSA"),
    GET_VEICULOS_CRONOGRAMA_AFERICAO("GET_VEICULOS_CRONOGRAMA_AFERICAO"),
    GET_PNEU_NOVA_AFERICAO_AVULSA("GET_PNEU_NOVA_AFERICAO_AVULSA"),
    GET_CAMPOS_PERSONALIZADOS_AFERICAO("GET_CAMPOS_PERSONALIZADOS_AFERICAO"),
    GET_VEICULO_NOVA_AFERICAO_PLACA("GET_VEICULO_NOVA_AFERICAO_PLACA"),
    GET_LOCAIS_DE_MOVIMENTO("GET_LOCAIS_DE_MOVIMENTO"),
    GET_AUTENTICACAO("GET_AUTENTICACAO");

    @NotNull
    private final String key;

    MetodoIntegrado(@NotNull final String key) {
        this.key = key;
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

    @NotNull
    public String getKey() {
        return key;
    }
}
