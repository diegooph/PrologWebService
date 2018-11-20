package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.listagem;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class OrdemServicoListagem {
    private Long codOrdemServico;
    private Long codUnidadeOrdemServico;
    private String placaVeiculo;
    private LocalDateTime dataHoraAbertura;
    private int qtdItensAbertos;
    private int qtdItensFechados;

    public OrdemServicoListagem() {

    }

    @NotNull
    public static OrdemServicoListagem createDummy() {
        final OrdemServicoListagem ordem = new OrdemServicoListagem();
        ordem.setCodOrdemServico(1L);
        ordem.setCodUnidadeOrdemServico(5L);
        ordem.setPlacaVeiculo("AAA1234");
        ordem.setDataHoraAbertura(LocalDateTime.now());
        ordem.setQtdItensAbertos(10);
        ordem.setQtdItensAbertos(3);
        return ordem;
    }

    public Long getCodOrdemServico() {
        return codOrdemServico;
    }

    public void setCodOrdemServico(final Long codOrdemServico) {
        this.codOrdemServico = codOrdemServico;
    }

    public Long getCodUnidadeOrdemServico() {
        return codUnidadeOrdemServico;
    }

    public void setCodUnidadeOrdemServico(final Long codUnidadeOrdemServico) {
        this.codUnidadeOrdemServico = codUnidadeOrdemServico;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(final String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public LocalDateTime getDataHoraAbertura() {
        return dataHoraAbertura;
    }

    public void setDataHoraAbertura(final LocalDateTime dataHoraAbertura) {
        this.dataHoraAbertura = dataHoraAbertura;
    }

    public int getQtdItensAbertos() {
        return qtdItensAbertos;
    }

    public void setQtdItensAbertos(final int qtdItensAbertos) {
        this.qtdItensAbertos = qtdItensAbertos;
    }

    public int getQtdItensFechados() {
        return qtdItensFechados;
    }

    public void setQtdItensFechados(final int qtdItensFechados) {
        this.qtdItensFechados = qtdItensFechados;
    }
}