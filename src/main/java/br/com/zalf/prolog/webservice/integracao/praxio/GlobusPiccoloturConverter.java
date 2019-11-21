package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.AlternativaChecklistStatus;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.Movimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.destino.DestinoVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.origem.OrigemVeiculo;
import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.MovimentacaoGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.ProcessoMovimentacaoGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.*;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.*;
import org.jetbrains.annotations.NotNull;

import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
                    // O Map irá conter todas as alternativas do modelo (ativas e inativas) isso garante que nunca
                    // retornará null em um get.
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
    public static ProcessoMovimentacaoGlobus convert(@NotNull final ProcessoMovimentacao processoMovimentacao,
                                                     @NotNull final LocalDateTime dataHoraMovimentacao) {
        final List<MovimentacaoGlobus> movimentacoesGlobus = new ArrayList<>();
        for (int i = 0; i < processoMovimentacao.getMovimentacoes().size(); i++) {
            final Movimentacao movimentacao = processoMovimentacao.getMovimentacoes().get(i);
            if (movimentacao.isFromOrigemToDestino(OrigemDestinoEnum.ESTOQUE, OrigemDestinoEnum.VEICULO)) {
                final DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
                movimentacoesGlobus.add(new MovimentacaoGlobus(
                        (long) i,
                        GlobusPiccoloturUtils.addHifenPlaca(destinoVeiculo.getVeiculo().getPlaca()),
                        dataHoraMovimentacao,
                        GlobusPiccoloturUtils.formatNumeroFogo(movimentacao.getPneu().getCodigoCliente()),
                        MovimentacaoGlobus.PNEU_INSERIDO,
                        movimentacao.getObservacao(),
                        destinoVeiculo.getPosicaoDestinoPneu()));
            } else if (movimentacao.isFromOrigemToDestino(OrigemDestinoEnum.VEICULO, OrigemDestinoEnum.ESTOQUE)) {
                final OrigemVeiculo origemVeiculo = ((OrigemVeiculo) movimentacao.getOrigem());
                movimentacoesGlobus.add(new MovimentacaoGlobus(
                        (long) i,
                        GlobusPiccoloturUtils.addHifenPlaca(origemVeiculo.getVeiculo().getPlaca()),
                        dataHoraMovimentacao,
                        GlobusPiccoloturUtils.formatNumeroFogo(movimentacao.getPneu().getCodigoCliente()),
                        MovimentacaoGlobus.PNEU_RETIRADO,
                        movimentacao.getObservacao(),
                        origemVeiculo.getPosicaoOrigemPneu()));
            } else if (movimentacao.isFromOrigemToDestino(OrigemDestinoEnum.VEICULO, OrigemDestinoEnum.VEICULO)) {
                final OrigemVeiculo origemVeiculo = ((OrigemVeiculo) movimentacao.getOrigem());
                final DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
                final String codigoCliente = movimentacao.getPneu().getCodigoCliente();
                // Retira pneu
                movimentacoesGlobus.add(new MovimentacaoGlobus(
                        (long) i,
                        GlobusPiccoloturUtils.addHifenPlaca(origemVeiculo.getVeiculo().getPlaca()),
                        dataHoraMovimentacao,
                        GlobusPiccoloturUtils.formatNumeroFogo(codigoCliente),
                        MovimentacaoGlobus.PNEU_RETIRADO,
                        movimentacao.getObservacao(),
                        origemVeiculo.getPosicaoOrigemPneu()));

                // Aplica pneu
                movimentacoesGlobus.add(new MovimentacaoGlobus(
                        (long) i,
                        GlobusPiccoloturUtils.addHifenPlaca(destinoVeiculo.getVeiculo().getPlaca()),
                        dataHoraMovimentacao,
                        GlobusPiccoloturUtils.formatNumeroFogo(codigoCliente),
                        MovimentacaoGlobus.PNEU_INSERIDO,
                        movimentacao.getObservacao(),
                        destinoVeiculo.getPosicaoDestinoPneu()));
            } else {
                throw new IllegalStateException("Esse processo de movimentação não é válido para essa integração");
            }
        }

        // Ordena a lista com base na ordem das operações que foram executadas.
        movimentacoesGlobus.sort(Comparator.comparingInt(MovimentacaoGlobus::getTipoOperacaoOrdem));

        // Atualiza o valor da sequência com o index do objeto na lista.
        for (int i = 0; i < movimentacoesGlobus.size(); i++) {
            movimentacoesGlobus.get(i).setSequencia((long) i);
        }

        return new ProcessoMovimentacaoGlobus(movimentacoesGlobus);
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
