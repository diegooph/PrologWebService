package br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.realizacao;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 05/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuTransferenciaRealizacao {

    private Long codUnidadeOrigem;
    private Long codUnidadeDestino;
    private Long codColaborador;
    private LocalDateTime dataHoraTransferencia;

    /**
     * Código do cliente, número de fogo do pneu.
     */
    private List<String> codPneusCliente;
    private String observacao;

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

    public List<String> getCodPneusCliente() {
        return codPneusCliente;
    }

    public void setCodPneusCliente(List<String> codPneusCliente) {
        this.codPneusCliente = codPneusCliente;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public LocalDateTime getDataHoraTransferencia() {
        return dataHoraTransferencia;
    }

    public void setDataHoraTransferencia(LocalDateTime dataHoraTransferencia) {
        this.dataHoraTransferencia = dataHoraTransferencia;
    }


    @NotNull
    public static PneuTransferenciaRealizacao createDummy() {
        final PneuTransferenciaRealizacao transferencia = new PneuTransferenciaRealizacao();
        transferencia.setCodUnidadeOrigem(5L);
        transferencia.setCodUnidadeDestino(3L);
        transferencia.setCodColaborador(190L);
        transferencia.setDataHoraTransferencia(LocalDateTime.now());
        List<String> codPneusCliente = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            codPneusCliente.add(String.valueOf(i));
        }
        transferencia.setCodPneusCliente(codPneusCliente);
        transferencia.setObservacao("Operação Verão");

        return transferencia;
    }
}
