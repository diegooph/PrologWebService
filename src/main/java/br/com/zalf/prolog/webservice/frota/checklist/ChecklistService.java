package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.FiltroRegionalUnidadeChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.integracao.router.RouterChecklists;
import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * Classe ChecklistService responsável por comunicar-se com a interface DAO
 */
public final class ChecklistService {
    private static final String TAG = ChecklistService.class.getSimpleName();
    @NotNull
    private final ChecklistDao dao = Injection.provideChecklistDao();

    public Long insert(@NotNull final String userToken, @NotNull final Checklist checklist) throws ProLogException {
        try {
            checklist.setData(Now.localDateTimeUtc());
            return RouterChecklists
                    .create(dao, userToken)
                    .insertChecklist(checklist);
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
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(
            @NotNull final Long codUnidade,
            @NotNull final Long codCargo,
            @NotNull final String userToken) {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codCargo);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar modelos de checklist e placas dos veículos\n" +
                    "codUnidade: %d\n" +
                    "codCargo: %d", codUnidade, codCargo), throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar modelos de checklist, tente novamente");
        }
    }

    @NotNull
    public NovoChecklistHolder getNovoChecklistHolder(@NotNull final Long codUnidadeModelo,
                                                      @NotNull final Long codModelo,
                                                      @NotNull final String placa,
                                                      final char tipoChecklist,
                                                      @NotNull final String userToken) {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getNovoChecklistHolder(codUnidadeModelo, codModelo, placa, tipoChecklist);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar o NovoChecklistHolder", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao iniciar checklist, tente novamente");
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
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar os checklists", e);
            return null;
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
        // TODO: Precisamos aplicar o tz do cliente
        final LocalDate hoje = ZonedDateTime.now(Clock.systemUTC()).withZoneSameInstant(ZoneId.of("America/Sao_Paulo")).toLocalDate();
        return internalGetFarolChecklist(codUnidade, hoje, hoje, itensCriticosRetroativos, userToken);
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