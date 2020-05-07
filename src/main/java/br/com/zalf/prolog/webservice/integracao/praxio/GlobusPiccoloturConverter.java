package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoParaRealizacao;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.InfosAlternativaAberturaOrdemServico;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.Movimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.destino.DestinoVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.origem.OrigemVeiculo;
import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.GlobusPiccoloturLocalMovimento;
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
    private static final Short COD_FUNCAO_AGRUPAMENTO_MOVIMENTACAO = 14;

    @NotNull
    public static ChecklistItensNokGlobus createChecklistItensNokGlobus(
            @NotNull final Long codUnidadeProLog,
            @NotNull final Long codChecklistProLog,
            @NotNull final Checklist checklist,
            @NotNull final Map<Long, List<InfosAlternativaAberturaOrdemServico>> alternativasStatus) {
        final List<PerguntaNokGlobus> perguntasNok = new ArrayList<>();
        for (final PerguntaRespostaChecklist resposta : checklist.getListRespostas()) {
            final List<AlternativaNokGlobus> alternativasNok = new ArrayList<>();
            Long codContextoPergunta = null;
            for (final AlternativaChecklist alternativa : resposta.getAlternativasResposta()) {
                // Uma alternativa selecionada quer dizer uma alternativa NOK
                if (alternativa.selected) {
                    // O Map irá conter todas as alternativas do modelo (ativas e inativas) isso garante que nunca
                    // retornará null em um get.
                    final List<InfosAlternativaAberturaOrdemServico> infosAlternativaAberturaOrdemServicos =
                            alternativasStatus.get(alternativa.getCodigo());
                    if (infosAlternativaAberturaOrdemServicos != null
                            && infosAlternativaAberturaOrdemServicos.size() > 0) {
                        final InfosAlternativaAberturaOrdemServico infosAlternativaAberturaOrdemServico =
                                infosAlternativaAberturaOrdemServicos.get(0);
                        if (infosAlternativaAberturaOrdemServico.isDeveAbrirOrdemServico()
                                && infosAlternativaAberturaOrdemServico.getCodItemOrdemServico() <= 0) {
                            final String descricao = alternativa.isTipoOutros()
                                    ? alternativa.getRespostaOutros()
                                    : alternativa.getAlternativa();
                            alternativasNok.add(new AlternativaNokGlobus(
                                    infosAlternativaAberturaOrdemServico.getCodAlternativa(),
                                    infosAlternativaAberturaOrdemServico.getCodContextoAlternativa(),
                                    descricao,
                                    infosAlternativaAberturaOrdemServico.isAlternativaTipoOutros(),
                                    getPrioridadeAlternativaGlobus(
                                            infosAlternativaAberturaOrdemServico.getPrioridadeAlternativa())));
                            codContextoPergunta = infosAlternativaAberturaOrdemServico.getCodContextoPergunta();
                        }

                    }
                }
            }
            if (!alternativasNok.isEmpty()) {
                perguntasNok.add(new PerguntaNokGlobus(
                        // Podemos inserir o código de contexto da pergunta com segurança, pois, se existir uma
                        // alternativa criada, com certeza o código de contexto da pergunta existirá. E se não existir
                        // nenhuma alternativa, o fluxo não chega nesse ponto.
                        codContextoPergunta,
                        resposta.getPergunta(),
                        alternativasNok));
            }
        }
        return new ChecklistItensNokGlobus(
                codUnidadeProLog,
                codChecklistProLog,
                checklist.getCodModelo(),
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
        osGlobus.setCodModeloChecklist(checklistItensNokGlobus.getCodModeloChecklistRealizado().intValue());
        osGlobus.setCpfColaboradorRealizacao(checklistItensNokGlobus.getCpfColaboradorRealizacao());
        osGlobus.setPlacaVeiculoChecklist(checklistItensNokGlobus.getPlacaVeiculoChecklist());
        osGlobus.setKmColetadoChecklist(checklistItensNokGlobus.getKmColetadoChecklist().intValue());
        osGlobus.setTipoChecklist(checklistItensNokGlobus.getTipoChecklist().asString());
        osGlobus.setDataHoraRealizacaoUtc(
                DatatypeFactory.newInstance().newXMLGregorianCalendar(
                        checklistItensNokGlobus.getDataHoraRealizacaoUtc()));
        osGlobus.setUsuario(GlobusPiccoloturConstants.USUARIO_PROLOG_INTEGRACAO);
        osGlobus.setListaPerguntasNokVO(convertPerguntas(factory, checklistItensNokGlobus.getPerguntasNok()));
        return osGlobus;
    }

    @NotNull
    public static ProcessoMovimentacaoGlobus convert(
            @NotNull final Long codUnidadeMovimento,
            @NotNull final String usuarioGlobus,
            @NotNull final GlobusPiccoloturLocalMovimento localMovimentoGlobus,
            @NotNull final ProcessoMovimentacao processoMovimentacao,
            @NotNull final LocalDateTime dataHoraMovimentacao) {
        final List<MovimentacaoGlobus> movimentacoesGlobus = new ArrayList<>();
        for (int i = 0; i < processoMovimentacao.getMovimentacoes().size(); i++) {
            final Movimentacao movimentacao = processoMovimentacao.getMovimentacoes().get(i);
            if (movimentacao.isFromOrigemToDestino(OrigemDestinoEnum.ESTOQUE, OrigemDestinoEnum.VEICULO)) {
                final DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
                movimentacoesGlobus.add(new MovimentacaoGlobus(
                        (long) i,
                        GlobusPiccoloturUtils.addHifenPlacaSePadraoAntigo(destinoVeiculo.getVeiculo().getPlaca()),
                        dataHoraMovimentacao,
                        GlobusPiccoloturUtils.formatNumeroFogo(movimentacao.getPneu().getCodigoCliente()),
                        MovimentacaoGlobus.PNEU_INSERIDO,
                        codUnidadeMovimento,
                        localMovimentoGlobus.getCodLocalGlobus(),
                        usuarioGlobus,
                        movimentacao.getObservacao(),
                        destinoVeiculo.getPosicaoDestinoPneu()));
            } else if (movimentacao.isFromOrigemToDestino(OrigemDestinoEnum.VEICULO, OrigemDestinoEnum.ESTOQUE)) {
                final OrigemVeiculo origemVeiculo = ((OrigemVeiculo) movimentacao.getOrigem());
                movimentacoesGlobus.add(new MovimentacaoGlobus(
                        (long) i,
                        GlobusPiccoloturUtils.addHifenPlacaSePadraoAntigo(origemVeiculo.getVeiculo().getPlaca()),
                        dataHoraMovimentacao,
                        GlobusPiccoloturUtils.formatNumeroFogo(movimentacao.getPneu().getCodigoCliente()),
                        MovimentacaoGlobus.PNEU_RETIRADO,
                        codUnidadeMovimento,
                        localMovimentoGlobus.getCodLocalGlobus(),
                        usuarioGlobus,
                        movimentacao.getObservacao(),
                        origemVeiculo.getPosicaoOrigemPneu()));
            } else if (movimentacao.isFromOrigemToDestino(OrigemDestinoEnum.VEICULO, OrigemDestinoEnum.VEICULO)) {
                final OrigemVeiculo origemVeiculo = ((OrigemVeiculo) movimentacao.getOrigem());
                final DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
                final String codigoCliente = movimentacao.getPneu().getCodigoCliente();
                // Retira pneu
                movimentacoesGlobus.add(new MovimentacaoGlobus(
                        (long) i,
                        GlobusPiccoloturUtils.addHifenPlacaSePadraoAntigo(origemVeiculo.getVeiculo().getPlaca()),
                        dataHoraMovimentacao,
                        GlobusPiccoloturUtils.formatNumeroFogo(codigoCliente),
                        MovimentacaoGlobus.PNEU_RETIRADO,
                        codUnidadeMovimento,
                        localMovimentoGlobus.getCodLocalGlobus(),
                        usuarioGlobus,
                        movimentacao.getObservacao(),
                        origemVeiculo.getPosicaoOrigemPneu()));

                // Aplica pneu
                movimentacoesGlobus.add(new MovimentacaoGlobus(
                        (long) i,
                        GlobusPiccoloturUtils.addHifenPlacaSePadraoAntigo(destinoVeiculo.getVeiculo().getPlaca()),
                        dataHoraMovimentacao,
                        GlobusPiccoloturUtils.formatNumeroFogo(codigoCliente),
                        MovimentacaoGlobus.PNEU_INSERIDO,
                        codUnidadeMovimento,
                        localMovimentoGlobus.getCodLocalGlobus(),
                        usuarioGlobus,
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
    public static CampoPersonalizadoParaRealizacao convert(
            @NotNull final CampoPersonalizadoParaRealizacao campoSelecaoLocalMovimento,
            @NotNull final List<GlobusPiccoloturLocalMovimento> locaisMovimentoGlobus) {
        return new CampoPersonalizadoParaRealizacao(
                campoSelecaoLocalMovimento.getCodigo(),
                campoSelecaoLocalMovimento.getCodEmpresa(),
                campoSelecaoLocalMovimento.getCodFuncaoProlog(),
                campoSelecaoLocalMovimento.getTipoCampo(),
                campoSelecaoLocalMovimento.getNomeCampo(),
                campoSelecaoLocalMovimento.getDescricaoCampo(),
                campoSelecaoLocalMovimento.getTextoAuxilioPreenchimento(),
                campoSelecaoLocalMovimento.isPreenchimentoObrigatorio(),
                campoSelecaoLocalMovimento.getMensagemCasoCampoNaoPreenchido(),
                campoSelecaoLocalMovimento.getPermiteSelecaoMultipla(),
                locaisMovimentoGlobus
                        .stream()
                        .map(GlobusPiccoloturLocalMovimento::getLocalAsOpcaoSelecao)
                        .toArray(String[]::new),
                campoSelecaoLocalMovimento.getOrdemExibicao());
    }

    @NotNull
    private static ArrayOfPerguntasNokVO convertPerguntas(@NotNull final ObjectFactory factory,
                                                          @NotNull final List<PerguntaNokGlobus> perguntasNok) {
        final ArrayOfPerguntasNokVO arrayOfPerguntasNokVO = factory.createArrayOfPerguntasNokVO();
        for (final PerguntaNokGlobus perguntaNokGlobus : perguntasNok) {
            final PerguntasNokVO perguntaNokVO = factory.createPerguntasNokVO();
            perguntaNokVO.setCodPerguntaNok(perguntaNokGlobus.getCodContextoPerguntaNok().intValue());
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
            alternativaNokVO.setCodAlternativaNok(alternativaNokGlobus.getCodContextoAlternativaNok().intValue());
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
