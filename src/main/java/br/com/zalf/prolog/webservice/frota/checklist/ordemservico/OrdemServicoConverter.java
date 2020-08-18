package br.com.zalf.prolog.webservice.frota.checklist.ordemservico;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.InfosAlternativaAberturaOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem.OrdemServicoAbertaListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem.OrdemServicoFechadaListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem.OrdemServicoListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem.QtdItensPlacaListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.HolderResolucaoItensOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.HolderResolucaoOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.OrdemServicoAbertaVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.OrdemServicoFechadaVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.OrdemServicoVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.item.*;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 22/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class OrdemServicoConverter {

    private OrdemServicoConverter() {
        throw new IllegalStateException(OrdemServicoConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    static InfosAlternativaAberturaOrdemServico createAlternativaChecklistAbreOrdemServico(
            @NotNull final ResultSet rSet) throws Throwable {
        return new InfosAlternativaAberturaOrdemServico(
                rSet.getLong("COD_ALTERNATIVA"),
                rSet.getLong("COD_CONTEXTO_PERGUNTA"),
                rSet.getLong("COD_CONTEXTO_ALTERNATIVA"),
                rSet.getLong("COD_ITEM_ORDEM_SERVICO"),
                rSet.getString("RESPOSTA_TIPO_OUTROS_ABERTURA_ITEM"),
                rSet.getInt("QTD_APONTAMENTOS_ITEM"),
                rSet.getBoolean("DEVE_ABRIR_ORDEM_SERVICO"),
                rSet.getBoolean("ALTERNATIVA_TIPO_OUTROS"),
                PrioridadeAlternativa.fromString(rSet.getString("PRIORIDADE_ALTERNATIVA")));
    }

    @NotNull
    static OrdemServicoListagem createOrdemServicoListagem(@NotNull final ResultSet rSet) throws Throwable {
        final StatusOrdemServico status = StatusOrdemServico.fromString(rSet.getString("STATUS_OS"));

        final OrdemServicoListagem ordem;
        if (status.equals(StatusOrdemServico.ABERTA)) {
            ordem = new OrdemServicoAbertaListagem();
        } else {
            ordem = new OrdemServicoFechadaListagem();
            final LocalDateTime dataHoraFechamento =
                    rSet.getObject("DATA_HORA_FECHAMENTO", LocalDateTime.class);
            ((OrdemServicoFechadaListagem) ordem).setDataHoraFechamento(dataHoraFechamento);
        }
        ordem.setCodOrdemServico(rSet.getLong("COD_OS"));
        ordem.setCodUnidadeOrdemServico(rSet.getLong("COD_UNIDADE_OS"));
        ordem.setPlacaVeiculo(rSet.getString("PLACA_VEICULO"));
        ordem.setDataHoraAbertura(rSet.getObject("DATA_HORA_ABERTURA", LocalDateTime.class));
        ordem.setQtdItensPendentes(rSet.getInt("QTD_ITENS_PENDENTES"));
        ordem.setQtdItensResolvidos(rSet.getInt("QTD_ITENS_RESOLVIDOS"));
        return ordem;
    }

    @NotNull
    static QtdItensPlacaListagem createQtdItensPlacaListagem(@NotNull final ResultSet rSet) throws Throwable {
        final QtdItensPlacaListagem qtdItens = new QtdItensPlacaListagem();
        qtdItens.setPlacaVeiculo(rSet.getString("PLACA_VEICULO"));
        qtdItens.setQtdCritica(rSet.getInt("QTD_ITENS_PRIORIDADE_CRITICA"));
        qtdItens.setQtdAlta(rSet.getInt("QTD_ITENS_PRIORIDADE_ALTA"));
        qtdItens.setQtdBaixa(rSet.getInt("QTD_ITENS_PRIORIDADE_BAIXA"));
        return qtdItens;
    }

    @NotNull
    static HolderResolucaoOrdemServico createHolderResolucaoOrdemServico(
            @NotNull final ResultSet rSet) throws Throwable {
        final HolderResolucaoOrdemServico holder = new HolderResolucaoOrdemServico();
        holder.setPlacaVeiculo(rSet.getString("PLACA_VEICULO"));
        holder.setKmAtualVeiculo(rSet.getLong("KM_ATUAL_VEICULO"));
        holder.setOrdemServico(createOrdemServicoVisualizacao(rSet));
        return holder;
    }

    @NotNull
    static HolderResolucaoItensOrdemServico createHolderResolucaoItensOrdemServico(@NotNull final ResultSet rSet)
            throws Throwable {
        final HolderResolucaoItensOrdemServico holder = new HolderResolucaoItensOrdemServico();
        holder.setPlacaVeiculo(rSet.getString("PLACA_VEICULO"));
        holder.setKmAtualVeiculo(rSet.getLong("KM_ATUAL_VEICULO"));

        // Verifica se veio dados na query ou apenas a primeira linha para preenchimento do holder.
        if (rSet.getLong("COD_ITEM_OS") > 0) {
            holder.setItens(createItens(rSet));
        } else {
            holder.setItens(new ArrayList<>());
        }

        return holder;
    }

    @NotNull
    private static OrdemServicoVisualizacao createOrdemServicoVisualizacao(
            @NotNull final ResultSet rSet) throws Throwable {
        final StatusOrdemServico status =
                StatusOrdemServico.fromString(rSet.getString("STATUS_OS"));
        final OrdemServicoVisualizacao ordem;
        if (status.equals(StatusOrdemServico.ABERTA)) {
            ordem = new OrdemServicoAbertaVisualizacao();
        } else {
            ordem = new OrdemServicoFechadaVisualizacao();
            ((OrdemServicoFechadaVisualizacao) ordem)
                    .setDataHoraFechamento(rSet.getObject("DATA_HORA_FECHAMENTO_OS", LocalDateTime.class));
        }
        ordem.setCodOrdemServico(rSet.getLong("COD_OS"));
        ordem.setPlacaVeiculo(rSet.getString("PLACA_VEICULO"));
        ordem.setDataHoraAbertura(rSet.getObject("DATA_HORA_ABERTURA_OS", LocalDateTime.class));
        ordem.setItens(createItens(rSet));
        return ordem;
    }

    @NotNull
    private static List<ItemOrdemServicoVisualizacao> createItens(@NotNull final ResultSet rSet) throws Throwable {
        final List<ItemOrdemServicoVisualizacao> itens = new ArrayList<>();
        boolean isFirstLine = true;
        Long codItemAntigo = null;
        long codItemAtual;
        ItemOrdemServicoVisualizacao item = null;
        do {
            codItemAtual = rSet.getLong("COD_ITEM_OS");
            if (codItemAntigo == null) {
                codItemAntigo = codItemAtual;
            }

            if (isFirstLine) {
                item = createItemOrdemServicoVisualizacao(rSet);
                isFirstLine = false;
            }

            if (!codItemAntigo.equals(codItemAtual)) {
                itens.add(item);
                item = createItemOrdemServicoVisualizacao(rSet);
            }
            if (rSet.getString("URL_MIDIA") != null) {
                item.getImagensVinculadas().add(createImagemVinculadaChecklistVisualizacao(rSet));
            }
            codItemAntigo = codItemAtual;
        } while (rSet.next());
        itens.add(item);
        return itens;
    }

    @NotNull
    private static ItemOrdemServicoVisualizacao createItemOrdemServicoVisualizacao(
            @NotNull final ResultSet rSet) throws Throwable {
        final StatusItemOrdemServico status =
                StatusItemOrdemServico.fromString(rSet.getString("STATUS_ITEM_OS"));
        final ItemOrdemServicoVisualizacao item;
        if (status.equals(StatusItemOrdemServico.PENDENTE)) {
            item = new ItemOrdemServicoPendente();
        } else {
            item = new ItemOrdemServicoResolvido();
            final ItemOrdemServicoResolvido resolvido = (ItemOrdemServicoResolvido) item;
            resolvido.setCodColaboradorResolucao(rSet.getLong("COD_COLABORADOR_RESOLUCAO"));
            resolvido.setNomeColaboradorResolucao(rSet.getString("NOME_COLABORADOR_RESOLUCAO"));
            resolvido.setDataHoraResolvidoProLog(rSet.getObject("DATA_HORA_RESOLUCAO", LocalDateTime.class));
            resolvido.setFeedbackResolucao(rSet.getString("FEEDBACK_RESOLUCAO"));
            resolvido.setDataHoraInicioResolucao(rSet.getObject("DATA_HORA_INICIO_RESOLUCAO", LocalDateTime.class));
            resolvido.setDataHoraFimResolucao(rSet.getObject("DATA_HORA_FIM_RESOLUCAO", LocalDateTime.class));
            resolvido.setDuracaoResolucao(Duration.ofMinutes(rSet.getLong("DURACAO_RESOLUCAO_MINUTOS")));
            resolvido.setKmVeiculoColetadoResolucao(rSet.getLong("KM_VEICULO_COLETADO_RESOLUCAO"));
        }
        item.setStatus(status);
        item.setCodigo(rSet.getLong("COD_ITEM_OS"));
        item.setCodOrdemServico(rSet.getLong("COD_OS"));
        item.setCodUnidadeItemOrdemServico(rSet.getLong("COD_UNIDADE_ITEM_OS"));
        item.setPergunta(createPerguntaItemOrdemServico(rSet));
        final LocalDateTime dataHoraPrimeiroApontamentoItem =
                rSet.getObject("DATA_HORA_PRIMEIRO_APONTAMENTO_ITEM", LocalDateTime.class);
        item.setDataHoraPrimeiroApontamento(dataHoraPrimeiroApontamentoItem);
        item.setPrazoResolucaoItem(Duration.ofHours(rSet.getInt("PRAZO_RESOLUCAO_ITEM_HORAS")));
        final Duration prazoRestanteResolucao = Duration.ofMinutes(
                rSet.getLong("PRAZO_RESTANTE_RESOLUCAO_ITEM_MINUTOS"));
        item.setPrazoRestanteResolucaoItem(prazoRestanteResolucao);
        item.setQtdApontamentos(rSet.getInt("QTD_APONTAMENTOS"));
        item.setImagensVinculadas(new ArrayList<>());
        return item;
    }

    @NotNull
    private static ImagemVinculadaChecklistVisualizacao createImagemVinculadaChecklistVisualizacao(
            @NotNull final ResultSet rSet) throws Throwable {
        return new ImagemVinculadaChecklistVisualizacao(
                rSet.getLong("COD_CHECKLIST"),
                rSet.getString("URL_MIDIA"));
    }

    @NotNull
    private static PerguntaItemOrdemServico createPerguntaItemOrdemServico(
            @NotNull final ResultSet rSet) throws Throwable {
        final PerguntaItemOrdemServico pergunta = new PerguntaItemOrdemServico();
        pergunta.setCodPergunta(rSet.getLong("COD_PERGUNTA"));
        pergunta.setDescricao(rSet.getString("DESCRICAO_PERGUNTA"));
        pergunta.setAlternativaMarcada(createAlternativaItemOrdemServico(rSet));
        return pergunta;
    }

    @NotNull
    private static AlternativaItemOrdemServico createAlternativaItemOrdemServico(
            @NotNull final ResultSet rSet) throws Throwable {
        final AlternativaItemOrdemServico alternativa = new AlternativaItemOrdemServico();
        alternativa.setCodAlteranativa(rSet.getLong("COD_ALTERNATIVA"));
        alternativa.setDescricao(rSet.getString("DESCRICAO_ALTERNATIVA"));
        alternativa.setTipoOutros(rSet.getBoolean("ALTERNATIVA_TIPO_OUTROS"));
        alternativa.setDescricaoTipoOutros(rSet.getString("DESCRICAO_TIPO_OUTROS"));
        alternativa.setPrioridade(PrioridadeAlternativa.fromString(
                rSet.getString("PRIORIDADE_ALTERNATIVA")));
        return alternativa;
    }

}