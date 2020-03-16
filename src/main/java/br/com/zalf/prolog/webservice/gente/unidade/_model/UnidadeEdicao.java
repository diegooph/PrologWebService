package br.com.zalf.prolog.webservice.gente.unidade._model;

import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public class UnidadeEdicao {

    @NotNull(message = "O código da unidade é obrigatório.")
    private Long codUnidade;

    @NotNull(message = "O nome da unidade não pode estar vazio.")
    @Size(max = 40, message = "O nome da precisa conter até 40 caracteres.")
    private String nomeUnidade;

    @Nullable
    private String codAuxiliar;

    @Nullable
    private String latitude;

    @Nullable
    private String longitude;

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
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(@Nullable final String latitude) {
        this.latitude = latitude;
    }

    @Nullable
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(@Nullable final String longitude) {
        this.longitude = longitude;
    }

    @Nullable
    public String getCodAuxiliar() {
        return codAuxiliar;
    }

    public void setCodAuxiliar(@Nullable final String codAuxiliar) {
        this.codAuxiliar = codAuxiliar;
    }

}
