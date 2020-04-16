package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;

/**
 * Objeto utilizado em todos os locais do socorro em rota onde precisamos representar a localização.
 * <p>
 * Created on 2019-12-06
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class LocalizacaoSocorroRota {
    @NotNull
    private final String latitude;
    @NotNull
    private final String longitude;
    private final float precisaoLocalizacaoMetros;

    public LocalizacaoSocorroRota(@NotNull final String latitude,
                                  @NotNull final String longitude,
                                  final float precisaoLocalizacaoMetros) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.precisaoLocalizacaoMetros = precisaoLocalizacaoMetros;
    }

    @NotNull
    public String getLatitude() {
        return latitude;
    }

    @NotNull
    public String getLongitude() {
        return longitude;
    }

    public float getPrecisaoLocalizacaoMetros() {
        return precisaoLocalizacaoMetros;
    }
}