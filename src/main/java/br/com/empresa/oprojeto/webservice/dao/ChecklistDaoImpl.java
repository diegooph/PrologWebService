package br.com.empresa.oprojeto.webservice.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.empresa.oprojeto.models.Pergunta;
import br.com.empresa.oprojeto.models.Resposta;
import br.com.empresa.oprojeto.models.checklist.Checklist;
import br.com.empresa.oprojeto.models.checklist.ChecklistSaida;
import br.com.empresa.oprojeto.webservice.dao.interfaces.BaseDao;

public class ChecklistDaoImpl implements BaseDao<Checklist> {
	private Map<Pergunta, Resposta> perguntaRespostaMap = new HashMap<>();

	@Override
	public boolean save(Checklist object) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public boolean delete(Long codigo) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public Checklist getByCod(Long codigo) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public List<Checklist> getAll() throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}
	
	private Checklist createChecklist(ResultSet rSet) throws SQLException {
		Checklist checklist = new ChecklistSaida();
		checklist.setCodigo(rSet.getLong("CODIGO_CHECKLIST"));
		checklist.setCpfColaborador(rSet.getLong("CPF_COLABORADOR"));
		checklist.setData(rSet.getDate("DATA"));
		checklist.setPlacaVeiculo(rSet.getString("PLACA_VEICULO"));
		checklist.setTipo(rSet.getString("TIPO").charAt(0));
		getPerguntaResposta(rSet);
		checklist.setPerguntaRespostaMap(perguntaRespostaMap);
		return checklist;
	}
	
	// TODO: precisa verificar pois apenas é adicionado uma pergunta e resposta
	// ao HashMap. Sendo que serão várias retornadas. Dúvida: como percorrer o
	// ResultSet? As respostas (e perguntas) retornam por linha ou por coluna na consulta após
	// o Join das três tabelas (checklist, checklist_perguntas, checklist_respostas)?
	// É preciso arranjar um jeito de percorrer o ResultSet sem tirar ele da 
	// posição atual! Talvez passar a outro objeto ResultSet. E também, percorrer
	// e adicionar no HashMap todas as perguntas e resposta de um checklist antes
	// de trocar de checklist. Pois a partir da troca ele é adicionado ao List e 
	// perdemos a referência. :/
	private void getPerguntaResposta(ResultSet rSet) throws SQLException {
		Pergunta pergunta = new Pergunta();
		pergunta.setCodigo(rSet.getLong("CODIGO_PERGUNTA"));
		pergunta.setPergunta(rSet.getString("PERGUNTA"));
		Resposta resposta = new Resposta();
		resposta.setCodigo(rSet.getLong("CODIGO_RESPOSTA"));
		resposta.setResposta(rSet.getString("RESPOSTA"));
		perguntaRespostaMap.put(pergunta, resposta);
	}
}
