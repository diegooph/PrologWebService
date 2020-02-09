package br.com.zalf.prolog.webservice.frota.socorrorota.opcaoproblema._model;

import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.NotBlank;

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

    public OpcaoProblemaSocorroRotaCadastro(@NotNull final Long codEmpresa,
                                            @NotNull final Long codColaborador,
                                            @NotNull final String descricao,
                                            final boolean obrigaDescricao) {
        this.codEmpresa = codEmpresa;
        this.codColaborador = codColaborador;
        this.descricao = descricao;
        this.obrigaDescricao = obrigaDescricao;
    }

    @NotNull
    public Long getCodEmpresa() {
        return codEmpresa;
    }

    @NotNull
    public Long getCodColaborador() {
        return codColaborador;
    }

    @NotNull
    public String getDescricao() {
        return descricao;
    }

    public boolean isObrigaDescricao() {
        return obrigaDescricao;
    }
}