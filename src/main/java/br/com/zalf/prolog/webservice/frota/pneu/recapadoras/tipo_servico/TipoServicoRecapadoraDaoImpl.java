package br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico;

import br.com.zalf.prolog.webservice.commons.util.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                    "(SELECT ta.cpf_colaborador FROM TOKEN_AUTENTICACAO AS TA WHERE TOKEN = ?)), ?) RETURNING CODIGO;");
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
    public void atualizaTipoServicoRecapadora(@NotNull final Long codEmpresa,
                                              @NotNull final TipoServicoRecapadora tipoServico) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            inativaTipoServico(conn, codEmpresa, tipoServico);
            atualizaTipoServico(conn, codEmpresa, tipoServico);
        } finally {
            closeConnection(conn);
        }
    }

    @Override
    public List<TipoServicoRecapadora> getTiposServicosRecapadora(@NotNull final Long codEmpresa,
                                                                  final boolean ativas) throws SQLException {
        return null;
    }

    @Override
    public TipoServicoRecapadora getTipoServicoRecapadora(@NotNull final Long codEmpresa,
                                                          @NotNull final Long codTipoServico) throws SQLException {
        return null;
    }

    @Override
    public void alterarStatusTipoServicoRecapadora(@NotNull final String token,
                                                   @NotNull final Long codEmpresa,
                                                   @NotNull final TipoServicoRecapadora tipoServico) throws SQLException {

    }

    private void atualizaTipoServico(@NotNull final Connection conn,
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
}
