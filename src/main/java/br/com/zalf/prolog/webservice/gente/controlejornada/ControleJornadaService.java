package br.com.zalf.prolog.webservice.gente.controlejornada;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.gente.controlejornada.OLD.DeprecatedControleIntervaloDao_2;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.EstadoVersaoIntervalo;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.IntervaloMarcacao;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.ResponseIntervalo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 08/11/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
class ControleJornadaService {
    @NotNull
    private static final String TAG = ControleJornadaService.class.getSimpleName();
    @NotNull
    private DeprecatedControleIntervaloDao_2 daoAntiga = Injection.provideControleJornadaDao();
    @NotNull
    private final ControleJornadaDao dao = Injection.provideControleJornadaDaoNovo();

    @SuppressWarnings("Duplicates")
    @NotNull
    ResponseIntervalo insertMarcacaoIntervalo(final long versaoDadosIntervalo,
                                              @NotNull final IntervaloMarcacao intervaloMarcacao,
                                              @Nullable final Integer versaoAppMomentoSincronizacao) {
        // Devemos salvar no objeto o parâmetro de versão capturado no Header da requisição.
        intervaloMarcacao.setVersaoAppMomentoSincronizacao(versaoAppMomentoSincronizacao);
        EstadoVersaoIntervalo estadoVersaoIntervalo = null;
        try {
            @SuppressWarnings({"OptionalGetWithoutIsPresent", "ConstantConditions"})
            final Long versaoDadosBanco =
                    daoAntiga.getVersaoDadosIntervaloByUnidade(intervaloMarcacao.getCodUnidade()).get();
            estadoVersaoIntervalo = versaoDadosIntervalo < versaoDadosBanco
                    ? EstadoVersaoIntervalo.VERSAO_DESATUALIZADA
                    : EstadoVersaoIntervalo.VERSAO_ATUALIZADA;
            final Long codIntervalo = dao.insertMarcacaoIntervalo(intervaloMarcacao);
            return ResponseIntervalo.ok(codIntervalo, "Intervalo inserido com sucesso", estadoVersaoIntervalo);
        } catch (Throwable e) {
            Log.e(TAG, String.format(
                    "Erro ao inserir ou atualizar um intervalo. \n" +
                            "versaoDadosIntervalo: %d", versaoDadosIntervalo),
                    e);
            return ResponseIntervalo.error("Erro ao inserir intervalo", estadoVersaoIntervalo);
        }
    }

    @Nullable
    IntervaloMarcacao getUltimaMarcacaoInicioNaoFechada(@NotNull final Long codUnidade,
                                                        @NotNull final Long cpfColaborador,
                                                        @NotNull final Long codTipoIntervalo) throws Throwable {
        try {
            return dao.getUltimaMarcacaoInicioNaoFechada(codUnidade, cpfColaborador, codTipoIntervalo);
        } catch (Throwable e) {
            Log.e(TAG, String.format("Erro ao buscar os intervalos em abertos de um colaborador. \n" +
                    "cpfColaborador: %d \n" +
                    "codTipoIntervalo: %d", cpfColaborador, codTipoIntervalo), e);
            throw e;
        }
    }
}
