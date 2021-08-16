package br.com.zalf.prolog.webservice.raizen.produtividade;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.autenticacao._model.token.TokenCleaner;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.commons.util.datetime.PrologDateParser;
import br.com.zalf.prolog.webservice.commons.util.files.FileUtils;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.raizen.produtividade.error.RaizenProdutividadeValidator;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividade;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividadeAgrupamento;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividadeIndividualHolder;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.insert.RaizenProdutividadeItemInsert;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.insert.RaizenProdutividadeReader;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItemVisualizacao;
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
    public Response uploadRaizenProdutividade(
            @NotNull final String token,
            @NotNull final Long codUnidade,
            @NotNull final InputStream fileInputStream,
            @NotNull final FormDataContentDisposition fileDetail) throws ProLogException {
        try {
            final File file = createFileFromImport(codUnidade, fileInputStream, fileDetail);
            final List<RaizenProdutividadeItemInsert> raizenProdutividadeItens = RaizenProdutividadeReader
                    .readListFromCsvFilePath(file);
            for (final RaizenProdutividadeItemInsert item : raizenProdutividadeItens) {
                RaizenProdutividadeValidator.validacaoAtributosRaizenProdutividade(item);
                // O código da unidade vem no path pois os itens são importados através de arquivo.
                item.setCodUnidade(codUnidade);
            }
            dao.insertOrUpdateProdutividadeRaizen(
                    TokenCleaner.getOnlyToken(token),
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
            @NotNull final RaizenProdutividadeItemInsert raizenProdutividadeItemInsert) throws ProLogException {
        try {
            RaizenProdutividadeValidator.validacaoAtributosRaizenProdutividade(raizenProdutividadeItemInsert);
            dao.insertRaizenProdutividadeItem(
                    TokenCleaner.getOnlyToken(token),
                    raizenProdutividadeItemInsert);
            return Response.ok("Produtividade cadastrada com sucesso");
        } catch (final Throwable e) {
            final String errorMessage = "Não foi possível cadastrar este item, tente novamente";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    @NotNull
    public Response updateRaizenProdutividade(
            @NotNull final String token,
            @NotNull final Long codItem,
            @NotNull final RaizenProdutividadeItemInsert updateRaizenProdutividadeItemInsert) throws ProLogException {
        try {
            RaizenProdutividadeValidator.validacaoAtributosRaizenProdutividade(updateRaizenProdutividadeItemInsert);
            dao.updateRaizenProdutividadeItem(
                    TokenCleaner.getOnlyToken(token),
                    codItem,
                    updateRaizenProdutividadeItemInsert);
            return Response.ok("Produtividade alterada com sucesso");
        } catch (final Throwable e) {
            final String errorMessage = "Não foi possível atualizar este item, tente novamente";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    @NotNull
    public List<RaizenProdutividade> getRaizenProdutividade(@NotNull final Long codUnidade,
                                                            @NotNull final String dataInicial,
                                                            @NotNull final String dataFinal,
                                                            @NotNull final String agrupamento) throws ProLogException {
        try {
            final RaizenProdutividadeAgrupamento tipoAgrupamento =
                    RaizenProdutividadeAgrupamento.fromString(agrupamento);
            final List<RaizenProdutividade> itens;
            switch (tipoAgrupamento) {
                case POR_COLABORADOR:
                    itens = dao.getRaizenProdutividadeColaborador(
                            codUnidade,
                            PrologDateParser.toLocalDate(dataInicial),
                            PrologDateParser.toLocalDate(dataFinal));
                    break;
                case POR_DATA:
                    itens = dao.getRaizenProdutividadeData(
                            codUnidade,
                            PrologDateParser.toLocalDate(dataInicial),
                            PrologDateParser.toLocalDate(dataFinal));
                    break;
                default:
                    throw new IllegalStateException();
            }
            itens.forEach(RaizenProdutividade::calculaItensNaoCadastrados);
            return itens;
        } catch (final Throwable e) {
            final String errorMessage = "Não foi possível buscar a produtividade, tente novamente";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    @NotNull
    public RaizenProdutividadeItemVisualizacao getRaizenProdutividadeItem(@NotNull final Long codItem)
            throws ProLogException {
        try {
            return dao.getRaizenProdutividadeItemVisualizacao(codItem);
        } catch (final Throwable e) {
            final String errorMessage = "Não foi possível buscar o item, tente novamente";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    @NotNull
    public RaizenProdutividadeIndividualHolder getRaizenProdutividadeIndividual(@NotNull final Long codUnidade,
                                                                                @NotNull final Long codColaborador,
                                                                                final int mes,
                                                                                final int ano) throws ProLogException {
        try {
            return dao.getRaizenProdutividadeIndividual(
                    codUnidade,
                    codColaborador,
                    mes,
                    ano);
        } catch (final Throwable e) {
            final String errorMessage = "Não foi possível buscar a produtividade, tente novamente";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    @NotNull
    public Response deleteRaizenProdutividade(@NotNull final List<Long> codRaizenProdutividades) throws ProLogException {
        try {
            dao.deleteRaizenProdutividadeItens(codRaizenProdutividades);
            return Response.ok("Produtividades deletadas com sucesso!");
        } catch (final Throwable e) {
            final String errorMessage = "Não foi possível deletar estes itens, tente novamente";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    @NotNull
    @SuppressWarnings("Duplicates")
    private File createFileFromImport(@NotNull final Long codUnidade,
                                      @NotNull final InputStream fileInputStream,
                                      @NotNull final FormDataContentDisposition fileDetail) throws Throwable {
        final String fileName = String.valueOf(Now.getUtcMillis()) + "_" + codUnidade
                + "_" + fileDetail.getFileName().replace(" ", "_");
        // Pasta temporária
        final File tmpDir = FileUtils.getTempDir();
        final File file = new File(tmpDir, fileName);
        final FileOutputStream out = new FileOutputStream(file);
        IOUtils.copy(fileInputStream, out);
        IOUtils.closeQuietly(out);
        return file;
    }
}