package br.com.zalf.prolog.webservice.v3.validation;

import org.jetbrains.annotations.NotNull;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BranchIdValidator.class)
public @interface BranchId {
    @NotNull
    String message() default "Você não tem acesso à esse código de unidade";

    @NotNull
    Class<?>[] groups() default {};

    @NotNull
    Class<? extends Payload>[] payload() default {};

    @NotNull
    String value() default "";
}
