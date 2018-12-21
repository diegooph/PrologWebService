package br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.listagem;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 05/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuTransferenciaListagem {
    private Long codTransferenciaProcesso;
    private LocalDateTime dataHoraTransferenciaProcesso;
    /**
     * Código do cliente, número de fogo do pneu.
     */
    private List<String> codPneusCliente;
    private String nomeUnidadeOrigem;
    private String nomeUnidadeDestino;
    private String nomeRegionalOrigem;
    private String nomeRegionalDestino;
    private String nomeColaboradorRealizacaoTransferencia;
    private String observacaoTransferenciaProcesso;

    public PneuTransferenciaListagem() {

    }

    public Long getCodTransferenciaProcesso() {
        return codTransferenciaProcesso;
    }

    public void setCodTransferenciaProcesso(Long codTransferenciaProcesso) {
        this.codTransferenciaProcesso = codTransferenciaProcesso;
    }

    public LocalDateTime getDataHoraTransferenciaProcesso() {
        return dataHoraTransferenciaProcesso;
    }

    public void setDataHoraTransferenciaProcesso(LocalDateTime dataHoraTransferenciaProcesso) {
        this.dataHoraTransferenciaProcesso = dataHoraTransferenciaProcesso;
    }

    public List<String> getCodPneusCliente() {
        return codPneusCliente;
    }

    public void setCodPneusCliente(List<String> codPneusCliente) {
        this.codPneusCliente = codPneusCliente;
    }

    public String getNomeUnidadeOrigem() {
        return nomeUnidadeOrigem;
    }

    public void setNomeUnidadeOrigem(String nomeUnidadeOrigem) {
        this.nomeUnidadeOrigem = nomeUnidadeOrigem;
    }

    public String getNomeUnidadeDestino() {
        return nomeUnidadeDestino;
    }

    public void setNomeUnidadeDestino(String nomeUnidadeDestino) {
        this.nomeUnidadeDestino = nomeUnidadeDestino;
    }

    public String getNomeRegionalOrigem() {
        return nomeRegionalOrigem;
    }

    public void setNomeRegionalOrigem(String nomeRegionalOrigem) {
        this.nomeRegionalOrigem = nomeRegionalOrigem;
    }

    public String getNomeRegionalDestino() {
        return nomeRegionalDestino;
    }

    public void setNomeRegionalDestino(String nomeRegionalDestino) {
        this.nomeRegionalDestino = nomeRegionalDestino;
    }

    public String getNomeColaboradorRealizacaoTransferencia() {
        return nomeColaboradorRealizacaoTransferencia;
    }

    public void setNomeColaboradorRealizacaoTransferencia(String nomeColaboradorRealizacaoTransferencia) {
        this.nomeColaboradorRealizacaoTransferencia = nomeColaboradorRealizacaoTransferencia;
    }

    public String getObservacaoTransferenciaProcesso() {
        return observacaoTransferenciaProcesso;
    }

    public void setObservacaoTransferenciaProcesso(String observacaoTransferenciaProcesso) {
        this.observacaoTransferenciaProcesso = observacaoTransferenciaProcesso;
    }

    @NotNull
    public static PneuTransferenciaListagem createDummy() {
        final PneuTransferenciaListagem transferencia = new PneuTransferenciaListagem();
        transferencia.setCodTransferenciaProcesso(101L);
        transferencia.setDataHoraTransferenciaProcesso(LocalDateTime.now());
        final List<String> codPneusCliente = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            codPneusCliente.add(String.valueOf(i));
        }
        transferencia.setCodPneusCliente(codPneusCliente);
        transferencia.setNomeUnidadeOrigem("Floripa");
        transferencia.setNomeUnidadeDestino("Sapucaia");
        transferencia.setNomeRegionalOrigem("Sul");
        transferencia.setNomeRegionalDestino("Sudeste");
        transferencia.setNomeColaboradorRealizacaoTransferencia("Clementino");
        transferencia.setObservacaoTransferenciaProcesso("Operação Verão");
        return transferencia;
    }
}