package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.utils;

import br.com.zalf.prolog.webservice.commons.util.YamlUtils;
import br.com.zalf.prolog.webservice.entrega.mapa.validator.RegrasPlanilhaMapaLoader;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.FamiliasModelosBloqueio;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-07-29
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class FamiliaModeloBloqueadoLoader {
    // O arquivo está localizado na pasta "resources" do projeto.
    private static final String NOME_ARQUIVO_FAMILIA_MODELO_BLOQUEIO = "familia_modelo_bloqueado_nepomuceno.yaml";
    @Nullable
    private static volatile FamiliasModelosBloqueio sFamiliasModelosBloqueio;

    static {
        sFamiliasModelosBloqueio = loadFromResource();
    }

    @NotNull
    public static FamiliasModelosBloqueio getFamiliasModelosBloqueio() {
        // Apenas caso a inicialização no bloco static tenha falhado.
        if (sFamiliasModelosBloqueio == null) {
            synchronized (RegrasPlanilhaMapaLoader.class) {
                if (sFamiliasModelosBloqueio == null) {
                    sFamiliasModelosBloqueio = loadFromResource();
                }
            }
        }
        //noinspection ConstantConditions
        return sFamiliasModelosBloqueio;
    }

    @NotNull
    private static FamiliasModelosBloqueio loadFromResource() {
        return YamlUtils.parseFromResource(NOME_ARQUIVO_FAMILIA_MODELO_BLOQUEIO, FamiliasModelosBloqueio.class);
    }
}
