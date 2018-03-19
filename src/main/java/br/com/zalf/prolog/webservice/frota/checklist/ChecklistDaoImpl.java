package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.Now;
import br.com.zalf.prolog.webservice.frota.checklist.model.*;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloDao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.ItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.OrdemServicoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.Date;

public class ChecklistDaoImpl extends DatabaseConnection implements ChecklistDao {

    public ChecklistDaoImpl() {
    }

    @Override
    public Long insert(Checklist checklist) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
        final OrdemServicoDao osDao = Injection.provideOrdemServicoDao();
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO CHECKLIST "
                    + "(COD_UNIDADE,COD_CHECKLIST_MODELO, DATA_HORA, CPF_COLABORADOR, PLACA_VEICULO, TIPO, " +
                    "KM_VEICULO, TEMPO_REALIZACAO) "
                    + "VALUES ((SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?),?,?,?,?,?,?,?) RETURNING CODIGO, " +
					"COD_UNIDADE");
            stmt.setString(1, checklist.getPlacaVeiculo());
            stmt.setLong(2, checklist.getCodModelo());
            stmt.setObject(3, checklist.getData().atOffset(ZoneOffset.UTC));
            stmt.setLong(4, checklist.getColaborador().getCpf());
            stmt.setString(5, checklist.getPlacaVeiculo());
            stmt.setString(6, String.valueOf(checklist.getTipo()));
            stmt.setLong(7, checklist.getKmAtualVeiculo());
            stmt.setLong(8, checklist.getTempoRealizacaoCheckInMillis());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                checklist.setCodigo(rSet.getLong("CODIGO"));
                final Long codUnidade = rSet.getLong("cod_unidade");
                insertRespostas(checklist, conn);
                osDao.insertItemOs(checklist, conn, codUnidade);
                veiculoDao.updateKmByPlaca(checklist.getPlacaVeiculo(), checklist.getKmAtualVeiculo(), conn);
                conn.commit();
                return checklist.getCodigo();
            } else {
                throw new SQLException("Erro ao inserir o checklist");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Checklist getByCod(Long codChecklist, String userToken) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT C.CODIGO, C.COD_CHECKLIST_MODELO, C.DATA_HORA AT TIME ZONE ? AS DATA_HORA, C" +
					".KM_VEICULO, "
                    + "C.TEMPO_REALIZACAO,C.CPF_COLABORADOR, C.PLACA_VEICULO, "
                    + "C.TIPO, CO.NOME FROM CHECKLIST C JOIN COLABORADOR CO ON CO.CPF = C.CPF_COLABORADOR "
                    + "WHERE C.CODIGO =  ? ");
            stmt.setString(1, TimeZoneManager.getZoneIdForToken(userToken, conn).getId());
            stmt.setLong(2, codChecklist);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createChecklist(rSet, false);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    @Nonnull
    @Override
    public List<Checklist> getAll(@Nonnull Long codUnidade,
                                  @Nullable Long codEquipe,
                                  @Nullable Long codTipoVeiculo,
                                  @Nullable String placaVeiculo,
                                  long dataInicial,
                                  long dataFinal,
                                  int limit,
                                  long offset,
                                  boolean resumido) throws SQLException {
        final List<Checklist> checklists = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT C.CODIGO, C.DATA_HORA AT TIME ZONE ? AS DATA_HORA, "
                    + "C.cod_checklist_modelo, C.KM_VEICULO, "
                    + "C.TEMPO_REALIZACAO,C.CPF_COLABORADOR, C.PLACA_VEICULO, "
                    + "C.TIPO, CO.NOME FROM CHECKLIST C "
                    + "JOIN COLABORADOR CO ON CO.CPF = C.CPF_COLABORADOR "
                    + "JOIN EQUIPE E ON E.CODIGO = CO.COD_EQUIPE "
                    + "JOIN VEICULO V ON V.PLACA = C.PLACA_VEICULO "
                    + "WHERE (C.DATA_HORA AT TIME ZONE ?)::DATE >= ? "
                    + "AND (C.DATA_HORA AT TIME ZONE ?)::DATE <= ? "
                    + "AND C.COD_UNIDADE = ? "
                    + "AND (? = 1 OR E.CODIGO = ?) "
                    + "AND (? = 1 OR V.COD_TIPO = ?) "
                    + "AND (? = 1 OR C.PLACA_VEICULO = ?)"
                    + "ORDER BY DATA_HORA DESC "
                    + "LIMIT ? OFFSET ?");
            final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId();
            stmt.setString(1, zoneId);
            stmt.setString(2, zoneId);
            stmt.setDate(3, new java.sql.Date(dataInicial));
            stmt.setString(4, zoneId);
            stmt.setDate(5, new java.sql.Date(dataFinal));
            stmt.setLong(6, codUnidade);
            if (codEquipe == null) {
                stmt.setInt(7, 1);
                stmt.setInt(8, 1);
            } else {
                stmt.setInt(7, 0);
                stmt.setLong(8, codEquipe);
            }

            if (codTipoVeiculo == null) {
                stmt.setInt(9, 1);
                stmt.setInt(10, 1);
            } else {
                stmt.setInt(9, 0);
                stmt.setLong(10, codTipoVeiculo);
            }

            if (placaVeiculo == null) {
                stmt.setInt(11, 1);
                stmt.setString(12, "");
            } else {
                stmt.setInt(11, 0);
                stmt.setString(12, placaVeiculo);
            }
            stmt.setInt(13, limit);
            stmt.setLong(14, offset);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                checklists.add(createChecklist(rSet, resumido));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return checklists;
    }

    @Override
    public List<Checklist> getByColaborador(Long cpf, @Nullable Long dataInicial, @Nullable Long dataFinal, int limit,
                                            long offset, boolean resumido) throws SQLException {
        List<Checklist> checklists = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT C.CODIGO, C.COD_CHECKLIST_MODELO, C.DATA_HORA AT TIME ZONE ? AS " +
					"DATA_HORA, "
                    + "C.CPF_COLABORADOR, C.PLACA_VEICULO, C.KM_VEICULO, C.TIPO , C.TEMPO_REALIZACAO, CO.NOME FROM " +
					"CHECKLIST C "
                    + "JOIN COLABORADOR CO ON CO.CPF = C.CPF_COLABORADOR "
                    + "WHERE C.CPF_COLABORADOR = ? "
                    + "AND (? = 1 OR C.DATA_HORA::DATE >= (? AT TIME ZONE ?)) "
                    + "AND (? = 1 OR C.DATA_HORA::DATE <= (? AT TIME ZONE ?)) "
                    + "ORDER BY C.DATA_HORA DESC "
                    + "LIMIT ? OFFSET ?");
            final String zoneId = TimeZoneManager.getZoneIdForCpf(cpf, conn).getId();
            stmt.setString(1, zoneId);
            stmt.setLong(2, cpf);
            if (dataInicial == null || dataFinal == null) {
                stmt.setInt(3, 1);
                stmt.setNull(4, Types.DATE);
                stmt.setNull(5, Types.CHAR);
                stmt.setInt(6, 1);
                stmt.setNull(7, Types.DATE);
                stmt.setNull(8, Types.CHAR);
            } else {
                stmt.setInt(3, 0);
                stmt.setDate(4, new java.sql.Date(dataInicial));
                stmt.setString(5, zoneId);
                stmt.setInt(6, 0);
                stmt.setDate(7, new java.sql.Date(dataFinal));
                stmt.setString(8, zoneId);
            }
            stmt.setInt(9, limit);
            stmt.setLong(10, offset);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                Checklist checklist = createChecklist(rSet, resumido);
                checklists.add(checklist);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return checklists;
    }

    @Override
    public List<String> getUrlImagensPerguntas(Long codUnidade, Long codFuncao) throws SQLException {
        List<String> listUrl = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT DISTINCT CP.URL_IMAGEM FROM CHECKLIST_MODELO_FUNCAO CMF "
                    + "JOIN CHECKLIST_PERGUNTAS CP ON CP.COD_UNIDADE = CMF.COD_UNIDADE "
                    + "AND CP.COD_CHECKLIST_MODELO = CMF.COD_CHECKLIST_MODELO "
                    + "WHERE CMF.COD_UNIDADE = ? "
                    + "AND CMF.COD_FUNCAO = ? "
                    + "AND CP.STATUS_ATIVO = TRUE");

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
    public NovoChecklistHolder getNovoChecklistHolder(Long codUnidade, Long codModelo, String placa, char
			tipoChecklis) throws SQLException {
        final NovoChecklistHolder holder = new NovoChecklistHolder();
        final ChecklistModeloDao checklistModeloDaoImpl = Injection.provideChecklistModeloDao();
        final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
        holder.setCodigoModeloChecklist(codModelo);
        holder.setListPerguntas(checklistModeloDaoImpl.getPerguntas(codUnidade, codModelo));
        holder.setVeiculo(veiculoDao.getVeiculoByPlaca(placa, false));
        return holder;
    }

    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(Long codUnidade, Long codFuncao)
			throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final Map<ModeloChecklist, List<String>> modeloPlaca = new LinkedHashMap<>();
        ModeloChecklist modelo = null;
        List<String> placas = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT CM.CODIGO, CM.NOME, V.PLACA, V.KM FROM "
                            + "CHECKLIST_MODELO CM "
                            + "JOIN CHECKLIST_MODELO_FUNCAO CMF ON CMF.COD_CHECKLIST_MODELO = CM.CODIGO AND CM" +
							".COD_UNIDADE = CMF.COD_UNIDADE "
                            + "JOIN CHECKLIST_MODELO_VEICULO_TIPO CMVT ON CMVT.COD_MODELO = CM.CODIGO AND CMVT" +
							".COD_UNIDADE = CM.COD_UNIDADE "
                            + "JOIN VEICULO_TIPO VT ON VT.CODIGO = CMVT.COD_TIPO_VEICULO "
                            + "JOIN VEICULO V ON V.COD_TIPO = VT.CODIGO "
                            + "WHERE CM.COD_UNIDADE = ? AND CMF.COD_FUNCAO = ? "
                            + "ORDER BY CM.NOME, V.PLACA",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codFuncao);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                // primeira liha do Rset, cria o modelo, add a primeira placa
                if (modelo == null) {
                    modelo = new ModeloChecklist();
                    modelo.setCodigo(rSet.getLong("CODIGO"));
                    modelo.setNome(rSet.getString("NOME"));
                    placas.add(rSet.getString("PLACA"));
                } else {// verificar se o prox modelo é igual ao ja criado
                    if (rSet.getLong("CODIGO") == modelo.getCodigo()) {
                        placas.add(rSet.getString("PLACA"));
                    } else {// modelo diferente, deve setar adicionar tudo ao map e zerar os valores.
                        modeloPlaca.put(modelo, placas);
                        modelo = new ModeloChecklist();
                        placas = new ArrayList<>();
                        modelo.setCodigo(rSet.getLong("CODIGO"));
                        modelo.setNome(rSet.getString("NOME"));
                        placas.add(rSet.getString("PLACA"));
                    }
                }
            }
            if (modelo != null) {
                modeloPlaca.put(modelo, placas);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return modeloPlaca;
    }

    @Override
    public FarolChecklist getFarolChecklist(Long codUnidade,
                                            Date dataInicial,
                                            Date dataFinal,
                                            boolean itensCriticosRetroativos) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select dados_checklist.*, COS.nome AS \"COLABORADOR_SAIDA\", COR.NOME AS " +
					"\"COLABORADOR_RETORNO\" from\n" +
                    "  (SELECT\n" +
                    "  AD.data,\n" +
                    "  V.PLACA,\n" +
                    "  max(CASE WHEN C.TIPO = 'S' THEN C.codigo END) as cod_checklist_saida,\n" +
                    "  MAX(CASE WHEN C.TIPO = 'S' THEN C.DATA_HORA END) AT TIME ZONE ? AS DATA_HORA_ULTIMO_CHECKLIST_SAIDA,\n" +
                    "  max(CASE WHEN C.TIPO = 'R' THEN C.codigo END) as cod_checklist_retorno,\n" +
                    "  MAX(CASE WHEN C.TIPO = 'R' THEN C.DATA_HORA END) AT TIME ZONE ? AS DATA_HORA_ULTIMO_CHECKLIST_RETORNO\n" +
                    "FROM aux_data AD\n" +
                    "  LEFT JOIN VEICULO V ON V.cod_unidade = ?\n" +
                    "  LEFT JOIN CHECKLIST C ON AD.data = C.data_hora::DATE AND C.placa_veiculo = V.placa\n" +
                    "WHERE AD.data BETWEEN (? AT TIME ZONE ?) AND (? AT TIME ZONE ?)\n" +
                    "GROUP BY 1, 2\n" +
                    "ORDER BY 1, 2) as dados_checklist LEFT JOIN CHECKLIST CS ON CS.CODIGO = dados_checklist" +
					".cod_checklist_saida\n" +
                    "LEFT JOIN CHECKLIST CR ON CR.CODIGO = dados_checklist.cod_checklist_retorno\n" +
                    "LEFT JOIN COLABORADOR COS ON COS.cpf = CS.cpf_colaborador\n" +
                    "LEFT JOIN COLABORADOR COR ON COR.cpf = CR.cpf_colaborador\n" +
                    "ORDER BY dados_checklist.placa;");
            final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId();
            stmt.setString(1, zoneId);
            stmt.setString(2, zoneId);
            stmt.setLong(3, codUnidade);
            stmt.setDate(4, DateUtils.toSqlDate(dataInicial));
            stmt.setString(5, zoneId);
            stmt.setDate(6, DateUtils.toSqlDate(dataFinal));
            stmt.setString(7, zoneId);
            rSet = stmt.executeQuery();
            List<FarolVeiculoDia> farois = new ArrayList<>();
            while (rSet.next()) {
                farois.add(createFarolVeiculoDia(rSet, dataInicial, dataFinal, itensCriticosRetroativos));
            }
            return new FarolChecklist(farois);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private FarolVeiculoDia createFarolVeiculoDia(ResultSet rSet,
                                                  Date dataInicial,
                                                  Date dataFinal,
                                                  boolean itensCriticosRetroativos) throws SQLException {
        Checklist checkSaida = null;
        final Long codChecklistSaida = rSet.getLong("COD_CHECKLIST_SAIDA");
        if (!rSet.wasNull()) {
            checkSaida = new Checklist();
            final Colaborador colaboradorSaida = new Colaborador();
            colaboradorSaida.setNome(rSet.getString("COLABORADOR_SAIDA"));
            checkSaida.setCodigo(codChecklistSaida);
            checkSaida.setColaborador(colaboradorSaida);
            checkSaida.setData(rSet.getObject("DATA_HORA_ULTIMO_CHECKLIST_SAIDA", LocalDateTime.class));
        }
        Checklist checkRetorno = null;
        final Long codChecklistRetorno = rSet.getLong("COD_CHECKLIST_RETORNO");
        if (!rSet.wasNull()) {
            checkRetorno = new Checklist();
            final Colaborador colaboradorRetorno = new Colaborador();
            colaboradorRetorno.setNome(rSet.getString("COLABORADOR_RETORNO"));
            checkRetorno.setCodigo(codChecklistRetorno);
            checkRetorno.setColaborador(colaboradorRetorno);
            checkRetorno.setData(rSet.getObject("DATA_HORA_ULTIMO_CHECKLIST_RETORNO", LocalDateTime.class));
        }
        final Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(rSet.getString("PLACA"));
        final List<ItemOrdemServico> itensCriticos = Injection
                .provideOrdemServicoDao()
                .getItensOsManutencaoHolder(veiculo.getPlaca(), dataInicial, dataFinal, itensCriticosRetroativos);
        return new FarolVeiculoDia(veiculo, checkSaida, checkRetorno, itensCriticos);
    }

    /**
     * Busca uma lista de todas as placas da unidade, separando em 3 status:
     * PENDENTE: não tem checklist realizado no dia atual e não tem itens críticos a serem arrumados
     * NÃO LIBERADO: placa tem itens críticos que necessitam de conserto imediato, não sendo permitida a liberação do
     * veículo
     * LIBERADO: checklist foi realizado e não tem problemas críticos a serem resolvidos
     *
     * @param codUnidade
     * @return
     * @throws SQLException
     */
    @Override
    @Deprecated
    public List<VeiculoLiberacao> getStatusLiberacaoVeiculos(Long codUnidade) throws SQLException {
        List<VeiculoLiberacao> listVeiculos = new ArrayList<>();
        List<PerguntaRespostaChecklist> listProblemas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        VeiculoLiberacao veiculo = null;
        PerguntaRespostaChecklist pergunta = null;
        boolean hasCheck = false;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT DISTINCT V.PLACA, PLACAS_MANUTENCAO.ITEM_MANUTENCAO, CHECK_HOJE" +
					".PLACA_CHECK FROM \n" +
                    "(SELECT DISTINCT PLACA_VEICULO AS PLACA_CHECK FROM CHECKLIST C \n" +
                    "JOIN VEICULO V ON V.PLACA = C.PLACA_VEICULO WHERE DATA_HORA::DATE = ?\n" +
                    "AND V.cod_unidade = ?) AS CHECK_HOJE RIGHT JOIN VEICULO V ON V.PLACA = PLACA_CHECK\n" +
                    "LEFT JOIN (SELECT e.placa_veiculo as PLACA_MANUTENCAO, e.pergunta AS ITEM_MANUTENCAO\n" +
                    "FROM estratificacao_os e\n" +
                    "where e.cod_unidade = ? and e.status_item like 'P' and e.prioridade like 'CRITICA' and e" +
					".cpf_mecanico is null\n" +
                    "order by e.placa_veiculo) AS PLACAS_MANUTENCAO ON PLACA_MANUTENCAO = V.PLACA\n" +
                    "WHERE V.COD_UNIDADE = ?\n" +
                    "ORDER BY V.PLACA, PLACAS_MANUTENCAO.ITEM_MANUTENCAO;");
            stmt.setDate(1, new java.sql.Date(Now.utcMillis()));
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codUnidade);
            stmt.setLong(4, codUnidade);
            rSet = stmt.executeQuery();

            while (rSet.next()) {
                if (veiculo == null) {//primeira linha do rSet
                    veiculo = new VeiculoLiberacao();
                    veiculo.setPlaca(rSet.getString("PLACA"));
                    if (rSet.getString("item_manutencao") != null) {
                        pergunta = new PerguntaRespostaChecklist();
                        pergunta.setPergunta(rSet.getString("ITEM_MANUTENCAO"));
                        listProblemas.add(pergunta);
                    }
                    if (veiculo.getPlaca().equals(rSet.getString("PLACA_CHECK"))) {
                        hasCheck = true;
                    } else {
                        hasCheck = false;
                    }
                } else {//a partir da segunda linha do Rset
                    if (veiculo.getPlaca().equals(rSet.getString("placa"))) {
                        if (rSet.getString("item_manutencao") != null) {
                            pergunta = new PerguntaRespostaChecklist();
                            pergunta.setPergunta(rSet.getString("ITEM_MANUTENCAO"));
                            listProblemas.add(pergunta);
                        }
                    } else {
                        verificaInsereListaLiberacao(hasCheck, listProblemas, listVeiculos, veiculo);
                        veiculo = new VeiculoLiberacao();
                        veiculo.setPlaca(rSet.getString("placa"));
                        if (veiculo.getPlaca().equals(rSet.getString("PLACA_CHECK"))) {
                            hasCheck = true;
                        } else {
                            hasCheck = false;
                        }
                        listProblemas = new ArrayList<>();
                        if (rSet.getString("item_manutencao") != null) {
                            pergunta = new PerguntaRespostaChecklist();
                            pergunta.setPergunta(rSet.getString("ITEM_MANUTENCAO"));
                            listProblemas.add(pergunta);
                        }
                    }
                }
            }
            verificaInsereListaLiberacao(hasCheck, listProblemas, listVeiculos, veiculo);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return listVeiculos;
    }

    private PerguntaRespostaChecklist createPergunta(ResultSet rSet) throws SQLException {
        PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
        pergunta.setCodigo(rSet.getLong("COD_PERGUNTA"));
        pergunta.setOrdemExibicao(rSet.getInt("ORDEM_PERGUNTA"));
        pergunta.setPergunta(rSet.getString("PERGUNTA"));
        pergunta.setSingleChoice(rSet.getBoolean("SINGLE_CHOICE"));
        pergunta.setUrl(rSet.getString("URL_IMAGEM"));
        pergunta.setPrioridade(rSet.getString("PRIORIDADE"));
        return pergunta;
    }

    private AlternativaChecklist createAlternativa(ResultSet rSet) throws SQLException {
        AlternativaChecklist alternativa = new AlternativaChecklist();
        alternativa.codigo = rSet.getLong("COD_ALTERNATIVA");
        alternativa.alternativa = rSet.getString("ALTERNATIVA");
        if (alternativa.alternativa.equals("Outros")) {
            alternativa.tipo = AlternativaChecklist.TIPO_OUTROS;
            try {
                alternativa.respostaOutros = rSet.getString("resposta");
            } catch (SQLException e) {
            }
        }
        return alternativa;
    }

    private void insertRespostas(Checklist checklist, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO CHECKLIST_RESPOSTAS "
                    + "(COD_UNIDADE, COD_CHECKLIST_MODELO, COD_CHECKLIST, COD_PERGUNTA, COD_ALTERNATIVA, RESPOSTA) "
                    + "VALUES ((SELECT V.COD_UNIDADE FROM VEICULO V WHERE V.PLACA=?), ?, ?, ?, ?, ?)");
            for (PerguntaRespostaChecklist resposta : checklist.getListRespostas()) {
                stmt.setString(1, checklist.getPlacaVeiculo());
                stmt.setLong(2, checklist.getCodModelo());
                stmt.setLong(3, checklist.getCodigo());
                stmt.setLong(4, resposta.getCodigo());
                for (AlternativaChecklist alternativa : resposta.getAlternativasResposta()) {
                    stmt.setLong(5, alternativa.codigo);
                    //se a alternativa esta selecionada
                    if (alternativa.selected) {
                        // se a alternativa é do tipo Outros
                        if (alternativa.tipo == AlternativaChecklist.TIPO_OUTROS) {
                            // salva a resposta escrita do usuário
                            stmt.setString(6, alternativa.respostaOutros);
                        } else {
                            // se a alternativa esta MARCADA e não é do tipo Outros
                            stmt.setString(6, "NOK");
                        }
                        // alternativa esta desmarcada
                    } else {
                        // salva OK, indicando que o item NÃO tem problema
                        stmt.setString(6, "OK");
                    }
                    int count = stmt.executeUpdate();
                    if (count == 0) {
                        throw new SQLException("Erro ao inserir resposta");
                    }
                }
            }
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private Checklist createChecklist(ResultSet rSet, boolean resumido) throws SQLException {
        final Checklist checklist = new Checklist();
        checklist.setCodigo(rSet.getLong("CODIGO"));
        checklist.setCodModelo(rSet.getLong("COD_CHECKLIST_MODELO"));
        checklist.setColaborador(createColaborador(rSet.getLong("CPF_COLABORADOR"), rSet.getString("NOME")));
        checklist.setData(rSet.getObject("DATA_HORA", LocalDateTime.class));
        checklist.setPlacaVeiculo(rSet.getString("PLACA_VEICULO"));
        checklist.setTipo(rSet.getString("TIPO").charAt(0));
        checklist.setKmAtualVeiculo(rSet.getLong("KM_VEICULO"));
        checklist.setTempoRealizacaoCheckInMillis(rSet.getLong("TEMPO_REALIZACAO"));
        setQtdOkOrNok(checklist);
        if (!resumido) {
            checklist.setListRespostas(getPerguntasRespostas(checklist));
        }
        return checklist;
    }

    private Colaborador createColaborador(Long cpf, String nome) {
        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(cpf);
        colaborador.setNome(nome);
        return colaborador;
    }

    private void setQtdOkOrNok(Checklist checklist) throws SQLException {
        final List<PerguntaRespostaChecklist> respostas = getPerguntasRespostas(checklist);
        int qtdNok = 0;
        for (PerguntaRespostaChecklist resposta : respostas) {
            for (AlternativaChecklist alternativa : resposta.getAlternativasResposta()) {
                if (alternativa.selected) {
                    qtdNok++;
                    break;
                }
            }
        }
        checklist.setQtdItensNok(qtdNok);
        checklist.setQtdItensOk(respostas.size() - qtdNok);
    }

    private List<PerguntaRespostaChecklist> getPerguntasRespostas(Checklist checklist) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<PerguntaRespostaChecklist> perguntas = new ArrayList<>();
        List<AlternativaChecklist> alternativas = new ArrayList<>();
        PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
        AlternativaChecklist alternativa;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT CP.CODIGO AS COD_PERGUNTA,\n" +
                            "  CP.ORDEM AS ORDEM_PERGUNTA,\n" +
                            "  CP.PERGUNTA,\n" +
                            "  CP.SINGLE_CHOICE,\n" +
                            "  CAP.CODIGO AS COD_ALTERNATIVA,\n" +
                            "  CP.PRIORIDADE,\n" +
                            "  CAP.ORDEM,\n" +
                            "  CP.URL_IMAGEM,\n" +
                            "  CAP.ALTERNATIVA,\n" +
                            "  CR.RESPOSTA\n" +
                            "FROM CHECKLIST C\n" +
                            "  JOIN CHECKLIST_RESPOSTAS CR\n" +
                            "    ON C.CODIGO = CR.COD_CHECKLIST\n" +
                            "       AND CR.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO\n" +
                            "       AND c.cod_unidade = cr.cod_unidade\n" +
                            "  JOIN CHECKLIST_PERGUNTAS CP\n" +
                            "    ON CP.CODIGO = CR.COD_PERGUNTA\n" +
                            "       AND CP.COD_UNIDADE = CR.COD_UNIDADE\n" +
                            "       AND CP.COD_CHECKLIST_MODELO = CR.COD_CHECKLIST_MODELO\n" +
                            "       AND cp.codigo = cr.cod_pergunta\n" +
                            "  JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP\n" +
                            "    ON CAP.CODIGO = CR.COD_ALTERNATIVA\n" +
                            "       AND CAP.COD_UNIDADE = CR.COD_UNIDADE\n" +
                            "       AND CAP.COD_CHECKLIST_MODELO = CR.COD_CHECKLIST_MODELO\n" +
                            "       AND cap.cod_pergunta = cr.cod_pergunta\n" +
                            "WHERE c.codigo = ? AND c.cpf_colaborador = ?\n" +
                            "ORDER BY CP.ORDEM, CAP.ORDEM", ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            stmt.setLong(1, checklist.getCodigo());
            stmt.setLong(2, checklist.getColaborador().getCpf());
            rSet = stmt.executeQuery();
            if (rSet.first()) {
                pergunta = createPergunta(rSet);
                alternativa = createAlternativa(rSet);
                setRespostaAlternativa(alternativa, rSet);
                alternativas.add(alternativa);
            }
            while (rSet.next()) {
                if (rSet.getLong("COD_PERGUNTA") == pergunta.getCodigo()) {
                    alternativa = createAlternativa(rSet);
                    setRespostaAlternativa(alternativa, rSet);
                    alternativas.add(alternativa);
                } else {
                    pergunta.setAlternativasResposta(alternativas);
                    perguntas.add(pergunta);
                    alternativas = new ArrayList<>();

                    pergunta = createPergunta(rSet);

                    alternativa = createAlternativa(rSet);
                    setRespostaAlternativa(alternativa, rSet);
                    alternativas.add(alternativa);
                }
            }
            pergunta.setAlternativasResposta(alternativas);
            perguntas.add(pergunta);

        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return perguntas;
    }

    // remonta as alternativas de uma Pergunta
    private void setRespostaAlternativa(AlternativaChecklist alternativa, ResultSet rSet) throws SQLException {
        if (rSet.getString("RESPOSTA").equals("NOK")) {
            alternativa.selected = true;
        } else if (rSet.getString("RESPOSTA").equals("OK")) {
            alternativa.selected = false;
        } else {
            alternativa.selected = true;
            alternativa.tipo = AlternativaChecklist.TIPO_OUTROS;
            alternativa.respostaOutros = rSet.getString("RESPOSTA");
        }
    }

    /**
     * Verifica se o veiculo tem problema e se tem check, setando o status e adicionando na lista
     *
     * @param hasCheck      boolean indicando se o veiculo possui checklist realizado no dia corrente
     * @param listProblemas lista de problemas que o veículo possui
     * @param listVeiculos  lista final com os veiculos {@link VeiculoLiberacao}
     * @param veiculo       um veiculo {@link VeiculoLiberacao}
     */
    @Deprecated
    private void verificaInsereListaLiberacao(boolean hasCheck, List<PerguntaRespostaChecklist> listProblemas,
                                              List<VeiculoLiberacao> listVeiculos, VeiculoLiberacao veiculo) {
        if (listProblemas.size() > 0) {
            VeiculoLiberacao v = new VeiculoLiberacao();
            v.setItensCriticos(listProblemas);
            v.setPlaca(veiculo.getPlaca());
            v.setStatus(VeiculoLiberacao.STATUS_NAO_LIBERADO);
            listVeiculos.add(v);
            if (!hasCheck) {
                veiculo.setStatus(VeiculoLiberacao.STATUS_PENDENTE);
                listVeiculos.add(veiculo);
            }
        } else {
            if (hasCheck) {
                veiculo.setStatus(VeiculoLiberacao.STATUS_LIBERADO);
                listVeiculos.add(veiculo);
            } else {
                veiculo.setStatus(VeiculoLiberacao.STATUS_PENDENTE);
                listVeiculos.add(veiculo);
            }
        }
    }
}