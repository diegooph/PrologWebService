package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import java.time.LocalDateTime;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class MarcacaoAjusteEdicao extends MarcacaoAjuste {

    private LocalDateTime dataHoraOriginal;
    private LocalDateTime dataHoraNova;

    public MarcacaoAjusteEdicao(final LocalDateTime dataHoraOriginal, final LocalDateTime dataHoraNova) {
        this.dataHoraOriginal = dataHoraOriginal;
        this.dataHoraNova = dataHoraNova;
    }


}
