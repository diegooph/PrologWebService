package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.listagem;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created on 22/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class OrdemServicoFechadaListagem extends OrdemServicoListagem {
    static final String TIPO_SERIALIZACAO = "ORDEM_SERVICO_FECHADA";

    private LocalDateTime dataHoraFechamento;

    public OrdemServicoFechadaListagem() {
        super(TIPO_SERIALIZACAO);
    }

    @NotNull
    public static OrdemServicoFechadaListagem createDummy() {
        final OrdemServicoFechadaListagem ordem = new OrdemServicoFechadaListagem();
        ordem.setCodOrdemServico(1L);
        ordem.setCodUnidadeOrdemServico(5L);
        ordem.setPlacaVeiculo("AAA1234");
        ordem.setDataHoraAbertura(LocalDateTime.now());
        ordem.setDataHoraFechamento(LocalDateTime.now().plus(10, ChronoUnit.DAYS));
        ordem.setQtdItensPendentes(10);
        ordem.setQtdItensPendentes(3);
        return ordem;
    }

    public LocalDateTime getDataHoraFechamento() {
        return dataHoraFechamento;
    }

    public void setDataHoraFechamento(final LocalDateTime dataHoraFechamento) {
        this.dataHoraFechamento = dataHoraFechamento;
    }
}