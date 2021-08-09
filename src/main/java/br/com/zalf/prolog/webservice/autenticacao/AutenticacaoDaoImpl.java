package br.com.zalf.prolog.webservice.autenticacao;

import br.com.zalf.prolog.webservice.autenticacao.token.TokenGenerator;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.errorhandling.exception.ResourceAlreadyDeletedException;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.authorization.StatusSecured;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;

@SuppressWarnings("NullableProblems")
public class AutenticacaoDaoImpl extends DatabaseConnection implements AutenticacaoDao {

    public AutenticacaoDaoImpl() {

    }

    @NotNull
    @Override
    public Autenticacao createTokenByCpf(@NotNull final Long cpf) throws Throwable {
        final String token = new TokenGenerator().getNextToken();
        return insert(cpf, token);
    }

    @NotNull
    @Override
    public Autenticacao createTokenByCodColaborador(@NotNull final Long codColaborador) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from func_geral_get_or_insert_token_autenticacao(" +
                                                 "f_cod_colaborador => ?, " +
                                                 "f_token_autenticacao => ?);");
            stmt.setLong(1, codColaborador);
            stmt.setString(2, new TokenGenerator().getNextToken());
            rSet = stmt.executeQuery();
            final Autenticacao autenticacao = new Autenticacao();
            if (rSet.next()) {
                autenticacao.setCpf(rSet.getLong("CPF_COLABORADOR"));
                autenticacao.setToken(rSet.getString("TOKEN_AUTENTICACAO"));
                autenticacao.setStatus(Autenticacao.OK);
            } else {
                autenticacao.setStatus(Autenticacao.ERROR);
            }
            return autenticacao;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public boolean delete(@NotNull final String token) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("delete from token_autenticacao ta where ta.token = ?");
            stmt.setString(1, token);
            if (stmt.executeUpdate() == 0) {
                throw new ResourceAlreadyDeletedException();
            }
            return true;
        } finally {
            close(conn, stmt);
        }
    }

    @NotNull
    @Override
    public Optional<ColaboradorAutenticado> verifyIfTokenExists(@NotNull final String token,
                                                                final boolean apenasUsuariosAtivos) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("update token_autenticacao set " +
                                                 "data_hora = ? where token = ? " +
                                                 "and (select c.status_ativo " +
                                                 "from colaborador c " +
                                                 "join token_autenticacao ta on c.cpf = ta.cpf_colaborador and ta" +
                                                 ".token = ?)::text = ?" +
                                                 "returning cod_colaborador, cpf_colaborador");
            stmt.setObject(1, OffsetDateTime.now(Clock.systemUTC()));
            stmt.setString(2, token);
            stmt.setString(3, token);
            stmt.setString(4, apenasUsuariosAtivos ? Boolean.toString(true) : "%");
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return Optional.of(new ColaboradorAutenticado(
                        rSet.getLong("COD_COLABORADOR"),
                        rSet.getLong("CPF_COLABORADOR"),
                        StatusSecured.TOKEN_E_PERMISSAO_OK));
            } else {
                return Optional.empty();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Optional<ColaboradorAutenticado> verifyIfUserExists(@NotNull final Long cpf,
                                                               @NotNull final LocalDate dataNascimento,
                                                               final boolean apenasUsuariosAtivos) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select c.codigo as cod_colaborador " +
                                                 "from colaborador c " +
                                                 "where c.cpf = ? " +
                                                 "and c.data_nascimento = ? " +
                                                 "and c.status_ativo::text like ?;");
            stmt.setLong(1, cpf);
            stmt.setObject(2, dataNascimento);
            stmt.setString(3, apenasUsuariosAtivos ? Boolean.toString(true) : "%");
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return Optional.of(new ColaboradorAutenticado(
                        rSet.getLong("COD_COLABORADOR"),
                        cpf,
                        StatusSecured.TOKEN_E_PERMISSAO_OK));
            } else {
                return Optional.empty();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Optional<ColaboradorAutenticado> userHasPermission(@NotNull final String token,
                                                              @NotNull final int[] permissions,
                                                              final boolean needsToHaveAllPermissions,
                                                              final boolean apenasUsuariosAtivos) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from func_colaborador_verifica_permissoes_token(" +
                                                 "f_token                           := ?," +
                                                 "f_permisssoes_necessarias         := ?," +
                                                 "f_precisa_ter_todas_as_permissoes := ?," +
                                                 "f_apenas_usuarios_ativos          := ?);");
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
    public Optional<ColaboradorAutenticado> userHasPermission(final long cpf,
                                                              @NotNull final LocalDate dataNascimento,
                                                              @NotNull final int[] permissions,
                                                              final boolean needsToHaveAllPermissions,
                                                              final boolean apenasUsuariosAtivos) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from func_colaborador_verifica_permissoes_cpf_data_nascimento(" +
                                                 "f_cpf                             := ?," +
                                                 "f_data_nascimento                 := ?," +
                                                 "f_permisssoes_necessarias         := ?," +
                                                 "f_precisa_ter_todas_as_permissoes := ?," +
                                                 "f_apenas_usuarios_ativos          := ?);");
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
            stmt = conn.prepareStatement("insert into token_autenticacao"
                                                 + "(cpf_colaborador, cod_colaborador, token) values (?, (select " +
                                                 "codigo from colaborador where cpf = ?), ?);");
            stmt.setLong(1, cpf);
            stmt.setLong(2, cpf);
            stmt.setString(3, token);
            final int count = stmt.executeUpdate();
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
    private Optional<ColaboradorAutenticado> generateStatusSecured(@NotNull final ResultSet rSet) throws Throwable {
        final Long cpf = rSet.getObject("CPF_COLABORADOR", Long.class);
        final Long codigo = rSet.getObject("COD_COLABORADOR", Long.class);
        if (cpf != null && codigo != null) {
            final boolean tokenValido = rSet.getBoolean("TOKEN_VALIDO");
            final boolean possuiPermisssao = rSet.getBoolean("POSSUI_PERMISSSAO");
            final StatusSecured statusSecured;
            if (tokenValido && possuiPermisssao) {
                statusSecured = StatusSecured.TOKEN_E_PERMISSAO_OK;
            } else if (tokenValido) {
                statusSecured = StatusSecured.TOKEN_OK_SEM_PERMISSAO;
            } else {
                statusSecured = StatusSecured.TOKEN_INVALIDO;
            }
            return Optional.of(new ColaboradorAutenticado(codigo, cpf, statusSecured));
        } else {
            return Optional.empty();
        }
    }
}