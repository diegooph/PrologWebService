package br.com.zalf.prolog.webservice.gente.colaborador.model;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.gente.unidade._model.Unidade;
import br.com.zalf.prolog.webservice.permissao.Visao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Informações do colaborador.
 */
public class Colaborador {
	private static final String DATA_FORMATTER = "yyyy-MM-dd";
	private Long codigo;
	private Long cpf;
	private String pis;
	private Date dataNascimento;
	private Cargo funcao;
	private Setor setor;
	private String nome;
	private Integer matriculaAmbev;
	private Integer matriculaTrans;
	private Date dataAdmissao;
	private Date dataDemissao;
	private Boolean ativo;
	private Empresa empresa;
	private Unidade unidade;
	private Regional regional;
	private Equipe equipe;
	private Visao visao;
	private Integer codPermissao;
	private String tzUnidade;

	@Nullable
	private ColaboradorTelefone telefone;

	@Nullable
	private String email;

	@Deprecated
	private Long codEmpresa;
	@Deprecated
	private Long codUnidade;

	public Colaborador() {

	}

	@NotNull
	public static String formatCpf(@NotNull final Long cpfLong) {
		final String cpf = String.format("%011d", cpfLong);
		final String bloco1 = cpf.substring(0, 3);
		final String bloco2 = cpf.substring(3, 6);
		final String bloco3 = cpf.substring(6, 9);
		final String bloco4 = cpf.substring(9, 11);
		return String.format("%s.%s.%s-%s", bloco1, bloco2, bloco3, bloco4);
	}

	@NotNull
	public static Long formatCpf(@NotNull final String cpfString) {
		return Long.valueOf(StringUtils.getOnlyNumbers(cpfString));
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(final Long codigo) {
		this.codigo = codigo;
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

	public String getPis() {
		return pis;
	}

	public String getCpfAsString() {
		// Preenche com 0 a esquerda caso CPF tenha menos do que 11 caracteres.
		return String.format("%011d", cpf);
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public void setPis(String pis) {
		this.pis = pis;
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

	public Boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
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

	public void setTzUnidade(String tzUnidade) { this.tzUnidade = tzUnidade; }

	public String getTzUnidade(){ return this.tzUnidade; }

	@Nullable
	public ColaboradorTelefone getTelefone() { return telefone; }

	public void setTelefone(@Nullable final ColaboradorTelefone telefone) { this.telefone = telefone; }

	@Nullable
	public String getEmail() { return email; }

	public void setEmail(@Nullable final String email) { this.email = email; }

	public String getDataNascimentoAsString() {
		final Format formatter = new SimpleDateFormat(DATA_FORMATTER);
		return formatter.format(dataNascimento);
	}

	@Override
	public String toString() {
		return "Colaborador{" +
				"cpf=" + cpf +
				", pis=" + pis +
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
				", tzUnidade=" + tzUnidade +
				'}';
	}
}
