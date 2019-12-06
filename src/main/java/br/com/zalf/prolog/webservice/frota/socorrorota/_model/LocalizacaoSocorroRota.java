package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-12-06
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class LocalizacaoSocorroRota {
    @NotNull
    private final String latitude;
    @NotNull
    private final String longitude;
    private final double precisaoLocalizacao;

    public LocalizacaoSocorroRota(@NotNull final String latitude,
                                  @NotNull final String longitude,
                                  final double precisaoLocalizacao) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.precisaoLocalizacao = precisaoLocalizacao;
    }

    @NotNull
    public String getLatitude() {
        return latitude;
    }

    @NotNull
    public String getLongitude() {
        return longitude;
    }

    public double getPrecisaoLocalizacao() {
        return precisaoLocalizacao;
    }
}