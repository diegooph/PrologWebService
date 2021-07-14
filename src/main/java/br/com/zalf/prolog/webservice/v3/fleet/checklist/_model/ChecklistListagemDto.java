package br.com.zalf.prolog.webservice.v3.fleet.checklist._model;

import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import io.swagger.annotations.ApiModelProperty;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 2021-04-07
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Value(staticConstructor = "of")
public class ChecklistListagemDto {
    @ApiModelProperty(value = "Código do checklist", required = true, example = "12345")
    @NotNull
    Long codChecklist;
    @ApiModelProperty(value = "Código da unidade onde o checklist foi realizado", required = true, example = "215")
    @NotNull
    Long codUnidadeChecklist;
    @ApiModelProperty(value = "Código do modelo do checklist. Modelo de checklist é onde o usuário define quais itens" +
            " (perguntas e alternativas) devem ser checados.",
                      required = true,
                      example = "10")
    @NotNull
    Long codModeloChecklist;
    @ApiModelProperty(value = "Código da versão modelo do checklist. Quando um modelo de checklist é alterado, uma " +
            "nova versão é gerada.",
                      required = true,
                      example = "10")
    @NotNull
    Long codVersaoModeloChecklist;
    @ApiModelProperty(value = "Código do colaborador que realizou o checklist", required = true, example = "272")
    @NotNull
    Long codColaborador;
    @ApiModelProperty(value = "Cpf do colaborador que realizou o checklist. Esse campo não possui nenhuma formatação.",
                      required = true,
                      example = "3383283194")
    @NotNull
    Long cpfColaborador;
    @ApiModelProperty(value = "Nome do colaborador que realizou o checklist", required = true, example = "Jean")
    @NotNull
    String nomeColaborador;
    @ApiModelProperty(value = "Código do veículo que foi realizado o checklist", required = true, example = "12345")
    @NotNull
    Long codVeiculo;
    @ApiModelProperty(value = "Placa do veículo. Esse campo não possui nenhuma formatação.",
                      required = true,
                      example = "PRO1102")
    @NotNull
    String placaVeiculo;
    @ApiModelProperty(value = "Identificador de frota do veículo. Esse campo não possui nenhuma formatação.",
                      example = "FROTA01")
    @Nullable
    String identificadorFrota;
    @ApiModelProperty(value = "Km do veículo que no momento do checklist.", required = true, example = "111111")
    long kmVeiculoChecklist;
    @ApiModelProperty(value = "Tipo do checklist. Pode ser SAIDA ou RETORNO.", required = true, example = "SAIDA")
    @NotNull
    TipoChecklist tipoChecklist;
    @ApiModelProperty(value = "Data e hora que o checklist foi realizado. Valor expresso em UTC.",
                      required = true,
                      example = "2021-01-01T17:00:00")
    @NotNull
    LocalDateTime dataHoraRealizacaoUtc;
    @ApiModelProperty(value = "Data e hora que o checklist foi realizado. Valor expresso com Time Zone do cliente " +
            "aplicado. O Time Zone do cliente é configurado por Unidade.",
                      required = true,
                      example = "2021-01-01T14:00:00")
    @NotNull
    LocalDateTime dataHoraRealizacaoTimeZoneAplicado;
    @ApiModelProperty(value = "Data e hora que o checklist foi importado. Valor expresso em UTC. Esse valor somente " +
            "será enviado caso o checklist tenha sido importado para o sistema.",
                      example = "2021-01-01T17:00:00")
    @Nullable
    LocalDateTime dataHoraImportacaoUtc;
    @ApiModelProperty(value = "Data e hora que o checklist foi importado. Valor expresso com Time Zone do cliente " +
            "aplicado. O Time Zone do cliente é configurado por Unidade. Esse valor somente será enviado caso o " +
            "checklist tenha sido importado para o sistema.",
                      example = "2021-01-01T14:00:00")
    @Nullable
    LocalDateTime dataHoraImportacaoTimeZoneAplicado;
    @ApiModelProperty(value = "Tempo que o colaborador demorou para realizar o checklist.",
                      required = true,
                      example = "36000")
    long tempoRealizacaoEmMilisegundos;
    @ApiModelProperty(value = "Observação inserido pelo usuário. Esse campo só será enviado caso o usuário tenha " +
            "preenchido com alguma observação. Não é aplicado nenhuma formatação e não tem limite de caracteres.",
                      example = "Checklist de teste.")
    @Nullable
    String observacaoChecklist;
    @ApiModelProperty(value = "Total de perguntas que o colaborador marcou como OK.", required = true, example = "1")
    int totalPerguntasOk;
    @ApiModelProperty(value = "Total de perguntas que o colaborador marcou como NOK.", required = true, example = "1")
    int totalPerguntasNok;
    @ApiModelProperty(value = "Total de alternativas que o colaborador marcou como OK.", required = true, example = "1")
    int totalAlternativasOk;
    @ApiModelProperty(value = "Total de alternativas que o colaborador marcou como NOK.",
                      required = true,
                      example = "1")
    int totalAlternativasNok;
    @ApiModelProperty(value = "Total de midias que o colaborador capturou para as perguntas OK.",
                      required = true,
                      example = "1")
    int totalMidiasPerguntasOk;
    @ApiModelProperty(value = "Total de midias que o colaborador capturou para as alternativas NOK.",
                      required = true,
                      example = "1")
    int totalMidiasAlternativasNok;
    @ApiModelProperty(value = "Total de alternativas NOK com prioridade BAIXA.", required = true, example = "1")
    int totalNokBaixa;
    @ApiModelProperty(value = "Total de alternativas NOK com prioridade ALTA.", required = true, example = "1")
    int totalNokAlta;
    @ApiModelProperty(value = "Total de alternativas NOK com prioridade CRITICA.", required = true, example = "1")
    int totalNokCritica;
    @ApiModelProperty(value = "Flag indicando se o checklist foi realizado de forma offline.",
                      required = true,
                      example = "true")
    boolean isOffline;
    @ApiModelProperty(value = "Data e hora que o checklist foi sincronizados. Valor expresso em UTC. Caso o checklist" +
            " não tiver sido offline, essa data e hora será igual a data e hora de realização.",
                      required = true,
                      example = "2021-01-01T17:00:00")
    @NotNull
    LocalDateTime dataHoraSincronizacaoUtc;
    @ApiModelProperty(value = "Data e hora que o checklist foi sincronizado. Valor expresso com Time Zone do cliente " +
            "aplicado. O Time Zone do cliente é configurado por Unidade. Caso o checklist não tiver sido offline, " +
            "essa data e hora será igual a data e hora de realização.",
                      required = true,
                      example = "2021-01-01T14:00:00")
    @NotNull
    LocalDateTime dataHoraSincronizacaoTimeZoneAplicado;
    @ApiModelProperty(value = "Fonte de coleta da data e hora. Pode ser REDE_CELULAR, LOCAL_CELULAR ou SERVIDOR. Essa" +
            " propriedade indica se a data e hora de realização foi coletada do celular, da operadora ou do servidor.",
                      required = true,
                      example = "SAIDA")
    @NotNull
    FonteDataHora fonteDataHora;
    @ApiModelProperty(value = "Versão do aplicativo no momento da realização do checklist. Esse valor pode não estar " +
            "presente",
                      example = "100")
    @Nullable
    Integer versaoAppMomentoRealizacao;
    @ApiModelProperty(value = "Versão do aplicativo no momento da sincronização do checklist. Esse valor pode não " +
            "estar presente",
                      example = "100")
    @Nullable
    Integer versaoAppMomentoSincronizacao;
    @ApiModelProperty(value = "Identificador do aparelho onde o checklist foi realizado. Esse valor pode não estar " +
            "presente",
                      example = "94148c2190c2aa2a")
    @Nullable
    String deviceId;
    @ApiModelProperty(value = "Imei do aparelho onde o checklist foi realizado. Esse valor pode não estar presente",
                      example = "358112086583961")
    @Nullable
    String deviceImei;
    @ApiModelProperty(value = "Tempo de atividade do aparelho até o momento de realização do checklist. Valor será " +
            "zero caso não for possível coletar a informação real.",
                      example = "360000")
    long deviceUptimeRealizacaoEmMilisegundos;
    @ApiModelProperty(value = "Tempo de atividade do aparelho até o momento de sincronia do checklist. Valor será " +
            "zero caso não for possível coletar a informação real.",
                      example = "360000")
    long deviceUptimeSincronizacaoEmMilisegundos;
    @ApiModelProperty(value = "Perguntas do checklist. Essa lista conterá uma entrada para cada pergunta presente no " +
            "modelo de checklist. Por padrão, essas lista é sempre retornada, porém, caso não tenha necessidade dessa" +
            " informação, pode optar por não incluir no momento da requisição.")
    @Nullable
    List<ChecklistPerguntasDto> perguntasChecklist;
}