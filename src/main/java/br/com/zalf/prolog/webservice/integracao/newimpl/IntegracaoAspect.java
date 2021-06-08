package br.com.zalf.prolog.webservice.integracao.newimpl;

import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.integrador.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.newimpl.sistemas.SistemaFactory;
import br.com.zalf.prolog.webservice.integracao.newimpl.sistemas.SistemaIntegrado;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.config.CurrentRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Created on 2021-04-11
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Aspect
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public final class IntegracaoAspect {
    @NotNull
    private final SistemaFactory sistemaFactory;
    @NotNull
    private final IntegracaoDao integracaoDao;
    @NotNull
    private final CurrentRequest request;

    @Around("@annotation(Integrado)")
    public Object onIntegratedMethodCalled(final ProceedingJoinPoint joinPoint) throws Throwable {
        final Optional<String> optional = request.getRequestToken();
        if (optional.isPresent()) {
            final String requestToken = optional.get();
            final Integrado integrado = getIntegradoAnnotation(joinPoint);
            final Optional<SistemaKey> sistemaKey = getSistemaKey(requestToken, integrado.recursoIntegrado());
            if (sistemaKey.isPresent()) {
                return delegateToSistema(joinPoint, sistemaKey.get());
            } else {
                return joinPoint.proceed();
            }
        } else {
            throw new IllegalStateException("Nenhum token presente no request para poder rotear");
        }
    }

    @NotNull
    private Object delegateToSistema(@NotNull final ProceedingJoinPoint joinPoint,
                                     @NotNull final SistemaKey sistemaKey) throws Throwable {
        final SistemaIntegrado sistema = sistemaFactory.createSistema(sistemaKey);
        final Method calledMethod = getCalledMethod(joinPoint);
        final Method methodToDelegate = sistema.getClass().getDeclaredMethod(
                calledMethod.getName(),
                calledMethod.getParameterTypes());
        return methodToDelegate.invoke(sistema, joinPoint.getArgs());
    }

    @NotNull
    private Optional<SistemaKey> getSistemaKey(@NotNull final String requestToken,
                                               @NotNull final RecursoIntegrado recursoIntegrado) throws Exception {
        return Optional.ofNullable(integracaoDao.getSistemaKey(requestToken, recursoIntegrado));
    }

    @NotNull
    private Integrado getIntegradoAnnotation(@NotNull final ProceedingJoinPoint joinPoint) {
        final Method method = getCalledMethod(joinPoint);
        return method.getAnnotation(Integrado.class);
    }

    @NotNull
    private Method getCalledMethod(@NotNull final ProceedingJoinPoint joinPoint) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }
}