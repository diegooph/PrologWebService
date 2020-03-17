package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.OLD;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import com.google.common.annotations.VisibleForTesting;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 07/08/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@VisibleForTesting
@Deprecated
public final class DeprecatedOrdemServicoConverter {

    @Deprecated
    private DeprecatedOrdemServicoConverter() {
        throw new IllegalStateException(DeprecatedOrdemServicoConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @VisibleForTesting
    @NotNull
    @Deprecated
    public static OrdemServico createOrdemServicoSemItens(@NotNull final ResultSet rSet) throws SQLException {
        final OrdemServico os = new OrdemServico();
        os.setCodChecklist(rSet.getLong("cod_checklist"));
        os.setCodigo(rSet.getLong("cod_os"));
        os.setStatus(OrdemServico.Status.fromString(rSet.getString("status")));
        final Veiculo v = new Veiculo();
        v.setKmAtual(rSet.getLong("km"));
        v.setPlaca(rSet.getString("placa_veiculo"));
        v.setAtivo(true);
        os.setVeiculo(v);
        os.setDataAbertura(rSet.getObject("data_hora", LocalDateTime.class));
        os.setDataFechamento(rSet.getObject("data_hora_fechamento", LocalDateTime.class));
        return os;
    }

    @VisibleForTesting
    @NotNull
    @Deprecated
    public static List<ItemOrdemServico> createItensOrdemServico(@NotNull final ResultSet rSet) throws SQLException {
        final List<ItemOrdemServico> itens = new ArrayList<>();
        ItemOrdemServico item = null;
        PerguntaRespostaChecklist pergunta = null;
        AlternativaChecklist alternativa = null;
        List<AlternativaChecklist> alternativas = null;
        Colaborador mecanico = null;
        while (rSet.next()) {
            item = new ItemOrdemServico();
            item.setCodigo(rSet.getLong("codigo"));
            item.setCodOs(rSet.getLong("cod_os"));
            item.setCodUnidadeItemOs(rSet.getLong("COD_UNIDADE"));
            pergunta = createPergunta(rSet);
            alternativa = createAlternativa(rSet);
            alternativas = new ArrayList<>();
            alternativas.add(alternativa);
            pergunta.setAlternativasResposta(alternativas);
            item.setPergunta(pergunta);
            item.setPlaca(rSet.getString("placa_veiculo"));
            item.setDataApontamento(rSet.getObject("data_hora", LocalDateTime.class));
            item.setTempoLimiteResolucao(Duration.ofHours(rSet.getLong("PRAZO")));
            // TODO: O tempo restante precisa ser setado para itens já fechados?
            setTempoRestante(item, rSet.getInt("prazo"), ZoneId.of(rSet.getString("TIME_ZONE_UNIDADE")));
            item.setQtdApontamentos(rSet.getInt("qt_apontamentos"));
            item.setStatus(ItemOrdemServico.Status.fromString(rSet.getString("status_item")));
            if (rSet.getString("nome_mecanico") != null) {
                mecanico = new Colaborador();
                mecanico.setCpf(rSet.getLong("cpf_mecanico"));
                mecanico.setNome(rSet.getString("nome_mecanico"));
                item.setMecanico(mecanico);
                item.setTempoRealizacaoConserto(Duration.ofMillis(rSet.getLong("tempo_realizacao")));
                item.setKmVeiculoFechamento(rSet.getLong("km_fechamento"));
                item.setDataHoraConserto(rSet.getObject("data_hora_conserto", LocalDateTime.class));
                item.setFeedbackResolucao(rSet.getString("feedback_conserto"));
            }
            itens.add(item);
        }
        return itens;
    }

    @Deprecated
    private static void setTempoRestante(@NotNull final ItemOrdemServico itemManutencao,
                                         final int prazoHoras,
                                         @NotNull final ZoneId unidadeZone) {
        final LocalDateTime dataMaxima = itemManutencao.getDataApontamento().plus(prazoHoras, ChronoUnit.HOURS);
        final LocalDateTime dataAtualUnidade = LocalDateTime.now(unidadeZone);
        itemManutencao.setTempoRestante(Duration.ofMillis(ChronoUnit.MILLIS.between(dataAtualUnidade, dataMaxima)));
    }

    @NotNull
    @Deprecated
    private static PerguntaRespostaChecklist createPergunta(@NotNull final ResultSet rSet) throws SQLException {
        final PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
        pergunta.setCodigo(rSet.getLong("COD_PERGUNTA"));
        pergunta.setOrdemExibicao(rSet.getInt("ORDEM_PERGUNTA"));
        pergunta.setPergunta(rSet.getString("PERGUNTA"));
        pergunta.setSingleChoice(rSet.getBoolean("SINGLE_CHOICE"));
        return pergunta;
    }

    @SuppressWarnings("Duplicates")
    @NotNull
    @Deprecated
    private static AlternativaChecklist createAlternativa(@NotNull final ResultSet rSet) throws SQLException {
        final AlternativaChecklist alternativa = new AlternativaChecklist();
        alternativa.setCodigo(rSet.getLong("COD_ALTERNATIVA"));
        alternativa.setAlternativa(rSet.getString("ALTERNATIVA"));
        alternativa.setPrioridade(PrioridadeAlternativa.fromString(rSet.getString("PRIORIDADE")));
        if (alternativa.getAlternativa().equals("Outros")) {
            alternativa.setTipo(AlternativaChecklist.TIPO_OUTROS);
            alternativa.setRespostaOutros(rSet.getString("RESPOSTA"));
        }
        return alternativa;
    }
}