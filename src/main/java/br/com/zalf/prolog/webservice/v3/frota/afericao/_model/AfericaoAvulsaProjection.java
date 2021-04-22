package br.com.zalf.prolog.webservice.v3.frota.afericao._model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoProcessoColetaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created on 2021-02-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface AfericaoAvulsaProjection {
    @Value("#{target.COD_AFERICAO}")
    Long getCodigo();

    @Value("#{target.COD_UNIDADE}")
    Long getCodUnidade();

    @Value("#{target.DATA_HORA_AFERICAO_UTC}")
    LocalDateTime getDataHoraAfericaoUtc();

    @Value("#{target.DATA_HORA_AFERICAO_TZ_APLICADO}")
    LocalDateTime getDataHoraAfericaoTzAplicado();

    @Value("#{target.TIPO_MEDICAO_COLETADA}")
    TipoMedicaoColetadaAfericao getTipoMedicaoColetadaAfericao();

    @Value("#{target.TIPO_PROCESSO_COLETA}")
    TipoProcessoColetaAfericao getTipoProcessoColetaAfericao();

    @Value("#{target.TEMPO_REALIZACAO}")
    Long getTempoRealizacaoAfericaoInMillis();

    @Value("#{target.FORMA_COLETA_DADOS}")
    FormaColetaDadosAfericaoEnum getFormaColetaDadosAfericao();

    @Value("#{target.COD_COLABORADOR}")
    Long getCodColaboradroAferidor();

    @Value("#{target.CPF}")
    String getCpfAferidor();

    @Value("#{target.NOME}")
    String getNomeAferidor();

    @Value("#{target.COD_PNEU}")
    Long getCodPneu();

    @Value("#{target.POSICAO}")
    Integer getPosicao();

    @Value("#{target.PSI}")
    BigDecimal getPsi();

    @Value("#{target.VIDA_MOMENTO_AFERICAO}")
    Integer getVidaMomentoAfericao();

    @Value("#{target.ALTURA_SULCO_INTERNO}")
    BigDecimal getAlturaSulcoInterno();

    @Value("#{target.ALTURA_SULCO_CENTRAL_INTERNO}")
    BigDecimal getAlturaSulcoCentralInterno();

    @Value("#{target.ALTURA_SULCO_CENTRAL_EXTERNO}")
    BigDecimal getAlturaSulcoCentralExterno();

    @Value("#{target.ALTURA_SULCO_EXTERNO}")
    BigDecimal getAlturaSulcoExterno();
}