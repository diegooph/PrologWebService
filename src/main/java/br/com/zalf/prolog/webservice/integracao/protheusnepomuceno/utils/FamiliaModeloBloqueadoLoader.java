package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.utils;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.YamlUtils;
import br.com.zalf.prolog.webservice.config.PrologConfigFilesWatcher;
import br.com.zalf.prolog.webservice.entrega.mapa.validator.RegrasPlanilhaMapaLoader;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.FamiliasModelosPlacasBloqueio;
import lombok.Data;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-07-29
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Data(staticConstructor = "of")
public final class FamiliaModeloBloqueadoLoader implements PrologConfigFilesWatcher.FileWatchListener {
    @NotNull
    private static final String TAG = FamiliaModeloBloqueadoLoader.class.getSimpleName();
    // O arquivo está localizado na pasta "resources" do projeto.
    @NotNull
    private static final String NOME_ARQUIVO_FAMILIA_MODELO_BLOQUEIO =
            "configs/integracoes/familia_modelo_bloqueado_nepomuceno.yaml";
    @Nullable
    private static volatile FamiliasModelosPlacasBloqueio sFamiliasModelosPlacasBloqueio;

    static {
        sFamiliasModelosPlacasBloqueio = loadFromResource();
    }

    @NotNull
    public static FamiliasModelosPlacasBloqueio getFamiliasModelosPlacasBloqueio() {
        // Apenas caso a inicialização no bloco static tenha falhado.
        if (sFamiliasModelosPlacasBloqueio == null) {
            synchronized (RegrasPlanilhaMapaLoader.class) {
                if (sFamiliasModelosPlacasBloqueio == null) {
                    sFamiliasModelosPlacasBloqueio = loadFromResource();
                }
            }
        }
        //noinspection ConstantConditions
        return sFamiliasModelosPlacasBloqueio;
    }

    @NotNull
    private static FamiliasModelosPlacasBloqueio loadFromResource() {
        return YamlUtils.parseFromResource(NOME_ARQUIVO_FAMILIA_MODELO_BLOQUEIO, FamiliasModelosPlacasBloqueio.class);
    }

    @Override
    @NotNull
    public String getFileNameToWatchChanges() {
        return FilenameUtils.getName(NOME_ARQUIVO_FAMILIA_MODELO_BLOQUEIO);
    }

    @Override
    public void onWatchedFileChanged() {
        Log.d(TAG, "File changed: " + NOME_ARQUIVO_FAMILIA_MODELO_BLOQUEIO);
        sFamiliasModelosPlacasBloqueio = loadFromResource();
    }
}
