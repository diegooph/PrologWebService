package br.com.zalf.prolog.webservice.database;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created on 25/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class DataSourceLifecycleManager implements ServletContextListener {

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
    }
}