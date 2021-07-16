package br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface ChecklistWorkOrderProjection {
    @Value("#{target.codigo_os_prolog}")
    long getWorkOrderIdProlog();

    @Value("#{target.codigo_os}")
    long getWorkOrderId();

    @Value("#{target.codigo_unidade}")
    long getBranchId();

    @Value("#{target.codigo_checklist}")
    long getChecklistId();

    @Value("#{@localDateTimeConverter.fromInstantUtc(target.data_hora_abertura_utc)}")
    LocalDateTime getOpenedAtUtc();

    @Value("#{target.data_hora_abertura_tz_aplicado}")
    LocalDateTime getOpenedAtWithTimeZone();

    @Value("#{target.status_os}")
    String getWorkOrderStatus();

    @Value("#{@localDateTimeConverter.fromInstantUtc(target.data_hora_fechamento_utc)}")
    LocalDateTime getClosedAtUtc();

    @Value("#{target.data_hora_fechamento_tz_aplicado}")
    LocalDateTime getClosedAtWithTimeZone();

    @Value("#{target.codigo_colaborador_abertura}")
    long getChecklistUserId();

    @Value("#{target.cpf_colaborador_abertura}")
    String getChecklistUserCpf();

    @Value("#{target.nome_colaborador_abertura}")
    String getChecklistUserName();

    @Value("#{target.codigo_veiculo}")
    long getVehicleId();

    @Value("#{target.placa_veiculo}")
    String getVehiclePlate();

    @Value("#{target.identificador_frota}")
    String getFleetId();

    @Value("#{target.codigo_item_os}")
    long getWorkOrderItemId();

    @Value("#{target.codigo_colaborador_fechamento}")
    Long getResolverUserId();

    @Value("#{target.cpf_colaborador_fechamento}")
    Long getResolverUserCpf();

    @Value("#{target.nome_colaborador_fechamento}")
    String getResolverUserName();

    @Value("#{target.codigo_pergunta_primeiro_apontamento}")
    long getQuestionId();

    @Value("#{target.codigo_contexto_pergunta}")
    long getQuestionContextId();

    @Value("#{target.codigo_alternativa_primeiro_apontamento}")
    long getOptionId();

    @Value("#{target.codigo_auxiliar_alternativa_primeiro_apontamento}")
    String getOptionAddicionalId();

    @Value("#{target.codigo_contexto_alternativa}")
    long getOptionContextId();

    @Value("#{target.status_resolucao}")
    String getWorkOrderItemStatus();

    @Value("#{target.quantidade_apontamentos}")
    int getAmountTimesPointed();

    @Value("#{target.km}")
    Long getKm();

    @Value("#{target.codigo_agrupamento_resolucao_em_lote}")
    Long getBatchGroupId();

    @Value("#{@localDateTimeConverter.fromInstantUtc(target.data_hora_conserto_utc)}")
    LocalDateTime getResolvedAtUtc();

    @Value("#{target.data_hora_conserto_tz_aplicado}")
    LocalDateTime getResolvedAtWithTimeZone();

    @Value("#{@localDateTimeConverter.fromInstantUtc(target.data_hora_inicio_resolucao_utc)}")
    LocalDateTime getStartedAtUtc();

    @Value("#{target.data_hora_inicio_resolucao_tz_aplicado}")
    LocalDateTime getStartedAtWithTimeZone();

    @Value("#{@localDateTimeConverter.fromInstantUtc(target.data_hora_fim_resolucao_utc)}")
    LocalDateTime getEndedAtUtc();

    @Value("#{target.data_hora_fim_resolucao_tz_aplicado}")
    LocalDateTime getEndedAtWithTimeZone();

    @Value("#{target.tempo_realizacao}")
    Long getResolutionTime();

    @Value("#{target.feedback_conserto}")
    String getResolutionNotes();
}
