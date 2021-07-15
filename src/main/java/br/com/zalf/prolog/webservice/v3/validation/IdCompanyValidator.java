package br.com.zalf.prolog.webservice.v3.validation;

import br.com.zalf.prolog.webservice.config.CurrentRequest;
import br.com.zalf.prolog.webservice.v3.general.branch.BranchService;
import br.com.zalf.prolog.webservice.v3.general.branch._model.UnidadeEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Optional;

public final class IdCompanyValidator implements ConstraintValidator<IdCompany, Long> {
    @NotNull
    private final CurrentRequest currentRequest;
    @NotNull
    private final BranchService branchService;

    @Autowired
    public IdCompanyValidator(@NotNull final CurrentRequest currentRequest,
                              @NotNull final BranchService branchService) {
        this.currentRequest = currentRequest;
        this.branchService = branchService;
    }

    @Override
    public void initialize(final IdCompany constraintAnnotation) {
    }

    @Override
    public boolean isValid(final Long value, final ConstraintValidatorContext context) {
        if (currentRequest.isFromApi()) {
            final Optional<String> requestTokenFromApi = currentRequest.getRequestTokenFromApi();
            if (requestTokenFromApi.isPresent()) {
                return containsCodEmpresa(branchService.getUnidadesByTokenApi(requestTokenFromApi.get()), value);
            }
        } else {
            final Optional<String> requestToken = currentRequest.getRequestToken();
            if (requestToken.isPresent()) {
                return containsCodEmpresa(branchService.getUnidadesByTokenUser(requestToken.get()), value);
            }
        }
        return false;
    }

    private boolean containsCodEmpresa(@NotNull final List<UnidadeEntity> unidades, @NotNull final Long codEmpresa) {
        return unidades.stream()
                .map(unidadeEntity -> unidadeEntity.getCompanyEntity().getCodigo())
                .distinct()
                .allMatch(codigo -> codigo.equals(codEmpresa));
    }
}