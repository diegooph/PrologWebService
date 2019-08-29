package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.implantacao.ImplantacaoImportTokensValidator;
import br.com.zalf.prolog.webservice.implantacao.ImplatancaoImportTokens;
import br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo.model.VeiculoPlanilha;
import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.List;

/**
 * Created on 23/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class VeiculoConferenciaService {
    @NotNull
    private static final String TAG = VeiculoConferenciaService.class.getSimpleName();
    @NotNull
    private final VeiculoConferenciaDao dao = Injection.provideVeiculoConferenciaDao();

    void getVerificacaoPlanilhaImportVeiculoCsv(@NotNull final String token,
                                                @NotNull final OutputStream out,
                                                @NotNull final Long codUnidade,
                                                @NotNull final InputStream fileInputStream) {
        try {
            ImplantacaoImportTokensValidator.validateTokenFor(ImplatancaoImportTokens.IMPORT_VEICULO, token);

            final File file = createFileFromImport(codUnidade, fileInputStream);
            readAndInsertImport(out, codUnidade, file);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao verificar planilha de import de veiculos (CSV)", throwable);
            throw new RuntimeException(throwable);
        }
    }

    @SuppressWarnings("Duplicates")
    @NotNull
    private File createFileFromImport(@NotNull final Long codUnidade,
                                      @NotNull final InputStream fileInputStream) throws ProLogException {
        try {
            final String fileName = Now.utcMillis() + "_" + codUnidade;
            final File tmpDir = Files.createTempDir();
            final File file = new File(tmpDir, fileName);
            final FileOutputStream out = new FileOutputStream(file);
            IOUtils.copy(fileInputStream, out);
            IOUtils.closeQuietly(out);
            return file;
        } catch (IOException e) {
            Log.e(TAG, "Erro ao ler arquivo do import", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao verificar dados, tente novamente");
        }
    }

    private void readAndInsertImport(@NotNull final OutputStream out,
                                     @NotNull final Long codUnidade,
                                     @NotNull final File file) throws ProLogException {
        try {
            final List<VeiculoPlanilha> veiculoPlanilha = VeiculoPlanilhaReader.readListFromCsvFilePath(file);
            String jsonPlanilha = GsonUtils.getGson().toJson(veiculoPlanilha);
            dao.getVerificacaoPlanilhaImportVeiculoCsv(out, codUnidade, jsonPlanilha);
        } catch (Throwable e) {
            Log.e(TAG, "Erro ao enviar dados para o BD", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao verificar dados, tente novamente");
        }
    }
}
