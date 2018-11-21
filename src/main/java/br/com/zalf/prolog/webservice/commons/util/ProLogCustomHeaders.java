package br.com.zalf.prolog.webservice.commons.util;

import br.com.zalf.prolog.webservice.gente.controlejornada.model.IntervaloOfflineSupport;

/**
 * Created on 14/05/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ProLogCustomHeaders {

    public ProLogCustomHeaders() {
        throw new IllegalStateException(ProLogCustomHeaders.class.getSimpleName() + " cannot be instantiated!");
    }

    public static final String VERSAO_DADOS_INTERVALO = IntervaloOfflineSupport.HEADER_NAME_VERSAO_DADOS_INTERVALO;
    public static final String APP_VERSION_ANDROID_APP = "ProLog-Android-App-Version";
}