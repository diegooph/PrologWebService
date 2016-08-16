package br.com.zalf.prolog.webservice.frota.ordemServico;

import br.com.zalf.prolog.models.Alternativa;
import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.models.Veiculo;
import br.com.zalf.prolog.models.checklist.Checklist;
import br.com.zalf.prolog.models.checklist.PerguntaRespostaChecklist;
import br.com.zalf.prolog.models.checklist.os.*;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDaoImpl;
import br.com.zalf.prolog.webservice.util.L;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by jean on 10/08/16.
 */
@SuppressWarnings("Duplicates")
public class OrdemServicoDaoImpl extends DatabaseConnection {

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
            "order by e.placa_veiculo, e.prioridade ;";

    /**
     * Mesma função da query acima, porém essa aplica limit e offset sobre as placas,
     * usada para pegar a lista de Manutencao Holder (tela das bolinhas)
     */
    private static final String BUSCA_ITENS_MANUTENCAO_HOLDER = "select * from estratificacao_os e\n" +
            "            where e.placa_veiculo IN (\n" +
            "            SELECT distinct c.placa_veiculo from \n" +
            "            checklist c \n" +
            "            join checklist_ordem_servico os on c.codigo = os.cod_checklist AND \n" +
            "            c.cod_unidade = os.cod_unidade \n" +
            "            join checklist_ordem_servico_itens cosi on \n" +
            "            os.codigo = cosi.cod_os AND \n" +
            "            os.cod_unidade = cosi.cod_unidade\n" +
            "            join veiculo v on v.placa = c.placa_veiculo\n" +
            "            where cosi.STATUS_RESOLUCAO like ? and c.cod_unidade::text like ?\n" +
            "              and v.placa like ? and v.cod_tipo::text like ?\n" +
            "            limit ? offset ?) \n" +
            "            ORDER BY E.placa_veiculo, e.prioridade";

    /**
     * Visão utilizada como base para as pesquisas.
     */
    private static final String VIEW_ESTRATIFICACAO_OS = "CREATE OR REPLACE VIEW public.estratificacao_os AS  SELECT os.codigo AS cod_os,\n" +
            "    os.cod_unidade,\n" +
            "    os.status AS status_os,\n" +
            "    os.cod_checklist,\n" +
            "    cp.codigo AS cod_pergunta,\n" +
            "    cp.ordem AS ordem_pergunta,\n" +
            "    cp.pergunta,\n" +
            "    cp.single_choice,\n" +
            "    NULL::unknown AS url_imagem,\n" +
            "    cp.prioridade,\n" +
            "    c.placa_veiculo,\n" +
            "    cap.codigo AS cod_alternativa,\n" +
            "    cap.alternativa,\n" +
            "    cr.resposta,\n" +
            "    cosi.status_resolucao AS status_item,\n" +
            "    co.nome AS nome_mecanico,\n" +
            "    cosi.cpf_mecanico,\n" +
            "    c.data_hora,\n" +
            "    ppc.prazo,\n" +
            "    cosi.data_hora_inicio,\n" +
            "    cosi.data_hora_fim,\n" +
            "    cosi.km AS km_fechamento,\n" +
            "    cosi.qt_apontamentos\n" +
            "   FROM (((((((checklist c\n" +
            "     JOIN checklist_ordem_servico os ON (((c.codigo = os.cod_checklist) AND (c.cod_unidade = os.cod_unidade))))\n" +
            "     JOIN checklist_ordem_servico_itens cosi ON (((os.codigo = cosi.cod_os) AND (os.cod_unidade = cosi.cod_unidade))))\n" +
            "     JOIN checklist_perguntas cp ON ((((cp.cod_unidade = os.cod_unidade) AND (cp.codigo = cosi.cod_pergunta)) AND (cp.cod_checklist_modelo = c.cod_checklist_modelo))))\n" +
            "     JOIN prioridade_pergunta_checklist ppc ON (((ppc.prioridade)::text = (cp.prioridade)::text)))\n" +
            "     JOIN checklist_alternativa_pergunta cap ON (((((cap.cod_unidade = cp.cod_unidade) AND (cap.cod_checklist_modelo = cp.cod_checklist_modelo)) AND (cap.cod_pergunta = cp.codigo)) AND (cap.codigo = cosi.cod_alternativa))))\n" +
            "     JOIN checklist_respostas cr ON ((((((c.cod_unidade = cr.cod_unidade) AND (cr.cod_checklist_modelo = c.cod_checklist_modelo)) AND (cr.cod_checklist = c.codigo)) AND (cr.cod_pergunta = cp.codigo)) AND (cr.cod_alternativa = cap.codigo))))\n" +
            "     LEFT JOIN colaborador co ON ((co.cpf = cosi.cpf_mecanico)));";


    /**
     * Cria uma ordem de serviço no banco de dados e retorna o código gerado na criação
     * @param placa
     * @param codChecklist código do checklist que originou a O.S.
     * @param conn
     * @return
     * @throws SQLException
     */
    private Long createOs (String placa, Long codChecklist, Connection conn) throws SQLException {
        L.d("criando OS", "Placa: " + placa + "checklist: " + codChecklist);
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
     * @param placa
     * @param conn
     * @throws SQLException
     */
    private void insertServicoOs(Long codPergunta, Long codAlternativa, Long codOs, String placa, Connection conn) throws SQLException{
        L.d("Inserindo serviço: ", "Pergunta: " + codPergunta + " codAlternativa: " + codAlternativa + " codOs: " + codOs);
        PreparedStatement stmt = null;
        try{
            stmt = conn.prepareStatement("INSERT INTO checklist_ordem_servico_itens(COD_UNIDADE, COD_OS, cod_pergunta, cod_alternativa, status_resolucao)\n" +
                    "VALUES ((SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?),?,?,?,?)");
            stmt.setString(1, placa);
            stmt.setLong(2, codOs);
            stmt.setLong(3, codPergunta);
            stmt.setLong(4, codAlternativa);
            stmt.setString(5, ItemOrdemServico.Status.PENDENTE.asString());
            int count = stmt.executeUpdate();
            if(count == 0){
                throw new SQLException("Erro ao inserir o serviço");
            }
        }finally {
            closeConnection(null, stmt, null);
        }
    }

    /**
     * Incrementa a quantidade de apontamentos de um item, caso ele ainda esteja em aberto e tenha sido
     * inserido em um novo checklist.
     * @param placa
     * @param codOs
     * @param codPergunta
     * @param codAlternativa
     * @param conn
     * @throws SQLException
     */
    private void incrementaQtApontamento(String placa, Long codOs, Long codPergunta, Long codAlternativa, Connection conn) throws SQLException{
        L.d("incrementandoQt", "Placa: " + placa + "codOs: " + codOs + "Pergunta: " + codPergunta + "Alternativa: " + codAlternativa);
        PreparedStatement stmt = null;
        ResultSet rSet = null;
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
            closeConnection(null, stmt, rSet);
        }
    }

    /**
     * Busca todas as OS e seus devidos itens, respeitando os filtros enviados nos parâmetros
     * @param placa uma placa especifica ou '%' para buscar OS de todas as placas
     * @param status status da OS, podendo ser Aberta ou Fechada
     * @param conn
     * @param codUnidade código da unidade a serem buscadas as OS
     * @param tipoVeiculo tipo do veículo ou '%' para todos os tipos
     * @param limit quantidade de OS que deseja retornar
     * @param offset
     * @return
     * @throws SQLException
     */
    public List<OsHolder> getOs(String placa, String status, Connection conn, Long codUnidade,
                                String tipoVeiculo, Integer limit, Long offset) throws SQLException{

        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<OsHolder> holders = new ArrayList<>();
        List<OrdemServico> oss = null;
        OsHolder holder = null;
        OrdemServico os = null;
        try{
            if (conn == null){
                conn = getConnection();
            }
            /**
             * query que busca apenas os dados da OS, e não os itens
             */
            String query = "SELECT cos.codigo as cod_os, cos.cod_checklist, cos.data_hora_fechamento, cos.status, C.placa_veiculo, c.km_veiculo, c.data_hora " +
                    "FROM checklist_ordem_servico cos join checklist c ON cos.cod_checklist = C.codigo\n" +
                    "and c.cod_unidade = cos.cod_unidade\n" +
                    "JOIN VEICULO V ON V.placa = C.placa_veiculo\n" +
                    "JOIN veiculo_tipo VT ON VT.cod_unidade = C.cod_unidade AND v.cod_tipo = vt.codigo\n" +
                    "where c.placa_veiculo LIKE ? and cos.status LIKE ? and c.cod_unidade = ? AND " +
                    "VT.codigo::TEXT LIKE ? \n "  +
                    "ORDER BY C.placa_veiculo\n" +
                    "%s";
            /**
             * limit e offset podem vir null, quando o método é chamado do insertItemOs
             * */
            if (limit != null){
                query = String.format(query, " LIMIT ?  OFFSET ? ");
            }else{
                query = String.format(query, "");
            }
            stmt = conn.prepareStatement(query);
            stmt.setString(1, placa);
            stmt.setString(2, status);
            stmt.setLong(3, codUnidade);
            stmt.setString(4, tipoVeiculo);
            if (limit !=null) {
                stmt.setInt(5, limit);
            }
            if (offset!= null) {
                stmt.setLong(6, offset);
            }
            rSet = stmt.executeQuery();
            while(rSet.next()){
                if (holder == null){//primeiro item do ResultSet
                    oss = new ArrayList<>();
                    holder = new OsHolder();
                    holder.setPlaca(rSet.getString("placa_veiculo"));
                    os = createOrdemServico(rSet);
                    /**
                     * seta os itens da ordem de serviço.
                     */
                    os.setItens(getItensOs(holder.getPlaca(), String.valueOf(os.getCodigo()), "%", conn, codUnidade));
                    oss.add(os);
                }else{ // Próximos itens
                    if (rSet.getString("placa_veiculo").equals(os.getPlaca())){ // caso a placa seja igual ao item anterior, criar nova os e add na lista
                        os = new OrdemServico();
                        os = createOrdemServico(rSet);
                        os.setItens(getItensOs(holder.getPlaca(), String.valueOf(os.getCodigo()), "%", conn, codUnidade));
                        oss.add(os);
                    }else{//placa diferente, fechar a lista, setar no holder, criar novo holder, nova os e add na lista
                        holder.setOs(oss);
                        holders.add(holder);
                        holder = new OsHolder();
                        holder.setPlaca(rSet.getString("placa_veiculo"));
                        os = new OrdemServico();
                        os = createOrdemServico(rSet);
                        oss = new ArrayList<>();
                        os.setItens(getItensOs(holder.getPlaca(), String.valueOf(os.getCodigo()), "%", conn, codUnidade));
                        oss.add(os);
                    }
                }
            }
            if (holder != null) {
                holder.setOs(oss);
                holders.add(holder);
            }
        }finally {
            closeConnection(null, stmt, rSet);
        }
        return holders;
    }

    /**
     * Busca os itens que compõe uma ou mais OS, usdao quando são buscadas as OS (e não o manutencaoHolder que busca só os itens)
     * @param placa Placa da OS, "%" para todas as placas
     * @param codOs Código da OS, "%" para todas as OS
     * @param status Status da OS, "%" para todos os status
     * @param conn
     * @param codUnidade Código da unidade
     * @return
     * @throws SQLException
     */
    public List<ItemOrdemServico> getItensOs(String placa, String codOs, String status, Connection conn, Long codUnidade) throws SQLException{
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
     * Busca dos itens para montar a tela das "bolinhas", são buscados apenas os itens, independente da OS ao qual pertencem
     * @param status Usado para buscar os itens abertos ou fechados
     * @param conn
     * @param codUnidade Código da unidade
     * @param limit Quantidade de PLACAS que serão retornadas
     * @param offset
     * @return
     * @throws SQLException
     */
    public List<ItemOrdemServico> getItensOsManutencaoHolder(String placa, String codTipo, String status, Connection conn,
                                                             Long codUnidade, int limit, long offset) throws SQLException{
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try{
            stmt = conn.prepareStatement(BUSCA_ITENS_MANUTENCAO_HOLDER);
            stmt.setString(1, status);
            stmt.setString(2, String.valueOf(codUnidade));
            stmt.setString(3, placa);
            stmt.setString(4, codTipo);
            stmt.setInt(5, limit);
            stmt.setLong(6, offset);
            rSet = stmt.executeQuery();
            return createItensOs(rSet);
        }finally {
            closeConnection(null, stmt, rSet);
        }
    }

    /**
     * Método genérico para criar os itens de uma OS.
     * @param rSet
     * @return
     * @throws SQLException
     */
    private List<ItemOrdemServico> createItensOs (ResultSet rSet) throws SQLException{
        List<ItemOrdemServico> itens = new ArrayList<>();
        ItemOrdemServico item = null;
        PerguntaRespostaChecklist pergunta = null;
        PerguntaRespostaChecklist.Alternativa alternativa = null;
        List<PerguntaRespostaChecklist.Alternativa> alternativas = null;
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
                item.setTempoLimiteResolucao(createTempo(TimeUnit.HOURS.toMinutes(rSet.getLong("PRAZO"))));
                setTempoRestante(item, rSet.getInt("prazo"));
                item.setQtdApontamentos(rSet.getInt("qt_apontamentos"));
                if (rSet.getString("nome_mecanico")!= null){
                    mecanico = new Colaborador();
                    mecanico.setCpf(rSet.getLong("cpf_mecanico"));
                    mecanico.setNome(rSet.getString("nome_mecanico"));
                    item.setMecanico(mecanico);
                    item.setTempoRealizacaoConsertoInMillis(rSet.getLong("tempo_realizacao"));
                    item.setKmVeiculoFechamento(rSet.getLong("km_fechamento"));
                    item.setStatus(ItemOrdemServico.Status.fromString(rSet.getString("status_item")));
                    item.setDataHoraConserto(rSet.getTimestamp("data_hora_conserto"));
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
     * @param rSet
     * @return
     * @throws SQLException
     */
    private OrdemServico createOrdemServico(ResultSet rSet) throws SQLException{
        OrdemServico os = new OrdemServico();
        os.setCodChecklist(rSet.getLong("cod_checklist"));
        os.setCodigo(rSet.getLong("cod_os"));
        os.setStatus(OrdemServico.Status.fromString(rSet.getString("status")));
        os.setKmVeiculo(rSet.getLong("km_veiculo"));
        //os.setPlaca(rSet.getString("placa_veiculo"));
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

    private PerguntaRespostaChecklist.Alternativa createAlternativa(ResultSet rSet) throws SQLException{
        PerguntaRespostaChecklist.Alternativa alternativa = new PerguntaRespostaChecklist.Alternativa();
        alternativa.codigo = rSet.getLong("COD_ALTERNATIVA");
        alternativa.alternativa = rSet.getString("ALTERNATIVA");
        if(alternativa.alternativa.equals("Outros")){
            alternativa.tipo = PerguntaRespostaChecklist.Alternativa.TIPO_OUTROS;
            alternativa.respostaOutros = rSet.getString("resposta");
        }
        return alternativa;
    }

    /**
     * Método chamado quando é recebido um checklist, verifica as premissas para criar uma nova OS ou add
     * o item com problema a uma OS existente
     * @param checklist Um checklist
     * @param conn
     * @param codUnidade Código da unidade que gerou o checklist
     * @throws SQLException
     */
    public void insertItemOs(Checklist checklist, Connection conn, Long codUnidade) throws SQLException{
        Long tempCodOs = null;
        Long gerouOs = null;
        // vem apenas um holder, ja que a busca foi feita apenas para uma placa
        List<OsHolder> oss = getOs(checklist.getPlacaVeiculo(), OrdemServico.Status.ABERTA.asString(), conn, codUnidade, "%", null, null);
        // todas as os de uma unica placa
        List<OrdemServico> ordens = null;
        if (oss != null) {
            ordens = oss.get(0).getOs();
            L.d("ordens", ordens.toString());
        }
        for (PerguntaRespostaChecklist pergunta: checklist.getListRespostas()) { //verifica cada pergunta do checklist
            L.d("Pergunta", pergunta.getCodigo().toString());
            for (PerguntaRespostaChecklist.Alternativa alternativa: pergunta.getAlternativasResposta()) { // varre cada alternativa de uma pergunta
                L.d("Verificando Alternativa:", String.valueOf(alternativa.codigo));
                if (alternativa.selected) {
                    L.d("Alternativa esta elecionada", String.valueOf(alternativa.codigo));
                    if (ordens != null) {//verifica se ja tem algum item em aberto
                        tempCodOs = jaPossuiItemEmAberto(pergunta.getCodigo(), alternativa.codigo, ordens);
                        if (tempCodOs != null) {
                            L.d("tempCodOs", tempCodOs.toString());
                        }
                    }
                    if (tempCodOs != null) {
                        incrementaQtApontamento(checklist.getPlacaVeiculo(), tempCodOs, pergunta.getCodigo(), alternativa.codigo, conn);
                        L.d("incrementa", "chamou metodo para incrementar a qt de apontamentos");
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
     * Verifica se o item com problema já consta em alguma OS, caso já exista, retorna o código dessa OS
     * @param codPergunta
     * @param codAlternativa
     * @param oss Todas as OS em aberto de uma placa
     * @return
     */
    private Long jaPossuiItemEmAberto(Long codPergunta, Long codAlternativa, List<OrdemServico> oss){
        L.d("verificando se possui item em aberto", "Pergunta: " + codPergunta + "Alternativa: " + codAlternativa);
        for (OrdemServico os:oss) {
            for (ItemOrdemServico item:os.getItens()) {
                for (Alternativa alternativa: item.getPergunta().getAlternativasResposta()) {
                    if (item.getPergunta().getCodigo().equals(codPergunta) && alternativa.codigo == codAlternativa && !alternativa.alternativa.equals("Outros")){
                        L.d("item existe", "item existe na lista");
                        return os.getCodigo();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Busca a lista de itens agrupadas por placa e criticidade (tela das bolinhas)
     * @param codUnidade Código da unidade
     * @param limit Quantidade de placas
     * @param offset
     * @param status
     * @return
     * @throws SQLException
     */
    public List<ManutencaoHolder> getManutencaoHolder (String placa, String codTipo, Long codUnidade, int limit,
                                                       long offset, String status) throws SQLException{
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<ManutencaoHolder> holders = new ArrayList<>();
        ManutencaoHolder holder = null;
        List<ItemOrdemServico> itens = null;
        Veiculo v = null;
        try{
            conn = getConnection();
            itens = getItensOsManutencaoHolder(placa, codTipo,status, conn, codUnidade, limit, offset);
            if (itens!=null) {
                for (ItemOrdemServico item : itens) {
                    if (holder == null){//primeiro item
                        holder = new ManutencaoHolder();
                        v = new Veiculo();
                        v.setPlaca(item.getPlaca());
                        holder.setVeiculo(v);
                        itens = new ArrayList<>();
                        itens.add(item);
                    }else{// a partir da segunda linha da lista de itens
                        if (holder.getVeiculo().getPlaca().equals(item.getPlaca())) {//mesma placa, add o item ao mesmo holder
                            itens.add(item);
                        }else{ // item é de placa diferente, fechar e add o holder na lista geral
                            holder.setListManutencao(itens);
                            setQtItens(holder);
                            holders.add(holder);
                            holder = new ManutencaoHolder();
                            v = new Veiculo();
                            v.setPlaca(item.getPlaca());
                            holder.setVeiculo(v);
                            itens = new ArrayList<>();
                            itens.add(item);
                        }
                    }
                }
                if(holder!=null) {
                    holder.setListManutencao(itens);
                    setQtItens(holder);
                    holders.add(holder);
                    setKmVeiculos(holders,placa,codTipo,codUnidade);
                    ordenaLista(holders);
                }
            }
        }finally {
            closeConnection(conn,stmt,rSet);
        }
        return holders;
    }

    private void setKmVeiculos(List<ManutencaoHolder> holders, String placa, String codTipo, Long codUnidade) throws SQLException{
        VeiculoDaoImpl veiculoDao = new VeiculoDaoImpl();
        List<Veiculo> veiculos = veiculoDao.getVeiculoKm(codUnidade, placa, codTipo);

        for (int i = 0; i < holders.size(); i++) {
            L.d("holder:", holders.get(i).getVeiculo().getPlaca());
            for (int j = 0; j < veiculos.size(); j++) {
                L.d("veiculo:", veiculos.get(j).getPlaca());
                if(holders.get(i).getVeiculo().getPlaca().equals(veiculos.get(j).getPlaca())){
                    holders.get(i).getVeiculo().setKmAtual(veiculos.get(j).getKmAtual());
                    veiculos.remove(j);
                    j--;
                }
            }
        }
//        for (int i = 0; i < veiculos.size(); i++) {
//            for (ManutencaoHolder holder:holders) {
//                L.d("comparando o holder de placa:", holder.getVeiculo().getPlaca());
//                if (veiculos.get(i).getPlaca().equals(holder.getVeiculo().getPlaca())){
//                    holder.getVeiculo().setKmAtual(veiculos.get(i).getKmAtual());
//                    veiculos.remove(i);
//                    i--;
//                }
//            }
//        }
    }

    /**
     * Atribui o tempo restante para conserto de um item
     * @param itemManutencao
     * @param prazoHoras
     */
    public void setTempoRestante(ItemOrdemServico itemManutencao, int prazoHoras) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(itemManutencao.getDataApontamento());
        calendar.add(Calendar.HOUR, prazoHoras);// data apontamento + prazo
        Date dataMaxima = calendar.getTime(); // data máxima de resolução
        long tempoRestante = dataMaxima.getTime() - System.currentTimeMillis();
        itemManutencao.setTempoRestante(createTempo(TimeUnit.MILLISECONDS.toMinutes(tempoRestante)));
    }

    /**
     * Cria o objeto Tempo, transforma uma quantidade de horas (prazo) em dias, horas e minutos
     * @param temp
     * @return
     */
    private Tempo createTempo(long temp){
        Tempo tempo = new Tempo();
        if (temp < MINUTOS_NUMA_HORA) {
            tempo.setMinuto((int)temp);
        } else if (temp < MINUTOS_NUM_DIA) {
            long hours = TimeUnit.MINUTES.toHours(temp);
            temp = hours % 60;
            tempo.setHora((int)hours);
            tempo.setMinuto((int)temp);
        } else if (temp >= MINUTOS_NUM_DIA) {
            long days = TimeUnit.MINUTES.toDays(temp);
            long hours = TimeUnit.MINUTES.toHours(temp) % 24;
            tempo.setDia((int)days);
            tempo.setHora((int)hours);;
        }
        return tempo;
    }

    /**
     * Faz a contagem de acordo com a prioridade de cada item
     * @param holder
     */
    private void setQtItens(ManutencaoHolder holder){
        for(ItemOrdemServico item : holder.getListManutencao()){
            switch (item.getPergunta().getPrioridade()) {
                case PRIORIDADE_CRITICA:
                    holder.setQtdCritica(holder.getQtdCritica() + 1);;
                    break;
                case PRIORIDADE_ALTA:
                    holder.setQtdAlta(holder.getQtdAlta() + 1);;
                    break;
                case PRIORIDADE_BAIXA:
                    holder.setQtdBaixa(holder.getQtdBaixa() + 1);;
                    break;
                default:
                    break;
            }
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

    public boolean consertaItem (Long codUnidade,ItemOrdemServico item) throws SQLException{
        Connection conn = null;
        PreparedStatement stmt = null;
        try{
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE CHECKLIST_ORDEM_SERVICO_ITENS SET " +
                    "CPF_MECANICO = ?, TEMPO_REALIZACAO = ?, KM = ?, STATUS_RESOLUCAO = ?" +
                    "WHERE COD_UNIDADE = ? AND COD_OS = ? AND COD_PERGUNTA = ? AND " +
                    "COD_ALTERNATIVA = ?");
            stmt.setLong(1, item.getMecanico().getCpf());
            stmt.setLong(2, item.getTempoRealizacaoConsertoInMillis());
            stmt.setLong(3, item.getKmVeiculoFechamento());
            stmt.setString(4, ItemOrdemServico.Status.RESOLVIDO.asString());
            stmt.setLong(5, codUnidade);
            stmt.setLong(6, item.getCodOs());
            stmt.setLong(7, item.getPergunta().getCodigo());
            stmt.setLong(8, item.getPergunta().getAlternativasResposta().get(0).codigo);
            int count = stmt.executeUpdate();
            if (count > 0){
                updateStatusOs(codUnidade, item.getCodOs(), conn);
                return true;
            }
        }finally {
            closeConnection(conn,stmt,null);
        }
        return false;
    }

    private void updateStatusOs (Long codUnidade, Long codOs, Connection conn) throws SQLException{
        PreparedStatement stmt = null;
        try{
            stmt = conn.prepareStatement("UPDATE checklist_ordem_servico SET status = ?, DATA_HORA_FECHAMENTO = ? WHERE\n" +
                    "    COD_UNIDADE = ? AND CODIGO = ? AND (SELECT count(*) FROM checklist_ordem_servico_itens\n" +
                    "    WHERE COD_UNIDADE = ? AND COD_OS = ? AND status_resolucao = ?) = 0");
            stmt.setString(1, OrdemServico.Status.FECHADA.asString());
            stmt.setTimestamp(2, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            stmt.setLong(3, codUnidade);
            stmt.setLong(4, codOs);
            stmt.setLong(5, codUnidade);
            stmt.setLong(6, codOs);
            stmt.setString(7, ItemOrdemServico.Status.PENDENTE.asString());
            stmt.execute();
        }finally {
            closeConnection(null, stmt, null);
        }
    }
}
