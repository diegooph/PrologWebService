package br.com.empresa.oprojeto.webservice.domain.checklist;

import java.util.Date;
import java.util.Map;

import br.com.empresa.oprojeto.webservice.domain.Pergunta;
import br.com.empresa.oprojeto.webservice.domain.Resposta;

public abstract class Checklist {
	private long codigo;
	private int cpfColaborador;
	private Date data;
	private String placaVeiculo;
	private Map<Pergunta, Resposta> perguntaRespostaMap;
	
	public Checklist() {
		
	}

	public Checklist(int codigo, int cpfColaborador, Date data, String placaVeiculo, Map<Pergunta, Resposta> perguntaRespostaMap) {
		this.codigo = codigo;
		this.cpfColaborador = cpfColaborador;
		this.data = data;
		this.placaVeiculo = placaVeiculo;
		this.perguntaRespostaMap = perguntaRespostaMap;
	}

	public long getCodigo() {
		return codigo;
	}
	public void setCodigo(long codigo) {
		this.codigo = codigo;
	}
	public int getCpfColaborador() {
		return cpfColaborador;
	}
	public void setCpfColaborador(int cpfColaborador) {
		this.cpfColaborador = cpfColaborador;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getPlacaVeiculo() {
		return placaVeiculo;
	}
	public void setPlacaVeiculo(String placaVeiculo) {
		this.placaVeiculo = placaVeiculo;
	}
	public Map<Pergunta, Resposta> getPerguntaRespostaMap() {
		return perguntaRespostaMap;
	}
	public void setPerguntaRespostaMap(Map<Pergunta, Resposta> perguntaRespostaMap) {
		this.perguntaRespostaMap = perguntaRespostaMap;
	}
}
