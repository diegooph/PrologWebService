package br.com.zalf.prolog.webservice.commons.util;

import br.com.zalf.prolog.webservice.gente.controlejornada.model.IntervaloOfflineSupport;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 14/05/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ProLogCustomHeaders {

    public ProLogCustomHeaders() {
        throw new IllegalStateException(ProLogCustomHeaders.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static final String VERSAO_DADOS_INTERVALO = IntervaloOfflineSupport.HEADER_NAME_VERSAO_DADOS_INTERVALO;
    @NotNull
    public static final String APP_VERSION_ANDROID_APP = "ProLog-Android-App-Version";
    @NotNull
    public static final String HEADER_TOKEN_INTEGRACAO = "ProLog-Token-Integracao";
    @NotNull
    public static final String HEADER_TOKEN_AGENDADOR = "ProLog-Token-Agendador";
}