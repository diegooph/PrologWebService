package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.projections;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

/**
 * Created on 2021-02-09
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface AfericaoProjection {

    @Value("#{target.COD_AFERICAO}")
    Long getCodigo();

    @Value("#{target.COD_UNIDADE}")
    Long getCodUnidade();

    @Value("#{target.DATA_HORA}")
    LocalDateTime getDataHora();

    @Value("#{target.TIPO_MEDICAO_COLETADA}")
    TipoMedicaoColetadaAfericao getTipoMedicaoColetadaAfericao();

    @Value("#{target.TEMPO_REALIZACAO}")
    Long getTempoRealizacaoAfericaoInMillis();

    @Value("#{target.FORMA_COLETA_DADOS}")
    FormaColetaDadosAfericaoEnum getFormaColetaDadosAfericao();

    @Value("#{target.CPF}")
    String getCpfAferidor();

    @Value("#{target.NOME}")
    String getNomeAferidor();

    @Value("#{target.KM_VEICULO}")
    Long getKmVeiculo();
}
