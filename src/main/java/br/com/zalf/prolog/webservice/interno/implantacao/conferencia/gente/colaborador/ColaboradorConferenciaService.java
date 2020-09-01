package br.com.zalf.prolog.webservice.interno.implantacao.conferencia.gente.colaborador;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Files;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interno.autenticacao.AutenticacaoLoginSenhaValidator;
import br.com.zalf.prolog.webservice.interno.autenticacao._model.PrologInternalUser;
import br.com.zalf.prolog.webservice.interno.autenticacao._model.PrologInternalUserFactory;
import br.com.zalf.prolog.webservice.interno.implantacao.conferencia._model.TipoImport;
import br.com.zalf.prolog.webservice.interno.implantacao.conferencia.gente.colaborador._model.ColaboradorPlanilha;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created on 29/07/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class ColaboradorConferenciaService {

    @NotNull
    private static final String TAG = ColaboradorConferenciaService.class.getSimpleName();
    @NotNull
    private final ColaboradorConferenciaDao dao = Injection.provideColaboradorConferenciaDao();

    public Response getVerificacaoPlanilhaImportColaborador(@NotNull final String authorization,
                                                            @NotNull final Long codEmpresa,
                                                            @NotNull final Long codUnidade,
                                                            @NotNull final InputStream fileInputStream,
                                                            @NotNull final FormDataContentDisposition fileDetail) {
        // Deve ficar fora do try/catch porque não queremos mascarar erros de autentação com erros do processo de
        // import.
        final PrologInternalUser internalUser = PrologInternalUserFactory.fromHeaderAuthorization(authorization);
        new AutenticacaoLoginSenhaValidator().verifyUsernamePassword(internalUser);

        try {
            final File file = createFileFromImport(codUnidade, fileInputStream, fileDetail);
            readAndInsertImport(codEmpresa, codUnidade, internalUser.getUsername(), file);
            return Response.ok("Upload realizado com sucesso!");
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao verificar planilha de import de colaboradores", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro com a conexão");
        }
    }

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
        } catch (final IOException e) {
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
            final List<ColaboradorPlanilha> colaboradorPlanilha = ColaboradorPlanilhaReader.readListFromCsvFilePath(file);
            final String jsonPlanilha = GsonUtils.getGson().toJson(colaboradorPlanilha);
            dao.importPlanilhaColaborador(codEmpresa, codUnidade, usuario, jsonPlanilha, TipoImport.COLABORADOR);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao enviar dados para o BD", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao verificar dados, tente novamente");
        }
    }
}
