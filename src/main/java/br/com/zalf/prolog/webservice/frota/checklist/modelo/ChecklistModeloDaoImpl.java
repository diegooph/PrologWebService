package br.com.zalf.prolog.webservice.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.commons.imagens.Galeria;
import br.com.zalf.prolog.webservice.commons.imagens.ImagemProLog;
import br.com.zalf.prolog.webservice.frota.checklist.model.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
                            "  CP.COD_IMAGEM,, " +
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
                            "  JOIN CHECKLIST_GALERIA_IMAGENS CGI " +
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
    public List<ModeloChecklist> getModelosChecklistByCodUnidadeByCodFuncao(Long codUnidade, String codFuncao) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<ModeloChecklist> listModelos = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT DISTINCT CM.NOME AS MODELO, CM.CODIGO AS COD_MODELO "
                    + "FROM CHECKLIST_MODELO_FUNCAO CMF JOIN CHECKLIST_MODELO CM ON CM.COD_UNIDADE = CMF.COD_UNIDADE AND CM.CODIGO = CMF.COD_CHECKLIST_MODELO "
                    + "WHERE CMF.COD_UNIDADE = ? AND CMF.COD_FUNCAO::TEXT LIKE ? AND CM.STATUS_ATIVO = TRUE "
                    + "ORDER BY MODELO");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, codFuncao);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final ModeloChecklist modeloChecklist = new ModeloChecklist();
                modeloChecklist.setCodigo(rSet.getLong("COD_MODELO"));
                modeloChecklist.setNome(rSet.getString("MODELO"));
                listModelos.add(modeloChecklist);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return listModelos;
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
                    + "FROM CHECKLIST_MODELO_FUNCAO CMF JOIN CHECKLIST_MODELO CM ON CM.COD_UNIDADE = CMF.COD_UNIDADE AND CM.CODIGO = CMF.COD_CHECKLIST_MODELO "
                    + "WHERE CMF.COD_UNIDADE = ? AND CM.CODIGO = ? "
                    + "ORDER BY MODELO");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                modeloChecklist = new ModeloChecklist();
                modeloChecklist.setCodigo(rSet.getLong("COD_MODELO"));
                modeloChecklist.setNome(rSet.getString("MODELO"));
                modeloChecklist.setListPerguntas(getPerguntas(codUnidade, codModelo));
                modeloChecklist.setListTipoVeiculo(getTipoVeiculoByCodModeloChecklist(codUnidade, codModelo));
                modeloChecklist.setListFuncao(getFuncaoByCodModelo(codUnidade, codModelo));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return modeloChecklist;
    }

    @Override
    public boolean insertModeloChecklist(ModeloChecklist modeloChecklist) throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO CHECKLIST_MODELO(COD_UNIDADE, NOME, STATUS_ATIVO) VALUES (?,?,?) RETURNING CODIGO");
            stmt.setLong(1, modeloChecklist.getCodUnidade());
            stmt.setString(2, modeloChecklist.getNome());
            stmt.setBoolean(3, true);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                modeloChecklist.setCodigo(rSet.getLong("CODIGO"));
                insertModeloTipoVeiculo(conn, modeloChecklist);
                insertModeloFuncao(conn, modeloChecklist);
                insertModeloPerguntas(conn, modeloChecklist);
                insertModeloAlternativas(conn, modeloChecklist);
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback();
            return false;
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return true;
    }

    @Override
    public boolean setModeloChecklistInativo(Long codUnidade, Long codModelo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE CHECKLIST_MODELO SET STATUS_ATIVO = FALSE WHERE COD_UNIDADE  = ? AND CODIGO = ?");
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
                modelo.setListPerguntas(getPerguntasAlternativasProLog());
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
            stmt = conn.prepareStatement("SELECT DISTINCT CGP.URL_IMAGEM FROM CHECKLIST_MODELO_FUNCAO CMF " +
                    "  JOIN CHECKLIST_PERGUNTAS CP ON CP.COD_UNIDADE = CMF.COD_UNIDADE " +
                    "                                 AND CP.COD_CHECKLIST_MODELO = CMF.COD_CHECKLIST_MODELO " +
                    "  JOIN CHECKLIST_GALERIA_IMAGENS CGP ON CP.COD_IMAGEM = CGP.COD_IMAGEM " +
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
    public Long insertImagem(@NotNull final Long codEmpresa, @NotNull final ImagemProLog imagemProLog) throws SQLException {
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
                return rSet.getLong("CODIGO");
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
                    "  CGI.URL_IMAGEM AS URL_IMAGEM " +
                    "FROM CHECKLIST_PERGUNTAS_PROLOG CPP " +
                    "  JOIN CHECKLIST_ALTERNATIVA_PERGUNTA_PROLOG CAPP " +
                    "    ON CPP.CODIGO = CAPP.COD_PERGUNTA_PROLOG " +
                    "  JOIN CHECKLIST_GALERIA_IMAGENS CGI " +
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
                        "WHERE COD_EMPRESA = ? AND STATUS = TRUE;");
                stmt.setLong(1, codEmpresa);
            } else {
                stmt = conn.prepareStatement("SELECT * FROM CHECKLIST_GALERIA_IMAGENS " +
                        "WHERE COD_EMPRESA IS NULL AND STATUS = TRUE;");
            }
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final ImagemProLog imagemProLog = new ImagemProLog();
                imagemProLog.setCodImagem(rSet.getLong("COD_IMAGEM"));
                imagemProLog.setUrlImagem(rSet.getString("URL_IMAGEM"));
                imagemProLog.setDataHoraCadastro(rSet.getObject("DATA_HORA_CADASTRO", LocalDateTime.class));
                imagemProLog.setStatusImagem(rSet.getBoolean("STATUS"));
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
    private List<PerguntaRespostaChecklist> createPerguntasAlternativas(@NotNull final ResultSet rSet) throws SQLException {
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
        for (TipoVeiculo tipoVeiculo : modeloChecklist.getListTipoVeiculo()) {
            final PreparedStatement stmt = conn.prepareStatement("INSERT INTO CHECKLIST_MODELO_VEICULO_TIPO VALUES (?,?,?)");
            stmt.setLong(1, modeloChecklist.getCodUnidade());
            stmt.setLong(2, modeloChecklist.getCodigo());
            stmt.setLong(3, tipoVeiculo.getCodigo());
            stmt.executeUpdate();
        }
    }

    private void insertModeloFuncao(Connection conn, ModeloChecklist modeloChecklist) throws SQLException {
        for (Cargo cargo : modeloChecklist.getListFuncao()) {
            final PreparedStatement stmt = conn.prepareStatement("INSERT INTO CHECKLIST_MODELO_FUNCAO VALUES (?,?,?)");
            stmt.setLong(1, modeloChecklist.getCodUnidade());
            stmt.setLong(2, modeloChecklist.getCodigo());
            stmt.setLong(3, cargo.getCodigo());
            stmt.executeUpdate();
        }
    }

    private void insertModeloPerguntas(Connection conn, ModeloChecklist modeloChecklist) throws SQLException {
        for (PerguntaRespostaChecklist pergunta : modeloChecklist.getListPerguntas()) {
            final PreparedStatement stmt = conn.prepareStatement("INSERT INTO CHECKLIST_PERGUNTAS ("
                    + "COD_CHECKLIST_MODELO, COD_UNIDADE, ORDEM, PERGUNTA, COD_IMAGEM, "
                    + "STATUS_ATIVO, PRIORIDADE, SINGLE_CHOICE) VALUES (?,?,?,?,?,?,?,?) RETURNING CODIGO");
            stmt.setLong(1, modeloChecklist.getCodigo());
            stmt.setLong(2, modeloChecklist.getCodUnidade());
            stmt.setInt(3, pergunta.getOrdemExibicao());
            stmt.setString(4, pergunta.getPergunta());
            stmt.setLong(5, pergunta.getCodImagem());
            stmt.setBoolean(6, true);
            stmt.setString(7, pergunta.getPrioridade());
            stmt.setBoolean(8, pergunta.isSingleChoice());
            final ResultSet rSet = stmt.executeQuery();
            if (rSet.next()) {
                pergunta.setCodigo(rSet.getLong("CODIGO"));
            }
        }
    }

    private void insertModeloAlternativas(Connection conn, ModeloChecklist modeloChecklist) throws SQLException {
        for (PerguntaRespostaChecklist pergunta : modeloChecklist.getListPerguntas()) {
            for (AlternativaChecklist alternativa : pergunta.getAlternativasResposta()) {
                final PreparedStatement stmt = conn.prepareStatement("INSERT INTO CHECKLIST_ALTERNATIVA_PERGUNTA ( "
                        + "COD_CHECKLIST_MODELO, COD_UNIDADE, COD_PERGUNTA, ALTERNATIVA, ORDEM, "
                        + "STATUS_ATIVO) VALUES (?,?,?,?,?,?) RETURNING CODIGO");
                stmt.setLong(1, modeloChecklist.getCodigo());
                stmt.setLong(2, modeloChecklist.getCodUnidade());
                stmt.setLong(3, pergunta.getCodigo());
                stmt.setString(4, alternativa.alternativa);
                stmt.setInt(5, alternativa.ordemExibicao);
                stmt.setBoolean(6, true);
                stmt.executeQuery();
            }
        }
    }
}