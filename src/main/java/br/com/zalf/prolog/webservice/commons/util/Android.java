package br.com.zalf.prolog.webservice.commons.util;

import javax.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @deprecated Utilize {@link UsedBy} para denotar por qual plataforma o recurso está sendo utilizado.
 *
 * Created by luiz on 8/2/16.
 *
 * Notação utilitária que informa apenas se um método, ou todos os métodos de uma classe, são usados
 * no Android.
 */
@NameBinding
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD})
@Deprecated
public @interface Android {}