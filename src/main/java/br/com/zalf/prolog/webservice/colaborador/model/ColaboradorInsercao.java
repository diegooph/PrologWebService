package br.com.zalf.prolog.webservice.colaborador.model;

import br.com.zalf.prolog.webservice.colaborador.constraints.Pis;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * Informações do colaborador.
 */
public class ColaboradorInsercao {
    /*
     * A validação do CPF só está considerando 11 caracteres devido às operações no Paraguai
     * */
    @NotNull(message = "Você precisa fornecer o CPF.")
    @Size(min = 11, max = 11, message = "Número CPF inválido.")
    private String cpf;

    @Nullable
    @Pis
    private String pis;

    @NotNull(message = "A data de nascimento não pode estar vazia.")
    private Date dataNascimento;
    @NotNull(message = "Por favor, selecione um cargo.")
    private Long codFuncao;
    @NotNull
    private Long codSetor;
    @NotNull
    @NotBlank(message = "O nome do colaborador não pode estar vazio.")
    @Pattern(regexp = "^[a-zA-Z]*$", message = "É permitido somente caracteres alfabéticos no nome do colaborador.")
    private String nome;
    @Nullable
    @Positive(message = "Matrícula Ambev inválida.")
    private Integer matriculaAmbev;
    @Nullable
    @Positive(message = "Matrícula Transportadora inválida.")
    private Integer matriculaTrans;
    @NotNull(message = "A data de admissão não pode estar vazia.")
    private Date dataAdmissao;
    @Nullable
    private Date dataDemissao;
    @NotNull
    private Long codEmpresa;
    @NotNull(message = "Por favor, selecione uma unidade.")
    private Long codUnidade;
    @NotNull(message = "Por favor, selecione uma equipe.")
    private Long codEquipe;
    @NotNull(message = "Por favor, selecione o nível de acesso à informação.")
    private Integer codPermissao;
    @Nullable
    private String prefixoPais;
    @Nullable
    private String telefone;
    @Nullable
    @Email(message = "Por favor, digite um e-mail válido.")
    private String email;

    public ColaboradorInsercao(@NotNull final String cpf,
                               @Nullable final String pis,
                               @NotNull final Date dataNascimento,
                               @NotNull final Long codFuncao,
                               @NotNull final Long codSetor,
                               @NotNull final String nome,
                               @Nullable final Integer matriculaAmbev,
                               @Nullable final Integer matriculaTrans,
                               @NotNull final Date dataAdmissao,
                               @Nullable final Date dataDemissao,
                               @NotNull final Long codEmpresa,
                               @NotNull final Long codUnidade,
                               @NotNull final Long codEquipe,
                               @NotNull final Integer codPermissao,
                               @Nullable String prefixoPais,
                               @Nullable String telefone,
                               @Nullable String email) {
        this.cpf = cpf;
        this.pis = pis;
        this.dataNascimento = dataNascimento;
        this.codFuncao = codFuncao;
        this.codSetor = codSetor;
        this.nome = nome;
        this.matriculaAmbev = matriculaAmbev;
        this.matriculaTrans = matriculaTrans;
        this.dataAdmissao = dataAdmissao;
        this.dataDemissao = dataDemissao;
        this.codEmpresa = codEmpresa;
        this.codUnidade = codUnidade;
        this.codEquipe = codEquipe;
        this.codPermissao = codPermissao;
        this.prefixoPais = prefixoPais;
        this.telefone = telefone;
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public String getPis() {
        return pis;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public Long getCodFuncao() {
        return codFuncao;
    }

    public Long getCodSetor() {
        return codSetor;
    }

    public String getNome() {
        return nome;
    }

    public Integer getMatriculaAmbev() {
        return matriculaAmbev;
    }

    public Integer getMatriculaTrans() {
        return matriculaTrans;
    }

    public Date getDataAdmissao() {
        return dataAdmissao;
    }

    public Date getDataDemissao() {
        return dataDemissao;
    }

    public Long getCodEmpresa() {
        return codEmpresa;
    }

    public Long getCodUnidade() {
        return codUnidade;
    }

    public Long getCodEquipe() {
        return codEquipe;
    }

    public Integer getCodPermissao() {
        return codPermissao;
    }

    public String getPrefixoPais() {
        return prefixoPais;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Colaborador{" +
                "cpf=" + cpf +
                ", pis=" + pis +
                ", dataNascimento=" + dataNascimento +
                ", funcao=" + codFuncao +
                ", setor=" + codSetor +
                ", codUnidade=" + codUnidade +
                ", nome='" + nome + '\'' +
                ", matriculaAmbev=" + matriculaAmbev +
                ", matriculaTrans=" + matriculaTrans +
                ", dataAdmissao=" + dataAdmissao +
                ", dataDemissao=" + dataDemissao +
                ", empresa=" + codEmpresa +
                ", unidade=" + codUnidade +
                ", equipe=" + codEquipe +
                ", codPermissao=" + codPermissao +
                ", codEmpresa=" + codEmpresa +
                '}';
    }
}
