package br.com.zalf.prolog.webservice.gente.treinamento;

import br.com.zalf.prolog.gente.treinamento.Treinamento;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TreinamentoHelper { 

	public static String createFileName(Treinamento treinamento) {
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
		String extensao = "pdf";
		return String.format(
				"%d_%s.%s",
				treinamento.getCodUnidade(),
				s.format(new Date(System.currentTimeMillis())),
				extensao);
	}
}