package br.com.zalf.prolog.webservice.frota.checklist.model;


import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/** 29/02/2016
 * Checklist de saída ou retorno (atributo "tipo") do veículo, contém placa do veículo e colaborador que realizou o check
 * além de um map com as perguntas e respostas.
 */
@Deprecated
public class Checklist {
	public static final char TIPO_SAIDA = 'S';
	public static final char TIPO_RETORNO = 'R';
	private Long codModelo;
	private Long codigo;
	private Colaborador colaborador;
	private LocalDateTime data;
	@Nullable
	private LocalDateTime dataHoraImportadoProLog;
	private String placaVeiculo;
	private List<PerguntaRespostaChecklist> listRespostas;
	/**
	 * tipo pode assumir S e R, de saída e retorno, respectivamente
 	 */
	private char tipo;
	private long kmAtualVeiculo;
	private long tempoRealizacaoCheckInMillis;
	private int qtdItensOk;
	private int qtdItensNok;
	private int qtdAlternativasOk;
	private int qtdAlternativasNok;
	
	public Checklist() {
		
	}

	@NotNull
	public static List<Checklist> sortByDate(@NotNull final List<Checklist> checklists, final boolean ascending) {
		if (ascending) {
			checklists.sort(Comparator.comparing(Checklist::getData));
		} else {
			checklists.sort(Comparator.comparing(Checklist::getData).reversed());
		}

		return checklists;
	}

	public Long getCodModelo() {
		return codModelo;
	}

	public void setCodModelo(Long codModelo) {
		this.codModelo = codModelo;
	}

	public long getTempoRealizacaoCheckInMillis() {
		return tempoRealizacaoCheckInMillis;
	}

	public void setTempoRealizacaoCheckInMillis(long tempoRealizacaoCheckInMillis) {
		this.tempoRealizacaoCheckInMillis = tempoRealizacaoCheckInMillis;
	}

	public static char getTipoSaida() {
		return TIPO_SAIDA;
	}

	public static char getTipoRetorno() {
		return TIPO_RETORNO;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public Colaborador getColaborador() {
		return colaborador;
	}

	public void setColaborador(Colaborador colaborador) {
		this.colaborador = colaborador;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}

	public LocalDateTime getDataHoraImportadoProLog() {
		return dataHoraImportadoProLog;
	}

	public void setDataHoraImportadoProLog(@Nullable final LocalDateTime dataHoraImportadoProLog) {
		this.dataHoraImportadoProLog = dataHoraImportadoProLog;
	}

	public String getPlacaVeiculo() {
		return placaVeiculo;
	}

	public void setPlacaVeiculo(String placaVeiculo) {
		this.placaVeiculo = placaVeiculo;
	}

	public List<PerguntaRespostaChecklist> getListRespostas() {
		return listRespostas;
	}

	public void setListRespostas(List<PerguntaRespostaChecklist> listRespostas) {
		this.listRespostas = listRespostas;
		calculaQtdOkOrNok();
	}

	public char getTipo() {
		return tipo;
	}

	public void setTipo(char tipo) {
		this.tipo = tipo;
	}

	public long getKmAtualVeiculo() {
		return kmAtualVeiculo;
	}

	public void setKmAtualVeiculo(long kmAtualVeiculo) {
		this.kmAtualVeiculo = kmAtualVeiculo;
	}

	public int getQtdItensOk() {
		return qtdItensOk;
	}

	public void setQtdItensOk(int qtdItensOk) {
		this.qtdItensOk = qtdItensOk;
	}

	public int getQtdItensNok() {
		return qtdItensNok;
	}

	public void setQtdItensNok(int qtdItensNok) {
		this.qtdItensNok = qtdItensNok;
	}

	public int getQtdAlternativasOk() {
		return qtdAlternativasOk;
	}

	public int getQtdAlternativasNok() {
		return qtdAlternativasNok;
	}

	public void calculaQtdOkOrNok() {
		int qtdPerguntasOk = 0;
		int qtdPerguntasNok = 0;
		int qtdAlternativasOk = 0;
		int qtdAlternativasNok = 0;
		boolean perguntaTeveAlternativasNok = false;
		for (int i = 0; i < listRespostas.size(); i++) {
			final PerguntaRespostaChecklist checklistResposta = listRespostas.get(i);
			final List<AlternativaChecklist> alternativasRespostas = checklistResposta.getAlternativasResposta();
			for (int j = 0; j < alternativasRespostas.size(); j++) {
				final AlternativaChecklist alternativaResposta = alternativasRespostas.get(i);
				if (alternativaResposta.isSelected()) {
					qtdAlternativasNok++;
					perguntaTeveAlternativasNok = true;
				} else {
					qtdAlternativasOk++;
				}
			}
			if (perguntaTeveAlternativasNok) {
				qtdAlternativasNok++;
				perguntaTeveAlternativasNok = false;
			} else {
				qtdPerguntasOk++;
			}
		}
        this.qtdItensOk = qtdPerguntasOk;
		this.qtdItensNok = qtdPerguntasNok;
        this.qtdAlternativasOk = qtdAlternativasOk;
        this.qtdAlternativasNok = qtdAlternativasNok;
	}

	@Override
	public String toString() {
		return "Checklist{" +
				"codModelo=" + codModelo +
				", codigo=" + codigo +
				", colaborador=" + colaborador +
				", data=" + data +
				", placaVeiculo='" + placaVeiculo + '\'' +
				", listRespostas=" + listRespostas +
				", tipo=" + tipo +
				", kmAtualVeiculo=" + kmAtualVeiculo +
				", tempoRealizacaoCheckInMillis=" + tempoRealizacaoCheckInMillis +
				", qtdItensOk=" + qtdItensOk +
				", qtdItensNok=" + qtdItensNok +
				'}';
	}
}
