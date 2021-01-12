package br.com.zalf.prolog.webservice.commons.network.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 1/16/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface UsedBy {
    Platform[] platforms();
}