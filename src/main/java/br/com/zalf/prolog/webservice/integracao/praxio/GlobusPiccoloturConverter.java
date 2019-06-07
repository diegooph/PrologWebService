package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.*;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 04/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class GlobusPiccoloturConverter {
    @NotNull
    public static ChecklistItensNokGlobus createChecklistItensNokGlobus(
            @NotNull final Long codUnidadeProLog,
            @NotNull final Long codChecklistProLog,
            @NotNull final Checklist checklist) {
        final List<PerguntaNokGlobus> perguntasNok = new ArrayList<>();
        for (final PerguntaRespostaChecklist resposta : checklist.getListRespostas()) {
            final List<AlternativaNokGlobus> alternativasNok = new ArrayList<>();
            for (final AlternativaChecklist alternativa : resposta.getAlternativasResposta()) {
                // Uma alternativa selecionada quer dizer uma alternativa NOK
                if (alternativa.selected) {
                    final String descricao = alternativa.isTipoOutros()
                            ? alternativa.getRespostaOutros()
                            : alternativa.getAlternativa();
                    alternativasNok.add(new AlternativaNokGlobus(
                            alternativa.getCodigo(),
                            descricao,
                            getPrioridadeAlternativaGlobus(alternativa.getPrioridade())));
                }
            }
            if (!alternativasNok.isEmpty()) {
                perguntasNok.add(new PerguntaNokGlobus(
                        resposta.getCodigo(),
                        resposta.getPergunta(),
                        alternativasNok));
            }
        }
        if (perguntasNok.isEmpty()) {
            throw new IllegalStateException("");
        }
        return new ChecklistItensNokGlobus(
                codUnidadeProLog,
                codChecklistProLog,
                checklist.getColaborador().getCpfAsString(),
                checklist.getPlacaVeiculo(),
                checklist.getKmAtualVeiculo(),
                checklist.getTipo() == Checklist.TIPO_SAIDA ? TipoChecklistGlobus.SAIDA : TipoChecklistGlobus.RETORNO,
                checklist.getData(),
                perguntasNok);
    }

    @NotNull
    public static OrdemDeServicoCorretivaPrologVO convert(
            @NotNull final ChecklistItensNokGlobus checklistItensNokGlobus) throws DatatypeConfigurationException {
        final OrdemDeServicoCorretivaPrologVO osGlobus = new OrdemDeServicoCorretivaPrologVO();
        osGlobus.setCodUnidadeChecklist(checklistItensNokGlobus.getCodUnidadeChecklist().intValue());
        osGlobus.setCodChecklistRealizado(checklistItensNokGlobus.getCodChecklistRealizado().intValue());
        osGlobus.setCpfColaboradorRealizacao(checklistItensNokGlobus.getCpfColaboradorRealizacao());
        osGlobus.setPlacaVeiculoChecklist(checklistItensNokGlobus.getPlacaVeiculoChecklist());
        osGlobus.setKmColetadoChecklist(checklistItensNokGlobus.getKmColetadoChecklist().intValue());
        osGlobus.setTipoChecklist(checklistItensNokGlobus.getTipoChecklist().asString());
        osGlobus.setDataHoraRealizacaoUtc(DatatypeFactory.newInstance().newXMLGregorianCalendar(checklistItensNokGlobus.getDataHoraRealizacaoUtc().toString()));
        osGlobus.setUsuario("MANAGER");
        osGlobus.setListaPerguntasNokVO(convertPerguntas(checklistItensNokGlobus.getPerguntasNok()));
        return osGlobus;
    }

    @NotNull
    private static ArrayOfPerguntasNokVO convertPerguntas(@NotNull final List<PerguntaNokGlobus> perguntasNok) {
        final ArrayOfPerguntasNokVO arrayOfPerguntasNokVO = new ArrayOfPerguntasNokVO();
        for (final PerguntaNokGlobus perguntaNokGlobus : perguntasNok) {
            final PerguntasNokVO perguntaNokVO = new PerguntasNokVO();
            perguntaNokVO.setCodPerguntaNok(perguntaNokGlobus.getCodPerguntaNok().intValue());
            perguntaNokVO.setDescricaoPerguntaNok(perguntaNokGlobus.getDescricaoPerguntaNok());
            perguntaNokVO.setListaAlternativasNok(convertAlternativas(perguntaNokGlobus.getAlternativasNok()));
            arrayOfPerguntasNokVO.getPerguntasNokVO().add(perguntaNokVO);
        }
        return arrayOfPerguntasNokVO;
    }

    @NotNull
    private static ArrayOfAlternativasNokVO convertAlternativas(
            @NotNull final List<AlternativaNokGlobus> alternativasNok) {
        final ArrayOfAlternativasNokVO arrayOfAlternativasNokVO = new ArrayOfAlternativasNokVO();
        for (final AlternativaNokGlobus alternativaNokGlobus : alternativasNok) {
            final AlternativasNokVO alternativaNokVO = new AlternativasNokVO();
            alternativaNokVO.setCodAlternativaNok(alternativaNokGlobus.getCodAlternativaNok().intValue());
            alternativaNokVO.setDescricaoAlternativaNok(alternativaNokGlobus.getDescricaoAlternativaNok());
            alternativaNokVO.setPrioridadeAlternativaNok(alternativaNokGlobus.getPrioridadeAlternativaNok().asString());
            arrayOfAlternativasNokVO.getAlternativasNokVO().add(alternativaNokVO);
        }
        return arrayOfAlternativasNokVO;
    }

    @NotNull
    private static PrioridadeAlternativaGlobus getPrioridadeAlternativaGlobus(
            @Nullable final PrioridadeAlternativa prioridade) {
        // TODO - Corrigir aqui. O checklist não envia a Prioridade. Temos que pegar no BD.
        if (prioridade == null) {
            return PrioridadeAlternativaGlobus.CRITICA;
        }
        if (prioridade.equals(PrioridadeAlternativa.CRITICA)) {
            return PrioridadeAlternativaGlobus.CRITICA;
        } else if (prioridade.equals(PrioridadeAlternativa.ALTA)) {
            return PrioridadeAlternativaGlobus.ALTA;
        } else {
            return PrioridadeAlternativaGlobus.BAIXA;
        }
    }
}
