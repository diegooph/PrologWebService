package br.com.zalf.prolog.webservice.gente.treinamento;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.gente.treinamento.model.Treinamento;
import br.com.zalf.prolog.webservice.gente.treinamento.model.TreinamentoColaborador;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TreinamentoDaoImpl extends DatabaseConnection implements TreinamentoDao {

    @Override
    public List<Treinamento> getAll(Long dataInicial,
                                    Long dataFinal,
                                    String codFuncao,
                                    Long codUnidade,
                                    Boolean comCargosLiberados,
                                    boolean apenasTreinamentosLiberados,
                                    long limit,
                                    long offset) throws SQLException {
        final List<Treinamento> treinamentos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT DISTINCT T.* " +
                    "FROM TREINAMENTO T " +
                    "LEFT JOIN RESTRICAO_TREINAMENTO RT ON T.CODIGO = RT.COD_TREINAMENTO " +
                    "WHERE T.COD_UNIDADE = ? " +
                    "AND (? = 1 OR T.DATA_LIBERACAO::DATE <= ?) " +
                    "AND (? = 1 OR RT.COD_FUNCAO::TEXT LIKE ?) " +
                    "AND (? = 1 OR T.DATA_HORA_CADASTRO::DATE >= ?) " +
                    "AND (? = 1 OR T.DATA_HORA_CADASTRO::DATE <= ?) " +
                    "ORDER BY T.DATA_HORA_CADASTRO " +
                    "LIMIT ? OFFSET ?;");
            stmt.setLong(1, codUnidade);

            if (apenasTreinamentosLiberados) {
                stmt.setInt(2, 0);
                stmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            } else {
                stmt.setInt(2, 1);
                stmt.setNull(3, Types.DATE);
            }

            if (codFuncao == null) {
                stmt.setInt(4, 1);
                stmt.setString(5, "");
            } else {
                stmt.setInt(4, 0);
                stmt.setString(5, String.valueOf(codFuncao));
            }

            if (dataInicial == null || dataFinal == null) {
                stmt.setInt(6, 1);
                stmt.setNull(7, Types.DATE);
                stmt.setInt(8, 1);
                stmt.setNull(9, Types.DATE);
            } else {
                stmt.setInt(6, 0);
                stmt.setDate(7, new java.sql.Date(dataInicial));
                stmt.setInt(8, 0);
                stmt.setDate(9, new java.sql.Date(dataFinal));
            }

            stmt.setLong(10, limit);
            stmt.setLong(11, offset);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final Treinamento t = createTreinamento(rSet);
                // Por default mandamos os cargos com acesso ao treinamento, por isso se for null isso vai ser incluido.
                if (comCargosLiberados == null || comCargosLiberados) {
                    t.setCargosLiberados(getFuncoesLiberadasByTreinamento(conn, t.getCodigo()));
                }
                treinamentos.add(t);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return treinamentos;
    }

    @Override
    public List<Treinamento> getNaoVistosColaborador(Long cpf) throws SQLException {
        List<Treinamento> treinamentos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM TREINAMENTO T JOIN "
                    + "RESTRICAO_TREINAMENTO RT ON RT.COD_TREINAMENTO = T.CODIGO "
                    + "JOIN COLABORADOR C ON C.COD_FUNCAO = RT.COD_FUNCAO AND C.COD_UNIDADE = T.cod_unidade AND C.CPF "
                    + "= ? WHERE T.CODIGO NOT IN (SELECT TC.COD_TREINAMENTO FROM COLABORADOR C JOIN "
                    + "TREINAMENTO_COLABORADOR TC ON C.CPF = TC.CPF_COLABORADOR WHERE "
                    + "C.CPF = ?) AND t.data_liberacao <= ? ;");
            stmt.setLong(1, cpf);
            stmt.setLong(2, cpf);
            stmt.setDate(3, DateUtils.toSqlDate(new Date(System.currentTimeMillis())));
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                Treinamento treinamento = createTreinamento(rSet);
                treinamentos.add(treinamento);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return treinamentos;
    }

    @Override
    public List<Treinamento> getVistosColaborador(Long cpf) throws SQLException {
        List<Treinamento> treinamentos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        String treinamentosVistosQuery =
                "SELECT DISTINCT T.CODIGO, T.titulo, T.descricao, T.url_arquivo, T.data_liberacao, T.cod_unidade " +
                        "FROM TREINAMENTO T JOIN TREINAMENTO_COLABORADOR TC ON \n" +
                        "T.CODIGO = TC.COD_TREINAMENTO WHERE TC.CPF_COLABORADOR = ?";
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(treinamentosVistosQuery);
            stmt.setLong(1, cpf);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                Treinamento treinamento = createTreinamento(rSet);
                treinamentos.add(treinamento);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return treinamentos;
    }

    @Override
    public boolean marcarTreinamentoComoVisto(Long codTreinamento, Long cpf) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO TREINAMENTO_COLABORADOR "
                    + "(COD_TREINAMENTO, CPF_COLABORADOR, DATA_VISUALIZACAO) VALUES "
                    + "(?, ?, ?)");
            stmt.setLong(1, codTreinamento);
            stmt.setLong(2, cpf);
            stmt.setTimestamp(3, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao marcar o treinamento como visto");
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    @Override
    public List<TreinamentoColaborador> getVisualizacoesByTreinamento(Long codTreinamento, Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        TreinamentoColaborador tColaborador = null;
        Colaborador colaborador = null;
        List<TreinamentoColaborador> colaboradores = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT TC.cod_treinamento, TC.data_visualizacao, C.cpf, C.nome " +
                    "FROM treinamento T JOIN restricao_treinamento RT ON T.codigo = RT.cod_treinamento\n" +
                    "LEFT JOIN colaborador C ON C.cod_unidade = T.cod_unidade AND C.cod_funcao = RT.cod_funcao " +
                    "AND C.status_ativo = TRUE LEFT JOIN treinamento_colaborador TC ON TC.cod_treinamento = T.codigo " +
                    "AND TC.cpf_colaborador = C.cpf WHERE T.cod_unidade = ? AND T.CODIGO = ? \n" +
                    "ORDER BY C.nome");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codTreinamento);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                tColaborador = new TreinamentoColaborador();
                colaborador = new Colaborador();
                colaborador.setCpf(rSet.getLong("cpf"));
                colaborador.setNome(rSet.getString("nome"));
                tColaborador.setColaborador(colaborador);
                tColaborador.setDataVisualizacao(rSet.getDate("data_visualizacao"));
                colaboradores.add(tColaborador);
            }
            return colaboradores;
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private Cargo createFuncao(ResultSet rSet) throws SQLException {
        Cargo cargo = new Cargo();
        cargo.setCodigo(rSet.getLong("COD_FUNCAO"));
        cargo.setNome(rSet.getString("NOME_FUNCAO"));
        return cargo;
    }

    private Treinamento createTreinamento(ResultSet rSet) throws SQLException {
        Treinamento treinamento = new Treinamento();
        treinamento.setCodigo(rSet.getLong("CODIGO"));
        treinamento.setTitulo(rSet.getString("TITULO"));
        treinamento.setDescricao(rSet.getString("DESCRICAO"));
        treinamento.setUrlArquivo(rSet.getString("URL_ARQUIVO"));
        treinamento.setDataLiberacao(rSet.getDate("DATA_LIBERACAO"));
        treinamento.setDataHoraCadastro(rSet.getDate("DATA_HORA_CADASTRO"));
        treinamento.setCodUnidade(rSet.getLong("COD_UNIDADE"));
        treinamento.setUrlsImagensArquivo(getUrlImagensTreinamento(treinamento.getCodigo()));
        return treinamento;
    }

    @Override
    public Long insert(Treinamento treinamento) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO TREINAMENTO (TITULO, DESCRICAO, URL_ARQUIVO, "
                    + "DATA_LIBERACAO, COD_UNIDADE, data_hora_cadastro) "
                    + "VALUES (?,?,?,?,?,?) RETURNING codigo");
            stmt.setString(1, treinamento.getTitulo());
            stmt.setString(2, treinamento.getDescricao());
            stmt.setString(3, treinamento.getUrlArquivo());
            stmt.setDate(4, DateUtils.toSqlDate(treinamento.getDataLiberacao()));
            stmt.setLong(5, treinamento.getCodUnidade());
            stmt.setTimestamp(6, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                Long codTreinamento = rSet.getLong("CODIGO");
                treinamento.setCodigo(codTreinamento);
                insertFuncoesLiberadasTreinamento(treinamento.getCargosLiberados(), codTreinamento, conn);
                insertUrlImagensTreinamento(treinamento.getUrlsImagensArquivo(), codTreinamento, conn);
                conn.commit();
                return codTreinamento;
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    @Override
    public boolean updateTreinamento(Treinamento treinamento) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // As funções liberadas para ver um treinamento podem ou não ter mudado. Assumimos que tenham mudado e
            // deletamos e reinserimos as funções novamente
            deleteFuncoesLiberadasTreinamento(treinamento.getCodigo(), conn);
            insertFuncoesLiberadasTreinamento(treinamento.getCargosLiberados(), treinamento.getCodigo(), conn);

            stmt = conn.prepareStatement("UPDATE treinamento SET titulo = ?, descricao = ?, data_liberacao = ? WHERE codigo = ?");
            stmt.setString(1, treinamento.getTitulo());
            stmt.setString(2, treinamento.getDescricao());
            stmt.setDate(3, DateUtils.toSqlDate(treinamento.getDataLiberacao()));
            stmt.setLong(4, treinamento.getCodigo());
            if (stmt.executeUpdate() > 0) {
                conn.commit();
                return true;
            } else {
                throw new SQLException("Erro ao atualizar treinamento = " + treinamento.getCodigo());
            }
        } catch (SQLException ex) {
            // Rollback feito, podemos subir a exceção
            conn.rollback();
            throw ex;
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    private void insertFuncoesLiberadasTreinamento(List<Cargo> listCargo, Long codTreinamento, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO RESTRICAO_TREINAMENTO VALUES (?,?)");
            stmt.setLong(1, codTreinamento);
            for (Cargo cargo : listCargo) {
                stmt.setLong(2, cargo.getCodigo());
                int count = stmt.executeUpdate();
                if (count == 0) {
                    throw new SQLException("Erro ao inserir funções que estão liberadas para ver o treinamento = " + codTreinamento);
                }
            }
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private void deleteFuncoesLiberadasTreinamento(Long codTreinamento, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("DELETE FROM RESTRICAO_TREINAMENTO WHERE COD_TREINAMENTO = ?");
            stmt.setLong(1, codTreinamento);
            stmt.executeUpdate();
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private void insertUrlImagensTreinamento(List<String> urls, Long codTreinamento, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO treinamento_url_paginas(cod_treinamento,url, ordem) VALUES (?,?,?)");
            stmt.setLong(1, codTreinamento);
            int count = 0;
            for (String url : urls) {
                stmt.setString(2, url);
                stmt.setInt(3, count);
                if (stmt.executeUpdate() == 0) {
                    throw new SQLException("Erro ao inserir a URL");
                }
                count++;
            }
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    @Override
    public boolean updateUrlImagensTreinamento(List<String> urls, Long codTreinamento) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            deleteUrlPaginasTreinamento(conn, codTreinamento);
            insertUrlImagensTreinamento(urls, codTreinamento, conn);
            return true;
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    public Treinamento getTreinamentoByCod(Long codTreinamento, Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Treinamento treinamento = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM treinamento WHERE CODIGO = ? AND COD_UNIDADE = ?");
            stmt.setLong(1, codTreinamento);
            stmt.setLong(2, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                treinamento = new Treinamento();
                treinamento.setCodigo(rSet.getLong("CODIGO"));
                treinamento.setDataHoraCadastro(rSet.getTimestamp("DATA_HORA_CADASTRO"));
                treinamento.setDescricao(rSet.getString("DESCRICAO"));
                treinamento.setTitulo(rSet.getString("TITULO"));
                treinamento.setDataLiberacao(rSet.getTimestamp("DATA_LIBERACAO"));
                treinamento.setUrlArquivo(rSet.getString("URL_ARQUIVO"));
                treinamento.setUrlsImagensArquivo(getUrlImagensTreinamento(codTreinamento));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return treinamento;
    }

    private List<String> getUrlImagensTreinamento(Long codTreinamento) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<String> urls = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT URL FROM TREINAMENTO_URL_PAGINAS " +
                    "WHERE COD_TREINAMENTO = ? " +
                    "ORDER BY ORDEM");
            stmt.setLong(1, codTreinamento);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                urls.add(rSet.getString("URL"));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return urls;
    }

    private List<Cargo> getFuncoesLiberadasByTreinamento(Connection conn, Long codTreinamento) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Cargo> funcoes = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT DISTINCT f.*\n" +
                    "FROM restricao_treinamento rt\n" +
                    "  JOIN treinamento t on t.codigo = rt.cod_treinamento\n" +
                    "  JOIN unidade u on u.codigo = t.cod_unidade\n" +
                    "  JOIN funcao f on f.codigo = rt.cod_funcao and u.cod_empresa = f.cod_empresa\n" +
                    "WHERE cod_treinamento = ?\n" +
                    "ORDER BY f.nome");
            stmt.setLong(1, codTreinamento);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                Cargo cargo = new Cargo();
                cargo.setCodigo(rSet.getLong("CODIGO"));
                cargo.setNome(rSet.getString("NOME"));
                funcoes.add(cargo);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return funcoes;
    }

    public boolean deleteTreinamento(Long codTreinamento) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            deleteFuncoesLiberadasTreinamento(codTreinamento, conn);
            deleteVisualizacoesTreinamento(conn, codTreinamento);
            deleteUrlPaginasTreinamento(conn, codTreinamento);
            stmt = conn.prepareStatement("DELETE FROM TREINAMENTO WHERE CODIGO = ?");
            stmt.setLong(1, codTreinamento);
            if (stmt.executeUpdate() > 0) {
                conn.commit();
                return true;
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            closeConnection(conn, stmt, null);
        }
        return false;
    }

    private void deleteVisualizacoesTreinamento(Connection conn, Long codTreinamento) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("DELETE FROM treinamento_colaborador WHERE cod_treinamento = ?");
            stmt.setLong(1, codTreinamento);
            stmt.executeUpdate();
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private void deleteUrlPaginasTreinamento(Connection conn, Long codTreinamento) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("DELETE FROM treinamento_url_paginas WHERE cod_treinamento = ?");
            stmt.setLong(1, codTreinamento);
            stmt.executeUpdate();
        } finally {
            closeConnection(null, stmt, null);
        }
    }
}
