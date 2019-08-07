package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.AlternativaChecklistStatus;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.*;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.*;
import org.jetbrains.annotations.NotNull;

import javax.xml.datatype.DatatypeFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            @NotNull final Checklist checklist,
            @NotNull final Map<Long, AlternativaChecklistStatus> alternativasStatus) {
        final List<PerguntaNokGlobus> perguntasNok = new ArrayList<>();
        for (final PerguntaRespostaChecklist resposta : checklist.getListRespostas()) {
            final List<AlternativaNokGlobus> alternativasNok = new ArrayList<>();
            for (final AlternativaChecklist alternativa : resposta.getAlternativasResposta()) {
                // Uma alternativa selecionada quer dizer uma alternativa NOK
                if (alternativa.selected) {
                    final AlternativaChecklistStatus alternativaChecklistStatus =
                            alternativasStatus.get(alternativa.getCodigo());

                    if (!alternativaChecklistStatus.isTemItemOsPendente()
                            && alternativaChecklistStatus.isDeveAbrirOrdemServico()) {
                        final String descricao = alternativa.isTipoOutros()
                                ? alternativa.getRespostaOutros()
                                : alternativa.getAlternativa();
                        alternativasNok.add(new AlternativaNokGlobus(
                                alternativa.getCodigo(),
                                descricao,
                                getPrioridadeAlternativaGlobus(alternativaChecklistStatus.getPrioridadeAlternativa())));
                    }
                }
            }
            if (!alternativasNok.isEmpty()) {
                perguntasNok.add(new PerguntaNokGlobus(
                        resposta.getCodigo(),
                        resposta.getPergunta(),
                        alternativasNok));
            }
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
            @NotNull final ChecklistItensNokGlobus checklistItensNokGlobus) throws Throwable {
        final ObjectFactory factory = new ObjectFactory();
        final OrdemDeServicoCorretivaPrologVO osGlobus = new OrdemDeServicoCorretivaPrologVO();
        osGlobus.setCodUnidadeChecklist(checklistItensNokGlobus.getCodUnidadeChecklist().intValue());
        osGlobus.setCodChecklistRealizado(checklistItensNokGlobus.getCodChecklistRealizado().intValue());
        osGlobus.setCpfColaboradorRealizacao(checklistItensNokGlobus.getCpfColaboradorRealizacao());
        osGlobus.setPlacaVeiculoChecklist(checklistItensNokGlobus.getPlacaVeiculoChecklist());
        osGlobus.setKmColetadoChecklist(checklistItensNokGlobus.getKmColetadoChecklist().intValue());
        osGlobus.setTipoChecklist(checklistItensNokGlobus.getTipoChecklist().asString());
        osGlobus.setDataHoraRealizacaoUtc(
                DatatypeFactory.newInstance().newXMLGregorianCalendar(
                        checklistItensNokGlobus.getDataHoraRealizacaoUtc().toString()));
        osGlobus.setUsuario(GlobusPiccoloturConstants.USUARIO_PROLOG_INTEGRACAO);
        osGlobus.setListaPerguntasNokVO(convertPerguntas(factory, checklistItensNokGlobus.getPerguntasNok()));
        return osGlobus;
    }

    @NotNull
    private static ArrayOfPerguntasNokVO convertPerguntas(@NotNull final ObjectFactory factory,
                                                          @NotNull final List<PerguntaNokGlobus> perguntasNok) {
        final ArrayOfPerguntasNokVO arrayOfPerguntasNokVO = factory.createArrayOfPerguntasNokVO();
        for (final PerguntaNokGlobus perguntaNokGlobus : perguntasNok) {
            final PerguntasNokVO perguntaNokVO = factory.createPerguntasNokVO();
            perguntaNokVO.setCodPerguntaNok(perguntaNokGlobus.getCodPerguntaNok().intValue());
            perguntaNokVO.setDescricaoPerguntaNok(perguntaNokGlobus.getDescricaoPerguntaNok());
            perguntaNokVO.setListaAlternativasNok(convertAlternativas(factory, perguntaNokGlobus.getAlternativasNok()));
            arrayOfPerguntasNokVO.getPerguntasNokVO().add(perguntaNokVO);
        }
        return arrayOfPerguntasNokVO;
    }

    @NotNull
    private static ArrayOfAlternativasNokVO convertAlternativas(
            @NotNull final ObjectFactory factory,
            @NotNull final List<AlternativaNokGlobus> alternativasNok) {
        final ArrayOfAlternativasNokVO arrayOfAlternativasNokVO = factory.createArrayOfAlternativasNokVO();
        for (final AlternativaNokGlobus alternativaNokGlobus : alternativasNok) {
            final AlternativasNokVO alternativaNokVO = factory.createAlternativasNokVO();
            alternativaNokVO.setCodAlternativaNok(alternativaNokGlobus.getCodAlternativaNok().intValue());
            alternativaNokVO.setDescricaoAlternativaNok(alternativaNokGlobus.getDescricaoAlternativaNok());
            alternativaNokVO.setPrioridadeAlternativaNok(alternativaNokGlobus.getPrioridadeAlternativaNok().asString());
            arrayOfAlternativasNokVO.getAlternativasNokVO().add(alternativaNokVO);
        }
        return arrayOfAlternativasNokVO;
    }

    @NotNull
    private static PrioridadeAlternativaGlobus getPrioridadeAlternativaGlobus(
            @NotNull final PrioridadeAlternativa prioridade) {
        if (prioridade.equals(PrioridadeAlternativa.CRITICA)) {
            return PrioridadeAlternativaGlobus.CRITICA;
        } else if (prioridade.equals(PrioridadeAlternativa.ALTA)) {
            return PrioridadeAlternativaGlobus.ALTA;
        } else {
            return PrioridadeAlternativaGlobus.BAIXA;
        }
    }
}
