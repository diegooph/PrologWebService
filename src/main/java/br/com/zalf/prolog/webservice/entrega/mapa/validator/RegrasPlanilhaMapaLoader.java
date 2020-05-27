package br.com.zalf.prolog.webservice.entrega.mapa.validator;

import br.com.zalf.prolog.webservice.commons.util.YamlUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-05-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class RegrasPlanilhaMapaLoader {
    // O arquivo está localizado na pasta "resources" do projeto.
    private static final String NOME_ARQUIVO_MAPEAMENTO_PLANILHA_MAPA = "regras_colunas_planilha_mapa.yaml";
    @Nullable
    private static volatile RegrasValidacaoPlanilhaMapa sRegras;

    static {
        sRegras = loadFromResource();
    }

    @NotNull
    public static RegrasValidacaoPlanilhaMapa getRegras() {
        // Apenas caso a inicialização no bloco static tenha falhado.
        if (sRegras == null) {
            synchronized (RegrasPlanilhaMapaLoader.class) {
                if (sRegras == null) {
                    sRegras = loadFromResource();
                }
            }
        }
        //noinspection ConstantConditions
        return sRegras;
    }

    @NotNull
    private static RegrasValidacaoPlanilhaMapa loadFromResource() {
        return YamlUtils.parseFromResource(NOME_ARQUIVO_MAPEAMENTO_PLANILHA_MAPA, RegrasValidacaoPlanilhaMapa.class);
    }
}
