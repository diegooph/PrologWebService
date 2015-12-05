package br.com.empresa.oprojeto.webservice.domain;


import java.util.Date;

public class Relato {
	private long codigo;
	private int cpfColaborador;
	private Date data;
	private String assunto;
	private String descricao;
	private double latitude;
	private double longitude;
	private String urlFoto1;
	private String urlFoto2;
	private String urlFoto3;
	
	public Relato() {
		
	}

	public Relato(long codigo, int cpfColaborador, Date data, String assunto, String descricao, double latitude,
			double longitude, String urlFoto1, String urlFoto2, String urlFoto3) {
		super();
		this.codigo = codigo;
		this.cpfColaborador = cpfColaborador;
		this.data = data;
		this.assunto = assunto;
		this.descricao = descricao;
		this.latitude = latitude;
		this.longitude = longitude;
		this.urlFoto1 = urlFoto1;
		this.urlFoto2 = urlFoto2;
		this.urlFoto3 = urlFoto3;
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

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getUrlFoto1() {
		return urlFoto1;
	}

	public void setUrlFoto1(String urlFoto1) {
		this.urlFoto1 = urlFoto1;
	}

	public String getUrlFoto2() {
		return urlFoto2;
	}

	public void setUrlFoto2(String urlFoto2) {
		this.urlFoto2 = urlFoto2;
	}

	public String getUrlFoto3() {
		return urlFoto3;
	}

	public void setUrlFoto3(String urlFoto3) {
		this.urlFoto3 = urlFoto3;
	}
}
