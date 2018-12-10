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
public class PneuTransferenciaListagem {

    private Long codTransferencia;
    private Long codUnidadeOrigem;
    private Long codUnidadeDestino;
    private Long codColaborador;
    private LocalDateTime dataHoraTransferencia;
    /**
     * Código do cliente, número de fogo do pneu.
     */
    private List<String> codPneusCliente;
    private String nomeUnidadeOrigem;
    private String nomeUnidadeDestino;
    private String nomeRegionalOrigem;
    private String nomeRegionalDestino;
    private String nomeColaborador;


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

    public String getNomeColaborador() {
        return nomeColaborador;
    }

    public void setNomeColaborador(String nomeColaborador) {
        this.nomeColaborador = nomeColaborador;
    }

    @NotNull
    public static PneuTransferenciaListagem createDummy() {
        final PneuTransferenciaListagem transferencia = new PneuTransferenciaListagem();
        transferencia.setCodTransferencia(101L);
        transferencia.setCodUnidadeOrigem(5L);
        transferencia.setCodUnidadeDestino(3L);
        transferencia.setCodColaborador(190L);
        transferencia.setDataHoraTransferencia(LocalDateTime.now());
        final List<String> codPneusCliente = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            codPneusCliente.add(String.valueOf(i));
        }
        transferencia.setCodPneusCliente(codPneusCliente);
        transferencia.setNomeUnidadeOrigem("Floripa");
        transferencia.setNomeUnidadeDestino("Sapucaia");
        transferencia.setNomeRegionalOrigem("Sul");
        transferencia.setNomeRegionalDestino("Sudeste");
        transferencia.setNomeColaborador("Clementino");
        return transferencia;
    }
}
