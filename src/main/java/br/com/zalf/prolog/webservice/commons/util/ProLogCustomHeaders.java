package br.com.zalf.prolog.webservice.commons.util;

import br.com.zalf.prolog.webservice.gente.controleintervalo.model.IntervaloOfflineSupport;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 14/05/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum ProLogCustomHeaders {
    VERSAO_DADOS_INTERVALO(IntervaloOfflineSupport.HEADER_NAME_VERSAO_DADOS_INTERVALO),
    APP_VERSION_ANDROID_APP("ProLog-Android-App-Version");

    @NotNull
    private final String headerName;

    ProLogCustomHeaders(@NotNull final String headerName) {
        this.headerName = headerName;
    }

    @NotNull
    public String getHeaderName() {
        return headerName;
    }
}