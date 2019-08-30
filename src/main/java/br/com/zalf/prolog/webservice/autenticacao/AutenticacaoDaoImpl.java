package br.com.zalf.prolog.webservice.autenticacao;

import br.com.zalf.prolog.webservice.commons.util.SessionIdentifierGenerator;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.errorhandling.exception.ResourceAlreadyDeletedException;
import br.com.zalf.prolog.webservice.interceptors.auth.authenticator.StatusSecured;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Classe responsável pela comunicação com o banco de dados da aplicação.
 */
public class AutenticacaoDaoImpl extends DatabaseConnection implements AutenticacaoDao {

    public AutenticacaoDaoImpl() {

    }

    @NotNull
    @Override
    public Autenticacao insertOrUpdate(@NotNull final Long cpf) throws SQLException {
        final String token = new SessionIdentifierGenerator().nextSessionId();
        return insert(cpf, token);
    }

    @Override
    public boolean delete(@NotNull final String token) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("DELETE FROM TOKEN_AUTENTICACAO TA WHERE TA.TOKEN = ?");
            stmt.setString(1, token);
            if (stmt.executeUpdate() == 0) {
                throw new ResourceAlreadyDeletedException();
            }
            return true;
        } finally {
            close(conn, stmt);
        }
    }

    @Override
    public boolean verifyIfTokenExists(@NotNull final String token,
                                       final boolean apenasUsuariosAtivos) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE TOKEN_AUTENTICACAO SET " +
                    "DATA_HORA = ? WHERE TOKEN = ? " +
                    "AND (SELECT C.STATUS_ATIVO " +
                    "FROM COLABORADOR C " +
                    "JOIN TOKEN_AUTENTICACAO TA ON C.CPF = TA.CPF_COLABORADOR AND TA.TOKEN = ?)::TEXT = ?");
            stmt.setObject(1, OffsetDateTime.now(Clock.systemUTC()));
            stmt.setString(2, token);
            stmt.setString(3, token);
            stmt.setString(4, apenasUsuariosAtivos ? Boolean.toString(true) : "%");
            return stmt.executeUpdate() > 0;
        } finally {
            close(conn, stmt);
        }
    }

    @Override
    public boolean verifyIfUserExists(@NotNull final Long cpf,
                                      @NotNull final LocalDate dataNascimento,
                                      final boolean apenasUsuariosAtivos) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT EXISTS(SELECT C.NOME FROM COLABORADOR C WHERE C.CPF = ? " +
                    "AND C.DATA_NASCIMENTO = ? AND C.STATUS_ATIVO::TEXT LIKE ?);");
            stmt.setLong(1, cpf);
            stmt.setObject(2, dataNascimento);
            stmt.setString(3, apenasUsuariosAtivos ? Boolean.toString(true) : "%");
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("EXISTS");
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return false;
    }

    @NotNull
    @Override
    public StatusSecured userHasPermission(@NotNull final String token,
                                           @NotNull final int[] permissions,
                                           final boolean needsToHaveAllPermissions,
                                           final boolean apenasUsuariosAtivos) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_COLABORADOR_VERIFICA_PERMISSOES_TOKEN(" +
                    "F_TOKEN                           := ?," +
                    "F_PERMISSSOES_NECESSARIAS         := ?," +
                    "F_PRECISA_TER_TODAS_AS_PERMISSOES := ?," +
                    "F_APENAS_USUARIOS_ATIVOS          := ?);");
            stmt.setString(1, token);
            stmt.setObject(2, permissions);
            stmt.setBoolean(3, needsToHaveAllPermissions);
            stmt.setBoolean(4, apenasUsuariosAtivos);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return generateStatusSecured(rSet);
            } else {
                throw new SQLException("Erro ao verificar permissões do colaborador!");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public StatusSecured userHasPermission(final long cpf,
                                           @NotNull final LocalDate dataNascimento,
                                           @NotNull int[] permissions,
                                           final boolean needsToHaveAllPermissions,
                                           final boolean apenasUsuariosAtivos) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_COLABORADOR_VERIFICA_PERMISSOES_CPF_DATA_NASCIMENTO(" +
                    "F_CPF                             := ?," +
                    "F_DATA_NASCIMENTO                 := ?," +
                    "F_PERMISSSOES_NECESSARIAS         := ?," +
                    "F_PRECISA_TER_TODAS_AS_PERMISSOES := ?," +
                    "F_APENAS_USUARIOS_ATIVOS          := ?);");
            stmt.setLong(1, cpf);
            stmt.setObject(2, dataNascimento);
            stmt.setObject(3, permissions);
            stmt.setBoolean(4, needsToHaveAllPermissions);
            stmt.setBoolean(5, apenasUsuariosAtivos);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return generateStatusSecured(rSet);
            } else {
                throw new SQLException("Erro ao verificar permissões do colaborador!");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private Autenticacao insert(@NotNull final Long cpf, @NotNull final String token) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        final Autenticacao autenticacao = new Autenticacao();
        autenticacao.setCpf(cpf);
        autenticacao.setToken(token);
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO TOKEN_AUTENTICACAO"
                    + "(CPF_COLABORADOR, COD_COLABORADOR, TOKEN) VALUES (?, (SELECT CODIGO FROM COLABORADOR WHERE CPF = ?), ?);");
            stmt.setLong(1, cpf);
            stmt.setLong(2, cpf);
            stmt.setString(3, token);
            int count = stmt.executeUpdate();
            if (count == 0) {
                autenticacao.setStatus(Autenticacao.ERROR);
                return autenticacao;
            }
        } finally {
            close(conn, stmt);
        }
        autenticacao.setStatus(Autenticacao.OK);
        return autenticacao;
    }

    @NotNull
    private StatusSecured generateStatusSecured(@NotNull final ResultSet rSet) throws Throwable {
        final boolean tokenValido = rSet.getBoolean("TOKEN_VALIDO");
        final boolean possuiPermisssao = rSet.getBoolean("POSSUI_PERMISSSAO");
        if (tokenValido && possuiPermisssao) {
            return StatusSecured.TOKEN_E_PERMISSAO_OK;
        } else if (tokenValido) {
            return StatusSecured.TOKEN_OK_SEM_PERMISSAO;
        } else {
            return StatusSecured.TOKEN_INVALIDO;
        }
    }
}