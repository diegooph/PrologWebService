package br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.visualizacao;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Sulcos;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 05/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuTransferenciaProcessoVisualizacao {

    private Long codTransferenciaProcesso;
    private LocalDateTime dataHoraTransferencia;
    private List<PneuTransferenciaInformacoes> pneusTransferidos;
    private String nomeUnidadeOrigem;
    private String nomeUnidadeDestino;
    private String nomeRegionalOrigem;
    private String nomeRegionalDestino;
    private String nomeColaboradorRealizacaoTransferencia;

    public Long getCodProcessoTransferencia() {
        return codTransferenciaProcesso;
    }

    public void setCodProcessoTransferencia(Long codProcessoTransferencia) {
        this.codTransferenciaProcesso = codProcessoTransferencia;
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

    public String getNomeColaboradorRealizacaoTransferencia() {
        return nomeColaboradorRealizacaoTransferencia;
    }

    public void setNomeColaboradorRealizacaoTransferencia(String nomeColaboradorRealizacaoTransferencia) {
        this.nomeColaboradorRealizacaoTransferencia = nomeColaboradorRealizacaoTransferencia;
    }

    public List<PneuTransferenciaInformacoes> getPneusTransferidos() {
        return pneusTransferidos;
    }

    public void setPneusTransferidos(List<PneuTransferenciaInformacoes> pneusTransferidos) {
        this.pneusTransferidos = pneusTransferidos;
    }

    @NotNull
    public static PneuTransferenciaProcessoVisualizacao createDummy() {
        final PneuTransferenciaProcessoVisualizacao transferencia = new PneuTransferenciaProcessoVisualizacao();
        transferencia.setCodProcessoTransferencia(101L);
        transferencia.setDataHoraTransferencia(LocalDateTime.now());
        transferencia.setNomeUnidadeOrigem("A");
        transferencia.setNomeUnidadeDestino("B");
        transferencia.setNomeRegionalOrigem("Sul");
        transferencia.setNomeRegionalDestino("Sudeste");
        transferencia.setNomeColaboradorRealizacaoTransferencia("Gertrudes");

        final List<PneuTransferenciaInformacoes> pneusList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final Sulcos sulcos = new Sulcos();
            sulcos.setInterno(i + 13.5);
            sulcos.setCentralInterno(i + 13.4);
            sulcos.setCentralExterno(i + 13.3);
            sulcos.setExterno(i + 12.9);
            final PneuTransferenciaInformacoes pneuTransferenciaInformacoes = new PneuTransferenciaInformacoes();
            pneuTransferenciaInformacoes.setCodPneuCliente(String.valueOf(i));
            pneuTransferenciaInformacoes.setSulcosMomentoTransferencia(sulcos);
            pneuTransferenciaInformacoes.setPressaoMomentoTransferencia(i + 100.5);
            pneuTransferenciaInformacoes.setVidaMomentoTransferencia(i);
            pneusList.add(pneuTransferenciaInformacoes);
        }
        transferencia.setPneusTransferidos(pneusList);
        return transferencia;
    }
}
