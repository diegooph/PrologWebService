package br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model;

/**
 * Created on 30/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class PneuServicoRealizadoIncrementaVida extends PneuServicoRealizado {
    public static final String TIPO_SERVICO_REALIZADO_INCREMENTA_VIDA = "PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA";

    private Long codModeloBanda;
    private int vidaNovaPneu;

    public PneuServicoRealizadoIncrementaVida() {
        this.setTipo(TIPO_SERVICO_REALIZADO_INCREMENTA_VIDA);
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
        return "PneuServicoRealizadoIncrementaVida{" +
                "codModeloBanda=" + codModeloBanda +
                ", vidaNovaPneu=" + vidaNovaPneu +
                '}';
    }
}
