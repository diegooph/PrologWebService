package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 14/01/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public class OpcaoProblemaSocorroRota {
    /*
    * Código único para identificação do problema
    * */
    @NotNull
    private final Long codigo;

    /*
    * Descrição do problema
    * */
    @NotNull
    private final String descricao;

    /*
    * Define se o problema exige uma descrição obrigatória
    * */
    private final boolean obrigaDescricao;

    /*
    * Define o status
    * */
    private final boolean ativo;

    public OpcaoProblemaSocorroRota(@NotNull final Long codigo,
                                    @NotNull final String descricao,
                                    final boolean obrigaDescricao,
                                    final boolean ativo) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.obrigaDescricao = obrigaDescricao;
        this.ativo = ativo;
    }

    @NotNull
    public Long getCodigo() { return codigo; }

    @NotNull
    public String getDescricao() { return descricao; }

    public boolean isObrigaDescricao() { return obrigaDescricao; }

    public boolean isAtivo() { return ativo; }
}