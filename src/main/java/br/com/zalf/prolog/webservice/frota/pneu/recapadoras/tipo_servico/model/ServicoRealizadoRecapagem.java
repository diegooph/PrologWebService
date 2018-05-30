package br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico.model;

/**
 * Created on 30/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ServicoRealizadoRecapagem extends ServicoRealizadoRecapadora {
    public static final String TIPO_SERVICO_REALIZADO_RECAPAGEM = "SERVICO_REALIZADO_RECAPAGE";

    private Long codModeloBanda;
    private Integer vidaNovaPneu;

    public ServicoRealizadoRecapagem() {
        this.setTipo(TIPO_SERVICO_REALIZADO_RECAPAGEM);
    }

    public Long getCodModeloBanda() {
        return codModeloBanda;
    }

    public void setCodModeloBanda(final Long codModeloBanda) {
        this.codModeloBanda = codModeloBanda;
    }

    public Integer getVidaNovaPneu() {
        return vidaNovaPneu;
    }

    public void setVidaNovaPneu(final Integer vidaNovaPneu) {
        this.vidaNovaPneu = vidaNovaPneu;
    }

    @Override
    public String toString() {
        return "ServicoRealizadoRecapagem{" +
                "codModeloBanda=" + codModeloBanda +
                ", vidaNovaPneu=" + vidaNovaPneu +
                '}';
    }
}
