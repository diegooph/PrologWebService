package br.com.zalf.prolog.webservice.v3.frota.afericao._model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoProcessoColetaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

/**
 * Created on 2021-02-09
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface AfericaoPlacaProjection {
    @Value("#{target.PLACA_VEICULO}")
    String getPlacaVeiculo();

    @Value("#{target.IDENTIFICADOR_FROTA}")
    String getIdentificadorFrota();

    @Value("#{target.KM_VEICULO}")
    Long getKmVeiculo();

    @Value("#{target.COD_AFERICAO}")
    Long getCodigo();

    @Value("#{target.COD_UNIDADE}")
    Long getCodUnidade();

    @Value("#{target.DATA_HORA}")
    LocalDateTime getDataHora();

    @Value("#{target.TIPO_MEDICAO_COLETADA}")
    TipoMedicaoColetadaAfericao getTipoMedicaoColetadaAfericao();

    @Value("#{target.TIPO_PROCESSO_COLETA}")
    TipoProcessoColetaAfericao getTipoProcessoColetaAfericao();

    @Value("#{target.TEMPO_REALIZACAO}")
    Long getTempoRealizacaoAfericaoInMillis();

    @Value("#{target.FORMA_COLETA_DADOS}")
    FormaColetaDadosAfericaoEnum getFormaColetaDadosAfericao();

    @Value("#{target.CPF}")
    String getCpfAferidor();

    @Value("#{target.NOME}")
    String getNomeAferidor();
}
