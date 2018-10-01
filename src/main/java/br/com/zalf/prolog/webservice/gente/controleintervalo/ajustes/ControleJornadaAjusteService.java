package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAdicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAdicaoInicioFim;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAtivacaoInativacao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteEdicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.ConsolidadoMarcacoesDia;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.MarcacaoColaboradorAjuste;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ControleJornadaAjusteService {
    private static final String TAG = ControleJornadaAjusteService.class.getSimpleName();
    @NotNull
    private final ControleJornadaAjusteDao dao = Injection.provideControleJornadaAjustesDao();
    @NotNull
    private final ProLogExceptionHandler exceptionHandler = Injection.provideProLogExceptionHandler();

    @NotNull
    List<ConsolidadoMarcacoesDia> getMarcacoesConsolidadasParaAjuste(
            @NotNull final Long codUnidade,
            @Nullable final Long codTipoMarcacao,
            @Nullable final Long codColaborador,
            @NotNull final String dataInicial,
            @NotNull final String dataFinal) throws ProLogException {
        try {
            return dao.getMarcacoesConsolidadasParaAjuste(
                    codUnidade,
                    codTipoMarcacao,
                    codColaborador,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            final String log = String.format("Erro ao buscar as marcações:\n" +
                    "codUnidade: %d\n" +
                    "codTipoMarcacao %d\n" +
                    "codColaborador: %d\n" +
                    "dataInicial: %s\n" +
                    "dataFinal: %s", codUnidade, codTipoMarcacao, codColaborador, dataInicial, dataFinal);
            Log.e(TAG, log, e);
            throw exceptionHandler.map(e, "Erro ao buscar as marcações");
        }
    }

    @NotNull
    List<MarcacaoColaboradorAjuste> getMarcacoesColaboradorParaAjuste(
            @NotNull final Long codColaborador,
            @Nullable final Long codTipoMarcacao,
            @NotNull final String dia) throws ProLogException {
        try {
            return dao.getMarcacoesColaboradorParaAjuste(
                    codColaborador,
                    codTipoMarcacao,
                    ProLogDateParser.toLocalDate(dia));
        } catch (final Throwable e) {
            final String log = String.format("Erro ao buscar as marcações do colaborador:\n" +
                    "codColaborador: %d\n" +
                    "codTipoMarcacao: %d\n" +
                    "dia: %s\n", codColaborador, codTipoMarcacao, dia);
            Log.e(TAG, log, e);
            throw exceptionHandler.map(e, "Erro ao buscar as marcações do colaborador");
        }
    }

    @NotNull
    public Response adicionarMarcacaoAjuste(@NotNull final String userToken,
                                            @NotNull final MarcacaoAjusteAdicao marcacaoAjuste) throws ProLogException {
        try {
            dao.adicionarMarcacaoAjuste(TokenCleaner.getOnlyToken(userToken), marcacaoAjuste);
            return Response.ok("Adição de marcação realizada");
        } catch (final Throwable e) {
            final String msg = "Erro ao realizar adição de marcação, tente novamente";
            Log.e(TAG, msg, e);
            throw exceptionHandler.map(e, msg);
        }
    }

    @NotNull
    public Response adicionarMarcacaoAjusteInicioFim(
            @NotNull final String userToken,
            @NotNull final MarcacaoAjusteAdicaoInicioFim marcacaoAjuste) throws ProLogException {
        try {
            dao.adicionarMarcacaoAjusteInicioFim(TokenCleaner.getOnlyToken(userToken), marcacaoAjuste);
            return Response.ok("Adição das marcações de início e fim realizadas");
        } catch (final Throwable e) {
            final String msg = "Erro ao realizar adição das marcações de início e fim, tente novamente";
            Log.e(TAG, msg, e);
            throw exceptionHandler.map(e, msg);
        }
    }

    @NotNull
    public Response editarMarcacaoAjuste(@NotNull final String userToken,
                                         @NotNull final MarcacaoAjusteEdicao marcacaoAjuste) throws ProLogException {
        try {
            dao.editarMarcacaoAjuste(TokenCleaner.getOnlyToken(userToken), marcacaoAjuste);
            return Response.ok("Edição da marcação realizada");
        } catch (final Throwable e) {
            final String msg = "Erro ao realizar edição da marcação, tente novamente";
            Log.e(TAG, msg, e);
            throw exceptionHandler.map(e, msg);
        }
    }

    @NotNull
    public Response ativarInativarMarcacaoAjuste(
            @NotNull final String userToken,
            @NotNull final MarcacaoAjusteAtivacaoInativacao marcacaoAjuste) throws ProLogException {
        try {
            dao.ativarInativarMarcacaoAjuste(TokenCleaner.getOnlyToken(userToken), marcacaoAjuste);
            return Response.ok(String.format("Marcação %s com sucesso",
                    marcacaoAjuste.isDeveAtivar() ? "ativada" : "desativada"));
        } catch (final Throwable e) {
            final String msg = String.format("Erro ao %s a marcação",
                    marcacaoAjuste.isDeveAtivar() ? "ativar" : "desativar");
            Log.e(TAG, msg, e);
            throw exceptionHandler.map(e, msg);
        }
    }
}