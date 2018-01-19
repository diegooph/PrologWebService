package br.com.zalf.prolog.webservice.frota.pneu.pneu;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu.Dimensao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Modelo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import com.google.common.base.Preconditions;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PneuDaoImpl extends DatabaseConnection implements PneuDao {
    public static final String TAG = PneuDaoImpl.class.getSimpleName();

    private static final String BASE_QUERY_BUSCA_PNEU = "SELECT " +
            "  MP.NOME                                    AS NOME_MARCA_PNEU, " +
            "  MP.CODIGO                                  AS COD_MARCA_PNEU, " +
            "  P.CODIGO, " +
            "  U.CODIGO                                   AS COD_UNIDADE_ALOCADO, " +
            "  R.CODIGO                                   AS COD_REGIONAL_ALOCADO, " +
            "  P.PRESSAO_ATUAL, " +
            "  P.VIDA_ATUAL, " +
            "  P.VIDA_TOTAL, " +
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
            "LEFT JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO " +
            "LEFT JOIN PNEU_ORDEM PO ON VP.POSICAO = PO.POSICAO_PROLOG " +
            "LEFT JOIN MODELO_BANDA MOB ON MOB.CODIGO = P.COD_MODELO_BANDA AND MOB.COD_EMPRESA = U.COD_EMPRESA " +
            "LEFT JOIN MARCA_BANDA MAB ON MAB.CODIGO = MOB.COD_MARCA AND MAB.COD_EMPRESA = MOB.COD_EMPRESA " +
            "LEFT JOIN PNEU_VALOR_VIDA PVV ON PVV.COD_UNIDADE = P.COD_UNIDADE AND PVV.COD_PNEU = P.CODIGO AND PVV.VIDA = P.VIDA_ATUAL ";

    public PneuDaoImpl() {

    }

    @Override
    public List<Pneu> getPneusByPlaca(String placa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Pneu> listPneu = new ArrayList<>();
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
    public boolean insert(Pneu pneu, Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
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
            stmt = conn.prepareStatement("INSERT INTO pneu(codigo, cod_modelo, cod_dimensao, pressao_recomendada, " +
                    "pressao_atual, altura_sulco_interno, altura_sulco_central_interno, " +
                    "altura_sulco_central_externo, altura_sulco_externo, cod_unidade, status, \n" +
                    "                 vida_atual, vida_total, cod_modelo_banda, dot, valor)\n" +
                    "    VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
            stmt.setString(1, pneu.getCodigo().trim());
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
            stmt.setString(11, pneu.getStatus());
            stmt.setInt(12, pneu.getVidaAtual());
            stmt.setInt(13, pneu.getVidasTotal());
            if (pneu.getVidaAtual() == 1) {
                stmt.setNull(14, Types.BIGINT);
            } else {
                stmt.setLong(14, pneu.getBanda().getModelo().getCodigo());
            }
            stmt.setString(15, pneu.getDot().trim());
            stmt.setBigDecimal(16, pneu.getValor());

            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o pneu");
            }

            // Verifica se precisamos inserir informações de valor da banda para a vida atual.
            if (pneu.getVidaAtual() > 1) {
                insertValorBandaVidaAtual(pneu, codUnidade, conn);
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
    public void trocarVida(Pneu pneu, Long codUnidade, Connection conn) throws SQLException {
        Preconditions.checkArgument(pneu.getVidaAtual() > 1, "Vida atual precisa ser maior que 1");

        PreparedStatement stmt = null;
        try {
            // Insere o valor da nova vida para o pneu.
            insertValorBandaVidaAtual(pneu, codUnidade, conn);

            // Atualiza a vidao, o código do modelo de banda e a altura dos sulcos do pneu.
            // É preciso atualizar os sulcos pois para trocar de vida o pneu foi recapado, logo, ele tem novos sulcos.
            stmt = conn.prepareStatement("UPDATE PNEU SET " +
                    "VIDA_ATUAL = ?, VIDA_TOTAL = ?, COD_MODELO_BANDA = ?, " +
                    "ALTURA_SULCO_INTERNO = ?, ALTURA_SULCO_EXTERNO = ?, " +
                    "ALTURA_SULCO_CENTRAL_INTERNO = ?, ALTURA_SULCO_CENTRAL_EXTERNO = ? "+
                    "WHERE CODIGO = ? AND COD_UNIDADE = ?");
            stmt.setInt(1, pneu.getVidaAtual());
            if (pneu.getVidaAtual() > pneu.getVidasTotal()) {
                stmt.setLong(2, pneu.getVidaAtual());
            } else {
                stmt.setLong(2, pneu.getVidasTotal());
            }
            stmt.setLong(3, pneu.getCodModeloBanda());

            // Atualiza os sulcos do pneu.
            stmt.setDouble(4, pneu.getAlturaSulcoBandaPneu());
            stmt.setDouble(5, pneu.getAlturaSulcoBandaPneu());
            stmt.setDouble(6, pneu.getAlturaSulcoBandaPneu());
            stmt.setDouble(7, pneu.getAlturaSulcoBandaPneu());

            stmt.setString(8, pneu.getCodigo());
            stmt.setLong(9, codUnidade);

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao trocar a vida do pneu " + pneu.getCodigo() + " da unidade " + codUnidade);
            }
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    @Override
    public boolean updateMedicoes(Pneu pneu, Long codUnidade, Connection conn) throws SQLException {
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
            stmt.setString(6, pneu.getCodigo());
            stmt.setLong(7, codUnidade);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao atualizar medições do pneu: " + pneu.getCodigo());
            }
        } finally {
            closeConnection(null, stmt, null);
        }
        return true;
    }

    @Override
    public boolean update(Pneu pneu, Long codUnidade, String codOriginal) throws SQLException {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("UPDATE PNEU SET COD_MODELO = ?, COD_DIMENSAO = ?, "
                    + "COD_MODELO_BANDA = ?, DOT = ?, VALOR = ? "
                    + "WHERE CODIGO = ? AND COD_UNIDADE = ?");
            stmt.setLong(1, pneu.getModelo().getCodigo());
            stmt.setLong(2, pneu.getDimensao().codigo);
            if (pneu.jaFoiRecapado()) {
                stmt.setLong(3, pneu.getBanda().getModelo().getCodigo());
                updateTrocaVidaPneu(pneu, codUnidade, conn);
            } else {
                stmt.setNull(3, Types.BIGINT);
            }
            stmt.setString(4, pneu.getDot());
            stmt.setBigDecimal(5, pneu.getValor());
            stmt.setString(6, codOriginal);
            stmt.setLong(7, codUnidade);

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
    public boolean updatePressao(String codPneu, double pressao, Long codUnidade, Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE PNEU SET "
                + "PRESSAO_ATUAL = ? "
                + "WHERE CODIGO = ? AND COD_UNIDADE = ?");
        stmt.setDouble(1, pressao);
        stmt.setString(2, codPneu);
        stmt.setLong(3, codUnidade);
        if (stmt.executeUpdate() == 0) {
            throw new SQLException("Erro ao atualizar pressão do pneu: " + codPneu);
        }
        return true;
    }

    @Override
    public void updateStatus(Pneu pneu, Long codUnidade, String status, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE PNEU SET "
                    + "STATUS = ? "
                    + "WHERE CODIGO = ? AND COD_UNIDADE = ?");
            stmt.setString(1, status);
            stmt.setString(2, pneu.getCodigo());
            stmt.setLong(3, codUnidade);
            if(stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao atualizar o status do pneu");
            }
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    @Override
    public boolean updateVeiculoPneu(String placa, Pneu pneu, Pneu pneuNovo, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = null;
            Log.d(TAG, pneu.getCodigo() + " " + pneuNovo.getCodigo() + " " + placa);
            stmt = conn.prepareStatement("UPDATE VEICULO_PNEU SET COD_PNEU = ? WHERE PLACA = ? AND COD_PNEU = ?");
            stmt.setString(1, pneuNovo.getCodigo());
            stmt.setString(2, placa);
            stmt.setString(3, pneu.getCodigo());
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao substituir o pneu vinculado a placa");
            }
        } finally {
            closeConnection(null, stmt, null);
        }
        return true;
    }

    @Override
    public List<Pneu> getPneusByCodUnidadeByStatus(Long codUnidade, String status) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Pneu> pneus = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(BASE_QUERY_BUSCA_PNEU +
                    "WHERE P.COD_UNIDADE = ? AND P.STATUS LIKE ? ORDER BY P.CODIGO ASC;");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, status);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                Pneu pneu = PneuConverter.createPneuCompleto(rSet);
                pneus.add(pneu);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return pneus;
    }

    @Override
    public List<Marca> getMarcaModeloPneuByCodEmpresa(Long codEmpresa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Marca> marcas = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT DISTINCT MA.NOME AS NOME_MARCA_PNEU, MA.CODIGO AS COD_MARCA_PNEU " +
                    "FROM MARCA_PNEU MA LEFT OUTER JOIN MODELO_PNEU MP ON MA.CODIGO = MP.COD_MARCA " +
                    "AND MP.COD_EMPRESA = ? " +
                    "ORDER BY MA.NOME ASC");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                Marca marca = createMarcaPneu(rSet);
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
                Dimensao dimensao = new Dimensao();
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
            stmt = conn.prepareStatement("INSERT INTO MODELO_PNEU(NOME, QT_SULCOS, ALTURA_SULCOS, COD_MARCA, " +
                    "COD_EMPRESA) VALUES (?,?,?,?,?) RETURNING CODIGO");
            stmt.setString(1, modelo.getNome());
            stmt.setInt(2, modelo.getQuantidadeSulcos());
            stmt.setDouble(3, modelo.getAlturaSulcos());
            stmt.setLong(4, codMarca);
            stmt.setLong(5, codEmpresa);
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
    public boolean vinculaPneuVeiculo(String placaVeiculo, List<Pneu> pneus) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        long codUnidade;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            for (Pneu pneu : pneus) {
                stmt = conn.prepareStatement("INSERT INTO VEICULO_PNEU VALUES(?,?,(SELECT COD_UNIDADE FROM VEICULO " +
                        "WHERE PLACA = ?),?) RETURNING COD_UNIDADE");
                stmt.setString(1, placaVeiculo);
                stmt.setString(2, pneu.getCodigo());
                stmt.setString(3, placaVeiculo);
                stmt.setInt(4, pneu.getPosicao());
                rSet = stmt.executeQuery();
                if (rSet.next()) {
                    codUnidade = rSet.getLong("COD_UNIDADE");
                } else {
                    throw new SQLException("Erro ao vincular o pneu ao veículo");
                }
                updateStatus(pneu, codUnidade, Pneu.EM_USO, conn);
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback();
            throw e;
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return true;
    }

    @Override
    public void updateSulcos(String codPneu, Sulcos sulcosNovos, Long codUnidade, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE PNEU SET ALTURA_SULCO_INTERNO = ?, ALTURA_SULCO_EXTERNO = ?, "
                    + "ALTURA_SULCO_CENTRAL_INTERNO = ?, ALTURA_SULCO_CENTRAL_EXTERNO = ? "
                    + "WHERE CODIGO = ? AND COD_UNIDADE = ?;");
            stmt.setDouble(1, sulcosNovos.getInterno());
            stmt.setDouble(2, sulcosNovos.getExterno());
            stmt.setDouble(3, sulcosNovos.getCentralInterno());
            stmt.setDouble(4, sulcosNovos.getCentralExterno());
            stmt.setString(5, codPneu);
            stmt.setLong(6, codUnidade);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao atualizar os dados do Pneu");
            }
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    @Override
    public Pneu getPneuByCod(String codPneu, Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(BASE_QUERY_BUSCA_PNEU +
                    "WHERE P.CODIGO = ? AND P.cod_unidade = ?;");
            stmt.setString(1, codPneu);
            stmt.setLong(2, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return PneuConverter.createPneuCompleto(rSet);
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
        List<Marca> marcas = new ArrayList<>();
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
            stmt = conn.prepareStatement("INSERT INTO marca_banda\n" +
                    "    (nome, cod_empresa)\n" +
                    "SELECT ?, ?\n" +
                    "WHERE\n" +
                    "    NOT EXISTS (\n" +
                    "        SELECT nome FROM marca_banda WHERE lower(nome) = lower(?) and cod_empresa = ?\n" +
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

    private void insertValorBandaVidaAtual(Pneu pneu, Long codUnidade, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO pneu_valor_vida(cod_unidade, cod_pneu, cod_modelo_banda, vida, " +
                    "valor)" +
                    "VALUES (?,?,?,?,?) ");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, pneu.getCodigo());
            stmt.setLong(3, pneu.getCodModeloBanda());
            stmt.setBigDecimal(5, pneu.getValorBanda());
            stmt.setInt(4, pneu.getVidaAtual());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir o valor da banda do pneu "
                        + pneu.getCodigo() + " da unidade " + codUnidade);
            }
        } finally {
            closeConnection(null, stmt, null);
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
        List<Modelo> modelos = new ArrayList<>();
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
            stmt.setString(4, pneu.getCodigo());
            stmt.setLong(5, pneu.getVidaAtual());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao atualizar a relação vida x banda");
            }
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private void updatePosicao(String placa, Pneu pneu, int posicao, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("UPDATE VEICULO_PNEU SET POSICAO = ? WHERE PLACA = ? AND COD_PNEU = ?");
            stmt.setInt(1, posicao);
            stmt.setString(2, placa);
            stmt.setString(3, pneu.getCodigo());
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao atualizar a posicao do pneu");
            }
        } finally {
            closeConnection(null, stmt, null);
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

    @Deprecated
    private void updatePosicaoPneuVeiculo(Veiculo veiculo) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            // primeiramente é alterado a posicao de todos os pneus para uma posicao ficticia,
            //após isso é feito o update para inserrir a nova posicao do pneu, esse processo é realizado pois existe
            // a clausula unique(placa, posicao)
            //se fosse realizado direto o update pneu a pneu iria violar o unique.
            for (Pneu pneu : veiculo.getListPneus()) {
                updatePosicao(veiculo.getPlaca(), pneu, pneu.getPosicao() + 5, conn);
            }
            for (Pneu pneu : veiculo.getListPneus()) {
                updatePosicao(veiculo.getPlaca(), pneu, pneu.getPosicao(), conn);
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback();
            throw e;
        } finally {
            closeConnection(conn, null, null);
        }
    }
}