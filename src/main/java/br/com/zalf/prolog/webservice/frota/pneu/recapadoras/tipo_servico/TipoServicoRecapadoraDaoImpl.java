package br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico;

import br.com.zalf.prolog.webservice.commons.util.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.Recapadora;
import org.jetbrains.annotations.NotNull;

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
public class TipoServicoRecapadoraDaoImpl extends DatabaseConnection implements TipoServicoRecapadoraDao {

    @Override
    public Long insertTipoServicoRecapadora(@NotNull final String token,
                                            @NotNull final TipoServicoRecapadora tipoServico) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO RECAPADORA_TIPO_SERVICO (COD_EMPRESA, NOME, COD_COLABORADOR_CRIACAO, DATA_HORA_CRIACAO) " +
                    "VALUES (?, ?, " +
                    "(SELECT C.CODIGO FROM COLABORADOR AS C WHERE C.CPF = " +
                    "(SELECT TA.CPF_COLABORADOR FROM TOKEN_AUTENTICACAO AS TA WHERE TOKEN = ?)), ?) RETURNING CODIGO;");
            stmt.setLong(1, tipoServico.getCodEmpresa());
            stmt.setString(2, tipoServico.getNome());
            stmt.setString(3, token);
            stmt.setTimestamp(4, Now.timestampUtc());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Erro ao inserir tipo de serviço");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void atualizaTipoServicoRecapadora(@NotNull final String token,
                                              @NotNull final Long codEmpresa,
                                              @NotNull final TipoServicoRecapadora tipoServico) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            inativaTipoServico(conn, token, codEmpresa, tipoServico);
            atualizaTipoServico(conn, token, codEmpresa, tipoServico);
        } finally {
            closeConnection(conn);
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    public List<TipoServicoRecapadora> getTiposServicosRecapadora(@NotNull final Long codEmpresa,
                                                                  final Boolean ativas) throws SQLException {
        final List<TipoServicoRecapadora> tiposServicos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM RECAPADORA_TIPO_SERVICO " +
                    "WHERE (COD_EMPRESA = ? OR COD_EMPRESA IS NULL) " +
                    "AND (? = 1 OR STATUS_ATIVA = ?)");
            stmt.setLong(1, codEmpresa);

            if (ativas == null) {
                stmt.setInt(2, 1);
                stmt.setBoolean(3, true);
            } else {
                stmt.setInt(2, 0);
                stmt.setBoolean(3, ativas);
            }
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                tiposServicos.add(createTipoServicoRecapadora(rSet));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return tiposServicos;
    }

    @Override
    public TipoServicoRecapadora getTipoServicoRecapadora(@NotNull final Long codEmpresa,
                                                          @NotNull final Long codTipoServico) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM RECAPADORA_TIPO_SERVICO WHERE (COD_EMPRESA = ? OR COD_EMPRESA IS NULL) AND CODIGO = ?");
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codTipoServico);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createTipoServicoRecapadora(rSet);
            } else {
                throw new SQLException("Nenhuma tipo de serviço encontrado para esse código: " + codTipoServico);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void alterarStatusTipoServicoRecapadora(@NotNull final String token,
                                                   @NotNull final Long codEmpresa,
                                                   @NotNull final TipoServicoRecapadora tipoServico) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            inativaTipoServico(conn, token, codEmpresa, tipoServico);
        } finally {
            closeConnection(conn, null, null);
        }
    }

    private void atualizaTipoServico(@NotNull final Connection conn,
                                     @NotNull final String token,
                                     @NotNull final Long codEmpresa,
                                     @NotNull final TipoServicoRecapadora tipoServico) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE RECAPADORA_TIPO_SERVICO SET NOME = ? " +
                    "WHERE CODIGO = ? AND COD_EMPRESA = ?;");
            stmt.setString(1, tipoServico.getNome());
            stmt.setLong(2, tipoServico.getCodigo());
            stmt.setLong(3, codEmpresa);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao atualizar nome do tipo serviço de código: " + tipoServico.getCodigo());
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private void inativaTipoServico(@NotNull final Connection conn,
                                    @NotNull final String token,
                                    @NotNull final Long codEmpresa,
                                    @NotNull final TipoServicoRecapadora tipoServico) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE RECAPADORA_TIPO_SERVICO SET STATUS_ATIVO = FALSE " +
                    "WHERE CODIGO = ? AND COD_EMPRESA = ?");
            stmt.setLong(1, tipoServico.getCodigo());
            stmt.setLong(2, codEmpresa);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inativar tipo serviço de código: " + tipoServico.getCodigo());
            }
        } finally {
            closeStatement(stmt);
        }
    }

    @NotNull
    private TipoServicoRecapadora createTipoServicoRecapadora(@NotNull final ResultSet rSet) throws SQLException {
        final TipoServicoRecapadora tipoServico = new TipoServicoRecapadora();
        tipoServico.setCodigo(rSet.getLong("CODIGO"));
        tipoServico.setCodEmpresa(rSet.getLong("COD_EMPRESA"));
        tipoServico.setNome(rSet.getString("NOME"));
        tipoServico.setStatus(rSet.getBoolean("STATUS_ATIVO"));
        tipoServico.setEditavel(rSet.getBoolean("EDITAVEL"));
        tipoServico.setIncrementaVida(rSet.getBoolean("INCREMENTA_VIDA"));
        return tipoServico;
    }
}
