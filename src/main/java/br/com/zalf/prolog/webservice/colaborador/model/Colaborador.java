package br.com.zalf.prolog.webservice.colaborador.model;

import br.com.zalf.prolog.webservice.permissao.Visao;

import java.util.Date;

/**
 * Informações do colaborador.
 */
public class Colaborador {
	private Long cpf;
	private Date dataNascimento;
	private Cargo funcao;
	private Setor setor;
	private String nome;
	private Integer matriculaAmbev;
	private Integer matriculaTrans;
	private Date dataAdmissao;
	private Date dataDemissao;
	private boolean ativo;
	private Empresa empresa;
	private Unidade unidade;
	private Regional regional;
	private Equipe equipe;
	private Visao visao;
	private Integer codPermissao;

	@Deprecated
	private Long codEmpresa;
	@Deprecated
	private Long codUnidade;

	public Colaborador() {

	}

	public Equipe getEquipe() {
		return equipe;
	}

	public void setEquipe(Equipe equipe) {
		this.equipe = equipe;
	}

	public Cargo getFuncao() {
		return funcao;
	}

	public void setFuncao(Cargo cargo) {
		this.funcao = cargo;
	}

	public Setor getSetor() {
		return setor;
	}

	public void setSetor(Setor setor) {
		this.setor = setor;
	}

	public Visao getVisao() {
		return visao;
	}

	public void setVisao(Visao visao) {
		this.visao = visao;
	}

	public Long getCpf() {
		return cpf;
	}

	public String getCpfAsString() {
		// Preenche com 0 a esquerda caso CPF tenha menos do que 11 caracteres.
		return String.format("%011d", cpf);
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getMatriculaAmbev() {
		return matriculaAmbev;
	}

	public void setMatriculaAmbev(int matriculaAmbev) {
		this.matriculaAmbev = matriculaAmbev;
	}

	public Integer getMatriculaTrans() {
		return matriculaTrans;
	}

	public void setMatriculaTrans(int matriculaTrans) {
		this.matriculaTrans = matriculaTrans;
	}

	public Date getDataAdmissao() {
		return dataAdmissao;
	}

	public void setDataAdmissao(Date dataAdmissao) {
		this.dataAdmissao = dataAdmissao;
	}

	public Date getDataDemissao() {
		return dataDemissao;
	}

	public void setDataDemissao(Date dataDemissao) {
		this.dataDemissao = dataDemissao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Integer getCodPermissao() {
		return codPermissao;
	}

	public void setCodPermissao(Integer codPermissao) {
		this.codPermissao = codPermissao;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
		this.codUnidade = unidade.getCodigo();
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
		this.codEmpresa = empresa.getCodigo();
	}

	public Regional getRegional() {
		return regional;
	}

	public void setRegional(Regional regional) {
		this.regional = regional;
	}

	public Long getCodEmpresa() {
		return empresa.getCodigo();
	}

	public long getCodUnidade() {
		return unidade.getCodigo();
	}

	@Override
	public String toString() {
		return "Colaborador{" +
				"cpf=" + cpf +
				", dataNascimento=" + dataNascimento +
				", funcao=" + funcao +
				", setor=" + setor +
				", codUnidade=" + codUnidade +
				", nome='" + nome + '\'' +
				", matriculaAmbev=" + matriculaAmbev +
				", matriculaTrans=" + matriculaTrans +
				", dataAdmissao=" + dataAdmissao +
				", dataDemissao=" + dataDemissao +
				", ativo=" + ativo +
				", empresa=" + empresa +
				", unidade=" + unidade +
				", regional=" + regional +
				", equipe=" + equipe +
				", codPermissao=" + codPermissao +
				", codEmpresa=" + codEmpresa +
				", visao=" + visao +
				'}';
	}
}
