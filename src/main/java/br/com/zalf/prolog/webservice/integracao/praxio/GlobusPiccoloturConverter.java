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
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.AlternativaNokDto;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.OrdemServicoCorretivaDto;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.OrdemServicoHolderDto;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.PerguntaNokDto;
import org.jetbrains.annotations.NotNull;

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
                        resposta.getCodigo(),
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
    public static OrdemServicoHolderDto convert(
            @NotNull final ChecklistItensNokGlobus checklistItensNokGlobus) throws Throwable {
        final OrdemServicoCorretivaDto os = new OrdemServicoCorretivaDto(
                checklistItensNokGlobus.getCodUnidadeChecklist().intValue(),
                checklistItensNokGlobus.getCodChecklistRealizado().intValue(),
                checklistItensNokGlobus.getCodModeloChecklistRealizado().intValue(),
                checklistItensNokGlobus.getCpfColaboradorRealizacao(),
                checklistItensNokGlobus.getPlacaVeiculoChecklist(),
                checklistItensNokGlobus.getKmColetadoChecklist().intValue(),
                checklistItensNokGlobus.getTipoChecklist().asString(),
                checklistItensNokGlobus.getDataHoraRealizacaoUtc(),
                GlobusPiccoloturConstants.USUARIO_PROLOG_INTEGRACAO,
                convertPerguntas(checklistItensNokGlobus));
        return new OrdemServicoHolderDto(os);
    }

    @NotNull
    private static List<PerguntaNokDto> convertPerguntas(
            @NotNull final ChecklistItensNokGlobus checklistItensNokGlobus) {
        final List<PerguntaNokDto> perguntas = new ArrayList<>();
        checklistItensNokGlobus
                .getPerguntasNok()
                .forEach(pergunta -> perguntas.add(new PerguntaNokDto(
                        pergunta.getCodPerguntaNok().intValue(),
                        pergunta.getDescricaoPerguntaNok(),
                        convertAlternativas(pergunta.getAlternativasNok()))));
        return perguntas;
    }

    @NotNull
    private static List<AlternativaNokDto> convertAlternativas(
            @NotNull final List<AlternativaNokGlobus> alternativas) {
        final List<AlternativaNokDto> alternativasConvertidas = new ArrayList<>();
        alternativas.forEach(alternativa -> alternativasConvertidas.add(new AlternativaNokDto(
                alternativa.getCodContextoAlternativaNok().intValue(),
                alternativa.getDescricaoAlternativaNok(),
                alternativa.getPrioridadeAlternativaNok().asString())));
        return alternativasConvertidas;
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
