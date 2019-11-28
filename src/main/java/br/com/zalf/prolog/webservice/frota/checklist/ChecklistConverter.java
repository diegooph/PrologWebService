package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.*;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.*;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.OLD.ItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import com.google.common.annotations.VisibleForTesting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 01/08/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@VisibleForTesting
public final class ChecklistConverter {

    private ChecklistConverter() {
        throw new IllegalStateException(ChecklistConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static RegionalSelecaoChecklist createRegionalSelecao(
            @NotNull final ResultSet rSet,
            @NotNull final List<UnidadeSelecaoChecklist> unidades) throws Throwable {
        return new RegionalSelecaoChecklist(
                rSet.getLong("CODIGO_REGIONAL"),
                rSet.getString("NOME_REGIONAL"),
                unidades);
    }

    @NotNull
    public static UnidadeSelecaoChecklist createUnidadeSelecao(
            @NotNull final ResultSet rSet) throws Throwable {
        return new UnidadeSelecaoChecklist(
                rSet.getLong("CODIGO_UNIDADE"),
                rSet.getString("NOME_UNIDADE"),
                rSet.getLong("CODIGO_REGIONAL"));
    }

    @VisibleForTesting
    @NotNull
    public static Checklist createChecklist(@NotNull final ResultSet rSet,
                                            final boolean setTotalItensOkNok) throws SQLException {
        final Checklist checklist = new Checklist();
        checklist.setCodigo(rSet.getLong("COD_CHECKLIST"));
        checklist.setCodModelo(rSet.getLong("COD_CHECKLIST_MODELO"));
        checklist.setCodVersaoModeloChecklist(rSet.getLong("COD_VERSAO_CHECKLIST_MODELO"));
        checklist.setColaborador(createColaborador(rSet));
        checklist.setData(rSet.getObject("DATA_HORA_REALIZACAO", LocalDateTime.class));
        checklist.setDataHoraImportadoProLog(rSet.getObject("DATA_HORA_IMPORTADO_PROLOG", LocalDateTime.class));
        checklist.setPlacaVeiculo(rSet.getString("PLACA_VEICULO"));
        checklist.setTipo(rSet.getString("TIPO_CHECKLIST").charAt(0));
        checklist.setKmAtualVeiculo(rSet.getLong("KM_VEICULO_MOMENTO_REALIZACAO"));
        checklist.setTempoRealizacaoCheckInMillis(rSet.getLong("DURACAO_REALIZACAO_MILLIS"));
        if (setTotalItensOkNok) {
            checklist.setQtdItensOk(rSet.getInt("TOTAL_ITENS_OK"));
            checklist.setQtdItensNok(rSet.getInt("TOTAL_ITENS_NOK"));
        }
        return checklist;
    }

    @VisibleForTesting
    @NotNull
    public static List<PerguntaRespostaChecklist> createPerguntasRespostasChecklist(
            @NotNull final ResultSet rSet) throws SQLException {
        final List<PerguntaRespostaChecklist> perguntas = new ArrayList<>();
        List<AlternativaChecklist> alternativas = new ArrayList<>();
        PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
        AlternativaChecklist alternativa;
        if (rSet.first()) {
            pergunta = createPergunta(rSet);
            alternativa = createAlternativa(rSet);
            setRespostaAlternativa(alternativa, rSet);
            alternativas.add(alternativa);
        }
        while (rSet.next()) {
            if (rSet.getLong("COD_PERGUNTA") == pergunta.getCodigo()) {
                alternativa = createAlternativa(rSet);
                setRespostaAlternativa(alternativa, rSet);
                alternativas.add(alternativa);
            } else {
                pergunta.setAlternativasResposta(alternativas);
                perguntas.add(pergunta);
                alternativas = new ArrayList<>();

                pergunta = createPergunta(rSet);
                alternativa = createAlternativa(rSet);
                setRespostaAlternativa(alternativa, rSet);
                alternativas.add(alternativa);
            }
        }
        pergunta.setAlternativasResposta(alternativas);
        perguntas.add(pergunta);
        return perguntas;
    }

    @NotNull
    static AlternativaChecklistStatus createAlternativaChecklistStatus(
            @NotNull final ResultSet rSet) throws SQLException {
        return new AlternativaChecklistStatus(
                rSet.getLong("COD_ALTERNATIVA"),
                rSet.getLong("COD_ITEM_ORDEM_SERVICO"),
                rSet.getBoolean("TEM_ITEM_OS_PENDENTE"),
                rSet.getBoolean("DEVE_ABRIR_ORDEM_SERVICO"),
                rSet.getInt("QTD_APONTAMENTOS_ITEM"),
                PrioridadeAlternativa.fromString(rSet.getString("PRIORIDADE_ALTERNATIVA")));
    }

    @NotNull
    static DeprecatedFarolChecklist createFarolChecklist(@NotNull final ResultSet rSet) throws SQLException {
        final List<FarolVeiculoDia> farolVeiculoDias = new ArrayList<>();
        String placaAntiga = null, placaAtual = null;
        while (rSet.next()) {
            if (rSet.getLong("CODIGO_PERGUNTA") == 0) {
                // Verificamos se existem perguntas. Se não, podemos instanciar apenas as informações do Checklist
                farolVeiculoDias.add(createFarolVeiculoDiaSemItensCriticos(rSet));
            } else {
                placaAtual = rSet.getString("PLACA");
                if (placaAtual.equals(placaAntiga)) {
                    createAndAddPerguntaCritica(rSet, farolVeiculoDias, placaAtual);
                } else {
                    farolVeiculoDias.add(createFarolVeiculoDiaComItensCriticos(rSet));
                }
                // Após processar a placa, mudamos a variável dela
                placaAntiga = placaAtual;
            }

        }
        return parseToDeprecatedFarolChecklist(new FarolChecklist(farolVeiculoDias));
    }

    @NotNull
    static PerguntaRespostaChecklist createPergunta(ResultSet rSet) throws SQLException {
        final PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
        pergunta.setCodigo(rSet.getLong("COD_PERGUNTA"));
        pergunta.setOrdemExibicao(rSet.getInt("ORDEM_PERGUNTA"));
        pergunta.setPergunta(rSet.getString("DESCRICAO_PERGUNTA"));
        pergunta.setSingleChoice(rSet.getBoolean("PERGUNTA_SINGLE_CHOICE"));
        pergunta.setUrl(rSet.getString("URL_IMAGEM"));
        pergunta.setCodImagem(rSet.getLong("COD_IMAGEM"));
        return pergunta;
    }

    @NotNull
    static AlternativaChecklist createAlternativaComResposta(@NotNull final ResultSet rSet) throws SQLException {
        final AlternativaChecklist alternativaChecklist = createAlternativa(rSet);
        setRespostaAlternativa(alternativaChecklist, rSet);
        return alternativaChecklist;
    }

    @NotNull
    private static DeprecatedFarolChecklist parseToDeprecatedFarolChecklist(
            @NotNull final FarolChecklist farolChecklist) {
        final List<DeprecatedFarolVeiculoDia> veiculoDias = new ArrayList<>();
        for (final FarolVeiculoDia veiculoDia : farolChecklist.getFarolVeiculos()) {
            final Veiculo veiculo = new Veiculo();
            veiculo.setPlaca(veiculoDia.getPlacaVeiculo());
            final Checklist checklistSaida = parseToChecklist(veiculoDia.getChecklistSaidaDia());
            final Checklist checklistRetorno = parseToChecklist(veiculoDia.getChecklistRetornoDia());
            List<ItemOrdemServico> itensCriticos = null;
            if (veiculoDia.getPerguntasCriticasEmAberto() != null) {
                itensCriticos = parseToItemOrdemServico(veiculoDia.getPerguntasCriticasEmAberto());
            }
            final DeprecatedFarolVeiculoDia deprecatedVeiculoDia =
                    new DeprecatedFarolVeiculoDia(veiculo, checklistSaida, checklistRetorno, itensCriticos);
            veiculoDias.add(deprecatedVeiculoDia);
        }
        return new DeprecatedFarolChecklist(veiculoDias);
    }

    @NotNull
    private static List<ItemOrdemServico> parseToItemOrdemServico(
            @NotNull final List<FarolPerguntaCritica> perguntasCriticas) {
        final List<ItemOrdemServico> itens = new ArrayList<>();

        for (final FarolPerguntaCritica perguntasCritica : perguntasCriticas) {
            for (final FarolItemCritico itemCritico : perguntasCritica.getItensCriticosEmAberto()) {
                final ItemOrdemServico servico = new ItemOrdemServico();
                final PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
                pergunta.setCodigo(perguntasCritica.getCodigoPergunta());
                pergunta.setPergunta(perguntasCritica.getDescricaoPergunta());

                final List<AlternativaChecklist> alternativas = new ArrayList<>();
                final AlternativaChecklist alternativa = new AlternativaChecklist();
                if (itemCritico.isRespostaTipoOutros()) {
                    alternativa.setAlternativa(itemCritico.getDescricaoRespostaTipoOutros());
                    alternativa.setRespostaOutros(null);
                    alternativa.setTipo(Alternativa.TIPO_OUTROS);
                } else {
                    alternativa.setAlternativa(itemCritico.getRespostaSelecionada());
                    alternativa.setRespostaOutros(null);
                }
                alternativas.add(alternativa);
                pergunta.setAlternativasResposta(alternativas);
                servico.setPergunta(pergunta);
                servico.setDataApontamento(itemCritico.getDataHoraApontamentoItemCritico());
                servico.setCodigo(itemCritico.getCodigoItemCritico());
                itens.add(servico);
            }
        }

        return itens;
    }

    @Nullable
    private static Checklist parseToChecklist(@Nullable final ChecklistFarol checklistSaidaDia) {
        if (checklistSaidaDia == null) {
            return null;
        }

        final Checklist checklist = new Checklist();
        checklist.setCodigo(checklistSaidaDia.getCodigoChecklist());
        final Colaborador colaborador = new Colaborador();
        colaborador.setNome(checklistSaidaDia.getNomeColaboradorRealizacao());
        checklist.setColaborador(colaborador);
        checklist.setData(checklistSaidaDia.getDataHoraRealizacao());
        checklist.setTipo(checklistSaidaDia.getTipoChecklist());
        return checklist;
    }

    private static void createAndAddPerguntaCritica(@NotNull final ResultSet rSet,
                                                    @NotNull final List<FarolVeiculoDia> farolVeiculoDias,
                                                    @NotNull final String placaAtual) throws SQLException {
        boolean itemCriticoAdicionado = false;
        for (final FarolVeiculoDia farolVeiculoDia : farolVeiculoDias) {
            if (farolVeiculoDia.getPlacaVeiculo().equals(placaAtual)) {
                final List<FarolPerguntaCritica> perguntasCriticas = farolVeiculoDia.getPerguntasCriticasEmAberto();
                if (perguntasCriticas != null && !perguntasCriticas.isEmpty()) {
                    for (final FarolPerguntaCritica perguntaCritica : perguntasCriticas) {
                        if (perguntaCritica.getCodigoPergunta().equals(rSet.getLong("CODIGO_PERGUNTA"))) {
                            perguntaCritica.getItensCriticosEmAberto().add(createItemCritico(rSet));
                            itemCriticoAdicionado = true;
                        }
                    }
                    if (!itemCriticoAdicionado) {
                        perguntasCriticas.add(createAndAddPerguntaCritica(rSet));
                    }
                } else {
                    // TODO - O que deveremos fazer aqui?
                    throw new IllegalStateException("Lista de perguntas críticas é nulla para a placa: " + placaAtual);
                }
            }
        }
    }

    @NotNull
    private static FarolVeiculoDia createFarolVeiculoDiaComItensCriticos(@NotNull final ResultSet rSet)
            throws SQLException {
        final FarolVeiculoDia farolVeiculoDia = createFarolVeiculoDiaSemItensCriticos(rSet);
        final List<FarolPerguntaCritica> perguntasCriticas = farolVeiculoDia.getPerguntasCriticasEmAberto();
        if (perguntasCriticas != null) {
            perguntasCriticas.add(createAndAddPerguntaCritica(rSet));
        }
        return farolVeiculoDia;
    }

    @NotNull
    private static FarolPerguntaCritica createAndAddPerguntaCritica(@NotNull final ResultSet rSet) throws SQLException {
        final List<FarolItemCritico> itensCriticos = new ArrayList<>();
        itensCriticos.add(createItemCritico(rSet));
        return new FarolPerguntaCritica(
                rSet.getLong("CODIGO_PERGUNTA"),
                rSet.getString("DESCRICAO_PERGUNTA"),
                itensCriticos);
    }

    @NotNull
    private static FarolItemCritico createItemCritico(@NotNull final ResultSet rSet) throws SQLException {
        return new FarolItemCritico(
                rSet.getLong("CODIGO_ITEM_CRITICO"),
                rSet.getObject("DATA_HORA_APONTAMENTO_ITEM_CRITICO", LocalDateTime.class),
                rSet.getString("DESCRICAO_ALTERNATIVA"),
                rSet.getString("DESCRICAO_ALTERNATIVA_TIPO_OUTROS"));
    }

    @NotNull
    private static FarolVeiculoDia createFarolVeiculoDiaSemItensCriticos(@NotNull final ResultSet rSet)
            throws SQLException {
        final String placa = rSet.getString("PLACA");

        ChecklistFarol checkSaida = null;
        final Long codChecklistSaida = rSet.getLong("COD_CHECKLIST_SAIDA");
        if (!rSet.wasNull()) {
            checkSaida = new ChecklistFarol(
                    codChecklistSaida,
                    rSet.getString("NOME_COLABORADOR_CHECKLIST_SAIDA"),
                    rSet.getObject("DATA_HORA_ULTIMO_CHECKLIST_SAIDA", LocalDateTime.class),
                    Checklist.TIPO_SAIDA);
        }
        ChecklistFarol checkRetorno = null;
        final Long codChecklistRetorno = rSet.getLong("COD_CHECKLIST_RETORNO");
        if (!rSet.wasNull()) {
            checkRetorno = new ChecklistFarol(
                    codChecklistRetorno,
                    rSet.getString("NOME_COLABORADOR_CHECKLIST_RETORNO"),
                    rSet.getObject("DATA_HORA_ULTIMO_CHECKLIST_RETORNO", LocalDateTime.class),
                    Checklist.TIPO_RETORNO);
        }
        return new FarolVeiculoDia(placa, checkSaida, checkRetorno, new ArrayList<>());
    }

    @NotNull
    private static Colaborador createColaborador(@NotNull final ResultSet rSet) throws SQLException {
        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(rSet.getLong("CPF_COLABORADOR"));
        colaborador.setNome(rSet.getString("NOME_COLABORADOR"));
        return colaborador;
    }

    @SuppressWarnings("Duplicates")
    @NotNull
    private static AlternativaChecklist createAlternativa(@NotNull final ResultSet rSet) throws SQLException {
        final AlternativaChecklist alternativa = new AlternativaChecklist();
        alternativa.setCodigo(rSet.getLong("COD_ALTERNATIVA"));
        alternativa.setAlternativa(rSet.getString("DESCRICAO_ALTERNATIVA"));
        alternativa.setPrioridade(PrioridadeAlternativa.fromString(rSet.getString("PRIORIDADE_ALTERNATIVA")));
        alternativa.setOrdemExibicao(rSet.getInt("ORDEM_ALTERNATIVA"));
        if (alternativa.getAlternativa().equals("Outros")) {
            alternativa.setTipo(AlternativaChecklist.TIPO_OUTROS);
        }
        return alternativa;
    }

    private static void setRespostaAlternativa(@NotNull final AlternativaChecklist alternativa,
                                               @NotNull final ResultSet rSet) throws SQLException {
        if (rSet.getBoolean("ALTERNATIVA_SELECIONADA")) {
            alternativa.selected = true;
            if (rSet.getBoolean("ALTERNATIVA_TIPO_OUTROS")) {
                alternativa.tipo = AlternativaChecklist.TIPO_OUTROS;
                alternativa.respostaOutros = rSet.getString("RESPOSTA_OUTROS");
            }
        } else {
            alternativa.selected = false;
        }
    }
}