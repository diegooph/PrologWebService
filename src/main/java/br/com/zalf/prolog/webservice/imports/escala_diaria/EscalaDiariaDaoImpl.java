package br.com.zalf.prolog.webservice.imports.escala_diaria;

import br.com.zalf.prolog.webservice.commons.util.PostgresUtil;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created on 10/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class EscalaDiariaDaoImpl extends DatabaseConnection implements EscalaDiariaDao {

    public EscalaDiariaDaoImpl() {
    }

    @Override
    public void insertOrUpdateEscalaDiaria(@NotNull final Long codUnidade,
                                           @NotNull final List<EscalaDiariaItem> escalaDiariaItens) throws SQLException {

    }

    @Override
    public void insertOrUpdateEscalaDiariaItem(@NotNull final Long codUnidade,
                                               @NotNull final EscalaDiariaItem escalaDiariaItem,
                                               final boolean isInsert) throws SQLException {

    }

    @Override
    public List<EscalaDiaria> getEscalasDiarias(@NotNull final Long codUnidade,
                                                @NotNull final LocalDate dataInicial,
                                                @NotNull final LocalDate dataFinal) throws SQLException {
        final List<EscalaDiaria> escalasDiarias = new ArrayList<>();
        EscalaDiaria escala = new EscalaDiaria();
        List<EscalaDiariaItem> itens = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT ED.COD_ESCALA, " +
                    "  ED.PLACA, " +
                    "  (CASE WHEN V.PLACA IS NULL THEN TRUE ELSE FALSE END) AS PLACA_OK, " +
                    "  ED.MAPA, " +
                    "  (CASE WHEN M.MAPA IS NULL THEN TRUE ELSE FALSE END) AS MAPA_OK, " +
                    "  ED.DATA, " +
                    "  ED.CPF_MOTORISTA, " +
                    "  CM.NOME AS NOME_MOTORISTA, " +
                    "  (CASE WHEN CM.CPF IS NULL THEN TRUE ELSE FALSE END) AS MOTORISTA_OK, " +
                    "  ED.CPF_AJUDANTE_1, " +
                    "  CA1.NOME AS NOME_AJUDANTE_1, " +
                    "  (CASE WHEN CA1.CPF IS NULL THEN TRUE ELSE FALSE END) AS AJUDANTE_1_OK, " +
                    "  ED.CPF_AJUDANTE_2, " +
                    "  CA2.NOME AS NOME_AJUDANTE_2, " +
                    "  (CASE WHEN CA2.CPF IS NULL THEN TRUE ELSE FALSE END) AS AJUDANTE_2_OK " +
                    "FROM ESCALA_DIARIA AS ED " +
                    "  LEFT JOIN COLABORADOR AS CM ON CM.CPF = ED.CPF_MOTORISTA " +
                    "  LEFT JOIN COLABORADOR AS CA1 ON CA1.CPF = ED.CPF_AJUDANTE_1 " +
                    "  LEFT JOIN COLABORADOR AS CA2 ON CA2.CPF = ED.CPF_AJUDANTE_2 " +
                    "  LEFT JOIN VEICULO AS V ON ED.PLACA = V.PLACA " +
                    "  LEFT JOIN MAPA AS M ON ED.MAPA = M.MAPA " +
                    "WHERE ED.COD_UNIDADE = ? AND (ED.DATA >= ? AND ED.DATA <= ?) ORDER BY ED.DATA");
            stmt.setLong(1, codUnidade);
            stmt.setObject(2, dataInicial);
            stmt.setObject(3, dataFinal);
            rSet = stmt.executeQuery();
            Date ultimaData = null;
            while (rSet.next()) {
                final Date dataAtual = rSet.getDate("DATA");
                if (ultimaData == null) {
                    ultimaData = dataAtual;
                } else if (dataAtual != ultimaData) {
                    escala.calculaItensErrados();
                    escala.setItensEscalaDiaria(itens);
                    escalasDiarias.add(escala);
                    escala = new EscalaDiaria();
                    itens = new ArrayList<>();
                }
                itens.add(createEscalaDiariaItem(rSet));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        escala.calculaItensErrados();
        escala.setItensEscalaDiaria(itens);
        escalasDiarias.add(escala);
        return escalasDiarias;
    }

    private EscalaDiariaItem createEscalaDiariaItem(@NotNull final ResultSet rSet) throws SQLException {
        final EscalaDiariaItem item = new EscalaDiariaItem();
        item.setCodEscala(rSet.getLong("COD_ESCALA"));
        item.setData(rSet.getDate("DATA"));
        item.setPlaca(rSet.getString("PLACA"));
        item.setPlacaOk(rSet.getBoolean("PLACA_OK"));
        item.setCodMapa(rSet.getInt("MAPA"));
        item.setMapaOk(rSet.getBoolean("MAPA_OK"));
        item.setCpfMotorista(rSet.getLong("CPF_MOTORISTA"));
        item.setNomeMotorista(rSet.getString("NOME_MOTORISTA"));
        item.setCpfMotoristaOk(rSet.getBoolean("MOTORISTA_OK"));
        item.setCpfAjudante1(rSet.getLong("CPF_AJUDANTE_1"));
        item.setNomeAjudante1(rSet.getString("NOME_AJUDANTE_1"));
        item.setCpfAjudante1Ok(rSet.getBoolean("AJUDANTE_1_OK"));
        item.setCpfAjudante2(rSet.getLong("CPF_AJUDANTE_2"));
        item.setNomeAjudante2(rSet.getString("NOME_AJUDANTE_2"));
        item.setCpfAjudante2Ok(rSet.getBoolean("AJUDANTE_2_OK"));
        return item;
    }

    @Override
    public void deleteEscalaDiariaItens(@NotNull final Long codUnidade,
                                        @NotNull final List<Long> codEscalas) throws SQLException {
        if (codEscalas.isEmpty())
            return;

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("DELETE FROM ESCALA_DIARIA " +
                    "WHERE COD_UNIDADE = ? AND COD_ESCALA::TEXT LIKE ANY (ARRAY[?])");
            stmt.setLong(1, codUnidade);
            stmt.setArray(2, PostgresUtil.ListLongToArray(conn, codEscalas));
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao deletar Escala");
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
    }
}
