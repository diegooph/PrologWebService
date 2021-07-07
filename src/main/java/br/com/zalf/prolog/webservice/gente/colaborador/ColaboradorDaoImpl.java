package br.com.zalf.prolog.webservice.gente.colaborador;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.colaborador.model.ColaboradorEdicao;
import br.com.zalf.prolog.webservice.gente.colaborador.model.ColaboradorInsercao;
import br.com.zalf.prolog.webservice.gente.colaborador.model.ColaboradorListagem;
import br.com.zalf.prolog.webservice.gente.controlejornada.DadosIntervaloChangedListener;
import br.com.zalf.prolog.webservice.gente.empresa.EmpresaDao;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import br.com.zalf.prolog.webservice.permissao.Visao;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilar;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static br.com.zalf.prolog.webservice.commons.util.database.StatementUtils.bindValueOrNull;

/**
 * Classe ColaboradorDaoImpl, responsavel pela execução da lógica e comunicação com a interface de dados
 */
public class ColaboradorDaoImpl extends DatabaseConnection implements ColaboradorDao {

    @Override
    public void insert(@NotNull final ColaboradorInsercao colaborador,
                       @NotNull final DadosIntervaloChangedListener intervaloListener,
                       @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
                       @NotNull final String userToken) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("SELECT FUNC_COLABORADOR_INSERT_COLABORADOR("
                                                 + "F_CPF := ?,"
                                                 + "F_MATRICULA_AMBEV := ?,"
                                                 + "F_MATRICULA_TRANS := ?,"
                                                 + "F_DATA_NASCIMENTO := ?::DATE,"
                                                 + "F_DATA_ADMISSAO := ?::DATE,"
                                                 + "F_NOME := ?::VARCHAR,"
                                                 + "F_COD_SETOR := ?,"
                                                 + "F_COD_FUNCAO := ?::INTEGER,"
                                                 + "F_COD_UNIDADE := ?::INTEGER,"
                                                 + "F_COD_PERMISSAO := ?,"
                                                 + "F_COD_EMPRESA := ?,"
                                                 + "F_COD_EQUIPE := ?,"
                                                 + "F_PIS := ?::VARCHAR,"
                                                 + "F_SIGLA_ISO2 := ?::VARCHAR,"
                                                 + "F_PREFIXO_PAIS := ?,"
                                                 + "F_TELEFONE := ?::TEXT,"
                                                 + "F_EMAIL := ?::EMAIL,"
                                                 + "F_COD_UNIDADE_CADASTRO := ?::INTEGER,"
                                                 + "F_TOKEN := ?::TEXT) AS CODIGO");
            stmt.setLong(1, Long.parseLong(colaborador.getCpf().trim()));
            bindValueOrNull(stmt, 2, colaborador.getMatriculaAmbev(), SqlType.INTEGER);
            bindValueOrNull(stmt, 3, colaborador.getMatriculaTrans(), SqlType.INTEGER);
            stmt.setObject(4, colaborador.getDataNascimento());
            stmt.setObject(5, colaborador.getDataAdmissao());
            stmt.setString(6, StringUtils.trimToNull(colaborador.getNome()));
            stmt.setLong(7, colaborador.getCodSetor());
            stmt.setLong(8, colaborador.getCodFuncao());
            stmt.setLong(9, colaborador.getCodUnidade());
            stmt.setLong(10, colaborador.getCodPermissao());
            stmt.setLong(11, colaborador.getCodEmpresa());
            stmt.setLong(12, colaborador.getCodEquipe());
            bindValueOrNull(stmt, 13, colaborador.getPis(), SqlType.TEXT);
            bindValueOrNull(stmt,
                            14,
                            colaborador.getTelefone() != null ? colaborador.getTelefone().getSiglaIso2() : null,
                            SqlType.VARCHAR);
            bindValueOrNull(stmt,
                            15,
                            colaborador.getTelefone() != null ? colaborador.getTelefone().getPrefixoPais() : null,
                            SqlType.INTEGER);
            bindValueOrNull(stmt,
                            16,
                            colaborador.getTelefone() != null ? colaborador.getTelefone().getNumero() : null,
                            SqlType.TEXT);
            bindValueOrNull(stmt, 17, colaborador.getEmail(), SqlType.TEXT);
            stmt.setLong(18, colaborador.getCodUnidade());
            stmt.setString(19, userToken);

            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codColaboradorInserido = rSet.getLong("CODIGO");
                if (codColaboradorInserido <= 0) {
                    throw new SQLException("Erro ao inserir o colaborador:\n" +
                                                   "codColaboradorInserido: " + codColaboradorInserido);
                }

                // Avisamos os Listeners que um colaborador foi inserido.
                intervaloListener.onColaboradorInserido(conn, Injection.provideEmpresaDao(), colaborador);
                checklistOfflineListener.onInsertColaborador(conn, codColaboradorInserido);

                // Tudo certo, commita.
                conn.commit();
            } else {
                throw new SQLException("Erro ao inserir o colaborador");
            }
        } catch (final Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void update(@NotNull final ColaboradorEdicao colaborador,
                       @NotNull final DadosIntervaloChangedListener intervaloListener,
                       @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
                       @NotNull final String userToken) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("SELECT FUNC_COLABORADOR_UPDATE_COLABORADOR("
                                                 + "F_COD_COLABORADOR := ?,"
                                                 + "F_CPF := ?,"
                                                 + "F_MATRICULA_AMBEV := ?,"
                                                 + "F_MATRICULA_TRANS := ?,"
                                                 + "F_DATA_NASCIMENTO := ?::DATE,"
                                                 + "F_DATA_ADMISSAO := ?::DATE,"
                                                 + "F_NOME := ?::VARCHAR,"
                                                 + "F_COD_SETOR := ?,"
                                                 + "F_COD_FUNCAO := ?::INTEGER,"
                                                 + "F_COD_UNIDADE := ?::INTEGER,"
                                                 + "F_COD_PERMISSAO := ?,"
                                                 + "F_COD_EMPRESA := ?,"
                                                 + "F_COD_EQUIPE := ?,"
                                                 + "F_PIS := ?::VARCHAR,"
                                                 + "F_SIGLA_ISO2 := ?::VARCHAR,"
                                                 + "F_PREFIXO_PAIS := ?,"
                                                 + "F_TELEFONE := ?::TEXT,"
                                                 + "F_EMAIL := ?::EMAIL,"
                                                 + "F_TOKEN := ?::TEXT) AS CODIGO");
            stmt.setLong(1, colaborador.getCodigo());
            stmt.setLong(2, Long.parseLong(colaborador.getCpf().trim()));
            bindValueOrNull(stmt, 3, colaborador.getMatriculaAmbev(), SqlType.INTEGER);
            bindValueOrNull(stmt, 4, colaborador.getMatriculaTrans(), SqlType.INTEGER);
            stmt.setObject(5, colaborador.getDataNascimento());
            stmt.setObject(6, colaborador.getDataAdmissao());
            stmt.setString(7, StringUtils.trimToNull(colaborador.getNome()));
            stmt.setLong(8, colaborador.getCodSetor());
            stmt.setLong(9, colaborador.getCodFuncao());
            stmt.setLong(10, colaborador.getCodUnidade());
            stmt.setLong(11, colaborador.getCodPermissao());
            stmt.setLong(12, colaborador.getCodEmpresa());
            stmt.setLong(13, colaborador.getCodEquipe());
            bindValueOrNull(stmt, 14, colaborador.getPis(), SqlType.TEXT);
            bindValueOrNull(stmt,
                            15,
                            colaborador.getTelefone() != null ? colaborador.getTelefone().getSiglaIso2() : null,
                            SqlType.VARCHAR);
            bindValueOrNull(stmt,
                            16,
                            colaborador.getTelefone() != null ? colaborador.getTelefone().getPrefixoPais() : null,
                            SqlType.INTEGER);
            bindValueOrNull(stmt,
                            17,
                            colaborador.getTelefone() != null ? colaborador.getTelefone().getNumero() : null,
                            SqlType.TEXT);
            bindValueOrNull(stmt, 18, colaborador.getEmail(), SqlType.TEXT);
            stmt.setString(19, userToken);

            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codColaboradorAtualizado = rSet.getLong("CODIGO");
                if (codColaboradorAtualizado <= 0) {
                    throw new SQLException("Erro ao atualizar o colaborador:\n" +
                                                   "CPF: " + colaborador.getCpf() + "\n" +
                                                   "codColaborador:" + colaborador.getCodigo());
                }

                // Avisa os Listeners que atualizamos um colaborador.
                intervaloListener.onColaboradorAtualizado(
                        conn,
                        Injection.provideEmpresaDao(),
                        this,
                        colaborador,
                        Long.parseLong(colaborador.getCpf()));
                checklistOfflineListener.onUpdateColaborador(conn, codColaboradorAtualizado);

                // Tudo certo, commita.
                conn.commit();
            } else {
                throw new SQLException("Erro ao atualizar o colaborador com CPF: " + colaborador.getCpf());
            }
        } catch (final Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void updateStatus(
            @NotNull final Long cpf,
            @NotNull final Colaborador colaborador,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("UPDATE COLABORADOR SET STATUS_ATIVO = ? WHERE CPF = ? RETURNING CODIGO;");
            stmt.setBoolean(1, colaborador.isAtivo());
            stmt.setLong(2, cpf);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codColaboradorAtualizado = rSet.getLong("CODIGO");
                if (codColaboradorAtualizado <= 0) {
                    throw new SQLException("Erro ao atualizar o status do colaborador:\n" +
                                                   "CPF: " + cpf + "\n" +
                                                   "codColaboradorAtualizado: " + codColaboradorAtualizado);
                }
                // Avisamos que o colaborador teve seu status atualizado
                checklistOfflineListener.onUpdateStatusColaborador(conn, codColaboradorAtualizado);

                conn.commit();
            } else {
                throw new SQLException("Erro ao atualizar o status do colaborador com CPF: " + cpf);
            }
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void delete(@NotNull final Long cpf,
                       @NotNull final DadosIntervaloChangedListener intervaloListener,
                       @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("UPDATE COLABORADOR SET "
                                                 + "STATUS_ATIVO = FALSE, data_demissao = ? "
                                                 + "WHERE CPF = ? RETURNING CODIGO;");
            stmt.setObject(1, LocalDate.now(Clock.systemUTC()));
            stmt.setLong(2, cpf);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codColaboradorInativado = rSet.getLong("CODIGO");
                if (codColaboradorInativado <= 0) {
                    throw new SQLException("Erro ao inativar colaborador:\n" +
                                                   "CPF: " + cpf + "\n" +
                                                   "codColaboradorInativado: " + codColaboradorInativado);
                }

                // Já inativamos o colaborador, repassamos o evento aos Listeners.
                intervaloListener.onColaboradorInativado(conn, this, cpf);
                checklistOfflineListener.onDeleteColaborador(conn, codColaboradorInativado);

                // Se deu tudo certo, commita.
                conn.commit();
            } else {
                throw new SQLException("Erro ao inativar colaborador com CPF: " + cpf);
            }
        } catch (final Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    @Nullable
    public Colaborador getByCpf(final Long cpf, final boolean apenasAtivos) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT C.CODIGO, C.CPF, C.PIS, C.MATRICULA_AMBEV, C.MATRICULA_TRANS, "
                                                 + "C.DATA_NASCIMENTO, C.DATA_ADMISSAO, C.DATA_DEMISSAO, C" +
                                                 ".STATUS_ATIVO, "
                                                 + "C.NOME AS NOME_COLABORADOR, EM.NOME AS NOME_EMPRESA, EM.CODIGO AS" +
                                                 " COD_EMPRESA, EM" +
                                                 ".LOGO_THUMBNAIL_URL, "
                                                 +
                                                 "R.REGIAO AS NOME_REGIONAL, R.CODIGO AS COD_REGIONAL, U.NOME AS " +
                                                 "NOME_UNIDADE, U.CODIGO AS " +
                                                 "COD_UNIDADE, EQ.NOME AS NOME_EQUIPE, EQ.CODIGO AS COD_EQUIPE, "
                                                 + "S.NOME AS NOME_SETOR, S.CODIGO AS COD_SETOR, "
                                                 + "C.COD_FUNCAO, F.NOME AS NOME_FUNCAO, C.COD_PERMISSAO AS " +
                                                 "PERMISSAO, U.TIMEZONE AS TZ_UNIDADE, "
                                                 + "CT.SIGLA_ISO2, CT.PREFIXO_PAIS, CT.NUMERO_TELEFONE, CE.EMAIL "
                                                 + "FROM COLABORADOR C JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO "
                                                 + " JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE "
                                                 + " JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE "
                                                 + " JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U" +
                                                 ".COD_EMPRESA"
                                                 + " JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL "
                                                 + " JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S" +
                                                 ".COD_UNIDADE "
                                                 + " LEFT JOIN COLABORADOR_TELEFONE CT ON C.CODIGO = CT" +
                                                 ".COD_COLABORADOR "
                                                 + " LEFT JOIN COLABORADOR_EMAIL CE ON C.CODIGO = CE.COD_COLABORADOR "
                                                 + "WHERE CPF = ? "
                                                 + " AND (? = 1 OR C.STATUS_ATIVO = ?)");

            stmt.setLong(1, cpf);
            if (apenasAtivos) {
                stmt.setInt(2, 0);
                stmt.setBoolean(3, true);
            } else {
                stmt.setInt(2, 1);
                stmt.setBoolean(3, false);
            }

            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Colaborador c = ColaboradorConverter.createColaborador(rSet);
                c.setVisao(getVisaoByCpf(c.getCpf()));
                return c;
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return null;
    }

    @NotNull
    @Override
    public List<Colaborador> getAllByUnidade(@NotNull final Long codUnidade, final boolean apenasAtivos)
            throws Throwable {
        return internalGetAll(codUnidade, apenasAtivos, true);
    }

    @NotNull
    @Override
    public List<ColaboradorListagem> getAllByUnidades(@NotNull final List<Long> codUnidades,
                                                      final boolean apenasAtivos) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_COLABORADOR_GET_ALL_BY_UNIDADES(F_COD_UNIDADES := ?," +
                                                 " F_APENAS_ATIVOS := ?) AS COLABORADOR");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            stmt.setBoolean(2, apenasAtivos);
            rSet = stmt.executeQuery();
            final List<ColaboradorListagem> colaboradores = new ArrayList<>();
            while (rSet.next()) {
                colaboradores.add(ColaboradorConverter.createColaboradorListagem(rSet));
            }
            return colaboradores;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<Colaborador> getAllByEmpresa(@NotNull final Long codEmpresa, final boolean apenasAtivos)
            throws Throwable {
        return internalGetAll(codEmpresa, apenasAtivos, false);
    }

    @Override
    public List<Colaborador> getMotoristasAndAjudantes(final Long codUnidade) throws SQLException {
        final List<Colaborador> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                                                 "  C.CODIGO, " +
                                                 "  C.CPF, " +
                                                 "  C.PIS, " +
                                                 "  C.MATRICULA_AMBEV, " +
                                                 "  C.MATRICULA_TRANS, " +
                                                 "  C.DATA_NASCIMENTO, " +
                                                 "  C.DATA_ADMISSAO, " +
                                                 "  C.DATA_DEMISSAO, " +
                                                 "  C.STATUS_ATIVO, " +
                                                 "  initcap(C.NOME) AS NOME_COLABORADOR, " +
                                                 "  EM.NOME         AS NOME_EMPRESA, " +
                                                 "  EM.CODIGO       AS COD_EMPRESA, " +
                                                 "  EM.LOGO_THUMBNAIL_URL, " +
                                                 "  R.REGIAO        AS NOME_REGIONAL, " +
                                                 "  R.CODIGO        AS COD_REGIONAL, " +
                                                 "  U.NOME          AS NOME_UNIDADE, " +
                                                 "  U.CODIGO        AS COD_UNIDADE, " +
                                                 "  EQ.NOME         AS NOME_EQUIPE, " +
                                                 "  EQ.CODIGO       AS COD_EQUIPE, " +
                                                 "  S.NOME          AS NOME_SETOR, " +
                                                 "  S.CODIGO        AS COD_SETOR, " +
                                                 "  C.COD_FUNCAO, " +
                                                 "  F.NOME          AS NOME_FUNCAO, " +
                                                 "  C.COD_PERMISSAO AS PERMISSAO, " +
                                                 "  U.TIMEZONE      AS TZ_UNIDADE, " +
                                                 "  CT.SIGLA_ISO2, " +
                                                 "  CT.PREFIXO_PAIS, " +
                                                 "  CT.NUMERO_TELEFONE, " +
                                                 "  CE.EMAIL " +
                                                 "FROM COLABORADOR C " +
                                                 "  JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO " +
                                                 "  JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE " +
                                                 "  JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE " +
                                                 "  JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U" +
                                                 ".COD_EMPRESA " +
                                                 "  JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL " +
                                                 "  JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S" +
                                                 ".COD_UNIDADE " +
                                                 "  LEFT JOIN COLABORADOR_TELEFONE CT ON C.CODIGO = CT" +
                                                 ".COD_COLABORADOR " +
                                                 "  LEFT JOIN COLABORADOR_EMAIL CE ON C.CODIGO = CE.COD_COLABORADOR " +
                                                 "  JOIN unidade_funcao_produtividade UFP ON UFP.cod_unidade = C" +
                                                 ".cod_unidade AND " +
                                                 "                                           (C.cod_funcao = UFP" +
                                                 ".cod_funcao_ajudante OR " +
                                                 "                                            C.COD_FUNCAO = UFP" +
                                                 ".cod_funcao_motorista) " +
                                                 "WHERE C.COD_UNIDADE = ? " +
                                                 "ORDER BY 8");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final Colaborador c = ColaboradorConverter.createColaborador(rSet);
                list.add(c);
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return list;
    }

    @Override
    public boolean verifyIfCpfExists(final Long cpf, final Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT EXISTS(SELECT C.NOME FROM "
                                                 + "COLABORADOR C WHERE C.CPF = ? AND C.cod_unidade = ?)");
            stmt.setLong(1, cpf);
            stmt.setLong(2, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("EXISTS");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return false;
    }

    @NotNull
    @Override
    public Colaborador getByToken(@NotNull final String token) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT C.CODIGO, C.CPF, C.PIS, C.MATRICULA_AMBEV, C.MATRICULA_TRANS, "
                                                 + "C.DATA_NASCIMENTO, C.DATA_ADMISSAO, C.DATA_DEMISSAO, C" +
                                                 ".STATUS_ATIVO, "
                                                 + "C.NOME AS NOME_COLABORADOR, EM.NOME AS NOME_EMPRESA, EM.CODIGO AS" +
                                                 " COD_EMPRESA, EM" +
                                                 ".LOGO_THUMBNAIL_URL, "
                                                 + "R.REGIAO AS NOME_REGIONAL, R.CODIGO AS COD_REGIONAL, U.NOME AS " +
                                                 "NOME_UNIDADE, U.CODIGO AS " +
                                                 "COD_UNIDADE, EQ.NOME AS NOME_EQUIPE, EQ.CODIGO AS COD_EQUIPE, "
                                                 + "S.NOME AS NOME_SETOR, S.CODIGO AS COD_SETOR, "
                                                 + "C.COD_FUNCAO, F.NOME AS NOME_FUNCAO, C.COD_PERMISSAO AS " +
                                                 "PERMISSAO, U.TIMEZONE AS TZ_UNIDADE, "
                                                 + "CT.SIGLA_ISO2, CT.PREFIXO_PAIS, CT.NUMERO_TELEFONE, CE.EMAIL "
                                                 + "FROM COLABORADOR C JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO "
                                                 + " JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE "
                                                 + " JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE "
                                                 + " JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U" +
                                                 ".COD_EMPRESA "
                                                 + " JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL "
                                                 + " JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S" +
                                                 ".COD_UNIDADE "
                                                 + " LEFT JOIN COLABORADOR_TELEFONE CT ON C.CODIGO = CT" +
                                                 ".COD_COLABORADOR "
                                                 + " LEFT JOIN COLABORADOR_EMAIL CE ON C.CODIGO = CE.COD_COLABORADOR "
                                                 + " JOIN TOKEN_AUTENTICACAO TA ON TA.TOKEN = ? AND TA" +
                                                 ".CPF_COLABORADOR = C.CPF "
                                                 + "WHERE C.STATUS_ATIVO = TRUE");
            stmt.setString(1, token);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Colaborador c = ColaboradorConverter.createColaborador(rSet);
                c.setVisao(getVisaoByCpf(c.getCpf()));
                return c;
            } else {
                throw new IllegalStateException("Colaborador não encontrado com o token: " + token);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<Colaborador> getColaboradoresComAcessoFuncaoByUnidade(@NotNull final Long codUnidade,
                                                                      final int codFuncaoProLog) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * " +
                                                 "from func_colaborador_get_colaboradores_acesso_funcao_prolog(" +
                                                 "f_cod_unidade => ?, " +
                                                 "f_cod_funcao_prolog => ?);");
            stmt.setLong(1, codUnidade);
            stmt.setInt(2, codFuncaoProLog);
            rSet = stmt.executeQuery();

            if (!rSet.next()) {
                return Collections.emptyList();
            } else {
                final List<Colaborador> colaboradores = new ArrayList<>();
                do {
                    final Colaborador colaborador = new Colaborador();
                    colaborador.setCpf(rSet.getLong("CPF"));
                    colaborador.setNome(rSet.getString("NOME_COLABORADOR"));
                    colaborador.setDataNascimento(rSet.getDate("DATA_NASCIMENTO"));
                    colaborador.setFuncao(ColaboradorConverter.createFuncao(rSet));
                    colaboradores.add(colaborador);
                } while (rSet.next());

                return colaboradores;
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    /**
     * Esse método não lida com a possibilidade de o código unidade não existir ou de o CPF pelo qual você busca não
     * estar cadastrado no banco. Tenha certeza de que o {@link Colaborador} do qual vocẽ está utilizando o CPF esteja
     * cadastrado no banco.
     *
     * @param cpf Um CPF.
     * @return O código da {@link Unidade}.
     *
     * @throws SQLException Caso aconteça algum erro na requisação ao banco.
     */
    @NotNull
    @Override
    public Long getCodUnidadeByCpf(@NotNull final Long cpf) throws SQLException {
        Preconditions.checkNotNull(cpf, "cpf não pode ser null!");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT COD_UNIDADE FROM COLABORADOR C WHERE C.CPF = ?;");
            stmt.setLong(1, cpf);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("COD_UNIDADE");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }

        throw new IllegalStateException("Unidade não encontrada para o CPF: " + cpf);
    }

    @Override
    public boolean colaboradorTemAcessoFuncao(@NotNull final Long cpf,
                                              final int codPilar,
                                              final int codFuncaoProLog) throws SQLException {
        ResultSet rSet = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * " +
                                                 "from func_colaborador_tem_permissao_funcao_prolog(" +
                                                 "f_cpf_colaborador => ?, " +
                                                 "f_cod_pilar_prolog => ?, " +
                                                 "f_cod_funcao_prolog => ?) as exists;");
            stmt.setLong(1, cpf);
            stmt.setInt(2, codPilar);
            stmt.setInt(3, codFuncaoProLog);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("EXISTS");
            }
            return false;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Long getCodColaboradorByCpfAndCodEmpresa(@NotNull final Connection conn,
                                                    @NotNull final Long codEmpresa,
                                                    @NotNull final String cpfColaborador) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT C.CODIGO " +
                                                 "FROM COLABORADOR C " +
                                                 "WHERE C.CPF = ? " +
                                                 "AND C.COD_EMPRESA = ?;");
            stmt.setLong(1, Colaborador.formatCpf(cpfColaborador));
            stmt.setLong(2, codEmpresa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codColaborador = rSet.getLong("CODIGO");
                if (codColaborador <= 0) {
                    throw new SQLException("Erro ao buscar código do colaborador:" +
                                                   "\ncpfColaborador: " + cpfColaborador + "" +
                                                   "\ncodColaborador: " + codColaborador);
                }
                return codColaborador;
            } else {
                throw new SQLException("Erro ao buscar código do colaborador:\n" +
                                               "cpfColaborador: " + cpfColaborador);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Long getCodColaboradorByCpfAndCodEmpresa(@NotNull final Long codEmpresa,
                                                    @NotNull final String cpfColaborador) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            return getCodColaboradorByCpfAndCodEmpresa(conn, codEmpresa, cpfColaborador);
        } finally {
            close(conn);
        }
    }

    @NotNull
    @Override
    public Long getCodColaboradorByCpfAndCodColaboradorBase(@NotNull final Long codColaboradorBase,
                                                            @NotNull final String cpf) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select c.codigo as codigo " +
                                                 "from colaborador c " +
                                                 "where c.cpf = ? " +
                                                 "  and c.cod_empresa = (select cbase.cod_empresa " +
                                                 "                       from colaborador cbase " +
                                                 "                       where cbase.codigo = ?);");
            stmt.setLong(1, Colaborador.formatCpf(cpf));
            stmt.setLong(2, codColaboradorBase);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codColaborador = rSet.getLong("codigo");
                if (codColaborador <= 0) {
                    throw new SQLException("Erro ao buscar código do colaborador:" +
                                                   "\ncpf: " + cpf + "" +
                                                   "\ncodColaboradorBase: " + codColaboradorBase);
                }
                return codColaborador;
            } else {
                throw new SQLException("Erro ao buscar código do colaborador:\ncpf: " + cpf);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private List<Colaborador> internalGetAll(@NotNull final Long codigoFiltro,
                                             final boolean apenasAtivos,
                                             final boolean porUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            if (porUnidade) {
                stmt = conn.prepareStatement("SELECT * FROM FUNC_COLABORADOR_GET_ALL_BY_UNIDADE(?, ?);");
            } else {
                stmt = conn.prepareStatement("SELECT * FROM FUNC_COLABORADOR_GET_ALL_BY_EMPRESA(?, ?);");
            }
            stmt.setLong(1, codigoFiltro);
            if (apenasAtivos) {
                stmt.setBoolean(2, true);
            } else {
                stmt.setNull(2, Types.BOOLEAN);
            }

            rSet = stmt.executeQuery();
            final List<Colaborador> colaboradores = new ArrayList<>();
            while (rSet.next()) {
                colaboradores.add(ColaboradorConverter.createColaborador(rSet));
            }
            return colaboradores;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private Visao getVisaoByCpf(final Long cpf) throws SQLException {
        final Visao visao = new Visao();
        List<Pilar> pilares;
        ResultSet rSet = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        final EmpresaDao empresaDao = Injection.provideEmpresaDao();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * " +
                                                 "from func_colaborador_get_funcoes_pilares_by_cpf(f_cpf_colaborador " +
                                                 "=> ?);");
            stmt.setLong(1, cpf);
            rSet = stmt.executeQuery();
            pilares = empresaDao.createPilares(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
        visao.setPilares(pilares);
        return visao;
    }
}