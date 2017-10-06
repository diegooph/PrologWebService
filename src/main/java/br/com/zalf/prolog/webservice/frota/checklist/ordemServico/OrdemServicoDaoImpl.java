package br.com.zalf.prolog.webservice.frota.checklist.ordemServico;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.checklist.model.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;

/**
 * Created by jean on 10/08/16.
 */
@SuppressWarnings("Duplicates")
public class OrdemServicoDaoImpl extends DatabaseConnection implements OrdemServicoDao {

    private static final String PRIORIDADE_CRITICA = "CRITICA";
    private static final String PRIORIDADE_ALTA = "ALTA";
    private static final String PRIORIDADE_BAIXA = "BAIXA";

    private static final int MINUTOS_NUM_DIA = 1440;
    private static final int MINUTOS_NUMA_HORA = 60;

    /**
     * Busca os itens de uma ou mais OS, respeitando os parâmetros de filtro,
     * codigo da os / código da unidade / placa / status da OS
     */
    private static final String BUSCA_ITENS_OS = "select * from estratificacao_os e\n" +
            "where  e.cod_os::TEXT LIKE ? and e.cod_unidade::TEXT LIKE ? and e.placa_veiculo like ? " +
            "and e.status_item LIKE ?\n" +
            "order by e.placa_veiculo, e.prazo ASC;";

    /**
     * Busca todas as OS e seus devidos itens, respeitando os filtros enviados nos parâmetros
     * @param placa uma placa especifica ou '%' para buscar OS de todas as placas
     * @param status status da OS, podendo ser Aberta ou Fechada
     * @param codUnidade código da unidade a serem buscadas as OS
     * @param tipoVeiculo tipo do veículo ou '%' para todos os tipos
     * @param limit quantidade de OS que deseja retornar
     * @param offset um offset
     * @return uma lista de OrdemServico
     * @throws SQLException caso não seja possivel realizar a busca
     */
    @Override
    public List<OrdemServico> getOs(String placa, String status, Long codUnidade,
                                    String tipoVeiculo, Integer limit, Long offset) throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<OrdemServico> oss = new ArrayList<>();

        try{
            conn = getConnection();
            /**
             * query que busca apenas os dados da OS, e não os itens
             */
            String query = "SELECT cos.codigo as cod_os, cos.cod_checklist, cos.data_hora_fechamento, cos.status, C.placa_veiculo, V.km, c.data_hora " +
                    "FROM checklist_ordem_servico cos join checklist c ON cos.cod_checklist = C.codigo\n" +
                    "and c.cod_unidade = cos.cod_unidade\n" +
                    "JOIN VEICULO V ON V.placa = C.placa_veiculo\n" +
                    "JOIN veiculo_tipo VT ON VT.cod_unidade = C.cod_unidade AND v.cod_tipo = vt.codigo\n" +
                    "where c.placa_veiculo LIKE ? and cos.status LIKE ? and c.cod_unidade = ? AND " +
                    "VT.codigo::TEXT LIKE ? \n "  +
                    "ORDER BY cos.codigo desc\n" +
                    "%s";
            if (limit != null && offset != null){
                query = String.format(query, " LIMIT " + limit +  "OFFSET " + offset);
            }else{
                query = String.format(query, "");
            }
            stmt = conn.prepareStatement(query);
            stmt.setString(1, placa);
            stmt.setString(2, status);
            stmt.setLong(3, codUnidade);
            stmt.setString(4, tipoVeiculo);
            rSet = stmt.executeQuery();
            while(rSet.next()){
                OrdemServico os = createOrdemServico(rSet);
                /**
                 * seta os itens da ordem de serviço.
                 */
                os.setItens(getItensOs(os.getVeiculo().getPlaca(), String.valueOf(os.getCodigo()), "%", conn, codUnidade));
                oss.add(os);
            }
        }finally {
            closeConnection(conn, stmt, rSet);
        }
        return oss;
    }

    /**
     * Busca os itens para a tela das bolinhas, após selecionar uma placa
     * @param placa uma Placa
     * @param status status dos Itens da OS
     * @param limit um limit
     * @param offset um offset
     * @param prioridade prioridade dos itens a serem buscados
     * @return Lista de ItemOrdemServico
     * @throws SQLException caso não seja possível buscar os itens
     */
    @Override
    public List<ItemOrdemServico> getItensOsManutencaoHolder(String placa, String status,
                                                             int limit, long offset, String prioridade) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Connection conn = null;
        try{
            conn = getConnection();
            stmt = conn.prepareStatement("select * from estratificacao_os e \n" +
                    "where e.status_item like ? and e.prioridade like ? and e.placa_veiculo = ?\n" +
                    "ORDER BY E.placa_veiculo, e.prioridade, e.data_hora DESC  \n" +
                    "limit ? offset ?");
            stmt.setString(1, status);
            stmt.setString(2, prioridade);
            stmt.setString(3, placa);
            stmt.setInt(4, limit);
            stmt.setLong(5, offset);
            rSet = stmt.executeQuery();
            return createItensOs(rSet);
        }finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public List<ItemOrdemServico> getItensOsManutencaoHolder(String placa, Date dataInicial, Date dataFinal,
                                                             boolean itensCriticosRetroativos) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Connection conn = null;
        try{
            conn = getConnection();
            String query = "select * from estratificacao_os e \n" +
                    "where e.status_item like 'P' and e.prioridade like 'CRITICA' and e.placa_veiculo = ? \n" +
                    "and e.data_hora::date %s ? \n " +
                    "ORDER BY E.placa_veiculo, e.prioridade, e.data_hora DESC ";
            if(itensCriticosRetroativos){
                query = String.format(query, "<=");
            } else {
                query = String.format(query, "=");
            }
            stmt = conn.prepareStatement(query);
            stmt.setString(1, placa);
            stmt.setDate(2, DateUtils.toSqlDate(dataFinal));
            rSet = stmt.executeQuery();
            return createItensOs(rSet);
        }finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    /**
     * Método chamado quando é recebido um checklist, verifica as premissas para criar uma nova OS ou add
     * o item com problema a uma OS existente
     * @param checklist Um checklist
     * @param conn uma Connection
     * @param codUnidade Código da unidade que gerou o checklist
     * @throws SQLException caso nao seja possivel realizar a busca
     */
    @Override
    public void insertItemOs(Checklist checklist, Connection conn, Long codUnidade) throws SQLException {
        Long tempCodOs = null;
        Long gerouOs = null;
        // todas as os de uma unica placa
        List<OrdemServico> ordens = getOs(checklist.getPlacaVeiculo(), OrdemServico.Status.ABERTA.asString(), codUnidade, "%", null, null);
        for (PerguntaRespostaChecklist pergunta: checklist.getListRespostas()) { //verifica cada pergunta do checklist
            for (AlternativaChecklist alternativa: pergunta.getAlternativasResposta()) { // varre cada alternativa de uma pergunta
                if (alternativa.selected) {
                    if (ordens != null) {//verifica se ja tem algum item em aberto
                        tempCodOs = jaPossuiItemEmAberto(pergunta.getCodigo(), alternativa.codigo, ordens);
                        if (tempCodOs != null) {
                            Log.d("tempCodOs", tempCodOs.toString());
                        }
                    }
                    if (tempCodOs != null) {
                        incrementaQtApontamento(checklist.getPlacaVeiculo(), tempCodOs, pergunta.getCodigo(), alternativa.codigo, conn);
                    } else {
                        if (gerouOs != null) { //checklist ja gerou uma os -> deve inserir o item nessa os gerada
                            insertServicoOs(pergunta.getCodigo(), alternativa.codigo, gerouOs, checklist.getPlacaVeiculo(), conn);
                        } else {
                            gerouOs = createOs(checklist.getPlacaVeiculo(), checklist.getCodigo(), conn);
                            insertServicoOs(pergunta.getCodigo(), alternativa.codigo, gerouOs, checklist.getPlacaVeiculo(), conn);
                        }
                    }
                }
            }
        }
    }

    /**
     * Busca a lista de itens agrupadas por placa e criticidade (tela das bolinhas)
     * @param codUnidade Código da unidade
     * @param limit Quantidade de placas
     * @param offset um offset
     * @param status status
     * @return lista de ManutencaoHolder
     * @throws SQLException caso não seja possível realizar a busca
     */
    @Override
    public List<ManutencaoHolder> getResumoManutencaoHolder(String placa, String codTipo, Long codUnidade, int limit,
                                                            long offset, String status) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<ManutencaoHolder> placas = new ArrayList<>();
        ManutencaoHolder holder;
        Veiculo v;
        try{
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT V.placa, v.km,TOTAL, COALESCE(CRITICA.CRITICAS, CRITICA.CRITICAS, 0) as criticas, coalesce(ALTAS.ALTAS, ALTAS.ALTAS, 0) as altas,\n" +
                    "  coalesce(BAIXAS.BAIXAS, BAIXAS.BAIXAS, 0) as baixas FROM VEICULO V\n" +
                    "  JOIN\n" +
                    "  (SELECT es.placa_veiculo AS PLACA_TOTAL, COUNT(COD_unidade) as TOTAL FROM estratificacao_os es\n" +
                    "where es.status_item like ? and\n" +
                    "      es.prioridade like ?\n" +
                    "group by es.placa_veiculo) AS TOTAL ON PLACA_TOTAL = V.placa\n" +
                    "  LEFT JOIN\n" +
                    "  (SELECT es.placa_veiculo AS PLACA_CRITICA, COUNT(COD_unidade) as CRITICAS FROM estratificacao_os es\n" +
                    "where es.status_item like ? and\n" +
                    "      es.prioridade like ?\n" +
                    "group by es.placa_veiculo) AS CRITICA ON PLACA_CRITICA = V.placa\n" +
                    "LEFT join\n" +
                    "  (SELECT es.placa_veiculo as PLACA_ALTA, COUNT(COD_unidade) as ALTAS FROM estratificacao_os es\n" +
                    "where es.status_item like ? and\n" +
                    "      es.prioridade like ?\n" +
                    "group by es.placa_veiculo) AS ALTAS ON PLACA_ALTA = V.placa\n" +
                    "LEFT join\n" +
                    "  (SELECT es.placa_veiculo as PLACA_BAIXA, COUNT(COD_unidade) as BAIXAS FROM estratificacao_os es\n" +
                    "where es.status_item like ? and\n" +
                    "      es.prioridade like ?\n" +
                    "group by es.placa_veiculo) AS BAIXAS ON PLACA_BAIXA = V.placa\n" +
                    "  JOIN\n" +
                    "  veiculo_tipo vt on vt.cod_unidade = v.cod_unidade and vt.codigo = v.cod_tipo\n" +
                    "  WHERE V.cod_unidade = ? and v.placa like ? and v.cod_tipo::text like ?\n" +
                    "  ORDER BY criticas desc, altas desc, baixas desc, V.placa\n" +
                    "  LIMIT ? OFFSET ?");
            stmt.setString(1, status);
            stmt.setString(2, "%");
            stmt.setString(3, status);
            stmt.setString(4, PRIORIDADE_CRITICA);
            stmt.setString(5, status);
            stmt.setString(6, PRIORIDADE_ALTA);
            stmt.setString(7, status);
            stmt.setString(8, PRIORIDADE_BAIXA);
            stmt.setLong(9, codUnidade);
            stmt.setString(10, placa);
            stmt.setString(11, codTipo);
            stmt.setInt(12, limit);
            stmt.setLong(13, offset);
            rSet = stmt.executeQuery();
            while (rSet.next()){
                holder = new ManutencaoHolder();
                v = new Veiculo();
                v.setPlaca(rSet.getString("PLACA"));
                v.setKmAtual(rSet.getLong("km"));
                v.setAtivo(true);
                holder.setVeiculo(v);
                holder.setQtdCritica(rSet.getInt("CRITICAS"));
                holder.setQtdAlta(rSet.getInt("ALTAS"));
                holder.setQtdBaixa(rSet.getInt("BAIXAS"));
                placas.add(holder);
            }
        }finally {
            closeConnection(conn,stmt,rSet);
        }
        return placas;
    }

    @Override
    public boolean consertaItem (ItemOrdemServico item, String placa) throws SQLException {
        VeiculoDao veiculoDao = Injection.provideVeiculoDao();
        Connection conn = null;
        PreparedStatement stmt = null;
        try{
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("UPDATE CHECKLIST_ORDEM_SERVICO_ITENS SET " +
                    "CPF_MECANICO = ?, TEMPO_REALIZACAO = ?, KM = ?, STATUS_RESOLUCAO = ?, data_hora_conserto = ?, " +
                    "FEEDBACK_CONSERTO = ? " +
                    "WHERE COD_UNIDADE = (SELECT COD_UNIDADE FROM veiculo WHERE placa = ?) AND COD_OS = ? AND COD_PERGUNTA = ? AND " +
                    "COD_ALTERNATIVA = ? ");
            stmt.setLong(1, item.getMecanico().getCpf());
            stmt.setLong(2, item.getTempoRealizacaoConserto().toMillis());
            stmt.setLong(3, item.getKmVeiculoFechamento());
            stmt.setString(4, ItemOrdemServico.Status.RESOLVIDO.asString());
            stmt.setTimestamp(5, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            stmt.setString(6, item.getFeedbackResolucao().trim());
            stmt.setString(7, placa);
            stmt.setLong(8, item.getCodOs());
            stmt.setLong(9, item.getPergunta().getCodigo());
            stmt.setLong(10, item.getPergunta().getAlternativasResposta().get(0).codigo);
            int count = stmt.executeUpdate();
            if (count > 0){
                updateStatusOs(placa, item.getCodOs(), conn);
                veiculoDao.updateKmByPlaca(placa, item.getKmVeiculoFechamento(), conn);
            }else{
                throw new SQLException("Erro ao consertar o item");
            }
            conn.commit();
        }finally {
            closeConnection(conn,stmt,null);
        }
        return true;
    }

    /**
     * Atribui o tempo restante para conserto de um item
     * @param itemManutencao um ItemManutencao
     * @param prazoHoras prazo do item, em horas
     */
    private void setTempoRestante(ItemOrdemServico itemManutencao, int prazoHoras) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(itemManutencao.getDataApontamento());
        calendar.add(Calendar.HOUR, prazoHoras);// data apontamento + prazo
        Date dataMaxima = calendar.getTime(); // data máxima de resolução
        long tempoRestante = dataMaxima.getTime() - System.currentTimeMillis();
        itemManutencao.setTempoRestante(Duration.ofMillis(tempoRestante));
    }

    /**
     * Cria uma ordem de serviço no banco de dados e retorna o código gerado na criação
     * @param placa uma placa
     * @param codChecklist código do checklist que originou a O.S.
     * @param conn uma Connection
     * @return um Long com o códdigo da OS criada
     * @throws SQLException caso não seja possivel inserir a OS
     */
    private Long createOs (String placa, Long codChecklist, Connection conn) throws SQLException {
        Log.d("criando OS", "Placa: " + placa + "checklist: " + codChecklist);
        ResultSet rSet = null;
        PreparedStatement stmt = null;
        try{
            stmt = conn.prepareStatement("INSERT INTO checklist_ordem_servico(CODIGO, cod_unidade, cod_checklist, status) VALUES\n" +
                    "((SELECT COALESCE(MAX(CODIGO), MAX(CODIGO), 0) +1 AS CODIGO\n" +
                    "  FROM checklist_ordem_servico\n" +
                    "  WHERE cod_unidade = (SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?)),\n" +
                    " (SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?),?,?) RETURNING CODIGO");
            stmt.setString(1, placa);
            stmt.setString(2, placa);
            stmt.setLong(3, codChecklist);
            stmt.setString(4, OrdemServico.Status.ABERTA.asString());
            rSet = stmt.executeQuery();
            if (rSet.next()){
                return rSet.getLong("codigo");
            }else{
                throw new SQLException("Erro ao criar nova OS");
            }
        }finally {
            closeConnection(null, stmt, rSet);
        }
    }

    /**
     * Insere um serviço em uma O.S. específica
     * @param codPergunta código do item a ser adicional
     * @param codAlternativa código da alternativa marcada
     * @param codOs código da OS ao qual deve ser inserido o item
     * @param placa uma placa
     * @param conn uma Connection
     * @throws SQLException caso não seja possível realizar a busca
     */
    private void insertServicoOs(Long codPergunta, Long codAlternativa, Long codOs, String placa, Connection conn) throws SQLException{
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO checklist_ordem_servico_itens(COD_UNIDADE, COD_OS, cod_pergunta, cod_alternativa, status_resolucao)\n" +
                    "VALUES ((SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?),?,?,?,?)");
            stmt.setString(1, placa);
            stmt.setLong(2, codOs);
            stmt.setLong(3, codPergunta);
            stmt.setLong(4, codAlternativa);
            stmt.setString(5, ItemOrdemServico.Status.PENDENTE.asString());
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o serviço");
            }
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    /**
     * Incrementa a quantidade de apontamentos de um item, caso ele ainda esteja em aberto e tenha sido
     * inserido em um novo checklist.
     * @param placa uma placa
     * @param codOs um codigo
     * @param codPergunta um codigo
     * @param codAlternativa     um codigo
     * @param conn uma Connection
     * @throws SQLException caso não seja possivel realizar a busca
     */
    private void incrementaQtApontamento(String placa, Long codOs, Long codPergunta, Long codAlternativa, Connection conn) throws SQLException{
        PreparedStatement stmt = null;
        try{
            stmt = conn.prepareStatement("UPDATE checklist_ordem_servico_itens SET qt_apontamentos =\n" +
                    "(SELECT qt_apontamentos FROM\n" +
                    "checklist_ordem_servico_itens WHERE\n" +
                    "cod_unidade = (SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?)\n" +
                    "AND cod_os = ?\n" +
                    "AND cod_pergunta = ?\n" +
                    "AND cod_alternativa = ?\n" +
                    "AND status_resolucao = ? ) + 1\n" +
                    "WHERE cod_unidade = (SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?)\n" +
                    "AND cod_os = ?\n" +
                    "AND cod_pergunta = ?\n" +
                    "AND cod_alternativa = ?\n" +
                    "AND status_resolucao = ?");
            stmt.setString(1, placa);
            stmt.setLong(2, codOs);
            stmt.setLong(3, codPergunta);
            stmt.setLong(4, codAlternativa);
            stmt.setString(5, ItemOrdemServico.Status.PENDENTE.asString());
            stmt.setString(6, placa);
            stmt.setLong(7, codOs);
            stmt.setLong(8, codPergunta);
            stmt.setLong(9, codAlternativa);
            stmt.setString(10, ItemOrdemServico.Status.PENDENTE.asString());
            int count = stmt.executeUpdate();
            if (count == 0){
                throw new SQLException("Erro ao incrementar a quantidade de apontamentos");
            }
        }finally {
            closeConnection(null, stmt, null);
        }
    }

    /**
     * Busca os itens que compõe uma ou mais OS, usdao quando são buscadas as OS (e não o manutencaoHolder que busca só os itens)
     * @param placa Placa da OS, "%" para todas as placas
     * @param codOs Código da OS, "%" para todas as OS
     * @param status Status da OS, "%" para todos os status
     * @param conn uma Connection
     * @param codUnidade Código da unidade
     * @return um list de ItemOrdemServico
     * @throws SQLException caso não seja possível realizar a busca
     */
    private List<ItemOrdemServico> getItensOs(String placa, String codOs, String status, Connection conn, Long codUnidade) throws SQLException{
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try{
            stmt = conn.prepareStatement(BUSCA_ITENS_OS);
            stmt.setString(1, String.valueOf(codOs));
            stmt.setString(2, String.valueOf(codUnidade));
            stmt.setString(3, placa);
            stmt.setString(4, status);
            rSet = stmt.executeQuery();
            return createItensOs(rSet);
        }finally {
            closeConnection(null, stmt, rSet);
        }
    }

    /**
     * Método genérico para criar os itens de uma OS.
     * @param rSet um ResultSet
     * @return uma lista de ItemOrdemServico
     * @throws SQLException caso não seja possivel acessar algum item do rSet
     */
    private List<ItemOrdemServico> createItensOs (ResultSet rSet) throws SQLException{
        List<ItemOrdemServico> itens = new ArrayList<>();
        ItemOrdemServico item = null;
        PerguntaRespostaChecklist pergunta = null;
        AlternativaChecklist alternativa = null;
        List<AlternativaChecklist> alternativas = null;
        Colaborador mecanico = null;
        try{
            while (rSet.next()){
                item = new ItemOrdemServico();
                item.setCodOs(rSet.getLong("cod_os"));
                pergunta = createPergunta(rSet);
                alternativa = createAlternativa(rSet);
                alternativas = new ArrayList<>();
                alternativas.add(alternativa);
                pergunta.setAlternativasResposta(alternativas);
                item.setPergunta(pergunta);
                item.setPlaca(rSet.getString("placa_veiculo"));
                item.setDataApontamento(rSet.getTimestamp("data_hora"));
                item.setTempoLimiteResolucao(Duration.ofHours(rSet.getLong("PRAZO")));
                setTempoRestante(item, rSet.getInt("prazo"));
                item.setQtdApontamentos(rSet.getInt("qt_apontamentos"));
                item.setStatus(ItemOrdemServico.Status.fromString(rSet.getString("status_item")));
                if (rSet.getString("nome_mecanico")!= null){
                    mecanico = new Colaborador();
                    mecanico.setCpf(rSet.getLong("cpf_mecanico"));
                    mecanico.setNome(rSet.getString("nome_mecanico"));
                    item.setMecanico(mecanico);
                    item.setTempoRealizacaoConserto(Duration.ofMillis(rSet.getLong("tempo_realizacao")));
                    item.setKmVeiculoFechamento(rSet.getLong("km_fechamento"));
                    item.setDataHoraConserto(rSet.getTimestamp("data_hora_conserto"));
                    item.setFeedbackResolucao(rSet.getString("feedback_conserto"));
                }
                itens.add(item);
            }
        }finally {
            closeConnection(null, null, rSet);
        }
        return itens;
    }

    /**
     * Cria a ordem de serviço
     * @param rSet um ResultSet contendo os dados da OS
     * @return uma OrdemServico com os atributos setados
     * @throws SQLException caso não seja possível realizar as consultas no rSet
     */
    private OrdemServico createOrdemServico(ResultSet rSet) throws SQLException{
        OrdemServico os = new OrdemServico();
        os.setCodChecklist(rSet.getLong("cod_checklist"));
        os.setCodigo(rSet.getLong("cod_os"));
        os.setStatus(OrdemServico.Status.fromString(rSet.getString("status")));
        Veiculo v = new Veiculo();
        v.setKmAtual(rSet.getLong("km"));
        v.setPlaca(rSet.getString("placa_veiculo"));
        v.setAtivo(true);
        os.setVeiculo(v);
        os.setDataAbertura(rSet.getTimestamp("data_hora"));
        os.setDataFechamento(rSet.getTimestamp("data_hora_fechamento"));
        return os;
    }

    private PerguntaRespostaChecklist createPergunta(ResultSet rSet) throws SQLException{
        PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
        pergunta.setCodigo(rSet.getLong("COD_PERGUNTA"));
        pergunta.setOrdemExibicao(rSet.getInt("ORDEM_PERGUNTA"));
        pergunta.setPergunta(rSet.getString("PERGUNTA"));
        pergunta.setSingleChoice(rSet.getBoolean("SINGLE_CHOICE"));
        pergunta.setUrl(rSet.getString("URL_IMAGEM"));
        pergunta.setPrioridade(rSet.getString("PRIORIDADE"));
        return pergunta;
    }

    private AlternativaChecklist createAlternativa(ResultSet rSet) throws SQLException{
        AlternativaChecklist alternativa = new AlternativaChecklist();
        alternativa.codigo = rSet.getLong("COD_ALTERNATIVA");
        alternativa.alternativa = rSet.getString("ALTERNATIVA");
        if(alternativa.alternativa.equals("Outros")){
            alternativa.tipo = AlternativaChecklist.TIPO_OUTROS;
            alternativa.respostaOutros = rSet.getString("resposta");
        }
        return alternativa;
    }

    /**
     * Verifica se o item com problema já consta em alguma OS, caso já exista, retorna o código dessa OS
     * @param codPergunta um cod
     * @param codAlternativa um cod
     * @param oss Todas as OS em aberto de uma placa
     * @return Long com o código da OS no qual o item se encontra em aberto
     */
    private Long jaPossuiItemEmAberto(Long codPergunta, long codAlternativa, List<OrdemServico> oss){
        Log.d("verificando se possui item em aberto", "Pergunta: " + codPergunta + "Alternativa: " + codAlternativa);
        for (OrdemServico os:oss) {
            for (ItemOrdemServico item:os.getItens()) {
                for (Alternativa alternativa: item.getPergunta().getAlternativasResposta()) {
                    if (item.getPergunta().getCodigo().equals(codPergunta) && alternativa.codigo == codAlternativa && alternativa.tipo != Alternativa.TIPO_OUTROS
                            && item.getStatus().asString().equals(ItemOrdemServico.Status.PENDENTE.asString())){
                        return os.getCodigo();
                    }
                }
            }
        }
        return null;
    }

    private void updateStatusOs (String placa, Long codOs, Connection conn) throws SQLException{
        PreparedStatement stmt = null;
        try{
            stmt = conn.prepareStatement("UPDATE checklist_ordem_servico SET status = ?, DATA_HORA_FECHAMENTO = ? WHERE\n" +
                    "    COD_UNIDADE = (SELECT COD_UNIDADE FROM veiculo WHERE placa = ?) AND CODIGO = ? AND (SELECT count(*) FROM checklist_ordem_servico_itens\n" +
                    "    WHERE COD_UNIDADE = (SELECT COD_UNIDADE FROM veiculo WHERE placa = ?) AND COD_OS = ? AND status_resolucao = ?) = 0");
            stmt.setString(1, OrdemServico.Status.FECHADA.asString());
            stmt.setTimestamp(2, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            stmt.setString(3, placa);
            stmt.setLong(4, codOs);
            stmt.setString(5, placa);
            stmt.setLong(6, codOs);
            stmt.setString(7, ItemOrdemServico.Status.PENDENTE.asString());
            stmt.execute();
        }finally {
            closeConnection(null, stmt, null);
        }
    }

    /**
     * Ordena lista das bolinhas, jogando para cima as placas com maior quantidade de itens críticos
     * @param list
     */
    private void ordenaLista (List<ManutencaoHolder> list){

        Collections.sort(list, new OrdemServicoDaoImpl.CustomComparator());
        Collections.reverse(list);
    }

    private class CustomComparator implements Comparator<ManutencaoHolder> {

        /**
         * Compara primeiro pela pontuação e depois pela devolução em NF, evitando empates
         */
        @Override
        public int compare(ManutencaoHolder o1, ManutencaoHolder o2) {
            Integer valor1 = Double.compare(o1.getQtdCritica(), o2.getQtdCritica());
            if(valor1!=0){
                return valor1;
            }
            Integer valor2 = Double.compare(o1.getQtdAlta(), o2.getQtdAlta());
            if(valor2 != 0){
                return valor2;
            }
            Integer valor3 = Double.compare(o1.getQtdBaixa(), o2.getQtdBaixa());
            return valor3;
        }
    }

}
