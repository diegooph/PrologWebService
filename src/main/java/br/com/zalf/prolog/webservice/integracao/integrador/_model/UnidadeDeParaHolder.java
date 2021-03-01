package br.com.zalf.prolog.webservice.integracao.integrador._model;

import br.com.zalf.prolog.webservice.integracao.webfinatto.utils.SistemaWebFinattoConstants;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@Data
public final class UnidadeDeParaHolder {
    @NotNull
    private final Long codEmpresaProlog;
    @NotNull
    private final List<UnidadeDePara> unidadesDePara;

    public boolean isEmpty() {
        return unidadesDePara.isEmpty();
    }

    @NotNull
    public String getCodFiliais() {
        return unidadesDePara
                .stream()
                .map(UnidadeDePara::getCodAuxiliarUnidade)
                .distinct()
                .collect(Collectors.joining(SistemaWebFinattoConstants.SEPARADOR_FILIAIS_REQUEST));
    }

    @NotNull
    public List<Long> getCodUnidadesMapeadas() {
        return unidadesDePara
                .stream()
                .map(UnidadeDePara::getCodUnidadeProlog)
                .distinct()
                .collect(Collectors.toList());
    }

    @Nullable
    public UnidadeDePara getByCodAuxiliar(@NotNull final String codEmpresaFilialVeiculo) {
        return unidadesDePara
                .stream()
                .filter(unidadeDePara ->
                                unidadeDePara.getCodAuxiliarUnidade().equalsIgnoreCase(codEmpresaFilialVeiculo))
                .findAny()
                .orElse(null);
    }
}
