package br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico;

import br.com.zalf.prolog.webservice.commons.OrderByClause;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuTipoServico;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 24/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class PneuTipoServicoDaoImpl extends DatabaseConnection implements PneuTipoServicoDao {

    @NotNull
    @Override
    public Long insertPneuTipoServico(@NotNull final String token,
                                      @NotNull final PneuTipoServico tipoServico) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            return insereTipoServico(conn, token, tipoServico.getCodEmpresa(), tipoServico);
        } finally {
            closeConnection(conn);
        }
    }

    @Override
    public void atualizaPneuTipoServico(@NotNull final String token,
                                        @NotNull final Long codEmpresa,
                                        @NotNull final PneuTipoServico tipoServico) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            inativaPneuTipoServico(conn, token, codEmpresa, tipoServico);
            insereTipoServico(conn, token, codEmpresa, tipoServico);
        } finally {
            closeConnection(conn);
        }
    }

    @SuppressWarnings("Duplicates")
    @NotNull
    @Override
    public List<PneuTipoServico> getPneuTiposServicos(@NotNull final Long codEmpresa,
                                                      @NotNull final List<OrderByClause> orderBy,
                                                      @Nullable final Boolean ativos) throws Throwable {
        final List<PneuTipoServico> tiposServicos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            final String sql = "SELECT * FROM PNEU_TIPO_SERVICO " +
                    "WHERE COD_EMPRESA = ? " +
                    "AND UTILIZADO_CADASTRO_PNEU = FALSE " +
                    "AND (? = 1 OR STATUS_ATIVO = ?) ORDER BY ";
            final StringBuilder builder = new StringBuilder();
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < orderBy.size(); i++) {
                final OrderByClause order = orderBy.get(i);
                if (order.getPropertyName().equals("nome") || order.getPropertyName().equals("incrementaVida")) {
                    if (builder.length() == 0) {
                        builder.append(order.toSqlString());
                    } else {
                        builder.append(",").append(order.toSqlString());
                    }
                }
            }
            stmt = conn.prepareStatement(sql.concat(builder.toString()));
            stmt.setLong(1, codEmpresa);
            if (ativos == null) {
                stmt.setInt(2, 1);
                stmt.setBoolean(3, true);
            } else {
                stmt.setInt(2, 0);
                stmt.setBoolean(3, ativos);
            }
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                tiposServicos.add(createPneuTipoServico(rSet));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return tiposServicos;
    }

    @NotNull
    @Override
    public PneuTipoServico getPneuTipoServico(@NotNull final Long codEmpresa,
                                              @NotNull final Long codTipoServico) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM PNEU_TIPO_SERVICO WHERE " +
                    "COD_EMPRESA = ? AND CODIGO = ? AND UTILIZADO_CADASTRO_PNEU = FALSE;");
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codTipoServico);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createPneuTipoServico(rSet);
            } else {
                throw new SQLException("Nenhuma tipo de serviço encontrado para esse código: " + codTipoServico);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void alterarStatusPneuTipoServico(@NotNull final String token,
                                             @NotNull final Long codEmpresa,
                                             @NotNull final PneuTipoServico tipoServico) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            inativaPneuTipoServico(conn, token, codEmpresa, tipoServico);
        } finally {
            closeConnection(conn);
        }
    }

    @NotNull
    private Long insereTipoServico(@NotNull final Connection conn,
                                   @NotNull final String token,
                                   @NotNull final Long codEmpresa,
                                   @NotNull final PneuTipoServico tipoServico) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO PNEU_TIPO_SERVICO " +
                    "(COD_EMPRESA, NOME, COD_COLABORADOR_CRIACAO, INCREMENTA_VIDA, DATA_HORA_CRIACAO) " +
                    "VALUES (?, ?,(SELECT C.CODIGO " +
                    "FROM COLABORADOR AS C " +
                    "WHERE C.CPF = (SELECT TA.CPF_COLABORADOR " +
                    "FROM TOKEN_AUTENTICACAO AS TA WHERE TOKEN = ?)), ?, ?) RETURNING CODIGO;");
            stmt.setLong(1, codEmpresa);
            stmt.setString(2, tipoServico.getNome().toUpperCase());
            stmt.setString(3, token);
            stmt.setBoolean(4, tipoServico.isIncrementaVida());
            stmt.setTimestamp(5, Now.getTimestampUtc());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Erro ao inserir tipo de serviço");
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
    }

    private void inativaPneuTipoServico(@NotNull final Connection conn,
                                        @NotNull final String token,
                                        @NotNull final Long codEmpresa,
                                        @NotNull final PneuTipoServico tipoServico) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE PNEU_TIPO_SERVICO SET " +
                    "STATUS_ATIVO = FALSE, " +
                    "COD_COLABORADOR_EDICAO = (SELECT C.CODIGO " +
                    "FROM COLABORADOR AS C " +
                    "WHERE C.CPF = (SELECT TA.CPF_COLABORADOR FROM TOKEN_AUTENTICACAO AS TA WHERE TOKEN = ?)), " +
                    "DATA_HORA_EDICAO = ? WHERE CODIGO = ? AND COD_EMPRESA = ?;");
            stmt.setString(1, token);
            stmt.setTimestamp(2, Now.getTimestampUtc());
            stmt.setLong(3, tipoServico.getCodigo());
            stmt.setLong(4, codEmpresa);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inativar tipo serviço de código: " + tipoServico.getCodigo());
            }
        } finally {
            closeStatement(stmt);
        }
    }

    @NotNull
    private PneuTipoServico createPneuTipoServico(@NotNull final ResultSet rSet) throws Throwable {
        final PneuTipoServico tipoServico = new PneuTipoServico();
        tipoServico.setCodigo(rSet.getLong("CODIGO"));
        tipoServico.setCodEmpresa(rSet.getLong("COD_EMPRESA"));
        tipoServico.setNome(rSet.getString("NOME"));
        tipoServico.setStatusAtivo(rSet.getBoolean("STATUS_ATIVO"));
        tipoServico.setEditavel(rSet.getBoolean("EDITAVEL"));
        tipoServico.setIncrementaVida(rSet.getBoolean("INCREMENTA_VIDA"));
        return tipoServico;
    }
}