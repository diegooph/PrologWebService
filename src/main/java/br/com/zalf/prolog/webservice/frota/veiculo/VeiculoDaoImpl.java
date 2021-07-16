package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.NullIf;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.database.StatementUtils;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.errorhandling.Exceptions;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.EixoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.TipoEixoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.InfosVeiculoEditado;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.VeiculoAntesEdicao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.VeiculoEdicao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.listagem.VeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.model.listagem.VeiculosAcopladosPorVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoDadosColetaKm;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoVisualizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoVisualizacaoPneu;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculosAcopladosVisualizacao;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VeiculoCadastroDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.*;

public final class VeiculoDaoImpl extends DatabaseConnection implements VeiculoDao {

    public VeiculoDaoImpl() {

    }

    @Override
    public void insert(@NotNull final VeiculoCadastroDto veiculo,
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
                                                 "F_IDENTIFICADOR_FROTA := ?," +
                                                 "F_KM_ATUAL := ?, " +
                                                 "F_COD_MODELO := ?, " +
                                                 "F_COD_TIPO := ?," +
                                                 "F_POSSUI_HUBODOMETRO := ?," +
                                                 "F_ORIGEM_CADASTRO := ?) AS CODIGO;");
            stmt.setLong(1, veiculo.getCodUnidadeAlocado());
            stmt.setString(2, veiculo.getPlacaVeiculo().toUpperCase());
            stmt.setString(3, StringUtils.trimToNull(veiculo.getIdentificadorFrota()));
            stmt.setLong(4, veiculo.getKmAtualVeiculo());
            stmt.setLong(5, veiculo.getCodModeloVeiculo());
            stmt.setLong(6, veiculo.getCodTipoVeiculo());
            stmt.setBoolean(7, veiculo.getPossuiHubodometro());
            stmt.setString(8, OrigemAcaoEnum.PROLOG_WEB.asString());
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

    @NotNull
    @Override
    public InfosVeiculoEditado update(
            @NotNull final Long codColaboradorResponsavelEdicao,
            @NotNull final VeiculoEdicao veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("select * from func_veiculo_atualiza_veiculo(" +
                                                 "f_cod_veiculo := ?," +
                                                 "f_nova_placa := ?, " +
                                                 "f_novo_identificador_frota := ?, " +
                                                 "f_novo_km := ?, " +
                                                 "f_novo_cod_tipo := ?, " +
                                                 "f_novo_cod_modelo := ?, " +
                                                 "f_novo_status := ?, " +
                                                 "f_novo_possui_hubodometro := ?," +
                                                 "f_cod_colaborador_edicao := ?, " +
                                                 "f_origem_edicao := ?, " +
                                                 "f_data_hora_edicao := ?, " +
                                                 "f_informacoes_extras_edicao := null);");
            stmt.setLong(1, veiculo.getCodigo());
            stmt.setString(2, veiculo.getPlacaVeiculo());
            stmt.setString(3, StringUtils.trimToNull(veiculo.getIdentificadorFrota()));
            stmt.setLong(4, veiculo.getKmAtualVeiculo());
            stmt.setLong(5, veiculo.getCodTipoVeiculo());
            stmt.setLong(6, veiculo.getCodModeloVeiculo());
            stmt.setBoolean(7, veiculo.isStatusAtivo());
            stmt.setBoolean(8, veiculo.getPossuiHubodometro());
            stmt.setLong(9, codColaboradorResponsavelEdicao);
            stmt.setString(10, OrigemAcaoEnum.PROLOG_WEB.toString());
            stmt.setObject(11, Now.getOffsetDateTimeUtc());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final VeiculoAntesEdicao veiculoAntesEdicao = VeiculoConverter.createVeiculoAntesEdicao(rSet);

                // Notificamos o Listener sobre a atualização do veículo.
                final long kmAntigoVeiculo = veiculoAntesEdicao.getKmAntigo();
                final long kmNovoVeiculo = veiculo.getKmAtualVeiculo();
                final boolean statusAntigoVeiculo = veiculoAntesEdicao.isStatusAtivoAntigo();
                final boolean statusNovoVeiculo = veiculo.isStatusAtivo();
                checklistOfflineListener.onUpdateVeiculo(
                        conn,
                        veiculo.getCodigo(),
                        kmAntigoVeiculo,
                        kmNovoVeiculo,
                        statusAntigoVeiculo,
                        statusNovoVeiculo);
                conn.commit();
                return new InfosVeiculoEditado(
                        veiculo.getCodigo(),
                        NullIf.equalOrLess(rSet.getLong("cod_edicao_historico_antigo"), 0),
                        NullIf.equalOrLess(rSet.getLong("cod_edicao_historico_novo"), 0),
                        rSet.getInt("total_edicoes"),
                        veiculoAntesEdicao);
            } else {
                throw new SQLException("Erro ao atualizar o veículo de código: " + veiculo.getCodigo());
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

    @NotNull
    @Override
    public List<VeiculoListagem> getVeiculosByUnidades(@NotNull final List<Long> codUnidades,
                                                       final boolean apenasAtivos,
                                                       @Nullable final Long codTipoVeiculo)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from func_veiculo_get_all_by_unidades(" +
                                                 "f_cod_unidades => ?," +
                                                 "f_apenas_ativos => ?," +
                                                 "f_cod_tipo_veiculo => ?); ");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            stmt.setBoolean(2, apenasAtivos);
            StatementUtils.bindValueOrNull(stmt, 3, codTipoVeiculo, SqlType.BIGINT);
            rSet = stmt.executeQuery();
            final VeiculosAcopladosPorVeiculo veiculosAcopladosPorVeiculo = getVeiculosAcopladosByCodUnidades(
                    conn,
                    codUnidades,
                    apenasAtivos,
                    codTipoVeiculo);
            return VeiculoConverter.createVeiculosListagem(rSet, veiculosAcopladosPorVeiculo);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidadeByColaborador(final Long cpf) throws SQLException {
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
                                                 "EV.TRASEIRO, " +
                                                 "EV.CODIGO AS COD_EIXOS, " +
                                                 "TV.NOME AS TIPO, " +
                                                 "MAV.NOME AS MARCA, " +
                                                 "MAV.CODIGO AS COD_MARCA," +
                                                 "V.IDENTIFICADOR_FROTA AS ID_FROTA  "
                                                 + "FROM VEICULO V JOIN MODELO_VEICULO MV ON MV.CODIGO = V.COD_MODELO "
                                                 + "JOIN EIXOS_VEICULO EV ON EV.CODIGO = V.COD_EIXOS "
                                                 + "JOIN VEICULO_TIPO TV ON TV.CODIGO = V.COD_TIPO "
                                                 + "JOIN MARCA_VEICULO MAV ON MAV.CODIGO = MV.COD_MARCA "
                                                 + "JOIN UNIDADE U ON U.CODIGO = V.COD_UNIDADE "
                                                 + "JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO "
                                                 + "WHERE V.COD_UNIDADE = (SELECT COD_UNIDADE FROM COLABORADOR C " +
                                                 "WHERE C.CPF = ?) "
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

    @NotNull
    @Override
    public VeiculoVisualizacao getVeiculoByCodigo(@NotNull final Long codVeiculo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from func_veiculo_get_veiculo(f_cod_veiculo => ?);");
            stmt.setLong(1, codVeiculo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<VeiculoVisualizacaoPneu> pneus = getPneusByCodigoVeiculo(conn, codVeiculo);
                final Optional<VeiculosAcopladosVisualizacao> veiculosAcoplados =
                        getVeiculosAcopladosByCodVeiculo(conn, codVeiculo);
                return VeiculoConverter.createVeiculoVisualizacao(rSet, pneus, veiculosAcoplados);
            } else {
                throw new Throwable("Erro ao buscar veiculo de codigo " + codVeiculo);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    @NotNull
    public List<Long> getCodVeiculosByPlacas(@NotNull final Long codColaborador,
                                             @NotNull final List<String> placas) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_VEICULO_GET_CODIGO_BY_PLACA(" +
                                                 "F_COD_COLABORADOR => ?, " +
                                                 "F_PLACAS => ?);");
            stmt.setLong(1, codColaborador);
            stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.TEXT, placas));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Long[] codigos = (Long[]) rSet.getArray(1).getArray();
                return Arrays.asList(codigos);
            } else {
                throw new IllegalStateException("Erro ao buscar os códigos de veículos");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Deprecated
    @NotNull
    @Override
    public Veiculo getVeiculoByPlaca(@NotNull final String placa,
                                     @NotNull final Long codUnidade,
                                     final boolean withPneus) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            return internalGetVeiculoByPlaca(conn, codUnidade, placa, withPneus);
        } finally {
            close(conn);
        }
    }

    @Deprecated
    @NotNull
    @Override
    public Veiculo getVeiculoByPlaca(@NotNull final Connection conn,
                                     @NotNull final String placa,
                                     @NotNull final Long codUnidade,
                                     final boolean withPneus) throws Throwable {
        return internalGetVeiculoByPlaca(conn, codUnidade, placa, withPneus);
    }

    @Override
    public void updateKmByPlaca(final String placa, final long km, final Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE VEICULO SET "
                                                 + "KM = ? "
                                                 + "WHERE PLACA = ?");
            stmt.setLong(1, km);
            stmt.setString(2, placa);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao atualizar o km do veículo");
            }
        } finally {
            close(stmt);
        }
    }

    @Deprecated
    @Override
    @NotNull
    public Long updateKmByCodVeiculo(@NotNull final Connection conn,
                                     @NotNull final Long codUnidade,
                                     @NotNull final Long codVeiculo,
                                     @NotNull final Long veiculoCodProcesso,
                                     @NotNull final VeiculoTipoProcesso veiculoTipoProcesso,
                                     @NotNull final OffsetDateTime dataHoraProcesso,
                                     final long kmVeiculo,
                                     final boolean devePropagarKmParaReboques) {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("select * from func_veiculo_update_km_atual(" +
                                                 "f_cod_unidade => ?," +
                                                 "f_cod_veiculo => ?," +
                                                 "f_km_coletado => ?," +
                                                 "f_cod_processo => ?," +
                                                 "f_tipo_processo => ?," +
                                                 "f_deve_propagar_km => ?," +
                                                 "f_data_hora => ?) as km_processo;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codVeiculo);
            stmt.setLong(3, kmVeiculo);
            stmt.setLong(4, veiculoCodProcesso);
            stmt.setString(5, veiculoTipoProcesso.asString());
            stmt.setBoolean(6, devePropagarKmParaReboques);
            stmt.setObject(7, dataHoraProcesso);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long kmProcesso = rSet.getLong("km_processo");
                if (!rSet.wasNull()) {
                    return kmProcesso;
                }
            }

            throw new SQLException(String.format("Erro ao atualizar o km!" +
                                                         "\ncodUnidade: %d" +
                                                         "\ncodVeiculo: %d" +
                                                         "\ntipoProcesso: %s" +
                                                         "\nkmVeiculo: %d" +
                                                         "\ndevePropagarKm: %b",
                                                 codUnidade,
                                                 codVeiculo,
                                                 veiculoTipoProcesso.asString(),
                                                 kmVeiculo,
                                                 devePropagarKmParaReboques));
        } catch (final SQLException e) {
            throw Exceptions.rethrow(e);
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    @Deprecated
    @Override
    public List<Marca> getMarcaModeloVeiculoByCodEmpresa(final Long codEmpresa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;

        final List<Marca> marcas = new ArrayList<>();
        List<Modelo> modelos = new ArrayList<>();
        Marca marca = new Marca();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "SELECT MO.CODIGO AS COD_MODELO, MO.NOME AS MODELO, MA.CODIGO AS COD_MARCA, MA.NOME AS MARCA"
                            + " FROM MARCA_VEICULO MA left JOIN MODELO_VEICULO MO ON MA.CODIGO = MO.COD_MARCA AND MO" +
                            ".cod_empresa = ? "
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
                    if (marca.getCodigo() == rSet.getLong("COD_MARCA")) { // se o modelo atual pertence a mesma marca
                        // do modelo anterior
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

    @Deprecated
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

    @Deprecated
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
    public int getTotalVeiculosByUnidade(final Long codUnidade, final Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        int total = 0;
        try {
            stmt = conn.prepareStatement(
                    "SELECT COUNT(PLACA) FROM VEICULO WHERE STATUS_ATIVO = TRUE AND COD_UNIDADE = ?");
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

    @Override
    public List<String> getPlacasVeiculosByTipo(final Long codUnidade, final String codTipo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<String> placas = new ArrayList<>();
        try {
            conn = getConnection();
            // Não entendi essa parte, se já vem o código do tipo, porque receber ele em String e depois fazer join com
            // veiculo_tipo sendo que já tem o código do tipo na tabela veículo?
            stmt =
                    conn.prepareStatement(
                            "SELECT V.PLACA FROM VEICULO V JOIN VEICULO_TIPO VT ON V.COD_TIPO = VT.CODIGO " +
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
    public Optional<DiagramaVeiculo> getDiagramaVeiculoByPlaca(@NotNull final String placa,
                                                               @NotNull final Long codUnidade) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            return internalGetDiagramaVeiculoByPlaca(conn, placa, codUnidade);
        } finally {
            close(conn);
        }
    }

    @Override
    public Optional<DiagramaVeiculo> getDiagramaVeiculoByPlaca(@NotNull final Connection conn,
                                                               @NotNull final String placa,
                                                               @NotNull final Long codUnidade) throws SQLException {
        return internalGetDiagramaVeiculoByPlaca(conn, placa, codUnidade);
    }

    @Override
    public Optional<DiagramaVeiculo> getDiagramaVeiculoByCod(@NotNull final Short codDiagrama) throws SQLException {
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
        final Set<DiagramaVeiculo> diagramas = new HashSet<>();
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
    public Modelo getModeloVeiculo(final Long codUnidade, final Long codModelo) throws SQLException {
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
                final ModeloVeiculo modelo = new ModeloVeiculo();
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
    public boolean updateModelo(final Modelo modelo, final Long codUnidade, final Long codMarca) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "UPDATE modelo_veiculo SET nome = ?, cod_marca = ? WHERE codigo = ? and cod_empresa = " +
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
    public boolean deleteModelo(final Long codModelo, final Long codUnidade) throws SQLException {
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
    public void adicionaPneuVeiculo(@NotNull final Connection conn,
                                    @NotNull final Long codUnidade,
                                    @NotNull final String placa,
                                    @NotNull final Long codPneu,
                                    final int posicaoPneuVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_VEICULO_INSERE_VEICULO_PNEU(" +
                                                 "F_COD_UNIDADE => ?," +
                                                 "F_PLACA => ?," +
                                                 "F_COD_VEICULO => ?," +
                                                 "F_COD_PNEU  => ?," +
                                                 "F_POSICAO  => ?) AS RESULT;");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, placa);
            stmt.setLong(3, getCodVeiculoByPlaca(conn, placa, codUnidade));
            stmt.setLong(4, codPneu);
            stmt.setInt(5, posicaoPneuVeiculo);
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
                                  @NotNull final Long codVeiculo,
                                  @NotNull final Long codPneu) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("DELETE FROM VEICULO_PNEU WHERE COD_UNIDADE = ? AND COD_VEICULO = ? AND " +
                                                 "COD_PNEU = ?;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codVeiculo);
            stmt.setLong(3, codPneu);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao remover o pneu " + codPneu + " do veículo com código " + codVeiculo);
            }
        } finally {
            close(stmt);
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
    public Long getCodVeiculoByPlaca(@NotNull final Connection conn,
                                     @NotNull final String placaVeiculo,
                                     @NotNull final Long codUnidade) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("select v.codigo from veiculo v where v.placa = ? and v.cod_unidade = ?;");
            stmt.setString(1, placaVeiculo);
            stmt.setLong(2, codUnidade);
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

    @Deprecated
    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(final Long codUnidade, @Nullable final Boolean ativos)
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
                                                 "MAV.CODIGO AS COD_MARCA," +
                                                 "V.IDENTIFICADOR_FROTA AS ID_FROTA  " +
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

    @Override
    @NotNull
    public VeiculoDadosColetaKm getDadosColetaKmByCodigo(@NotNull final Long codVeiculo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "select * from func_veiculo_busca_dados_coleta_km_por_cod_veiculo(f_cod_veiculo => ?)");
            stmt.setLong(1, codVeiculo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return VeiculoConverter.createVeiculoDadosColetaKm(rSet);
            } else {
                throw new SQLException("Erro ao buscar o estado do veículo de código: " + codVeiculo);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    @NotNull
    public Set<EixoVeiculo> getEixosDiagrama(final int codDiagrama, final Connection conn) throws SQLException {
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
    private Optional<VeiculosAcopladosVisualizacao> getVeiculosAcopladosByCodVeiculo(@NotNull final Connection conn,
                                                                                     @NotNull final Long codVeiculo)
            throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("select * from func_veiculo_get_veiculos_acoplados(f_cod_veiculo => ?);");
            stmt.setLong(1, codVeiculo);
            rSet = stmt.executeQuery();
            return VeiculoConverter.createVeiculosAcopladosVisualizacao(rSet);
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private VeiculosAcopladosPorVeiculo getVeiculosAcopladosByCodUnidades(@NotNull final Connection conn,
                                                                          @NotNull final List<Long> codUnidades,
                                                                          final boolean apenasVeiculosAtivos,
                                                                          @Nullable final Long codTipoVeiculo)
            throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("select * from func_veiculo_get_veiculos_acoplados_unidades(" +
                                                 "f_cod_unidades => ?," +
                                                 "f_apenas_veiculos_ativos => ?," +
                                                 "f_cod_tipo_veiculo => ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            stmt.setBoolean(2, apenasVeiculosAtivos);
            StatementUtils.bindValueOrNull(stmt, 3, codTipoVeiculo, SqlType.BIGINT);
            rSet = stmt.executeQuery();
            return VeiculoConverter.createVeiculosAcopladosPorVeiculo(rSet);
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private List<VeiculoVisualizacaoPneu> getPneusByCodigoVeiculo(@NotNull final Connection conn,
                                                                  @NotNull final Long codVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_GET_PNEU_BY_COD_VEICULO(F_COD_VEICULO => ?);");
            stmt.setLong(1, codVeiculo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<VeiculoVisualizacaoPneu> pneus = new ArrayList<>();
                do {
                    pneus.add(VeiculoConverter.createVeiculoVisualizacaoPneu(rSet));
                } while (rSet.next());
                return pneus;
            }
            return Collections.emptyList();
        } finally {
            close(stmt, rSet);
        }
    }

    @Deprecated
    @NotNull
    private Veiculo internalGetVeiculoByPlaca(@NotNull final Connection conn,
                                              @NotNull final Long codUnidade,
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
                                                 "MAV.CODIGO AS COD_MARCA," +
                                                 "V.IDENTIFICADOR_FROTA AS ID_FROTA  " +
                                                 "FROM VEICULO V JOIN MODELO_VEICULO MV ON MV.CODIGO = V.COD_MODELO " +
                                                 "JOIN EIXOS_VEICULO EV ON EV.CODIGO = V.COD_EIXOS " +
                                                 "JOIN VEICULO_TIPO TV ON TV.CODIGO = V.COD_TIPO " +
                                                 "JOIN MARCA_VEICULO MAV ON MAV.CODIGO = MV.COD_MARCA " +
                                                 "JOIN UNIDADE U ON U.CODIGO = V.COD_UNIDADE " +
                                                 "JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO " +
                                                 "WHERE V.PLACA = ? " +
                                                 "AND V.COD_EMPRESA = (SELECT UN.COD_EMPRESA FROM UNIDADE UN WHERE UN" +
                                                 ".CODIGO = ?);");
            stmt.setString(1, placa);
            stmt.setLong(2, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Veiculo veiculo = createVeiculo(rSet);
                if (withPneus) {
                    final PneuDao pneuDao = Injection.providePneuDao();
                    veiculo.setListPneus(pneuDao.getPneusByPlaca(placa, codUnidade));
                }
                return veiculo;
            } else {
                throw new IllegalStateException("Erro ao buscar veículo com a placa: " + placa);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private ModeloVeiculo createModelo(final ResultSet rSet) throws SQLException {
        final ModeloVeiculo modelo = new ModeloVeiculo();
        modelo.setCodigo(rSet.getLong("COD_MODELO"));
        modelo.setNome(rSet.getString("NOME_MODELO"));
        return modelo;
    }

    @NotNull
    private Optional<DiagramaVeiculo> internalGetDiagramaVeiculoByPlaca(@NotNull final Connection conn,
                                                                        @NotNull final String placa,
                                                                        @NotNull final Long codUnidade)
            throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT VD.* " +
                                                 "FROM VEICULO V " +
                                                 "         JOIN VEICULO_TIPO VT ON V.COD_TIPO = VT.CODIGO " +
                                                 "         JOIN VEICULO_DIAGRAMA VD ON VD.CODIGO = VT.COD_DIAGRAMA " +
                                                 "WHERE V.PLACA = ? " +
                                                 "  AND V.COD_EMPRESA = (SELECT UN.COD_EMPRESA " +
                                                 "                       FROM UNIDADE UN " +
                                                 "                       WHERE UN.CODIGO = ?);");
            stmt.setString(1, placa);
            stmt.setLong(2, codUnidade);
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
    private Optional<DiagramaVeiculo> createDiagramaVeiculo(final ResultSet rSet,
                                                            final Connection conn) throws SQLException {
        return Optional.of(new DiagramaVeiculo(
                rSet.getShort("CODIGO"),
                rSet.getString("NOME"),
                getEixosDiagrama(rSet.getInt("CODIGO"), conn),
                rSet.getString("URL_IMAGEM")));
    }

    @NotNull
    private Veiculo createVeiculo(@NotNull final ResultSet rSet) throws SQLException {
        final Veiculo veiculo = new Veiculo();
        veiculo.setCodigo(rSet.getLong("CODIGO"));
        veiculo.setPlaca(rSet.getString("PLACA"));
        veiculo.setIdentificadorFrota(rSet.getString("ID_FROTA"));
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
        getDiagramaVeiculoByPlaca(veiculo.getPlaca(), veiculo.getCodUnidadeAlocado()).ifPresent(veiculo::setDiagrama);
        return veiculo;
    }
}
