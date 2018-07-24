package br.com.zalf.prolog.webservice.frota.pneu.pneu;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu.Dimensao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu_tipo_servico.model.PneuServicoRealizadoIncrementaVida;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Modelo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PneuDaoImpl extends DatabaseConnection implements PneuDao {
    public static final String TAG = PneuDaoImpl.class.getSimpleName();

    private static final String BASE_QUERY_BUSCA_PNEU = "SELECT " +
            "  MP.NOME                                    AS NOME_MARCA_PNEU, " +
            "  MP.CODIGO                                  AS COD_MARCA_PNEU, " +
            "  P.CODIGO, " +
            "  P.CODIGO_CLIENTE, " +
            "  U.CODIGO                                   AS COD_UNIDADE_ALOCADO, " +
            "  R.CODIGO                                   AS COD_REGIONAL_ALOCADO, " +
            "  P.PRESSAO_ATUAL, " +
            "  P.VIDA_ATUAL, " +
            "  P.VIDA_TOTAL, " +
            "  P.PNEU_NOVO_NUNCA_RODADO, " +
            "  MOP.NOME                                   AS NOME_MODELO_PNEU, " +
            "  MOP.CODIGO                                 AS COD_MODELO_PNEU, " +
            "  MOP.QT_SULCOS                              AS QT_SULCOS_MODELO_PNEU, " +
            "  MOP.ALTURA_SULCOS                          AS ALTURA_SULCOS_MODELO_PNEU, " +
            "  PD.ALTURA, " +
            "  PD.LARGURA, " +
            "  PD.ARO, " +
            "  PD.CODIGO                                  AS COD_DIMENSAO, " +
            "  P.PRESSAO_RECOMENDADA, " +
            "  P.ALTURA_SULCO_CENTRAL_INTERNO, " +
            "  P.ALTURA_SULCO_CENTRAL_EXTERNO, " +
            "  P.ALTURA_SULCO_INTERNO, " +
            "  P.ALTURA_SULCO_EXTERNO, " +
            "  P.STATUS, " +
            "  P.DOT, " +
            "  P.VALOR, " +
            "  MOB.CODIGO                                  AS COD_MODELO_BANDA, " +
            "  MOB.NOME                                    AS NOME_MODELO_BANDA, " +
            "  MOB.QT_SULCOS                               AS QT_SULCOS_MODELO_BANDA, " +
            "  MOB.ALTURA_SULCOS                           AS ALTURA_SULCOS_MODELO_BANDA, " +
            "  MAB.CODIGO                                  AS COD_MARCA_BANDA, " +
            "  MAB.NOME                                    AS NOME_MARCA_BANDA, " +
            "  PVV.VALOR                                   AS VALOR_BANDA, " +
            "  PO.POSICAO_PROLOG                           AS POSICAO_PNEU " +
            "FROM PNEU P " +
            "JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO " +
            "JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA " +
            "JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO " +
            "JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE " +
            "JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO " +
            "LEFT JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO AND VP.COD_UNIDADE = P.COD_UNIDADE " +
            "LEFT JOIN PNEU_ORDEM PO ON VP.POSICAO = PO.POSICAO_PROLOG " +
            "LEFT JOIN MODELO_BANDA MOB ON MOB.CODIGO = P.COD_MODELO_BANDA AND MOB.COD_EMPRESA = U.COD_EMPRESA " +
            "LEFT JOIN MARCA_BANDA MAB ON MAB.CODIGO = MOB.COD_MARCA AND MAB.COD_EMPRESA = MOB.COD_EMPRESA " +
            "LEFT JOIN PNEU_VALOR_VIDA PVV ON PVV.COD_PNEU = P.CODIGO AND PVV.VIDA = P.VIDA_ATUAL ";

    public PneuDaoImpl() {

    }

    @Override
    @NotNull
    public Long insert(Pneu pneu, Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Long codPneu;
        try {
            // Se um pneu tem número ímpar de sulcos, o valor do sulco central deve ser duplicado nos dois campos de
            // de sulco central.
            if (pneu.temQtdImparSulcos()) {
                if (!pneu.getSulcosAtuais().getCentralInterno().equals(pneu.getSulcosAtuais().getCentralExterno())) {
                    throw new IllegalStateException("Um pneu com número ímpar de sulcos deve ter seus sulcos centrais iguais");
                }
            }

            conn = getConnection();
            conn.setAutoCommit(false);
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
            stmt.setDouble(6, pneu.getSulcosAtuais().getInterno());
            stmt.setDouble(7, pneu.getSulcosAtuais().getCentralInterno());
            stmt.setDouble(8, pneu.getSulcosAtuais().getCentralExterno());
            stmt.setDouble(9, pneu.getSulcosAtuais().getExterno());
            stmt.setLong(10, codUnidade);
            stmt.setString(11, pneu.getStatus().asString());
            stmt.setInt(12, pneu.getVidaAtual());
            stmt.setInt(13, pneu.getVidasTotal());
            if (pneu.getVidaAtual() == 1) {
                stmt.setNull(14, Types.BIGINT);
            } else {
                stmt.setLong(14, pneu.getBanda().getModelo().getCodigo());
            }
            stmt.setString(15, pneu.getDot().trim());
            stmt.setBigDecimal(16, pneu.getValor());
            if (pneu.isPneuNovoNuncaRodado() != null) {
                stmt.setBoolean(17, pneu.isPneuNovoNuncaRodado());
            } else {
                stmt.setBoolean(17, false);
            }
            stmt.setLong(18, codUnidade);
            stmt.setLong(19, codUnidade);

            rSet = stmt.executeQuery();
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
                insertFotosCadastroPneu(pneu.getCodigo(), codUnidade, fotosCadastro, conn);
            }

            conn.commit();
        } catch (Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return codPneu;
    }

    @Override
    public boolean update(Pneu pneu, Long codUnidade, Long codOriginalPneu) throws SQLException {
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
                updateTrocaVidaPneu(pneu, codUnidade, conn);
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
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    @Override
    public List<PneuComum> getPneusByPlaca(String placa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<PneuComum> listPneu = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(BASE_QUERY_BUSCA_PNEU +
                    "WHERE VP.PLACA = ? " +
                    "ORDER BY PO.ORDEM_EXIBICAO ASC;");
            stmt.setString(1, placa);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                listPneu.add(PneuConverter.createPneuCompleto(rSet));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
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

            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEUS_INCREMENTA_VIDA_PNEU(?, ?) " );
                    stmt.setLong(1, codPneu);
                    stmt.setLong(2, codModeloBanda);
                    rSet = stmt.executeQuery();
                    if (rSet.next()) {
            if (!rSet.getBoolean(1)){
            throw new SQLException("Erro ao trocar a vida dopneu: " + codPneu);
                }
            } else {
                throw new SQLException("Erro ao trocar a vida do pneu: " + codPneu);
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
    }

    @Override
    public boolean updateMedicoes(PneuComum pneu, Long codUnidade, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE PNEU SET "
                    + "PRESSAO_ATUAL = ?, ALTURA_SULCO_INTERNO = ?, ALTURA_SULCO_EXTERNO = ?, " +
                    "ALTURA_SULCO_CENTRAL_INTERNO = ?, ALTURA_SULCO_CENTRAL_EXTERNO = ? "
                    + "WHERE CODIGO = ? AND COD_UNIDADE = ?");
            stmt.setDouble(1, pneu.getPressaoAtual());
            stmt.setDouble(2, pneu.getSulcosAtuais().getInterno());
            stmt.setDouble(3, pneu.getSulcosAtuais().getExterno());
            stmt.setDouble(4, pneu.getSulcosAtuais().getCentralInterno());
            stmt.setDouble(5, pneu.getSulcosAtuais().getCentralExterno());
            stmt.setLong(6, pneu.getCodigo());
            stmt.setLong(7, codUnidade);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao atualizar medições do pneu: " + pneu.getCodigo());
            }
        } finally {
            closeStatement(stmt);
        }
        return true;
    }

    @Override
    public boolean updatePressao(Long codPneu, double pressao, Long codUnidade, Connection conn) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("UPDATE PNEU SET "
                + "PRESSAO_ATUAL = ? "
                + "WHERE CODIGO = ? AND COD_UNIDADE = ?");
        stmt.setDouble(1, pressao);
        stmt.setLong(2, codPneu);
        stmt.setLong(3, codUnidade);
        if (stmt.executeUpdate() == 0) {
            throw new SQLException("Erro ao atualizar pressão do pneu: " + codPneu);
        }
        return true;
    }

    @Override
    public void updateStatus(Pneu pneu, Long codUnidade, StatusPneu status, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE PNEU SET "
                    + "STATUS = ? "
                    + "WHERE CODIGO = ? AND COD_UNIDADE = ?;");
            stmt.setString(1, status.asString());
            stmt.setLong(2, pneu.getCodigo());
            stmt.setLong(3, codUnidade);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao atualizar o status do pneu");
            }
        } finally {
            closeStatement(stmt);
        }
    }

    @NotNull
    @Override
    public List<Pneu> getPneusByCodUnidadeByStatus(Long codUnidade, StatusPneu status) throws Throwable {
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
            closeConnection(conn, stmt, rSet);
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
                marca.setModelos(getModelosPneu(codEmpresa, marca.getCodigo(), conn));
                marcas.add(marca);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return marcas;
    }

    @Override
    public Modelo getModeloPneu(Long codModelo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM MODELO_PNEU WHERE CODIGO = ? ");
            stmt.setLong(1, codModelo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createModeloPneu(rSet);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    @Override
    public List<Dimensao> getDimensoes() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Dimensao> dimensoes = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM DIMENSAO_PNEU");
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final Dimensao dimensao = new Dimensao();
                dimensao.codigo = rSet.getLong("CODIGO");
                dimensao.altura = rSet.getInt("ALTURA");
                dimensao.largura = rSet.getInt("LARGURA");
                dimensao.aro = rSet.getDouble("ARO");
                dimensoes.add(dimensao);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return dimensoes;
    }

    @Override
    public Long insertModeloPneu(ModeloPneu modelo, Long codEmpresa, Long codMarca) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO MODELO_PNEU(NOME, QT_SULCOS, ALTURA_SULCOS, COD_MARCA, COD_EMPRESA)" +
                    "  SELECT ?, ?, ?, ?, ?\n" +
                    "  WHERE NOT EXISTS (SELECT nome FROM modelo_pneu as mp " +
                    "WHERE lower(mp.nome) = lower(?) and mp.cod_marca = ? and mp.cod_empresa = ?)" +
                    "RETURNING codigo");
            stmt.setString(1, modelo.getNome());
            stmt.setInt(2, modelo.getQuantidadeSulcos());
            stmt.setDouble(3, modelo.getAlturaSulcos());
            stmt.setLong(4, codMarca);
            stmt.setLong(5, codEmpresa);
            stmt.setString(6, modelo.getNome());
            stmt.setLong(7, codMarca);
            stmt.setLong(8, codEmpresa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Erro ao inserir o modelo do pneu ou modelo já existente");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
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
                        "WHERE PLACA = ?),?) RETURNING COD_UNIDADE");
                stmt.setString(1, placaVeiculo);
                stmt.setLong(2, pneu.getCodigo());
                stmt.setString(3, placaVeiculo);
                stmt.setInt(4, pneu.getPosicao());
                rSet = stmt.executeQuery();
                if (rSet.next()) {
                    final Long codUnidade = rSet.getLong("COD_UNIDADE");
                    updateStatus(pneu, codUnidade, StatusPneu.EM_USO, conn);
                    updatePneuNovoNuncaRodado(pneu.getCodigo(), codUnidade, false, conn);
                } else {
                    throw new SQLException("Erro ao vincular o pneu ao veículo");
                }
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return true;
    }

    @Override
    public void updateSulcos(Long codPneu, Sulcos sulcosNovos, Long codUnidade, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE PNEU SET ALTURA_SULCO_INTERNO = ?, ALTURA_SULCO_EXTERNO = ?, "
                    + "ALTURA_SULCO_CENTRAL_INTERNO = ?, ALTURA_SULCO_CENTRAL_EXTERNO = ? "
                    + "WHERE CODIGO = ? AND COD_UNIDADE = ?;");
            stmt.setDouble(1, sulcosNovos.getInterno());
            stmt.setDouble(2, sulcosNovos.getExterno());
            stmt.setDouble(3, sulcosNovos.getCentralInterno());
            stmt.setDouble(4, sulcosNovos.getCentralExterno());
            stmt.setLong(5, codPneu);
            stmt.setLong(6, codUnidade);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao atualizar os dados do Pneu");
            }
        } finally {
            closeStatement(stmt);
        }
    }

    @Override
    public PneuComum getPneuByCod(Long codPneu, Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(BASE_QUERY_BUSCA_PNEU +
                    "WHERE P.CODIGO = ? AND P.cod_unidade = ?;");
            stmt.setLong(1, codPneu);
            stmt.setLong(2, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final PneuComum pneu = PneuConverter.createPneuCompleto(rSet);
                pneu.setFotosCadastro(getFotosCadastroPneu(codPneu, codUnidade, conn));
                return pneu;
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    @Override
    public List<Marca> getMarcaModeloBanda(Long codEmpresa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Marca> marcas = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM marca_banda WHERE cod_empresa = ? ORDER BY nome ASC");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final Marca marca = new Marca();
                marca.setCodigo(rSet.getLong("CODIGO"));
                marca.setNome(rSet.getString("NOME"));
                marca.setModelos(getModelosBanda(codEmpresa, marca.getCodigo(), conn));
                marcas.add(marca);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return marcas;
    }

    @Override
    public Long insertMarcaBanda(Marca marca, Long codEmpresa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO marca_banda" +
                    "    (nome, cod_empresa)" +
                    "SELECT ?, ?" +
                    "WHERE" +
                    "    NOT EXISTS (" +
                    "        SELECT nome FROM marca_banda WHERE lower(nome) = lower(?) and cod_empresa = ?" +
                    "    ) RETURNING codigo;");
            stmt.setString(1, marca.getNome().trim().replaceAll("\\s+", " "));
            stmt.setLong(2, codEmpresa);
            stmt.setString(3, marca.getNome().trim().toLowerCase().replaceAll("\\s+", " "));
            stmt.setLong(4, codEmpresa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Erro ao inserir a marca da banda ou banda já existente");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Long insertModeloBanda(ModeloBanda modelo, Long codMarcaBanda, Long codEmpresa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO modelo_banda " +
                    "    (nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos) " +
                    "SELECT ?, ?, ?, ?, ? " +
                    "WHERE NOT EXISTS " +
                    " (SELECT nome FROM modelo_banda WHERE lower(nome) = lower(?) and cod_marca = ? and cod_empresa = ?) " +
                    "RETURNING codigo;");
            stmt.setString(1, modelo.getNome().trim().replaceAll("\\s+", " "));
            stmt.setLong(2, codMarcaBanda);
            stmt.setLong(3, codEmpresa);
            stmt.setInt(4, modelo.getQuantidadeSulcos());
            stmt.setDouble(5, modelo.getAlturaSulcos());
            stmt.setString(6, modelo.getNome().trim().toLowerCase().replaceAll("\\s+", " "));
            stmt.setLong(7, codMarcaBanda);
            stmt.setLong(8, codEmpresa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Erro ao inserir o modelo da banda");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public boolean updateMarcaBanda(Marca marca, Long codEmpresa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE marca_banda SET nome = ? WHERE codigo = ? AND cod_empresa = ?");
            stmt.setString(1, marca.getNome());
            stmt.setLong(2, marca.getCodigo());
            stmt.setLong(3, codEmpresa);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao atualizar a marca da banca: " + marca.getCodigo());
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    @Override
    public boolean updateModeloBanda(Modelo modelo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE modelo_banda SET nome = ? WHERE codigo = ?");
            stmt.setString(1, modelo.getNome());
            stmt.setLong(2, modelo.getCodigo());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao atualizar a modelo da banca: " + modelo.getCodigo());
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    @Override
    public void marcarFotoComoSincronizada(@NotNull final Long codUnidade,
                                           @NotNull final Long codPneu,
                                           @NotNull final String urlFotoPneu) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE PNEU_FOTO_CADASTRO SET FOTO_SINCRONIZADA = TRUE, " +
                    "DATA_HORA_SINCRONIZACAO_FOTO = ? WHERE COD_UNIDADE_PNEU = ? AND COD_PNEU = ? AND URL_FOTO = ?;");
            stmt.setTimestamp(1, Now.timestampUtc());
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codPneu);
            stmt.setString(4, urlFotoPneu);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao marcar a foto como sincronizada com URL: " + urlFotoPneu);
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    @NotNull
    private List<Pneu> internalGetPneus(@NotNull final Long codUnidade, @NotNull final String statusString)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEUS_GET_LISTAGEM_PNEUS_BY_STATUS(?, ?);");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, statusString);
            rSet = stmt.executeQuery();
            final List<Pneu> pneus = new ArrayList<>();
            while (rSet.next()) {
                pneus.add(PneuConverter.createPneuCompleto(rSet));
            }
            return pneus;
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Nullable
    private List<PneuFotoCadastro> insertFotosCadastroPneu(@NotNull final Long codPneu,
                                                           @NotNull final Long codUnidade,
                                                           @NotNull final List<PneuFotoCadastro> fotosCadastro,
                                                           @NotNull final Connection connection)
            throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("INSERT INTO PNEU_FOTO_CADASTRO(COD_PNEU, COD_UNIDADE_PNEU, URL_FOTO) " +
                    "VALUES (?, ?, ?);");
            for (final PneuFotoCadastro fotoCadastro : fotosCadastro) {
                stmt.setLong(1, codPneu);
                stmt.setLong(2, codUnidade);
                stmt.setString(3, fotoCadastro.getUrlFoto());
                if (stmt.executeUpdate() == 0) {
                    throw new SQLException("Erro ao inserir URL de foto do pneu: " + codPneu + " da unidade: " + codUnidade);
                }
            }
        } finally {
            closeStatement(stmt);
        }
        return null;
    }

    @Nullable
    private List<PneuFotoCadastro> getFotosCadastroPneu(@NotNull final Long codPneu,
                                                        @NotNull final Long codUnidade,
                                                        @NotNull final Connection connection) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = connection.prepareStatement("SELECT CODIGO, URL_FOTO, FOTO_SINCRONIZADA " +
                    "FROM PNEU_FOTO_CADASTRO PFC WHERE PFC.COD_PNEU = ? AND PFC.COD_UNIDADE_PNEU = ?;");
            stmt.setLong(1, codPneu);
            stmt.setLong(2, codUnidade);
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
            closeConnection(null, stmt, rSet);
        }
        return null;
    }

    private void updatePneuNovoNuncaRodado(@NotNull final Long codPneu,
                                           @NotNull final Long codUnidade,
                                           final boolean pneuNovoNuncaRodado,
                                           @NotNull final Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE PNEU SET PNEU_NOVO_NUNCA_RODADO = ? " +
                    "WHERE CODIGO = ? AND COD_UNIDADE = ?;");
            stmt.setBoolean(1, pneuNovoNuncaRodado);
            stmt.setLong(2, codPneu);
            stmt.setLong(3, codUnidade);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao atualizar flag de pneu novo para o pneu: " + codPneu + " da unidade: " + codUnidade);
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private void criaServicoIncrementaVidaCadastroPneu(@NotNull final Connection conn,
                                                       @NotNull final Long codUnidade,
                                                       @NotNull final Pneu pneu) throws Throwable {
        PreparedStatement stmt = null;
        try {
            final PneuServicoRealizadoIncrementaVida servicoRecapagem = createServicoRealizadoIncrementaVidaCadastro(conn, codUnidade, pneu);
            final Long codServicoRealizado = Injection
                    .providePneuServicoRealizadoDao()
                    .insertServicoByPneuCadastro(conn, codUnidade, pneu.getCodigo(), servicoRecapagem);
            stmt = conn.prepareStatement("INSERT INTO PNEU_SERVICO_CADASTRO " +
                    "(COD_PNEU, COD_PNEU_SERVICO_REALIZADO) " +
                    "VALUES (?, ?);");
            stmt.setLong(1, pneu.getCodigo());
            stmt.setLong(2, codServicoRealizado);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir o valor da banda do pneu "
                        + pneu.getCodigo() + " da unidade " + codUnidade);
            }
        } finally {
            closeStatement(stmt);
        }
    }

    @NotNull
    private PneuServicoRealizadoIncrementaVida createServicoRealizadoIncrementaVidaCadastro(@NotNull final Connection conn,
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
            closeConnection(null, stmt, rSet);
        }
    }

    private Marca createMarcaPneu(ResultSet rSet) throws SQLException {
        final Marca marca = new Marca();
        marca.setCodigo(rSet.getLong("COD_MARCA_PNEU"));
        marca.setNome(rSet.getString("NOME_MARCA_PNEU"));
        return marca;
    }

    private List<Modelo> getModelosPneu(Long codEmpresa, Long codMarcaPneu, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Modelo> modelos = new ArrayList<>();
        try {
            stmt = conn.prepareStatement("SELECT * FROM MODELO_PNEU WHERE COD_EMPRESA = ? " +
                    "AND COD_MARCA = ? ORDER BY NOME ASC");
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codMarcaPneu);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                modelos.add(createModeloPneu(rSet));
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
        return modelos;
    }

    private Modelo createModeloPneu(ResultSet rSet) throws SQLException {
        final ModeloPneu modelo = new ModeloPneu();
        modelo.setCodigo(rSet.getLong("CODIGO"));
        modelo.setNome(rSet.getString("NOME"));
        modelo.setQuantidadeSulcos(rSet.getInt("QT_SULCOS"));
        modelo.setAlturaSulcos(rSet.getDouble("ALTURA_SULCOS"));
        return modelo;
    }

    private void updateTrocaVidaPneu(Pneu pneu, Long codUnidade, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE pneu_valor_vida set cod_modelo_banda = ?, valor = ? WHERE " +
                    "cod_unidade = ? AND cod_pneu = ? " +
                    "AND vida = ?");
            stmt.setLong(1, pneu.getBanda().getModelo().getCodigo());
            stmt.setBigDecimal(2, pneu.getBanda().getValor());
            stmt.setLong(3, codUnidade);
            stmt.setLong(4, pneu.getCodigo());
            stmt.setLong(5, pneu.getVidaAtual());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao atualizar a relação vida x banda");
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private List<Modelo> getModelosBanda(Long codEmpresa, Long codMarcaBanda, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Modelo> modelos = new ArrayList<>();
        try {
            stmt = conn.prepareStatement("SELECT * FROM modelo_banda WHERE cod_marca = ? and cod_empresa = ? ORDER BY" +
                    " nome ASC");
            stmt.setLong(1, codMarcaBanda);
            stmt.setLong(2, codEmpresa);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final ModeloBanda modelo = new ModeloBanda();
                modelo.setCodigo(rSet.getLong("CODIGO"));
                modelo.setNome(rSet.getString("NOME"));
                modelo.setQuantidadeSulcos(rSet.getInt("QT_SULCOS"));
                modelo.setAlturaSulcos(rSet.getDouble("ALTURA_SULCOS"));
                modelos.add(modelo);
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
        return modelos;
    }
}