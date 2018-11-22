package br.com.zalf.prolog.webservice.frota.checklist.ordemServico;

import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.listagem.OrdemServicoAbertaListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.listagem.OrdemServicoFechadaListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.listagem.OrdemServicoListagem;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.time.LocalDateTime;

/**
 * Created on 22/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class OrdemServicoConverter {

    private OrdemServicoConverter() {
        throw new IllegalStateException(OrdemServicoConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    static OrdemServicoListagem createOrdemServicoListagem(@NotNull final ResultSet rSet) throws Throwable {
        final StatusOrdemServico status = StatusOrdemServico.fromString(rSet.getString("STATUS_OS"));

        final OrdemServicoListagem ordem;
        if (status.equals(StatusOrdemServico.ABERTA)) {
            ordem = new OrdemServicoAbertaListagem();
        } else {
            ordem = new OrdemServicoFechadaListagem();
            final LocalDateTime dataHoraFechamento = rSet.getObject("DATA_HORA_FECHAMENTO", LocalDateTime.class);
            ((OrdemServicoFechadaListagem) ordem).setDataHoraFechamento(dataHoraFechamento);
        }
        ordem.setCodOrdemServico(rSet.getLong("COD_OS"));
        ordem.setCodUnidadeOrdemServico(rSet.getLong("COD_UNIDADE_OS"));
        ordem.setPlacaVeiculo(rSet.getString("PLACA_VEICULO"));
        ordem.setDataHoraAbertura(rSet.getObject("DATA_HORA_ABERTURA", LocalDateTime.class));
        ordem.setQtdItensPendentes(rSet.getInt("QTD_ITENS_PENDENTES"));
        ordem.setQtdItensPendentes(rSet.getInt("QTD_ITENS_RESOLVIDOS"));
        return ordem;
    }
}