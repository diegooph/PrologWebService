package br.com.zalf.prolog.webservice.integracao.praxio.data;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.AlternativaNokGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ChecklistItensNokGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.PerguntaNokGlobus;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

/**
 * Created on 17/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class SistemaGlobusPiccoloturDaoImpl extends DatabaseConnection implements SistemaGlobusPiccoloturDao {
    @Override
    public void insertItensNokPendentesParaSincronizar(
            @NotNull final Connection conn,
            @NotNull final Long codChecklistParaSincronizar) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR(" +
                    "COD_CHECKLIST_PARA_SINCRONIZAR) " +
                    "VALUES (?);");
            stmt.setLong(1, codChecklistParaSincronizar);
            if (stmt.executeUpdate() <= 0) {
                throw new SQLException(
                        "Não foi possível inserir o código do checklist na tabela de checks para sincronizar:\n" +
                                "codChecklistParaSincronizar: " + codChecklistParaSincronizar);
            }
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
            stmt = conn.prepareStatement("INSERT INTO PICCOLOTUR.CHECKLIST_ITEM_NOK_ENVIADO_GLOBUS(" +
                    "  COD_UNIDADE, " +
                    "  PLACA_VEICULO_OS, " +
                    "  CPF_COLABORADOR, " +
                    "  COD_CHECKLIST, " +
                    "  COD_CONTEXTO_PERGUNTA, " +
                    "  COD_CONTEXTO_ALTERNATIVA, " +
                    "  DATA_HORA_ENVIO) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);");
            final LocalDateTime dataHoraAtual = Now.localDateTimeUtc();
            stmt.setLong(1, checklistItensNokGlobus.getCodUnidadeChecklist());
            stmt.setString(2, checklistItensNokGlobus.getPlacaVeiculoChecklist());
            stmt.setLong(3, Colaborador.formatCpf(checklistItensNokGlobus.getCpfColaboradorRealizacao()));
            stmt.setLong(4, checklistItensNokGlobus.getCodChecklistRealizado());
            for (final PerguntaNokGlobus perguntaNokGlobus : checklistItensNokGlobus.getPerguntasNok()) {
                for (final AlternativaNokGlobus alternativaNokGlobus : perguntaNokGlobus.getAlternativasNok()) {
                    stmt.setLong(5, perguntaNokGlobus.getCodContextoPerguntaNok());
                    stmt.setLong(6, alternativaNokGlobus.getCodContextoAlternativaNok());
                    stmt.setObject(7, dataHoraAtual);
                    stmt.addBatch();
                }
            }
            final boolean todasInsercoesOk = IntStream
                    .of(stmt.executeBatch())
                    .allMatch(rowsAffectedCount -> rowsAffectedCount == 1);
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
            stmt = conn.prepareStatement("UPDATE PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR " +
                    "SET PRECISA_SER_SINCRONIZADO = FALSE " +
                    "WHERE COD_CHECKLIST_PARA_SINCRONIZAR = ?;");
            stmt.setLong(1, codChecklistNaoPrecisaSincronizar);
            if (stmt.executeUpdate() <= 0) {
                throw new SQLException(
                        "Não foi possível marcar o checklist para não precisar sincronizar:\n" +
                                "codChecklistNaoPrecisaSincronizar: " + codChecklistNaoPrecisaSincronizar);
            }
        } finally {
            close(stmt);
        }
    }

    @Override
    public void marcaChecklistSincronizado(@NotNull final Connection conn,
                                           @NotNull final Long codChecklistSincronizado) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR " +
                    "SET SINCRONIZADO = TRUE " +
                    "WHERE COD_CHECKLIST_PARA_SINCRONIZAR = ?;");
            stmt.setLong(1, codChecklistSincronizado);
            if (stmt.executeUpdate() <= 0) {
                throw new SQLException(
                        "Não foi possível marcar o checklist para sincronizado:\n" +
                                "codChecklistSincronizado: " + codChecklistSincronizado);
            }
        } finally {
            close(stmt);
        }
    }

    @Override
    public void erroAoSicronizarChecklist(@NotNull final Long codChecklistProLog,
                                          @NotNull final String errorMessage) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR " +
                    "SET " +
                    "  MENSAGEM_ERRO_AO_SINCRONIZAR = ? " +
                    "WHERE COD_CHECKLIST_PARA_SINCRONIZAR = ?;");
            stmt.setString(1, errorMessage);
            stmt.setLong(2, codChecklistProLog);
            stmt.executeUpdate();
        } finally {
            close(conn, stmt);
        }
    }
}
