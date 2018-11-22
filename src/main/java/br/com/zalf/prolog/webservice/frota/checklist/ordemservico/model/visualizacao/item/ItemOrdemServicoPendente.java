package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.item;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ItemOrdemServicoPendente extends ItemOrdemServicoVisualizacao {
    public static final String TIPO_SERIALIZACAO = "ITEM_RESOLVIDO";

    public ItemOrdemServicoPendente() {
        super(TIPO_SERIALIZACAO);
    }

    @NotNull
    public static ItemOrdemServicoPendente createDummy() {
        final ItemOrdemServicoPendente item = new ItemOrdemServicoPendente();
        item.setCodigo(1L);
        item.setCodOrdemServico(2L);
        item.setCodUnidadeItemOrdemServico(5L);
        item.setPergunta(PerguntaItemOrdemServico.createDummy());
        item.setDataHoraPrimeiroApontamento(LocalDateTime.now().minus(30, ChronoUnit.DAYS));
        item.setStatus(StatusItemOrdemServico.PENDENTE);
        item.setPrazoResolucaoItem(Duration.ofMinutes(42));
        item.setPrazoRestanteResolucaoItem(Duration.ofMinutes(20));
        item.setQtdApontamentos(10);
        return item;
    }
}