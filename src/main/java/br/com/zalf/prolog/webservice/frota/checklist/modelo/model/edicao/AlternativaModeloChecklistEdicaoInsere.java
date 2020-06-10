package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AnexoMidiaChecklistEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-09-22
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class AlternativaModeloChecklistEdicaoInsere extends AlternativaModeloChecklistEdicao {
    @NotNull
    private final String descricao;
    @NotNull
    private final PrioridadeAlternativa prioridade;
    private final boolean tipoOutros;
    private final int ordemExibicao;
    private final boolean deveAbrirOrdemServico;
    @NotNull
    private final AnexoMidiaChecklistEnum anexoMidia;

    @NotNull
    @Override
    public Long getCodigo() {
        throw new UnsupportedOperationException(AlternativaModeloChecklistEdicaoInsere.class.getSimpleName()
                + " não tem codigo");
    }

    @NotNull
    @Override
    public Long getCodigoContexto() {
        throw new UnsupportedOperationException(AlternativaModeloChecklistEdicaoInsere.class.getSimpleName()
                + " não tem codigoContexto");
    }
}
