package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.colaborador.model.*;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.empresa.EmpresaDaoImpl;
import br.com.zalf.prolog.webservice.gente.controleintervalo.DadosIntervaloChangedListener;
import br.com.zalf.prolog.webservice.permissao.Visao;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilar;
import com.google.common.base.Preconditions;
import com.sun.istack.internal.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Classe ColaboradorDaoImpl, responsavel pela execução da lógica e comunicação com a interface de dados
 */
public class ColaboradorDaoImpl extends DatabaseConnection implements ColaboradorDao {
    private static final String TAG = ColaboradorDaoImpl.class.getSimpleName();

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
                    + "COD_SETOR, COD_FUNCAO, COD_UNIDADE, COD_PERMISSAO, COD_EMPRESA, COD_EQUIPE) VALUES "
                    + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            stmt.setLong(1, colaborador.getCpf());
            if (colaborador.getMatriculaAmbev() == 0) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, colaborador.getMatriculaAmbev());
            }
            stmt.setInt(3, colaborador.getMatriculaTrans());
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
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o colaborador");
            }

            // Avisamos o listener que um colaborador foi inserido.
            listener.onColaboradorInserido(conn, new EmpresaDaoImpl(), colaborador);

            // Tudo certo, commita.
            conn.commit();
        } catch (Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw  e;
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
                    + "DATA_NASCIMENTO = ?, DATA_ADMISSAO = ?, DATA_DEMISSAO = ?, "
                    + "STATUS_ATIVO = ?, NOME = ?, COD_SETOR = ?, "
                    + "COD_FUNCAO = ?, COD_UNIDADE = ?, COD_PERMISSAO = ?, "
                    + "COD_EMPRESA = ?, COD_EQUIPE = ? "
                    + "WHERE CPF = ?;");
            stmt.setLong(1, colaborador.getCpf());
            stmt.setInt(2, colaborador.getMatriculaAmbev());
            stmt.setInt(3, colaborador.getMatriculaTrans());
            stmt.setDate(4, DateUtils.toSqlDate(colaborador.getDataNascimento()));
            stmt.setDate(5, DateUtils.toSqlDate(colaborador.getDataAdmissao()));

            // Só vai ter data de demissão quando estiver fazendo um update
            // em um colaborador que já está deletado (inativo).
            if (colaborador.getDataDemissao() != null)
                stmt.setDate(6, DateUtils.toSqlDate(new Date(System.currentTimeMillis())));
            else
                stmt.setDate(6, null);
            stmt.setBoolean(7, colaborador.isAtivo());
            stmt.setString(8, colaborador.getNome());
            stmt.setLong(9, colaborador.getSetor().getCodigo());
            stmt.setLong(10, colaborador.getFuncao().getCodigo());
            stmt.setLong(11, colaborador.getCodUnidade());
            stmt.setLong(12, colaborador.getCodPermissao());
            stmt.setLong(13, colaborador.getCodEmpresa());
            stmt.setLong(14, colaborador.getEquipe().getCodigo());
            stmt.setLong(15, cpfAntigo);
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao atualizar o colaborador com CPF: " + cpfAntigo);
            }

            // Avisa o listener que atualizamos um colaborador.
            listener.onColaboradorAtualizado(conn, new EmpresaDaoImpl(), this, colaborador, cpfAntigo);

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
    public void delete(Long cpf, DadosIntervaloChangedListener listener) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("UPDATE COLABORADOR SET "
                    + "STATUS_ATIVO = FALSE "
                    + "WHERE CPF = ?;");
            stmt.setLong(1, cpf);
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

    /**
     * Busca um colaborador por código
     *
     * @param cpf chave a ser buscada no banco de dados
     * @return um colaborador
     * @throws SQLException
     */
    @Override
    public Colaborador getByCpf(Long cpf) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT C.CPF, C.MATRICULA_AMBEV, C.MATRICULA_TRANS, "
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
                    + "WHERE CPF = ? AND C.STATUS_ATIVO = TRUE");
            stmt.setLong(1, cpf);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                Colaborador c = createColaborador(rSet);
                c.setVisao(getVisaoByCpf(c.getCpf()));
                return c;
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    @Override
    public Colaborador getByToken(@NotNull String token) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT C.CPF, C.MATRICULA_AMBEV, C.MATRICULA_TRANS, "
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
                Colaborador c = createColaborador(rSet);
                c.setVisao(getVisaoByCpf(c.getCpf()));
                return c;
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    /**
     * Busca todos os colaboradores de uma unidade
     */
    @Override
    public List<Colaborador> getAll(Long codUnidade) throws SQLException {
        List<Colaborador> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT\n" +
                    "  C.CPF,\n" +
                    "  C.MATRICULA_AMBEV,\n" +
                    "  C.MATRICULA_TRANS,\n" +
                    "  C.DATA_NASCIMENTO,\n" +
                    "  C.DATA_ADMISSAO,\n" +
                    "  C.DATA_DEMISSAO,\n" +
                    "  C.STATUS_ATIVO,\n" +
                    "  initcap(C.NOME)          AS NOME_COLABORADOR,\n" +
                    "  EM.NOME         AS NOME_EMPRESA,\n" +
                    "  EM.CODIGO       AS COD_EMPRESA,\n" +
                    "  EM.LOGO_THUMBNAIL_URL,\n" +
                    "  R.REGIAO        AS NOME_REGIONAL,\n" +
                    "  R.CODIGO        AS COD_REGIONAL,\n" +
                    "  U.NOME          AS NOME_UNIDADE,\n" +
                    "  U.CODIGO        AS COD_UNIDADE,\n" +
                    "  EQ.NOME         AS NOME_EQUIPE,\n" +
                    "  EQ.CODIGO       AS COD_EQUIPE,\n" +
                    "  S.NOME          AS NOME_SETOR,\n" +
                    "  S.CODIGO        AS COD_SETOR,\n" +
                    "  C.COD_FUNCAO,\n" +
                    "  F.NOME          AS NOME_FUNCAO,\n" +
                    "  C.COD_PERMISSAO AS PERMISSAO\n" +
                    "FROM COLABORADOR C\n" +
                    "  JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO\n" +
                    "  JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE\n" +
                    "  JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE\n" +
                    "  JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA\n" +
                    "  JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL\n" +
                    "  JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE\n" +
                    "WHERE C.COD_UNIDADE = ?\n" +
                    "ORDER BY 8");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                Colaborador c = createColaborador(rSet);
                list.add(c);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return list;
    }

    @Override
    public List<Colaborador> getMotoristasAndAjudantes(Long codUnidade) throws SQLException {
        List<Colaborador> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT\n" +
                    "  C.CPF,\n" +
                    "  C.MATRICULA_AMBEV,\n" +
                    "  C.MATRICULA_TRANS,\n" +
                    "  C.DATA_NASCIMENTO,\n" +
                    "  C.DATA_ADMISSAO,\n" +
                    "  C.DATA_DEMISSAO,\n" +
                    "  C.STATUS_ATIVO,\n" +
                    "  initcap(C.NOME) AS NOME_COLABORADOR,\n" +
                    "  EM.NOME         AS NOME_EMPRESA,\n" +
                    "  EM.CODIGO       AS COD_EMPRESA,\n" +
                    "  EM.LOGO_THUMBNAIL_URL,\n" +
                    "  R.REGIAO        AS NOME_REGIONAL,\n" +
                    "  R.CODIGO        AS COD_REGIONAL,\n" +
                    "  U.NOME          AS NOME_UNIDADE,\n" +
                    "  U.CODIGO        AS COD_UNIDADE,\n" +
                    "  EQ.NOME         AS NOME_EQUIPE,\n" +
                    "  EQ.CODIGO       AS COD_EQUIPE,\n" +
                    "  S.NOME          AS NOME_SETOR,\n" +
                    "  S.CODIGO        AS COD_SETOR,\n" +
                    "  C.COD_FUNCAO,\n" +
                    "  F.NOME          AS NOME_FUNCAO,\n" +
                    "  C.COD_PERMISSAO AS PERMISSAO\n" +
                    "FROM COLABORADOR C\n" +
                    "  JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO\n" +
                    "  JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE\n" +
                    "  JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE\n" +
                    "  JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA\n" +
                    "  JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL\n" +
                    "  JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE\n" +
                    "  JOIN unidade_funcao_produtividade UFP ON UFP.cod_unidade = C.cod_unidade AND\n" +
                    "                                           (C.cod_funcao = UFP.cod_funcao_ajudante OR\n" +
                    "                                            C.COD_FUNCAO = UFP.cod_funcao_motorista)\n" +
                    "WHERE C.COD_UNIDADE = ?\n" +
                    "ORDER BY 8");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                Colaborador c = createColaborador(rSet);
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
    public List<Colaborador> getColaboradoresComAcessoFuncaoByUnidade(final int codFuncaoProLog,
                                                                       @NotNull final Long codUnidade)
            throws SQLException {

        Preconditions.checkNotNull(codUnidade, "codUnidade não pode ser null!");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT CPF, NOME, DATA_NASCIMENTO FROM COLABORADOR C JOIN " +
                    "CARGO_FUNCAO_PROLOG_V11 CFP ON C.COD_UNIDADE = CFP.COD_UNIDADE " +
                    "AND C.COD_FUNCAO = CFP.COD_FUNCAO_COLABORADOR " +
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
                    colaborador.setNome(rSet.getString("NOME"));
                    colaborador.setDataNascimento(rSet.getDate("DATA_NASCIMENTO"));
                    colaboradores.add(colaborador);
                } while (rSet.next());

                return colaboradores;
            }
        } finally {
            closeConnection(conn, stmt, rSet);
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

    private Visao getVisaoByCpf(Long cpf) throws SQLException {
        Visao visao = new Visao();
        List<Pilar> pilares;
        ResultSet rSet = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        EmpresaDaoImpl empresaDao = new EmpresaDaoImpl();

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
        Cargo f = new Cargo();
        f.setCodigo(rSet.getLong("CODIGO"));
        f.setNome(rSet.getString("NOME"));
        return f;
    }

    private Colaborador createColaborador(ResultSet rSet) throws SQLException {
        Colaborador c = new Colaborador();
        c.setAtivo(rSet.getBoolean("STATUS_ATIVO"));

        Cargo cargo = new Cargo();
        cargo.setCodigo(rSet.getLong("COD_FUNCAO"));
        cargo.setNome(rSet.getString("NOME_FUNCAO"));
        c.setFuncao(cargo);

        Empresa empresa = new Empresa();
        empresa.setCodigo(rSet.getInt("COD_EMPRESA"));
        empresa.setNome(rSet.getString("NOME_EMPRESA"));
        empresa.setLogoThumbnailUrl(rSet.getString("LOGO_THUMBNAIL_URL"));
        c.setEmpresa(empresa);

        Regional regional = new Regional();
        regional.setCodigo(rSet.getLong("COD_REGIONAL"));
        regional.setNome(rSet.getString("NOME_REGIONAL"));
        c.setRegional(regional);

        Unidade unidade = new Unidade();
        unidade.setCodigo(rSet.getLong("COD_UNIDADE"));
        unidade.setNome(rSet.getString("NOME_UNIDADE"));
        c.setUnidade(unidade);

        Equipe equipe = new Equipe();
        equipe.setCodigo(rSet.getLong("COD_EQUIPE"));
        equipe.setNome(rSet.getString("NOME_EQUIPE"));
        c.setEquipe(equipe);

        Setor setor = new Setor();
        setor.setCodigo(rSet.getLong("COD_SETOR"));
        setor.setNome(rSet.getString("NOME_SETOR"));
        c.setSetor(setor);

        c.setCpf(rSet.getLong("CPF"));
        c.setDataNascimento(rSet.getDate("DATA_NASCIMENTO"));
        c.setCodUnidade(rSet.getLong("COD_UNIDADE"));
        c.setNome(rSet.getString("NOME_COLABORADOR"));
        c.setMatriculaAmbev(rSet.getInt("MATRICULA_AMBEV"));
        c.setMatriculaTrans(rSet.getInt("MATRICULA_TRANS"));
        c.setDataAdmissao(rSet.getDate("DATA_ADMISSAO"));
        c.setDataDemissao(rSet.getDate("DATA_DEMISSAO"));
        c.setCodPermissao(rSet.getLong("PERMISSAO"));
        c.setCodEmpresa(rSet.getLong("COD_EMPRESA"));
        return c;
    }
}