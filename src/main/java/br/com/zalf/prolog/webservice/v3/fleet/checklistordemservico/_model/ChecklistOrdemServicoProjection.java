package br.com.zalf.prolog.webservice.v3.fleet.checklistordemservico._model;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface ChecklistOrdemServicoProjection {
    @Value("#{target.codigo_os_prolog}")
    long getCodigoOsProlog();

    @Value("#{target.codigo_os}")
    long getCodigoOs();

    @Value("#{target.codigo_unidade}")
    long getCodigoUnidade();

    @Value("#{target.codigo_checklist}")
    long getCodigoChecklist();

    @Value("#{@localDateTimeConverter.fromInstantUtc(target.data_hora_abertura_utc)}")
    LocalDateTime getDataHoraAberturaUtc();

    @Value("#{target.data_hora_abertura_tz_aplicado}")
    LocalDateTime getDataHoraAberturaTzAplicado();

    @Value("#{target.status_os}")
    String getStatusOs();

    @Value("#{@localDateTimeConverter.fromInstantUtc(target.data_hora_fechamento_utc)}")
    LocalDateTime getDataHoraFechamentoUtc();

    @Value("#{target.data_hora_fechamento_tz_aplicado}")
    LocalDateTime getDataHoraFechamentoTzAplicado();

    @Value("#{target.codigo_colaborador_abertura}")
    long getCodigoColaboradorAberturaOs();

    @Value("#{target.cpf_colaborador_abertura}")
    String getCpfColaboradorAberturaOs();

    @Value("#{target.nome_colaborador_abertura}")
    String getNomeColaboradorAberturaOs();

    @Value("#{target.codigo_veiculo}")
    long getCodigoVeiculo();

    @Value("#{target.placa_veiculo}")
    String getPlacaVeiculo();

    @Value("#{target.identificador_frota}")
    String getIdentificadorFrota();

    @Value("#{target.codigo_item_os}")
    long getCodigoItemOs();

    @Value("#{target.codigo_colaborador_fechamento}")
    Long getCodColaboradorFechamento();

    @Value("#{target.cpf_colaborador_fechamento}")
    Long getCpfColaboradorFechamento();

    @Value("#{target.nome_colaborador_fechamento}")
    String getNomeColaboradorFechamento();

    @Value("#{target.codigo_pergunta_primeiro_apontamento}")
    long getCodigoPerguntaPrimeiroApontamento();

    @Value("#{target.codigo_contexto_pergunta}")
    long getCodigoContextoPergunta();

    @Value("#{target.codigo_alternativa_primeiro_apontamento}")
    long getCodigoAlternativaPrimeiroApontamento();

    @Value("#{target.codigo_auxiliar_alternativa_primeiro_apontamento}")
    String getCodigoAuxiliarAlternativaPrimeiroApontamento();

    @Value("#{target.codigo_contexto_alternativa}")
    long getCodigoContextoAlternativa();

    @Value("#{target.status_resolucao}")
    String getStatusItemOs();

    @Value("#{target.quantidade_apontamentos}")
    int getQuantidadeApontamentos();

    @Value("#{target.km}")
    Long getKm();

    @Value("#{target.codigo_agrupamento_resolucao_em_lote}")
    Long getCodigoAgrupamentoResolucaoEmLote();

    @Value("#{@localDateTimeConverter.fromInstantUtc(target.data_hora_conserto_utc)}")
    LocalDateTime getDataHoraConsertoUtc();

    @Value("#{target.data_hora_conserto_tz_aplicado}")
    LocalDateTime getDataHoraConsertoTzAplicado();

    @Value("#{@localDateTimeConverter.fromInstantUtc(target.data_hora_inicio_resolucao_utc)}")
    LocalDateTime getDataHoraInicioResolucaoUtc();

    @Value("#{target.data_hora_inicio_resolucao_tz_aplicado}")
    LocalDateTime getDataHoraInicioResolucaoTzAplicado();

    @Value("#{@localDateTimeConverter.fromInstantUtc(target.data_hora_fim_resolucao_utc)}")
    LocalDateTime getDataHoraFimResolucaoUtc();

    @Value("#{target.data_hora_fim_resolucao_tz_aplicado}")
    LocalDateTime getDataHoraFimResolucaoTzAplicado();

    @Value("#{target.tempo_realizacao}")
    Long getTempoRealizacao();

    @Value("#{target.feedback_conserto}")
    String getFeedbackConserto();
}
