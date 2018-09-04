package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import java.time.LocalDateTime;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class MarcacaoAjusteAdicao extends MarcacaoAjuste {

    private Long codMarcacaoVinculo;
    private Long codColaborador;
    private Long codTipoIntervaloMarcado;
    private LocalDateTime dataHoraInserida;

    public MarcacaoAjusteAdicao(final LocalDateTime dataHoraInserida) {
        this.dataHoraInserida = dataHoraInserida;
    }
}
