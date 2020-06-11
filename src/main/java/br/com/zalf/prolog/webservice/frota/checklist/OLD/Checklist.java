package br.com.zalf.prolog.webservice.frota.checklist.OLD;


import br.com.zalf.prolog.webservice.frota.checklist.model.ChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 29/02/2016
 * Checklist de saída ou retorno (atributo "tipo") do veículo, contém placa do veículo e colaborador que realizou o check
 * além de um map com as perguntas e respostas.
 */
@Deprecated
public class Checklist {
    public static final char TIPO_SAIDA = 'S';
    public static final char TIPO_RETORNO = 'R';
    private Long codModelo;
    private Long codVersaoModeloChecklist;
    private Long codigo;
    private Colaborador colaborador;
    private LocalDateTime data;
    @Nullable
    private LocalDateTime dataHoraImportadoProLog;
    private String placaVeiculo;
    private List<PerguntaRespostaChecklist> listRespostas;
    /**
     * tipo pode assumir S e R, de saída e retorno, respectivamente
     */
    private char tipo;
    private long kmAtualVeiculo;
    private long tempoRealizacaoCheckInMillis;
    private int qtdItensOk;
    private int qtdItensNok;
    private int qtdAlternativasOk;
    private int qtdAlternativasNok;

    private int qtdNokBaixa;
    private int qtdNokAlta;
    private int qtdNokCritica;

    public Checklist() {

    }

    @NotNull
    public static List<Checklist> sortByDate(@NotNull final List<Checklist> checklists, final boolean ascending) {
        if (ascending) {
            checklists.sort(Comparator.comparing(Checklist::getData));
        } else {
            checklists.sort(Comparator.comparing(Checklist::getData).reversed());
        }

        return checklists;
    }

    public Long getCodModelo() {
        return codModelo;
    }

    public void setCodModelo(Long codModelo) {
        this.codModelo = codModelo;
    }

    public Long getCodVersaoModeloChecklist() {
        return codVersaoModeloChecklist;
    }

    public void setCodVersaoModeloChecklist(Long codVersaoModeloChecklist) {
        this.codVersaoModeloChecklist = codVersaoModeloChecklist;
    }

    public long getTempoRealizacaoCheckInMillis() {
        return tempoRealizacaoCheckInMillis;
    }

    public void setTempoRealizacaoCheckInMillis(long tempoRealizacaoCheckInMillis) {
        this.tempoRealizacaoCheckInMillis = tempoRealizacaoCheckInMillis;
    }

    public static char getTipoSaida() {
        return TIPO_SAIDA;
    }

    public static char getTipoRetorno() {
        return TIPO_RETORNO;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    @Nullable
    public LocalDateTime getDataHoraImportadoProLog() {
        return dataHoraImportadoProLog;
    }

    public void setDataHoraImportadoProLog(@Nullable final LocalDateTime dataHoraImportadoProLog) {
        this.dataHoraImportadoProLog = dataHoraImportadoProLog;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public List<PerguntaRespostaChecklist> getListRespostas() {
        return listRespostas;
    }

    public void setListRespostas(List<PerguntaRespostaChecklist> listRespostas) {
        this.listRespostas = listRespostas;
        calculaQtdOkOrNok();
    }

    public char getTipo() {
        return tipo;
    }

    public void setTipo(char tipo) {
        this.tipo = tipo;
    }

    public long getKmAtualVeiculo() {
        return kmAtualVeiculo;
    }

    public void setKmAtualVeiculo(long kmAtualVeiculo) {
        this.kmAtualVeiculo = kmAtualVeiculo;
    }

    public int getQtdItensOk() {
        return qtdItensOk;
    }

    public void setQtdItensOk(int qtdItensOk) {
        this.qtdItensOk = qtdItensOk;
    }

    public int getQtdItensNok() {
        return qtdItensNok;
    }

    public void setQtdItensNok(int qtdItensNok) {
        this.qtdItensNok = qtdItensNok;
    }

    public void setQtdAlternativasOk(int qtdAlternativasOk) {
        this.qtdAlternativasOk = qtdAlternativasOk;
    }

    public int getQtdAlternativasOk() {
        return qtdAlternativasOk;
    }

    public void setQtdAlternativasNok(int qtdAlternativasNok) {
        this.qtdAlternativasNok = qtdAlternativasNok;
    }

    public int getQtdAlternativasNok() {
        return qtdAlternativasNok;
    }

    public int getQtdNokBaixa() {
        return qtdNokBaixa;
    }

    public void setQtdNokBaixa(int qtdNokBaixa) {
        this.qtdNokBaixa = qtdNokBaixa;
    }

    public int getQtdNokAlta() {
        return qtdNokAlta;
    }

    public void setQtdNokAlta(int qtdNokAlta) {
        this.qtdNokAlta = qtdNokAlta;
    }

    public int getQtdNokCritica() {
        return qtdNokCritica;
    }

    public void setQtdNokCritica(int qtdNokCritica) {
        this.qtdNokCritica = qtdNokCritica;
    }

    public void calculaQtdOkOrNok() {
        int qtdPerguntasOk = 0;
        int qtdPerguntasNok = 0;
        int qtdAlternativasOk = 0;
        int qtdAlternativasNok = 0;

        int qtdNokBaixa = 0;
        int qtdNokAlta = 0;
        int qtdNokCritica = 0;
        boolean perguntaTeveAlternativasNok = false;
        for (int i = 0; i < listRespostas.size(); i++) {
            final PerguntaRespostaChecklist checklistResposta = listRespostas.get(i);
            final List<AlternativaChecklist> alternativasRespostas = checklistResposta.getAlternativasResposta();
            for (int j = 0; j < alternativasRespostas.size(); j++) {
                final AlternativaChecklist alternativaResposta = alternativasRespostas.get(j);
                if (alternativaResposta.isSelected()) {
                    qtdAlternativasNok++;
                    perguntaTeveAlternativasNok = true;

                    if (alternativaResposta.getPrioridade() == PrioridadeAlternativa.BAIXA) {
                        qtdNokBaixa++;
                    } else if (alternativaResposta.getPrioridade() == PrioridadeAlternativa.ALTA) {
                        qtdNokAlta++;
                    } else if (alternativaResposta.getPrioridade() == PrioridadeAlternativa.CRITICA) {
                        qtdNokCritica++;
                    }
                } else {
                    qtdAlternativasOk++;
                }
            }
            if (perguntaTeveAlternativasNok) {
                qtdPerguntasNok++;
                perguntaTeveAlternativasNok = false;
            } else {
                qtdPerguntasOk++;
            }
        }

        this.setQtdItensOk(qtdPerguntasOk);
        this.setQtdItensNok(qtdPerguntasNok);
        this.setQtdAlternativasOk(qtdAlternativasOk);
        this.setQtdAlternativasNok(qtdAlternativasNok);
        this.setQtdNokBaixa(qtdNokBaixa);
        this.setQtdNokAlta(qtdNokAlta);
        this.setQtdNokCritica(qtdNokCritica);
    }

    @Override
    public String toString() {
        return "Checklist{" +
                "codModelo=" + codModelo +
                ", codigo=" + codigo +
                ", colaborador=" + colaborador +
                ", data=" + data +
                ", placaVeiculo='" + placaVeiculo + '\'' +
                ", listRespostas=" + listRespostas +
                ", tipo=" + tipo +
                ", kmAtualVeiculo=" + kmAtualVeiculo +
                ", tempoRealizacaoCheckInMillis=" + tempoRealizacaoCheckInMillis +
                ", qtdItensOk=" + qtdItensOk +
                ", qtdItensNok=" + qtdItensNok +
                '}';
    }

    @NotNull
    public static List<ChecklistListagem> toChecklistListagem(@NotNull final List<Checklist> checklistsAntigos) {
        return checklistsAntigos.stream().map(Checklist::toChecklistListagem).collect(Collectors.toList());
    }

    @NotNull
    public static ChecklistListagem toChecklistListagem(@NotNull final Checklist checklistAntigo) {
        return new ChecklistListagem(
                checklistAntigo.getCodigo(),
                checklistAntigo.getCodModelo(),
                checklistAntigo.getCodVersaoModeloChecklist(),
                checklistAntigo.getData(),
                checklistAntigo.getDataHoraImportadoProLog(),
                checklistAntigo.getKmAtualVeiculo(),
                checklistAntigo.getTempoRealizacaoCheckInMillis(),
                -1L,
                checklistAntigo.getColaborador().getCpf(),
                checklistAntigo.getColaborador().getNome(),
                -1L,
                checklistAntigo.getPlacaVeiculo(),
                TipoChecklist.fromChar(checklistAntigo.getTipo()),
                checklistAntigo.getQtdItensOk(),
                checklistAntigo.getQtdItensNok(),
                checklistAntigo.getQtdAlternativasOk(),
                checklistAntigo.getQtdAlternativasNok(),
                0,
                0,
                checklistAntigo.getQtdNokBaixa(),
                checklistAntigo.getQtdNokAlta(),
                checklistAntigo.getQtdNokCritica());
    }
}
