package br.com.zalf.prolog.webservice.frota.socorrorota.opcaoproblema;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.socorrorota.opcaoproblema._model.*;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 09/12/19.
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */

public final class OpcaoProblemaDaoImpl extends DatabaseConnection implements OpcaoProblemaDao {
    @NotNull
    @Override
    public List<OpcaoProblemaAberturaSocorro> getOpcoesProblemasDisponiveisAberturaSocorroByEmpresa(
            @NotNull final Long codEmpresa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT CODIGO, DESCRICAO, OBRIGA_DESCRICAO " +
                    "FROM SOCORRO_ROTA_OPCAO_PROBLEMA " +
                    "WHERE COD_EMPRESA = ? AND STATUS_ATIVO IS TRUE " +
                    "ORDER BY DESCRICAO;");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            final List<OpcaoProblemaAberturaSocorro> opcoesProblemas = new ArrayList<>();
            while (rSet.next()) {
                opcoesProblemas.add(OpcaoProblemaConverter.createOpcaoProblemaAberturaSocorro(rSet));
            }
            return opcoesProblemas;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<OpcaoProblemaSocorroRotaListagem> getOpcoesProblemasSocorroRotaByEmpresa(@NotNull final Long codEmpresa)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareCall("{CALL FUNC_SOCORRO_ROTA_OPCOES_PROBLEMAS_LISTAGEM(F_COD_EMPRESA := ?)}");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            final List<OpcaoProblemaSocorroRotaListagem> opcoesProblemas = new ArrayList<>();
            if (rSet.next()) {
                while (rSet.next()) {
                    opcoesProblemas.add(OpcaoProblemaConverter.createOpcaoProblemaSocorroRota(rSet));
                }
                return opcoesProblemas;
            } else {
                throw new SQLException("Erro ao buscar opções de problemas");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public OpcaoProblemaSocorroRotaVisualizacao getOpcaoProblemaSocorroRotaVisualizacao(
            @NotNull final Long codOpcaoProblema) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareCall("{CALL FUNC_SOCORRO_ROTA_OPCAO_PROBLEMA_ITEM(F_COD_OPCAO_PROBLEMA := ?)}");
            stmt.setLong(1, codOpcaoProblema);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return OpcaoProblemaConverter.createOpcaoProblemaSocorroRotaVisualizacao(rSet);
            } else {
                throw new Throwable("Erro ao buscar opção de problema pelo código: " + codOpcaoProblema);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Long insertOpcoesProblemas(
            @NotNull final OpcaoProblemaSocorroRotaCadastro opcaoProblemaSocorroRotaCadastro)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_SOCORRO_ROTA_INSERT_OPCOES_PROBLEMAS(" +
                    "F_COD_EMPRESA := ?," +
                    "F_DESCRICAO := ?," +
                    "F_OBRIGA_DESCRICAO := ?," +
                    "F_COD_COLABORADOR := ?," +
                    "F_DATA_HORA := ?) AS CODIGO;");
            stmt.setLong(1, opcaoProblemaSocorroRotaCadastro.getCodEmpresa());
            stmt.setString(2, opcaoProblemaSocorroRotaCadastro.getDescricao());
            stmt.setBoolean(3, opcaoProblemaSocorroRotaCadastro.isObrigaDescricao());
            stmt.setLong(4, opcaoProblemaSocorroRotaCadastro.getCodColaborador());
            stmt.setObject(5, Now.localDateTimeUtc());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new Throwable("Erro ao inserir uma opção de problema");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void updateOpcoesProblemas(
            @NotNull final OpcaoProblemaSocorroRotaEdicao opcaoProblemaSocorroRotaEdicao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_SOCORRO_ROTA_UPDATE_OPCOES_PROBLEMAS(" +
                    "F_COD_OPCAO_PROBLEMA := ?," +
                    "F_COD_EMPRESA := ?," +
                    "F_COD_COLABORADOR := ?," +
                    "F_DESCRICAO := ?," +
                    "F_OBRIGA_DESCRICAO := ?," +
                    "F_DATA_HORA := ?) AS CODIGO;");
            stmt.setLong(1, opcaoProblemaSocorroRotaEdicao.getCodOpcaoProblema());
            stmt.setLong(2, opcaoProblemaSocorroRotaEdicao.getCodEmpresa());
            stmt.setLong(3, opcaoProblemaSocorroRotaEdicao.getCodColaborador());
            stmt.setString(4, opcaoProblemaSocorroRotaEdicao.getDescricao());
            stmt.setBoolean(5, opcaoProblemaSocorroRotaEdicao.isObrigaDescricao());
            stmt.setObject(6, Now.localDateTimeUtc());

            rSet = stmt.executeQuery();
            if (rSet.next()) {
                if (!rSet.getBoolean(1)) {
                    throw new IllegalStateException("Erro ao editar esta opção de problema.");
                }
            } else {
                throw new IllegalStateException("Erro ao editar esta opção de problema.");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void updateStatusAtivo(@NotNull final OpcaoProblemaSocorroRotaStatus opcaoProblemaSocorroRotaStatus) throws
            Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareCall("{CALL FUNC_SOCORRO_ROTA_UPDATE_STATUS_OPCAO_PROBLEMA(" +
                    "F_COD_EMPRESA := ?," +
                    "F_COD_COLABORADOR := ?," +
                    "F_COD_OPCAO_PROBLEMA := ?," +
                    "F_STATUS_ATIVO := ?)}");
            stmt.setLong(1, opcaoProblemaSocorroRotaStatus.getCodEmpresa());
            stmt.setLong(2, opcaoProblemaSocorroRotaStatus.getCodColaborador());
            stmt.setLong(3, opcaoProblemaSocorroRotaStatus.getCodOpcaoProblema());
            stmt.setBoolean(4, opcaoProblemaSocorroRotaStatus.isStatusAtivo());
            stmt.execute();
        } finally {
            close(conn, stmt);
        }
    }
}