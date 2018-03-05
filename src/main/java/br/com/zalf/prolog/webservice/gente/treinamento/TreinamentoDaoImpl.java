package br.com.zalf.prolog.webservice.gente.treinamento;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.gente.treinamento.model.Treinamento;
import br.com.zalf.prolog.webservice.gente.treinamento.model.TreinamentoColaborador;

import java.sql.*;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class TreinamentoDaoImpl extends DatabaseConnection implements TreinamentoDao {

    public TreinamentoDaoImpl() {

    }

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
            stmt = conn.prepareStatement("SELECT " +
                    "T.CODIGO AS CODIGO, " +
                    "T.TITULO AS TITULO, " +
                    "T.DESCRICAO AS DESCRICAO, " +
                    "T.URL_ARQUIVO AS URL_ARQUIVO, " +
                    "T.DATA_LIBERACAO AS DATA_LIBERACAO, " +
                    "T.DATA_FECHAMENTO AS DATA_FECHAMENTO, " +
                    "T.DATA_HORA_CADASTRO AT TIME ZONE ? AS DATA_HORA_CADASTRO, " +
                    "T.COD_UNIDADE AS COD_UNIDADE " +
                    "FROM TREINAMENTO T " +
                    "LEFT JOIN RESTRICAO_TREINAMENTO RT ON T.CODIGO = RT.COD_TREINAMENTO " +
                    "WHERE T.COD_UNIDADE = ? " +
                    "AND (? = 1 OR T.DATA_LIBERACAO::DATE <= ?) " +
                    "AND (? = 1 OR RT.COD_FUNCAO::TEXT LIKE ?) " +
                    "AND (? = 1 OR T.DATA_HORA_CADASTRO::DATE >= ?) " +
                    "AND (? = 1 OR T.DATA_HORA_CADASTRO::DATE <= ?) " +
                    "GROUP BY T.CODIGO " +
                    "ORDER BY T.DATA_HORA_CADASTRO " +
                    "LIMIT ? OFFSET ?;");

            stmt.setString(1, TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId());
            stmt.setLong(2, codUnidade);

            if (apenasTreinamentosLiberados) {
                stmt.setInt(3, 0);
                stmt.setObject(4, LocalDate.now(Clock.systemUTC()));
            } else {
                stmt.setInt(3, 1);
                stmt.setNull(4, Types.DATE);
            }

            if (codFuncao == null) {
                stmt.setInt(5, 1);
                stmt.setString(6, "");
            } else {
                stmt.setInt(5, 0);
                stmt.setString(6, String.valueOf(codFuncao));
            }

            if (dataInicial == null || dataFinal == null) {
                stmt.setInt(7, 1);
                stmt.setNull(8, Types.DATE);
                stmt.setInt(9, 1);
                stmt.setNull(10, Types.DATE);
            } else {
                stmt.setInt(7, 0);
                stmt.setDate(8, new java.sql.Date(dataInicial));
                stmt.setInt(9, 0);
                stmt.setDate(10, new java.sql.Date(dataFinal));
            }

            stmt.setLong(11, limit);
            stmt.setLong(12, offset);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final Treinamento t = createTreinamento(rSet);
                // Por default mandamos os cargos com acesso ao treinamento, por isso se for null isso vai ser incluído.
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
    public Treinamento getTreinamentoByCod(Long codTreinamento, Long codUnidade, boolean comCargosLiberados) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "T.CODIGO AS CODIGO, " +
                    "T.TITULO AS TITULO, " +
                    "T.DESCRICAO AS DESCRICAO, " +
                    "T.URL_ARQUIVO AS URL_ARQUIVO, " +
                    "T.DATA_LIBERACAO AS DATA_LIBERACAO, " +
                    "T.DATA_FECHAMENTO AS DATA_FECHAMENTO, " +
                    "T.DATA_HORA_CADASTRO AT TIME ZONE ? AS DATA_HORA_CADASTRO, " +
                    "T.COD_UNIDADE AS COD_UNIDADE " +
                    "FROM TREINAMENTO T " +
                    "LEFT JOIN RESTRICAO_TREINAMENTO RT ON T.CODIGO = RT.COD_TREINAMENTO " +
                    "WHERE T.COD_UNIDADE = ? AND T.CODIGO = ?");

            stmt.setString(1, TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId());
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codTreinamento);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Treinamento treinamento = createTreinamento(rSet);
                if (comCargosLiberados) {
                    treinamento.setCargosLiberados(getFuncoesLiberadasByTreinamento(conn, treinamento.getCodigo()));
                }
                return treinamento;
            } else {
                throw new IllegalArgumentException("Nenhum treinamento encontrado para a unidade: " + codUnidade
                        + " com o código: " + codTreinamento);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public List<Treinamento> getNaoVistosColaborador(Long cpf) throws SQLException {
        List<Treinamento> treinamentos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "T.CODIGO AS CODIGO, " +
                    "T.TITULO AS TITULO, " +
                    "T.DESCRICAO AS DESCRICAO, " +
                    "T.URL_ARQUIVO AS URL_ARQUIVO, " +
                    "T.DATA_LIBERACAO AS DATA_LIBERACAO, " +
                    "T.DATA_FECHAMENTO AS DATA_FECHAMENTO, " +
                    "T.DATA_HORA_CADASTRO AT TIME ZONE ? AS DATA_HORA_CADASTRO, " +
                    "T.COD_UNIDADE AS COD_UNIDADE " +
                    "FROM TREINAMENTO T JOIN "
                    + "RESTRICAO_TREINAMENTO RT ON RT.COD_TREINAMENTO = T.CODIGO "
                    + "JOIN COLABORADOR C ON C.COD_FUNCAO = RT.COD_FUNCAO AND C.COD_UNIDADE = T.cod_unidade AND C.CPF "
                    + "= ? WHERE T.CODIGO NOT IN (SELECT TC.COD_TREINAMENTO FROM COLABORADOR C JOIN "
                    + "TREINAMENTO_COLABORADOR TC ON C.CPF = TC.CPF_COLABORADOR WHERE "
                    + "C.CPF = ?) AND t.data_liberacao <= ? ;");
            stmt.setString(1, TimeZoneManager.getZoneIdForCpf(cpf, conn).getId());
            stmt.setLong(2, cpf);
            stmt.setLong(3, cpf);
            stmt.setObject(4, LocalDate.now(Clock.systemUTC()));
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
        final List<Treinamento> treinamentos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        String treinamentosVistosQuery = "SELECT DISTINCT " +
                "T.CODIGO AS CODIGO, " +
                "T.TITULO AS TITULO, " +
                "T.DESCRICAO AS DESCRICAO, " +
                "T.URL_ARQUIVO AS URL_ARQUIVO, " +
                "T.DATA_LIBERACAO AS DATA_LIBERACAO, " +
                "T.DATA_FECHAMENTO AS DATA_FECHAMENTO, " +
                "T.DATA_HORA_CADASTRO AT TIME ZONE ? AS DATA_HORA_CADASTRO, " +
                "T.COD_UNIDADE AS COD_UNIDADE " +
                "FROM TREINAMENTO T JOIN TREINAMENTO_COLABORADOR TC ON \n" +
                "T.CODIGO = TC.COD_TREINAMENTO WHERE TC.CPF_COLABORADOR = ?";
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(treinamentosVistosQuery);
            stmt.setString(1, TimeZoneManager.getZoneIdForCpf(cpf, conn).getId());
            stmt.setLong(2, cpf);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                treinamentos.add(createTreinamento(rSet));
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
            // A Coluna DATA_VISUALIZACAO é na verdade um TIMESTAMP e deveria se chamar DATA_HORA_VISUALIZACAO. Por
            // ser um TIMESTAMP WITH TIME ZONE utilizamos um OffsetDateTime.
            stmt.setObject(3, OffsetDateTime.now(Clock.systemUTC()));
            if (stmt.executeUpdate() == 0) {
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
        final List<TreinamentoColaborador> colaboradores = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "TC.COD_TREINAMENTO, " +
                    "TC.DATA_VISUALIZACAO AT TIME ZONE ? AS DATA_VISUALIZACAO, " +
                    "C.CPF, " +
                    "C.NOME " +
                    "FROM TREINAMENTO T JOIN RESTRICAO_TREINAMENTO RT ON T.CODIGO = RT.COD_TREINAMENTO " +
                    "LEFT JOIN COLABORADOR C ON C.COD_UNIDADE = T.COD_UNIDADE AND C.COD_FUNCAO = RT.COD_FUNCAO " +
                    "AND C.STATUS_ATIVO = TRUE LEFT JOIN TREINAMENTO_COLABORADOR TC ON TC.COD_TREINAMENTO = T.CODIGO " +
                    "AND TC.CPF_COLABORADOR = C.CPF WHERE T.COD_UNIDADE = ? AND T.CODIGO = ? " +
                    "ORDER BY C.NOME;");
            stmt.setString(1, TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId());
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codTreinamento);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final TreinamentoColaborador tColaborador = new TreinamentoColaborador();
                final Colaborador colaborador = new Colaborador();
                colaborador.setCpf(rSet.getLong("cpf"));
                colaborador.setNome(rSet.getString("nome"));
                tColaborador.setColaborador(colaborador);
                tColaborador.setDataVisualizacao(rSet.getObject("data_visualizacao", LocalDateTime.class));
                colaboradores.add(tColaborador);
            }
            return colaboradores;
        } finally {
            closeConnection(conn, stmt, rSet);
        }
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
                    + "DATA_LIBERACAO, DATA_FECHAMENTO, COD_UNIDADE, DATA_HORA_CADASTRO) "
                    + "VALUES (?,?,?,?,?,?,?) RETURNING CODIGO");
            stmt.setString(1, treinamento.getTitulo());
            stmt.setString(2, treinamento.getDescricao());
            stmt.setString(3, treinamento.getUrlArquivo());
            // Colunas são DATE no banco, não precisamos nos preocupar com time zone.
            stmt.setDate(4, DateUtils.toSqlDate(treinamento.getDataLiberacao()));
            stmt.setDate(5, DateUtils.toSqlDate(treinamento.getDataFechamento()));
            stmt.setLong(6, treinamento.getCodUnidade());
            stmt.setObject(7, OffsetDateTime.now(Clock.systemUTC()));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Long codTreinamento = rSet.getLong("CODIGO");
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
            // deletamos e reinserimos as funções novamente.
            deleteFuncoesLiberadasTreinamento(treinamento.getCodigo(), conn);
            insertFuncoesLiberadasTreinamento(treinamento.getCargosLiberados(), treinamento.getCodigo(), conn);

            stmt = conn.prepareStatement("UPDATE treinamento SET titulo = ?, descricao = ?, data_liberacao = ?, " +
                    "data_fechamento = ? WHERE codigo = ?");
            stmt.setString(1, treinamento.getTitulo());
            stmt.setString(2, treinamento.getDescricao());
            // Colunas são DATE no banco, não precisamos nos preocupar com time zone.
            stmt.setDate(3, DateUtils.toSqlDate(treinamento.getDataLiberacao()));
            stmt.setDate(4, DateUtils.toSqlDate(treinamento.getDataFechamento()));
            stmt.setLong(5, treinamento.getCodigo());
            if (stmt.executeUpdate() > 0) {
                conn.commit();
                return true;
            } else {
                throw new SQLException("Erro ao atualizar treinamento = " + treinamento.getCodigo());
            }
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    @Override
    public boolean updateUrlImagensTreinamento(List<String> urls, Long codTreinamento) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            deleteUrlPaginasTreinamento(conn, codTreinamento);
            insertUrlImagensTreinamento(urls, codTreinamento, conn);
            return true;
        } finally {
            closeConnection(conn, null, null);
        }
    }

    @Override
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
            stmt = conn.prepareStatement("INSERT INTO treinamento_url_paginas(cod_treinamento, url, ordem) VALUES (?,?,?)");
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

    private List<String> getUrlImagensTreinamento(Long codTreinamento) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<String> urls = new ArrayList<>();
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

    private Treinamento createTreinamento(ResultSet rSet) throws SQLException {
        final Treinamento treinamento = new Treinamento();
        treinamento.setCodigo(rSet.getLong("CODIGO"));
        treinamento.setTitulo(rSet.getString("TITULO"));
        treinamento.setDescricao(rSet.getString("DESCRICAO"));
        treinamento.setUrlArquivo(rSet.getString("URL_ARQUIVO"));
        treinamento.setDataLiberacao(rSet.getDate("DATA_LIBERACAO"));
        treinamento.setDataFechamento(rSet.getDate("DATA_FECHAMENTO"));
        treinamento.setDataHoraCadastro(rSet.getObject("DATA_HORA_CADASTRO", LocalDateTime.class));
        treinamento.setCodUnidade(rSet.getLong("COD_UNIDADE"));
        treinamento.setUrlsImagensArquivo(getUrlImagensTreinamento(treinamento.getCodigo()));
        return treinamento;
    }

    private List<Cargo> getFuncoesLiberadasByTreinamento(Connection conn, Long codTreinamento) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Cargo> funcoes = new ArrayList<>();
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
                final Cargo cargo = new Cargo();
                cargo.setCodigo(rSet.getLong("CODIGO"));
                cargo.setNome(rSet.getString("NOME"));
                funcoes.add(cargo);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return funcoes;
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