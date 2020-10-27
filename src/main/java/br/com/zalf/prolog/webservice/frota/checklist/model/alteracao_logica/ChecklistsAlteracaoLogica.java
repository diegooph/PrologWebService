package br.com.zalf.prolog.webservice.frota.checklist.model.alteracao_logica;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 2020-10-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Data(staticConstructor = "of")
public class ChecklistsAlteracaoLogica {
    @NotNull
    private final List<Long> codigos;
    @NotNull
    private final Long codigoColaborador;
    @NotNull
    private final ChecklistAlteracaoAcao acaoExecutada;
    @NotNull
    private final OrigemAcaoEnum origemDelecao;
    @Nullable
    private final String observacao;
}
