package br.com.zalf.prolog.webservice.commons.spring;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created on 2021-02-02
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Component
public final class SpringContext implements ApplicationContextAware {

    private static ApplicationContext CONTEXT;

    public static <T> T getBean(final Class<T> clazz) {
        return CONTEXT.getBean(clazz);
    }

    @Override
    public void setApplicationContext(@NotNull final ApplicationContext applicationContext) throws BeansException {
        SpringContext.CONTEXT = applicationContext;
    }
}
