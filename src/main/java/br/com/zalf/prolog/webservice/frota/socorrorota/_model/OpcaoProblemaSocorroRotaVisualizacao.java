package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 16/01/20.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class OpcaoProblemaSocorroRotaVisualizacao {
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

    @NotNull
    private final String nomeColaboradorUltimaAtualizacao;

    @NotNull
    private final String dataUltimaAtualizacao;

    public OpcaoProblemaSocorroRotaVisualizacao(@NotNull final Long codigo,
                                                @NotNull final String descricao,
                                                final boolean obrigaDescricao,
                                                final boolean ativo,
                                                @NotNull final String nomeColaboradorUltimaAtualizacao,
                                                @NotNull final String dataUltimaAtualizacao) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.obrigaDescricao = obrigaDescricao;
        this.ativo = ativo;
        this.nomeColaboradorUltimaAtualizacao = nomeColaboradorUltimaAtualizacao;
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getDescricao() {
        return descricao;
    }

    public boolean isObrigaDescricao() {
        return obrigaDescricao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public String getNomeColaboradorUltimaAtualizacao() {
        return nomeColaboradorUltimaAtualizacao;
    }

    public String getDataUltimaAtualizacao() {
        return dataUltimaAtualizacao;
    }
}