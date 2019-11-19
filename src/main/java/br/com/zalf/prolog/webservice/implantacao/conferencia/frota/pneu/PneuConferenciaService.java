package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.pneu;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Files;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.implantacao.autenticacao.ImplantacaoLoginSenhaValidator;
import br.com.zalf.prolog.webservice.implantacao.conferencia.frota.pneu._model.PneuPlanilha;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created on 19/11/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuConferenciaService {

    @NotNull
    private static final String TAG = PneuConferenciaService.class.getSimpleName();
    @NotNull
    private final PneuConferenciaDao dao = Injection.providePneuConferenciaDao();

    public Response getVerificacaoPlanilhaImportPneu(@NotNull final String authorization,
                                                     @NotNull final Long codEmpresa,
                                                     @NotNull final Long codUnidade,
                                                     @NotNull final InputStream fileInputStream,
                                                     @NotNull final FormDataContentDisposition fileDetail) throws ProLogException {
        try {
            final String usuario = new ImplantacaoLoginSenhaValidator().verifyUsernamePassword(authorization);
            final File file = createFileFromImport(codUnidade, fileInputStream, fileDetail);
            readAndInsertImport(codEmpresa, codUnidade, usuario, file);
            return Response.ok("Upload realizado com sucesso!");
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao verificar planilha de import de pneus", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro com a conexão");
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
            final List<PneuPlanilha> pneuPlanilha = PneuPlanilhaReader.readListFromCsvFilePath(file);
            String jsonPlanilha = GsonUtils.getGson().toJson(pneuPlanilha);
            dao.importPlanilhaPneus(codEmpresa, codUnidade, usuario, jsonPlanilha);
        } catch (Throwable e) {
            Log.e(TAG, "Erro ao enviar dados para o BD", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao verificar dados, tente novamente");
        }
    }
}
