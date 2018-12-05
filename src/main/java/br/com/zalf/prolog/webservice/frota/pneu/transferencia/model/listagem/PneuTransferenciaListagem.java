package br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.listagem;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Sulcos;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

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
    private Long codPneu;
    private Sulcos sulcosAtuais;
    private double pressao;
    private int vidaTransferencia;

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

    public Long getCodPneu() {
        return codPneu;
    }

    public void setCodPneu(Long codPneu) {
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

    @NotNull
    public static PneuTransferenciaListagem createDummy() {
        final PneuTransferenciaListagem transferencia = new PneuTransferenciaListagem();
        transferencia.setCodTransferencia(101L);
        transferencia.setCodUnidadeOrigem(5L);
        transferencia.setCodUnidadeDestino(3L);
        transferencia.setCodColaborador(190L);
        transferencia.setDataHoraTransferencia(LocalDateTime.now());
        transferencia.setCodPneu(1541L);

        final Sulcos sulcos = new Sulcos();
        sulcos.setInterno(13.5);
        sulcos.setCentralInterno(13.4);
        sulcos.setCentralExterno(13.3);
        sulcos.setExterno(12.9);
        transferencia.setSulcosAtuais(sulcos);
        transferencia.setPressao(100.5);
        transferencia.setVidaTransferencia(2);
        return transferencia;
    }
}
