package br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.listagem;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Sulcos;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.PneusTransferencia;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private List<PneusTransferencia> pneus;
    private LocalDateTime dataHoraTransferencia;
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

    public List<PneusTransferencia> getPneus() {
        return pneus;
    }

    public void setPneus(List<PneusTransferencia> pneus) {
        this.pneus = pneus;
    }

    @NotNull
    public static PneuTransferenciaVisualizacao createDummy() {
        final PneuTransferenciaVisualizacao transferencia = new PneuTransferenciaVisualizacao();
        transferencia.setCodTransferencia(101L);
        transferencia.setCodUnidadeOrigem(5L);
        transferencia.setCodUnidadeDestino(3L);
        transferencia.setCodColaborador(190L);
        transferencia.setDataHoraTransferencia(LocalDateTime.now());
        transferencia.setNomeUnidadeOrigem("A");
        transferencia.setNomeUnidadeDestino("B");
        transferencia.setNomeRegionalOrigem("Sul");
        transferencia.setNomeRegionalDestino("Sudeste");
        transferencia.setNomeColaborador("Gertrudes");

        final List<PneusTransferencia> pneusList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final Sulcos sulcos = new Sulcos();
            sulcos.setInterno(i + 13.5);
            sulcos.setCentralInterno(i + 13.4);
            sulcos.setCentralExterno(i + 13.3);
            sulcos.setExterno(i + 12.9);
            final PneusTransferencia pneusTransferencia = new PneusTransferencia();
            pneusTransferencia.setCodPneuCliente(String.valueOf(i));
            pneusTransferencia.setSulcosAtuais(sulcos);
            pneusTransferencia.setPressao(i + 100.5);
            pneusTransferencia.setVidaTransferencia(i);
            pneusList.add(pneusTransferencia);
        }
        transferencia.setPneus(pneusList);
        return transferencia;
    }
}
