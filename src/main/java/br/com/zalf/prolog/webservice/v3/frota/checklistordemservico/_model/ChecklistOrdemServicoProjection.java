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

    @Value("#{target.codigo_item_os}")
    long getCodigoItemOs();

    @Value("#{target.cpf_mecanico}")
    Long getCpfMecanico();

    @Value("#{target.codigo_pergunta_primeiro_apontamento}")
    long getCodigoPerguntaPrimeiroApontamento();

    @Value("#{target.codigo_contexto_pergunta}")
    long getCodigoContextoPergunta();

    @Value("#{target.codigo_alternativa_primeiro_apontamento}")
    long getCodigoAlternativaPrimeiroApontamento();

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

    @Value("#{target.data_hora_conserto}")
    LocalDateTime getDataHoraConserto();

    @Value("#{target.data_hora_inicio_resolucao}")
    LocalDateTime getDataHoraInicioResolucao();

    @Value("#{target.data_hora_fim_resolucao}")
    LocalDateTime getDataHoraFimResolucao();

    @Value("#{target.tempo_realizacao}")
    Long getTempoRealizacao();

    @Value("#{target.feedback_conserto}")
    String getFeedbackConserto();
}
