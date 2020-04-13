package br.com.zalf.prolog.webservice.config;

import br.com.zalf.prolog.webservice.commons.util.EnvironmentHelper;
import io.sentry.Sentry;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

/**
 * Created on 11/3/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class ProLogApplicationEventListener implements ApplicationEventListener {

    @Override
    public void onEvent(final ApplicationEvent applicationEvent) {
        switch (applicationEvent.getType()) {
            case INITIALIZATION_APP_FINISHED:
                Sentry.init(EnvironmentHelper.SENTRY_DSN + "?release=" + BuildConfig.VERSION_CODE);
                break;
        }
    }

    @Override
    public RequestEventListener onRequest(final RequestEvent requestEvent) {
        // Do nothing.
        return null;
    }
}