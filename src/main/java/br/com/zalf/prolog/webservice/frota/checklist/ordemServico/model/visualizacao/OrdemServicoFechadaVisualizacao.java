package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.visualizacao;

import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.visualizacao.item.ItemOrdemServicoAberto;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.visualizacao.item.ItemOrdemServicoVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class OrdemServicoFechadaVisualizacao extends OrdemServicoVisualizacao {
    public static final String TIPO_SERIALIZACAO = "ORDEM_SERVICO_FECHADA";

    private LocalDateTime dataHoraFechamento;

    public OrdemServicoFechadaVisualizacao() {
        super(TIPO_SERIALIZACAO);
    }

    @NotNull
    public static OrdemServicoFechadaVisualizacao createDummy() {
        final OrdemServicoFechadaVisualizacao ordemAberta = new OrdemServicoFechadaVisualizacao();
        ordemAberta.setCodOrdemServico(1L);
        ordemAberta.setPlacaVeiculo("AAA1234");
        ordemAberta.setDataHoraAbertura(LocalDateTime.now());
        final List<ItemOrdemServicoVisualizacao> itens = new ArrayList<>();
        itens.add(ItemOrdemServicoAberto.createDummy());
        ordemAberta.setItens(itens);
        return ordemAberta;
    }

    public LocalDateTime getDataHoraFechamento() {
        return dataHoraFechamento;
    }

    public void setDataHoraFechamento(final LocalDateTime dataHoraFechamento) {
        this.dataHoraFechamento = dataHoraFechamento;
    }
}