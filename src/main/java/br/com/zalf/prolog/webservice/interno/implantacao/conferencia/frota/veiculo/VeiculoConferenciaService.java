package br.com.zalf.prolog.webservice.interno.implantacao.conferencia.frota.veiculo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interno.autenticacao.AutenticacaoLoginSenhaValidator;
import br.com.zalf.prolog.webservice.interno.autenticacao._model.PrologInternalUser;
import br.com.zalf.prolog.webservice.interno.autenticacao._model.PrologInternalUserFactory;
import br.com.zalf.prolog.webservice.interno.implantacao.conferencia._model.TipoImport;
import br.com.zalf.prolog.webservice.interno.implantacao.conferencia.frota.veiculo._model.VeiculoPlanilha;
import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public Response getVerificacaoPlanilhaImportVeiculo(@NotNull final String authorization,
                                                        @NotNull final Long codEmpresa,
                                                        @NotNull final Long codUnidade,
                                                        @NotNull final InputStream fileInputStream,
                                                        @NotNull final FormDataContentDisposition fileDetail) throws ProLogException {
        try {
            final PrologInternalUser internalUser = PrologInternalUserFactory.fromHeaderAuthorization(authorization);
            new AutenticacaoLoginSenhaValidator().verifyUsernamePassword(internalUser);
            final File file = createFileFromImport(codUnidade, fileInputStream, fileDetail);
            readAndInsertImport(codEmpresa, codUnidade, internalUser.getUsername(), file);
            return Response.ok("Upload realizado com sucesso!");
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao verificar planilha de import de veiculos", throwable);
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
            final List<VeiculoPlanilha> veiculoPlanilha = VeiculoPlanilhaReader.readListFromCsvFilePath(file);
            final String jsonPlanilha = GsonUtils.getGson().toJson(veiculoPlanilha);
            dao.importPlanilhaVeiculos(codEmpresa, codUnidade, usuario, jsonPlanilha,  TipoImport.VEICULO);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao enviar dados para o BD", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao verificar dados, tente novamente");
        }
    }
}
