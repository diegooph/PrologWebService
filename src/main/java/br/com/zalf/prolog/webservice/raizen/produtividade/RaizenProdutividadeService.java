package br.com.zalf.prolog.webservice.raizen.produtividade;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.Now;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.errorhandling.exception.RaizenProdutividadeException;
import br.com.zalf.prolog.webservice.seguranca.relato.model.Local;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.jetbrains.annotations.NotNull;
import software.amazon.ion.IonException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * Created on 04/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeService {

    private static final String TAG = RaizenProdutividadeService.class.getSimpleName();
    private final RaizenProdutividadeDao dao = Injection.provideRaizenProdutividadeDao();

    public Response uploadRaizenProdutividade(@NotNull final String token,
                                              @NotNull final Long codEmpresa,
                                              @NotNull final InputStream fileInputStream,
                                              @NotNull final FormDataContentDisposition fileDetail)
            throws RaizenProdutividadeException {
        final File file = createFileFromImport(codEmpresa, fileInputStream, fileDetail);
        readAndInsertImport(token, codEmpresa, file);
        return Response.ok("Upload realizado com sucesso!");
    }

    public Response insertRaizenProdutividade(@NotNull final String token,
                                              @NotNull final Long codEmpresa,
                                              @NotNull final RaizenProdutividadeItemInsert raizenProdutividadeItemInsert)
            throws RaizenProdutividadeException {
        Preconditions.checkNotNull(raizenProdutividadeItemInsert, "raizenProdutividadeItemInsert não pode ser nulla!");
        try {
            dao.insertRaizenProdutividadeItem(TokenCleaner.getOnlyToken(token), codEmpresa, raizenProdutividadeItemInsert);
            return Response.ok("Produtividade cadastrada com sucesso");
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao cadastrar/alterar a produtividade", e);
            throw new RaizenProdutividadeException(
                    "Não foi possível cadastrar este item, tente novamente",
                    "Erro ao cadastrar a produtividade",
                    e);
        }
    }

    public Response updateRaizenProdutividade(@NotNull final String token,
                                              @NotNull final Long codEmpresa,
                                              @NotNull final RaizenProdutividadeItemInsert updateRaizenProdutividadeItemInsert)
            throws RaizenProdutividadeException {
        Preconditions.checkNotNull(updateRaizenProdutividadeItemInsert, "Produtividade não pode ser nulla!");
        try {
            dao.updateRaizenProdutividadeItem(TokenCleaner.getOnlyToken(token), codEmpresa, updateRaizenProdutividadeItemInsert);
            return Response.ok("Produtividade alterada com sucesso");
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao alterar a produtividade");
            throw new RaizenProdutividadeException(
                    "Não foi possível cadastrar este item, tente novamente",
                    "Erro ao cadastrar a produtividade",
                    e);
        }
    }

    public List<RaizenProdutividade> getRaizenProdutividade(@NotNull final Long codEmpresa,
                                                            @NotNull final String data) throws RaizenProdutividadeException {
        try {
            return dao.getRaizenProdutividade(
                    codEmpresa,
                    ProLogDateParser.validateAndParse(data));
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar produtividade", e);
            throw new RaizenProdutividadeException(
                    "Não foi possível buscar a produtividade, tente novamente",
                    "Erro ao buscar produtividade",
                    e);
        }
    }

    public Response deleteRaizenProdutividade(@NotNull final Long codEmpresa,
                                              @NotNull final List<Long> codRaizenProdutividades) throws RaizenProdutividadeException {
        try {
            dao.deleteRaizenProdutividadeItens(codEmpresa, codRaizenProdutividades);
            return Response.ok("Escalas deletadas com sucesso!");
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao deletar os itens da produtividade", e);
            throw new RaizenProdutividadeException(
                    "Não foi possível deletar estes itens, tente novamente",
                    "Erro ao deletar os itens",
                    e);
        }
    }


    private File createFileFromImport(@NotNull final Long codEmpresa,
                                      @NotNull final InputStream fileInputStream,
                                      @NotNull final FormDataContentDisposition fileDetail)
            throws RaizenProdutividadeException {
        try {
            final String fileName = String.valueOf(Now.utcMillis()) + "_" + codEmpresa
                    + "_" + fileDetail.getFileName().replace(" ", "_");
            // Pasta temporária
            final File tmpDIr = Files.createTempDir();
            final File file = new File(tmpDIr, fileName);
            final FileOutputStream out = new FileOutputStream(file);
            IOUtils.copy(fileInputStream, out);
            IOUtils.closeQuietly(out);
            return file;
        } catch (IOException e) {
            Log.e(TAG, "Erro ao ler arquivo binário no import: " + codEmpresa);
            throw new RaizenProdutividadeException(
                    "Arquivo importado possui inconsistências",
                    "Erro ao ler arquivo binário no import",
                    e);
        }
    }

    private void readAndInsertImport(@NotNull final String token,
                                     @NotNull final Long codEmpresa,
                                     @NotNull final File file) throws RaizenProdutividadeException {
        try {
            final List<RaizenProdutividadeItemInsert> raizenProdutividadeItens = RaizenProdutividadeReader.readListFromCsvFilePath(file);
            dao.insertOrUpdateProdutividadeRaizen(TokenCleaner.getOnlyToken(token), codEmpresa, raizenProdutividadeItens);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao inserir dados da escala no BD", e);
            throw new RaizenProdutividadeException(
                    "Não foi possível inserir os dados no banco de dados, tente novamente!",
                    "Erro ao inserir informações no banco",
                    e);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao ler arquivo no servidor", e);
            throw new RaizenProdutividadeException(
                    "O arquivo enviado está com problemas, tente novamente!",
                    "Erro ao ler arquivo no servidor",
                    e);
        }
    }


}
