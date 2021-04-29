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
    private final String nomeEmpresaProlog;
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
                .filter(unidadeDePara -> unidadeDePara.getCodAuxiliarUnidade() != null)
                .map(UnidadeDePara::getCodUnidadeProlog)
                .distinct()
                .collect(Collectors.toList());
    }

    @Nullable
    public UnidadeDePara getByCodAuxiliar(@NotNull final String codEmpresaFilialVeiculo) {
        return unidadesDePara
                .stream()
                .filter(unidadeDePara -> unidadeDePara.getCodAuxiliarUnidade() != null)
                .filter(unidadeDePara ->
                                unidadeDePara.getCodAuxiliarUnidade().equalsIgnoreCase(codEmpresaFilialVeiculo))
                .findAny()
                .orElse(null);
    }

    public boolean isCodUnidadePrologMapeado(@NotNull final Long codUnidadeProlog) {
        return unidadesDePara.stream()
                .filter(unidadeDePara -> unidadeDePara.getCodUnidadeProlog().equals(codUnidadeProlog))
                .findFirst()
                .filter(unidadeDePara -> unidadeDePara.getCodAuxiliarUnidade() != null)
                .isPresent();
    }

    @NotNull
    public String getCodAuxiliarEmpresa() {
        final String[] empresaFilial = getCodFiliais().split(SistemaWebFinattoConstants.SEPARADOR_EMPRESA_FILIAL);
        return empresaFilial[SistemaWebFinattoConstants.COD_EMPRESA_INDEX];
    }

    @NotNull
    public String getCodAuxiliarFilial() {
        final String[] empresaFilial = getCodFiliais().split(SistemaWebFinattoConstants.SEPARADOR_EMPRESA_FILIAL);
        return empresaFilial[SistemaWebFinattoConstants.COD_FILIAL_INDEX];
    }
}
