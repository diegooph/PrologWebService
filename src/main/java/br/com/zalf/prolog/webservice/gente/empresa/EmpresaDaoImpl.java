package br.com.zalf.prolog.webservice.gente.empresa;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.gente.colaborador.model.*;
import br.com.zalf.prolog.webservice.gente.controlejornada.DadosIntervaloChangedListener;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import br.com.zalf.prolog.webservice.permissao.Visao;
import br.com.zalf.prolog.webservice.permissao.pilares.FuncaoProLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilar;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.core.NoContentException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpresaDaoImpl extends DatabaseConnection implements EmpresaDao {

    private static final String BUSCA_EMPRESA_REGIONAL_UNIDADE_EQUIPE_BY_CPF = ""
            + "select emp.codigo as cod_empresa, emp.nome nome_empresa, "
            + "reg.codigo as cod_regional, reg.regiao nome_regional, "
            + "u.codigo as cod_unidade, u.nome as nome_unidade, "
            + "e.codigo as cod_equipe, e.nome as nome_equipe "
            + "from colaborador c join unidade u on u.codigo = c.cod_unidade "
            + "join empresa emp on emp.codigo = u.cod_empresa "
            + "join regional reg on reg.codigo = u.cod_regional "
            + "join equipe e on e.codigo = c.cod_equipe where c.cpf = ?";
    private static final String BUSCA_UNIDADE_BY_REGIONAL = " SELECT DISTINCT U.CODIGO, U.NOME "
            + " FROM UNIDADE U JOIN REGIONAL REG ON REG.CODIGO = U.COD_REGIONAL "
            + " JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO"
            + " WHERE REG.CODIGO = ? AND E.CODIGO = ? ORDER BY 2 ";
    private static final String BUSCA_EQUIPE_BY_UNIDADE = "SELECT DISTINCT E.NOME, E.CODIGO "
            + "FROM EQUIPE E JOIN UNIDADE U ON U.CODIGO = E.COD_UNIDADE "
            + "WHERE U.CODIGO = ?"
            + "ORDER BY 1";
    private static final String BUSCA_EMPRESA_REGIONAL_UNIDADE_BY_CPF = "select emp.codigo as cod_empresa, emp.nome as nome_empresa,"
            + " reg.codigo as cod_regional, reg.regiao nome_regional,"
            + " u.codigo as cod_unidade, u.nome as nome_unidade "
            + "from colaborador c join unidade u on u.codigo = c.cod_unidade "
            + "join empresa emp on emp.codigo = u.cod_empresa "
            + "join regional reg on reg.codigo = u.cod_regional "
            + "where c.cpf = ?";
    private static final String BUSCA_CODIGO_PERMISSAO_BY_CPF = "select c.cod_permissao "
            + "from colaborador c "
            + "where c.cpf = ?";
    private static final String BUSCA_REGIONAL = "select distinct reg.codigo, reg.regiao, e.codigo as codigo_empresa, e.nome as nome_empresa "
            + "from unidade u join empresa e on e.codigo = u.cod_empresa "
            + "join regional reg on reg.codigo = u.cod_regional	"
            + "where e.codigo in (select c.cod_empresa	"
            + "from colaborador c	where c.cpf = ?"
            + " ORDER BY 1)";
    private static final String BUSCA_REGIONAL_BY_CPF = "select distinct reg.codigo, reg.regiao, e.codigo as cod_empresa, e.nome as nome_empresa "
            + "from regional reg "
            + "left join unidade u on u.cod_regional = reg.codigo "
            + "join empresa e on e.codigo = u.cod_empresa join colaborador c on c.cod_unidade = u.codigo and c.cpf=? "
            + "where reg.codigo in ( "
            + "select r.codigo "
            + "from colaborador c join unidade u on u.codigo = c.cod_unidade "
            + "join regional r on r.codigo = u.cod_regional	"
            + "where c.cpf=?)";
    private final String TAG = EmpresaDaoImpl.class.getSimpleName();
    private final String BUSCA_EQUIPES_BY_COD_UNIDADE = "SELECT E.CODIGO, E.NOME "
            + "FROM EQUIPE E JOIN UNIDADE U ON U.CODIGO = E.COD_UNIDADE "
            + "WHERE U.CODIGO = ?";
    private final String UPDATE_EQUIPE = "UPDATE EQUIPE SET NOME = (?) "
            + "FROM TOKEN_AUTENTICACAO TA WHERE CODIGO = ?	"
            + "AND TA.CPF_COLABORADOR=? "
            + "AND TA.TOKEN=?";

    //TODO: Verificar a viabilidade de implementar um método para exclusão de uma equipe,
    //a equipe está ligada como fk de colaborador e fk de calendário

    public EmpresaDaoImpl() {

    }

    @Override
    public AbstractResponse insertEquipe(@NotNull final Long codUnidade,
                                         @NotNull final Equipe equipe) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO EQUIPE "
                    + "(NOME, COD_UNIDADE) VALUES "
                    + "(?,?) RETURNING CODIGO;");
            stmt.setString(1, equipe.getNome());
            stmt.setLong(2, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return ResponseWithCod.ok("Equipe inserida com sucesso", rSet.getLong("CODIGO"));
            } else {
                return Response.error("Erro ao inserir a equipe");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public boolean updateEquipe(@NotNull final Long codUnidade,
                                @NotNull final Long codEquipe,
                                @NotNull final Equipe equipe) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE EQUIPE SET NOME = ? WHERE CODIGO = ? AND COD_UNIDADE = ?");
            stmt.setString(1, equipe.getNome());
            stmt.setLong(2, codEquipe);
            stmt.setLong(3, codUnidade);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao atualizar a equipe");
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    @Override
    public Equipe getEquipe(final Long codUnidade, final Long codEquipe) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM EQUIPE WHERE COD_UNIDADE = ?  AND CODIGO = ?");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codEquipe);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Equipe equipe = new Equipe();
                equipe.setCodigo(rSet.getLong("CODIGO"));
                equipe.setNome(rSet.getString("NOME"));
                return equipe;
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    @Override
    public AbstractResponse insertSetor(@NotNull final Long codUnidade,
                                        @NotNull final Setor setor) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO SETOR(cod_unidade, nome) VALUES (?,?) RETURNING CODIGO;");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, setor.getNome());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return ResponseWithCod.ok("Setor inserido com sucesso", rSet.getLong("codigo"));
            } else {
                return Response.error("Erro ao inserir o setor");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Setor getSetor(final Long codUnidade, final Long codSetor) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM SETOR WHERE COD_UNIDADE = ?  AND CODIGO = ?");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codSetor);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Setor setor = new Setor();
                setor.setCodigo(rSet.getLong("CODIGO"));
                setor.setNome(rSet.getString("NOME"));
                return setor;
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    @Override
    public boolean updateSetor(@NotNull final Long codUnidade,
                               @NotNull final Long codSetor,
                               @NotNull final Setor setor) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE SETOR SET NOME = ? WHERE CODIGO = ? AND COD_UNIDADE = ?");
            stmt.setString(1, setor.getNome());
            stmt.setLong(2, codSetor);
            stmt.setLong(3, codUnidade);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao atualizar o setor");
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    @Override
    public List<Equipe> getEquipesByCodUnidade(final Long codUnidade) throws SQLException {
        final List<Equipe> listEquipe = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(BUSCA_EQUIPES_BY_COD_UNIDADE);
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                listEquipe.add(createEquipe(rSet));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return listEquipe;
    }

    @Override
    public Cargo getCargo(final Long codEmpresa, final Long codCargo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT f.codigo, f.nome " +
                    "FROM funcao f " +
                    "WHERE f.cod_empresa = ? and f.codigo = ?;");
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codCargo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createCargo(rSet);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    @Override
    public Visao getVisaoCargo(final Long codUnidade, final Long codCargo) throws SQLException {
        final Visao visao = new Visao();
        visao.setPilares(getPilaresCargo(codUnidade, codCargo));
        return visao;
    }

    @Override
    public Visao getVisaoUnidade(final Long codUnidade) throws SQLException {
        final List<Pilar> pilares;
        ResultSet rSet = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT DISTINCT PP.codigo AS COD_PILAR, PP.pilar, FP.codigo AS COD_FUNCAO, FP.funcao\n" +
                    "FROM PILAR_PROLOG PP\n" +
                    "JOIN FUNCAO_PROLOG_v11 FP ON FP.cod_pilar = PP.codigo\n" +
                    "JOIN unidade_pilar_prolog upp on upp.cod_pilar = pp.codigo\n" +
                    "WHERE upp.cod_unidade = ?\n" +
                    "ORDER BY PP.pilar, FP.funcao");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            pilares = createPilares(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }

        final Visao visao = new Visao();
        visao.setPilares(pilares);
        return visao;
    }

    private List<Pilar> getPilaresCargo(final Long codUnidade, final Long codCargo) throws SQLException {
        final List<Pilar> pilares;
        ResultSet rSet = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT DISTINCT PP.codigo AS COD_PILAR, PP.pilar, FP.codigo AS COD_FUNCAO, FP.funcao FROM cargo_funcao_prolog_V11 CF\n" +
                    "JOIN PILAR_PROLOG PP ON PP.codigo = CF.cod_pilar_prolog\n" +
                    "JOIN FUNCAO_PROLOG_V11 FP ON FP.cod_pilar = PP.codigo AND FP.codigo = CF.cod_funcao_prolog\n" +
                    "WHERE CF.cod_unidade = ? AND cod_funcao_colaborador = ?\n" +
                    "ORDER BY PP.pilar, FP.funcao");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codCargo);
            rSet = stmt.executeQuery();
            pilares = createPilares(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }

        return pilares;
    }

    @Override
    public List<Pilar> createPilares(final ResultSet rSet) throws SQLException {
        final List<Pilar> pilares = new ArrayList<>();
        List<FuncaoProLog> funcoes = new ArrayList<>();
        Pilar pilar = null;
        while (rSet.next()) {
            if (pilar == null) {//primeira linha do rSet
                pilar = createPilar(rSet);
                funcoes.add(createFuncaoProLog(rSet));
            } else {
                if (rSet.getString("PILAR").equals(pilar.nome)) {
                    funcoes.add(createFuncaoProLog(rSet));
                } else {
                    pilar.funcoes = funcoes;
                    pilares.add(pilar);
                    pilar = createPilar(rSet);
                    funcoes = new ArrayList<>();
                    funcoes.add(createFuncaoProLog(rSet));
                }
            }
        }
        if (pilar != null) {
            pilar.funcoes = funcoes;
            pilares.add(pilar);
        }
        return pilares;
    }

    private FuncaoProLog createFuncaoProLog(final ResultSet rSet) throws SQLException {
        final FuncaoProLog funcao = new FuncaoProLog();
        funcao.setCodigo(rSet.getInt("COD_FUNCAO"));
        funcao.setDescricao(rSet.getString("FUNCAO"));
        return funcao;
    }

    private Pilar createPilar(final ResultSet rSet) throws SQLException {
        final Pilar pilar = new Pilar();
        pilar.codigo = rSet.getInt("COD_PILAR");
        pilar.nome = rSet.getString("PILAR");
        return pilar;
    }

    @Override
    public List<Setor> getSetorByCodUnidade(final Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Setor> setores = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM setor WHERE cod_unidade = ?\n" +
                    "ORDER BY nome");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final Setor setor = new Setor();
                setor.setCodigo(rSet.getLong("codigo"));
                setor.setNome(rSet.getString("nome"));
                setores.add(setor);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return setores;
    }

    @Override
    public List<HolderMapaTracking> getResumoAtualizacaoDados(final int ano,
                                                              final int mes,
                                                              final Long codUnidade) throws SQLException, NoContentException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<HolderMapaTracking> holders = null;
        HolderMapaTracking holder = null;
        List<MapaTracking> mapas = null;
        MapaTracking mapa;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT A.DATA AS DATA, M.MAPA, coalesce(M.placa, tracking.placa_tracking,M.placa) as placa, TRACKING.MAPA_TRACKING\n" +
                    "FROM MAPA M FULL OUTER JOIN\n" +
                    "(SELECT DISTINCT DATA AS DATA_TRACKING, MAPA AS MAPA_TRACKINg, código_transportadora as codigo, placa as placa_tracking FROM TRACKING) AS TRACKING ON MAPA_TRACKING = M.MAPA and m.cod_unidade = codigo\n" +
                    "JOIN aux_data A ON (A.data = M.data OR A.DATA = tracking.DATA_TRACKING)\n" +
                    "WHERE (tracking.codigo = ? or m.cod_unidade = ?) and extract(YEAR FROM a.data) = ? and extract(MONTH FROM a.data) = ?\n" +
                    "ORDER BY 1;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codUnidade);
            stmt.setInt(3, ano);
            stmt.setInt(4, mes);
            Log.d(TAG, stmt.toString());
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                Integer tempMapa = rSet.getInt("mapa");
                Integer tempTracking = rSet.getInt("mapa_tracking");
                if (tempMapa == 0) {
                    tempMapa = null;
                }
                if (tempTracking == 0) {
                    tempTracking = null;
                }

                if (holders == null) {// primeira iteração do rSet
                    holders = new ArrayList<>();
                    holder = new HolderMapaTracking();
                    holder.setData(rSet.getDate("DATA"));
                    mapas = new ArrayList<>();
                    mapa = new MapaTracking();
                    mapa.setMapa(tempMapa);
                    mapa.setPlaca(rSet.getString("placa"));
                    mapa.setTracking(tempTracking);
                    mapas.add(mapa);
                } else {// a partir da primeira linha do rset
                    if (rSet.getDate("data").equals(holder.getData())) {
                        mapa = new MapaTracking();
                        mapa.setMapa(tempMapa);
                        mapa.setTracking(tempTracking);
                        mapa.setPlaca(rSet.getString("placa"));
                        mapas.add(mapa);
                    } else {// mudou a data, fechar as listas e começar novamente
                        holder.setMapas(mapas);
                        holders.add(holder);
                        holder = new HolderMapaTracking();
                        holder.setData(rSet.getDate("data"));
                        mapas = new ArrayList<>();
                        mapa = new MapaTracking();
                        mapa.setMapa(tempMapa);
                        mapa.setTracking(tempTracking);
                        mapa.setPlaca(rSet.getString("placa"));
                        mapas.add(mapa);
                    }
                }
            }
            if (holder != null) {
                holder.setMapas(mapas);
                holders.add(holder);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        if (holder == null) {
            throw new NoContentException("Sem dados para retornar");
        }
        return holders;
    }

    /**
     * Busca dos filtros para os relatórios a partir da permissão cadastrada.
     */
    @Override
    public List<Empresa> getFiltros(final Long cpf) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;

        List<Empresa> listEmpresa = new ArrayList<>();
        int codPermissao = 0;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(BUSCA_CODIGO_PERMISSAO_BY_CPF);
            stmt.setLong(1, cpf);
            rSet = stmt.executeQuery();

            while (rSet.next()) { // rset com o código da permissão
                codPermissao = rSet.getInt("COD_PERMISSAO");
            }
            switch (codPermissao) {
                case 0:
                    listEmpresa = getPermissao0(cpf);
                    break;
                case 1:
                    listEmpresa = getPermissao1(cpf);
                    break;
                case 2:
                    listEmpresa = getPermissao2(cpf);
                    break;
                case 3:
                    listEmpresa = getPermissao3(cpf);
                    break;
                default:
                    break;
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return listEmpresa;
    }

    private Equipe createEquipe(final ResultSet rset) throws SQLException {
        final Equipe equipe = new Equipe();
        equipe.setCodigo(rset.getLong("CODIGO"));
        equipe.setNome(rset.getString("NOME"));
        return equipe;
    }

    private Cargo createCargo(final ResultSet rSet) throws SQLException {
        final Cargo cargo = new Cargo();
        cargo.setCodigo(rSet.getLong("CODIGO"));
        cargo.setNome(rSet.getString("NOME"));
        return cargo;
    }

    // buscar permisões para colaboradores com permissão = 3 = tudo
    private List<Empresa> getPermissao3(final Long cpf) throws SQLException {
        final List<Empresa> listEmpresa = new ArrayList<>();
        final Empresa empresa = new Empresa();
        final List<Regional> listRegional = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(BUSCA_REGIONAL);
            stmt.setLong(1, cpf);
            rSet = stmt.executeQuery();

            while (rSet.next()) { // rset com os codigos e nomes da regionais
                final Regional regional = new Regional();
                empresa.setNome(rSet.getString("NOME_EMPRESA"));
                empresa.setCodigo(rSet.getLong("CODIGO_EMPRESA"));
                regional.setCodigo(rSet.getLong("CODIGO"));
                regional.setNome(rSet.getString("REGIAO"));
                setUnidadesByRegional(regional, empresa.getCodigo());
                listRegional.add(regional);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        empresa.setListRegional(listRegional);
        listEmpresa.add(empresa);
        return listEmpresa;
    }

    // burcar permissoes para colaboradores com permissao = 2 = regional
    private List<Empresa> getPermissao2(final Long cpf) throws SQLException {
        final List<Empresa> listEmpresa = new ArrayList<>();
        final Empresa empresa = new Empresa();
        final List<Regional> listRegional = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(BUSCA_REGIONAL_BY_CPF);
            stmt.setLong(1, cpf);
            stmt.setLong(2, cpf);
            rSet = stmt.executeQuery();

            while (rSet.next()) { // rset com os codigos e nomes da regionais
                final Regional regional = new Regional();
                empresa.setCodigo(rSet.getLong("COD_EMPRESA"));
                empresa.setNome(rSet.getString("NOME_EMPRESA"));
                regional.setCodigo(rSet.getLong("CODIGO"));
                regional.setNome(rSet.getString("REGIAO"));
                setUnidadesByRegional(regional, empresa.getCodigo());
                listRegional.add(regional);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        empresa.setListRegional(listRegional);
        listEmpresa.add(empresa);
        return listEmpresa;
    }

    // buscar permissoes para colaboradores com permissao = 1 = local, gerente
    private List<Empresa> getPermissao1(final Long cpf) throws SQLException {
        final List<Empresa> listEmpresa = new ArrayList<>();
        final List<Regional> listRegional = new ArrayList<>();
        final List<Unidade> listUnidade = new ArrayList<>();
        final Empresa empresa = new Empresa();
        final Regional regional = new Regional();
        final Unidade unidade = new Unidade();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(BUSCA_EMPRESA_REGIONAL_UNIDADE_BY_CPF);
            stmt.setLong(1, cpf);
            rSet = stmt.executeQuery();

            while (rSet.next()) { // rset com os codigos e nomes da regionais

                empresa.setCodigo(rSet.getLong("COD_EMPRESA"));
                empresa.setNome(rSet.getString("NOME_EMPRESA"));
                regional.setCodigo(rSet.getLong("COD_REGIONAL"));
                regional.setNome(rSet.getString("NOME_REGIONAL"));
                unidade.setCodigo(rSet.getLong("COD_UNIDADE"));
                unidade.setNome(rSet.getString("NOME_UNIDADE"));
                setEquipesByUnidade(unidade);
                listEmpresa.add(empresa);
                listUnidade.add(unidade);
                listRegional.add(regional);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        empresa.setListRegional(listRegional);
        regional.setListUnidade(listUnidade);
        return listEmpresa;
    }

    // buscar permissoes para colaboradores com permissao = 0 = local, supervisor, busca apenas a sala que o cpf pertence
    private List<Empresa> getPermissao0(final Long cpf) throws SQLException {
        final List<Empresa> listEmpresa = new ArrayList<>();
        final List<Regional> listRegional = new ArrayList<>();
        final List<Unidade> listUnidade = new ArrayList<>();
        final List<Equipe> equipes = new ArrayList<>();
        final List<String> listEquipe = new ArrayList<>();
        final Empresa empresa = new Empresa();
        final Regional regional = new Regional();
        final Unidade unidade = new Unidade();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(BUSCA_EMPRESA_REGIONAL_UNIDADE_EQUIPE_BY_CPF);
            stmt.setLong(1, cpf);
            rSet = stmt.executeQuery();

            while (rSet.next()) { // rset com os codigos e nomes da regionais

                empresa.setCodigo(rSet.getLong("COD_EMPRESA"));
                regional.setCodigo(rSet.getLong("COD_REGIONAL"));
                regional.setNome(rSet.getString("NOME_REGIONAL"));
                unidade.setCodigo(rSet.getLong("COD_UNIDADE"));
                unidade.setNome(rSet.getString("NOME_UNIDADE"));
                final String nomeEquipe = rSet.getString("NOME_EQUIPE");
                final Long codEquipe = rSet.getLong("COD_EQUIPE");
                empresa.setNome(rSet.getString("NOME_EMPRESA"));
                listEmpresa.add(empresa);
                listUnidade.add(unidade);
                listRegional.add(regional);
                listEquipe.add(nomeEquipe);
                equipes.add(new Equipe(codEquipe, nomeEquipe));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        empresa.setListRegional(listRegional);
        regional.setListUnidade(listUnidade);
        unidade.setListNomesEquipes(listEquipe);
        unidade.setEquipes(equipes);
        return listEmpresa;
    }

    private void setUnidadesByRegional(final Regional regional, final Long codEmpresa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Unidade> listUnidades = new ArrayList<>();
        try {

            conn = getConnection();
            stmt = conn.prepareStatement(BUSCA_UNIDADE_BY_REGIONAL);
            stmt.setLong(1, regional.getCodigo());
            stmt.setLong(2, codEmpresa);
            rSet = stmt.executeQuery();

            while (rSet.next()) {
                final Unidade unidade = new Unidade();
                unidade.setCodigo(rSet.getLong("CODIGO"));
                unidade.setNome(rSet.getString("NOME"));
                setEquipesByUnidade(unidade);
                listUnidades.add(unidade);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        regional.setListUnidade(listUnidades);

    }

    private void setEquipesByUnidade(final Unidade unidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<String> listEquipes = new ArrayList<>();
        final List<Equipe> equipes = new ArrayList<>();
        try {

            conn = getConnection();
            stmt = conn.prepareStatement(BUSCA_EQUIPE_BY_UNIDADE);
            stmt.setLong(1, unidade.getCodigo());
            rSet = stmt.executeQuery();

            while (rSet.next()) {
                final Equipe equipe = createEquipe(rSet);
                listEquipes.add(equipe.getNome());
                equipes.add(equipe);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        unidade.setListNomesEquipes(listEquipes);
        unidade.setEquipes(equipes);
    }

    @Override
    public Long getCodEquipeByCodUnidadeByNome(final Long codUnidade, final String nomeEquipe) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT DISTINCT E.NOME, E.CODIGO "
                    + "FROM EQUIPE E JOIN UNIDADE U ON U.CODIGO = E.COD_UNIDADE "
                    + "WHERE U.CODIGO = ? AND E.NOME LIKE ? "
                    + "ORDER BY 1");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, nomeEquipe);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("codigo");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    @Override
    public void alterarVisaoCargo(
            @NotNull final Long codUnidade,
            @NotNull final Long codCargo,
            @NotNull final Visao visao,
            @NotNull final DadosIntervaloChangedListener intervaloListener,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            final boolean tinhaPermissaoRealizarChecklist = temPermissaoFuncaoProLog(
                    conn,
                    codUnidade,
                    codCargo,
                    Pilares.Frota.Checklist.REALIZAR);
            // Primeiro deletamos qualquer função dos pilares ativos do ProLog cadastrada nesse cargo para essa unidade.
            deleteCargoFuncaoProlog(codCargo, codUnidade, conn);

            stmt = conn.prepareStatement("INSERT INTO CARGO_FUNCAO_PROLOG_V11(COD_UNIDADE, COD_FUNCAO_COLABORADOR, " +
                    "COD_FUNCAO_PROLOG, COD_PILAR_PROLOG) VALUES (?,?,?,?)");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codCargo);
            for (final Pilar pilar : visao.getPilares()) {
                for (final FuncaoProLog funcao : pilar.funcoes) {
                    stmt.setInt(3, funcao.getCodigo());
                    stmt.setInt(4, pilar.codigo);
                    final int count = stmt.executeUpdate();
                    if (count == 0) {
                        throw new SQLException("Erro ao vincular a permissão "
                                + funcao.getCodigo() + " do ProLog ao cargo "
                                + codCargo + " da unidade " + codUnidade);
                    }
                }
            }

            // Notificamos a alteração de um cargo para os Listeners.
            intervaloListener.onCargoAtualizado(conn, this, visao, codCargo, codUnidade);
            checklistOfflineListener.onCargoAtualizado(
                    conn,
                    codUnidade,
                    codCargo,
                    tinhaPermissaoRealizarChecklist,
                    visao.hasAccessToFunction(Pilares.Frota.Checklist.REALIZAR));

            // Tudo certo, commita.
            conn.commit();
        } catch (final Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn, stmt);
        }
    }

    private boolean temPermissaoFuncaoProLog(@NotNull final Connection conn,
                                             @NotNull final Long codUnidade,
                                             @NotNull final Long codCargo,
                                             final int codFuncaoProLog) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT EXISTS(SELECT * FROM CARGO_FUNCAO_PROLOG_V11 CARGO " +
                    "WHERE CARGO.COD_UNIDADE = ? " +
                    "      AND CARGO.COD_FUNCAO_COLABORADOR = ? " +
                    "      AND CARGO.COD_FUNCAO_PROLOG = ?) AS TEM_PERMISSAO;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codCargo);
            stmt.setInt(3, codFuncaoProLog);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("TEM_PERMISSAO");
            } else {
                throw new SQLException("Erro ao verificar se o cargo possuia permissão de realizar checklist");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    private void deleteCargoFuncaoProlog(final Long codCargo,
                                         final Long codUnidade,
                                         final Connection conn) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("DELETE FROM CARGO_FUNCAO_PROLOG_V11 WHERE " +
                "COD_UNIDADE = ? AND COD_FUNCAO_COLABORADOR = ? " +
                "AND COD_PILAR_PROLOG IN (SELECT COD_PILAR FROM UNIDADE_PILAR_PROLOG WHERE COD_UNIDADE = ?);");
        stmt.setLong(1, codUnidade);
        stmt.setLong(2, codCargo);
        stmt.setLong(3, codUnidade);
        stmt.executeUpdate();
        // Não precisamos verificar se o delete afetou alguma linha pois o cargo pode não ter nenhuma permissão vinculada.
    }

    @Override
    public Long insertFuncao(final Cargo cargo, final Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO funcao(nome, cod_empresa) VALUES " +
                    "(?,(SELECT cod_empresa FROM unidade WHERE codigo = ?)) RETURNING CODIGO");
            stmt.setString(1, cargo.getNome());
            stmt.setLong(2, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            }
        } catch (final SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    @NotNull
    @Override
    public Long getCodEmpresaByCodUnidade(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT U.COD_EMPRESA FROM UNIDADE U WHERE U.CODIGO = ?;");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("COD_EMPRESA");
            } else {
                throw new IllegalStateException("Empresa não encontrada para o código de unidade: " + codUnidade);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}