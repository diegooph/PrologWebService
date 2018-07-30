package br.com.zalf.prolog.webservice.raizen.produtividade;

import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividade;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividadeColaborador;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividadeData;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividadeIndividualHolder;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.insert.RaizenProdutividadeItemInsert;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItemColaborador;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItemData;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItemVisualizacao;
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
                    "   TONELADAS = ?," +
                    "   COD_COLABORADOR_ALTERACAO = (SELECT TA.CPF_COLABORADOR FROM TOKEN_AUTENTICACAO AS TA WHERE TA" +
                    ".TOKEN = ?) " +
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

    @NotNull
    @Override
    public List<RaizenProdutividade> getRaizenProdutividadeColaborador(@NotNull final Long codEmpresa,
                                                                       @NotNull final LocalDate dataInicial,
                                                                       @NotNull final LocalDate dataFinal) throws
            SQLException {
        final List<RaizenProdutividade> produtividades = new ArrayList<>();
        RaizenProdutividadeColaborador raizenProdutividadeColaborador = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM RAIZEN.FUNC_RAIZEN_PRODUTIVIDADE_GET_ITENS_POR_COLABORADOR(?, ?, ?);");
            stmt.setLong(1, codEmpresa);
            stmt.setObject(2, dataInicial);
            stmt.setObject(3, dataFinal);
            rSet = stmt.executeQuery();
            boolean primeiraLinha = true;
            while (rSet.next()) {
                final Long cpfMotoristaAtual = rSet.getLong("CPF_MOTORISTA");
                if (primeiraLinha) {
                    raizenProdutividadeColaborador = createRaizenProdutividadeColaborador(rSet, produtividades);
                } else {
                    if (raizenProdutividadeColaborador.getCpf().equals(cpfMotoristaAtual)) {
                        raizenProdutividadeColaborador.getItensRaizen().add(createRaizenProdutividadeItemData(rSet));
                    } else {
                        raizenProdutividadeColaborador = createRaizenProdutividadeColaborador(rSet, produtividades);
                    }
                }
                primeiraLinha = false;
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return produtividades;
    }

    private RaizenProdutividadeColaborador createRaizenProdutividadeColaborador(@NotNull final ResultSet rSet,
                                                                                @NotNull final List<RaizenProdutividade> produtividades)
            throws SQLException {
        final List<RaizenProdutividadeItemData> itensData = new ArrayList<>();
        itensData.add(createRaizenProdutividadeItemData(rSet));
        final RaizenProdutividadeColaborador raizenProdutividadeColaborador = new RaizenProdutividadeColaborador(
                rSet.getLong("CPF_MOTORISTA"), rSet.getString("NOME_MOTORISTA"));
        raizenProdutividadeColaborador.setItensRaizen(itensData);
        produtividades.add(raizenProdutividadeColaborador);
        return raizenProdutividadeColaborador;
    }

    @NotNull
    @Override
    public List<RaizenProdutividade> getRaizenProdutividadeData(@NotNull final Long codEmpresa,
                                                                @NotNull final LocalDate dataInicial,
                                                                @NotNull final LocalDate dataFinal) throws
            SQLException {
        final List<RaizenProdutividade> produtividades = new ArrayList<>();
        RaizenProdutividadeData raizenProdutividadeData = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM RAIZEN.FUNC_RAIZEN_PRODUTIVIDADE_GET_ITENS_POR_DATA(?, ?, ?);");
            stmt.setLong(1, codEmpresa);
            stmt.setObject(2, dataInicial);
            stmt.setObject(3, dataFinal);
            rSet = stmt.executeQuery();
            boolean primeiraLinha = true;
            while (rSet.next()) {
                final LocalDate dataAtual = rSet.getObject("DATA_VIAGEM", LocalDate.class);
                if (primeiraLinha) {
                    raizenProdutividadeData = createRaizenProdutividadeData(rSet, produtividades);
                } else {
                    if (raizenProdutividadeData.getData().equals(dataAtual)) {
                        raizenProdutividadeData.getItensRaizen().add(createRaizenProdutividadeItemColaborador(rSet));
                    } else {
                        raizenProdutividadeData = createRaizenProdutividadeData(rSet, produtividades);
                    }
                }
                primeiraLinha = false;
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return produtividades;
    }

    @NotNull
    @Override
    public RaizenProdutividadeItemVisualizacao getRaizenProdutividadeItemVisualizacao(@NotNull final Long codEmpresa,
                                                                                      @NotNull final Long codItem)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM RAIZEN.FUNC_RAIZEN_PRODUTIVIDADE_GET_ITEM_POR_CODIGO(?, ?);");
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codItem);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createRaizenProdutividadeItemVisualizacao(rSet);
            } else {
                throw new SQLException("Item não encontrado com código: " + codItem);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    private RaizenProdutividadeData createRaizenProdutividadeData(
            @NotNull final ResultSet rSet,
            @NotNull final List<RaizenProdutividade> produtividades) throws SQLException {
        final List<RaizenProdutividadeItemColaborador> itensColaborador = new ArrayList<>();
        itensColaborador.add(createRaizenProdutividadeItemColaborador(rSet));
        final RaizenProdutividadeData raizenProdutividadeData = new RaizenProdutividadeData(
                rSet.getObject("DATA_VIAGEM", LocalDate.class));
        raizenProdutividadeData.setItensRaizen(itensColaborador);
        produtividades.add(raizenProdutividadeData);
        return raizenProdutividadeData;
    }

    private RaizenProdutividadeItemColaborador createRaizenProdutividadeItemColaborador(ResultSet rSet) throws
            SQLException {
        final RaizenProdutividadeItemColaborador item = new RaizenProdutividadeItemColaborador(
                rSet.getLong("CPF_MOTORISTA"),
                rSet.getString("NOME_MOTORISTA"));
        item.setCodigo(rSet.getLong("CODIGO"));
        item.setPlaca(rSet.getString("PLACA"));
        item.setPlacaCadastrada(rSet.getBoolean("PLACA_CADASTRADA"));
        item.setValor(rSet.getBigDecimal("VALOR"));
        item.setUsina(rSet.getString("USINA"));
        item.setFazenda(rSet.getString("FAZENDA"));
        item.setRaio(rSet.getDouble("RAIO"));
        item.setToneladas(rSet.getDouble("TONELADAS"));
        item.setCodColaboradorCadastro(rSet.getLong("COD_COLABORADOR_CADASTRO"));
        item.setCodColaboradorAlteracao(rSet.getLong("COD_COLABORADOR_ALTERACAO"));
        item.setCodEmpresa(rSet.getLong("COD_EMPRESA"));
        return item;
    }

    private RaizenProdutividadeItemData createRaizenProdutividadeItemData(ResultSet rSet) throws SQLException {
        final RaizenProdutividadeItemData item = new RaizenProdutividadeItemData();
        item.setCodigo(rSet.getLong("CODIGO"));
        item.setPlaca(rSet.getString("PLACA"));
        item.setPlacaCadastrada(rSet.getBoolean("PLACA_CADASTRADA"));
        item.setDataViagem(rSet.getObject("DATA_VIAGEM", LocalDate.class));
        item.setValor(rSet.getBigDecimal("VALOR"));
        item.setUsina(rSet.getString("USINA"));
        item.setFazenda(rSet.getString("FAZENDA"));
        item.setRaio(rSet.getDouble("RAIO"));
        item.setToneladas(rSet.getDouble("TONELADAS"));
        item.setCodColaboradorCadastro(rSet.getLong("COD_COLABORADOR_CADASTRO"));
        item.setCodColaboradorAlteracao(rSet.getLong("COD_COLABORADOR_ALTERACAO"));
        item.setCodEmpresa(rSet.getLong("COD_EMPRESA"));
        return item;
    }

    private RaizenProdutividadeItemVisualizacao createRaizenProdutividadeItemVisualizacao(ResultSet rSet) throws SQLException {
        final RaizenProdutividadeItemVisualizacao item = new RaizenProdutividadeItemVisualizacao();
        item.setCodigo(rSet.getLong("CODIGO"));
        item.setPlaca(rSet.getString("PLACA"));
        item.setPlacaCadastrada(rSet.getBoolean("PLACA_CADASTRADA"));
        item.setDataViagem(rSet.getObject("DATA_VIAGEM", LocalDate.class));
        item.setCpfColaborador(rSet.getLong("CPF_MOTORISTA"));
        item.setColaboradorCadastrado(rSet.getBoolean("MOTORISTA_CADASTRADO"));
        item.setValor(rSet.getBigDecimal("VALOR"));
        item.setUsina(rSet.getString("USINA"));
        item.setFazenda(rSet.getString("FAZENDA"));
        item.setRaio(rSet.getDouble("RAIO"));
        item.setToneladas(rSet.getDouble("TONELADAS"));
        item.setCodColaboradorCadastro(rSet.getLong("COD_COLABORADOR_CADASTRO"));
        item.setCodColaboradorAlteracao(rSet.getLong("COD_COLABORADOR_ALTERACAO"));
        item.setCodEmpresa(rSet.getLong("COD_EMPRESA"));
        return item;
    }

    @NotNull
    @Override
    public RaizenProdutividadeIndividualHolder getRaizenProdutividade(@NotNull final Long codColaborador,
                                                                      final int mes,
                                                                      final int ano) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<RaizenprodutividadeItemIndividual> itens;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM RAIZEN.FUNC_RAIZEN_PRODUTIVIDADE_GET_ITENS_INDIVIDUAL(?, ?, ?);");
            stmt.setLong(1, codColaborador);
            stmt.setInt(2, mes);
            stmt.setInt(3, ano);
            rSet = stmt.executeQuery();
            itens = new ArrayList<>();
            while (rSet.next()) {
                itens.add(createRaizenProdutividadeItemIndividual(rSet));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return new RaizenProdutividadeIndividualHolder(itens);
    }

    @NotNull
    private RaizenprodutividadeItemIndividual createRaizenProdutividadeItemIndividual(@NotNull final ResultSet rSet)
            throws SQLException {
        final RaizenprodutividadeItemIndividual item = new RaizenprodutividadeItemIndividual();
        item.setDataViagem(rSet.getDate("DATA_VIAGEM"));
        item.setValor(rSet.getBigDecimal("VALOR"));
        item.setPlaca(rSet.getString("PLACA"));
        item.setUsina(rSet.getString("USINA"));
        item.setFazenda(rSet.getString("FAZENDA"));
        item.setRaio(rSet.getDouble("RAIO"));
        item.setToneladas(rSet.getDouble("TONELADAS"));
        return item;
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
                                                       @NotNull final RaizenProdutividadeItemInsert item)
            throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO RAIZEN.PRODUTIVIDADE (CPF_MOTORISTA," +
                    "                           PLACA," +
                    "                           DATA_VIAGEM," +
                    "                           VALOR," +
                    "                           USINA," +
                    "                           FAZENDA," +
                    "                           RAIO," +
                    "                           TONELADAS, " +
                    "                           COD_COLABORADOR_CADASTRO, " +
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
                                                    @NotNull final RaizenProdutividadeItemInsert item) throws
            SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE RAIZEN.PRODUTIVIDADE SET CPF_MOTORISTA = ?," +
                    "   PLACA = ?," +
                    "   DATA_VIAGEM = ?," +
                    "   VALOR = ?," +
                    "   USINA = ?," +
                    "   FAZENDA = ?," +
                    "   RAIO = ?," +
                    "   TONELADAS = ?, " +
                    "   COD_COLABORADOR_ALTERACAO = " +
                    "(SELECT TA.CPF_COLABORADOR FROM TOKEN_AUTENTICACAO AS TA WHERE TA.TOKEN = ?) " +
                    "WHERE CPF_MOTORISTA = ?" +
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