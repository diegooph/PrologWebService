package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.ChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.model.FiltroRegionalUnidadeChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.alteracao_logica.ChecklistsAlteracaoAcaoData;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.InfosChecklistInserido;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Contém os métodos para manipular os checklists no banco de dados
 */
public interface ChecklistDao {

    @NotNull
    InfosChecklistInserido insertChecklist(@NotNull final Connection conn,
                                           @NotNull final ChecklistInsercao checklist,
                                           final boolean foiOffline,
                                           final boolean deveAbrirOs) throws Throwable;

    @NotNull
    InfosChecklistInserido insertChecklist(@NotNull final ChecklistInsercao checklist,
                                           final boolean foiOffline,
                                           final boolean deveAbrirOs) throws Throwable;

    void insertMidiaPerguntaChecklistRealizado(@NotNull final String uuidMidia,
                                               @NotNull final Long codChecklist,
                                               @NotNull final Long codPergunta,
                                               @NotNull final String urlMidia) throws Throwable;

    void insertMidiaAlternativaChecklistRealizado(@NotNull final String uuidMidia,
                                                  @NotNull final Long codChecklist,
                                                  @NotNull final Long codAlternativa,
                                                  @NotNull final String urlMidia) throws Throwable;

    @NotNull
    Checklist getByCod(@NotNull final Long codChecklist) throws SQLException;

    @NotNull
    List<ChecklistListagem> getListagemByColaborador(@NotNull final Long codColaborador,
                                                     @NotNull final LocalDate dataInicial,
                                                     @NotNull final LocalDate dataFinal,
                                                     final int limit,
                                                     final long offset) throws Throwable;

    @NotNull
    List<ChecklistListagem> getListagem(@NotNull final Long codUnidade,
                                        @Nullable final Long codEquipe,
                                        @Nullable final Long codTipoVeiculo,
                                        @Nullable final Long codVeiculo,
                                        @NotNull final LocalDate dataInicial,
                                        @NotNull final LocalDate dataFinal,
                                        final int limit,
                                        final long offset) throws Throwable;

    @NotNull
    FiltroRegionalUnidadeChecklist getRegionaisUnidadesSelecao(@NotNull final Long codColaborador) throws Throwable;

    @NotNull
    DeprecatedFarolChecklist getFarolChecklist(@NotNull final Long codUnidade,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal,
                                               final boolean itensCriticosRetroativos) throws Throwable;

    boolean getChecklistDiferentesUnidadesAtivoEmpresa(@NotNull final Long codEmpresa) throws Throwable;

    @Deprecated
    List<Checklist> getByColaborador(@NotNull final Long cpf,
                                     @NotNull final Long dataInicial,
                                     @NotNull final Long dataFinal,
                                     final int limit,
                                     final long offset,
                                     final boolean resumido) throws SQLException;

    void deleteLogicoChecklistsAndOs(@NotNull final ChecklistsAlteracaoAcaoData checkListsDelecao,
                                     @NotNull final Long codigoColaborador) throws Throwable;
}