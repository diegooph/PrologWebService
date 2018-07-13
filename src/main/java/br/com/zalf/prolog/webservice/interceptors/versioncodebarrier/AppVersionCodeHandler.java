package br.com.zalf.prolog.webservice.interceptors.versioncodebarrier;

import javax.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 14/05/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AppVersionCodeHandler {

    Class<? extends AppVersionCodeBarrier> implementation() default DefaultAppVersionCodeHandler.class;
    int targetVersionCode();
    VersionCodeHandlerMode versionCodeHandlerMode();
    VersionNotPresentAction actionIfVersionNotPresent();
    String appBlockedMessage() default "Uma nova versão do aplicativo está disponível, atualize para utilizar essa função";
}