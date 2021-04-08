package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model;

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

    @Value("#{target.status_os}")
    String getStatusOs();

    @Value("#{target.data_hora_fechamento}")
    LocalDateTime getDataHoraFechamento();
}
