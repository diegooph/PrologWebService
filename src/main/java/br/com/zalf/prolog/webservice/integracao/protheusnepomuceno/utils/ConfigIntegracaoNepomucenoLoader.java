package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.utils;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.files.YamlUtils;
import br.com.zalf.prolog.webservice.config.PrologConfigFilesWatcher;
import br.com.zalf.prolog.webservice.entrega.mapa.validator.RegrasPlanilhaMapaLoader;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.ConfigIntegracaoNepomuceno;
import lombok.Data;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data(staticConstructor = "of")
public class ConfigIntegracaoNepomucenoLoader implements PrologConfigFilesWatcher.FileWatchListener {
    @NotNull
    private static final String TAG = ConfigIntegracaoNepomucenoLoader.class.getSimpleName();
    // O arquivo está localizado na pasta "resources" do projeto.
    @NotNull
    private static final String NOME_ARQUIVO_FAMILIA_MODELO_BLOQUEIO =
            "configs/integracoes/config_integracao_nepomuceno.yaml";
    @Nullable
    private static volatile ConfigIntegracaoNepomuceno sConfigIntegracaoNepomuceno;

    static {
        sConfigIntegracaoNepomuceno = loadFromResource();
    }

    @NotNull
    public static ConfigIntegracaoNepomuceno getConfigIntegracaoNepomuceno() {
        // Apenas caso a inicialização no bloco static tenha falhado.
        if (sConfigIntegracaoNepomuceno == null) {
            synchronized (RegrasPlanilhaMapaLoader.class) {
                if (sConfigIntegracaoNepomuceno == null) {
                    sConfigIntegracaoNepomuceno = loadFromResource();
                }
            }
        }
        //noinspection ConstantConditions
        return sConfigIntegracaoNepomuceno;
    }

    @NotNull
    private static ConfigIntegracaoNepomuceno loadFromResource() {
        return YamlUtils.parseFromResource(NOME_ARQUIVO_FAMILIA_MODELO_BLOQUEIO, ConfigIntegracaoNepomuceno.class);
    }

    @Override
    @NotNull
    public String getFileNameToWatchChanges() {
        return FilenameUtils.getName(NOME_ARQUIVO_FAMILIA_MODELO_BLOQUEIO);
    }

    @Override
    public void onWatchedFileChanged() {
        Log.d(TAG, "File changed: " + NOME_ARQUIVO_FAMILIA_MODELO_BLOQUEIO);
        sConfigIntegracaoNepomuceno = loadFromResource();
    }
}
