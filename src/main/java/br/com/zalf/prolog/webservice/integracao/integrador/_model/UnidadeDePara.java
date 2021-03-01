package br.com.zalf.prolog.webservice.integracao.integrador._model;

import br.com.zalf.prolog.webservice.integracao.webfinatto.utils.SistemaWebFinattoConstants;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public final class UnidadeDePara {
    @NotNull
    private final Long codUnidadeProlog;
    @NotNull
    private final String nomeUnidadeProlog;
    @NotNull
    private final Long codRegionalProlog;
    @NotNull
    private final String nomeRegionalProlog;
    @NotNull
    private final String codAuxiliarUnidade;

    @NotNull
    public String getCodEmpresaFromCodAuxiliarUnidade() {
        final String[] empresaFilial = codAuxiliarUnidade.split(SistemaWebFinattoConstants.SEPARADOR_EMPRESA_FILIAL);
        return empresaFilial[0];
    }

    @NotNull
    public String getCodFilialFromCodAuxiliarUnidade() {
        final String[] empresaFilial = codAuxiliarUnidade.split(SistemaWebFinattoConstants.SEPARADOR_EMPRESA_FILIAL);
        return empresaFilial[1];
    }
}
