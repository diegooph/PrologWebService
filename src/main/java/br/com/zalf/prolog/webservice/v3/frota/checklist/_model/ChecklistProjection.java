package br.com.zalf.prolog.webservice.v3.frota.checklist._model;

import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AnexoMidiaChecklistEnum;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

/**
 * Created on 2021-04-08
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface ChecklistProjection {
    @Value("#{target.cod_unidade}")
    Long getCodUnidade();

    @Value("#{target.cod_checklist}")
    Long getCodChecklist();

    @Value("#{target.cod_checklist_modelo}")
    Long getCodModeloChecklist();

    @Value("#{target.cod_versao_checklist_modelo}")
    Long getCodVersaoModelo();

    @Value("#{target.cod_colaborador}")
    Long getCodColaborador();

    @Value("#{target.cpf_colaborador}")
    Long getCpfColaborador();

    @Value("#{target.nome_colaborador}")
    String getNomeColaborador();

    @Value("#{target.cod_veiculo}")
    Long getCodVeiculo();

    @Value("#{target.placa_veiculo}")
    String getPlacaVeiculo();

    @Value("#{target.identificador_frota}")
    String getIdentificadorFrota();

    @Value("#{target.km_veiculo_momento_realizacao}")
    long getKmVeiculoMomentoRealizacao();

    @Value("#{target.tipo_checklist}")
    TipoChecklist getTipoChecklist();

    @Value("#{@localDateTimeConverter.fromInstantUtc(target.data_hora_realizacao_utc)}")
    LocalDateTime getDataHoraRealizacaoUtc();

    @Value("#{target.data_hora_realizacao_tz_aplicado}")
    LocalDateTime getDataHoraRealizacaoTzAplicado();

    @Value("#{@localDateTimeConverter.fromInstantUtc(target.data_hora_importado_prolog_utc)}")
    LocalDateTime getDataHoraImportadoUtc();

    @Value("#{target.data_hora_importado_prolog_tz_aplicado}")
    LocalDateTime getDataHoraImportadoTzAplicado();

    @Value("#{target.duracao_realizacao_millis}")
    long getDuracaoRealizacaoInMillis();

    @Value("#{target.observacao_checklist}")
    String getObservacaoChecklist();

    @Value("#{target.total_perguntas_ok}")
    int getTotalPerguntasOk();

    @Value("#{target.total_perguntas_nok}")
    int getTotalPerguntasNok();

    @Value("#{target.total_alternativas_ok}")
    int getTotalAlternativasOk();

    @Value("#{target.total_alternativas_nok}")
    int getTotalAlternativasNok();

    @Value("#{target.total_midias_perguntas_ok}")
    int getTotalMidiasPerguntasOk();

    @Value("#{target.total_midias_alternativas_nok}")
    int getTotalMidiasAlternativasNok();

    @Value("#{target.total_alternativas_nok_prioridade_baixa}")
    int getTotalNokBaixa();

    @Value("#{target.total_alternativas_nok_prioridade_alta}")
    int getTotalNokAlta();

    @Value("#{target.total_alternativas_nok_prioridade_critica}")
    int getTotalNokCritica();

    @Value("#{target.foi_offline}")
    boolean isOffline();

    @Value("#{@localDateTimeConverter.fromInstantUtc(target.data_hora_sincronizacao_utc)}")
    LocalDateTime getDataHoraSincronizacaoUtc();

    @Value("#{target.data_hora_sincronizacao_tz_aplicado}")
    LocalDateTime getDataHoraSincronizacaoTzAplicado();

    @Value("#{target.fonte_data_hora}")
    FonteDataHora getFonteDataHora();

    @Value("#{target.versao_app_momento_realizacao}")
    Integer getVersaoAppMomentoRealizacao();

    @Value("#{target.versao_app_momento_sincronizacao}")
    Integer getVersaoAppMomentoSincronizacao();

    @Value("#{target.device_id}")
    String getDeviceId();

    @Value("#{target.device_imei}")
    String getDeviceImei();

    @Value("#{target.device_uptime_realizacao_millis}")
    long getDeviceUptimeRealizacaoMillis();

    @Value("#{target.device_uptime_sincronizacao_millis}")
    long getDeviceUptimeSincronizacaoMillis();

    @Value("#{target.cod_pergunta}")
    Long getCodPergunta();

    @Value("#{target.cod_contexto_pergunta}")
    Long getCodContextoPergunta();

    @Value("#{target.descricao_pergunta}")
    String getDescricaoPergunta();

    @Value("#{target.ordem_pergunta}")
    int getOrdemPergunta();

    @Value("#{target.pergunta_single_choice}")
    boolean isPerguntaSingleChoice();

    @Value("#{target.anexo_midia_pergunta_ok}")
    AnexoMidiaChecklistEnum getAnexoMidiaPerguntaOk();

    @Value("#{target.cod_alternativa}")
    Long getCodAlternativa();

    @Value("#{target.cod_contexto_alternativa}")
    Long getCodContextoAlternativa();

    @Value("#{target.descricao_alternativa}")
    String getDescricaoAlternativa();

    @Value("#{target.ordem_alternativa}")
    int getOrdemAlternativa();

    @Value("#{target.prioridade_alternativa}")
    PrioridadeAlternativa getPrioridadeAlternativa();

    @Value("#{target.alternativa_tipo_outros}")
    boolean getAlternativaTipoOutros();

    @Value("#{target.deve_abrir_ordem_servico}")
    boolean deveAbrirOrdemServico();

    @Value("#{target.anexo_midia_alternativa_nok}")
    AnexoMidiaChecklistEnum getAnexoMidiaAlternativaNok();

    @Value("#{target.cod_auxiliar_alternativa}")
    String getCodAuxiliarAlternativa();

    @Value("#{target.alternativa_selecionada}")
    boolean isAlternativaSelecionada();

    @Value("#{target.resposta_outros}")
    String getRespostaTipoOutros();
}
