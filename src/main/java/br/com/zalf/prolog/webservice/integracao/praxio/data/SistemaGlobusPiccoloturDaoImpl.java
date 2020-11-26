package br.com.zalf.prolog.webservice.integracao.praxio.data;

import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created on 17/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class SistemaGlobusPiccoloturDaoImpl extends DatabaseConnection implements SistemaGlobusPiccoloturDao {
    @Override
    @NotNull
    public ChecklistToSyncGlobus getChecklistToSyncGlobus(@NotNull final Connection conn,
                                                          @NotNull final Long codChecklistProLog) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM PICCOLOTUR.FUNC_CHECK_OS_BUSCA_CHECKLIST_ITENS_NOK(F_COD_CHECKLIST_PROLOG => ?);");
            stmt.setLong(1, codChecklistProLog);
            rSet = stmt.executeQuery();
            ChecklistToSyncGlobus checklistToSyncGlobus = null;
            ChecklistItensNokGlobus checklistItensNokGlobus = null;
            PerguntaNokGlobus perguntaNokGlobus = null;
            final List<PerguntaNokGlobus> perguntasNok = new ArrayList<>();
            Long codPerguntaAnterior = null;
            Long codPerguntaAtual;
            while (rSet.next()) {
                if (checklistItensNokGlobus == null) {
                    checklistItensNokGlobus = new ChecklistItensNokGlobus(
                            rSet.getLong("COD_UNIDADE_CHECKLIST"),
                            codChecklistProLog,
                            rSet.getLong("COD_MODELO_CHECKLIST"),
                            rSet.getString("CPF_COLABORADOR_REALIZACAO"),
                            rSet.getString("PLACA_VEICULO_CHECKLIST"),
                            rSet.getLong("KM_COLETADO_CHECKLIST"),
                            TipoChecklistGlobus.fromString(rSet.getString("TIPO_CHECKLIST")),
                            rSet.getObject("DATA_HORA_REALIZACAO", LocalDateTime.class),
                            perguntasNok);
                    // Cria objeto que será retornado contendo as informações do checklist para sincronizar.
                    checklistToSyncGlobus = new ChecklistToSyncGlobus(
                            rSet.getLong("COD_MODELO_CHECKLIST"),
                            rSet.getLong("COD_VERSAO_MODELO_CHECKLIST"),
                            rSet.getString("PLACA_VEICULO_CHECKLIST"),
                            checklistItensNokGlobus);

                    if (rSet.getInt("TOTAL_ALTERNATIVAS_NOK") <= 0) {
                        break;
                    }
                    perguntaNokGlobus = createPerguntaNokGlobus(rSet);
                    perguntasNok.add(perguntaNokGlobus);
                }

                codPerguntaAtual = rSet.getLong("COD_CONTEXTO_PERGUNTA_NOK");
                if (codPerguntaAnterior == null) {
                    codPerguntaAnterior = codPerguntaAtual;
                }

                if (codPerguntaAnterior.equals(codPerguntaAtual)) {
                    // Cria mais uma alternativa na pergunta atual.
                    perguntaNokGlobus.getAlternativasNok().add(createAlternativaNokGlobus(rSet));
                } else {
                    // Cria nova pergunta.
                    perguntaNokGlobus = createPerguntaNokGlobus(rSet);
                    perguntasNok.add(perguntaNokGlobus);
                    // Cria primeira alternativa da nova pergunta.
                    perguntaNokGlobus.getAlternativasNok().add(createAlternativaNokGlobus(rSet));
                }
                codPerguntaAnterior = codPerguntaAtual;
            }
            if (checklistToSyncGlobus == null) {
                throw new IllegalStateException("Nenhum checklist existente para o código:\n" +
                        "codChecklistProLog: " + codChecklistProLog);
            }
            return checklistToSyncGlobus;
        } finally {
            close(stmt, rSet);
        }
    }

    @Override
    public void insertItensNokPendentesParaSincronizar(
            @NotNull final Connection conn,
            @NotNull final Long codChecklistParaSincronizar) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareCall(
                    "{CALL PICCOLOTUR.FUNC_CHECK_OS_INSERE_CHECKLIST_PENDENTE_SINCRONIA(F_COD_CHECKLIST => ?)}");
            stmt.setLong(1, codChecklistParaSincronizar);
            stmt.execute();
        } finally {
            close(stmt);
        }
    }

    @Override
    public void insertItensNokEnviadosGlobus(
            @NotNull final Connection conn,
            @NotNull final ChecklistItensNokGlobus checklistItensNokGlobus) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("select * " +
                    "from piccolotur.func_check_os_insere_itens_nok_enviados_globus(" +
                    "f_cod_unidade => ?, " +
                    "f_placa_veiculo => ?, " +
                    "f_cpf_colaborador => ?, " +
                    "f_cod_checklist_realizado => ?, " +
                    "f_cod_pergunta => ?, " +
                    "f_cod_contexto_pergunta => ?, " +
                    "f_cod_alternativa => ?, " +
                    "f_cod_contexto_alternativa => ?, " +
                    "f_data_hora_envio => ?);");
            stmt.setLong(1, checklistItensNokGlobus.getCodUnidadeChecklist());
            stmt.setString(2, checklistItensNokGlobus.getPlacaVeiculoChecklist());
            stmt.setLong(3, Colaborador.formatCpf(checklistItensNokGlobus.getCpfColaboradorRealizacao()));
            stmt.setLong(4, checklistItensNokGlobus.getCodChecklistRealizado());
            for (final PerguntaNokGlobus perguntaNokGlobus : checklistItensNokGlobus.getPerguntasNok()) {
                for (final AlternativaNokGlobus alternativaNokGlobus : perguntaNokGlobus.getAlternativasNok()) {
                    stmt.setLong(5, perguntaNokGlobus.getCodPerguntaNok());
                    stmt.setLong(6, perguntaNokGlobus.getCodContextoPerguntaNok());
                    stmt.setLong(7, alternativaNokGlobus.getCodAlternativaNok());
                    stmt.setLong(8, alternativaNokGlobus.getCodContextoAlternativaNok());
                    stmt.setObject(9, Now.offsetDateTimeUtc());
                    stmt.addBatch();
                }
            }
            final boolean todasInsercoesOk = IntStream
                    .of(stmt.executeBatch())
                    .allMatch(rowsAffectedCount -> rowsAffectedCount == 0);
            if (!todasInsercoesOk) {
                throw new IllegalStateException(
                        "[ERRO INTEGRAÇÃO]: Erro ao inserir algum item NOK que seria enviado ao Globus");
            }
        } finally {
            close(stmt);
        }
    }

    @Override
    public void marcaChecklistNaoPrecisaSincronizar(
            @NotNull final Connection conn,
            @NotNull final Long codChecklistNaoPrecisaSincronizar) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareCall("{CALL PICCOLOTUR.FUNC_CHECK_OS_MARCA_CHECKLIST_NAO_PRECISA_SINCRONIZAR(" +
                    "F_COD_CHECKLIST => ?, " +
                    "F_DATA_HORA_ATUALIZACAO => ?)}");
            stmt.setLong(1, codChecklistNaoPrecisaSincronizar);
            stmt.setObject(2, Now.offsetDateTimeUtc());
            stmt.execute();
        } finally {
            close(stmt);
        }
    }

    @Override
    public void marcaChecklistSincronizado(@NotNull final Connection conn,
                                           @NotNull final Long codChecklistSincronizado) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareCall("{CALL PICCOLOTUR.FUNC_CHECK_OS_MARCA_CHECKLIST_COMO_SINCRONIZADO(" +
                    "F_COD_CHECKLIST => ?, " +
                    "F_DATA_HORA_ATUALIZACAO => ?)}");
            stmt.setLong(1, codChecklistSincronizado);
            stmt.setObject(2, Now.offsetDateTimeUtc());
            stmt.execute();
        } finally {
            close(stmt);
        }
    }

    @Override
    public void erroAoSicronizarChecklist(@NotNull final Connection conn,
                                          @NotNull final Long codChecklistProLog,
                                          @NotNull final String errorMessage,
                                          @NotNull final Throwable throwable) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareCall(
                    "{CALL PICCOLOTUR.FUNC_CHECK_OS_INSERE_ERRO_SINCRONIA_CHECKLIST(F_COD_CHECKLIST => ?, " +
                            "F_ERROR_MESSAGE => ?, " +
                            "F_STACKTRACE => ?, " +
                            "F_DATA_HORA_ATUALIZACAO => ?)}");
            stmt.setLong(1, codChecklistProLog);
            stmt.setString(2, errorMessage);
            stmt.setString(3, ExceptionUtils.getStackTrace(throwable));
            stmt.setObject(4, Now.offsetDateTimeUtc());
            stmt.execute();
        } finally {
            close(stmt);
        }
    }

    @NotNull
    private AlternativaNokGlobus createAlternativaNokGlobus(@NotNull final ResultSet rSet) throws SQLException {
        return new AlternativaNokGlobus(
                rSet.getLong("COD_ALTERNATIVA_NOK"),
                rSet.getLong("COD_CONTEXTO_ALTERNATIVA_NOK"),
                rSet.getString("DESCRICAO_ALTERNATIVA_NOK"),
                rSet.getBoolean("ALTERNATIVA_TIPO_OUTROS"),
                PrioridadeAlternativaGlobus.fromString(rSet.getString("PRIORIDADE_ALTERNATIVA_NOK")));
    }

    @NotNull
    private PerguntaNokGlobus createPerguntaNokGlobus(@NotNull final ResultSet rSet) throws SQLException {
        return new PerguntaNokGlobus(
                rSet.getLong("COD_PERGUNTA"),
                rSet.getLong("COD_CONTEXTO_PERGUNTA_NOK"),
                rSet.getString("DESCRICAO_PERGUNTA_NOK"),
                new ArrayList<>());
    }

    @Override
    public boolean verificaItensIntegrados(@NotNull final List<Long> codItensResolver) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO " +
                    "WHERE COD_ITEM_OS_PROLOG = ANY(?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codItensResolver));
            rSet = stmt.executeQuery();
            return rSet.next();
        } finally {
            close(conn, stmt, rSet);
        }
    }
}