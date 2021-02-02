package br.com.zalf.prolog.webservice.entrega.mapa.validator;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.files.YamlUtils;
import br.com.zalf.prolog.webservice.config.PrologConfigFilesWatcher;
import lombok.Data;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-05-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data(staticConstructor = "of")
public final class RegrasPlanilhaMapaLoader implements PrologConfigFilesWatcher.FileWatchListener {
    @NotNull
    private static final String TAG = RegrasPlanilhaMapaLoader.class.getSimpleName();
    // O arquivo está localizado na pasta "resources" do projeto.
    @NotNull
    private static final String NOME_ARQUIVO_MAPEAMENTO_PLANILHA_MAPA = "configs/regras_colunas_planilha_mapa.yaml";
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

    @Override
    @NotNull
    public String getFileNameToWatchChanges() {
        return FilenameUtils.getName(NOME_ARQUIVO_MAPEAMENTO_PLANILHA_MAPA);
    }

    @Override
    public void onWatchedFileChanged() {
        Log.d(TAG, "File changed: " + NOME_ARQUIVO_MAPEAMENTO_PLANILHA_MAPA);
        sRegras = loadFromResource();
    }
}
