package br.com.zalf.prolog.webservice.autenticacao;

import br.com.zalf.prolog.webservice.commons.util.SessionIdentifierGenerator;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.errorhandling.exception.ResourceAlreadyDeletedException;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public boolean verifyIfTokenMarcacaoExists(@NotNull final String tokenMarcacao) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT EXISTS(SELECT TOKEN_SINCRONIZACAO_MARCACAO " +
                    " FROM INTERVALO_UNIDADE WHERE TOKEN_SINCRONIZACAO_MARCACAO = ?) AS EXISTE_TOKEN;");
            stmt.setString(1, tokenMarcacao);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("EXISTE_TOKEN");
            } else {
                throw new SQLException(
                        "Não foi possível verifica a existencia do token de sincronia de marcação: " + tokenMarcacao);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public boolean userHasPermission(@NotNull final String token,
                                     @NotNull final int[] permissions,
                                     final boolean needsToHaveAllPermissions,
                                     final boolean apenasUsuariosAtivos) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            String query = "SELECT CFP.COD_FUNCAO_PROLOG AS COD_PERMISSAO " +
                    "FROM TOKEN_AUTENTICACAO TA " +
                    "  JOIN COLABORADOR C ON C.CPF = TA.CPF_COLABORADOR " +
                    "  JOIN CARGO_FUNCAO_PROLOG_V11 CFP " +
                    "    ON CFP.COD_UNIDADE = C.COD_UNIDADE " +
                    "       AND CFP.COD_FUNCAO_COLABORADOR = C.COD_FUNCAO " +
                    "WHERE TA.TOKEN = ? AND C.STATUS_ATIVO::TEXT LIKE ?;";
            conn = getConnection();
            stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.setString(1, token);
            stmt.setString(2, apenasUsuariosAtivos ? Boolean.toString(true) : "%");
            rSet = stmt.executeQuery();
            final List<Integer> permissoes = Arrays.stream(permissions).boxed().collect(Collectors.toList());
            return verifyPermissions(rSet, permissoes, needsToHaveAllPermissions);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public boolean userHasPermission(final long cpf,
                                     @NotNull final LocalDate dataNascimento,
                                     @NotNull int[] permissions,
                                     final boolean needsToHaveAllPermissions,
                                     final boolean apenasUsuariosAtivos) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            String query = "SELECT CFP.COD_FUNCAO_PROLOG AS COD_PERMISSAO " +
                    "FROM COLABORADOR C " +
                    "  JOIN CARGO_FUNCAO_PROLOG_V11 CFP " +
                    "    ON CFP.COD_UNIDADE = C.COD_UNIDADE " +
                    "       AND CFP.COD_FUNCAO_COLABORADOR = C.COD_FUNCAO " +
                    "WHERE C.CPF = ? " +
                    "      AND C.DATA_NASCIMENTO = ? " +
                    "      AND C.STATUS_ATIVO::TEXT LIKE ?;";
            conn = getConnection();
            stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.setLong(1, cpf);
            stmt.setObject(2, dataNascimento);
            stmt.setString(3, apenasUsuariosAtivos ? Boolean.toString(true) : "%");
            rSet = stmt.executeQuery();
            final List<Integer> permissoes = Arrays.stream(permissions).boxed().collect(Collectors.toList());
            return verifyPermissions(rSet, permissoes, needsToHaveAllPermissions);
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
                    + "(CPF_COLABORADOR, TOKEN) VALUES (?, ?);");
            stmt.setLong(1, cpf);
            stmt.setString(2, token);
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

    private boolean verifyPermissions(@NotNull final ResultSet rSet,
                                      @NotNull final List<Integer> permissoes,
                                      final boolean needsToHaveAll) throws SQLException {
        if (!rSet.next()) {
            return false;
        }
        rSet.beforeFirst();
        while (rSet.next()) {
            if (needsToHaveAll) {
                if (!permissoes.contains(rSet.getInt("cod_permissao"))) {
                    return false;
                }
            } else {
                if (permissoes.contains(rSet.getInt("cod_permissao"))) {
                    return true;
                }
            }
        }
        return needsToHaveAll;
    }
}