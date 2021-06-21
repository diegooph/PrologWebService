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

public final class CodEmpresaValidator implements ConstraintValidator<CodEmpresa, Long> {
    @NotNull
    private final CurrentRequest currentRequest;
    @NotNull
    private final UnidadeService unidadeService;

    @Autowired
    public CodEmpresaValidator(@NotNull final CurrentRequest currentRequest,
                               @NotNull final UnidadeService unidadeService) {
        this.currentRequest = currentRequest;
        this.unidadeService = unidadeService;
    }

    @Override
    public void initialize(final CodEmpresa constraintAnnotation) {
    }

    @Override
    public boolean isValid(final Long value, final ConstraintValidatorContext context) {
        if (currentRequest.isFromApi()) {
            final Optional<String> requestTokenFromApi = currentRequest.getRequestTokenFromApi();
            if (requestTokenFromApi.isPresent()) {
                return containsCodEmpresa(unidadeService.getUnidadesByTokenApi(requestTokenFromApi.get()), value);
            }
        } else {
            final Optional<String> requestToken = currentRequest.getRequestToken();
            if (requestToken.isPresent()) {
                return containsCodEmpresa(unidadeService.getUnidadesByTokenUser(requestToken.get()), value);
            }
        }
        return false;
    }

    private boolean containsCodEmpresa(@NotNull final List<UnidadeEntity> unidades, @NotNull final Long codEmpresa) {
        return unidades.stream()
                .map(unidadeEntity -> unidadeEntity.getEmpresaEntity().getCodigo())
                .distinct()
                .allMatch(codigo -> codigo.equals(codEmpresa));
    }
}