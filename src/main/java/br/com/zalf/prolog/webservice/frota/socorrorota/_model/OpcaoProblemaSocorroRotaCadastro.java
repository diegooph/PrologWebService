package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * Created on 14/01/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public class OpcaoProblemaSocorroRotaCadastro {
    /*
    * Código da empresa a ser vinculado
    * */
    @NotNull
    private final Long codEmpresa;

    /*
    * Código do colaborador que realizou o cadastro
    * */
    @NotNull
    private final Long codColaborador;

    /*
    * Descrição do problema
    * */
    @NotNull
    @NotBlank(message = "A descrição não pode estar vazia.")
    private final String descricao;

    /*
    * Flag para obrigar ao usuário uma descrição manual
    * */
    private final boolean obrigaDescricao;

    /*
    * Data e hora de cadastro
    * */
    @NotNull
    private final LocalDateTime dataHora;

    public OpcaoProblemaSocorroRotaCadastro(@NotNull final Long codEmpresa,
                                            @NotNull final Long codColaborador,
                                            @NotNull final String descricao,
                                            final boolean obrigaDescricao,
                                            @NotNull final LocalDateTime dataHora) {
        this.codEmpresa = codEmpresa;
        this.codColaborador = codColaborador;
        this.descricao = descricao;
        this.obrigaDescricao = obrigaDescricao;
        this.dataHora = dataHora;
    }

    @NotNull
    public Long getCodEmpresa() { return codEmpresa; }

    @NotNull
    public Long getCodColaborador() { return codColaborador; }

    @NotNull
    public String getDescricao() { return descricao; }

    public boolean isObrigaDescricao() { return obrigaDescricao; }

    @NotNull
    public LocalDateTime getDataHora() { return dataHora; }
}