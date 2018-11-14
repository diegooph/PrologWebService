package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.visualizacao;

import java.time.LocalDateTime;

/**
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class OrdemServicoFechadaVisualizacao extends OrdemServicoVisualizacao {
    private LocalDateTime dataHoraFechamento;

    public OrdemServicoFechadaVisualizacao() {

    }

    public LocalDateTime getDataHoraFechamento() {
        return dataHoraFechamento;
    }

    public void setDataHoraFechamento(final LocalDateTime dataHoraFechamento) {
        this.dataHoraFechamento = dataHoraFechamento;
    }
}