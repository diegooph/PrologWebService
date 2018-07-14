package br.com.zalf.prolog.webservice.frota.pneu.pneu_tipo_servico.model;

/**
 * Created on 02/07/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class PneuServicoRealizadoNaoIncrementaVida extends PneuServicoRealizado {
    public static final String TIPO_SERVICO_REALIZADO_NAO_INCREMENTA_VIDA = "PNEU_SERVICO_REALIZADO_NAO_INCREMENTA_VIDA";

    public PneuServicoRealizadoNaoIncrementaVida() {
        this.setTipo(TIPO_SERVICO_REALIZADO_NAO_INCREMENTA_VIDA);
    }
}
