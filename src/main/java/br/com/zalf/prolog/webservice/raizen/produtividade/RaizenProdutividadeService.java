package br.com.zalf.prolog.webservice.raizen.produtividade;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.raizen.produtividade.error.RaizenProdutividadeValidator;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividade;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividadeAgrupamento;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividadeIndividualHolder;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.insert.RaizenProdutividadeItemInsert;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.insert.RaizenProdutividadeReader;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItemVisualizacao;
import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created on 04/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeService {
    private static final String TAG = RaizenProdutividadeService.class.getSimpleName();
    @NotNull
    private final RaizenProdutividadeDao dao = Injection.provideRaizenProdutividadeDao();
    @NotNull
    private final ProLogExceptionHandler exceptionHandler = Injection.provideProLogExceptionHandler();

    @NotNull
    public Response uploadRaizenProdutividade(@NotNull final String token,
                                              @NotNull final Long codEmpresa,
                                              @NotNull final InputStream fileInputStream,
                                              @NotNull final FormDataContentDisposition fileDetail) throws ProLogException {
        try {
            final File file = createFileFromImport(codEmpresa, fileInputStream, fileDetail);
            final List<RaizenProdutividadeItemInsert> raizenProdutividadeItens = RaizenProdutividadeReader
                    .readListFromCsvFilePath(file);
            for (RaizenProdutividadeItemInsert item : raizenProdutividadeItens) {
                RaizenProdutividadeValidator.validacaoAtributosRaizenProdutividade(item);
            }
            dao.insertOrUpdateProdutividadeRaizen(
                    TokenCleaner.getOnlyToken(token),
                    codEmpresa,
                    raizenProdutividadeItens);
            return Response.ok("Upload realizado com sucesso!");
        } catch (final Throwable e) {
            final String errorMessage = "Não foi possível inserir os dados no banco de dados, tente novamente!";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    @NotNull
    public Response insertRaizenProdutividade(
            @NotNull final String token,
            @NotNull final Long codEmpresa,
            @NotNull final RaizenProdutividadeItemInsert raizenProdutividadeItemInsert) throws ProLogException {
        try {
            RaizenProdutividadeValidator.validacaoAtributosRaizenProdutividade(raizenProdutividadeItemInsert);
            dao.insertRaizenProdutividadeItem(
                    TokenCleaner.getOnlyToken(token),
                    codEmpresa,
                    raizenProdutividadeItemInsert);
            return Response.ok("Produtividade cadastrada com sucesso");
        } catch (Throwable e) {
            final String errorMessage = "Não foi possível cadastrar este item, tente novamente";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    @NotNull
    public Response updateRaizenProdutividade(
            @NotNull final String token,
            @NotNull final Long codEmpresa,
            @NotNull final RaizenProdutividadeItemInsert updateRaizenProdutividadeItemInsert) throws ProLogException {
        try {
            RaizenProdutividadeValidator.validacaoAtributosRaizenProdutividade(updateRaizenProdutividadeItemInsert);
            dao.updateRaizenProdutividadeItem(
                    TokenCleaner.getOnlyToken(token),
                    codEmpresa,
                    updateRaizenProdutividadeItemInsert);
            return Response.ok("Produtividade alterada com sucesso");
        } catch (Throwable e) {
            final String errorMessage = "Não foi possível atualizar este item, tente novamente";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    @NotNull
    public List<RaizenProdutividade> getRaizenProdutividade(@NotNull final Long codEmpresa,
                                                            @NotNull final String dataInicial,
                                                            @NotNull final String dataFinal,
                                                            @NotNull final String agrupamento) throws ProLogException {
        try {
            final RaizenProdutividadeAgrupamento tipoAgrupamento = RaizenProdutividadeAgrupamento.fromString(agrupamento);
            switch (tipoAgrupamento) {
                case POR_COLABORADOR:
                    return dao.getRaizenProdutividadeColaborador(
                            codEmpresa,
                            ProLogDateParser.toLocalDate(dataInicial),
                            ProLogDateParser.toLocalDate(dataFinal));
                case POR_DATA:
                    return dao.getRaizenProdutividadeData(
                            codEmpresa,
                            ProLogDateParser.toLocalDate(dataInicial),
                            ProLogDateParser.toLocalDate(dataFinal));
                default:
                    throw new IllegalStateException();

            }
        } catch (final Throwable e) {
            final String errorMessage = "Não foi possível buscar a produtividade, tente novamente";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    @NotNull
    public RaizenProdutividadeItemVisualizacao getRaizenProdutividadeItem(
            @NotNull final Long codEmpresa,
            @NotNull final Long codItem) throws ProLogException {
        try {
            return dao.getRaizenProdutividadeItemVisualizacao(codEmpresa, codItem);
        } catch (final Throwable e) {
            final String errorMessage = "Não foi possível buscar o item, tente novamente";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    @NotNull
    public RaizenProdutividadeIndividualHolder getRaizenProdutividadeIndividual(@NotNull final Long codColaborador,
                                                                                final int mes,
                                                                                final int ano) throws ProLogException {
        try {
            return dao.getRaizenProdutividadeIndividual(
                    codColaborador,
                    mes,
                    ano);
        } catch (Throwable e) {
            final String errorMessage = "Não foi possível buscar a produtividade, tente novamente";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    @NotNull
    public Response deleteRaizenProdutividade(@NotNull final Long codEmpresa,
                                              @NotNull final List<Long> codRaizenProdutividades) throws ProLogException {
        try {
            dao.deleteRaizenProdutividadeItens(codEmpresa, codRaizenProdutividades);
            return Response.ok("Produtividades deletadas com sucesso!");
        } catch (Throwable e) {
            final String errorMessage = "Não foi possível deletar estes itens, tente novamente";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    @NotNull
    @SuppressWarnings("Duplicates")
    private File createFileFromImport(@NotNull final Long codEmpresa,
                                      @NotNull final InputStream fileInputStream,
                                      @NotNull final FormDataContentDisposition fileDetail) throws Throwable {
        final String fileName = String.valueOf(Now.utcMillis()) + "_" + codEmpresa
                + "_" + fileDetail.getFileName().replace(" ", "_");
        // Pasta temporária
        final File tmpDir = Files.createTempDir();
        final File file = new File(tmpDir, fileName);
        final FileOutputStream out = new FileOutputStream(file);
        IOUtils.copy(fileInputStream, out);
        IOUtils.closeQuietly(out);
        return file;
    }
}