package br.com.zalf.prolog.webservice.integracao.api.pneu.model;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Created on 16/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiMarcaBanda {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    @NotNull
    private final List<ApiModeloBanda> modelos;
    @NotNull
    private final Boolean statusAtivo;

    public ApiMarcaBanda(@NotNull final Long codigo,
                         @NotNull final String nome,
                         @NotNull final List<ApiModeloBanda> modelos,
                         @NotNull final Boolean statusAtivo) {
        this.codigo = codigo;
        this.nome = nome;
        this.modelos = modelos;
        this.statusAtivo = statusAtivo;
    }

    @NotNull
    public static ApiMarcaBanda getApiMarcaBandaDummy() {
        return new ApiMarcaBanda(
                25L,
                "Vipal",
                Collections.singletonList(ApiModeloBanda.getApiModeloBandaDummy()),
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
    public List<ApiModeloBanda> getModelos() {
        return modelos;
    }

    @NotNull
    public Boolean getStatusAtivo() {
        return statusAtivo;
    }
}
