package br.com.empresa.oprojeto.webservice.domain.indicadores;


public abstract class Indicador {
	private double meta;
	private double resultado;

	public Indicador() {
	}
	
	public Indicador(double meta, double resultado) {
		this.meta = meta;
		this.resultado = resultado;
	}

	public abstract double calculaResultado();
	
	public double getMeta() {
		return meta;
	}
	public void setMeta(double aMeta) {
		this.meta = aMeta;
	}
	public double getResultado() {
		return resultado;
	}
	public void setResultado(double aResultado) {
		this.resultado = aResultado;
	}
	
}
