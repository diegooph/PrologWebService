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
public final class AlternativaModeloChecklistEdicaoAtualiza extends AlternativaModeloChecklistEdicao {
    @NotNull
    private final Long codigo;
    @NotNull
    private final Long codigoContexto;
    @NotNull
    private final String descricao;
    @NotNull
    private final PrioridadeAlternativa prioridade;
    private final boolean tipoOutros;
    private final int ordemExibicao;
    private final boolean deveAbrirOrdemServico;
    @NotNull
    private final AnexoMidiaChecklistEnum capturaFotos;
}
