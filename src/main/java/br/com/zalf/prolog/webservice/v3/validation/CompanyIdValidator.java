package br.com.zalf.prolog.webservice.v3.validation;

import br.com.zalf.prolog.webservice.config.CurrentRequest;
import br.com.zalf.prolog.webservice.v3.general.branch.BranchService;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Optional;

public final class CompanyIdValidator implements ConstraintValidator<CompanyId, Long> {
    @NotNull
    private final CurrentRequest currentRequest;
    @NotNull
    private final BranchService branchService;

    @Autowired
    public CompanyIdValidator(@NotNull final CurrentRequest currentRequest,
                              @NotNull final BranchService branchService) {
        this.currentRequest = currentRequest;
        this.branchService = branchService;
    }

    @Override
    public void initialize(final CompanyId constraintAnnotation) {
    }

    @Override
    public boolean isValid(final Long value, final ConstraintValidatorContext context) {
        if (currentRequest.isFromApi()) {
            final Optional<String> requestTokenFromApi = currentRequest.getRequestTokenFromApi();
            if (requestTokenFromApi.isPresent()) {
                return containsCompanyId(branchService.getBranchesByTokenApi(requestTokenFromApi.get()), value);
            }
        } else {
            final Optional<String> requestToken = currentRequest.getRequestToken();
            if (requestToken.isPresent()) {
                return containsCompanyId(branchService.getBranchesByTokenUser(requestToken.get()), value);
            }
        }
        return false;
    }

    private boolean containsCompanyId(@NotNull final List<BranchEntity> branches, @NotNull final Long companyId) {
        return branches.stream()
                .map(branchEntity -> branchEntity.getCompanyEntity().getId())
                .distinct()
                .allMatch(id -> id.equals(companyId));
    }
}