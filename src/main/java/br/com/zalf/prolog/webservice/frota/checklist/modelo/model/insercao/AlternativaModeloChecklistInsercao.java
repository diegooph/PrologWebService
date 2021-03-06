package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AnexoMidiaChecklistEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 10/12/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class AlternativaModeloChecklistInsercao extends AlternativaModeloChecklist {
    @NotNull
    private final String descricao;
    @NotNull
    private final PrioridadeAlternativa prioridade;
    private final boolean tipoOutros;
    private final int ordemExibicao;
    private final boolean deveAbrirOrdemServico;
    @NotNull
    private final AnexoMidiaChecklistEnum anexoMidia;
    @Nullable
    private final String codAuxiliar;

    @NotNull
    @Override
    public Long getCodigo() {
        throw new UnsupportedOperationException("Uma alternativa de inserção não tem código");
    }

    @NotNull
    @Override
    public Long getCodigoContexto() {
        throw new UnsupportedOperationException("Uma alternativa de inserção não tem código contexto");
    }
}