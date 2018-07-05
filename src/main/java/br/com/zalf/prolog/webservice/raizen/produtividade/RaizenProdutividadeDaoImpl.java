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
    public void insertOrUpdateProdutividadeRaizen(@NotNull String token,
                                                  @NotNull List<RaizenProdutividadeItem> raizenItens) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            for (final RaizenProdutividadeItem item : raizenItens) {
                if(!updateRaizenUpload(conn, token, item)){
                   internalInsertRaizenItem(conn, token, item);
                }
            }
        }finally {
            closeConnection(conn);
        }
    }

    @Override
    public void insertRaizenItem(@NotNull String token,
                                       @NotNull RaizenProdutividadeItem item) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            internalInsertRaizenItem(conn, token, item);
        } finally {
            closeConnection(conn);
        }
    }

    @Override
    public void updateRaizenProdutividadeItem(@NotNull String token, @NotNull Long codUnidade, @NotNull RaizenProdutividadeItem item) throws SQLException {

    }

    @Override
    public List<RaizenProdutividade> getRaizenProdutividade(@NotNull LocalDate dataInicial, @NotNull LocalDate dataFinal) throws SQLException {
        return null;
    }

    @Override
    public void deleteRaizenProdutividadeItens(@NotNull List<Long> codRaizenProdutividade) throws SQLException {

    }


    private void internalInsertRaizenItem(@NotNull final Connection conn,
                                          @NotNull final String token,
                                          @NotNull final RaizenProdutividadeItem item) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO RAIZEN.PRODUTIVIDADE (CPF," +
                    "                           PLACA," +
                    "                           DATA_VIAGEM," +
                    "                           VALOR," +
                    "                           USINA," +
                    "                           FAZENDA," +
                    "                           RAIO," +
                    "                           TONELADA)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?,)");
            stmt.setLong(1, item.getCpfMotorista());
            stmt.setString(2, item.getPlaca());
            stmt.setDate(3, DateUtils.toSqlDate(item.getDataViagem()));
            stmt.setDouble(4, item.getValor());
            stmt.setString(5, item.getUsina());
            stmt.setString(6, item.getFazenda());
            stmt.setDouble(7, item.getRaio());
            stmt.setDouble(8, item.getTonelada());
            if(stmt.executeUpdate() == 0){
                throw new SQLException("Erro ao inserir item na tabela produtividade");
            }
        }finally {
            closeStatement(stmt);
        }
    }

    private boolean updateRaizenUpload(Connection conn, String token, RaizenProdutividadeItem item) {
        return true;
    }


}
