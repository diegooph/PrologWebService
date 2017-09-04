package br.com.zalf.prolog.webservice.interceptors.auth;

import javax.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Secured {

    AuthType authType() default AuthType.BEARER;
    int[] permissions() default {};
    boolean needsToHaveAll() default false;
}