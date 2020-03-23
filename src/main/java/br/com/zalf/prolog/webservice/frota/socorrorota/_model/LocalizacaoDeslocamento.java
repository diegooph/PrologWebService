package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.Nullable;

/**
 * Representa a localização do registro de deslocamento de socorros em atendimento.
 *
 * Created on 2020-03-23
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class LocalizacaoDeslocamento {
    @Nullable
    private final String latitude;
    @Nullable
    private final String longitude;
    private final float precisaoLocalizacaoMetros;

    public LocalizacaoDeslocamento(@Nullable final String latitude,
                                   @Nullable final String longitude,
                                   final float precisaoLocalizacaoMetros) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.precisaoLocalizacaoMetros = precisaoLocalizacaoMetros;
    }

    @Nullable
    public String getLatitude() {
        return latitude;
    }

    @Nullable
    public String getLongitude() {
        return longitude;
    }

    public float getPrecisaoLocalizacaoMetros() {
        return precisaoLocalizacaoMetros;
    }
}