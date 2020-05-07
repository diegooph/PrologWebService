package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Essa classe possui as informações marcadas como NOK (não OK) em um determinado checklist. Com essa classe
 * encapsulamos o envio de informações NOK para o Sistema Globus.
 * <p>
 * Esses atributos são referentes à apenas um checklist realizado.
 * <p>
 * Com essas informações, o Globus irá processar e gerar uma Ordem de Serviço seguindo a parametrização do sistema deles.
 * <p>
 * Created on 02/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ChecklistItensNokGlobus {
    /**
     * Código da Unidade onde o checklist foi realizado.
     */
    @NotNull
    private final Long codUnidadeChecklist;
    /**
     * Código único de identificação que o checklist realizado possui no banco de dados do ProLog.
     */
    @NotNull
    private final Long codChecklistRealizado;
    /**
     * Código único de identificação do modelo do checklist que foi realizado pelo usuário.
     */
    @NotNull
    private final Long codModeloChecklistRealizado;
    /**
     * CPF do colaborador que realizou o checklist e apontou os itens como NOK.
     */
    @NotNull
    private final String cpfColaboradorRealizacao;
    /**
     * Placa do veículo em que o checklist foi realizado.
     */
    @NotNull
    private final String placaVeiculoChecklist;
    /**
     * Quilometragem do veículo no momento que o checklist foi realizado.
     */
    @NotNull
    private final Long kmColetadoChecklist;
    /**
     * Tipo de checklist realizado. Este tipo pode ser {@link TipoChecklistGlobus#SAIDA} ou
     * {@link TipoChecklistGlobus#RETORNO}.
     */
    @NotNull
    private final TipoChecklistGlobus tipoChecklist;
    /**
     * Data e hora que o checklist foi realizado pelo colaborador.
     * <p>
     * A data e hora estão no padrão UTC, exemplo: "2019-05-23T15:30:00"
     */
    @NotNull
    private final LocalDateTime dataHoraRealizacaoUtc;
    /**
     * Lista de {@link PerguntaNokGlobus itens NOK} apontados pelo colaborador.
     */
    @NotNull
    private final List<PerguntaNokGlobus> perguntasNok;

    public ChecklistItensNokGlobus(@NotNull final Long codUnidadeChecklist,
                                   @NotNull final Long codChecklistRealizado,
                                   @NotNull final Long codModeloChecklistRealizado,
                                   @NotNull final String cpfColaboradorRealizacao,
                                   @NotNull final String placaVeiculoChecklist,
                                   @NotNull final Long kmColetadoChecklist,
                                   @NotNull final TipoChecklistGlobus tipoChecklist,
                                   @NotNull final LocalDateTime dataHoraRealizacaoUtc,
                                   @NotNull final List<PerguntaNokGlobus> perguntasNok) {
        this.codUnidadeChecklist = codUnidadeChecklist;
        this.codChecklistRealizado = codChecklistRealizado;
        this.codModeloChecklistRealizado = codModeloChecklistRealizado;
        this.cpfColaboradorRealizacao = cpfColaboradorRealizacao;
        this.placaVeiculoChecklist = placaVeiculoChecklist;
        this.kmColetadoChecklist = kmColetadoChecklist;
        this.tipoChecklist = tipoChecklist;
        this.dataHoraRealizacaoUtc = dataHoraRealizacaoUtc;
        this.perguntasNok = perguntasNok;
    }

    @NotNull
    public static ChecklistItensNokGlobus getDummy() {
        final List<PerguntaNokGlobus> respostasNok = new ArrayList<>();
        respostasNok.add(PerguntaNokGlobus.getDummy());
        return new ChecklistItensNokGlobus(
                5L,
                13873L,
                501L,
                "03383283194",
                "PRO0001",
                54920L,
                TipoChecklistGlobus.SAIDA,
                Now.localDateTimeUtc(),
                respostasNok);
    }

    @NotNull
    public Long getCodUnidadeChecklist() {
        return codUnidadeChecklist;
    }

    @NotNull
    public Long getCodChecklistRealizado() {
        return codChecklistRealizado;
    }

    @NotNull
    public Long getCodModeloChecklistRealizado() {
        return codModeloChecklistRealizado;
    }

    @NotNull
    public String getCpfColaboradorRealizacao() {
        return cpfColaboradorRealizacao;
    }

    @NotNull
    public String getPlacaVeiculoChecklist() {
        return placaVeiculoChecklist;
    }

    @NotNull
    public Long getKmColetadoChecklist() {
        return kmColetadoChecklist;
    }

    @NotNull
    public TipoChecklistGlobus getTipoChecklist() {
        return tipoChecklist;
    }

    @NotNull
    public String getDataHoraRealizacaoUtc() {
        // Forçamos o retorno no padrão 'ISO_DATE_TIME' para evitar o problema evidenciado na
        // https://prologapp.atlassian.net/browse/PLI-146
        return dataHoraRealizacaoUtc.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    @NotNull
    public List<PerguntaNokGlobus> getPerguntasNok() {
        return perguntasNok;
    }
}
