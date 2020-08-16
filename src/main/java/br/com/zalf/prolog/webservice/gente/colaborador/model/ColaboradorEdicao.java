package br.com.zalf.prolog.webservice.gente.colaborador.model;

import br.com.zalf.prolog.webservice.gente.colaborador.constraints.Pis;
import br.com.zalf.prolog.webservice.gente.colaborador.constraints.Telefone;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 * Created on 2020-01-27
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
@Data
public final class ColaboradorEdicao {
    @NotNull
    private final Long codigo;
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
    @Pattern(regexp = "^[A-Za-záàâãéèêíïóôõöúçñÁÀÂÃÉÈÍÏÓÔÕÖÚÇÑ ]+$", message = "É permitido somente letras no nome do colaborador.")
    private final String nome;

    @Nullable
    @Positive(message = "A Matrícula Ambev fornecida é inválida.")
    private final Integer matriculaAmbev;

    @Nullable
    @Positive(message = "A Matrícula Transportadora fornecida é inválida.")
    private final Integer matriculaTrans;

    @Nullable
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
}
