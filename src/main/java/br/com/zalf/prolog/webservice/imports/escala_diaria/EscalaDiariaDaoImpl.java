package br.com.zalf.prolog.webservice.imports.escala_diaria;

import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.Now;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtil;
import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
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

    private static final String BASE_QUERY = "SELECT ED.CODIGO, " +
            "  ED.PLACA, " +
            "  (CASE WHEN V.PLACA IS NULL THEN FALSE ELSE TRUE END) AS PLACA_OK, " +
            "  ED.MAPA, " +
            "  (CASE WHEN M.MAPA IS NULL THEN FALSE ELSE TRUE END) AS MAPA_OK, " +
            "  ED.DATA, " +
            "  ED.CPF_MOTORISTA, " +
            "  CM.NOME AS NOME_MOTORISTA, " +
            "  (CASE WHEN CM.CPF IS NULL THEN FALSE ELSE TRUE END) AS MOTORISTA_OK, " +
            "  ED.CPF_AJUDANTE_1, " +
            "  CA1.NOME AS NOME_AJUDANTE_1, " +
            "  (CASE WHEN CA1.CPF IS NULL THEN FALSE ELSE TRUE END) AS AJUDANTE_1_OK, " +
            "  ED.CPF_AJUDANTE_2, " +
            "  CA2.NOME AS NOME_AJUDANTE_2, " +
            "  (CASE WHEN CA2.CPF IS NULL THEN FALSE ELSE TRUE END) AS AJUDANTE_2_OK " +
            "FROM ESCALA_DIARIA AS ED " +
            "  LEFT JOIN COLABORADOR AS CM ON CM.CPF = ED.CPF_MOTORISTA " +
            "  LEFT JOIN COLABORADOR AS CA1 ON CA1.CPF = ED.CPF_AJUDANTE_1 " +
            "  LEFT JOIN COLABORADOR AS CA2 ON CA2.CPF = ED.CPF_AJUDANTE_2 " +
            "  LEFT JOIN VEICULO AS V ON ED.PLACA = V.PLACA " +
            "  LEFT JOIN MAPA AS M ON ED.MAPA = M.MAPA ";

    public EscalaDiariaDaoImpl() {
    }

    @Override
    public void insertOrUpdateEscalaDiaria(@NotNull final String token,
                                           @NotNull final Long codUnidade,
                                           @NotNull final List<EscalaDiariaItem> escalaDiariaItens) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            for (final EscalaDiariaItem item : escalaDiariaItens) {
                if (!updateEscalaDiaria(conn, token, codUnidade, item)) {
                    if (!insertEscalaDiaria(conn, token, codUnidade, item)) {
                        throw new IllegalStateException("Não foi possível inserir ou atualizar o item : " + item);
                    }
                }
            }
        } finally {
            closeConnection(conn);
        }
    }

    @Override
    public void insertOrUpdateEscalaDiariaItem(@NotNull final String token,
                                               @NotNull final Long codUnidade,
                                               @NotNull final EscalaDiariaItem escalaDiariaItem,
                                               final boolean isInsert) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            if (isInsert) {
                insertEscalaDiaria(conn, token, codUnidade, escalaDiariaItem);
            } else {
                updateEscalaDiaria(conn, token, codUnidade, escalaDiariaItem);
            }
        } finally {
            closeConnection(conn);
        }
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
            stmt = conn.prepareStatement(BASE_QUERY +
                    "WHERE ED.COD_UNIDADE = ? AND (ED.DATA >= ? AND ED.DATA <= ?) ORDER BY ED.DATA;");
            stmt.setLong(1, codUnidade);
            stmt.setObject(2, dataInicial);
            stmt.setObject(3, dataFinal);
            rSet = stmt.executeQuery();
            LocalDate ultimaData = null;
            while (rSet.next()) {
                final LocalDate dataAtual = rSet.getObject("DATA", LocalDate.class);
                if (ultimaData == null) {
                    ultimaData = dataAtual;
                } else if (!dataAtual.equals(ultimaData)) {
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
        escala.setItensEscalaDiaria(itens);
        escalasDiarias.add(escala);
        return escalasDiarias;
    }

    @Override
    public EscalaDiariaItem getEscalaDiariaItem(@NotNull final Long codUnidade,
                                                @NotNull final Long codEscala) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(BASE_QUERY +
                    "WHERE ED.COD_UNIDADE = ? AND ED.CODIGO = ?");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codEscala);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createEscalaDiariaItem(rSet);
            } else {
                throw new SQLDataException("Nenhuma escala Diária para o código : " + codEscala);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private EscalaDiariaItem createEscalaDiariaItem(@NotNull final ResultSet rSet) throws SQLException {
        final EscalaDiariaItem item = new EscalaDiariaItem();
        item.setCodigo(rSet.getLong("CODIGO"));
        item.setData(rSet.getObject("DATA", LocalDate.class));
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
                    "WHERE COD_UNIDADE = ? AND CODIGO::TEXT LIKE ANY (ARRAY[?])");
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

    private boolean insertEscalaDiaria(@NotNull final Connection conn,
                                       @NotNull final String token,
                                       @NotNull final Long codUnidade,
                                       @NotNull final EscalaDiariaItem item) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO ESCALA_DIARIA (COD_UNIDADE, " +
                    "                           DATA, " +
                    "                           PLACA, " +
                    "                           MAPA, " +
                    "                           CPF_MOTORISTA, " +
                    "                           CPF_AJUDANTE_1, " +
                    "                           CPF_AJUDANTE_2, " +
                    "                           DATA_HORA_CADASTRO, " +
                    "                           DATA_HORA_ULTIMA_ALTERACAO, " +
                    "                           CPF_CADASTRO, " +
                    "                           CPF_ULTIMA_ALTERACAO) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "  (SELECT TA.CPF_COLABORADOR FROM TOKEN_AUTENTICACAO AS TA WHERE TA.TOKEN = ?), " +
                    "  (SELECT TA.CPF_COLABORADOR FROM TOKEN_AUTENTICACAO AS TA WHERE TA.TOKEN = ?))");
            stmt.setLong(1, codUnidade);
            stmt.setDate(2, DateUtils.toSqlDate(item.getData()));
            stmt.setString(3, item.getPlaca().toUpperCase());
            stmt.setInt(4, item.getCodMapa());
            stmt.setLong(5, item.getCpfMotorista());
            stmt.setLong(6, item.getCpfAjudante1());
            stmt.setLong(7, item.getCpfAjudante2());
            stmt.setTimestamp(8, Now.timestampUtc());
            stmt.setTimestamp(9, Now.timestampUtc());
            stmt.setString(10, TokenCleaner.getOnlyToken(token));
            stmt.setString(11, TokenCleaner.getOnlyToken(token));
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir item na tabela escala");
            }
        } finally {
            closeStatement(stmt);
        }
        return true;
    }

    private boolean updateEscalaDiaria(@NotNull final Connection conn,
                                       @NotNull final String token,
                                       @NotNull final Long codUnidade,
                                       @NotNull final EscalaDiariaItem item) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE ESCALA_DIARIA SET COD_UNIDADE = ?, " +
                    "  DATA = ?, " +
                    "  PLACA = ?, " +
                    "  MAPA = ?, " +
                    "  CPF_MOTORISTA = ?, " +
                    "  CPF_AJUDANTE_1 = ?," +
                    "  CPF_AJUDANTE_2 = ?, " +
                    "  DATA_HORA_ULTIMA_ALTERACAO = ?, " +
                    "  CPF_ULTIMA_ALTERACAO = (SELECT TA.CPF_COLABORADOR FROM TOKEN_AUTENTICACAO AS TA WHERE TA.TOKEN = ?) " +
                    "WHERE COD_ESCALA = ?");
            stmt.setLong(1, codUnidade);
            stmt.setDate(2, DateUtils.toSqlDate(item.getData()));
            stmt.setString(3, item.getPlaca().toUpperCase());
            stmt.setInt(4, item.getCodMapa());
            stmt.setLong(5, item.getCpfMotorista());
            stmt.setLong(6, item.getCpfAjudante1());
            stmt.setLong(7, item.getCpfAjudante2());
            stmt.setTimestamp(8, Now.timestampUtc());
            stmt.setString(9, TokenCleaner.getOnlyToken(token));
            stmt.setLong(10, item.getCodigo());
            int count = stmt.executeUpdate();
            if (count == 0) {
                // nenhum para item atualizado
                return false;
            }
        } finally {
            closeStatement(stmt);
        }
        return true;
    }
}
