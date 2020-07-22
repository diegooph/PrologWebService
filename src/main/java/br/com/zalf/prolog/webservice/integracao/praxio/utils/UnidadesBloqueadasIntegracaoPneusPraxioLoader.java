package br.com.zalf.prolog.webservice.integracao.praxio.utils;

import br.com.zalf.prolog.webservice.commons.util.YamlUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 2020-07-21
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class UnidadesBloqueadasIntegracaoPneusPraxioLoader {
    // O arquivo está localizado na pasta "resources" do projeto.
    private static final String NOME_ARQUIVO_MAPEAMENTO_PLANILHA_MAPA =
            "unidades_bloqueadas_integracao_pneus_praxio.yaml";
    @Nullable
    private static volatile IntegracaoPraxioUnidadesBloqueadas sUnidadesBloqueadas;

    static {
        sUnidadesBloqueadas = loadFromResource();
    }

    @NotNull
    public static List<Long> getUnidadesBloqueadas() {
        // Apenas caso a inicialização no bloco static tenha falhado.
        if (sUnidadesBloqueadas == null) {
            synchronized (List.class) {
                if (sUnidadesBloqueadas == null) {
                    sUnidadesBloqueadas = loadFromResource();
                }
            }
        }
        //noinspection ConstantConditions
        return sUnidadesBloqueadas.getCodUnidadesBloqueadas();
    }

    @NotNull
    private static IntegracaoPraxioUnidadesBloqueadas loadFromResource() {
        return YamlUtils.parseFromResource(NOME_ARQUIVO_MAPEAMENTO_PLANILHA_MAPA, IntegracaoPraxioUnidadesBloqueadas.class);
    }
}
