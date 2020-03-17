package br.com.zalf.prolog.webservice.gente.colaborador.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;

import static java.lang.annotation.ElementType.CONSTRUCTOR;

import static java.lang.annotation.ElementType.FIELD;

import static java.lang.annotation.ElementType.METHOD;

import static java.lang.annotation.ElementType.PARAMETER;

import static java.lang.annotation.ElementType.TYPE_USE;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;

import java.lang.annotation.Repeatable;

import java.lang.annotation.Retention;

import java.lang.annotation.Target;

import javax.validation.Constraint;

import javax.validation.Payload;

/**
 * Created on 2020-01-22
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Repeatable(Pis.List.class)
@Documented
@Constraint(validatedBy = { })
public @interface Pis {

	String message() default "PIS inválido";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };

	@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
	@Retention(RUNTIME)
	@Documented
	@interface List {
		Pis[] value();
	}
}