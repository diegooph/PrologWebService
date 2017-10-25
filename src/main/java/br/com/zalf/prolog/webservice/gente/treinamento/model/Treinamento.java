package br.com.zalf.prolog.webservice.gente.treinamento.model;

import br.com.zalf.prolog.webservice.colaborador.model.Cargo;

import java.util.Date;
import java.util.List;

/**
 * Dados de um treinamento.
 */
public class Treinamento {
	private Long codigo;
	private Long codUnidade;
	private String titulo;
	private String descricao;
	/**
	 * urlArquivo = link do S3 no qual esta hospedado o arquivo (treinamento), em pdf.
	 */
	private String urlArquivo;
	/**
	 * Como não temos um leitor de pdf ainda no app, vamos converter cada página do pdf para uma
	 * imagem e armazenar no S3. Essa lista irá conter as urls dessas imagens, dessa forma podemos
	 * mostrar o treinamento no Android.
	 */
	private List<String> urlsImagensArquivo;
	private Date dataLiberacao;
	private Date dataHoraCadastro;
	private List<Cargo> cargosLiberados;

	public Treinamento() {
		
	}

	public Treinamento(Long codigo, Long codUnidade, String titulo, String descricao, String urlArquivo,
			Date dataLiberacao, List<Cargo> cargosLiberados) {
		super();
		this.codigo = codigo;
		this.codUnidade = codUnidade;
		this.titulo = titulo;
		this.descricao = descricao;
		this.urlArquivo = urlArquivo;
		this.dataLiberacao = dataLiberacao;
		this.cargosLiberados = cargosLiberados;
	}

	public Date getDataHoraCadastro() {
		return dataHoraCadastro;
	}

	public void setDataHoraCadastro(Date dataHoraCadastro) {
		this.dataHoraCadastro = dataHoraCadastro;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public Long getCodUnidade() {
		return codUnidade;
	}

	public void setCodUnidade(Long codUnidade) {
		this.codUnidade = codUnidade;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getUrlArquivo() {
		return urlArquivo;
	}

	public void setUrlArquivo(String urlArquivo) {
		this.urlArquivo = urlArquivo;
	}

	public List<String> getUrlsImagensArquivo() {
		return urlsImagensArquivo;
	}

	public void setUrlsImagensArquivo(List<String> urlsImagensArquivo) {
		this.urlsImagensArquivo = urlsImagensArquivo;
	}

	public Date getDataLiberacao() {
		return dataLiberacao;
	}

	public void setDataLiberacao(Date dataLiberacao) {
		this.dataLiberacao = dataLiberacao;
	}

	public List<Cargo> getCargosLiberados() {
		return cargosLiberados;
	}

	public void setCargosLiberados(List<Cargo> cargosLiberados) {
		this.cargosLiberados = cargosLiberados;
	}

	@Override
	public String toString() {
		return "Treinamento{" +
				"codigo=" + codigo +
				", codUnidade=" + codUnidade +
				", titulo='" + titulo + '\'' +
				", descricao='" + descricao + '\'' +
				", urlArquivo='" + urlArquivo + '\'' +
				", dataLiberacao=" + dataLiberacao +
				", dataHoraCadastro=" + dataHoraCadastro +
				", cargosLiberados=" + cargosLiberados +
				'}';
	}
}
