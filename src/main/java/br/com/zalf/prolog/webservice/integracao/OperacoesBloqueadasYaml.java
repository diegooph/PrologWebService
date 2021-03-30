package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.errorhandling.exception.BloqueadoIntegracaoException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created on 2021-03-22
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Component
@PropertySource("classpath:configs/integracoes/operacoes_bloqueadas.yaml")
@ConfigurationProperties(prefix = "methods.insert-pneu")
public class OperacoesBloqueadasYaml {
    @Value("${cod-empresas-integradas}")
    List<Long> empresasIntegradas;
    @Value("${cod-unidades-bloqueadas}")
    List<Long> unidadesBloqueadas;
    @Value("${error-message}")
    String errorMessage;

    public void validateEmpresaUnidadeBloqueada(@NotNull final Long codEmpresa, @NotNull final Long codUnidade) {
        validateEmpresa(codEmpresa);
        validateUnidade(codUnidade);
    }

    private void validateEmpresa(@NotNull final Long codEmpresa) {
        if (empresasIntegradas.isEmpty()) {
            return;
        }
        if (empresasIntegradas.stream().anyMatch(e -> e.equals(codEmpresa))) {
            throw new BloqueadoIntegracaoException(errorMessage);
        }
    }

    private void validateUnidade(@NotNull final Long codUnidade) {
        if (unidadesBloqueadas.isEmpty()) {
            return;
        }
        if (unidadesBloqueadas.stream().anyMatch(u -> u.equals(codUnidade))) {
            throw new BloqueadoIntegracaoException(errorMessage);
        }
    }
}
