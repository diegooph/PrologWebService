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

    private static final String BUSCA_PNEUS_BY_PLACA = "SELECT po.posicao_prolog AS posicao, "
            + "MP.NOME AS MARCA, MP.CODIGO AS COD_MARCA, P.CODIGO, P.PRESSAO_ATUAL, P.VIDA_ATUAL, P.VIDA_TOTAL, "
            + "MOP.NOME AS MODELO,\n"
            + "  MOP.CODIGO AS COD_MODELO, MOP.QT_SULCOS AS QT_SULCOS_MODELO, PD.CODIGO AS COD_DIMENSAO, PD.ALTURA, PD"
            + ".LARGURA, PD.ARO, P.PRESSAO_RECOMENDADA, P.DOT, "
            + "            P.altura_sulcos_novos,P.altura_sulco_CENTRAL_INTERNO, P.altura_sulco_CENTRAL_EXTERNO, "
            + "P.altura_sulco_INTERNO, P.altura_sulco_EXTERNO, p.status, P.VALOR, \n"
            + "            MB.codigo AS COD_MODELO_BANDA, MB.nome AS NOME_MODELO_BANDA, MB.QT_SULCOS AS "
            + "QT_SULCOS_BANDA, MAB.codigo AS COD_MARCA_BANDA, MAB.nome AS NOME_MARCA_BANDA, \n"
            + "PVV.valor AS VALOR_BANDA\n"
            + "FROM veiculo_pneu vp join pneu_ordem po on vp.posicao = po.posicao_prolog\n"
            + "  JOIN PNEU P ON P.CODIGO = VP.COD_PNEU\n"
            + "  JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO\n"
            + "  JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA\n"
            + "  JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO\n"
            + "  JOIN UNIDADE U ON U.CODIGO = P.cod_unidade\n"
            + "  LEFT JOIN modelo_banda MB ON MB.codigo = P.cod_modelo_banda AND MB.cod_empresa = U.cod_empresa\n"
            + "  LEFT JOIN marca_banda MAB ON MAB.codigo = MB.cod_marca AND MAB.cod_empresa = MB.cod_empresa\n"
            + "  LEFT JOIN pneu_valor_vida PVV ON PVV.cod_unidade = P.cod_unidade AND PVV.cod_pneu = P.codigo AND PVV.vida = P.vida_atual\n"
            + "WHERE vp.placa = ?\n"
            + "ORDER BY po.ordem_exibicao asc";

    private static final String BUSCA_PNEUS_BY_COD_UNIDADE = "SELECT MP.NOME AS MARCA, MP.CODIGO AS COD_MARCA, "
            + "P.CODIGO, P.PRESSAO_ATUAL, P.VALOR, "
            + "MOP.NOME AS MODELO, MOP.CODIGO AS COD_MODELO, MOP.QT_SULCOS AS QT_SULCOS_MODELO, PD.ALTURA, "
            + "PD.LARGURA, P.VIDA_ATUAL, P.VIDA_TOTAL, PD.ARO,PD.CODIGO AS COD_DIMENSAO, P.PRESSAO_RECOMENDADA, "
            + "P.altura_sulcos_novos, "
            + "P.altura_sulco_CENTRAL_interno, P.altura_sulco_central_externo, P.altura_sulco_EXTERNO, "
            + "P.altura_sulco_interno, p.status, P.DOT, "
            + "MB.codigo AS COD_MODELO_BANDA, MB.nome AS NOME_MODELO_BANDA, MB.QT_SULCOS AS QT_SULCOS_BANDA, "
            + "MAB.codigo AS COD_MARCA_BANDA, MAB.nome AS NOME_MARCA_BANDA, "
            + "PVV.valor AS VALOR_BANDA\n"
            + "FROM PNEU P "
            + "JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO "
            + "JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA "
            + "JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO "
            + "JOIN UNIDADE U ON U.CODIGO = P.cod_unidade\n "
            + "LEFT JOIN modelo_banda MB ON MB.codigo = P.cod_modelo_banda AND MB.cod_empresa = U.cod_empresa\n "
            + "LEFT JOIN marca_banda MAB ON MAB.codigo = MB.cod_marca AND MAB.cod_empresa = MB.cod_empresa\n "
            + "LEFT JOIN pneu_valor_vida PVV ON PVV.cod_unidade = P.cod_unidade AND PVV.cod_pneu = P.codigo AND PVV.vida = P.vida_atual "
            + "WHERE P.COD_UNIDADE = ? AND P.STATUS LIKE ? ORDER BY P.CODIGO ASC";
    public static final String TAG = PneuDaoImpl.class.getSimpleName();

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
            stmt = conn.prepareStatement(BUSCA_PNEUS_BY_PLACA);
            stmt.setString(1, placa);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                Pneu pneu = createPneu(rSet);
                pneu.setPosicao(rSet.getInt("POSICAO"));
                listPneu.add(pneu);
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
                    "pressao_atual, altura_sulcos_novos, \n" +
                    "                 altura_sulco_interno, altura_sulco_central_interno, " +
                    "altura_sulco_central_externo, altura_sulco_externo, cod_unidade, status, \n" +
                    "                 vida_atual, vida_total, cod_modelo_banda, dot, valor)\n" +
                    "    VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
            stmt.setString(1, pneu.getCodigo().trim());
            stmt.setLong(2, pneu.getModelo().getCodigo());
            stmt.setLong(3, pneu.getDimensao().codigo);
            stmt.setDouble(4, pneu.getPressaoCorreta());
            // Pressão atual.
            stmt.setDouble(5, 0L);
            stmt.setDouble(6, pneu.getValorSulcoPneuNovo());
            stmt.setDouble(7, pneu.getSulcosAtuais().getInterno());
            stmt.setDouble(8, pneu.getSulcosAtuais().getCentralInterno());
            stmt.setDouble(9, pneu.getSulcosAtuais().getCentralExterno());
            stmt.setDouble(10, pneu.getSulcosAtuais().getExterno());
            stmt.setLong(11, codUnidade);
            stmt.setString(12, pneu.getStatus());
            stmt.setInt(13, pneu.getVidaAtual());
            stmt.setInt(14, pneu.getVidasTotal());
            if (pneu.getVidaAtual() == 1) {
                stmt.setNull(15, Types.BIGINT);
            } else {
                stmt.setLong(15, pneu.getBanda().getModelo().getCodigo());
            }
            stmt.setString(16, pneu.getDot().trim());
            stmt.setBigDecimal(17, pneu.getValor());

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

            // Atualiza a vida e o código do modelo de banda na tabela PNEU.
            stmt = conn.prepareStatement("UPDATE PNEU SET " +
                    "VIDA_ATUAL = ?, VIDA_TOTAL = ?, COD_MODELO_BANDA = ? " +
                    "WHERE CODIGO = ? AND COD_UNIDADE = ?");
            stmt.setInt(1, pneu.getVidaAtual());
            if (pneu.getVidaAtual() > pneu.getVidasTotal()) {
                stmt.setLong(2, pneu.getVidaAtual());
            } else {
                stmt.setLong(2, pneu.getVidasTotal());
            }
            stmt.setLong(3, pneu.getCodModeloBanda());
            stmt.setString(4, pneu.getCodigo());
            stmt.setLong(5, codUnidade);
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
            // Se um pneu tem número ímpar de sulcos, o valor do sulco central deve ser duplicado nos dois campos de
            // de sulco central.
            if (pneu.temQtdImparSulcos()) {
                if (!pneu.getSulcosAtuais().getCentralInterno().equals(pneu.getSulcosAtuais().getCentralExterno())) {
                    throw new IllegalStateException("Um pneu com número ímpar de sulcos deve ter seus sulcos centrais iguais");
                }
            }

            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("UPDATE PNEU SET CODIGO = ?, COD_MODELO = ?, COD_DIMENSAO = ?, "
                    + "PRESSAO_RECOMENDADA = ?, "
                    + "PRESSAO_ATUAL = ?,ALTURA_SULCOS_NOVOS = ?, ALTURA_SULCO_INTERNO = ?, "
                    + "ALTURA_SULCO_CENTRAL_INTERNO = ?, ALTURA_SULCO_CENTRAL_EXTERNO = ?, "
                    + "ALTURA_SULCO_EXTERNO = ?, VIDA_TOTAL = ?, COD_MODELO_BANDA = ?, DOT = ?, VALOR = ? "
                    + "WHERE CODIGO = ? AND COD_UNIDADE = ? AND VIDA_ATUAL = ? AND STATUS = ?");
            stmt.setString(1, pneu.getCodigo().trim());
            stmt.setLong(2, pneu.getModelo().getCodigo());
            stmt.setLong(3, pneu.getDimensao().codigo);
            stmt.setDouble(4, pneu.getPressaoCorreta());
            stmt.setDouble(5, pneu.getPressaoAtual());
            stmt.setDouble(6, pneu.getSulcosPneuNovo().getCentralInterno());
            stmt.setDouble(7, pneu.getSulcosAtuais().getInterno());
            stmt.setDouble(8, pneu.getSulcosAtuais().getCentralInterno());
            stmt.setDouble(9, pneu.getSulcosAtuais().getCentralExterno());
            stmt.setDouble(10, pneu.getSulcosAtuais().getExterno());
            stmt.setInt(11, pneu.getVidasTotal());
            if (pneu.getBanda() != null) {
                stmt.setLong(12, pneu.getBanda().getModelo().getCodigo());
                updateTrocaVidaPneu(pneu, codUnidade, conn);
            } else {
                stmt.setNull(12, Types.BIGINT);
            }
            stmt.setString(13, pneu.getDot());
            stmt.setBigDecimal(14, pneu.getValor());
            stmt.setString(15, codOriginal);
            stmt.setLong(16, codUnidade);
            stmt.setInt(17, pneu.getVidaAtual());
            stmt.setString(18, pneu.getStatus());

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
            stmt = conn.prepareStatement(BUSCA_PNEUS_BY_COD_UNIDADE);
            stmt.setLong(1, codUnidade);
            stmt.setString(2, status);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                Pneu pneu = createPneu(rSet);
                pneus.add(pneu);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return pneus;
    }

    @Override
    public Pneu createPneu(ResultSet rSet) throws SQLException {
        final Pneu pneu = new Pneu();

        pneu.setCodigo(rSet.getString("CODIGO"));
        pneu.setDot(rSet.getString("DOT"));
        pneu.setValor(rSet.getBigDecimal("VALOR"));

        final Marca marcaPneu = new Marca();
        marcaPneu.setCodigo(rSet.getLong("COD_MARCA"));
        marcaPneu.setNome(rSet.getString("MARCA"));
        pneu.setMarca(marcaPneu);

        final ModeloPneu modeloPneu = new ModeloPneu();
        modeloPneu.setCodigo(rSet.getLong("COD_MODELO"));
        modeloPneu.setNome(rSet.getString("MODELO"));
        modeloPneu.setQuantidadeSulcos(rSet.getInt("QT_SULCOS_MODELO"));
        pneu.setModelo(modeloPneu);

        final Banda banda = new Banda();
        final ModeloBanda modeloBanda = new ModeloBanda();
        if (rSet.getString("COD_MARCA_BANDA") != null) {
            final Marca marcaBanda = new Marca();
            marcaBanda.setCodigo(rSet.getLong("COD_MARCA_BANDA"));
            marcaBanda.setNome(rSet.getString("NOME_MARCA_BANDA"));
            modeloBanda.setCodigo(rSet.getLong("COD_MODELO_BANDA"));
            modeloBanda.setNome(rSet.getString("NOME_MODELO_BANDA"));
            modeloBanda.setQuantidadeSulcos(rSet.getInt("QT_SULCOS_BANDA"));
            banda.setValor(rSet.getBigDecimal("VALOR_BANDA"));
            banda.setMarca(marcaBanda);
            banda.setModelo(modeloBanda);
        } else {
            banda.setMarca(marcaPneu);
            modeloBanda.setQuantidadeSulcos(modeloPneu.getQuantidadeSulcos());
            modeloBanda.setCodigo(modeloPneu.getCodigo());
            modeloBanda.setNome(modeloPneu.getNome());
            banda.setModelo(modeloBanda);
        }
        pneu.setBanda(banda);

        final Dimensao dimensao = new Dimensao();
        dimensao.codigo = rSet.getLong("COD_DIMENSAO");
        dimensao.altura = rSet.getInt("ALTURA");
        dimensao.aro = rSet.getInt("ARO");
        dimensao.largura = rSet.getInt("LARGURA");
        pneu.setDimensao(dimensao);

        final Sulcos sulcoAtual = new Sulcos();
        sulcoAtual.setCentralInterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_INTERNO"));
        sulcoAtual.setCentralExterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_EXTERNO"));
        sulcoAtual.setExterno(rSet.getDouble("ALTURA_SULCO_EXTERNO"));
        sulcoAtual.setInterno(rSet.getDouble("ALTURA_SULCO_INTERNO"));
        pneu.setSulcosAtuais(sulcoAtual);

        final Sulcos sulcoNovo = new Sulcos();
        sulcoNovo.setCentralExterno(rSet.getDouble("ALTURA_SULCOS_NOVOS"));
        sulcoNovo.setCentralInterno(rSet.getDouble("ALTURA_SULCOS_NOVOS"));
        sulcoNovo.setExterno(rSet.getDouble("ALTURA_SULCOS_NOVOS"));
        sulcoNovo.setInterno(rSet.getDouble("ALTURA_SULCOS_NOVOS"));
        pneu.setSulcosPneuNovo(sulcoNovo);

        pneu.setPressaoCorreta(rSet.getDouble("PRESSAO_RECOMENDADA"));
        pneu.setPressaoAtual(rSet.getDouble("PRESSAO_ATUAL"));
        pneu.setStatus(rSet.getString("STATUS"));
        pneu.setVidaAtual(rSet.getInt("VIDA_ATUAL"));
        pneu.setVidasTotal(rSet.getInt("VIDA_TOTAL"));
        return pneu;
    }

    @Override
    public List<Marca> getMarcaModeloPneuByCodEmpresa(Long codEmpresa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Marca> marcas = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT DISTINCT\n" +
                    "  MA.NOME   AS NOME,\n" +
                    "  MA.CODIGO AS COD_MARCA\n" +
                    "FROM MARCA_PNEU MA LEFT OUTER JOIN MODELO_PNEU MP ON MA.CODIGO = MP.COD_MARCA AND MP.COD_EMPRESA" +
                    " = ?\n" +
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
        List<Dimensao> dimensoes = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM DIMENSAO_PNEU");
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                Dimensao dimensao = new Dimensao();
                dimensao.codigo = rSet.getLong("CODIGO");
                dimensao.altura = rSet.getInt("ALTURA");
                dimensao.aro = rSet.getInt("ARO");
                dimensao.largura = rSet.getInt("LARGURA");
                dimensoes.add(dimensao);
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return dimensoes;
    }

    @Override
    public boolean insertModeloPneu(Modelo modelo, long codEmpresa, long codMarca) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO MODELO_PNEU(NOME, COD_MARCA, COD_EMPRESA) VALUES (?,?,?)");
            stmt.setString(1, modelo.getNome());
            stmt.setLong(2, codMarca);
            stmt.setLong(3, codEmpresa);
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao cadastrar modelo de pneu");
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
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
            stmt = conn.prepareStatement("SELECT\n" +
                    "  MP.NOME                                    AS MARCA,\n" +
                    "  MP.CODIGO                                  AS COD_MARCA,\n" +
                    "  P.CODIGO,\n" +
                    "  P.PRESSAO_ATUAL,\n" +
                    "  P.VIDA_ATUAL,\n" +
                    "  P.VIDA_TOTAL,\n" +
                    "  MOP.NOME                                   AS MODELO,\n" +
                    "  MOP.CODIGO                                 AS COD_MODELO,\n" +
                    "  MOP.QT_SULCOS                              AS QT_SULCOS_MODELO,\n" +
                    "  PD.ALTURA,\n" +
                    "  PD.LARGURA,\n" +
                    "  PD.ARO,\n" +
                    "  PD.CODIGO                                  AS COD_DIMENSAO,\n" +
                    "  P.PRESSAO_RECOMENDADA,\n" +
                    "  P.altura_sulcos_novos,\n" +
                    "  P.altura_sulco_CENTRAL_INTERNO,\n" +
                    "  P.altura_sulco_CENTRAL_EXTERNO,\n" +
                    "  P.altura_sulco_INTERNO,\n" +
                    "  P.altura_sulco_EXTERNO,\n" +
                    "  p.status,\n" +
                    "  p.dot,\n" +
                    "  p.valor,\n" +
                    "  MB.codigo                                  AS COD_MODELO_BANDA,\n" +
                    "  MB.nome                                    AS NOME_MODELO_BANDA,\n" +
                    "  MB.QT_SULCOS                               AS QT_SULCOS_BANDA,\n" +
                    "  MAB.codigo                                 AS COD_MARCA_BANDA,\n" +
                    "  MAB.nome                                   AS NOME_MARCA_BANDA,\n" +
                    "  PVV.valor                                   AS VALOR_BANDA\n" +
                    "FROM PNEU P\n" +
                    "JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO\n" +
                    "JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA\n" +
                    "JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO\n" +
                    "JOIN UNIDADE U ON U.CODIGO = P.cod_unidade\n" +
                    "LEFT JOIN modelo_banda MB ON MB.codigo = P.cod_modelo_banda AND MB.cod_empresa = U.cod_empresa\n" +
                    "LEFT JOIN marca_banda MAB ON MAB.codigo = MB.cod_marca AND MAB.cod_empresa = MB.cod_empresa\n" +
                    "LEFT JOIN pneu_valor_vida PVV ON PVV.cod_unidade = P.cod_unidade AND PVV.cod_pneu = P.codigo AND PVV.vida = P.vida_atual " +
                    "WHERE P.CODIGO = ? AND P.cod_unidade = ?");
            stmt.setString(1, codPneu);
            stmt.setLong(2, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createPneu(rSet);
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
                Marca marca = new Marca();
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
            stmt.setString(1, marca.getNome().trim().toLowerCase().replaceAll("\\s+", " "));
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
            closeConnection(null, stmt, rSet);
        }
    }

    @Override
    public Long insertModeloBanda(ModeloBanda modelo, Long codMarcaBanda, Long codEmpresa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO modelo_banda\n" +
                    "    (nome, cod_marca, cod_empresa, qt_sulcos)\n" +
                    "SELECT ?, ?, ?, ?\n" +
                    "WHERE\n" +
                    "    NOT EXISTS (\n" +
                    "        SELECT nome FROM modelo_banda WHERE lower(nome) = lower(?) and cod_marca = ? and " +
                    "cod_empresa = ?\n" +
                    "    ) RETURNING codigo;");
            stmt.setString(1, modelo.getNome().trim().toLowerCase().replaceAll("\\s+", " "));
            stmt.setLong(2, codMarcaBanda);
            stmt.setLong(3, codEmpresa);
            stmt.setInt(4, modelo.getQuantidadeSulcos());
            stmt.setString(5, modelo.getNome().trim().toLowerCase().replaceAll("\\s+", " "));
            stmt.setLong(6, codMarcaBanda);
            stmt.setLong(7, codEmpresa);
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
            stmt.setBigDecimal(5, pneu.getBanda().getValor());
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
        Marca marca = new Marca();
        marca.setCodigo(rSet.getLong("COD_MARCA"));
        marca.setNome(rSet.getString("NOME"));
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
        ModeloPneu modelo = new ModeloPneu();
        modelo.setCodigo(rSet.getLong("CODIGO"));
        modelo.setNome(rSet.getString("NOME"));
        modelo.setQuantidadeSulcos(rSet.getInt("QT_SULCOS"));
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
        List<Modelo> modelos = new ArrayList<>();
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM modelo_banda WHERE cod_marca = ? and cod_empresa = ? ORDER BY" +
                    " nome ASC");
            stmt.setLong(1, codMarcaBanda);
            stmt.setLong(2, codEmpresa);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                ModeloBanda modelo = new ModeloBanda();
                modelo.setCodigo(rSet.getLong("CODIGO"));
                modelo.setNome(rSet.getString("NOME"));
                modelo.setQuantidadeSulcos(rSet.getInt("QT_SULCOS"));
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
        PreparedStatement stmt = null;
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
            closeConnection(conn, stmt, null);
        }
    }
}