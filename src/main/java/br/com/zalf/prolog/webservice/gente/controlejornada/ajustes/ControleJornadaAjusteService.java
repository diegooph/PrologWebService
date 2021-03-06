package br.com.zalf.prolog.webservice.gente.controlejornada.ajustes;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.autenticacao._model.token.TokenCleaner;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.PrologDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.*;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.exibicao.ConsolidadoMarcacoesDia;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.exibicao.MarcacaoColaboradorAjuste;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.historico.MarcacaoAjusteHistoricoExibicao;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.inconsistencias.MarcacaoInconsistencia;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.inconsistencias.TipoInconsistenciaMarcacao;
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
    public Response adicionarMarcacaoAjuste(@NotNull final String userToken,
                                            @NotNull final MarcacaoAjusteAdicao marcacaoAjuste) throws ProLogException {
        try {
            dao.adicionarMarcacaoAjuste(TokenCleaner.getOnlyToken(userToken), marcacaoAjuste);
            return Response.ok("Adi????o de marca????o realizada");
        } catch (final Throwable e) {
            final String msg = "Erro ao realizar adi????o de marca????o, tente novamente";
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
            return Response.ok("Adi????o das marca????es de in??cio e fim realizadas");
        } catch (final Throwable e) {
            final String msg = "Erro ao realizar adi????o das marca????es de in??cio e fim, tente novamente";
            Log.e(TAG, msg, e);
            throw exceptionHandler.map(e, msg);
        }
    }

    @NotNull
    public Response editarMarcacaoAjuste(@NotNull final String userToken,
                                         @NotNull final MarcacaoAjusteEdicao marcacaoAjuste) throws ProLogException {
        try {
            dao.editarMarcacaoAjuste(TokenCleaner.getOnlyToken(userToken), marcacaoAjuste);
            return Response.ok("Edi????o da marca????o realizada");
        } catch (final Throwable e) {
            final String msg = "Erro ao realizar edi????o da marca????o, tente novamente";
            Log.e(TAG, msg, e);
            throw exceptionHandler.map(e, msg);
        }
    }

    @NotNull
    public Response ativarMarcacaoAjuste(
            @NotNull final String userToken,
            @NotNull final MarcacaoAjusteAtivacao marcacaoAjuste) throws ProLogException {
        try {
            dao.ativarInativarMarcacaoAjuste(
                    TokenCleaner.getOnlyToken(userToken),
                    marcacaoAjuste,
                    marcacaoAjuste.getCodMarcacaoAtivacao(),
                    true);
            return Response.ok("Marca????o ativada com sucesso");
        } catch (final Throwable e) {
            final String msg = "Erro ao ativar a marca????o";
            Log.e(TAG, msg, e);
            throw exceptionHandler.map(e, msg);
        }
    }

    @NotNull
    public Response inativarMarcacaoAjuste(
            @NotNull final String userToken,
            @NotNull final MarcacaoAjusteInativacao marcacaoAjuste) throws ProLogException {
        try {
            dao.ativarInativarMarcacaoAjuste(
                    TokenCleaner.getOnlyToken(userToken),
                    marcacaoAjuste,
                    marcacaoAjuste.getCodMarcacaoInativacao(),
                    false);
            return Response.ok("Marca????o inativada com sucesso");
        } catch (final Throwable e) {
            final String msg = "Erro ao inativar a marca????o";
            Log.e(TAG, msg, e);
            throw exceptionHandler.map(e, msg);
        }
    }

    @NotNull
    public List<MarcacaoAjusteHistoricoExibicao> getHistoricoAjusteMarcacoes(
            @NotNull final List<Long> codMarcacoes) throws ProLogException {
        try {
            return dao.getHistoricoAjusteMarcacoes(codMarcacoes);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar hist??rico para as marca????es: " + codMarcacoes, e);
            throw exceptionHandler.map(e, "Erro ao buscar hist??rico das marca????es, tente novamente");
        }
    }

    @NotNull
    public List<MarcacaoInconsistencia> getInconsistenciasColaboradorDia(
            @NotNull final Long codColaborador,
            @NotNull final String dia,
            @NotNull final String tipoInconsistencia) throws ProLogException {
        try {
            return dao.getInconsistenciasColaboradorDia(
                    codColaborador,
                    PrologDateParser.toLocalDate(dia),
                    TipoInconsistenciaMarcacao.fromString(tipoInconsistencia));
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao buscar as inconsist??ncias.\n" +
                    "codColaborador: %d\n" +
                    "dia: %s\n" +
                    "tipoInconsistencia: %s", codColaborador, dia, tipoInconsistencia), e);
            throw exceptionHandler.map(e, "Erro ao buscar as inconsist??ncias, tente novamente");
        }
    }

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
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            final String log = String.format("Erro ao buscar as marca????es:\n" +
                    "codUnidade: %d\n" +
                    "codTipoMarcacao %d\n" +
                    "codColaborador: %d\n" +
                    "dataInicial: %s\n" +
                    "dataFinal: %s", codUnidade, codTipoMarcacao, codColaborador, dataInicial, dataFinal);
            Log.e(TAG, log, e);
            throw exceptionHandler.map(e, "Erro ao buscar as marca????es");
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
                    PrologDateParser.toLocalDate(dia));
        } catch (final Throwable e) {
            final String log = String.format("Erro ao buscar as marca????es do colaborador:\n" +
                    "codColaborador: %d\n" +
                    "codTipoMarcacao: %d\n" +
                    "dia: %s\n", codColaborador, codTipoMarcacao, dia);
            Log.e(TAG, log, e);
            throw exceptionHandler.map(e, "Erro ao buscar as marca????es do colaborador");
        }
    }
}