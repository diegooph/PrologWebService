package br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface ChecklistWorkOrderProjection {
    @Value("#{target.work_order_id_prolog}")
    long getWorkOrderIdProlog();

    @Value("#{target.work_order_id}")
    long getWorkOrderId();

    @Value("#{target.branch_id}")
    long getBranchId();

    @Value("#{target.checklist_id}")
    long getChecklistId();

    @Value("#{@localDateTimeConverter.fromInstantUtc(target.opned_at_utc)}")
    LocalDateTime getOpenedAtUtc();

    @Value("#{target.opned_at_with_tz}")
    LocalDateTime getOpenedAtWithTimeZone();

    @Value("#{target.work_order_status}")
    String getWorkOrderStatus();

    @Value("#{@localDateTimeConverter.fromInstantUtc(target.closed_at_utc)}")
    LocalDateTime getClosedAtUtc();

    @Value("#{target.closed_at_with_tz}")
    LocalDateTime getClosedAtWithTimeZone();

    @Value("#{target.opned_by_user_id}")
    long getChecklistUserId();

    @Value("#{target.opned_by_user_cpf}")
    String getChecklistUserCpf();

    @Value("#{target.opned_by_user_name}")
    String getChecklistUserName();

    @Value("#{target.vehicle_id}")
    long getVehicleId();

    @Value("#{target.vehicle_plate}")
    String getVehiclePlate();

    @Value("#{target.fleet_id}")
    String getFleetId();

    @Value("#{target.work_order_item_id}")
    long getWorkOrderItemId();

    @Value("#{target.closed_by_user_id}")
    Long getResolverUserId();

    @Value("#{target.closed_by_user_cpf}")
    Long getResolverUserCpf();

    @Value("#{target.closed_by_user_name}")
    String getResolverUserName();

    @Value("#{target.question_id}")
    long getQuestionId();

    @Value("#{target.question_context_id}")
    long getQuestionContextId();

    @Value("#{target.option_id}")
    long getOptionId();

    @Value("#{target.option_additional_id}")
    String getOptionAddicionalId();

    @Value("#{target.option_context_id}")
    long getOptionContextId();

    @Value("#{target.work_order_item_status}")
    String getWorkOrderItemStatus();

    @Value("#{target.amount_times_pointed}")
    int getAmountTimesPointed();

    @Value("#{target.vehicle_km}")
    Long getKm();

    @Value("#{target.batch_group_id}")
    Long getBatchGroupId();

    @Value("#{@localDateTimeConverter.fromInstantUtc(target.resolved_at_utc)}")
    LocalDateTime getResolvedAtUtc();

    @Value("#{target.resolved_at_with_tz}")
    LocalDateTime getResolvedAtWithTimeZone();

    @Value("#{@localDateTimeConverter.fromInstantUtc(target.started_at_utc)}")
    LocalDateTime getStartedAtUtc();

    @Value("#{target.started_at_with_tz}")
    LocalDateTime getStartedAtWithTimeZone();

    @Value("#{@localDateTimeConverter.fromInstantUtc(target.ended_at_utc)}")
    LocalDateTime getEndedAtUtc();

    @Value("#{target.ended_at_with_tz}")
    LocalDateTime getEndedAtWithTimeZone();

    @Value("#{target.resolution_time}")
    Long getResolutionTime();

    @Value("#{target.resolution_notes}")
    String getResolutionNotes();
}
