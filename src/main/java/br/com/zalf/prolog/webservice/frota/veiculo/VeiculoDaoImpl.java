package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.EixoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.TipoEixoVeiculo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.*;

public final class VeiculoDaoImpl extends DatabaseConnection implements VeiculoDao {

    public VeiculoDaoImpl() {

    }

    @Override
    public boolean insert(@NotNull final VeiculoCadastro veiculo,
                          @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("SELECT * FROM FUNC_VEICULO_INSERE_VEICULO(" +
                    "F_COD_UNIDADE := ?," +
                    "F_PLACA := ?," +
                    "F_NUMERO_FROTA := ?," +
                    "F_KM_ATUAL := ?, " +
                    "F_COD_MODELO := ?, " +
                    "F_COD_TIPO := ?) AS CODIGO;");
            stmt.setLong(1, veiculo.getCodUnidadeAlocado());
            stmt.setString(2, veiculo.getPlacaVeiculo().toUpperCase());
            stmt.setString(3, veiculo.getIdentificadorFrotaVeiculo());
            stmt.setLong(4, veiculo.getKmAtualVeiculo());
            stmt.setLong(5, veiculo.getCodModeloVeiculo());
            stmt.setLong(6, veiculo.getCodTipoVeiculo());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codVeiculoInserido = rSet.getLong("CODIGO");
                if (codVeiculoInserido <= 0) {
                    throw new SQLException("Erro ao inserir veículo:\n" +
                            "codUnidade: " + veiculo.getCodUnidadeAlocado() + "\n" +
                            "codVeiculoInserido: " + codVeiculoInserido);
                }
                // Avisamos ao Listener que um veículo foi inserido.
                checklistOfflineListener.onInsertVeiculo(conn, codVeiculoInserido);

                conn.commit();
                return true;
            } else {
                throw new SQLException("Erro ao inserir o veículo");
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
    public boolean update(
            @NotNull final String placaOriginal,
            @NotNull final Veiculo veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // Busca armazena informações do veículo atual
            final Veiculo veiculoBd = getVeiculoByPlaca(conn, placaOriginal, false);
            // A verificação de pneus aplicados para alteração de tipo agora é feita diretamente na function

            // O 'veiculoBd' é o veículo antes de ser atualizado as informações.
            final long kmAntigoVeiculo = veiculoBd.getKmAtual();
            final long kmNovoVeiculo = veiculo.getKmAtual();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_VEICULO_ATUALIZA_VEICULO(" +
                    "F_PLACA := ?," +
                    "F_NOVO_NUMERO_FROTA := ?, " +
                    "F_NOVO_KM := ?, " +
                    "F_NOVO_COD_MODELO := ?, " +
                    "F_NOVO_COD_TIPO := ?) AS CODIGO;");
            stmt.setString(1, placaOriginal);
            stmt.setString(2, veiculo.getNumeroFrota());
            stmt.setLong(3, kmNovoVeiculo);
            stmt.setLong(4, veiculo.getCodModelo());
            stmt.setLong(5, veiculo.getCodTipo());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codVeiculoAtualizado = rSet.getLong("CODIGO");
                if (codVeiculoAtualizado <= 0) {
                    throw new SQLException("Erro ao atualizar o veículo:\n" +
                            "placaOriginal: " + placaOriginal + "\n" +
                            "codVeiculoAtualizado: " + codVeiculoAtualizado);
                }
                // Notificamos o Listener sobre a atualização do veículo.
                checklistOfflineListener.onUpdateVeiculo(conn, codVeiculoAtualizado, kmAntigoVeiculo, kmNovoVeiculo);
                conn.commit();
                return true;
            } else {
                throw new SQLException("Erro ao atualizar o veículo: " + placaOriginal);
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
    public void updateStatus(
            @NotNull final Long codUnidade,
            @NotNull final String placa,
            @NotNull final Veiculo veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("UPDATE VEICULO SET STATUS_ATIVO = ? " +
                    "WHERE COD_UNIDADE = ? AND PLACA = ? RETURNING CODIGO;");
            stmt.setBoolean(1, veiculo.isAtivo());
            stmt.setLong(2, codUnidade);
            stmt.setString(3, placa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codVeiculoAtualizado = rSet.getLong("CODIGO");
                if (codVeiculoAtualizado <= 0) {
                    throw new SQLException("Erro ao atualizar o status do veículo:\n" +
                            "codUnidade: " + codUnidade + "\n" +
                            "placa: " + placa + "\n" +
                            "codVeiculoAtualizado: " + codVeiculoAtualizado);
                }
                // Devemos disparar o listener avisando que ocorreu uma atualização de Status.
                checklistOfflineListener.onUpdateStatusVeiculo(conn, codVeiculoAtualizado);
                conn.commit();
            } else {
                throw new SQLException("Erro ao atualizar o status do veículo com placa: " + placa);
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
    public boolean delete(
            @NotNull final String placa,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("UPDATE VEICULO SET STATUS_ATIVO = ? "
                    + "WHERE PLACA = ? RETURNING CODIGO");
            stmt.setBoolean(1, false);
            stmt.setString(2, placa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codVeiculoDeletado = rSet.getLong("CODIGO");
                if (codVeiculoDeletado <= 0) {
                    throw new SQLException("Erro ao inativar o veículo:\n" +
                            "placa: " + placa + "\n" +
                            "codVeiculoDeletado: " + codVeiculoDeletado);
                }
                // Devemos disparar o listener avisando que ocorreu uma inativação.
                checklistOfflineListener.onDeleteVeiculo(conn, codVeiculoDeletado);
                conn.commit();
                return true;
            } else {
                throw new SQLException("Erro ao inativar o veículo, placa: " + placa);
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
    public List<VeiculoListagem> buscaVeiculosAtivosByUnidade(@NotNull final Long codUnidade,
                                                              @Nullable final Boolean ativos) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_VEICULO_GET_ALL_BY_UNIDADE (F_COD_UNIDADE := ?," +
                    "F_STATUS_ATIVO := ?); ");
            stmt.setLong(1, codUnidade);

            // Se for nulo não filtramos por ativos/inativos.
            if (ativos == null) {
                stmt.setNull(2, Types.BOOLEAN);
            } else {
                stmt.setBoolean(2, ativos);
            }

            rSet = stmt.executeQuery();
            final List<VeiculoListagem> veiculos = new ArrayList<>();
            while (rSet.next()) {
                veiculos.add(VeiculoConverter.createVeiculoListagem(rSet));
            }
            return veiculos;
        } finally {
            close(conn, stmt, rSet);
        }
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
                    "TV.NOME AS TIPO, " +
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
            close(conn, stmt, rSet);
        }
        return veiculos;
    }

    @Override
    public VeiculoVisualizacao buscaVeiculoByCodigo(@NotNull final Long codVeiculo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNCTION FUNC_VEICULO_GET_VEICULO_COMPLETO(" +
                    "F_COD_VEICULO := ?);");
            stmt.setLong(1, codVeiculo);
            if (rSet.next()) {
                VeiculoVisualizacao veiculoVisualizacao = VeiculoConverter.createVeiculoVisualizacao(rSet);
                //TODO: Fazer essa parte.
              /*  if (withPneus) {
                    final PneuDao pneuDao = Injection.providePneuDao();
                    veiculo.setListPneus(pneuDao.getPneusByPlaca(placa));
                }*/
                return veiculoVisualizacao;
            } else {
                throw new Throwable("Erro ao finalizar esta solitação de socorro");
            }
        }finally {
            close(conn, stmt, rSet);
        }
    }

    @Deprecated
    @NotNull
    @Override
    public Veiculo getVeiculoByPlaca(@NotNull final Connection conn,
                                     @NotNull final String placa,
                                     final boolean withPneus) throws Throwable {
        return internalGetVeiculoByPlaca(conn, placa, withPneus);
    }

    @NotNull
    @Override
    public Veiculo getVeiculoByPlaca(@NotNull final String placa, final boolean withPneus) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            return internalGetVeiculoByPlaca(conn, placa, withPneus);
        } finally {
            close(conn);
        }
    }

    @NotNull
    private Veiculo internalGetVeiculoByPlaca(@NotNull final Connection conn,
                                              @NotNull final String placa,
                                              final boolean withPneus) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
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
                    "MAV.CODIGO AS COD_MARCA  " +
                    "FROM VEICULO V JOIN MODELO_VEICULO MV ON MV.CODIGO = V.COD_MODELO " +
                    "JOIN EIXOS_VEICULO EV ON EV.CODIGO = V.COD_EIXOS " +
                    "JOIN VEICULO_TIPO TV ON TV.CODIGO = V.COD_TIPO " +
                    "JOIN MARCA_VEICULO MAV ON MAV.CODIGO = MV.COD_MARCA " +
                    "JOIN UNIDADE U ON U.CODIGO = V.COD_UNIDADE " +
                    "JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO " +
                    "WHERE V.PLACA = ?;");
            stmt.setString(1, placa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Veiculo veiculo = createVeiculo(rSet);
                if (withPneus) {
                    final PneuDao pneuDao = Injection.providePneuDao();
                    veiculo.setListPneus(pneuDao.getPneusByPlaca(placa));
                }
                return veiculo;
            } else {
                throw new IllegalStateException("Erro ao buscar veículo com a placa: " + placa);
            }
        } finally {
            close(stmt, rSet);
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
            close(conn, stmt, rSet);
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
            close(stmt);
        }
    }

    @Deprecated
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
            close(conn, stmt, rSet);
        }
        return marcas;
    }

    @NotNull
    @Override
    public List<Marca> getMarcasVeiculosNivelProLog() throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_VEICULO_GET_MARCAS_NIVEL_PROLOG();");
            rSet = stmt.executeQuery();
            final List<Marca> marcas = new ArrayList<>();
            while (rSet.next()) {
                final Marca marca = new Marca();
                marca.setCodigo(rSet.getLong("COD_MARCA"));
                marca.setNome(rSet.getString("NOME_MARCA"));
                marcas.add(marca);
            }
            return marcas;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<Marca> getMarcasModelosVeiculosByEmpresa(@NotNull final Long codEmpresa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_VEICULO_GET_MARCAS_MODELOS_EMPRESA(?);");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            final List<Marca> marcas = new ArrayList<>();
            Long codMarcaAnterior = null;
            List<Modelo> modelos = null;
            while (rSet.next()) {
                if (codMarcaAnterior == null || !codMarcaAnterior.equals(rSet.getLong("COD_MARCA"))) {
                    final Marca marca = new Marca();
                    marca.setNome(rSet.getString("NOME_MARCA"));
                    marca.setCodigo(rSet.getLong("COD_MARCA"));
                    modelos = new ArrayList<>();
                    marca.setModelos(modelos);
                    marcas.add(marca);
                }
                // No caso de iterar e ficar na mesma marca, seria apenas necessário criar um novo modelo, como isso
                // sempre acontece, não precisamos de um if específico acima para tratar isso.

                // Modelos são adicionados na lista por referência. O if abaixo é necessário para os casos onde a
                // empresa não possui nenhum modelo para alguma marca.
                if (rSet.getLong("COD_MODELO") > 0) {
                    modelos.add(createModelo(rSet));
                }
                codMarcaAnterior = rSet.getLong("COD_MARCA");
            }
            return marcas;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private ModeloVeiculo createModelo(ResultSet rSet) throws SQLException {
        ModeloVeiculo modelo = new ModeloVeiculo();
        modelo.setCodigo(rSet.getLong("COD_MODELO"));
        modelo.setNome(rSet.getString("NOME_MODELO"));
        return modelo;
    }

    @NotNull
    @Override
    public Long insertModeloVeiculo(@NotNull final Modelo modelo,
                                    @NotNull final Long codEmpresa,
                                    @NotNull final Long codMarca) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO MODELO_VEICULO(NOME, COD_MARCA, COD_EMPRESA) VALUES (?,?,?) " +
                    "RETURNING CODIGO");
            stmt.setString(1, modelo.getNome());
            stmt.setLong(2, codMarca);
            stmt.setLong(3, codEmpresa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Erro ao cadastrar o modelo do veículo");
            }
        } finally {
            close(conn, stmt, rSet);
        }
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
            close(conn, stmt, rSet);
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
            close(conn, stmt);
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
            close(conn, stmt);
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
            close(stmt, rSet);
        }
        return total;
    }

    public List<String> getPlacasVeiculosByTipo(Long codUnidade, String codTipo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<String> placas = new ArrayList<>();
        try {
            conn = getConnection();
            // Não entendi essa parte, se já vem o código do tipo, porque receber ele em String e depois fazer join com
            // veiculo_tipo sendo que já tem o código do tipo na tabela veículo?
            stmt = conn.prepareStatement("SELECT V.PLACA FROM VEICULO V JOIN VEICULO_TIPO VT ON V.COD_TIPO = VT.CODIGO " +
                    "WHERE V.COD_UNIDADE = ? AND VT.CODIGO::TEXT LIKE ? ORDER BY V.PLACA;");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, codTipo);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                placas.add(rSet.getString("placa"));
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return placas;
    }

    @Override
    public Optional<DiagramaVeiculo> getDiagramaVeiculoByPlaca(@NotNull final String placa) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            return internalGetDiagramaVeiculoByPlaca(conn, placa);
        } finally {
            close(conn);
        }
    }

    @Override
    public Optional<DiagramaVeiculo> getDiagramaVeiculoByPlaca(@NotNull final Connection conn,
                                                               @NotNull final String placa) throws SQLException {
        return internalGetDiagramaVeiculoByPlaca(conn, placa);
    }

    @Override
    public Optional<DiagramaVeiculo> getDiagramaVeiculoByCod(@NotNull Short codDiagrama) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM veiculo_diagrama AS vd " +
                    "  JOIN veiculo_tipo AS vt " +
                    "    ON vd.codigo = vt.cod_diagrama " +
                    "WHERE vd.codigo = ?");
            stmt.setShort(1, codDiagrama);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createDiagramaVeiculo(rSet, conn);
            }
        } finally {
            close(conn, stmt, rSet);
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
            close(conn, stmt, rSet);
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
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_VEICULO_INSERE_VEICULO_PNEU(?, ?, ?, ?) AS RESULT;");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, placa);
            stmt.setLong(3, codPneu);
            stmt.setInt(4, posicaoPneuVeiculo);
            rSet = stmt.executeQuery();
            if (!rSet.next() || !rSet.getBoolean("RESULT")) {
                throw new SQLException("Erro ao aplicar o pneu " + codPneu + " ao veículo " + placa);
            }
        } finally {
            close(stmt, rSet);
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
            close(stmt);
        }
    }

    @Override
    public Long getCodUnidadeByPlaca(@NotNull final Connection conn,
                                     @NotNull final String placaVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT V.COD_UNIDADE FROM VEICULO V WHERE V.PLACA = ?;");
            stmt.setString(1, placaVeiculo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codUnidade = rSet.getLong("COD_UNIDADE");
                if (codUnidade <= 0) {
                    throw new IllegalStateException(
                            "Código da unidade inválido para a placa:" +
                                    "\nplacaVeiculo: " + placaVeiculo);
                }
                return codUnidade;
            } else {
                throw new IllegalStateException(
                        "Nenhum dado encontrado para a placa:" +
                                "\nplacaVeiculo: " + placaVeiculo);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Optional<List<Long>> getCodPneusAplicadosVeiculo(@NotNull final Connection conn,
                                                            @NotNull final Long codVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_VEICULO_GET_COD_PNEUS_APLICADOS(?);");
            stmt.setLong(1, codVeiculo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<Long> codPneusAplicados = new ArrayList<>();
                do {
                    codPneusAplicados.add(rSet.getLong("COD_PNEU"));
                } while (rSet.next());
                return Optional.of(codPneusAplicados);
            } else {
                return Optional.empty();
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Long getCodVeiculoByPlaca(@NotNull final Connection conn,
                                     @NotNull final String placaVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT V.CODIGO FROM VEICULO V WHERE V.PLACA = ?;");
            stmt.setString(1, placaVeiculo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codVeiculo = rSet.getLong("CODIGO");
                if (codVeiculo <= 0) {
                    throw new SQLException("Erro ao buscar código do veículo:" +
                            "\nplacaVeiculo: " + placaVeiculo + "" +
                            "\ncodVeiculo: " + codVeiculo);
                }
                return codVeiculo;
            } else {
                throw new SQLException("Erro ao buscar código do veículo:\n" +
                        "placaVeiculo: " + placaVeiculo);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private Optional<DiagramaVeiculo> internalGetDiagramaVeiculoByPlaca(@NotNull final Connection conn,
                                                                        @NotNull final String placa) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT VD.* " +
                    "FROM VEICULO V JOIN VEICULO_TIPO VT ON V.COD_TIPO = VT.CODIGO " +
                    "JOIN VEICULO_DIAGRAMA VD ON VD.CODIGO = VT.COD_DIAGRAMA " +
                    "WHERE V.PLACA = ?");
            stmt.setString(1, placa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createDiagramaVeiculo(rSet, conn);
            }
        } finally {
            close(stmt, rSet);
        }
        return Optional.empty();
    }

    @NotNull
    private Optional<DiagramaVeiculo> createDiagramaVeiculo(ResultSet rSet, Connection conn) throws SQLException {
        return Optional.of(new DiagramaVeiculo(
                rSet.getShort("CODIGO"),
                rSet.getString("NOME"),
                getEixosDiagrama(rSet.getInt("CODIGO"), conn),
                rSet.getString("URL_IMAGEM")));
    }

    @NotNull
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
            close(stmt, rSet);
        }
        return eixos;
    }

    @NotNull
    private Veiculo createVeiculo(ResultSet rSet) throws SQLException {
        final Veiculo veiculo = new Veiculo();
        veiculo.setCodigo(rSet.getLong("CODIGO"));
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

    @NotNull
    public List<Veiculo> getVeiculoKm(Long codUnidade, String placa, String codTipo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Veiculo v = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT PLACA, KM FROM VEICULO V WHERE cod_unidade = ? AND " +
                    "cod_tipo::TEXT LIKE ? AND PLACA LIKE ? ORDER BY PLACA");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, codTipo);
            stmt.setString(3, placa);
            rSet = stmt.executeQuery();
            final List<Veiculo> veiculos = new ArrayList<>();
            while (rSet.next()) {
                v = new Veiculo();
                v.setPlaca(rSet.getString("placa"));
                v.setKmAtual(rSet.getLong("km"));
                veiculos.add(v);
            }
            return veiculos;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Deprecated
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
            close(conn, stmt, rSet);
        }
        return veiculos;
    }
}