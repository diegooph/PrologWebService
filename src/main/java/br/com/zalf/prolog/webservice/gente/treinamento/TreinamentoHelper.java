package br.com.zalf.prolog.webservice.gente.treinamento;

import br.com.zalf.prolog.models.treinamento.Treinamento;

public class TreinamentoHelper { 

	public static String createFileName(Treinamento treinamento) {
		return String.format(
				"%s_%d_%s", 
				treinamento.getTitulo(), 
				treinamento.getCodUnidade(),
				treinamento.getDataLiberacao().toString());
	}
}
