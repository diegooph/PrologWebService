package br.com.zalf.prolog.webservice.integracao.api.pneu.model;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Created on 16/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiMarcaPneu {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    @NotNull
    private final List<ApiModeloPneu> modelos;

    public ApiMarcaPneu(@NotNull final Long codigo,
                        @NotNull final String nome,
                        @NotNull final List<ApiModeloPneu> modelos) {
        this.codigo = codigo;
        this.nome = nome;
        this.modelos = modelos;
    }

    @NotNull
    public static ApiMarcaPneu getApiMarcaPneuDummy() {
        return new ApiMarcaPneu(
                1L,
                "Goodyear",
                Collections.singletonList(ApiModeloPneu.getApiModeloPneuDummy()));
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    @NotNull
    public List<ApiModeloPneu> getModelos() {
        return modelos;
    }
}
