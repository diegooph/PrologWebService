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
import java.util.stream.Collectors;

public final class BranchesIdValidator implements ConstraintValidator<BranchesId, List<Long>> {
    @NotNull
    private final CurrentRequest currentRequest;
    @NotNull
    private final BranchService branchService;

    @Autowired
    public BranchesIdValidator(@NotNull final CurrentRequest currentRequest,
                               @NotNull final BranchService branchService) {
        this.currentRequest = currentRequest;
        this.branchService = branchService;
    }

    @Override
    public void initialize(final BranchesId constraintAnnotation) {
    }

    @Override
    public boolean isValid(final List<Long> value, final ConstraintValidatorContext context) {
        if (currentRequest.isFromApi()) {
            final Optional<String> requestTokenFromApi = currentRequest.getRequestTokenFromApi();
            if (requestTokenFromApi.isPresent()) {
                return containsCodUnidade(branchService.getUnidadesByTokenApi(requestTokenFromApi.get()), value);
            }
        } else {
            final Optional<String> requestToken = currentRequest.getRequestToken();
            if (requestToken.isPresent()) {
                return containsCodUnidade(branchService.getUnidadesByTokenUser(requestToken.get()), value);
            }
        }
        return false;
    }

    private boolean containsCodUnidade(@NotNull final List<UnidadeEntity> branches,
                                       @NotNull final List<Long> branchesId) {
        return branches.stream()
                .map(UnidadeEntity::getCodigo)
                .collect(Collectors.toList())
                .containsAll(branchesId);
    }
}
