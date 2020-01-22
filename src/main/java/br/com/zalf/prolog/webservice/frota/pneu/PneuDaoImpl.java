package br.com.zalf.prolog.webservice.frota.pneu;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.pneu._model.*;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu.Dimensao;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizadoIncrementaVida;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Modelo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class PneuDaoImpl extends DatabaseConnection implements PneuDao {
    public static final String TAG = PneuDaoImpl.class.getSimpleName();

    public PneuDaoImpl() {

    }

    @NotNull
    @Override
    public List<Long> insert(@NotNull final List<Pneu> pneus) throws Throwable {
        Connection conn = null;
        int linha = 1;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            final List<Long> codigosPneus = new ArrayList<>(pneus.size());
            for (final Pneu pneu : pneus) {
                codigosPneus.add(internalInsert(conn, pneu, pneu.getCodUnidadeAlocado()));
                linha++;
            }
            conn.commit();
            return codigosPneus;
        } catch (Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw new GenericException("Erro ao inserir pneu da linha: " + linha + " -- " + e.getMessage());
        } finally {
            close(conn);
        }
    }

    @Override
    @NotNull
    public Long insert(Pneu pneu, Long codUnidade) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            final Long codPneuInserido = internalInsert(conn, pneu, codUnidade);
            conn.commit();
            return codPneuInserido;
        } catch (Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn);
        }
    }

    @NotNull
    private Long internalInsert(@NotNull final Connection conn,
                                @NotNull final Pneu pneu,
                                @NotNull final Long codUnidade) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO pneu (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, "
                    + "pressao_atual, altura_sulco_interno, altura_sulco_central_interno, altura_sulco_central_externo, "
                    + "altura_sulco_externo, cod_unidade, status, vida_atual, vida_total, cod_modelo_banda, dot, valor, "
                    + "pneu_novo_nunca_rodado, cod_empresa, cod_unidade_cadastro) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, "
                    + "(SELECT U.COD_EMPRESA FROM UNIDADE U WHERE U.CODIGO = ?),?) RETURNING CODIGO");
            stmt.setString(1, pneu.getCodigoCliente());
            stmt.setLong(2, pneu.getModelo().getCodigo());
            stmt.setLong(3, pneu.getDimensao().codigo);
            stmt.setDouble(4, pneu.getPressaoCorreta());
            // Pressão atual.
            stmt.setDouble(5, 0L);

            // Deixamos aqui apenas para tornar explícito que ao inserir um pneu seus valores de sulco são setados
            // para null.
            stmt.setNull(6, SqlType.REAL.asIntTypeJava());
            stmt.setNull(7, SqlType.REAL.asIntTypeJava());
            stmt.setNull(8, SqlType.REAL.asIntTypeJava());
            stmt.setNull(9, SqlType.REAL.asIntTypeJava());

            stmt.setLong(10, codUnidade);
            stmt.setString(11, pneu.getStatus().asString());
            stmt.setInt(12, pneu.getVidaAtual());
            stmt.setInt(13, pneu.getVidasTotal());
            if (pneu.getVidaAtual() == 1) {
                stmt.setNull(14, Types.BIGINT);
            } else {
                stmt.setLong(14, pneu.getBanda().getModelo().getCodigo());
            }
            if (pneu.getDot() == null) {
                stmt.setString(15, pneu.getDot());
            } else {
                stmt.setString(15, pneu.getDot().trim());
            }
            stmt.setBigDecimal(16, pneu.getValor());
            if (pneu.isPneuNovoNuncaRodado() != null) {
                stmt.setBoolean(17, pneu.isPneuNovoNuncaRodado());
            } else {
                stmt.setBoolean(17, false);
            }
            stmt.setLong(18, codUnidade);
            stmt.setLong(19, codUnidade);

            rSet = stmt.executeQuery();
            Long codPneu;
            if (rSet.next()) {
                codPneu = rSet.getLong("CODIGO");
                pneu.setCodigo(codPneu);
            } else {
                throw new SQLException("Erro ao inserir o pneu");
            }

            // Verifica se precisamos inserir informações de valor da banda para a vida atual.
            if (pneu.getVidaAtual() > 1) {
                criaServicoIncrementaVidaCadastroPneu(conn, codUnidade, pneu);
            }

            final List<PneuFotoCadastro> fotosCadastro = pneu.getFotosCadastro();
            if (fotosCadastro != null && !fotosCadastro.isEmpty()) {
                insertFotosCadastroPneu(pneu.getCodigo(), fotosCadastro, conn);
            }

            return codPneu;
        } finally {
            close(stmt, rSet);
        }
    }

    @Override
    public void update(@NotNull final Pneu pneu,
                       @NotNull final Long codUnidade,
                       @NotNull final Long codOriginalPneu) throws Throwable {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("UPDATE PNEU SET CODIGO_CLIENTE = ?, COD_MODELO = ?, COD_DIMENSAO = ?, "
                    + "COD_MODELO_BANDA = ?, DOT = ?, VALOR = ? "
                    + "WHERE CODIGO = ? AND COD_UNIDADE = ?;");
            stmt.setString(1, pneu.getCodigoCliente());
            stmt.setLong(2, pneu.getModelo().getCodigo());
            stmt.setLong(3, pneu.getDimensao().codigo);
            if (pneu.jaFoiRecapado()) {
                stmt.setLong(4, pneu.getBanda().getModelo().getCodigo());
                updateBandaPneu(
                        conn,
                        pneu.getCodigo(),
                        pneu.getBanda().getModelo().getCodigo(),
                        pneu.getBanda().getValor());
            } else {
                stmt.setNull(4, Types.BIGINT);
            }
            stmt.setString(5, pneu.getDot());
            stmt.setBigDecimal(6, pneu.getValor());
            stmt.setLong(7, codOriginalPneu);
            stmt.setLong(8, codUnidade);

            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao atualizar as informações do pneu: " + pneu.getCodigo());
            }
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

    @Override
    public List<Pneu> getPneusByPlaca(String placa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Pneu> listPneu = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_GET_PNEU_BY_PLACA(F_PLACA := ?);");
            stmt.setString(1, placa);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                listPneu.add(PneuConverter.createPneuCompleto(rSet, PneuTipo.PNEU_COMUM));
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return listPneu;
    }

    @Override
    public void incrementaVidaPneu(@NotNull final Connection conn,
                                   @NotNull final Long codPneu,
                                   @NotNull final Long codModeloBanda) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEUS_INCREMENTA_VIDA_PNEU(?, ?) ");
            stmt.setLong(1, codPneu);
            stmt.setLong(2, codModeloBanda);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                if (!rSet.getBoolean(1)) {
                    throw new SQLException("Erro ao trocar a vida dopneu: " + codPneu);
                }
            } else {
                throw new SQLException("Erro ao trocar a vida do pneu: " + codPneu);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @Override
    public boolean updateMedicoes(@NotNull final Connection conn,
                                  @NotNull final Long codPneu,
                                  @NotNull final Sulcos novosSulcos,
                                  final double novaPressao) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE PNEU SET "
                    + "PRESSAO_ATUAL = ?, ALTURA_SULCO_INTERNO = ?, ALTURA_SULCO_EXTERNO = ?, " +
                    "ALTURA_SULCO_CENTRAL_INTERNO = ?, ALTURA_SULCO_CENTRAL_EXTERNO = ? "
                    + "WHERE CODIGO = ?;");
            stmt.setDouble(1, novaPressao);
            stmt.setDouble(2, novosSulcos.getInterno());
            stmt.setDouble(3, novosSulcos.getExterno());
            stmt.setDouble(4, novosSulcos.getCentralInterno());
            stmt.setDouble(5, novosSulcos.getCentralExterno());
            stmt.setLong(6, codPneu);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao atualizar medições do pneu: " + codPneu);
            }
        } finally {
            close(stmt);
        }
        return true;
    }

    @Override
    public boolean updatePressao(@NotNull final Connection conn,
                                 @NotNull final Long codPneu,
                                 final double pressao) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("UPDATE PNEU SET "
                + "PRESSAO_ATUAL = ? "
                + "WHERE CODIGO = ?;");
        stmt.setDouble(1, pressao);
        stmt.setLong(2, codPneu);
        if (stmt.executeUpdate() == 0) {
            throw new SQLException("Erro ao atualizar pressão do pneu: " + codPneu);
        }
        return true;
    }

    @Override
    public void updateSulcos(@NotNull final Connection conn,
                             @NotNull final Long codPneu,
                             @NotNull final Sulcos novosSulcos) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE PNEU SET ALTURA_SULCO_INTERNO = ?, ALTURA_SULCO_EXTERNO = ?, "
                    + "ALTURA_SULCO_CENTRAL_INTERNO = ?, ALTURA_SULCO_CENTRAL_EXTERNO = ? "
                    + "WHERE CODIGO = ?;");
            stmt.setDouble(1, novosSulcos.getInterno());
            stmt.setDouble(2, novosSulcos.getExterno());
            stmt.setDouble(3, novosSulcos.getCentralInterno());
            stmt.setDouble(4, novosSulcos.getCentralExterno());
            stmt.setLong(5, codPneu);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao atualizar os dados do Pneu");
            }
        } finally {
            close(stmt);
        }
    }

    @Override
    public void updateStatus(@NotNull final Connection conn,
                             @NotNull final Pneu pneu,
                             @NotNull final StatusPneu status) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE PNEU SET "
                    + "STATUS = ? "
                    + "WHERE CODIGO = ?;");
            stmt.setString(1, status.asString());
            stmt.setLong(2, pneu.getCodigo());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao atualizar o status do pneu");
            }
        } finally {
            close(stmt);
        }
    }

    @NotNull
    @Override
    public List<Pneu> getPneusByCodUnidadeByStatus(@NotNull Long codUnidade, @NotNull StatusPneu status) throws Throwable {
        return internalGetPneus(codUnidade, status.asString());
    }

    @NotNull
    @Override
    public List<Pneu> getTodosPneus(@NotNull final Long codUnidade) throws Throwable {
        return internalGetPneus(codUnidade, "%");
    }

    @NotNull
    @Override
    public List<Pneu> getPneusAnalise(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEUS_GET_LISTAGEM_PNEUS_MOVIMENTACOES_ANALISE(?);");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            final List<Pneu> pneusAnalise = new ArrayList<>();
            while (rSet.next()) {
                pneusAnalise.add(PneuConverter.createPneuAnaliseCompleto(rSet));
            }
            return pneusAnalise;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public List<Marca> getMarcaModeloPneuByCodEmpresa(Long codEmpresa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Marca> marcas = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT DISTINCT MA.NOME AS NOME_MARCA_PNEU, MA.CODIGO AS COD_MARCA_PNEU " +
                    "FROM MARCA_PNEU MA LEFT OUTER JOIN MODELO_PNEU MP ON MA.CODIGO = MP.COD_MARCA " +
                    "AND MP.COD_EMPRESA = ? " +
                    "ORDER BY MA.NOME ASC");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final Marca marca = createMarcaPneu(rSet);
                marca.setModelos(getModelosPneu(conn, codEmpresa, marca.getCodigo()));
                marcas.add(marca);
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return marcas;
    }

    @NotNull
    @Override
    public List<Dimensao> getDimensoes() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Dimensao> dimensoes = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM DIMENSAO_PNEU;");
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final Dimensao dimensao = new Dimensao();
                dimensao.setCodigo(rSet.getLong("CODIGO"));
                dimensao.setAltura(rSet.getInt("ALTURA"));
                dimensao.setLargura(rSet.getInt("LARGURA"));
                dimensao.setAro(rSet.getDouble("ARO"));
                dimensoes.add(dimensao);
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return dimensoes;
    }

    @Override
    public boolean vinculaPneuVeiculo(String placaVeiculo, List<PneuComum> pneus) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            for (PneuComum pneu : pneus) {
                stmt = conn.prepareStatement("INSERT INTO VEICULO_PNEU VALUES(?,?,(SELECT COD_UNIDADE FROM VEICULO " +
                        "WHERE PLACA = ?),?);");
                stmt.setString(1, placaVeiculo);
                stmt.setLong(2, pneu.getCodigo());
                stmt.setString(3, placaVeiculo);
                stmt.setInt(4, pneu.getPosicao());
                rSet = stmt.executeQuery();
                if (rSet.next()) {
                    updateStatus(conn, pneu, StatusPneu.EM_USO);
                    updatePneuNovoNuncaRodado(conn, pneu.getCodigo(), false);
                } else {
                    throw new SQLException("Erro ao vincular o pneu ao veículo");
                }
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            close(conn, stmt, rSet);
        }
        return true;
    }

    @NotNull
    @Override
    public Pneu getPneuByCod(@NotNull final Long codPneu, @NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            return getPneuByCod(conn, codUnidade, codPneu);
        } finally {
            close(conn);
        }
    }

    @NotNull
    @Override
    public Pneu getPneuByCod(@NotNull final Connection conn,
                             @NotNull final Long codUnidade,
                             @NotNull final Long codPneu) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_GET_PNEU_BY_CODIGO(?);");
            stmt.setLong(1, codPneu);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Pneu pneu = PneuConverter.createPneuCompleto(
                        rSet,
                        StatusPneu.fromString(rSet.getString("STATUS")).toPneuTipo());
                pneu.setFotosCadastro(getFotosCadastroPneu(codPneu, conn));
                return pneu;
            } else {
                throw new SQLException("Nenhum pneu encontrado com o código: " + codPneu + " e unidade: " + codUnidade);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @Override
    public void marcarFotoComoSincronizada(@NotNull final Long codPneu,
                                           @NotNull final String urlFotoPneu) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE PNEU_FOTO_CADASTRO SET FOTO_SINCRONIZADA = TRUE, " +
                    "DATA_HORA_SINCRONIZACAO_FOTO = ? WHERE COD_PNEU = ? AND URL_FOTO = ?;");
            stmt.setTimestamp(1, Now.timestampUtc());
            stmt.setLong(2, codPneu);
            stmt.setString(3, urlFotoPneu);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao marcar a foto como sincronizada com URL: " + urlFotoPneu);
            }
        } finally {
            close(conn, stmt);
        }
    }

    @NotNull
    @Override
    public List<Long> getCodPneuByCodCliente(@NotNull final Connection conn,
                                             @NotNull final Long codEmpresa,
                                             @NotNull final List<String> codigoClientePneus) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_PNEU_GET_COD_PNEU_BY_CODIGO_CLIENTE(F_COD_EMPRESA => ?, F_COD_CLIENTE => ?);");
            stmt.setLong(1, codEmpresa);
            stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.TEXT, codigoClientePneus));
            rSet = stmt.executeQuery();
            final List<Long> codPneus = new ArrayList<>();
            while (rSet.next()) {
                codPneus.add(rSet.getLong("COD_PNEU"));
            }
            return codPneus;
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private List<Pneu> internalGetPneus(@NotNull final Long codUnidade,
                                        @NotNull final String statusString) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_GET_LISTAGEM_PNEUS_BY_STATUS(?, ?);");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, statusString);
            rSet = stmt.executeQuery();
            final List<Pneu> pneus = new ArrayList<>();
            while (rSet.next()) {
                pneus.add(PneuConverter.createPneuCompleto(
                        rSet,
                        StatusPneu.fromString(rSet.getString("STATUS")).toPneuTipo()));
            }
            return pneus;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private void insertFotosCadastroPneu(@NotNull final Long codPneu,
                                         @NotNull final List<PneuFotoCadastro> fotosCadastro,
                                         @NotNull final Connection connection) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("INSERT INTO PNEU_FOTO_CADASTRO(COD_PNEU, URL_FOTO) VALUES (?, ?);");
            for (final PneuFotoCadastro fotoCadastro : fotosCadastro) {
                stmt.setLong(1, codPneu);
                stmt.setString(2, fotoCadastro.getUrlFoto());
                if (stmt.executeUpdate() == 0) {
                    throw new SQLException("Erro ao inserir URL de foto do pneu: " + codPneu);
                }
            }
        } finally {
            close(stmt);
        }
    }

    @Nullable
    private List<PneuFotoCadastro> getFotosCadastroPneu(@NotNull final Long codPneu,
                                                        @NotNull final Connection connection) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = connection.prepareStatement("SELECT CODIGO, URL_FOTO, FOTO_SINCRONIZADA " +
                    "FROM PNEU_FOTO_CADASTRO PFC WHERE PFC.COD_PNEU = ?;");
            stmt.setLong(1, codPneu);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<PneuFotoCadastro> fotosCadastro = new ArrayList<>();
                do {
                    fotosCadastro.add(new PneuFotoCadastro(
                            rSet.getLong("CODIGO"),
                            rSet.getString("URL_FOTO"),
                            rSet.getBoolean("FOTO_SINCRONIZADA")));
                } while (rSet.next());
                return fotosCadastro;
            }
        } finally {
            close(stmt, rSet);
        }
        return null;
    }

    private void updatePneuNovoNuncaRodado(@NotNull final Connection conn,
                                           @NotNull final Long codPneu,
                                           final boolean pneuNovoNuncaRodado) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE PNEU SET PNEU_NOVO_NUNCA_RODADO = ? WHERE CODIGO = ?;");
            stmt.setBoolean(1, pneuNovoNuncaRodado);
            stmt.setLong(2, codPneu);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao atualizar flag de pneu novo para o pneu: " + codPneu);
            }
        } finally {
            close(stmt);
        }
    }

    private void criaServicoIncrementaVidaCadastroPneu(@NotNull final Connection conn,
                                                       @NotNull final Long codUnidade,
                                                       @NotNull final Pneu pneu) throws Throwable {
        PreparedStatement stmt = null;
        try {
            final PneuServicoRealizadoIncrementaVida servicoRecapagem =
                    createServicoRealizadoIncrementaVidaCadastro(conn, codUnidade, pneu);
            final Long codServicoRealizado = Injection
                    .providePneuServicoRealizadoDao()
                    .insertServicoByPneuCadastro(conn, codUnidade, pneu.getCodigo(), servicoRecapagem);
            stmt = conn.prepareStatement("INSERT INTO PNEU_SERVICO_CADASTRO " +
                    "(COD_PNEU, COD_SERVICO_REALIZADO) " +
                    "VALUES (?, ?);");
            stmt.setLong(1, pneu.getCodigo());
            stmt.setLong(2, codServicoRealizado);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir o valor da banda do pneu "
                        + pneu.getCodigo() + " da unidade " + codUnidade);
            }
        } finally {
            close(stmt);
        }
    }

    @NotNull
    private PneuServicoRealizadoIncrementaVida createServicoRealizadoIncrementaVidaCadastro(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final Pneu pneu) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT PTS.CODIGO AS CODIGO FROM PNEU_TIPO_SERVICO AS PTS " +
                    "WHERE PTS.COD_EMPRESA IS NULL " +
                    "AND PTS.STATUS_ATIVO = TRUE " +
                    "AND PTS.INCREMENTA_VIDA = TRUE " +
                    "AND PTS.UTILIZADO_CADASTRO_PNEU = TRUE;");
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final PneuServicoRealizadoIncrementaVida servico = new PneuServicoRealizadoIncrementaVida();
                servico.setCodPneuTipoServico(rSet.getLong("CODIGO"));
                servico.setCodUnidade(codUnidade);
                servico.setCodPneu(pneu.getCodigo());
                servico.setCusto(pneu.getValorBanda());
                servico.setVidaMomentoRealizacaoServico(pneu.getVidaAtual() - 1);
                servico.setCodModeloBanda(pneu.getCodModeloBanda());
                servico.setVidaNovaPneu(pneu.getVidaAtual());
                return servico;
            } else {
                throw new SQLException("Erro ao criar o serviço de recapagem para a troca de vida do pneu inserido");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private Marca createMarcaPneu(@NotNull final ResultSet rSet) throws SQLException {
        final Marca marca = new Marca();
        marca.setCodigo(rSet.getLong("COD_MARCA_PNEU"));
        marca.setNome(rSet.getString("NOME_MARCA_PNEU"));
        return marca;
    }

    @NotNull
    private List<Modelo> getModelosPneu(@NotNull final Connection conn,
                                        @NotNull final Long codEmpresa,
                                        @NotNull final Long codMarcaPneu) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Modelo> modelos = new ArrayList<>();
        try {
            stmt = conn.prepareStatement("SELECT * FROM MODELO_PNEU WHERE COD_EMPRESA = ? " +
                    "AND COD_MARCA = ? ORDER BY NOME ASC;");
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codMarcaPneu);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                modelos.add(createModeloPneu(rSet));
            }
        } finally {
            close(stmt, rSet);
        }
        return modelos;
    }

    @NotNull
    private Modelo createModeloPneu(@NotNull final ResultSet rSet) throws SQLException {
        final ModeloPneu modelo = new ModeloPneu();
        modelo.setCodigo(rSet.getLong("CODIGO"));
        modelo.setNome(rSet.getString("NOME"));
        modelo.setQuantidadeSulcos(rSet.getInt("QT_SULCOS"));
        modelo.setAlturaSulcos(rSet.getDouble("ALTURA_SULCOS"));
        return modelo;
    }

    private void updateBandaPneu(@NotNull final Connection conn,
                                 @NotNull final Long codPneu,
                                 @NotNull final Long codModeloBanda,
                                 @NotNull final BigDecimal valorBanda) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM func_pneus_update_banda_pneu(?, ?, ?)");
            stmt.setLong(1, codPneu);
            stmt.setLong(2, codModeloBanda);
            stmt.setBigDecimal(3, valorBanda);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                if (!rSet.getBoolean(1)) {
                    throw new SQLException("Erro ao atualizar as informações de banda do pneu: " + codPneu);
                }
            } else {
                throw new SQLException("Erro ao atualizar as informações de banda do pneu: " + codPneu);
            }
        } finally {
            close(stmt, rSet);
        }
    }
}