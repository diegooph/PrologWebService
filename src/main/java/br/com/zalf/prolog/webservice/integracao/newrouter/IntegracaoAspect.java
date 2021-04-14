package br.com.zalf.prolog.webservice.integracao.newrouter;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.autenticacao.token.TokenCleaner;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Created on 2021-04-11
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Aspect
@Component
public final class IntegracaoAspect {

    @Around("@annotation(Integrado)")
    public Object onIntegratedMethodCalled(final ProceedingJoinPoint joinPoint) throws Throwable {
        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        final String requestToken = getRequestToken(requestAttributes);
        final Integrado integrado = getIntegradoAnnotation(joinPoint);
        final Optional<SistemaKey> sistemaKey = getSistemaKey(requestToken, integrado.recursoIntegrado());
        if (sistemaKey.isPresent()) {
            return null;
            //            final SistemaInterface sistema = SistemasFactory.createSistema(
            //                    sistemaKey.get(),
            //                    integrado.recursoIntegrado(),
            //                    IntegradorProLog.full(requestToken),
            //                    requestToken);
            //            final Method method = sistema.getClass().getDeclaredMethod(
            //                    context.getMethod().getName(),
            //                    context.getMethod().getParameterTypes());
            //            return method.invoke(sistema, context.getParameters());
        } else {
            return joinPoint.proceed();
        }
    }

    @NotNull
    private Optional<SistemaKey> getSistemaKey(@NotNull final String requestToken,
                                               @NotNull final RecursoIntegrado recursoIntegrado) throws Exception {
        return Optional.ofNullable(Injection.provideIntegracaoDao().getSistemaKey(requestToken, recursoIntegrado));
    }

    @NotNull
    private Integrado getIntegradoAnnotation(@NotNull final ProceedingJoinPoint joinPoint) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();
        return method.getAnnotation(Integrado.class);
    }

    @NotNull
    private String getRequestToken(@NotNull final ServletRequestAttributes requestAttributes) {
        final String authorization = requestAttributes.getRequest().getHeader("Authorization");
        return TokenCleaner.getOnlyToken(authorization);
    }
}
