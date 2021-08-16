package br.com.zalf.prolog.webservice.entrega.escaladiaria;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.autenticacao._model.token.TokenCleaner;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.commons.util.datetime.PrologDateParser;
import br.com.zalf.prolog.webservice.commons.util.files.FileUtils;
import br.com.zalf.prolog.webservice.errorhandling.exception.EscalaDiariaException;
import com.google.common.base.Preconditions;
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

    public Response uploadEscala(@NotNull final String token,
                                 @NotNull final Long codUnidade,
                                 @NotNull final InputStream fileInputStream,
                                 @NotNull final FormDataContentDisposition fileDetail)
            throws EscalaDiariaException {
        final File file = createFileFromImport(codUnidade, fileInputStream, fileDetail);
        readAndInsertImport(token, codUnidade, file);
        return Response.ok("Upload realizado com sucesso!");
    }

    public Response insertEscalaDiaria(@NotNull final String token,
                                       @NotNull final Long codUnidade,
                                       @NotNull final EscalaDiariaItem escalaDiariaItem) throws EscalaDiariaException {
        Preconditions.checkNotNull(escalaDiariaItem, "escalaDiariaItem não pode ser nulla!");
        try {
            dao.insertEscalaDiariaItem(TokenCleaner.getOnlyToken(token), codUnidade, escalaDiariaItem);
            return Response.ok("Item escala diária cadastrado com sucesso");
        } catch (final SQLException e) {
            Log.e(TAG, "Erro ao cadastrar/alterar o item escala diária na unidade: " + codUnidade, e);
            throw new EscalaDiariaException(
                    "Não foi possível cadastrar este item, tente novamente",
                    "Erro ao cadastrar o item escala diária",
                    e);
        }
    }

    public Response updateEscalaDiaria(@NotNull final String token,
                                       @NotNull final Long codUnidade,
                                       @NotNull final EscalaDiariaItem escalaDiariaItem) throws EscalaDiariaException {
        Preconditions.checkNotNull(escalaDiariaItem, "escalaDiariaItem não pode ser nulla!");
        try {
            dao.updateEscalaDiariaItem(TokenCleaner.getOnlyToken(token), codUnidade, escalaDiariaItem);
            return Response.ok("Item escala diária alterado com sucesso");
        } catch (final SQLException e) {
            Log.e(TAG, "Erro ao alterar o item escala diária na unidade: " + codUnidade, e);
            throw new EscalaDiariaException(
                    "Não foi possível cadastrar este item, tente novamente",
                    "Erro ao cadastrar o item escala diária",
                    e);
        }
    }

    public List<EscalaDiaria> getEscalasDiarias(@NotNull final Long codUnidade,
                                                @NotNull final String dataInicial,
                                                @NotNull final String dataFinal) throws EscalaDiariaException {
        try {
            return dao.getEscalasDiarias(
                    codUnidade,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal));
        } catch (final SQLException e) {
            Log.e(TAG, "Erro ao buscar Escalas Diarias para a Unidade: " + codUnidade, e);
            throw new EscalaDiariaException(
                    "Não foi possível buscar as escalas diárias, tente novamente",
                    "Erro ao buscar Escalas Diarias",
                    e);
        }
    }

    public EscalaDiariaItem getEscalaDiariaItem(@NotNull final Long codUnidade,
                                                @NotNull final Long codEscala) throws EscalaDiariaException {
        try {
            return dao.getEscalaDiariaItem(codUnidade, codEscala);
        } catch (final SQLException e) {
            Log.e(TAG, "Erro ao buscar Escala Diaria Item de codigo: " + codEscala, e);
            throw new EscalaDiariaException(
                    "Não foi possível recuperrar este item, tente novamente",
                    "Erro ao buscar o item",
                    e);
        }
    }

    public Response deleteEscalaDiariaItens(@NotNull final Long codUnidade,
                                            @NotNull final List<Long> codEscalas) throws EscalaDiariaException {
        try {
            dao.deleteEscalaDiariaItens(codUnidade, codEscalas);
            return Response.ok("Escalas deletadas com sucesso!");
        } catch (final SQLException e) {
            Log.e(TAG, "Erro ao deletar os itens da unidade: " + codUnidade, e);
            throw new EscalaDiariaException(
                    "Não foi possível deletar estes itens, tente novamente",
                    "Erro ao deletar os itens",
                    e);
        }
    }

    private File createFileFromImport(@NotNull final Long codUnidade,
                                      @NotNull final InputStream fileInputStream,
                                      @NotNull final FormDataContentDisposition fileDetail) throws EscalaDiariaException {
        try {
            final String fileName = String.valueOf(Now.getUtcMillis()) + "_" + codUnidade
                    + "_" + fileDetail.getFileName().replace(" ", "_");
            // Pasta temporária da JVM
            final File tmpDir = FileUtils.getTempDir();
            final File file = new File(tmpDir, fileName);
            final FileOutputStream out = new FileOutputStream(file);
            IOUtils.copy(fileInputStream, out);
            IOUtils.closeQuietly(out);
            return file;
        } catch (final IOException e) {
            Log.e(TAG, "Erro ao ler arquivo binário do import", e);
            throw new EscalaDiariaException(
                    "O arquivo importado possui inconsistências",
                    "Erro ao ler arquivo binário do import",
                    e);
        }
    }

    private void readAndInsertImport(@NotNull final String token,
                                     @NotNull final Long codUnidade,
                                     @NotNull final File file)
            throws EscalaDiariaException {
        try {
            final List<EscalaDiariaItem> escalaItens = EscalaDiariaReader.readListFromCsvFilePath(file);
            dao.insertOrUpdateEscalaDiaria(TokenCleaner.getOnlyToken(token), codUnidade, escalaItens);
        } catch (final SQLException e) {
            Log.e(TAG, "Erro ao inserir dados da escala no BD", e);
            throw new EscalaDiariaException(
                    "Não foi possível inserir os dados no banco de dados, tente novamente!",
                    "Erro ao inserir informações no banco",
                    e);
        } catch (final Exception e) {
            Log.e(TAG, "Erro ao ler arquivo no servidor", e);
            throw new EscalaDiariaException(
                    "O arquivo enviado está com problemas, tente novamente!",
                    "Erro ao ler arquivo no servidor",
                    e);
        }
    }
}
