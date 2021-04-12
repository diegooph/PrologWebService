package br.com.zalf.prolog.webservice.v3.frota.checklist._model;

import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

/**
 * Created on 2021-04-08
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface ChecklistProjection {
    //o que eu recebo do banco
    @Value("#{target.codigo}")
    Long getCodChecklist;

    @Value("#{target.cod_modelo}")
    Long getCodModeloChecklist;

    @Value("#{target.cod_versao_modelo}")
    Long getCodVersaoModelo;

    @Value("#{target.data_hora}")
    LocalDateTime getDataHora;
    @Value("#{target.data_hora_importado_prolog}")
    LocalDateTime getDataHoraImportadoProlog;
    @Value("#{target.km_veiculo_momento_realizacao}")
    long getKmVeiculoMomentoRealizacao;

    @Value("#{target.duracao_realizacao_in_millis}")
    long getDuracaoRealizacaoInMillis;

    @Value("#{target.cod_colaborador}")
    Long getCodColaborador;

    @Value("#{target.cpf_colaborador}")
    Long getCpfColaborador;

    @Value("#{target.nome_colaborador}")
    String getNomeColaborador;

    @Value("#{target.cod_veiculo}")
    Long getCodVeiculo;

    @Value("#{target.placa_veiculo}")
    String getPlacaVeiculo;

    @Value("#{target.identificador_frota}")
    String getIdentificadorFrota;

    @Value("#{target.tipo}")
    TipoChecklist getTipo;

    @Value("#{target.total_perguntas_ok}")
    int getTotalPerguntasOk;

    @Value("#{target.total_perguntas_nok}")
    int getTotalPerguntasNok;

    @Value("#{target.total_alternativas_ok}")
    int getTotalAlternativasOk;

    @Value("#{target.total_alternativas_nok}")
    int getTotalAlternativasNok;

    @Value("#{target.total_imagens_perguntas_ok}")
    int getTotalImagensPerguntasOk;

    @Value("#{target.total_imagens_alternativas_nok}")
    int getTotalImagensAlternativasNok;

    @Value("#{target.total_nok_baixa}")
    int getTotalNokBaixa;

    @Value("#{target.total_nok_alta}")
    int getTotalNokAlta;

    @Value("#{target.total_nok_critica}")
    int getTotalNokCritica;
}
