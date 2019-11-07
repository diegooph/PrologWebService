package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.implantacao.autenticacao.ImplantacaoLoginSenhaValidator;
import br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo.model.VeiculoPlanilha;
import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
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

    public void getVerificacaoPlanilhaImportVeiculo(@NotNull final String usernamePassword,
                                                    @NotNull final Long codEmpresa,
                                                    @NotNull final Long codUnidade,
                                                    @NotNull final InputStream fileInputStream,
                                                    @NotNull final FormDataContentDisposition fileDetail) {
        try {
            final String usuario = new ImplantacaoLoginSenhaValidator().verifyUsernamePassword(usernamePassword);
            final File file = createFileFromImport(codUnidade, fileInputStream, fileDetail);
            readAndInsertImport(codEmpresa, codUnidade, usuario, file);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao verificar planilha de import de veiculos", throwable);
            throw new RuntimeException(throwable);
        }
    }

    @SuppressWarnings("Duplicates")
    @NotNull
    private File createFileFromImport(@NotNull final Long codUnidade,
                                      @NotNull final InputStream fileInputStream,
                                      @NotNull final FormDataContentDisposition fileDetail) throws ProLogException {
        try {
            final String fileName = String.valueOf(Now.utcMillis()) + "_" + codUnidade
                    + "_" + fileDetail.getFileName().replace(" ", "_");
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

    private void readAndInsertImport(@NotNull final Long codEmpresa,
                                     @NotNull final Long codUnidade,
                                     @NotNull final String usuario,
                                     @NotNull final File file) throws ProLogException {
        try {
            final List<VeiculoPlanilha> veiculoPlanilha = VeiculoPlanilhaReader.readListFromCsvFilePath(file);
            String jsonPlanilha = GsonUtils.getGson().toJson(veiculoPlanilha);
            dao.importPlanilhaVeiculos(codEmpresa, codUnidade, usuario, jsonPlanilha);
        } catch (Throwable e) {
            Log.e(TAG, "Erro ao enviar dados para o BD", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao verificar dados, tente novamente");
        }
    }
}
