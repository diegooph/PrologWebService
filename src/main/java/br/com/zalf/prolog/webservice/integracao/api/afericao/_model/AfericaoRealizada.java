package br.com.zalf.prolog.webservice.integracao.api.afericao._model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoProcessoColetaAfericao;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Created on 2020-05-06
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
@Data
public class AfericaoRealizada {
    private Long codigo;
    private Long codUnidadeAfericao;
    private String cpfColaborador;
    private String placaVeiculoAferido;
    private Long codPneuAferido;
    private String numeroFogoPneu;
    private Double alturaSulcoInterno;
    private Double alturaSulcoCentralInterno;
    private Double alturaSulcoCentralExterno;
    private Double alturaSulcoExterno;
    private Double pressao;
    private Long kmVeiculoMomentoAfericao;
    private Long tempoRealizacaoEmSegundos;
    private Integer vidaPneuMomentoAfericao;
    private Integer posicaoPneuMomentoAfericao;
    private LocalDateTime dataHoraAfericao;
    private TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao;
    private TipoProcessoColetaAfericao tipoProcessoColetaAfericao;
}
