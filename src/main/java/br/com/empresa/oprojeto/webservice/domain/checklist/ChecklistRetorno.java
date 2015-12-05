package br.com.empresa.oprojeto.webservice.domain.checklist;


import java.util.Date;
import java.util.Map;

import br.com.empresa.oprojeto.webservice.domain.Pergunta;
import br.com.empresa.oprojeto.webservice.domain.Resposta;

public class ChecklistRetorno extends Checklist {

	public ChecklistRetorno() {
		
	}

	public ChecklistRetorno(int codigo, int cpfColaborador, Date data, String placaVeiculo, Map<Pergunta, Resposta> perguntaRespostaMap) {
		super(codigo, cpfColaborador, data, placaVeiculo, perguntaRespostaMap);
	}

}
