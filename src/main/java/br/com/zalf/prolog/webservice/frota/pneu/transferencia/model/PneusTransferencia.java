package br.com.zalf.prolog.webservice.frota.pneu.transferencia.model;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Sulcos;

/**
 * Created on 06/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneusTransferencia {

    private Long codPneu;
    private Sulcos sulcosAtuais;
    private double pressao;
    private int vidaTransferencia;

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
}
