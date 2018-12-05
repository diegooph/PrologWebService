package br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.visualizacao;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 05/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuTransferenciaVisualizacao {

    private Long codTransferencia;
    private Long codUnidadeOrigem;
    private Long codUnidadeDestino;
    private Long codColaborador;
    private LocalDateTime dataHoraTransferencia;


    public Long getCodTransferencia() {
        return codTransferencia;
    }

    public void setCodTransferencia(Long codTransferencia) {
        this.codTransferencia = codTransferencia;
    }

    public Long getCodUnidadeOrigem() {
        return codUnidadeOrigem;
    }

    public void setCodUnidadeOrigem(Long codUnidadeOrigem) {
        this.codUnidadeOrigem = codUnidadeOrigem;
    }

    public Long getCodUnidadeDestino() {
        return codUnidadeDestino;
    }

    public void setCodUnidadeDestino(Long codUnidadeDestino) {
        this.codUnidadeDestino = codUnidadeDestino;
    }

    public Long getCodColaborador() {
        return codColaborador;
    }

    public void setCodColaborador(Long codColaborador) {
        this.codColaborador = codColaborador;
    }

    public LocalDateTime getDataHoraTransferencia() {
        return dataHoraTransferencia;
    }

    public void setDataHoraTransferencia(LocalDateTime dataHoraTransferencia) {
        this.dataHoraTransferencia = dataHoraTransferencia;
    }

    @NotNull
    public static PneuTransferenciaVisualizacao createDummy() {
        final PneuTransferenciaVisualizacao transferencia = new PneuTransferenciaVisualizacao();
        transferencia.setCodTransferencia(101L);
        transferencia.setCodUnidadeOrigem(5L);
        transferencia.setCodUnidadeDestino(3L);
        transferencia.setCodColaborador(190L);
        transferencia.setDataHoraTransferencia(LocalDateTime.now());
        return transferencia;
    }
}
