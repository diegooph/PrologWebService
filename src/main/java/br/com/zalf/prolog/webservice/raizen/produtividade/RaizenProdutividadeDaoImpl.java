package br.com.zalf.prolog.webservice.raizen.produtividade;

import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.*;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.insert.RaizenProdutividadeItemInsert;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItemColaborador;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItemData;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenprodutividadeItemIndividual;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 05/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeDaoImpl extends DatabaseConnection implements RaizenProdutividadeDao {

    public RaizenProdutividadeDaoImpl() {
    }

    @Override
    public void insertOrUpdateProdutividadeRaizen(@NotNull final String token,
                                                  @NotNull final Long codEmpresa,
                                                  @NotNull final List<RaizenProdutividadeItemInsert> raizenItens)
            throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            for (final RaizenProdutividadeItemInsert item : raizenItens) {
                if (!updateRaizenProdutividadeUpload(conn, token, codEmpresa, item)) {
                    internalInsertRaizenProdutividadeItem(conn, token, codEmpresa, item);
                }
            }
        } finally {
            closeConnection(conn);
        }
    }

    @Override
    public void insertRaizenProdutividadeItem(@NotNull final String token,
                                              @NotNull final Long codEmpresa,
                                              @NotNull final RaizenProdutividadeItemInsert item) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            internalInsertRaizenProdutividadeItem(conn, token, codEmpresa, item);
        } finally {
            closeConnection(conn);
        }
    }

    @Override
    public void updateRaizenProdutividadeItem(@NotNull final String token,
                                              @NotNull final Long codEmpresa,
                                              @NotNull final RaizenProdutividadeItemInsert item) throws SQLException {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE RAIZEN.PRODUTIDADE SET CPF_MOTORISTA = ?," +
                    "   DATA_VIAGEM = ?," +
                    "   PLACA = ?, " +
                    "   VALOR = ?," +
                    "   USINA = ?," +
                    "   FAZENDA = ?," +
                    "   RAIO = ?," +
                    "   TONELADA = ?," +
                    "   COD_COLABORADOR_ALTERACAO = (SELECT TA.CPF_COLABORADOR FROM TOKEN_AUTENTICACAO AS TA WHERE TA.TOKEN = ?) " +
                    "WHERE CODIGO = ?");
            stmt.setLong(1, item.getCpfMotorista());
            stmt.setDate(2, DateUtils.toSqlDate(item.getDataViagem()));
            stmt.setString(3, item.getPlaca().toUpperCase());
            stmt.setBigDecimal(4, item.getValor());
            stmt.setString(5, item.getUsina());
            stmt.setString(6, item.getFazenda());
            stmt.setDouble(7, item.getRaio());
            stmt.setDouble(8, item.getTonelada());
            stmt.setString(9, token);
            stmt.setLong(10, item.getCodigo());
            if (stmt.executeUpdate() == 0) {
                // nenhum para item atualizado
                throw new SQLDataException("Não foi possível atualizar o item de código: " + item.getCodigo());
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    @Override
    public List<RaizenProdutividade> getRaizenProdutividade(@NotNull final Long codEmpresa,
                                                            @NotNull final LocalDate dataInicial,
                                                            @NotNull final LocalDate dataFinal) throws SQLException {
        final List<RaizenProdutividade> raizenProdutividades = new ArrayList<>();
        RaizenProdutividadeData raizenProdutividade = null;
        List<RaizenProdutividadeItemColaborador> itens = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_RAIZEN_PRODUTIVIDADE_GET_ITENS_POR_DATA(?, ?, ?);");
            stmt.setLong(1, codEmpresa);
            stmt.setObject(2, dataInicial);
            stmt.setObject(3, dataFinal);
            rSet = stmt.executeQuery();
            LocalDate ultimaData = null;
            if (rSet.next()) {
                do {
                    final LocalDate dataAtual = rSet.getObject("DATA_VIAGEM", LocalDate.class);
                    if (ultimaData == null) {
                        ultimaData = dataAtual;
                    } else if (!dataAtual.equals(ultimaData)) {
                        raizenProdutividade = new RaizenProdutividadeData(
                                rSet.getObject("DATA_VIAGEM", LocalDate.class));
                        raizenProdutividade.setItensRaizen(itens);
                        raizenProdutividades.add(raizenProdutividade);
                        itens = new ArrayList<>();
                        ultimaData = dataAtual;
                    }
                } while (rSet.next());
            } else {
                throw new SQLException("Erro ao buscar produtividade");
            }
            itens.add(createRaizenProdutividadeItemColaborador(rSet));
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        raizenProdutividade.setItensRaizen(itens);
        raizenProdutividades.add(raizenProdutividade);
        return raizenProdutividades;
    }

    private RaizenProdutividadeItemColaborador createRaizenProdutividadeItemColaborador(ResultSet rSet) throws SQLException {
        final RaizenProdutividadeItemColaborador item = new RaizenProdutividadeItemColaborador(
                rSet.getString("CPF_MOTORISTA"),
                rSet.getString("NOME"));
        item.setCodigo(rSet.getLong("CODIGO"));
        item.setPlaca(rSet.getString("PLACA"));
        item.setValor(rSet.getBigDecimal("VALOR"));
        item.setUsina(rSet.getString("USINA"));
        item.setFazenda(rSet.getString("FAZENDA"));
        item.setRaio(rSet.getDouble("RAIO"));
        item.setTonelada(rSet.getDouble("TONELADA"));
        item.setCodColaboradorCadastro(rSet.getLong("COD_COLABORADOR_CADASTRO"));
        item.setCodColaboradorAlteracao(rSet.getLong("COD_COLABORADOR_ALTERACAO"));
        item.setCodEmpresa(rSet.getLong("COD_EMPRESA"));
        return item;
    }

    @Override
    public List<RaizenProdutividade> getRaizenProdutividade(@NotNull final Long codEmpresa,
                                                            @NotNull final Long cpfMotorista) throws SQLException {
        final List<RaizenProdutividade> raizenProdutividades = new ArrayList<>();
        RaizenProdutividadeColaborador raizenProdutividade = null;
        List<RaizenProdutividadeItemData> itens = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_RAIZEN_PRODUTIVIDADE_GET_ITENS_POR_COLABORADOR(?, ?);");
            stmt.setLong(1, codEmpresa);
            stmt.setObject(2, cpfMotorista);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                do {
                    raizenProdutividade = new RaizenProdutividadeColaborador(
                            rSet.getString("CPF_MOTORISTA"),
                            rSet.getString("NOME"));
                    raizenProdutividade.setItensRaizen(itens);
                    raizenProdutividades.add(raizenProdutividade);
                    itens = new ArrayList<>();
                } while (rSet.next());
            } else {
                throw new SQLException("Erro ao buscar produtividade");
            }
            itens.add(createRaizenProdutividadeItemData(rSet));
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        raizenProdutividade.setItensRaizen(itens);
        raizenProdutividades.add(raizenProdutividade);
        return raizenProdutividades;
    }

    private RaizenProdutividadeItemData createRaizenProdutividadeItemData(ResultSet rSet) throws SQLException {
        final RaizenProdutividadeItemData item = new RaizenProdutividadeItemData();
        item.setCodigo(rSet.getLong("CODIGO"));
        item.setPlaca(rSet.getString("PLACA"));
        item.setDataViagem(rSet.getObject("DATA_VIAGEM", LocalDate.class));
        item.setValor(rSet.getBigDecimal("VALOR"));
        item.setUsina(rSet.getString("USINA"));
        item.setFazenda(rSet.getString("FAZENDA"));
        item.setRaio(rSet.getDouble("RAIO"));
        item.setTonelada(rSet.getDouble("TONELADA"));
        item.setCodColaboradorCadastro(rSet.getLong("COD_COLABORADOR_CADASTRO"));
        item.setCodColaboradorAlteracao(rSet.getLong("COD_COLABORADOR_ALTERACAO"));
        item.setCodEmpresa(rSet.getLong("COD_EMPRESA"));
        return item;
    }

    @Override
    public List<RaizenProdutividade> getRaizenProdutividade(@NotNull final Long codColaborador,
                                                            @NotNull final int mes,
                                                            @NotNull final int ano) throws SQLException {
        final List<RaizenProdutividade> raizenProdutividades = new ArrayList<>();
        RaizenProdutividadeIndividualHolder raizenProdutividade = null;
        List<RaizenprodutividadeItemIndividual> itens = new ArrayList<>();
        Connection conn= null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_RAIZEN_PRODUTIVIDADE_GET_ITENS_INDIVIDUAL(?, ?, ?);");
            stmt.setLong(1, codColaborador);
            if (mes >= 1 && mes <= 12){
                stmt.setInt(2, mes);
            }
            stmt.setInt(3, ano);

        } finally {
        }

        return null;
    }

    @Override
    public void deleteRaizenProdutividadeItens(@NotNull final Long codEmpresa,
                                               @NotNull final List<Long> codRaizenProdutividade) throws SQLException {
        if (codRaizenProdutividade.isEmpty())
            return;

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("DELETE FROM RAIZEN.PRODUTIVIDADE " +
                    "WHERE COD_EMPRESA = ? AND CODIGO::TEXT LIKE ANY (ARRAY[?])");
            stmt.setLong(1, codEmpresa);
            stmt.setArray(2, PostgresUtils.ListLongToArray(conn, codRaizenProdutividade));
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao deletar produtividade");
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    private void internalInsertRaizenProdutividadeItem(@NotNull final Connection conn,
                                                       @NotNull final String token,
                                                       @NotNull final Long codEmpresa,
                                                       @NotNull final RaizenProdutividadeItemInsert item) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO RAIZEN.PRODUTIVIDADE (CPF," +
                    "                           PLACA," +
                    "                           DATA_VIAGEM," +
                    "                           VALOR," +
                    "                           USINA," +
                    "                           FAZENDA," +
                    "                           RAIO," +
                    "                           TONELADA, " +
                    "                           COD_COLABORADOR_CADSTRO, " +
                    "                           COD_COLABORADOR_ALTERACAO, " +
                    "                           COD_EMPRESA)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, " +
                    "   (SELECT TA.CPF_COLABORADOR FROM TOKEN_AUTENTICACAO AS TA WHERE TA.TOKEN = ?)," +
                    "   (SELECT TA.CPF_COLABORADOR FROM TOKEN_AUTENTICACAO AS TA WHERE TA.TOKEN = ?)," +
                    "   ?)");
            stmt.setLong(1, item.getCpfMotorista());
            stmt.setString(2, item.getPlaca().toUpperCase());
            stmt.setDate(3, DateUtils.toSqlDate(item.getDataViagem()));
            stmt.setBigDecimal(4, item.getValor());
            stmt.setString(5, item.getUsina());
            stmt.setString(6, item.getFazenda());
            stmt.setDouble(7, item.getRaio());
            stmt.setDouble(8, item.getTonelada());
            stmt.setString(9, token);
            stmt.setString(10, token);
            stmt.setLong(11, codEmpresa);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir item na tabela produtividade");
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private boolean updateRaizenProdutividadeUpload(@NotNull final Connection conn,
                                                    @NotNull final String token,
                                                    @NotNull final Long codEmpresa,
                                                    @NotNull final RaizenProdutividadeItemInsert item) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE RAIZEN.PRODUTIVIDADE SET CPF = ?," +
                    "   PLACA = ?," +
                    "   DATA_VIAGEM = ?," +
                    "   VALOR = ?," +
                    "   USINA = ?," +
                    "   FAZENDA = ?," +
                    "   RAIO = ?," +
                    "   TONELADA = ?, " +
                    "   COD_COLABORADOR_ALTERACAO = " +
                    "(SELECT TA.CPF_COLABORADOR FROM TOKEN_AUTENTICACAO AS TA WHERE TA.TOKEN = ?) " +
                    "WHERE CPF = ?" +
                    "AND PLACA = ?" +
                    "AND DATA_VIAGEM = ?" +
                    "AND COD_EMPRESA = ?");
            stmt.setLong(1, item.getCpfMotorista());
            stmt.setString(2, item.getPlaca());
            stmt.setDate(3, DateUtils.toSqlDate(item.getDataViagem()));
            stmt.setBigDecimal(4, item.getValor());
            stmt.setString(5, item.getUsina());
            stmt.setString(6, item.getFazenda());
            stmt.setDouble(7, item.getRaio());
            stmt.setDouble(8, item.getTonelada());
            stmt.setString(9, token);
            stmt.setLong(10, item.getCpfMotorista());
            stmt.setString(11, item.getPlaca());
            stmt.setDate(12, DateUtils.toSqlDate(item.getDataViagem()));
            stmt.setLong(13, codEmpresa);
            if (stmt.executeUpdate() == 0) {
                //nenhum item atualizado
                return false;
            }
        } finally {
            closeStatement(stmt);
        }
        return true;
    }
}