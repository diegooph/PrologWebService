package br.com.zalf.prolog.webservice.colaborador.model;

import br.com.zalf.prolog.webservice.colaborador.constraints.Pis;
import br.com.zalf.prolog.webservice.colaborador.constraints.Telefone;
import org.hibernate.validator.constraints.Length;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 * Informações do colaborador.
 */
public class ColaboradorEdicao {
    @NotNull
    private Long codigo;
    /*
     * A validação do CPF só está considerando 11 caracteres devido às operações no Paraguai
     * */
    @NotNull(message = "Você precisa fornecer o CPF.")
    @Length(min = 11, max = 11, message = "CPF inválido, deve conter 11 dígitos.")
    private String cpf;

    @Nullable
    @Pis
    private String pis;

    @NotNull(message = "A data de nascimento não pode estar vazia.")
    @PastOrPresent(message = "Data de nascimento fornecida é inválida.")
    private LocalDate dataNascimento;

    @NotNull(message = "Por favor, selecione um cargo.")
    @Positive(message = "O cargo selecionado é inválido.")
    private Long codFuncao;

    @NotNull
    @Positive(message = "O setor selecionado é inválido.")
    private Long codSetor;

    @NotNull
    @NotBlank(message = "O nome do colaborador não pode estar vazio.")
    @Pattern(regexp = "^[a-zA-Z ]*$", message = "É permitido somente caracteres alfabéticos no nome do colaborador.")
    private String nome;

    @Nullable
    @Positive(message = "A Matrícula Ambev fornecida é inválida.")
    private Integer matriculaAmbev;

    @Nullable
    @Positive(message = "A Matrícula Transportadora fornecida é inválida.")
    private Integer matriculaTrans;

    @NotNull(message = "A data de admissão não pode estar vazia.")
    @PastOrPresent(message = "Data de admissão fornecida é inválida.")
    private LocalDate dataAdmissao;

    @NotNull
    private Long codEmpresa;

    @NotNull(message = "Por favor, selecione uma unidade.")
    @Positive(message = "A unidade selecionada é inválida.")
    private Long codUnidade;

    @NotNull(message = "Por favor, selecione uma equipe.")
    @Positive(message = "A equipe selecionada é inválida.")
    private Long codEquipe;

    @NotNull(message = "Por favor, selecione o nível de acesso à informação.")
    @Min(message = "O nível de acesso selecionado é inválido", value = 0)
    @Max(message = "O nível de acesso selecionado é inválido", value = 3)
    private Long codPermissao;

    @Nullable
    @Telefone
    private ColaboradorTelefone telefone;

    @Nullable
    @Email(message = "Por favor, digite um e-mail válido.")
    private String email;

    public ColaboradorEdicao(@NotNull final Long codigo,
                             @NotNull final String cpf,
                             @Nullable final String pis,
                             @NotNull final LocalDate dataNascimento,
                             @NotNull final Long codFuncao,
                             @NotNull final Long codSetor,
                             @NotNull final String nome,
                             @Nullable final Integer matriculaAmbev,
                             @Nullable final Integer matriculaTrans,
                             @NotNull final LocalDate dataAdmissao,
                             @NotNull final Long codEmpresa,
                             @NotNull final Long codUnidade,
                             @NotNull final Long codEquipe,
                             @NotNull final Long codPermissao,
                             @Nullable final ColaboradorTelefone telefone,
                             @Nullable String email) {
        this.codigo = codigo;
        this.cpf = cpf;
        this.pis = pis;
        this.dataNascimento = dataNascimento;
        this.codFuncao = codFuncao;
        this.codSetor = codSetor;
        this.nome = nome;
        this.matriculaAmbev = matriculaAmbev;
        this.matriculaTrans = matriculaTrans;
        this.dataAdmissao = dataAdmissao;
        this.codEmpresa = codEmpresa;
        this.codUnidade = codUnidade;
        this.codEquipe = codEquipe;
        this.codPermissao = codPermissao;
        this.telefone = telefone;
        this.email = email;
    }

    public Long getCodigo() {
        return codigo;
    }

    public String getCpf() {
        return cpf;
    }

    public String getPis() {
        return pis;
    }

    public LocalDate getDataNascimento() {
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

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
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

    public Long getCodPermissao() {
        return codPermissao;
    }

    public ColaboradorTelefone getTelefone() {
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
                ", empresa=" + codEmpresa +
                ", unidade=" + codUnidade +
                ", equipe=" + codEquipe +
                ", codPermissao=" + codPermissao +
                ", codEmpresa=" + codEmpresa +
                '}';
    }
}
