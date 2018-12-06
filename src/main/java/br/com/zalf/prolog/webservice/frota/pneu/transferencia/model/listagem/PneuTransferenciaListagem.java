package br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.listagem;

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
public class PneuTransferenciaListagem {

    private Long codTransferencia;
    private Long codUnidadeOrigem;
    private Long codUnidadeDestino;
    private Long codColaborador;
    private LocalDateTime dataHoraTransferencia;
    private List<Long> codPneu;
    private Sulcos sulcosAtuais;
    private double pressao;
    private int vidaTransferencia;
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

    public List<Long> getCodPneu() {
        return codPneu;
    }

    public void setCodPneu(List<Long> codPneu) {
        this.codPneu = codPneu;
    }

    public Sulcos getSulcosAtuais() {
        return sulcosAtuais;
    }

    public void setSulcosAtuais(Sulcos sulcosAtuais) {
        this.sulcosAtuais = sulcosAtuais;
    }

    public double getPressao() {
        return pressao;
    }

    public void setPressao(double pressao) {
        this.pressao = pressao;
    }

    public int getVidaTransferencia() {
        return vidaTransferencia;
    }

    public void setVidaTransferencia(int vidaTransferencia) {
        this.vidaTransferencia = vidaTransferencia;
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
        transferencia.setNomeUnidadeOrigem("A");
        transferencia.setNomeUnidadeDestino("B");
        transferencia.setNomeRegionalOrigem("Sul");
        transferencia.setNomeRegionalDestino("Sudeste");
        transferencia.setNomeColaborador("Gertrudes");

        List<Long> codPneus = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            codPneus.add(Long.valueOf(i));
            final Sulcos sulcos = new Sulcos();
            sulcos.setInterno(i + 13.5);
            sulcos.setCentralInterno(i + 13.4);
            sulcos.setCentralExterno(i + 13.3);
            sulcos.setExterno(i + 12.9);
            transferencia.setSulcosAtuais(sulcos);
            transferencia.setPressao(i + 100.5);
            transferencia.setVidaTransferencia(i);
        }
        return transferencia;
    }
}
