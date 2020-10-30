package br.com.zalf.prolog.webservice.config;

import br.com.zalf.prolog.webservice.errorhandling.ErrorReportSystem;
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
        if (applicationEvent.getType() == ApplicationEvent.Type.INITIALIZATION_APP_FINISHED) {
            ErrorReportSystem.init();
        }
    }

    @Override
    public RequestEventListener onRequest(final RequestEvent requestEvent) {
        // Do nothing.
        return null;
    }
}