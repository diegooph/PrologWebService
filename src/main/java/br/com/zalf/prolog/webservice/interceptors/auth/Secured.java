package br.com.zalf.prolog.webservice.interceptors.auth;

import br.com.zalf.prolog.webservice.interceptors.auth.authorization.AuthType;

import javax.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Secured {
    AuthType[] authTypes() default AuthType.BEARER;
    int[] permissions() default {};
    boolean needsToHaveAllPermissions() default false;
    boolean considerOnlyActiveUsers() default true;
}