package br.com.zalf.prolog.webservice.interceptors.debug;

/**
 * Created on 13/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */

import javax.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ResourceDebugOnly { }