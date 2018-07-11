package br.com.zalf.prolog.webservice.raizen.produtividade;

import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
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

    }

    @Override
    public List<RaizenProdutividade> getRaizenProdutividade(@NotNull final Long codEmpresa,
                                                            @NotNull final LocalDate dataInicial) throws SQLException {
        return null;
    }

    @Override
    public List<RaizenProdutividade> getRaizenProdutividade(@NotNull final Long codEmpresa,
                                                            @NotNull final Long cpfMotorista) throws SQLException {
        return null;
    }

    @Override
    public void deleteRaizenProdutividadeItens(@NotNull final Long codEmpresa,
                                               @NotNull final List<Long> codRaizenProdutividade) throws SQLException {

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
                    "                           COD_COLABORADOR_ALTERACAO" +
                    "                           COD_EMPRESA)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, " +
                    "   (SELECT TA.CPF_COLABORADOR FROM TOKEN_AUTENTICACAO AS TA WHERE TA.TOKEN = ?)," +
                    "   (SELECT TA.CPF_COLABORADOR FROM TOKEN_AUTENTICACAO AS TA WHERE TA.TOKEN = ?)," +
                    "   ?)");
            stmt.setLong(1, item.getCpfMotorista());
            stmt.setString(2, item.getPlaca());
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
            stmt = conn.prepareStatement("UPDATE RAIZEN.PRODUTIVIDADE SET CPF," +
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
                    "AND COD_EMPRESA = codEmpresa ");
            stmt.setLong(1, item.getCpfMotorista());
            stmt.setString(2, item.getPlaca());
            stmt.setDate(3, DateUtils.toSqlDate(item.getDataViagem()));
            stmt.setBigDecimal(4, item.getValor());
            stmt.setString(5, item.getUsina());
            stmt.setString (6, item.getFazenda());
            stmt.setDouble(7, item.getRaio());
            stmt.setDouble(8, item.getTonelada());
            stmt.setString(9, token);

            if(stmt.executeUpdate() == 0){
                //nenhum item atualizado
                return false;
            }

        } finally {
            closeStatement(stmt);
        }
        return true;
    }
}
