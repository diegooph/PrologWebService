package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.colaborador.model.*;
import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.empresa.EmpresaDao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.DadosIntervaloChangedListener;
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

/**
 * Classe ColaboradorDaoImpl, responsavel pela execução da lógica e comunicação com a interface de dados
 */
public class ColaboradorDaoImpl extends DatabaseConnection implements ColaboradorDao {

    @Override
    public void insert(Colaborador colaborador, DadosIntervaloChangedListener listener) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO COLABORADOR "
                    + "(CPF, MATRICULA_AMBEV, MATRICULA_TRANS, DATA_NASCIMENTO, "
                    + "DATA_ADMISSAO, DATA_DEMISSAO, STATUS_ATIVO, NOME, "
                    + "COD_SETOR, COD_FUNCAO, COD_UNIDADE, COD_PERMISSAO, COD_EMPRESA, COD_EQUIPE, PIS, COD_UNIDADE_CADASTRO) VALUES "
                    + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            stmt.setLong(1, colaborador.getCpf());
            if (colaborador.getMatriculaAmbev() == null || colaborador.getMatriculaAmbev().equals(0)) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, colaborador.getMatriculaAmbev());
            }
            if (colaborador.getMatriculaTrans() == null || colaborador.getMatriculaTrans().equals(0)) {
                stmt.setNull(3, Types.INTEGER);
            } else {
                stmt.setInt(3, colaborador.getMatriculaTrans());
            }
            stmt.setDate(4, DateUtils.toSqlDate(colaborador.getDataNascimento()));
            stmt.setDate(5, DateUtils.toSqlDate(colaborador.getDataAdmissao()));
            stmt.setNull(6, Types.DATE);
            stmt.setBoolean(7, colaborador.isAtivo());
            stmt.setString(8, colaborador.getNome());
            stmt.setLong(9, colaborador.getSetor().getCodigo());
            stmt.setLong(10, colaborador.getFuncao().getCodigo());
            stmt.setLong(11, colaborador.getCodUnidade());
            stmt.setLong(12, colaborador.getCodPermissao());
            stmt.setLong(13, colaborador.getCodEmpresa());
            stmt.setLong(14, colaborador.getEquipe().getCodigo());
            stmt.setString(15, colaborador.getPis());
            stmt.setLong(16, colaborador.getCodUnidade());
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o colaborador");
            }

            // Avisamos o listener que um colaborador foi inserido.
            listener.onColaboradorInserido(conn, Injection.provideEmpresaDao(), colaborador);

            // Tudo certo, commita.
            conn.commit();
        } catch (Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    @Override
    public void update(Long cpfAntigo, Colaborador colaborador, DadosIntervaloChangedListener listener) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("UPDATE COLABORADOR SET "
                    + "CPF = ?, MATRICULA_AMBEV = ?, MATRICULA_TRANS = ?, "
                    + "DATA_NASCIMENTO = ?, DATA_ADMISSAO = ?, "
                    + "STATUS_ATIVO = ?, NOME = ?, COD_SETOR = ?, "
                    + "COD_FUNCAO = ?, COD_UNIDADE = ?, COD_PERMISSAO = ?, "
                    + "COD_EMPRESA = ?, COD_EQUIPE = ?, PIS = ? "
                    + "WHERE CPF = ?;");
            stmt.setLong(1, colaborador.getCpf());
            if (colaborador.getMatriculaAmbev() == null || colaborador.getMatriculaAmbev().equals(0)) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, colaborador.getMatriculaAmbev());
            }
            if (colaborador.getMatriculaTrans() == null || colaborador.getMatriculaTrans().equals(0)) {
                stmt.setNull(3, Types.INTEGER);
            } else {
                stmt.setInt(3, colaborador.getMatriculaTrans());
            }
            stmt.setDate(4, DateUtils.toSqlDate(colaborador.getDataNascimento()));
            stmt.setDate(5, DateUtils.toSqlDate(colaborador.getDataAdmissao()));
            stmt.setBoolean(6, colaborador.isAtivo());
            stmt.setString(7, colaborador.getNome());
            stmt.setLong(8, colaborador.getSetor().getCodigo());
            stmt.setLong(9, colaborador.getFuncao().getCodigo());
            stmt.setLong(10, colaborador.getUnidade().getCodigo());
            stmt.setLong(11, colaborador.getCodPermissao());
            stmt.setLong(12, colaborador.getEmpresa().getCodigo());
            stmt.setLong(13, colaborador.getEquipe().getCodigo());
            stmt.setString(14, colaborador.getPis());
            stmt.setLong(15, cpfAntigo);
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao atualizar o colaborador com CPF: " + cpfAntigo);
            }

            // Avisa o listener que atualizamos um colaborador.
            listener.onColaboradorAtualizado(conn, Injection.provideEmpresaDao(), this, colaborador, cpfAntigo);

            // Tudo certo, commita.
            conn.commit();
        } catch (Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    @Override
    public void updateStatus(Long cpf, Colaborador colaborador) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE COLABORADOR SET STATUS_ATIVO = ? WHERE CPF = ?;");
            stmt.setBoolean(1, colaborador.isAtivo());
            stmt.setLong(2, cpf);

            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao atualizar o status do colaborador com CPF: " + cpf);
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    @Override
    public void delete(Long cpf, DadosIntervaloChangedListener listener) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("UPDATE COLABORADOR SET "
                    + "STATUS_ATIVO = FALSE, data_demissao = ? "
                    + "WHERE CPF = ?;");
            stmt.setObject(1, LocalDate.now(Clock.systemUTC()));
            stmt.setLong(2, cpf);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inativar colaborador com CPF: " + cpf);
            }

            // Já inativamos o colaborador, repassamos o evento ao listener.
            listener.onColaboradorInativado(conn, this, cpf);

            // Se deu tudo certo, commita.
            conn.commit();
        } catch (Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            closeConnection(conn, stmt, null);
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
                    + "C.COD_FUNCAO, F.NOME AS NOME_FUNCAO, C.COD_PERMISSAO AS PERMISSAO "
                    + "FROM COLABORADOR C JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO "
                    + " JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE "
                    + " JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE "
                    + " JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA"
                    + " JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL "
                    + " JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE "
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
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    @NotNull
    @Override
    public Colaborador getByToken(@NotNull String token) throws SQLException {
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
                    + "C.COD_FUNCAO, F.NOME AS NOME_FUNCAO, C.COD_PERMISSAO AS PERMISSAO "
                    + "FROM COLABORADOR C JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO "
                    + " JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE "
                    + " JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE "
                    + " JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA "
                    + " JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL "
                    + " JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE "
                    + " JOIN TOKEN_AUTENTICACAO TA ON TA.TOKEN = ? AND TA.CPF_COLABORADOR = C.CPF "
                    + "WHERE C.STATUS_ATIVO = TRUE");
            stmt.setString(1, token);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Colaborador c = createColaborador(rSet);
                c.setVisao(getVisaoByCpf(c.getCpf()));
                return c;
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
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
                    "  C.COD_PERMISSAO AS PERMISSAO " +
                    "FROM COLABORADOR C " +
                    "  JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO " +
                    "  JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE " +
                    "  JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE " +
                    "  JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA " +
                    "  JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL " +
                    "  JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE " +
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
            closeConnection(conn, stmt, rSet);
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
            closeConnection(conn, stmt, rSet);
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
                    "WHERE C.CPF = ?\n" +
                    "ORDER BY PP.pilar, FP.funcao");
            stmt.setLong(1, cpf);
            rSet = stmt.executeQuery();
            pilares = empresaDao.createPilares(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
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
        return c;
    }
}