package br.com.zalf.prolog.webservice.integracao.protheusrodalog.model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.TipoMedicaoColetadaAfericao;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 27/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class AfericaoProtheusRodalog {
    private String placaAfericao;
    private Long codUnidade;
    private String cpfColaboradorAfericao;
    private Long kmMomentoAfericao;
    private Long tempoRealizacaoAfericaoInMillis;
    private LocalDateTime dataHora;
    private TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao;
    private List<MedicaoAfericaoProtheusRodalog> medicoes;
}
