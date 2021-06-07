package br.com.zalf.prolog.webservice.v3.validation;

import br.com.zalf.prolog.webservice.config.CurrentRequest;
import br.com.zalf.prolog.webservice.v3.geral.unidade.UnidadeService;
import br.com.zalf.prolog.webservice.v3.geral.unidade._model.UnidadeEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Optional;

public final class CodUnidadeValidator implements ConstraintValidator<CodUnidade, Long> {
    @NotNull
    private final CurrentRequest currentRequest;
    @NotNull
    private final UnidadeService unidadeService;

    @Autowired
    public CodUnidadeValidator(@NotNull final CurrentRequest currentRequest,
                               @NotNull final UnidadeService unidadeService) {
        this.currentRequest = currentRequest;
        this.unidadeService = unidadeService;
    }

    @Override
    public void initialize(final CodUnidade constraintAnnotation) {
    }

    @Override
    public boolean isValid(final Long value, final ConstraintValidatorContext context) {
        if (currentRequest.isFromApi()) {
            final Optional<String> requestTokenFromApi = currentRequest.getRequestTokenFromApi();
            if (requestTokenFromApi.isPresent()) {
                return containsCodUnidade(unidadeService.getUnidadesByTokenApi(requestTokenFromApi.get()), value);
            }
        } else {
            final Optional<String> requestToken = currentRequest.getRequestToken();
            if (requestToken.isPresent()) {
                return containsCodUnidade(unidadeService.getUnidadesByTokenUser(requestToken.get()), value);
            }
        }
        return false;
    }

    private boolean containsCodUnidade(@NotNull final List<UnidadeEntity> unidades, @NotNull final Long codUnidade) {
        return unidades.stream()
                .map(UnidadeEntity::getCodigo)
                .anyMatch(codigo -> codigo.equals(codUnidade));
    }
}
