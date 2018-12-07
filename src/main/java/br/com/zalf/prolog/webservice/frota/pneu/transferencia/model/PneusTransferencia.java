package br.com.zalf.prolog.webservice.frota.pneu.transferencia.model;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Sulcos;

/**
 * Created on 06/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneusTransferencia {

    /**
     * Código do cliente, número de fogo do pneu.
     */
    private String codPneuCliente;
    private Sulcos sulcosAtuais;
    private double pressao;
    private int vidaTransferencia;

    public String getCodPneuCliente() {
        return codPneuCliente;
    }

    public void setCodPneuCliente(String codPneuCliente) {
        this.codPneuCliente = codPneuCliente;
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
