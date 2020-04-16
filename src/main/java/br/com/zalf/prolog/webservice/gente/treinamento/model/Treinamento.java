package br.com.zalf.prolog.webservice.gente.treinamento.model;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;

import java.time.LocalDateTime;
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
	private Date dataFechamento;
	private LocalDateTime dataHoraCadastro;
	private List<Cargo> cargosLiberados;

	public Treinamento() {
		
	}

	public LocalDateTime getDataHoraCadastro() {
		return dataHoraCadastro;
	}

	public void setDataHoraCadastro(LocalDateTime dataHoraCadastro) {
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

	public Date getDataFechamento() {
		return dataFechamento;
	}

	public void setDataFechamento(Date dataFechamento) {
		this.dataFechamento = dataFechamento;
	}

	@Override
	public String toString() {
		return "Treinamento{" +
				"codigo=" + codigo +
				", codUnidade=" + codUnidade +
				", titulo='" + titulo + '\'' +
				", descricao='" + descricao + '\'' +
				", urlArquivo='" + urlArquivo + '\'' +
				", urlsImagensArquivo=" + urlsImagensArquivo +
				", dataLiberacao=" + dataLiberacao +
				", dataHoraCadastro=" + dataHoraCadastro +
				", dataFechamento=" + dataFechamento +
				", cargosLiberados=" + cargosLiberados +
				'}';
	}
}