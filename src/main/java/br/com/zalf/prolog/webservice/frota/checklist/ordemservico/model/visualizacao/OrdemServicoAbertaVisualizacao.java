package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.item.ItemOrdemServicoPendente;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.item.ItemOrdemServicoVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta é a classe utilizada para mostrar a Visualização de Ordens de Serviço em Abertas.
 *
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class OrdemServicoAbertaVisualizacao extends OrdemServicoVisualizacao {
    static final String TIPO_SERIALIZACAO = "ORDEM_SERVICO_ABERTA";

    public OrdemServicoAbertaVisualizacao() {
        super(TIPO_SERIALIZACAO);
    }

    @NotNull
    public static OrdemServicoAbertaVisualizacao createDummy() {
        final OrdemServicoAbertaVisualizacao ordemAberta = new OrdemServicoAbertaVisualizacao();
        ordemAberta.setCodOrdemServico(1L);
        ordemAberta.setPlacaVeiculo("AAA1234");
        ordemAberta.setDataHoraAbertura(LocalDateTime.now());
        final List<ItemOrdemServicoVisualizacao> itens = new ArrayList<>();
        itens.add(ItemOrdemServicoPendente.createDummy());
        ordemAberta.setItens(itens);
        return ordemAberta;
    }
}