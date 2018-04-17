package br.com.zalf.prolog.webservice.imports.escala_diaria;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.Now;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ParseDadosEscalaException;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * Created on 10/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class EscalaDiariaService {

    private static final String TAG = EscalaDiariaService.class.getSimpleName();
    private final EscalaDiariaDao dao = Injection.provideEscalaDiariaDao();

    public Response uploadMapa(@NotNull final String token,
                               @NotNull final Long codUnidade,
                               @NotNull final InputStream fileInputStream,
                               @NotNull final FormDataContentDisposition fileDetail)
            throws ParseDadosEscalaException {
        final File file = createFileFromImport(codUnidade, fileInputStream, fileDetail);
        readAndInsertImport(token, codUnidade, file.getPath());
        return Response.ok("Upload realizado com sucesso!");
    }

    public Response insertOrUpdateEscalaDiaria(@NotNull final String token,
                                               @NotNull final Long codUnidade,
                                               @NotNull final EscalaDiariaItem escalaDiariaItem,
                                               boolean isInsert) throws ParseDadosEscalaException {
        Preconditions.checkNotNull(escalaDiariaItem, "escalaDiariaItem não pode ser nulla!");
        try {
            dao.insertOrUpdateEscalaDiariaItem(token, codUnidade, escalaDiariaItem, isInsert);
            return Response.ok("Item escala diária cadastrado com sucesso");
        } catch (SQLException e) {
            throw new ParseDadosEscalaException(
                    javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode(),
                    "O arquivo enviado está com problemas, tente novamente",
                    "Erro ao ler arquivo no servidor",
                    e);
        }
    }

    public List<EscalaDiaria> getEscalasDiarias(@NotNull final Long codUnidade,
                                                @NotNull final String dataInicial,
                                                @NotNull final String dataFinal) throws ParseDadosEscalaException {
        try {
            return dao.getEscalasDiarias(
                    codUnidade,
                    ProLogDateParser.validateAndParse(dataInicial),
                    ProLogDateParser.validateAndParse(dataFinal));
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar Escalas Diarias para a Unidade: " + codUnidade, e);
            throw new ParseDadosEscalaException(
                    javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode(),
                    "O arquivo enviado está com problemas, tente novamente",
                    "Erro ao ler arquivo no servidor",
                    e);
        }
    }

    public EscalaDiariaItem getEscalaDiariaItem(@NotNull final Long codUnidade,
                                                @NotNull final Long codEscala) throws ParseDadosEscalaException {
        try {
            return dao.getEscalaDiariaItem(codUnidade, codEscala);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar Escala Diaria Item de codigo: " + codEscala, e);
            throw new ParseDadosEscalaException(
                    javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode(),
                    "O arquivo enviado está com problemas, tente novamente",
                    "Erro ao ler arquivo no servidor",
                    e);
        }
    }

    public Response deleteEscalaDiariaItens(@NotNull final Long codUnidade,
                                            @NotNull final List<Long> codEscalas) throws ParseDadosEscalaException {
        try {
            dao.deleteEscalaDiariaItens(codUnidade, codEscalas);
            return Response.ok("Escalas deletadas com sucesso!");
        } catch (SQLException e) {
            throw new ParseDadosEscalaException(
                    javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode(),
                    "O arquivo enviado está com problemas, tente novamente",
                    "Erro ao ler arquivo no servidor",
                    e);
        }
    }

    private File createFileFromImport(@NotNull final Long codUnidade,
                                      @NotNull final InputStream fileInputStream,
                                      @NotNull final FormDataContentDisposition fileDetail) throws ParseDadosEscalaException {
        try {
            final String fileName = String.valueOf(Now.utcMillis()) + "_" + codUnidade
                    + "_" + fileDetail.getFileName().replace(" ", "_");
            // Pasta temporária da JVM
            final File tmpDir = Files.createTempDir();
            final File file = new File(tmpDir, fileName);
            final FileOutputStream out = new FileOutputStream(file);
            IOUtils.copy(fileInputStream, out);
            IOUtils.closeQuietly(out);
            return file;
        } catch (IOException e) {
            Log.e(TAG, "Erro ao ler arquivo binário do import", e);
            throw new ParseDadosEscalaException(
                    javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode(),
                    "O arquivo importado possui inconsistências",
                    "Erro ao ler arquivo binário do import",
                    e);
        }
    }

    private void readAndInsertImport(@NotNull final String token,
                                     @NotNull final Long codUnidade,
                                     @NotNull final String path)
            throws ParseDadosEscalaException {
        try {
            final List<EscalaDiariaItem> escalaItens = EscalaDiariaReader.readListFromCsvFilePath(path);
            dao.insertOrUpdateEscalaDiaria(token, codUnidade, escalaItens);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao inserir dados da escala no BD", e);
            throw new ParseDadosEscalaException(
                    javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode(),
                    "",
                    "",
                    e);
        } catch (IOException e) {
            Log.e(TAG, "Erro ao ler arquivo no servidor", e);
            throw new ParseDadosEscalaException(
                    javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode(),
                    "O arquivo enviado está com problemas, tente novamente",
                    "Erro ao ler arquivo no servidor",
                    e);
        }
    }
}
