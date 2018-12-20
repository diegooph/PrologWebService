package br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.realizacao;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 05/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuTransferenciaRealizacao {
    private Long codUnidadeOrigem;
    private Long codUnidadeDestino;
    private Long codColaboradorRealizacaoTransferencia;

    /**
     * Código do cliente, número de fogo do pneu.
     */
    private List<Long> codPneus;
    private String observacao;

    public PneuTransferenciaRealizacao() {

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

    public Long getCodColaboradorRealizacaoTransferencia() {
        return codColaboradorRealizacaoTransferencia;
    }

    public void setCodColaboradorRealizacaoTransferencia(Long codColaboradorRealizacaoTransferencia) {
        this.codColaboradorRealizacaoTransferencia = codColaboradorRealizacaoTransferencia;
    }

    public List<Long> getCodPneus() {
        return codPneus;
    }

    public void setCodPneus(List<Long> codPneus) {
        this.codPneus = codPneus;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }


    @NotNull
    public static PneuTransferenciaRealizacao createDummy() {
        final PneuTransferenciaRealizacao transferencia = new PneuTransferenciaRealizacao();
        transferencia.setCodUnidadeOrigem(5L);
        transferencia.setCodUnidadeDestino(3L);
        transferencia.setCodColaboradorRealizacaoTransferencia(190L);
        List<Long> codPneus = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            codPneus.add(Long.valueOf(i));
        }
        transferencia.setCodPneus(codPneus);
        transferencia.setObservacao("Operação Verão");

        return transferencia;
    }
}