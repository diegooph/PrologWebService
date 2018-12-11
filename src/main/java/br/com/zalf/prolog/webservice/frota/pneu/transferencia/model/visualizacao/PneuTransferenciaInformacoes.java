package br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.visualizacao;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Sulcos;

/**
 * Created on 06/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuTransferenciaInformacoes {

    private Long codPneuTransferenciaInformacoes;
    /**
     * Código do cliente, número de fogo do pneu.
     */
    private String codPneuCliente;
    private Sulcos sulcosMomentoTransferencia;
    private double pressaoMomentoTransferencia;
    private int vidaMomentoTransferencia;

    public String getCodPneuCliente() {
        return codPneuCliente;
    }

    public void setCodPneuCliente(String codPneuCliente) {
        this.codPneuCliente = codPneuCliente;
    }

    public Sulcos getSulcosMomentoTransferencia() {
        return sulcosMomentoTransferencia;
    }

    public void setSulcosMomentoTransferencia(Sulcos sulcosMomentoTransferencia) {
        this.sulcosMomentoTransferencia = sulcosMomentoTransferencia;
    }

    public double getPressaoMomentoTransferencia() {
        return pressaoMomentoTransferencia;
    }

    public void setPressaoMomentoTransferencia(double pressaoMomentoTransferencia) {
        this.pressaoMomentoTransferencia = pressaoMomentoTransferencia;
    }

    public int getVidaMomentoTransferencia() {
        return vidaMomentoTransferencia;
    }

    public void setVidaMomentoTransferencia(int vidaMomentoTransferencia) {
        this.vidaMomentoTransferencia = vidaMomentoTransferencia;
    }

    public Long getCodPneuTransferenciaInformacoes() {
        return codPneuTransferenciaInformacoes;
    }

    public void setCodPneuTransferenciaInformacoes(Long codPneuTransferenciaInformacoes) {
        this.codPneuTransferenciaInformacoes = codPneuTransferenciaInformacoes;
    }
}
