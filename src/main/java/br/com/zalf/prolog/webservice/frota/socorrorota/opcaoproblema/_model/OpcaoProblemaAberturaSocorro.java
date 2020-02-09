package br.com.zalf.prolog.webservice.frota.socorrorota.opcaoproblema._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 12/19/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public class OpcaoProblemaAberturaSocorro {
    /*
    * Código único para identificação do problema
    * */
    @NotNull
    private final Long codOpcaoProblema;

    /*
    * Descrição do problema
    * */
    @NotNull
    private final String descricaoOpcaoProblema;

    /*
    * Define se o problema exige uma descrição obrigatória
    * */
    private final boolean opcaoProblemaObrigaDescricao;

    public OpcaoProblemaAberturaSocorro(@NotNull final Long codOpcaoProblema,
                                        @NotNull final String descricaoOpcaoProblema,
                                        final boolean opcaoProblemaObrigaDescricao) {
        this.codOpcaoProblema = codOpcaoProblema;
        this.descricaoOpcaoProblema = descricaoOpcaoProblema;
        this.opcaoProblemaObrigaDescricao = opcaoProblemaObrigaDescricao;
    }

    @NotNull
    public Long getCodOpcaoProblema() { return codOpcaoProblema; }

    @NotNull
    public String getDescricaoOpcaoProblema() { return descricaoOpcaoProblema; }

    public boolean isOpcaoProblemaObrigaDescricao() { return opcaoProblemaObrigaDescricao; }
}