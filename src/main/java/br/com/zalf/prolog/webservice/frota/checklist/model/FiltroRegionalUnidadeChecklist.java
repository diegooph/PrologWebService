package br.com.zalf.prolog.webservice.frota.checklist.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2019-06-10
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FiltroRegionalUnidadeChecklist {
    @NotNull
    private final Long codColaborador;
    @NotNull
    private final List<RegionalSelecaoChecklist> regionaisSelecao;
    private final boolean checklistDiferentesUnidadesAtivoEmpresa;

    public FiltroRegionalUnidadeChecklist(@NotNull final Long codColaborador,
                                          @NotNull final List<RegionalSelecaoChecklist> regionaisSelecao,
                                          final boolean checklistDiferentesUnidadesAtivoEmpresa) {
        this.codColaborador = codColaborador;
        this.regionaisSelecao = regionaisSelecao;
        this.checklistDiferentesUnidadesAtivoEmpresa = checklistDiferentesUnidadesAtivoEmpresa;
    }

    @NotNull
    public Long getCodColaborador() {
        return codColaborador;
    }

    @NotNull
    public List<RegionalSelecaoChecklist> getRegionaisSelecao() {
        return regionaisSelecao;
    }

    public boolean isChecklistDiferentesUnidadesAtivoEmpresa() {
        return checklistDiferentesUnidadesAtivoEmpresa;
    }

    public boolean temApenasUmaRegionalComUmaUnidade() {
        return regionaisSelecao.size() == 1
                && regionaisSelecao.get(0).getUnidadesVinculadas().size() == 1;
    }
}