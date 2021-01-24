package br.com.zalf.prolog.webservice.geral.unidade._model;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

/**
 * Created on 2021-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface UnidadeProjection {
    @Value("#{target.CODIGO_UNIDADE}")
    Long getCodigoUnidade();

    @Value("#{target.NOME_UNIDADE}")
    String getNomeUnidade();

    @Value("#{target.STATUS_ATIVO_UNIDADE}")
    boolean isUnidadeAtiva();

    @Value("#{target.TOTAL_COLABORADORES_UNIDADE}")
    Integer getTotalColaboradores();

    @Value("#{target.TIMEZONE_UNIDADE}")
    String getTimezoneUnidade();

    @Value("#{target.DATA_HORA_CADASTRO_UNIDADE}")
    LocalDateTime getDataHoraCadastroUnidade();

    @Value("#{target.CODIGO_AUXILIAR_UNIDADE}")
    String getCodAuxiliar();

    @Value("#{target.LATITUDE_UNIDADE}")
    String getLatitudeUnidade();

    @Value("#{target.LONGITUDE_UNIDADE}")
    String getLongitudeUnidade();

    @Value("#{target.CODIGO_REGIONAL_UNIDADE}")
    Long getCodRegional();

    @Value("#{target.NOME_REGIAO_REGIONAL_UNIDADE}")
    String getNomeRegional();
}
