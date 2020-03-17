package br.com.zalf.prolog.webservice.geral.unidade._model;

import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class UnidadeEdicao {

    @NotNull(message = "O código da unidade é obrigatório.")
    private Long codUnidade;

    @NotNull(message = "O nome da unidade não pode estar vazio.")
    @Size(max = 40, message = "O nome da precisa conter até 40 caracteres.")
    private String nomeUnidade;

    @Nullable
    private String codAuxiliarUnidade;

    @Nullable
    private String latitudeUnidade;

    @Nullable
    private String longitudeUnidade;

    @NotNull
    public Long getCodUnidade() {
        return codUnidade;
    }

    public void setCodUnidade(@NotNull final Long codUnidade) {
        this.codUnidade = codUnidade;
    }

    @NotNull
    public String getNomeUnidade() {
        return nomeUnidade;
    }

    public void setNomeUnidade(@NotNull final String nomeUnidade) {
        this.nomeUnidade = nomeUnidade;
    }

    @Nullable
    public String getLatitudeUnidade() {
        return latitudeUnidade;
    }

    public void setLatitudeUnidade(@Nullable final String latitudeUnidade) {
        this.latitudeUnidade = latitudeUnidade;
    }

    @Nullable
    public String getLongitudeUnidade() {
        return longitudeUnidade;
    }

    public void setLongitudeUnidade(@Nullable final String longitudeUnidade) {
        this.longitudeUnidade = longitudeUnidade;
    }

    @Nullable
    public String getCodAuxiliarUnidade() {
        return codAuxiliarUnidade;
    }

    public void setCodAuxiliarUnidade(@Nullable final String codAuxiliarUnidade) {
        this.codAuxiliarUnidade = codAuxiliarUnidade;
    }

}
