package br.com.zalf.prolog.webservice.frota.checklist.model.delecao;
import br.com.zalf.prolog.webservice.frota.checklist.model.delecao.CheckListDelecaoAcao;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.List;

/**
 * Created on 2020-10-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Data
@AllArgsConstructor
public class CheckListsDelecao {
    @NonNull
    private final List<Long> checklists;
    @NonNull
    private final Long codigoColaborador;
    @NonNull
    private final CheckListDelecaoAcao acaoExecutada;
    @NonNull
    private final OrigemAcaoEnum origemDelecao;
    private final String observacao;
}
