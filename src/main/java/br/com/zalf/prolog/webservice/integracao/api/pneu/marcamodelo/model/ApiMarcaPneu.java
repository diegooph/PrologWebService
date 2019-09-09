package br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model;

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
    @NotNull
    private final Boolean statusAtivo;

    public ApiMarcaPneu(@NotNull final Long codigo,
                        @NotNull final String nome,
                        @NotNull final List<ApiModeloPneu> modelos,
                        @NotNull final Boolean statusAtivo) {
        this.codigo = codigo;
        this.nome = nome;
        this.modelos = modelos;
        this.statusAtivo = statusAtivo;
    }

    @NotNull
    public static ApiMarcaPneu getApiMarcaPneuDummy() {
        return new ApiMarcaPneu(
                1L,
                "Goodyear",
                Collections.singletonList(ApiModeloPneu.getApiModeloPneuDummy()),
                true);
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

    @NotNull
    public Boolean getStatusAtivo() {
        return statusAtivo;
    }
}
