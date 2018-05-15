package br.com.zalf.prolog.webservice.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.imagens.Galeria;
import br.com.zalf.prolog.webservice.commons.imagens.ImagemProLog;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtil;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.model.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.ModeloChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.model.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChecklistModeloDaoImpl extends DatabaseConnection implements ChecklistModeloDao {

    @Override
    public List<PerguntaRespostaChecklist> getPerguntas(Long codUnidade, Long codModelo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT CP.CODIGO AS COD_PERGUNTA, " +
                            "  CP.PRIORIDADE, " +
                            "  CGI.COD_IMAGEM, " +
                            "  CGI.URL_IMAGEM, " +
                            "  CP.PERGUNTA, " +
                            "  CP.ORDEM AS ORDEM_PERGUNTA, " +
                            "  CP.SINGLE_CHOICE, " +
                            "  CAP.CODIGO AS COD_ALTERNATIVA, " +
                            "  CAP.ALTERNATIVA, " +
                            "  CAP.ORDEM AS ORDEM_ALTERNATIVA " +
                            "FROM CHECKLIST_PERGUNTAS CP " +
                            "  JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP " +
                            "    ON CP.CODIGO = CAP.COD_PERGUNTA " +
                            "       AND CAP.COD_UNIDADE = CP.COD_UNIDADE " +
                            "       AND CAP.COD_CHECKLIST_MODELO = CP.COD_CHECKLIST_MODELO " +
                            "  LEFT JOIN CHECKLIST_GALERIA_IMAGENS CGI " +
                            "    ON CGI.COD_IMAGEM = CP.COD_IMAGEM " +
                            "WHERE CP.COD_UNIDADE = ? AND CP.COD_CHECKLIST_MODELO = ? AND CP.STATUS_ATIVO = TRUE " +
                            "ORDER BY CP.ORDEM, Cp.PERGUNTA, CAP.ORDEM;",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            rSet = stmt.executeQuery();
            return createPerguntasAlternativas(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public List<ModeloChecklistListagem> getModelosChecklistListagemByCodUnidadeByCodFuncao(Long codUnidade, String codFuncao)
            throws SQLException {
        final List<ModeloChecklistListagem> modelosChecklistListagem = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Set<String> setCargos = new HashSet<>();
        Set<String> setTiposVeiculos = new HashSet<>();
        ModeloChecklistListagem modeloChecklistListagem = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from func_checklist_get_listagem_modelos_checklist(?, ?);");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, codFuncao);
            rSet = stmt.executeQuery();
            Long codModeloChecklistAnterior = null;
            while (rSet.next()) {
                final Long codModeloChecklistAtual = rSet.getLong("COD_MODELO");
                if (codModeloChecklistAnterior == null) {
                    codModeloChecklistAnterior = codModeloChecklistAtual;
                }
                if (!codModeloChecklistAtual.equals(codModeloChecklistAnterior)) {
                    modelosChecklistListagem.add(modeloChecklistListagem);
                    codModeloChecklistAnterior = codModeloChecklistAtual;
                    setCargos = new HashSet<>();
                    setTiposVeiculos = new HashSet<>();
                }
                setCargos.add(rSet.getString("NOME_CARGO"));
                setTiposVeiculos.add(rSet.getString("TIPO_VEICULO"));
                modeloChecklistListagem = createModeloChecklistListagem(rSet, codModeloChecklistAnterior, setCargos, setTiposVeiculos);
            }
            if (codModeloChecklistAnterior != null) {
                modelosChecklistListagem.add(modeloChecklistListagem);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return modelosChecklistListagem;
    }

    @Override
    public ModeloChecklist getModeloChecklist(Long codModelo, Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        ModeloChecklist modeloChecklist = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT DISTINCT CM.NOME AS MODELO, CM.CODIGO AS COD_MODELO "
                    + "FROM CHECKLIST_MODELO_FUNCAO CMF JOIN CHECKLIST_MODELO CM ON CM.COD_UNIDADE = CMF.COD_UNIDADE " +
                    "AND CM.CODIGO = CMF.COD_CHECKLIST_MODELO "
                    + "WHERE CMF.COD_UNIDADE = ? AND CM.CODIGO = ? "
                    + "ORDER BY MODELO");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                modeloChecklist = new ModeloChecklist();
                modeloChecklist.setCodigo(rSet.getLong("COD_MODELO"));
                modeloChecklist.setNome(rSet.getString("MODELO"));
                modeloChecklist.setPerguntas(getPerguntas(codUnidade, codModelo));
                modeloChecklist.setTiposVeiculoLiberados(getTipoVeiculoByCodModeloChecklist(codUnidade, codModelo));
                modeloChecklist.setCargosLiberados(getFuncaoByCodModelo(codUnidade, codModelo));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return modeloChecklist;
    }

    @Override
    public void updateModeloChecklist(@NotNull final String token,
                                      @NotNull final Long codUnidade,
                                      @NotNull final Long codModelo,
                                      @NotNull final ModeloChecklist modeloChecklist) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            atualizaModeloChecklist(conn, codUnidade, codModelo, modeloChecklist);
            // Nenhuma pergunta será enviada ao Servidor caso não tenham sofrido nenhuma edição.
            if (modeloChecklist.getPerguntas() != null && modeloChecklist.getPerguntas().size() > 0) {
                atualizaPerguntasModeloChecklist(conn, codUnidade, codModelo, modeloChecklist);
            }
        } finally {
            closeConnection(conn);
        }
    }

    @Override
    public void insertModeloChecklist(ModeloChecklist modeloChecklist) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            System.out.println(GsonUtils.getGson().toJson(modeloChecklist));
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO CHECKLIST_MODELO(COD_UNIDADE, NOME, STATUS_ATIVO) VALUES (?,?," +
                    "?) RETURNING CODIGO");
            stmt.setLong(1, modeloChecklist.getCodUnidade());
            stmt.setString(2, modeloChecklist.getNome());
            stmt.setBoolean(3, true);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                modeloChecklist.setCodigo(rSet.getLong("CODIGO"));
                insertModeloTipoVeiculo(conn, modeloChecklist);
                insertModeloFuncao(conn, modeloChecklist);
                insertModeloPerguntas(conn, modeloChecklist);
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public boolean setModeloChecklistInativo(Long codUnidade, Long codModelo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE CHECKLIST_MODELO SET STATUS_ATIVO = FALSE WHERE COD_UNIDADE  = ? AND" +
                    " CODIGO = ?");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            int count = stmt.executeUpdate();
            if (count == 0) {
                return false;
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    @NotNull
    @Override
    public List<ModeloChecklist> getModelosChecklistProLog() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("SELECT " +
                    "  CMP.CODIGO, " +
                    "  CMP.NOME " +
                    "FROM CHECKLIST_MODELO_PROLOG CMP " +
                    "WHERE CMP.STATUS_ATIVO = TRUE;");
            rSet = stmt.executeQuery();
            final List<ModeloChecklist> modelos = new ArrayList<>();
            while (rSet.next()) {
                final ModeloChecklist modelo = new ModeloChecklist();
                modelo.setCodigo(rSet.getLong("CODIGO"));
                modelo.setNome(rSet.getString("NOME"));
                modelo.setPerguntas(getPerguntasAlternativasProLog());
                modelos.add(modelo);
            }
            return modelos;
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public List<String> getUrlImagensPerguntas(final Long codUnidade, final Long codFuncao) throws SQLException {
        final List<String> listUrl = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT DISTINCT CGI.URL_IMAGEM FROM CHECKLIST_MODELO_FUNCAO CMF " +
                    "  JOIN CHECKLIST_PERGUNTAS CP ON CP.COD_UNIDADE = CMF.COD_UNIDADE " +
                    "                                 AND CP.COD_CHECKLIST_MODELO = CMF.COD_CHECKLIST_MODELO " +
                    "  JOIN CHECKLIST_GALERIA_IMAGENS CGI ON CP.COD_IMAGEM = CGI.COD_IMAGEM " +
                    "WHERE CMF.COD_UNIDADE = ? " +
                    "      AND CMF.COD_FUNCAO = ? " +
                    "      AND CP.STATUS_ATIVO = TRUE;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codFuncao);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                listUrl.add(rSet.getString("URL_IMAGEM"));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return listUrl;
    }

    @Override
    public Galeria getGaleriaImagensPublicas() throws SQLException {
        return getGaleria(null);
    }

    @Override
    public Galeria getGaleriaImagensEmpresa(@NotNull final Long codEmpresa) throws SQLException {
        return getGaleria(codEmpresa);
    }

    @NotNull
    @Override
    public Long insertImagem(@NotNull final Long codEmpresa, @NotNull final ImagemProLog imagemProLog) throws
            SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO CHECKLIST_GALERIA_IMAGENS(URL_IMAGEM, COD_EMPRESA) " +
                    "VALUES (?, ?) RETURNING COD_IMAGEM;");
            stmt.setString(1, imagemProLog.getUrlImagem());
            stmt.setLong(2, codEmpresa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("COD_IMAGEM");
            } else {
                throw new SQLException("Erro ao inserir imagem");
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private void atualizaPerguntasModeloChecklist(@NotNull final Connection conn,
                                                  @NotNull final Long codUnidade,
                                                  @NotNull final Long codModelo,
                                                  @NotNull final ModeloChecklist modeloChecklist) throws SQLException {
        for (final PerguntaRespostaChecklist pergunta : modeloChecklist.getPerguntas()) {
            switch (pergunta.getAcaoEdicao()) {
                case PerguntaRespostaChecklist.CRIADA:
                    // Apenas adicionamos uma nova entrada no banco.
                    // Adicionamos as alternativas também.
                    insertPerguntaAlternativaChecklist(conn, codUnidade, codModelo, pergunta);
                    break;
                case PerguntaRespostaChecklist.ALTERADA_NOME:
                    // Inativa a Pergunta atual.
                    // Cria uma nova entrada no banco de dados e retorna o código.
                    // Insere as alternativas (NÃO DELETADAS) da pergunta no código novo.
                    inativarPerguntaChecklist(conn, codUnidade, codModelo, pergunta);
                    final Long codPergunta = insertApenasPerguntaChecklist(conn, codUnidade, codModelo, pergunta);
                    // Adiciona a alternativa TIPO_OUTROS.
                    pergunta.getAlternativasResposta().add(createAlternativaTipoOutros(pergunta));
                    for (final AlternativaChecklist alternativa : pergunta.getAlternativasResposta()) {
                        if (alternativa.acaoEdicao.equals(AlternativaChecklist.DELETADA)) {
                            // Devemos passar o código da pergunta que foi desativada, para podermos desativar as suas
                            // alternativas também.
                            inativarAlternativaChecklist(conn, codUnidade, codModelo, pergunta.getCodigo(), alternativa);
                        } else {
                            insertAlternativaChecklist(conn, codUnidade, codModelo, codPergunta, alternativa);
                        }
                    }
                    break;
                case PerguntaRespostaChecklist.ALTERADA_INFOS:
                    // Apenas atualizamos a pergunta atual.
                    // Devemos fazer uma verificação em cima das alternativas para tratar
                    // os casos de ALTERACAO/CRIACAO/DELECAO.
                    atualizaPerguntaChecklist(conn, codUnidade, codModelo, pergunta);
                    for (final AlternativaChecklist alternativa : pergunta.getAlternativasResposta()) {
                        if (alternativa.acaoEdicao.equals(AlternativaChecklist.CRIADA)) {
                            insertAlternativaChecklist(conn, codUnidade, codModelo, pergunta.getCodigo(), alternativa);
                        } else if (alternativa.acaoEdicao.equals(AlternativaChecklist.ALTERADA)) {
                            // Devemos passar o código da pergunta que foi desativada, para podermos desativar as suas
                            // alternativas também.
                            inativarAlternativaChecklist(conn, codUnidade, codModelo, pergunta.getCodigo(), alternativa);
                            insertAlternativaChecklist(conn, codUnidade, codModelo, pergunta.getCodigo(), alternativa);
                        } else {
                            // Devemos passar o código da pergunta que foi desativada, para podermos desativar as suas
                            // alternativas também.
                            inativarAlternativaChecklist(conn, codUnidade, codModelo, pergunta.getCodigo(), alternativa);
                        }
                    }
                    break;
                case PerguntaRespostaChecklist.DELETADA:
                    // Vamos inativar a pergunta
                    inativarPerguntaChecklist(conn, codUnidade, codModelo, pergunta);
                    for (final AlternativaChecklist alternativa : pergunta.getAlternativasResposta()) {
                        inativarAlternativaChecklist(conn, codUnidade, codModelo, pergunta.getCodigo(), alternativa);
                    }
                    break;
            }
        }
    }

    private void atualizaPerguntaChecklist(@NotNull final Connection conn,
                                           @NotNull final Long codUnidade,
                                           @NotNull final Long codModelo,
                                           @NotNull final PerguntaRespostaChecklist pergunta) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE CHECKLIST_PERGUNTAS " +
                    "SET ORDEM = ?, PERGUNTA = ?, PRIORIDADE = ?, SINGLE_CHOICE = ?, COD_IMAGEM = ? " +
                    "WHERE COD_UNIDADE = ? AND COD_CHECKLIST_MODELO = ? AND CODIGO = ?;");
            stmt.setInt(1, pergunta.getOrdemExibicao());
            stmt.setString(2, pergunta.getPergunta());
            stmt.setString(3, pergunta.getPrioridade());
            stmt.setBoolean(4, pergunta.isSingleChoice());
            stmt.setLong(5, pergunta.getCodImagem());
            stmt.setLong(6, codUnidade);
            stmt.setLong(7, codModelo);
            stmt.setLong(8, pergunta.getCodigo());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Não foi possível atualizar a pergunta de código: " + pergunta.getCodigo());
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private void insertAlternativaChecklist(@NotNull final Connection conn,
                                            @NotNull final Long codUnidade,
                                            @NotNull final Long codModelo,
                                            @NotNull final Long codPergunta,
                                            @NotNull final AlternativaChecklist alternativa) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO CHECKLIST_ALTERNATIVA_PERGUNTA ( "
                    + "COD_CHECKLIST_MODELO, COD_UNIDADE, COD_PERGUNTA, ALTERNATIVA, ORDEM, "
                    + "STATUS_ATIVO) VALUES (?,?,?,?,?,?);");
            stmt.setLong(1, codModelo);
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codModelo);
            stmt.setString(4, alternativa.alternativa);
            stmt.setInt(5, alternativa.ordemExibicao);
            stmt.setBoolean(6, true);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Não foi possível inserir a alternativa da pergunta de código: " + codPergunta);
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private long insertApenasPerguntaChecklist(@NotNull final Connection conn,
                                               @NotNull final Long codUnidade,
                                               @NotNull final Long codModelo,
                                               @NotNull final PerguntaRespostaChecklist pergunta) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO CHECKLIST_PERGUNTAS ("
                    + "COD_CHECKLIST_MODELO, COD_UNIDADE, ORDEM, PERGUNTA, COD_IMAGEM, "
                    + "STATUS_ATIVO, PRIORIDADE, SINGLE_CHOICE) VALUES (?,?,?,?,?,?,?,?) RETURNING CODIGO");
            stmt.setLong(1, codModelo);
            stmt.setLong(2, codUnidade);
            stmt.setInt(3, pergunta.getOrdemExibicao());
            stmt.setString(4, pergunta.getPergunta());
            if (pergunta.getCodImagem() != null) {
                stmt.setLong(5, pergunta.getCodImagem());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }
            stmt.setBoolean(6, true);
            stmt.setString(7, pergunta.getPrioridade());
            stmt.setBoolean(8, pergunta.isSingleChoice());
            final ResultSet rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Não foi possível inserir a pergunta do checklist");
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private void inativarAlternativaChecklist(@NotNull final Connection conn,
                                              @NotNull final Long codUnidade,
                                              @NotNull final Long codModelo,
                                              @NotNull final Long codPergunta,
                                              @NotNull final AlternativaChecklist alternativa) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE CHECKLIST_ALTERNATIVA_PERGUNTA" +
                    " SET STATUS_ATIVO = FALSE" +
                    " WHERE COD_UNIDADE = ? AND COD_CHECKLIST_MODELO = ? AND COD_PERGUNTA = ? AND CODIGO = ?;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            stmt.setLong(3, codPergunta);
            stmt.setLong(4, alternativa.getCodigo());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Não foi possível inativar a alternativa de código: " + alternativa.getCodigo());
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private void inativarPerguntaChecklist(@NotNull final Connection conn,
                                           @NotNull final Long codUnidade,
                                           @NotNull final Long codModelo,
                                           @NotNull final PerguntaRespostaChecklist pergunta) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE CHECKLIST_PERGUNTAS " +
                    "SET STATUS_ATIVO = FALSE " +
                    "WHERE COD_UNIDADE = ? AND COD_CHECKLIST_MODELO = ? AND CODIGO = ?;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            stmt.setLong(3, pergunta.getCodigo());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Não foi possível inativar a pergunta de código: " + pergunta.getCodigo());
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private void insertPerguntaAlternativaChecklist(@NotNull final Connection conn,
                                                    @NotNull final Long codUnidade,
                                                    @NotNull final Long codModelo,
                                                    @NotNull final PerguntaRespostaChecklist pergunta) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO CHECKLIST_PERGUNTAS ("
                    + "COD_CHECKLIST_MODELO, COD_UNIDADE, ORDEM, PERGUNTA, COD_IMAGEM, "
                    + "STATUS_ATIVO, PRIORIDADE, SINGLE_CHOICE) VALUES (?,?,?,?,?,?,?,?) RETURNING CODIGO");
            stmt.setLong(1, codModelo);
            stmt.setLong(2, codUnidade);
            stmt.setInt(3, pergunta.getOrdemExibicao());
            stmt.setString(4, pergunta.getPergunta());
            if (pergunta.getCodImagem() != null) {
                stmt.setLong(5, pergunta.getCodImagem());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }
            stmt.setBoolean(6, true);
            stmt.setString(7, pergunta.getPrioridade());
            stmt.setBoolean(8, pergunta.isSingleChoice());
            final ResultSet rSet = stmt.executeQuery();
            if (rSet.next()) {
                pergunta.setCodigo(rSet.getLong("CODIGO"));
                // Adiciona a alternativa TIPO_OUTROS.
                pergunta.getAlternativasResposta().add(createAlternativaTipoOutros(pergunta));
                for (final AlternativaChecklist alternativa : pergunta.getAlternativasResposta()) {
                    stmt = conn.prepareStatement("INSERT INTO CHECKLIST_ALTERNATIVA_PERGUNTA ( "
                            + "COD_CHECKLIST_MODELO, COD_UNIDADE, COD_PERGUNTA, ALTERNATIVA, ORDEM, "
                            + "STATUS_ATIVO) VALUES (?,?,?,?,?,?);");
                    stmt.setLong(1, codModelo);
                    stmt.setLong(2, codUnidade);
                    stmt.setLong(3, pergunta.getCodigo());
                    stmt.setString(4, alternativa.alternativa);
                    stmt.setInt(5, alternativa.ordemExibicao);
                    stmt.setBoolean(6, true);
                    stmt.executeUpdate();
                }
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private void atualizaModeloChecklist(@NotNull final Connection conn,
                                         @NotNull final Long codUnidade,
                                         @NotNull final Long codModelo,
                                         @NotNull final ModeloChecklist modeloChecklist) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * " +
                "FROM func_checklist_update_modelo_checklist(?, ?, ?, ?, ?);");
        stmt.setString(1, modeloChecklist.getNome());
        stmt.setLong(2, codUnidade);
        stmt.setLong(3, codModelo);
        stmt.setArray(4,
                PostgresUtil.ListLongToArray(conn, getCodigosCargos(modeloChecklist.getCargosLiberados())));
        stmt.setArray(5,
                PostgresUtil.ListLongToArray(conn, getCodigosTiposVeiculos(modeloChecklist.getTiposVeiculoLiberados())));
        stmt.executeQuery();

        closeStatement(stmt);
    }

    @NotNull
    private List<Long> getCodigosTiposVeiculos(@NotNull final List<TipoVeiculo> tiposVeiculoLiberados) {
        final List<Long> codTiposVeiculo = new ArrayList<>();
        for (final TipoVeiculo tipoVeiculo : tiposVeiculoLiberados) {
            codTiposVeiculo.add(tipoVeiculo.getCodigo());
        }
        return codTiposVeiculo;
    }

    @NotNull
    private List<Long> getCodigosCargos(@NotNull final List<Cargo> cargosLiberados) {
        final List<Long> codCargos = new ArrayList<>();
        for (final Cargo cargo : cargosLiberados) {
            codCargos.add(cargo.getCodigo());
        }
        return codCargos;
    }

    @NotNull
    private ModeloChecklistListagem createModeloChecklistListagem(@NotNull final ResultSet rSet,
                                                                  @NotNull final Long codModeloChecklistAtual,
                                                                  @NotNull final Set<String> setCargos,
                                                                  @NotNull final Set<String> setTiposVeiculos) throws SQLException {
        final ModeloChecklistListagem modeloChecklist = new ModeloChecklistListagem();
        modeloChecklist.setCodigo(codModeloChecklistAtual);
        modeloChecklist.setCodUnidade(rSet.getLong("COD_UNIDADE"));
        modeloChecklist.setNome(rSet.getString("MODELO"));
        modeloChecklist.setCargosLiberados(new ArrayList<>(setCargos));
        modeloChecklist.setTiposVeiculoLiberados(new ArrayList<>(setTiposVeiculos));
        modeloChecklist.setQtdPerguntas(rSet.getInt("TOTAL_PERGUNTAS"));
        return modeloChecklist;
    }

    @NotNull
    private List<PerguntaRespostaChecklist> getPerguntasAlternativasProLog() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("SELECT " +
                    "  CPP.CODIGO AS COD_PERGUNTA, " +
                    "  CPP.PERGUNTA AS PERGUNTA, " +
                    "  CPP.ORDEM AS ORDEM_PERGUNTA, " +
                    "  CPP.PRIORIDADE AS PRIORIDADE, " +
                    "  CPP.SINGLE_CHOICE AS SINGLE_CHOICE, " +
                    "  CAPP.CODIGO AS COD_ALTERNATIVA, " +
                    "  CAPP.ALTERNATIVA AS ALTERNATIVA, " +
                    "  CAPP.ORDEM AS ORDEM_ALTERNATIVA, " +
                    "  CGI.URL_IMAGEM AS URL_IMAGEM, " +
                    "  CGI.COD_IMAGEM AS COD_IMAGEM " +
                    "FROM CHECKLIST_PERGUNTAS_PROLOG CPP " +
                    "  JOIN CHECKLIST_ALTERNATIVA_PERGUNTA_PROLOG CAPP " +
                    "    ON CPP.CODIGO = CAPP.COD_PERGUNTA_PROLOG " +
                    "  LEFT JOIN CHECKLIST_GALERIA_IMAGENS CGI " +
                    "    ON CGI.COD_IMAGEM = CPP.COD_IMAGEM " +
                    "ORDER BY CPP.ORDEM, CPP.PERGUNTA, CAPP.ORDEM;");
            rSet = stmt.executeQuery();
            return createPerguntasAlternativas(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    private Galeria getGaleria(@Nullable final Long codEmpresa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<ImagemProLog> imagensProLog = new ArrayList<>();
        try {
            conn = getConnection();
            if (codEmpresa != null) {
                stmt = conn.prepareStatement("SELECT * FROM CHECKLIST_GALERIA_IMAGENS " +
                        "WHERE COD_EMPRESA = ? AND STATUS_ATIVO = TRUE;");
                stmt.setLong(1, codEmpresa);
            } else {
                stmt = conn.prepareStatement("SELECT * FROM CHECKLIST_GALERIA_IMAGENS " +
                        "WHERE COD_EMPRESA IS NULL AND STATUS_ATIVO = TRUE;");
            }
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final ImagemProLog imagemProLog = new ImagemProLog();
                imagemProLog.setCodImagem(rSet.getLong("COD_IMAGEM"));
                imagemProLog.setUrlImagem(rSet.getString("URL_IMAGEM"));
                imagemProLog.setDataHoraCadastro(rSet.getObject("DATA_HORA_CADASTRO", LocalDateTime.class));
                imagemProLog.setStatusImagem(rSet.getBoolean("STATUS_ATIVO"));
                imagensProLog.add(imagemProLog);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return new Galeria(imagensProLog);
    }

    @NotNull
    private List<TipoVeiculo> getTipoVeiculoByCodModeloChecklist(Long codUnidade, Long codModelo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<TipoVeiculo> listTipos = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT VT.NOME AS TIPO_VEICULO, VT.CODIGO "
                    + "FROM CHECKLIST_MODELO_VEICULO_TIPO CM "
                    + "JOIN VEICULO_TIPO VT ON CM.COD_UNIDADE = VT.COD_UNIDADE "
                    + "AND CM.COD_TIPO_VEICULO = VT.CODIGO "
                    + "WHERE CM.COD_UNIDADE = ? "
                    + "AND CM.COD_MODELO = ? "
                    + "ORDER BY VT.NOME");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final TipoVeiculo tipo = new TipoVeiculo();
                tipo.setCodigo(rSet.getLong("CODIGO"));
                tipo.setNome(rSet.getString("TIPO_VEICULO"));
                listTipos.add(tipo);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return listTipos;
    }

    @NotNull
    private List<Cargo> getFuncaoByCodModelo(Long codUnidade, Long codModelo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Cargo> listCargo = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT F.CODIGO, F.NOME "
                    + "FROM CHECKLIST_MODELO_FUNCAO CM "
                    + "JOIN FUNCAO F ON F.CODIGO = CM.COD_FUNCAO "
                    + "WHERE CM.COD_UNIDADE = ? "
                    + "AND CM.COD_CHECKLIST_MODELO = ? "
                    + "ORDER BY F.NOME");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final Cargo cargo = new Cargo();
                cargo.setCodigo(rSet.getLong("CODIGO"));
                cargo.setNome(rSet.getString("NOME"));
                listCargo.add(cargo);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return listCargo;
    }

    @NotNull
    private List<PerguntaRespostaChecklist> createPerguntasAlternativas(@NotNull final ResultSet rSet) throws
            SQLException {
        final List<PerguntaRespostaChecklist> perguntas = new ArrayList<>();
        List<AlternativaChecklist> alternativas = new ArrayList<>();
        PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
        AlternativaChecklist alternativa;
        if (rSet.first()) {
            pergunta = createPergunta(rSet);
            alternativa = createAlternativa(rSet);
            alternativas.add(alternativa);
        }
        while (rSet.next()) {
            if (rSet.getLong("COD_PERGUNTA") == pergunta.getCodigo()) {
                alternativa = createAlternativa(rSet);
                alternativas.add(alternativa);
            } else {
                pergunta.setAlternativasResposta(alternativas);
                perguntas.add(pergunta);
                alternativas = new ArrayList<>();
                pergunta = createPergunta(rSet);
                alternativa = createAlternativa(rSet);
                alternativas.add(alternativa);
            }
        }
        pergunta.setAlternativasResposta(alternativas);
        perguntas.add(pergunta);
        return perguntas;
    }

    @NotNull
    private PerguntaRespostaChecklist createPergunta(ResultSet rSet) throws SQLException {
        final PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
        pergunta.setCodigo(rSet.getLong("COD_PERGUNTA"));
        pergunta.setOrdemExibicao(rSet.getInt("ORDEM_PERGUNTA"));
        pergunta.setPergunta(rSet.getString("PERGUNTA"));
        pergunta.setSingleChoice(rSet.getBoolean("SINGLE_CHOICE"));
        pergunta.setCodImagem(rSet.getLong("COD_IMAGEM"));
        pergunta.setUrl(rSet.getString("URL_IMAGEM"));
        pergunta.setPrioridade(rSet.getString("PRIORIDADE"));
        return pergunta;
    }

    @NotNull
    private AlternativaChecklist createAlternativa(ResultSet rSet) throws SQLException {
        final AlternativaChecklist alternativa = new AlternativaChecklist();
        alternativa.codigo = rSet.getLong("COD_ALTERNATIVA");
        alternativa.alternativa = rSet.getString("ALTERNATIVA");
        if (alternativa.alternativa.equals("Outros")) {
            alternativa.tipo = AlternativaChecklist.TIPO_OUTROS;
        }
        return alternativa;
    }

    private void insertModeloTipoVeiculo(Connection conn, ModeloChecklist modeloChecklist) throws SQLException {
        PreparedStatement stmt = null;
        try {
            for (final TipoVeiculo tipoVeiculo : modeloChecklist.getTiposVeiculoLiberados()) {
                stmt = conn.prepareStatement("INSERT INTO CHECKLIST_MODELO_VEICULO_TIPO VALUES (?,?,?);");
                stmt.setLong(1, modeloChecklist.getCodUnidade());
                stmt.setLong(2, modeloChecklist.getCodigo());
                stmt.setLong(3, tipoVeiculo.getCodigo());
                stmt.executeUpdate();
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private void insertModeloFuncao(Connection conn, ModeloChecklist modeloChecklist) throws SQLException {
        for (final Cargo cargo : modeloChecklist.getCargosLiberados()) {
            final PreparedStatement stmt = conn.prepareStatement("INSERT INTO CHECKLIST_MODELO_FUNCAO VALUES (?,?,?);");
            stmt.setLong(1, modeloChecklist.getCodUnidade());
            stmt.setLong(2, modeloChecklist.getCodigo());
            stmt.setLong(3, cargo.getCodigo());
            stmt.executeUpdate();
        }
    }

    private void insertModeloPerguntas(Connection conn, ModeloChecklist modeloChecklist) throws SQLException {
        for (final PerguntaRespostaChecklist pergunta : modeloChecklist.getPerguntas()) {
            insertPerguntaAlternativaChecklist(conn, modeloChecklist.getCodUnidade(), modeloChecklist.getCodigo(), pergunta);
        }
    }

    private AlternativaChecklist createAlternativaTipoOutros(@NotNull final PerguntaRespostaChecklist pergunta) {
        final AlternativaChecklist alternativa = new AlternativaChecklist();
        alternativa.setAlternativa("Outros");
        alternativa.setTipo(Alternativa.TIPO_OUTROS);
        // A alterntiva de tipo outros deve sempre ser a última alternativa de uma pergunta.
        alternativa.setOrdemExibicao(pergunta.getAlternativasResposta().size() + 1);
        return alternativa;
    }
}