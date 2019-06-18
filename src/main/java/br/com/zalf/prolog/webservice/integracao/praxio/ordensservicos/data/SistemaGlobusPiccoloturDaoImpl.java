package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.data;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.AlternativaNokGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ChecklistItensNokGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.PerguntaNokGlobus;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

/**
 * Created on 17/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class SistemaGlobusPiccoloturDaoImpl extends DatabaseConnection implements SistemaGlobusPiccoloturDao {
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
                    "  COD_PERGUNTA, " +
                    "  COD_ALTERNATIVA, " +
                    "  DATA_HORA_ENVIO) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);");
            final LocalDateTime dataHoraAtual = Now.localDateTimeUtc();
            stmt.setLong(1, checklistItensNokGlobus.getCodUnidadeChecklist());
            stmt.setString(2, checklistItensNokGlobus.getPlacaVeiculoChecklist());
            stmt.setLong(3, Colaborador.formatCpf(checklistItensNokGlobus.getCpfColaboradorRealizacao()));
            stmt.setLong(4, checklistItensNokGlobus.getCodChecklistRealizado());
            for (final PerguntaNokGlobus perguntaNokGlobus : checklistItensNokGlobus.getPerguntasNok()) {
                for (final AlternativaNokGlobus alternativaNokGlobus : perguntaNokGlobus.getAlternativasNok()) {
                    stmt.setLong(5, perguntaNokGlobus.getCodPerguntaNok());
                    stmt.setLong(6, alternativaNokGlobus.getCodAlternativaNok());
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
}
