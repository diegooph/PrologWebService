package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.colaborador.model.*;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.empresa.EmpresaDao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.gente.controlejornada.DadosIntervaloChangedListener;
import br.com.zalf.prolog.webservice.permissao.Visao;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilar;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static br.com.zalf.prolog.webservice.commons.util.StatementUtils.bindValueOrNull;

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
            bindValueOrNull(stmt, 14,
                    colaborador.getTelefone() != null ? colaborador.getTelefone().getPrefixoPais() : null, SqlType.INTEGER);
            bindValueOrNull(stmt, 15,
                    colaborador.getTelefone() != null ? colaborador.getTelefone().getNumero() : null, SqlType.TEXT);
            bindValueOrNull(stmt, 16, colaborador.getEmail(), SqlType.TEXT);
            stmt.setLong(17, colaborador.getCodUnidade());
            stmt.setString(18, userToken);

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
        } catch (Throwable e) {
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
            bindValueOrNull(stmt, 15,
                    colaborador.getTelefone() != null ? colaborador.getTelefone().getPrefixoPais() : null, SqlType.INTEGER);
            bindValueOrNull(stmt, 16,
                    colaborador.getTelefone() != null ? colaborador.getTelefone().getNumero() : null, SqlType.TEXT);
            bindValueOrNull(stmt, 17, colaborador.getEmail(), SqlType.TEXT);
            stmt.setString(18, userToken);

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
        } catch (Throwable e) {
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
        } catch (Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public Colaborador getByCpf(Long cpf, boolean apenasAtivos) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT C.CODIGO, C.CPF, C.PIS, C.MATRICULA_AMBEV, C.MATRICULA_TRANS, "
                    + "C.DATA_NASCIMENTO, C.DATA_ADMISSAO, C.DATA_DEMISSAO, C.STATUS_ATIVO, "
                    + "C.NOME AS NOME_COLABORADOR, EM.NOME AS NOME_EMPRESA, EM.CODIGO AS COD_EMPRESA, EM" +
                    ".LOGO_THUMBNAIL_URL, "
                    + "R.REGIAO AS NOME_REGIONAL, R.CODIGO AS COD_REGIONAL, U.NOME AS NOME_UNIDADE, U.CODIGO AS " +
                    "COD_UNIDADE, EQ.NOME AS NOME_EQUIPE, EQ.CODIGO AS COD_EQUIPE, "
                    + "S.NOME AS NOME_SETOR, S.CODIGO AS COD_SETOR, "
                    + "C.COD_FUNCAO, F.NOME AS NOME_FUNCAO, C.COD_PERMISSAO AS PERMISSAO, U.TIMEZONE AS TZ_UNIDADE, "
                    + "CT.PREFIXO_PAIS, CT.NUMERO_TELEFONE, CE.EMAIL "
                    + "FROM COLABORADOR C JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO "
                    + " JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE "
                    + " JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE "
                    + " JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA"
                    + " JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL "
                    + " JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE "
                    + " LEFT JOIN COLABORADOR_TELEFONE CT ON C.CODIGO = CT.COD_COLABORADOR "
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
                final Colaborador c = createColaborador(rSet);
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
    public Colaborador getByToken(@NotNull final String token) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT C.CODIGO, C.CPF, C.PIS, C.MATRICULA_AMBEV, C.MATRICULA_TRANS, "
                    + "C.DATA_NASCIMENTO, C.DATA_ADMISSAO, C.DATA_DEMISSAO, C.STATUS_ATIVO, "
                    + "C.NOME AS NOME_COLABORADOR, EM.NOME AS NOME_EMPRESA, EM.CODIGO AS COD_EMPRESA, EM" +
                    ".LOGO_THUMBNAIL_URL, "
                    + "R.REGIAO AS NOME_REGIONAL, R.CODIGO AS COD_REGIONAL, U.NOME AS NOME_UNIDADE, U.CODIGO AS " +
                    "COD_UNIDADE, EQ.NOME AS NOME_EQUIPE, EQ.CODIGO AS COD_EQUIPE, "
                    + "S.NOME AS NOME_SETOR, S.CODIGO AS COD_SETOR, "
                    + "C.COD_FUNCAO, F.NOME AS NOME_FUNCAO, C.COD_PERMISSAO AS PERMISSAO, U.TIMEZONE AS TZ_UNIDADE, "
                    + "CT.PREFIXO_PAIS, CT.NUMERO_TELEFONE, CE.EMAIL "
                    + "FROM COLABORADOR C JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO "
                    + " JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE "
                    + " JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE "
                    + " JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA "
                    + " JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL "
                    + " JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE "
                    + " LEFT JOIN COLABORADOR_TELEFONE CT ON C.CODIGO = CT.COD_COLABORADOR "
                    + " LEFT JOIN COLABORADOR_EMAIL CE ON C.CODIGO = CE.COD_COLABORADOR "
                    + " JOIN TOKEN_AUTENTICACAO TA ON TA.TOKEN = ? AND TA.CPF_COLABORADOR = C.CPF "
                    + "WHERE C.STATUS_ATIVO = TRUE");
            stmt.setString(1, token);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Colaborador c = createColaborador(rSet);
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
    public List<Colaborador> getAllByUnidade(@NotNull final Long codUnidade, final boolean apenasAtivos) throws Throwable {
        return internalGetAll(codUnidade, apenasAtivos, true);
    }

    @NotNull
    @Override
    public List<Colaborador> getAllByEmpresa(@NotNull final Long codEmpresa, final boolean apenasAtivos) throws Throwable {
        return internalGetAll(codEmpresa, apenasAtivos, false);
    }

    @Override
    public List<Colaborador> getMotoristasAndAjudantes(Long codUnidade) throws SQLException {
        List<Colaborador> list = new ArrayList<>();
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
                    "  CT.PREFIXO_PAIS, " +
                    "  CT.NUMERO_TELEFONE, " +
                    "  CE.EMAIL " +
                    "FROM COLABORADOR C " +
                    "  JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO " +
                    "  JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE " +
                    "  JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE " +
                    "  JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA " +
                    "  JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL " +
                    "  JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE " +
                    "  LEFT JOIN COLABORADOR_TELEFONE CT ON C.CODIGO = CT.COD_COLABORADOR " +
                    "  LEFT JOIN COLABORADOR_EMAIL CE ON C.CODIGO = CE.COD_COLABORADOR " +
                    "  JOIN unidade_funcao_produtividade UFP ON UFP.cod_unidade = C.cod_unidade AND " +
                    "                                           (C.cod_funcao = UFP.cod_funcao_ajudante OR " +
                    "                                            C.COD_FUNCAO = UFP.cod_funcao_motorista) " +
                    "WHERE C.COD_UNIDADE = ? " +
                    "ORDER BY 8");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final Colaborador c = createColaborador(rSet);
                list.add(c);
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return list;
    }

    @Override
    public boolean verifyIfCpfExists(Long cpf, Long codUnidade) throws SQLException {
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
    public List<Colaborador> getColaboradoresComAcessoFuncaoByUnidade(@NotNull final Long codUnidade,
                                                                      final int codFuncaoProLog) throws SQLException {
        Preconditions.checkNotNull(codUnidade, "codUnidade não pode ser null!");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT C.CPF, C.NOME AS NOME_COLABORADOR, C.DATA_NASCIMENTO, " +
                    "F.NOME AS NOME_CARGO, F.CODIGO AS CODIGO_CARGO " +
                    "FROM COLABORADOR C JOIN " +
                    "CARGO_FUNCAO_PROLOG_V11 CFP ON C.COD_UNIDADE = CFP.COD_UNIDADE " +
                    "AND C.COD_FUNCAO = CFP.COD_FUNCAO_COLABORADOR JOIN FUNCAO F ON F.CODIGO = C.COD_FUNCAO AND " +
                    "F.CODIGO = CFP.COD_FUNCAO_COLABORADOR AND C.COD_EMPRESA = F.COD_EMPRESA " +
                    "WHERE C.COD_UNIDADE = ? AND CFP.COD_FUNCAO_PROLOG = ? AND C.STATUS_ATIVO = TRUE;");
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
                    colaborador.setFuncao(createFuncao(rSet));
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
    public boolean colaboradorTemAcessoFuncao(@NotNull Long cpf, int codPilar, int codFuncaoProLog) throws SQLException {
        ResultSet rSet = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT EXISTS(SELECT C.CPF FROM COLABORADOR C " +
                    "JOIN CARGO_FUNCAO_PROLOG_V11 CFP ON C.COD_FUNCAO = CFP.COD_FUNCAO_COLABORADOR " +
                    "AND C.COD_UNIDADE = CFP.COD_UNIDADE WHERE C.CPF = ? AND CFP.COD_PILAR_PROLOG = ? " +
                    "AND CFP.COD_FUNCAO_PROLOG = ?);");
            stmt.setLong(1, cpf);
            stmt.setInt(2, codPilar);
            stmt.setInt(3, codFuncaoProLog);
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
    public Long getCodColaboradorByCpf(@NotNull final Connection conn,
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
                colaboradores.add(createColaborador(rSet));
            }
            return colaboradores;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private Visao getVisaoByCpf(Long cpf) throws SQLException {
        Visao visao = new Visao();
        List<Pilar> pilares;
        ResultSet rSet = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        final EmpresaDao empresaDao = Injection.provideEmpresaDao();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT DISTINCT PP.codigo AS COD_PILAR, PP.pilar, FP.codigo AS COD_FUNCAO, " +
                    "FP.funcao FROM cargo_funcao_prolog_v11 CF\n" +
                    "JOIN PILAR_PROLOG PP ON PP.codigo = CF.cod_pilar_prolog\n" +
                    "JOIN FUNCAO_PROLOG_v11 FP ON FP.cod_pilar = PP.codigo AND FP.codigo = CF.cod_funcao_prolog\n" +
                    "JOIN colaborador C ON C.cod_unidade = CF.cod_unidade AND CF.cod_funcao_colaborador = C" +
                    ".cod_funcao\n" +
                    "JOIN UNIDADE_PILAR_PROLOG UPP ON UPP.COD_UNIDADE = C.COD_UNIDADE AND UPP.cod_pilar = CF.cod_pilar_prolog\n" +
                    "WHERE C.CPF = ?\n" +
                    "ORDER BY PP.pilar, FP.funcao");
            stmt.setLong(1, cpf);
            rSet = stmt.executeQuery();
            pilares = empresaDao.createPilares(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
        visao.setPilares(pilares);
        return visao;
    }

    private Cargo createFuncao(ResultSet rSet) throws SQLException {
        final Cargo f = new Cargo();
        f.setCodigo(rSet.getLong("CODIGO_CARGO"));
        f.setNome(rSet.getString("NOME_CARGO"));
        return f;
    }

    private Colaborador createColaborador(ResultSet rSet) throws SQLException {
        final Colaborador c = new Colaborador();
        c.setCodigo(rSet.getLong("CODIGO"));
        c.setAtivo(rSet.getBoolean("STATUS_ATIVO"));

        final Cargo cargo = new Cargo();
        cargo.setCodigo(rSet.getLong("COD_FUNCAO"));
        cargo.setNome(rSet.getString("NOME_FUNCAO"));
        c.setFuncao(cargo);

        final Empresa empresa = new Empresa();
        empresa.setCodigo(rSet.getLong("COD_EMPRESA"));
        empresa.setNome(rSet.getString("NOME_EMPRESA"));
        empresa.setLogoThumbnailUrl(rSet.getString("LOGO_THUMBNAIL_URL"));
        c.setEmpresa(empresa);

        final Regional regional = new Regional();
        regional.setCodigo(rSet.getLong("COD_REGIONAL"));
        regional.setNome(rSet.getString("NOME_REGIONAL"));
        c.setRegional(regional);

        final Unidade unidade = new Unidade();
        unidade.setCodigo(rSet.getLong("COD_UNIDADE"));
        unidade.setNome(rSet.getString("NOME_UNIDADE"));
        c.setUnidade(unidade);

        final Equipe equipe = new Equipe();
        equipe.setCodigo(rSet.getLong("COD_EQUIPE"));
        equipe.setNome(rSet.getString("NOME_EQUIPE"));
        c.setEquipe(equipe);

        final Setor setor = new Setor();
        setor.setCodigo(rSet.getLong("COD_SETOR"));
        setor.setNome(rSet.getString("NOME_SETOR"));
        c.setSetor(setor);

        c.setCpf(rSet.getLong("CPF"));
        c.setPis(rSet.getString("PIS"));
        c.setDataNascimento(rSet.getDate("DATA_NASCIMENTO"));
        c.setNome(rSet.getString("NOME_COLABORADOR"));
        final int matriculaAmbev = rSet.getInt("MATRICULA_AMBEV");
        if (!rSet.wasNull()) {
            c.setMatriculaAmbev(matriculaAmbev);
        }
        final int matriculaTrans = rSet.getInt("MATRICULA_TRANS");
        if (!rSet.wasNull()) {
            c.setMatriculaTrans(matriculaTrans);
        }
        c.setDataAdmissao(rSet.getDate("DATA_ADMISSAO"));
        c.setDataDemissao(rSet.getDate("DATA_DEMISSAO"));
        c.setCodPermissao(rSet.getInt("PERMISSAO"));
        c.setTzUnidade(rSet.getString("TZ_UNIDADE"));

        if (rSet.getString("NUMERO_TELEFONE") != null) {
            c.setTelefone(new ColaboradorTelefone(
                    rSet.getInt("PREFIXO_PAIS"),
                    rSet.getString("NUMERO_TELEFONE")));
        }

        c.setEmail(rSet.getString("EMAIL"));

        return c;
    }
}