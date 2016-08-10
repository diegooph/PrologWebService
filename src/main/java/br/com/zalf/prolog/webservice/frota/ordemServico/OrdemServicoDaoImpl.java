package br.com.zalf.prolog.webservice.frota.ordemServico;

import br.com.zalf.prolog.models.Alternativa;
import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.models.checklist.Checklist;
import br.com.zalf.prolog.models.checklist.PerguntaRespostaChecklist;
import br.com.zalf.prolog.models.checklist.os.ItemOrdemServico;
import br.com.zalf.prolog.models.checklist.os.OrdemServico;
import br.com.zalf.prolog.models.checklist.os.OsHolder;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.util.L;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jean on 10/08/16.
 */
public class OrdemServicoDaoImpl extends DatabaseConnection {

    private static final String BUSCA_ITENS_OS = "select os.codigo AS COD_OS, os.status as status_os, os.cod_checklist, cp.codigo as cod_pergunta, cp.ordem as ordem_pergunta, cp.pergunta,\n" +
            "  cp.single_choice, null as url_imagem, cp.prioridade,\n" +
            "  cap.codigo as cod_alternativa, cap.alternativa, cr.resposta, cosi.status_resolucao as status_item, co.nome as nome_mecanico," +
            " cosi.cpf_mecanico, " +
            " cosi.data_hora_inicio, cosi.data_hora_fim, cosi.km as km_fechamento from\n" +
            "  checklist c join checklist_ordem_servico os\n" +
            "      on c.codigo = os.cod_checklist AND\n" +
            "      c.cod_unidade = os.cod_unidade\n" +
            "  join checklist_ordem_servico_itens cosi on\n" +
            "      os.codigo = cosi.cod_os AND\n" +
            "      os.cod_unidade = cosi.cod_unidade\n" +
            "  join checklist_perguntas cp on cp.cod_unidade = os.cod_unidade AND\n" +
            "    cp.codigo = cosi.cod_pergunta AND\n" +
            "    cp.cod_checklist_modelo = c.cod_checklist_modelo\n" +
            "  join checklist_alternativa_pergunta cap on cap.cod_unidade = cp.cod_unidade AND\n" +
            "    cap.cod_checklist_modelo = cp.cod_checklist_modelo AND\n" +
            "    cap.cod_pergunta = cp.codigo AND\n" +
            "    cap.codigo = cosi.cod_alternativa\n" +
            "  join checklist_respostas cr on c.cod_unidade = cr.cod_unidade AND\n" +
            "    cr.cod_checklist_modelo = c.cod_checklist_modelo AND\n" +
            "    cr.cod_checklist = c.codigo AND\n" +
            "    cr.cod_pergunta = cp.codigo AND\n" +
            "    cr.cod_alternativa = cap.codigo\n" +
            "    LEFT JOIN COLABORADOR Co ON Co.CPF = COSI.CPF_MECANICO\n" +
            "where os.codigo = ? and os.cod_unidade = (SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?) and cosi.status_resolucao LIKE ? \n" +
            "order by os.codigo, cp.codigo, cap.codigo";

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

    public List<OsHolder> getResumoOs(String placa, String status, Connection conn, Long codUnidade, String tipoVeiculo) throws SQLException{
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<OsHolder> holders = null;
        List<OrdemServico> oss = null;
        OsHolder holder = null;
        OrdemServico os = null;
        try{
            stmt = conn.prepareStatement("SELECT cos.codigo as cod_os, cos.cod_checklist, cos.status, C.placa_veiculo, c.km_veiculo, c.data_hora " +
                    "FROM checklist_ordem_servico cos join checklist c ON cos.cod_checklist = C.codigo\n" +
                    "and c.cod_unidade = cos.cod_unidade\n" +
                    "JOIN VEICULO V ON V.placa = C.placa_veiculo\n" +
                    "JOIN veiculo_tipo VT ON VT.cod_unidade = C.cod_unidade AND v.cod_tipo = vt.codigo\n" +
                    "where c.placa_veiculo LIKE ? and cos.status LIKE ? and c.cod_unidade = ? AND " +
                    "VT.codigo::TEXT LIKE ? \n" +
                    "ORDER BY C.placa_veiculo");
            stmt.setString(1, placa);
            stmt.setString(2, status);
            stmt.setLong(3, codUnidade);
            stmt.setString(4, tipoVeiculo);
            rSet = stmt.executeQuery();
            while(rSet.next()){
                if (holder == null){//primeiro item do ResultSet
                    holders = new ArrayList<>();
                    oss = new ArrayList<>();
                    holder = new OsHolder();
                    os = createOrdemServico(rSet);
                    os.setItens(getItensOs(os.getPlaca(), os.getCodigo(), ItemOrdemServico.Status.PENDENTE.asString(), conn));
                    oss.add(os);
                }else{ // Próximos itens
                    if (rSet.getString("placa_veiculo").equals(os.getPlaca())){ // caso a placa seja igual ao item anterior, criar nova os e add na lista
                        os = new OrdemServico();
                        os = createOrdemServico(rSet);
                        os.setItens(getItensOs(os.getPlaca(), os.getCodigo(), ItemOrdemServico.Status.PENDENTE.asString(), conn));
                        oss.add(os);
                    }else{//placa diferente, fechar a lista, setar no holder, criar novo holder, nova os e add na lista
                        holder.setOs(oss);
                        holders.add(holder);
                        holder = new OsHolder();
                        os = new OrdemServico();
                        os = createOrdemServico(rSet);
                        oss = new ArrayList<>();
                        os.setItens(getItensOs(os.getPlaca(), os.getCodigo(), "%", conn));
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

    public List<ItemOrdemServico> getItensOs(String placa, Long codOs, String status, Connection conn) throws SQLException{
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
            stmt.setLong(1, codOs);
            stmt.setString(2, placa);
            stmt.setString(3, status);
            rSet = stmt.executeQuery();
            while (rSet.next()){
                item = new ItemOrdemServico();
                pergunta = createPergunta(rSet);
                alternativa = createAlternativa(rSet);
                alternativas = new ArrayList<>();
                alternativas.add(alternativa);
                pergunta.setAlternativasResposta(alternativas);
                item.setPergunta(pergunta);
                if (rSet.getString("nome_mecanico")!= null){
                    mecanico = new Colaborador();
                    mecanico.setCpf(rSet.getLong("cpf_mecanico"));
                    mecanico.setNome(rSet.getString("nome_mecanico"));
                    item.setMecanico(mecanico);
                    item.setDataHoraInicio(rSet.getTimestamp("data_hora_inicio"));
                    item.setDataHoraTermino(rSet.getTimestamp("data_hora_fim"));
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
        List<OsHolder> oss = getResumoOs(checklist.getPlacaVeiculo(), OrdemServico.Status.ABERTA.asString(), conn, codUnidade, "%");
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

    private List<PerguntaRespostaChecklist> getOs(String placa, Connection conn) throws SQLException{
        List<PerguntaRespostaChecklist> itensAbertos = new ArrayList<>();
        PerguntaRespostaChecklist pergunta = null;
        List<PerguntaRespostaChecklist.Alternativa> alternativas = null;
        PerguntaRespostaChecklist.Alternativa alternativa = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try{
            stmt = conn.prepareStatement("SELECT I.cod_pergunta,  I.cod_alternativa, cp.pergunta, cap.alternativa FROM checklist C\n" +
                    "  JOIN checklist_ordem_servico OS on c.cod_unidade = os.cod_unidade AND C.CODIGO = OS.COD_CHECKLIST\n" +
                    "  JOIN checklist_ordem_servico_itens I ON I.COD_OS = OS.CODIGO AND I.cod_unidade = OS.COD_UNIDADE\n" +
                    "  JOIN checklist_perguntas cp on cp.cod_checklist_modelo = c.cod_checklist_modelo\n" +
                    "   and cp.cod_unidade = c.cod_unidade\n" +
                    "   and cp.codigo = i.cod_pergunta\n" +
                    "  JOIN checklist_alternativa_pergunta cap on cap.cod_unidade = i.cod_unidade\n" +
                    "    and cap.cod_checklist_modelo = c.cod_checklist_modelo\n" +
                    "    and cap.cod_pergunta = i.cod_pergunta\n" +
                    "    and cap.codigo = i.cod_alternativa\n" +
                    "WHERE C.PLACA_VEICULO = ? AND OS.STATUS = ? AND I.status_resolucao = ?;");
            stmt.setString(1,placa);
            stmt.setString(2, OrdemServico.Status.ABERTA.asString());
            stmt.setString(3, ItemOrdemServico.Status.PENDENTE.asString());
            rSet = stmt.executeQuery();
            if(rSet.next()){
                if (pergunta == null){
                    pergunta = new PerguntaRespostaChecklist();
                    pergunta.setCodigo(rSet.getLong("cod_pergunta"));
                    pergunta.setPergunta(rSet.getString("pergunta"));
                    alternativa = new PerguntaRespostaChecklist.Alternativa();
                    alternativa.codigo = rSet.getLong("cod_alternativa");
                    alternativa.alternativa = rSet.getString("alternativa");
                    alternativas = new ArrayList<>();
                    alternativas.add(alternativa);
                }else{
                    if (rSet.getLong("cod_pergunta") == pergunta.getCodigo()){
                        alternativa = new PerguntaRespostaChecklist.Alternativa();
                        alternativa.codigo = rSet.getLong("cod_alternativa");
                        alternativa.alternativa = rSet.getString("alternativa");
                        alternativas.add(alternativa);
                    }else{
                        pergunta.setAlternativasResposta(alternativas);
                        itensAbertos.add(pergunta);
                        pergunta = new PerguntaRespostaChecklist();
                        pergunta.setCodigo(rSet.getLong("cod_pergunta"));
                        pergunta.setPergunta(rSet.getString("pergunta"));
                        alternativa = new PerguntaRespostaChecklist.Alternativa();
                        alternativa.codigo = rSet.getLong("cod_alternativa");
                        alternativa.alternativa = rSet.getString("alternativa");
                        alternativas = new ArrayList<>();
                        alternativas.add(alternativa);
                    }
                }
            }
            pergunta.setAlternativasResposta(alternativas);
            itensAbertos.add(pergunta);
        }finally {
            closeConnection(null, stmt, rSet);
        }
        return itensAbertos;
    }
}
