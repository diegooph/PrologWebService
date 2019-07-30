package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo.model.insert.VeiculoPlanilhaReader;

import com.google.common.io.Files;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

/**
 * Created on 23/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class VeiculoConferenciaService  {
    private static final String TAG = VeiculoConferenciaService.class.getSimpleName();
    @NotNull
    private final VeiculoConferenciaDao dao = Injection.provideVeiculoConferenciaDao();

    @NotNull
    private final ProLogExceptionHandler exceptionHandler = Injection.provideProLogExceptionHandler();

    public Response uploadVeiculoPlanilha(@NotNull final Long codUnidade,
                                          @NotNull final InputStream fileInputStream)
            throws ProLogException {
        final File file = createFileFromImport(codUnidade, fileInputStream);
        readAndInsertImport(codUnidade, file);
        return Response.ok("Verificação realizada com sucesso!");
    }

    @NotNull
    private File createFileFromImport(@NotNull final Long codUnidade,
                                           @NotNull final InputStream fileInputStream) throws ProLogException {

        try {
            final String fileName = String.valueOf(Now.utcMillis()) + "_" + codUnidade;
            // Pasta temporária da JVM
            final File tmpDir = Files.createTempDir();
            final File file = new File(tmpDir, fileName);
            final FileOutputStream out = new FileOutputStream(file);
            IOUtils.copy(fileInputStream, out);
            IOUtils.closeQuietly(out);
            return file;
        } catch (IOException e) {
            Log.e(TAG, "Erro ao ler arquivo binário do import", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao verificar dados, tente novamente");
        }
    }

    private void readAndInsertImport(@NotNull final Long codUnidade,
                                     @NotNull final File file)
            throws ProLogException {
        try {

            final String jsonPlanilha = VeiculoPlanilhaReader.readListFromCsvFilePath(file);

            dao.verificarPlanilha(jsonPlanilha);

        } catch (SQLException e) {
            Log.e(TAG, "Erro ao enviar dados para o BD", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao verificar dados, tente novamente");
        }
        /*catch (Exception e) {
            Log.e(TAG, "Erro ao ler arquivo no servidor", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro verificar dados, tente novamente");
        } */
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
