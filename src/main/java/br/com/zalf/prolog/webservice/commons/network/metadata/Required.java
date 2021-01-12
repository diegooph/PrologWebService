package br.com.zalf.prolog.webservice.commons.network.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Essa anotação deve ser utilizada em parâmetros nos resources para indicar quando um parâmetro é <b>obrigatório</b>.
 *
 * Created on 12/6/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.PARAMETER})
public @interface Required {
}