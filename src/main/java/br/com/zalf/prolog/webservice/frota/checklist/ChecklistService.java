package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.FiltroRegionalUnidadeChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.integracao.router.RouterChecklists;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Classe ChecklistService responsável por comunicar-se com a interface DAO
 */
public final class ChecklistService {
    private static final String TAG = ChecklistService.class.getSimpleName();
    @NotNull
    private final ChecklistDao dao = Injection.provideChecklistDao();

    @NotNull
    public Long insert(@NotNull final String userToken,
                       @NotNull final ChecklistInsercao checklist) throws ProLogException {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .insertChecklist(checklist, false, true);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir um checklist", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir checklist, tente novamente");
        }
    }

    @NotNull
    public FiltroRegionalUnidadeChecklist getRegionaisUnidadesSelecao(@NotNull final Long codColaborador) {
        try {
            return dao.getRegionaisUnidadesSelecao(codColaborador);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar o regionais e unidades para seleção\n" +
                    "codColaborador: %d", codColaborador), throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar unidades, tente novamente");
        }
    }

    @NotNull
    public Checklist getByCod(@NotNull final Long codigo, @NotNull final String userToken) {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getChecklistByCodigo(codigo);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar um checklist específico: " + codigo, throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar checklist, tente novamente");
        }
    }

    public List<Checklist> getAll(Long codUnidade,
                                  Long codEquipe,
                                  Long codTipoVeiculo,
                                  String placaVeiculo,
                                  long dataInicial,
                                  long dataFinal,
                                  int limit,
                                  long offset,
                                  boolean resumido,
                                  String userToken) {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getTodosChecklists(codUnidade, codEquipe, codTipoVeiculo, placaVeiculo, dataInicial, dataFinal,
                            limit, offset, resumido);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar checklists", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar checklists, tente novamente");
        }
    }

    public List<Checklist> getByColaborador(Long cpf, Long dataInicial, Long dataFinal, int limit, long offset,
                                            boolean resumido, String userToken) {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getChecklistsByColaborador(cpf, dataInicial, dataFinal, limit, offset, resumido);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar os checklists de um colaborador específico", e);
            throw new RuntimeException("Erro ao buscar checklists para o colaborador: " + cpf);
        }
    }

    @NotNull
    public DeprecatedFarolChecklist getFarolChecklist(Long codUnidade,
                                                      String dataInicial,
                                                      String dataFinal,
                                                      boolean itensCriticosRetroativos,
                                                      String userToken) throws ProLogException {
        return internalGetFarolChecklist(
                codUnidade,
                ProLogDateParser.toLocalDate(dataInicial),
                ProLogDateParser.toLocalDate(dataFinal),
                itensCriticosRetroativos,
                userToken);
    }

    @NotNull
    public DeprecatedFarolChecklist getFarolChecklist(@NotNull final Long codUnidade,
                                            final boolean itensCriticosRetroativos,
                                            @NotNull final String userToken) throws ProLogException {
        LocalDate hojeComTz = null;
        try {
            hojeComTz = Now
                    .zonedDateTimeTzAware(TimeZoneManager.getZoneIdForCodUnidade(codUnidade))
                    .toLocalDate();
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar TZ do cliente para gerar farol do checklist.\n" +
                    "Unidade: " + codUnidade, throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar farol, tente novamente");
        }
        return internalGetFarolChecklist(codUnidade, hojeComTz, hojeComTz, itensCriticosRetroativos, userToken);
    }

    public boolean getChecklistDiferentesUnidadesAtivoEmpresa(@NotNull final Long codEmpresa) {
        try {
            return dao.getChecklistDiferentesUnidadesAtivoEmpresa(codEmpresa);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao verificar se empresa está bloqueada para realizar o checklist de " +
                    "diferentes unidades", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    // Mensagem propositalmente genérica para evitar de mostrar um erro sem sentido para o usuário
                    // quando ele tentar fazer Login, já que atualmente esse método é usado apenas no login.
                    .map(t, "Algo deu errado, tente novamente");
        }
    }

    @NotNull
    private DeprecatedFarolChecklist internalGetFarolChecklist(@NotNull final Long codUnidade,
                                                               @NotNull final LocalDate dataInicial,
                                                               @NotNull final LocalDate dataFinal,
                                                               final boolean itensCriticosRetroativos,
                                                               @NotNull final String userToken) throws ProLogException {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getFarolChecklist(
                            codUnidade,
                            dataInicial,
                            dataFinal,
                            itensCriticosRetroativos);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar o farol de realização dos checklists", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar o farol do checklist, tente novamente");
        }
    }
}