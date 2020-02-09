package br.com.zalf.prolog.webservice.colaborador.model;

import br.com.zalf.prolog.webservice.colaborador.constraints.Pis;
import br.com.zalf.prolog.webservice.colaborador.constraints.Telefone;
import org.hibernate.validator.constraints.Length;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 * Created on 2020-01-22
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class ColaboradorInsercao {
    /*
     * A validação do CPF só está considerando 11 caracteres devido às operações no Paraguai.
     * */
    @NotNull(message = "Você precisa fornecer o CPF.")
    @Length(min = 11, max = 11, message = "CPF inválido, deve conter 11 dígitos.")
    private final String cpf;

    @Nullable
    @Pis
    private final String pis;

    @NotNull(message = "A data de nascimento não pode estar vazia.")
    @PastOrPresent(message = "A data de nascimento fornecida é inválida.")
    private final LocalDate dataNascimento;

    @NotNull(message = "Por favor, selecione um cargo.")
    @Positive(message = "O cargo selecionado é inválido.")
    private final Long codFuncao;

    @NotNull(message = "Por favor, selecione um setor.")
    @Positive(message = "O setor selecionado é inválido.")
    private final Long codSetor;

    @NotNull(message = "O nome do colaborador não pode estar vazio.")
    @NotBlank(message = "O nome do colaborador não pode estar vazio.")
    @Pattern(regexp = "^[a-zA-Z ]*$", message = "É permitido somente letras no nome do colaborador.")
    private final String nome;

    @Nullable
    @Positive(message = "A Matrícula Ambev fornecida é inválida.")
    private final Integer matriculaAmbev;

    @Nullable
    @Positive(message = "A Matrícula Transportadora fornecida é inválida.")
    private final Integer matriculaTrans;

    @NotNull(message = "A data de admissão não pode estar vazia.")
    @PastOrPresent(message = "A data de admissão fornecida é inválida.")
    private final LocalDate dataAdmissao;

    @NotNull
    private final Long codEmpresa;

    @NotNull(message = "Por favor, selecione uma unidade.")
    @Positive(message = "A unidade selecionada é inválida.")
    private final Long codUnidade;

    @NotNull(message = "Por favor, selecione uma equipe.")
    @Positive(message = "A equipe selecionada é inválida.")
    private final Long codEquipe;

    @NotNull(message = "Por favor, selecione o nível de acesso à informação.")
    @Min(message = "O nível de acesso selecionado é inválido", value = 0)
    @Max(message = "O nível de acesso selecionado é inválido", value = 3)
    private final Long codPermissao;

    @Nullable
    @Telefone(message = "O telefone fornecido é inválido.")
    private final ColaboradorTelefone telefone;

    @Nullable
    @Email(message = "O e-mail fornecido é inválido.")
    private final String email;

    public ColaboradorInsercao(@NotNull final String cpf,
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

    public String getCpf() {
        return cpf;
    }

    @Nullable
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

    @Nullable
    public Integer getMatriculaAmbev() {
        return matriculaAmbev;
    }

    @Nullable
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

    @Nullable
    public ColaboradorTelefone getTelefone() {
        return telefone;
    }

    @Nullable
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
