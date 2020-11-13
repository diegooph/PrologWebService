package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.utils;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.YamlUtils;
import br.com.zalf.prolog.webservice.config.PrologConfigFilesWatcher;
import br.com.zalf.prolog.webservice.entrega.mapa.validator.RegrasPlanilhaMapaLoader;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.FamiliasModelosBloqueio;
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

    @Override
    @NotNull
    public String getFileNameToWatchChanges() {
        return FilenameUtils.getName(NOME_ARQUIVO_FAMILIA_MODELO_BLOQUEIO);
    }

    @Override
    public void onWatchedFileChanged() {
        Log.d(TAG, "File changed: " + NOME_ARQUIVO_FAMILIA_MODELO_BLOQUEIO);
        sFamiliasModelosBloqueio = loadFromResource();
    }
}
