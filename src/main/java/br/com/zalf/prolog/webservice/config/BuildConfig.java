package br.com.zalf.prolog.webservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BuildConfig {
    public static boolean DEBUG;

    @Value("#{new Boolean('${prolog.debug}')}")
    private void setDebug(final Boolean isDebug) {
        BuildConfig.DEBUG = isDebug;
    }
}
