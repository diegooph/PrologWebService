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
public class BlockedOperationYaml {
    @Value("${cod-empresas-integradas}")
    List<Long> integratedCompanies;
    @Value("${cod-unidades-bloqueadas}")
    List<Long> blockedBranches;
    @Value("${error-message}")
    String errorMessage;

    public void validateBlockedCompanyBranch(@NotNull final Long companyId, @NotNull final Long branchId) {
        validateCompanyId(companyId);
        validateBranchId(branchId);
    }

    private void validateCompanyId(@NotNull final Long companyId) {
        if (integratedCompanies.isEmpty()) {
            return;
        }
        if (integratedCompanies.stream().anyMatch(id -> id.equals(companyId))) {
            throw new BloqueadoIntegracaoException(errorMessage);
        }
    }

    private void validateBranchId(@NotNull final Long branchId) {
        if (blockedBranches.isEmpty()) {
            return;
        }
        if (blockedBranches.stream().anyMatch(id -> id.equals(branchId))) {
            throw new BloqueadoIntegracaoException(errorMessage);
        }
    }
}
