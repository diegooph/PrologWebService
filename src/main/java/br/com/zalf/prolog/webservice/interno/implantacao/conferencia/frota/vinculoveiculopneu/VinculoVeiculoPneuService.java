package br.com.zalf.prolog.webservice.interno.implantacao.conferencia.frota.vinculoveiculopneu;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.commons.util.files.FileUtils;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interno.PrologInternalUser;
import br.com.zalf.prolog.webservice.interno.autenticacao.AutenticacaoInternaService;
import br.com.zalf.prolog.webservice.interno.implantacao.conferencia._model.TipoImport;
import br.com.zalf.prolog.webservice.interno.implantacao.conferencia.frota.vinculoveiculopneu._model.VinculoVeiculoPneu;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created on 31/08/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class VinculoVeiculoPneuService {
    @NotNull
    private static final String TAG = VinculoVeiculoPneuService.class.getSimpleName();
    @NotNull
    private final VinculoVeiculoPneuDao dao = Injection.provideVinculoVeiculoPneuDao();

    @NotNull
    public Response getVerificacaoVinculoVeiculoPneu(@NotNull final String authorization,
                                                     @NotNull final Long codEmpresa,
                                                     @NotNull final Long codUnidade,
                                                     @NotNull final InputStream fileInputStream,
                                                     @NotNull final FormDataContentDisposition fileDetail) {
        // Deve ficar fora do try/catch porque n??o queremos mascarar erros de autentifica????o com erros do processo de
        // import.
        final PrologInternalUser internalUser = new AutenticacaoInternaService().authorize(authorization);

        try {
            final File file = createFileFromVinculo(codUnidade, fileInputStream, fileDetail);
            readAndInsertVinculo(codEmpresa, codUnidade, internalUser.getUsername(), file);
            return Response.ok("Import da planilha de v??nculo realizado com sucesso!");
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao verificar planilha de v??nculo entre veiculos e pneus", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro com a conex??o");
        }
    }

    @NotNull
    private File createFileFromVinculo(@NotNull final Long codUnidade,
                                       @NotNull final InputStream fileInputStream,
                                       @NotNull final FormDataContentDisposition fileDetail) throws ProLogException {
        try {
            final String fileName = String.valueOf(Now.getUtcMillis()) + "_" + codUnidade
                    + "_" + fileDetail.getFileName().replace(" ", "_");
            final File tmpDir = FileUtils.getTempDir();
            final File file = new File(tmpDir, fileName);
            final FileOutputStream out = new FileOutputStream(file);
            IOUtils.copy(fileInputStream, out);
            IOUtils.closeQuietly(out);
            return file;
        } catch (final IOException e) {
            Log.e(TAG, "Erro ao ler arquivo de v??nculos", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao verificar dados, tente novamente");
        }
    }

    private void readAndInsertVinculo(@NotNull final Long codEmpresa,
                                      @NotNull final Long codUnidade,
                                      @NotNull final String usuario,
                                      @NotNull final File file) throws ProLogException {
        try {
            final List<VinculoVeiculoPneu> vinculoVeiculoPneuPlanilha =
                    VinculoVeiculoPneuPlanilhaReader.readListFromCsvFilePath(file);
            final String jsonPlanilha = GsonUtils.getGson().toJson(vinculoVeiculoPneuPlanilha);
            dao.importPlanilhaVinculoVeiculoPneu(codEmpresa, codUnidade, usuario, jsonPlanilha, TipoImport.VINCULO);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao enviar dados para o BD", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao verificar dados, tente novamente");
        }
    }
}
