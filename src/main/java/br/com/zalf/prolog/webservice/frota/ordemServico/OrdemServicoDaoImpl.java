package br.com.zalf.prolog.webservice.frota.ordemServico;

import br.com.zalf.prolog.models.Alternativa;
import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.models.checklist.Checklist;
import br.com.zalf.prolog.models.checklist.PerguntaRespostaChecklist;
import br.com.zalf.prolog.models.checklist.os.*;
import br.com.zalf.prolog.webservice.DatabaseConnection;
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
public class OrdemServicoDaoImpl extends DatabaseConnection {

    private static final String PRIORIDADE_CRITICA = "CRITICA";
    private static final String PRIORIDADE_ALTA = "ALTA";
    private static final String PRIORIDADE_BAIXA = "BAIXA";

    private static final int MINUTOS_NUM_DIA = 1440;
    private static final int MINUTOS_NUMA_HORA = 60;

    private static final String BUSCA_ITENS_OS = "select * from estratificacao_os e\n" +
            "where  e.cod_os::TEXT LIKE ? and e.cod_unidade::TEXT LIKE ? and e.placa_veiculo like ? " +
            "and e.status_item LIKE ?\n" +
            "order by e.placa_veiculo;";

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

    private void incrementaQtApontamento2(String placa, Long codOs, Long codPergunta, Long codAlternativa, Connection conn) throws SQLException{
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

    public List<OsHolder> getResumoOs(String placa, String status, Connection conn, Long codUnidade, String tipoVeiculo, Integer limit, Long offset) throws SQLException{

        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<OsHolder> holders = null;
        List<OrdemServico> oss = null;
        OsHolder holder = null;
        OrdemServico os = null;
        try{
            if (conn == null){
                conn = getConnection();
            }
            String query = "SELECT cos.codigo as cod_os, cos.cod_checklist, cos.status, C.placa_veiculo, c.km_veiculo, c.data_hora " +
                    "FROM checklist_ordem_servico cos join checklist c ON cos.cod_checklist = C.codigo\n" +
                    "and c.cod_unidade = cos.cod_unidade\n" +
                    "JOIN VEICULO V ON V.placa = C.placa_veiculo\n" +
                    "JOIN veiculo_tipo VT ON VT.cod_unidade = C.cod_unidade AND v.cod_tipo = vt.codigo\n" +
                    "where c.placa_veiculo LIKE ? and cos.status LIKE ? and c.cod_unidade = ? AND " +
                    "VT.codigo::TEXT LIKE ? \n "  +
                    "ORDER BY C.placa_veiculo\n" +
                    "%s";
            if (limit != null){
                query = String.format(query, " LIMIT = ?  OFFSET ? ");
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
                    holders = new ArrayList<>();
                    oss = new ArrayList<>();
                    holder = new OsHolder();
                    os = createOrdemServico(rSet);
                    os.setItens(getItensOs(os.getPlaca(), String.valueOf(os.getCodigo()), ItemOrdemServico.Status.PENDENTE.asString(), conn, codUnidade));
                    oss.add(os);
                }else{ // Próximos itens
                    if (rSet.getString("placa_veiculo").equals(os.getPlaca())){ // caso a placa seja igual ao item anterior, criar nova os e add na lista
                        os = new OrdemServico();
                        os = createOrdemServico(rSet);
                        os.setItens(getItensOs(os.getPlaca(), String.valueOf(os.getCodigo()), ItemOrdemServico.Status.PENDENTE.asString(), conn, codUnidade));
                        oss.add(os);
                    }else{//placa diferente, fechar a lista, setar no holder, criar novo holder, nova os e add na lista
                        holder.setOs(oss);
                        holders.add(holder);
                        holder = new OsHolder();
                        os = new OrdemServico();
                        os = createOrdemServico(rSet);
                        oss = new ArrayList<>();
                        os.setItens(getItensOs(os.getPlaca(), String.valueOf(os.getCodigo()), "%", conn, codUnidade));
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

    public List<ItemOrdemServico> getItensOs(String placa, String codOs, String status, Connection conn, Long codUnidade) throws SQLException{
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<ItemOrdemServico> itens = new ArrayList<>();
        ItemOrdemServico item = null;
        PerguntaRespostaChecklist pergunta = null;
        PerguntaRespostaChecklist.Alternativa alternativa = null;
        List<PerguntaRespostaChecklist.Alternativa> alternativas = null;
        Colaborador mecanico = null;
        try{
            stmt = conn.prepareStatement(BUSCA_ITENS_OS);
            stmt.setString(1, String.valueOf(codOs));
            stmt.setString(2, String.valueOf(codUnidade));
            stmt.setString(3, placa);
            stmt.setString(4, status);
            rSet = stmt.executeQuery();
            while (rSet.next()){
                item = new ItemOrdemServico();
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
                if (rSet.getString("nome_mecanico")!= null){
                    mecanico = new Colaborador();
                    mecanico.setCpf(rSet.getLong("cpf_mecanico"));
                    mecanico.setNome(rSet.getString("nome_mecanico"));
                    item.setMecanico(mecanico);
                    item.setDataHoraInicioConserto(rSet.getTimestamp("data_hora_inicio"));
                    item.setDataHoraTerminoConserto(rSet.getTimestamp("data_hora_fim"));
                    item.setKmVeiculoFechamento(rSet.getLong("km_fechamento"));
                    item.setStatus(ItemOrdemServico.Status.fromString(rSet.getString("status_item")));
                }
                itens.add(item);
            }
        }finally {
            closeConnection(null, stmt, rSet);
        }
        return itens;
    }

    private OrdemServico createOrdemServico(ResultSet rSet) throws SQLException{
        OrdemServico os = new OrdemServico();
        os.setCodChecklist(rSet.getLong("cod_checklist"));
        os.setCodigo(rSet.getLong("cod_os"));
        os.setStatus(OrdemServico.Status.fromString(rSet.getString("status")));
        os.setKmVeiculo(rSet.getLong("km_veiculo"));
        os.setPlaca(rSet.getString("placa_veiculo"));
        os.setDataAbertura(rSet.getTimestamp("data_hora"));
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
            try{
                alternativa.respostaOutros = rSet.getString("resposta");
            }catch (SQLException e){}
        }
        return alternativa;
    }

    public void insertItemOs(Checklist checklist, Connection conn, Long codUnidade) throws SQLException{
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Long tempCodOs = null;
        Long gerouOs = null;
        // vem apenas um holder, ja que a busca foi feita apenas para uma placa
        List<OsHolder> oss = getResumoOs(checklist.getPlacaVeiculo(), OrdemServico.Status.ABERTA.asString(), conn, codUnidade, "%", null, null);
        // todas as os de uma unica placa
        List<OrdemServico> ordens = null;
        if (oss != null) {
            ordens = oss.get(0).getOs();
            L.d("ordens", ordens.toString());
        }
        try{
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
                            incrementaQtApontamento2(checklist.getPlacaVeiculo(), tempCodOs, pergunta.getCodigo(), alternativa.codigo, conn);
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
        }finally{
            closeConnection(null, stmt, null);
        }
    }

    private Long jaPossuiItemEmAberto(Long codPergunta, Long codAlternativa, List<OrdemServico> oss){
        L.d("verificando se possui item em aberto", "Pergunta: " + codPergunta + "Alternativa: " + codAlternativa);
        for (OrdemServico os:oss) {
            for (ItemOrdemServico item:os.getItens()) {
                for (Alternativa alternativa: item.getPergunta().getAlternativasResposta()) {
                    if (item.getPergunta().getCodigo().equals(codPergunta) && alternativa.codigo == codAlternativa){
                        L.d("item existe", "item existe na lista");
                        return os.getCodigo();
                    }
                }
            }
        }
        return null;
    }

    public List<ManutencaoHolder> getManutencaoHolder (Long codUnidade, int limit, long offset, String status) throws SQLException{
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<ManutencaoHolder> holders = null;
        ManutencaoHolder holder = null;
        List<ItemOrdemServico> itens = null;

        try{
            conn = getConnection();
            itens = getItensOs("%", "%", status, conn, codUnidade);
            if (itens!=null) {
                for (ItemOrdemServico item : itens) {
                    if (holder == null){//primeiro item
                        holders = new ArrayList<>();
                        holder = new ManutencaoHolder();
                        holder.setPlaca(item.getPlaca());
                        itens = new ArrayList<>();
                        itens.add(item);
                    }else{// a partir da segunda linha da lista de itens
                        if (holder.getPlaca().equals(item.getPlaca())) {//mesma placa, add o item ao mesmo holder
                            itens.add(item);
                        }else{ // item é de placa diferente, fechar e add o holder na lista geral
                            holder.setListManutencao(itens);
                            setQtItens(holder);
                            holders.add(holder);
                            holder = new ManutencaoHolder();
                            holder.setPlaca(item.getPlaca());
                            itens = new ArrayList<>();
                            itens.add(item);
                        }
                    }
                }
                holders.add(holder);
            }
        }finally {
            closeConnection(conn,stmt,rSet);
        }
        ordenaLista(holders);
        return holders;
    }

    public void setTempoRestante(ItemOrdemServico itemManutencao, int prazoHoras) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(itemManutencao.getDataApontamento());
        calendar.add(Calendar.HOUR, prazoHoras);// data apontamento + prazo
        Date dataMaxima = calendar.getTime(); // data máxima de resolução
        long tempoRestante = dataMaxima.getTime() - System.currentTimeMillis();
        itemManutencao.setTempoRestante(createTempo(TimeUnit.MILLISECONDS.toMinutes(tempoRestante)));
    }

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
