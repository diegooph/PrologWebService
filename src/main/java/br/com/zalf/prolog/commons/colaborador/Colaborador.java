package br.com.zalf.prolog.commons.colaborador;

import br.com.zalf.prolog.permissao.Visao;

import java.util.Date;

/**
 * Informações do colaborador.
 */
public class Colaborador {
	private long cpf;
	private Date dataNascimento;
	private Funcao funcao;
	private Setor setor;
	private long codUnidade;
	private String nome;
	private int matriculaAmbev;
	private int matriculaTrans;
	private Date dataAdmissao;
	private Date dataDemissao;
	private boolean ativo;
	private Empresa empresa;
	private Unidade unidade;
	private Regional regional;
	private Equipe equipe;
	private long codPermissao;
	private long codEmpresa;
	private Visao visao;

	public Colaborador() {

	}

	public Equipe getEquipe() {
		return equipe;
	}

	public void setEquipe(Equipe equipe) {
		this.equipe = equipe;
	}

	public Funcao getFuncao() {
		return funcao;
	}

	public void setFuncao(Funcao funcao) {
		this.funcao = funcao;
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

	public long getCpf() {
		return cpf;
	}

	public void setCpf(long cpf) {
		this.cpf = cpf;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public long getCodUnidade() {
		return codUnidade;
	}

	public void setCodUnidade(long codUnidade) {
		this.codUnidade = codUnidade;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getMatriculaAmbev() {
		return matriculaAmbev;
	}

	public void setMatriculaAmbev(int matriculaAmbev) {
		this.matriculaAmbev = matriculaAmbev;
	}

	public int getMatriculaTrans() {
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

	public long getCodPermissao() {
		return codPermissao;
	}

	public void setCodPermissao(long codPermissao) {
		this.codPermissao = codPermissao;
	}

	public long getCodEmpresa() {
		return codEmpresa;
	}

	public void setCodEmpresa(long codEmpresa) {
		this.codEmpresa = codEmpresa;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Regional getRegional() {
		return regional;
	}

	public void setRegional(Regional regional) {
		this.regional = regional;
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
