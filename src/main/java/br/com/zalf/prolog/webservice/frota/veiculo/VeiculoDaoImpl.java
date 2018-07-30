package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.EixoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.TipoEixoVeiculo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class VeiculoDaoImpl extends DatabaseConnection implements VeiculoDao {

    private static final String VEICULOS_BY_PLACA = "SELECT " +
            "V.*, " +
            "R.CODIGO AS COD_REGIONAL_ALOCADO, " +
            "MV.NOME AS MODELO, " +
            "EV.NOME AS EIXOS, " +
            "EV.DIANTEIRO, " +
            "EV.TRASEIRO, " +
            "EV.CODIGO AS COD_EIXOS, " +
            "tv.nome AS TIPO, " +
            "MAV.NOME AS MARCA, " +
            "MAV.CODIGO AS COD_MARCA  " +
            "FROM VEICULO V JOIN MODELO_VEICULO MV ON MV.CODIGO = V.COD_MODELO " +
            "JOIN EIXOS_VEICULO EV ON EV.CODIGO = V.COD_EIXOS " +
            "JOIN VEICULO_TIPO TV ON TV.CODIGO = V.COD_TIPO AND TV.COD_UNIDADE = V.COD_UNIDADE " +
            "JOIN MARCA_VEICULO MAV ON MAV.CODIGO = MV.COD_MARCA " +
            "JOIN UNIDADE U ON U.CODIGO = V.COD_UNIDADE " +
            "JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO " +
            "WHERE V.PLACA = ?";

    public VeiculoDaoImpl() {

    }

    @Override
    public boolean insert(Veiculo veiculo, Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO VEICULO (PLACA, COD_UNIDADE, KM, STATUS_ATIVO," +
                    " COD_TIPO, COD_MODELO, COD_EIXOS, COD_UNIDADE_CADASTRO)  VALUES (?,?,?,?,?,?,?,?)");
            stmt.setString(1, veiculo.getPlaca().toUpperCase());
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, veiculo.getKmAtual());
            stmt.setBoolean(4, true);
            stmt.setLong(5, veiculo.getTipo().getCodigo());
            stmt.setLong(6, veiculo.getModelo().getCodigo());
            stmt.setLong(7, veiculo.getEixos().codigo);
            stmt.setLong(8, codUnidade);
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o veículo");
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    @Override
    public boolean update(Veiculo veiculo, String placaOriginal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE VEICULO SET "
                    + "KM = ?, COD_MODELO = ?, COD_EIXOS = ? "
                    + "WHERE PLACA = ?");
            stmt.setLong(1, veiculo.getKmAtual());
            stmt.setLong(2, veiculo.getModelo().getCodigo());
            stmt.setLong(3, veiculo.getEixos().codigo);
            stmt.setString(4, placaOriginal);
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao atualizar o veículo");
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    @Override
    public void updateStatus(@NotNull Long codUnidade, @NotNull String placa, @NotNull Veiculo veiculo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE VEICULO SET STATUS_ATIVO = ? WHERE COD_UNIDADE = ? AND PLACA = ?;");
            stmt.setBoolean(1, veiculo.isAtivo());
            stmt.setLong(2, codUnidade);
            stmt.setString(3, placa);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao atualizar o status do veículo com placa: " + placa);
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    @Override
    public boolean delete(String placa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE VEICULO SET STATUS_ATIVO = ? "
                    + "WHERE PLACA = ?");
            stmt.setBoolean(1, false);
            stmt.setString(2, placa);
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao deletar o veículo");
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(Long codUnidade, @Nullable Boolean ativos)
            throws SQLException {
        final List<Veiculo> veiculos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "V.*, " +
                    "R.CODIGO AS COD_REGIONAL_ALOCADO, " +
                    "MV.NOME AS MODELO, " +
                    "EV.NOME AS EIXOS, " +
                    "EV.DIANTEIRO, " +
                    "EV.TRASEIRO,EV.CODIGO AS COD_EIXOS, " +
                    "TV.NOME AS TIPO, " +
                    "MAV.NOME AS MARCA, " +
                    "MAV.CODIGO AS COD_MARCA  " +
                    "FROM VEICULO V " +
                    "JOIN MODELO_VEICULO MV ON MV.CODIGO = V.COD_MODELO " +
                    "JOIN EIXOS_VEICULO EV ON EV.CODIGO = V.COD_EIXOS " +
                    "JOIN VEICULO_TIPO TV ON TV.CODIGO = V.COD_TIPO " +
                    "JOIN MARCA_VEICULO MAV ON MAV.CODIGO = MV.COD_MARCA " +
                    "JOIN UNIDADE U ON U.CODIGO = V.COD_UNIDADE " +
                    "JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO " +
                    "WHERE V.COD_UNIDADE = ? " +
                    "AND (? = 1 OR V.STATUS_ATIVO = ?) " +
                    "ORDER BY V.PLACA");
            stmt.setLong(1, codUnidade);

            // Se for nulo não filtramos por ativos/inativos.
            if (ativos == null) {
                stmt.setInt(2, 1);
                stmt.setBoolean(3, false);
            } else {
                stmt.setInt(2, 0);
                stmt.setBoolean(3, ativos);
            }

            rSet = stmt.executeQuery();
            while (rSet.next()) {
                veiculos.add(createVeiculo(rSet));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return veiculos;
    }

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidadeByColaborador(Long cpf) throws SQLException {
        List<Veiculo> veiculos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "V.*, " +
                    "R.CODIGO AS COD_REGIONAL_ALOCADO, " +
                    "MV.NOME AS MODELO, " +
                    "EV.NOME AS EIXOS, " +
                    "EV.DIANTEIRO, " +
                    "EV.TRASEIRO, " +
                    "EV.CODIGO AS COD_EIXOS, " +
                    "tv.nome AS TIPO, " +
                    "MAV.NOME AS MARCA, " +
                    "MAV.CODIGO AS COD_MARCA  "
                    + "FROM VEICULO V JOIN MODELO_VEICULO MV ON MV.CODIGO = V.COD_MODELO "
                    + "JOIN EIXOS_VEICULO EV ON EV.CODIGO = V.COD_EIXOS "
                    + "JOIN VEICULO_TIPO TV ON TV.CODIGO = V.COD_TIPO "
                    + "JOIN MARCA_VEICULO MAV ON MAV.CODIGO = MV.COD_MARCA "
                    + "JOIN UNIDADE U ON U.CODIGO = V.COD_UNIDADE "
                    + "JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO "
                    + "WHERE V.COD_UNIDADE = (SELECT COD_UNIDADE FROM COLABORADOR C WHERE C.CPF = ?) "
                    + "ORDER BY V.PLACA");
            stmt.setLong(1, cpf);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final Veiculo veiculo = createVeiculo(rSet);
                veiculos.add(veiculo);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return veiculos;
    }

    @Override
    public Veiculo getVeiculoByPlaca(String placa, boolean withPneus) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final PneuDao pneuDao = Injection.providePneuDao();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(VEICULOS_BY_PLACA);
            stmt.setString(1, placa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Veiculo veiculo = createVeiculo(rSet);
                if (withPneus) {
                    veiculo.setListPneus(pneuDao.getPneusByPlaca(placa));
                }
                return veiculo;
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return new Veiculo();
    }

    @Override
    public List<TipoVeiculo> getTipoVeiculosByUnidade(Long codUnidade) throws SQLException {
        List<TipoVeiculo> listTipo = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM VEICULO_TIPO WHERE COD_UNIDADE = ? AND STATUS_ATIVO = TRUE");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                listTipo.add(new TipoVeiculo(rSet.getLong("CODIGO"), rSet.getString("NOME")));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return listTipo;
    }

    @Override
    public boolean insertTipoVeiculo(TipoVeiculo tipoVeiculo, Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO VEICULO_TIPO(COD_UNIDADE, NOME, STATUS_ATIVO) VALUES (?,?,?)");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, tipoVeiculo.getNome());
            stmt.setBoolean(3, true);
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao cadastrar o tipo de veículo");
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    @Override
    public TipoVeiculo getTipoVeiculo(Long codTipo, Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM veiculo_tipo WHERE codigo = ? AND cod_unidade = ?");
            stmt.setLong(1, codTipo);
            stmt.setLong(2, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                TipoVeiculo tipo = new TipoVeiculo();
                tipo.setCodigo(rSet.getLong("CODIGO"));
                tipo.setNome(rSet.getString("NOME"));
                return tipo;
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    @Override
    public boolean updateTipoVeiculo(TipoVeiculo tipo, Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE veiculo_tipo SET nome = ? WHERE codigo = ? AND cod_unidade = ?;");
            stmt.setString(1, tipo.getNome());
            stmt.setLong(2, tipo.getCodigo());
            stmt.setLong(3, codUnidade);
            return stmt.executeUpdate() > 0;
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    public boolean deleteTipoVeiculo(Long codTipo, Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("DELETE FROM veiculo_tipo WHERE codigo = ? AND cod_unidade = ?");
            stmt.setLong(1, codTipo);
            stmt.setLong(2, codUnidade);
            return stmt.executeUpdate() > 0;
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    @Override
    public List<Eixos> getEixos() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Eixos> eixos = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM EIXOS_VEICULO");
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                Eixos eixo = new Eixos();
                eixo.codigo = rSet.getLong("CODIGO");
                eixo.nome = rSet.getString("NOME");
                eixo.dianteiro = rSet.getInt("DIANTEIRO");
                eixo.traseiro = rSet.getInt("TRASEIRO");
                eixos.add(eixo);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return eixos;
    }

    @Override
    public void updateKmByPlaca(String placa, long km, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE VEICULO SET "
                    + "KM = ? "
                    + "WHERE PLACA = ?");
            stmt.setLong(1, km);
            stmt.setString(2, placa);
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao atualizar o km do veículo");
            }
        } finally {
            closeStatement(stmt);
        }
    }

    @Override
    public List<Marca> getMarcaModeloVeiculoByCodEmpresa(Long codEmpresa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;

        List<Marca> marcas = new ArrayList<>();
        List<Modelo> modelos = new ArrayList<>();
        Marca marca = new Marca();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT MO.CODIGO AS COD_MODELO, MO.NOME AS MODELO, MA.CODIGO AS COD_MARCA, MA.NOME AS MARCA"
                    + " FROM MARCA_VEICULO MA left JOIN MODELO_VEICULO MO ON MA.CODIGO = MO.COD_MARCA AND MO.cod_empresa = ? "
                    + "WHERE MO.COD_EMPRESA = ? OR MO.COD_EMPRESA IS NULL "
                    + "ORDER BY COD_MARCA, COD_MODELO");
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codEmpresa);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                if (marcas.size() == 0 && modelos.size() == 0) { //primeiro resultado do rset
                    Log.d("metodo", "marcas.size == 0");
                    marca.setCodigo(rSet.getLong("COD_MARCA"));
                    marca.setNome(rSet.getString("MARCA"));
                    if (rSet.getString("MODELO") != null) {
                        modelos.add(createModelo(rSet));
                    }
                } else {
                    Log.d("metodo", "marcas.size > 0");
                    if (marca.getCodigo() == rSet.getLong("COD_MARCA")) { // se o modelo atual pertence a mesma marca do modelo anterior
                        if (rSet.getString("MODELO") != null) {
                            modelos.add(createModelo(rSet));
                        }
                    } else { // modelo diferente, deve encerrar a marca e criar uma nova
                        marca.setModelos(modelos);
                        marcas.add(marca);
                        marca = new Marca();
                        modelos = new ArrayList<>();
                        marca.setCodigo(rSet.getLong("COD_MARCA"));
                        marca.setNome(rSet.getString("MARCA"));
                        if (rSet.getString("MODELO") != null) {
                            modelos.add(createModelo(rSet));
                        }
                    }
                }
            }
            marca.setModelos(modelos);
            marcas.add(marca);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return marcas;
    }

    private ModeloVeiculo createModelo(ResultSet rSet) throws SQLException {
        ModeloVeiculo modelo = new ModeloVeiculo();
        modelo.setCodigo(rSet.getLong("COD_MODELO"));
        modelo.setNome(rSet.getString("MODELO"));
        return modelo;
    }

    @Override
    public boolean insertModeloVeiculo(Modelo modelo, long codEmpresa, long codMarca) throws SQLException, NullPointerException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            if (modelo.getNome().trim().isEmpty()) {
                throw new NullPointerException("Modelo sem nome!");
            } else {
                conn = getConnection();
                stmt = conn.prepareStatement("INSERT INTO MODELO_VEICULO(NOME, COD_MARCA, COD_EMPRESA) VALUES (?,?,?)");
                stmt.setString(1, modelo.getNome());
                stmt.setLong(2, codMarca);
                stmt.setLong(3, codEmpresa);
                int count = stmt.executeUpdate();
                if (count == 0) {
                    throw new SQLException("Erro ao cadastrar o modelo do veículo");
                }
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    @Override
    public Modelo getModeloVeiculo(Long codUnidade, Long codModelo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM modelo_veiculo WHERE codigo = ? AND cod_empresa  = " +
                    "(SELECT cod_empresa FROM unidade WHERE codigo = ?)");
            stmt.setLong(1, codModelo);
            stmt.setLong(2, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                ModeloVeiculo modelo = new ModeloVeiculo();
                modelo.setCodigo(rSet.getLong("CODIGO"));
                modelo.setNome(rSet.getString("NOME"));
                return modelo;
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    @Override
    public boolean updateModelo(Modelo modelo, Long codUnidade, Long codMarca) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE modelo_veiculo SET nome = ?, cod_marca = ? WHERE codigo = ? and cod_empresa = " +
                    "(SELECT cod_empresa FROM unidade WHERE codigo = ?)");
            stmt.setString(1, modelo.getNome());
            stmt.setLong(2, codMarca);
            stmt.setLong(3, modelo.getCodigo());
            stmt.setLong(4, codUnidade);
            return stmt.executeUpdate() > 0;
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    @Override
    public boolean deleteModelo(Long codModelo, Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("DELETE FROM modelo_veiculo WHERE codigo = ? and cod_empresa = " +
                    "(SELECT cod_empresa FROM unidade WHERE codigo = ?)");
            stmt.setLong(1, codModelo);
            stmt.setLong(2, codUnidade);
            return stmt.executeUpdate() > 0;
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    @Override
    public int getTotalVeiculosByUnidade(Long codUnidade, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        int total = 0;
        try {
            stmt = conn.prepareStatement("SELECT COUNT(PLACA) FROM VEICULO WHERE STATUS_ATIVO = TRUE AND COD_UNIDADE = ?");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                total = rSet.getInt("COUNT");
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
        return total;
    }

    @Override
    public List<String> getPlacasVeiculosByTipo(Long codUnidade, String codTipo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<String> placas = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT V.PLACA FROM veiculo V JOIN veiculo_tipo VT ON V.cod_unidade = VT.cod_unidade\n" +
                    "AND V.cod_tipo = VT.codigo\n" +
                    "WHERE VT.cod_unidade = ? AND VT.codigo::TEXT LIKE ? " +
                    "ORDER BY V.PLACA");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, codTipo);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                placas.add(rSet.getString("placa"));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return placas;
    }

    @Override
    public Optional<DiagramaVeiculo> getDiagramaVeiculoByPlaca(@NotNull final String placa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT vd.*\n" +
                    "FROM veiculo v JOIN veiculo_tipo vt on v.cod_tipo = vt.codigo and v.cod_unidade = vt.cod_unidade\n" +
                    "JOIN veiculo_diagrama vd on vd.codigo = vt.cod_diagrama\n" +
                    "WHERE v.placa = ?");
            stmt.setString(1, placa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createDiagramaVeiculo(rSet, conn);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return Optional.empty();
    }

    @Override
    public Optional<DiagramaVeiculo> getDiagramaVeiculoByCod(@NotNull Short codDiagrama) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT *\n" +
                    "FROM veiculo_diagrama AS vd\n" +
                    "  JOIN veiculo_tipo AS vt\n" +
                    "    ON vd.codigo = vt.cod_diagrama\n" +
                    "WHERE vd.codigo = ?");
            stmt.setShort(1, codDiagrama);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createDiagramaVeiculo(rSet, conn);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return Optional.empty();
    }

    @Override
    public Set<DiagramaVeiculo> getDiagramasVeiculos() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Set<DiagramaVeiculo> diagramas = new HashSet<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM veiculo_diagrama ");
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                createDiagramaVeiculo(rSet, conn).ifPresent(diagramas::add);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return diagramas;
    }

    @Override
    public void adicionaPneuVeiculo(@NotNull final Connection conn,
                                    @NotNull final Long codUnidade,
                                    @NotNull final String placa,
                                    @NotNull final Long codPneu,
                                    final int posicaoPneuVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO veiculo_pneu (placa, cod_pneu, cod_unidade, posicao) " +
                    "VALUES (?, ?, ?, ?)");
            stmt.setString(1, placa);
            stmt.setLong(2, codPneu);
            stmt.setLong(3, codUnidade);
            stmt.setInt(4, posicaoPneuVeiculo);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao aplicar o pneu " + codPneu + " ao veículo " + placa);
            }
        } finally {
            closeStatement(stmt);
        }
    }

    @Override
    public void removePneuVeiculo(@NotNull final Connection conn,
                                  @NotNull final Long codUnidade,
                                  @NotNull final String placa,
                                  @NotNull final Long codPneu) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("DELETE FROM VEICULO_PNEU WHERE COD_UNIDADE = ? AND PLACA = ? AND " +
                    "COD_PNEU = ?;");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, placa);
            stmt.setLong(3, codPneu);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao remover o pneu " + codPneu + " da placa " + placa);
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private Optional<DiagramaVeiculo> createDiagramaVeiculo(ResultSet rSet, Connection conn) throws SQLException {
        return Optional.of(new DiagramaVeiculo(
                rSet.getShort("CODIGO"),
                rSet.getString("NOME"),
                getEixosDiagrama(rSet.getInt("CODIGO"), conn),
                rSet.getString("URL_IMAGEM")));
    }

    private Set<EixoVeiculo> getEixosDiagrama(int codDiagrama, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final Set<EixoVeiculo> eixos = new HashSet<>();
        try {
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM veiculo_diagrama_eixos " +
                    "WHERE cod_diagrama = ? " +
                    "ORDER BY posicao;");
            stmt.setInt(1, codDiagrama);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final EixoVeiculo eixoVeiculo = new EixoVeiculo(
                        TipoEixoVeiculo.fromString(rSet.getString("TIPO_EIXO")),
                        rSet.getInt("QT_PNEUS"),
                        rSet.getInt("POSICAO"),
                        rSet.getBoolean("EIXO_DIRECIONAL"));
                eixos.add(eixoVeiculo);
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
        return eixos;
    }

    private Veiculo createVeiculo(ResultSet rSet) throws SQLException {
        final Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(rSet.getString("PLACA"));
        veiculo.setAtivo(rSet.getBoolean("STATUS_ATIVO"));
        veiculo.setKmAtual(rSet.getLong("KM"));

        veiculo.setCodRegionalAlocado(rSet.getLong("COD_REGIONAL_ALOCADO"));
        veiculo.setCodUnidadeAlocado(rSet.getLong("COD_UNIDADE"));

        // Eixos.
        final Eixos eixos = new Eixos();
        eixos.codigo = rSet.getLong("COD_EIXOS");
        eixos.dianteiro = rSet.getInt("DIANTEIRO");
        eixos.traseiro = rSet.getInt("TRASEIRO");
        veiculo.setEixos(eixos);

        // Tipo do veículo.
        final TipoVeiculo tipo = new TipoVeiculo();
        tipo.setCodigo(rSet.getLong("COD_TIPO"));
        tipo.setNome(rSet.getString("TIPO"));
        veiculo.setTipo(tipo);

        // Marca do veículo.
        final Marca marca = new Marca();
        marca.setCodigo(rSet.getLong("COD_MARCA"));
        marca.setNome(rSet.getString("MARCA"));
        veiculo.setMarca(marca);

        // Modelo do veículo.
        final ModeloVeiculo modelo = new ModeloVeiculo();
        modelo.setCodigo(rSet.getLong("COD_MODELO"));
        modelo.setNome(rSet.getString("MODELO"));
        veiculo.setModelo(modelo);

        // Diagrama do veículo.
        getDiagramaVeiculoByPlaca(veiculo.getPlaca()).ifPresent(veiculo::setDiagrama);
        return veiculo;
    }

    public List<Veiculo> getVeiculoKm(Long codUnidade, String placa, String codTipo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Veiculo> veiculos = new ArrayList<>();
        Veiculo v = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT PLACA, KM FROM VEICULO V WHERE cod_unidade = ? AND " +
                    "cod_tipo::TEXT LIKE ? AND PLACA LIKE ? ORDER BY PLACA");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, codTipo);
            stmt.setString(3, placa);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                v = new Veiculo();
                v.setPlaca(rSet.getString("placa"));
                v.setKmAtual(rSet.getLong("km"));
                veiculos.add(v);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return veiculos;
    }
}
