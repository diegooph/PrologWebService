package br.com.zalf.prolog.webservice.frota.pneu.pneu_tipo_servico.model;

/**
 * Created on 30/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class PneuServicoRealizadoRecapagem extends PneuServicoRealizado {
    public static final String TIPO_PNEU_SERVICO_REALIZADO_RECAPAGEM = "PNEU_SERVICO_REALIZADO_RECAPAGEM";

    private Long codModeloBanda;
    private int vidaNovaPneu;

    public PneuServicoRealizadoRecapagem() {
        this.setTipo(TIPO_PNEU_SERVICO_REALIZADO_RECAPAGEM);
    }

    public Long getCodModeloBanda() {
        return codModeloBanda;
    }

    public void setCodModeloBanda(final Long codModeloBanda) {
        this.codModeloBanda = codModeloBanda;
    }

    public int getVidaNovaPneu() {
        return vidaNovaPneu;
    }

    public void setVidaNovaPneu(final int vidaNovaPneu) {
        this.vidaNovaPneu = vidaNovaPneu;
    }

    @Override
    public String toString() {
        return "PneuServicoRealizadoRecapagem{" +
                "codModeloBanda=" + codModeloBanda +
                ", vidaNovaPneu=" + vidaNovaPneu +
                '}';
    }
}
