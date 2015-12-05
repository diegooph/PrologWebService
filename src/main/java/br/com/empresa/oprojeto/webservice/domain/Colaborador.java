package br.com.empresa.oprojeto.webservice.domain;

import java.util.Date;

public class Colaborador {
	private int cpf;
	private Date dataNascimento;
	private long codFuncao;
	private long codUnidade;
	private String nome;
	private int matriculaAmbev;
	private Date dataAdmissao;
	private Date dataDemissao;
	private boolean ativo;
	private String equipe;
	private String setor;
	
	public Colaborador() {
		
	}
	
	public Colaborador(int cpf, Date dataNascimento, int codFuncao, int codUnidade, String nome, int matriculaAmbev,
			Date dataAdmissao, Date dataDemissao, boolean ativo, String equipe, String setor) {
		this.cpf = cpf;
		this.dataNascimento = dataNascimento;
		this.codFuncao = codFuncao;
		this.codUnidade = codUnidade;
		this.nome = nome;
		this.matriculaAmbev = matriculaAmbev;
		this.dataAdmissao = dataAdmissao;
		this.dataDemissao = dataDemissao;
		this.ativo = ativo;
		this.equipe = equipe;
		this.setor = setor;
	}
	
	public int getCpf() {
		return cpf;
	}
	public void setCpf(int cpf) {
		this.cpf = cpf;
	}
	public Date getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	public long getCodFuncao() {
		return codFuncao;
	}
	public void setCodFuncao(long codFuncao) {
		this.codFuncao = codFuncao;
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
	public String getEquipe() {
		return equipe;
	}
	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}
	public String getSetor() {
		return setor;
	}
	public void setSetor(String setor) {
		this.setor = setor;
	}
}
