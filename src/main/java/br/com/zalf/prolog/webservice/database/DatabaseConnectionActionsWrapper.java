package br.com.zalf.prolog.webservice.database;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created on 2020-11-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Component
public class DatabaseConnectionActionsWrapper {

    private static ApplicationContext APPLICATION_CONTEXT;

    @Autowired
    public DatabaseConnectionActionsWrapper(@NotNull final ApplicationContext applicationContext) {
        APPLICATION_CONTEXT = applicationContext;
    }

    @NotNull
    public static DatabaseConnectionActions getActions() {
        return APPLICATION_CONTEXT.getBean(DatabaseConnectionActions.class);
    }
}
