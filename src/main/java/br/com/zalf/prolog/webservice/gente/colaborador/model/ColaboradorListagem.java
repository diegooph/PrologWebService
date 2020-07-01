package br.com.zalf.prolog.webservice.gente.colaborador.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

/**
 * Created on 2020-06-01
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
@Data
public class ColaboradorListagem {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    @NotNull
    private final Long cpf;
    @NotNull
    private final Long codRegional;
    @NotNull
    private final String nomeRegional;
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final String nomeUnidade;
    @NotNull
    private final Long codFuncao;
    @NotNull
    private final String nomeFuncao;
    @NotNull
    private final Long codEquipe;
    @NotNull
    private final String nomeEquipe;
    @NotNull
    private final Long codSetor;
    @NotNull
    private final String nomeSetor;
    @Nullable
    private final Integer matriculaAmbev;
    @Nullable
    private final Integer matriculaTrans;
    @NotNull
    private final Date dataNascimento;
    @NotNull
    private final Boolean ativo;
}
