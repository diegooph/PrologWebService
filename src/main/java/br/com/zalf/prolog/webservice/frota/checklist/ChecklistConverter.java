package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.frota.checklist.model.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.ChecklistFarol;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.FarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.FarolVeiculoDia;
import com.google.common.annotations.VisibleForTesting;
import org.jetbrains.annotations.NotNull;

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
    static FarolChecklist createFarolChecklist(@NotNull final ResultSet rSet) throws SQLException {
        final List<FarolVeiculoDia> farolVeiculoDias = new ArrayList<>();
        String placaAntiga = null, placaAtual = null;
        while (rSet.next()) {
            if (rSet.getLong("CODIGO_PERGUNTA") == 0) {
                // Verificamos se existem perguntas. Se não, podemos instanciar apenas as informações do Checklist
                farolVeiculoDias.add(createFarolVeiculoDiaSemItensCriticos(rSet));
            } else {
                // TODO - Tratar lógica de troca de placas aqui
            }

        }
        return new FarolChecklist(farolVeiculoDias);
    }

    @VisibleForTesting
    @NotNull
    public static Checklist createChecklist(@NotNull final ResultSet rSet) throws SQLException {
        final Checklist checklist = new Checklist();
        checklist.setCodigo(rSet.getLong("CODIGO"));
        checklist.setCodModelo(rSet.getLong("COD_CHECKLIST_MODELO"));
        checklist.setColaborador(createColaborador(rSet));
        checklist.setData(rSet.getObject("DATA_HORA", LocalDateTime.class));
        checklist.setPlacaVeiculo(rSet.getString("PLACA_VEICULO"));
        checklist.setTipo(rSet.getString("TIPO").charAt(0));
        checklist.setKmAtualVeiculo(rSet.getLong("KM_VEICULO"));
        checklist.setTempoRealizacaoCheckInMillis(rSet.getLong("TEMPO_REALIZACAO"));
        // TODO: Esse método atualmente não está funcionando, precisamos talvez adicionar esse retorno na query.
        setQtdOkOrNok(checklist);
        return checklist;
    }

    @VisibleForTesting
    @NotNull
    public static List<PerguntaRespostaChecklist> createPerguntasRespostasChecklist(@NotNull final ResultSet rSet)
            throws SQLException {
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
                alternativas = new ArrayList<>();
                pergunta = createPergunta(rSet);
                alternativa = createAlternativa(rSet);
                setRespostaAlternativa(alternativa, rSet);
                alternativas.add(alternativa);
                perguntas.add(pergunta);
            }
        }
        pergunta.setAlternativasResposta(alternativas);
        perguntas.add(pergunta);
        return perguntas;
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
                    rSet.getString("COLABORADOR_SAIDA"),
                    rSet.getObject("DATA_HORA_ULTIMO_CHECKLIST_SAIDA", LocalDateTime.class),
                    Checklist.TIPO_SAIDA);
        }
        ChecklistFarol checkRetorno = null;
        final Long codChecklistRetorno = rSet.getLong("COD_CHECKLIST_RETORNO");
        if (!rSet.wasNull()) {
            checkRetorno = new ChecklistFarol(
                    codChecklistRetorno,
                    rSet.getString("COLABORADOR_RETORNO"),
                    rSet.getObject("DATA_HORA_ULTIMO_CHECKLIST_RETORNO", LocalDateTime.class),
                    Checklist.TIPO_RETORNO);
        }
        return new FarolVeiculoDia(placa, checkSaida, checkRetorno, new ArrayList<>());
    }

    @NotNull
    private static Colaborador createColaborador(@NotNull final ResultSet rSet) throws SQLException {
        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(rSet.getLong("CPF_COLABORADOR"));
        colaborador.setNome(rSet.getString("NOME"));
        return colaborador;
    }

    private static void setQtdOkOrNok(Checklist checklist) throws SQLException {
//        final List<PerguntaRespostaChecklist> respostas = getPerguntasRespostas(checklist);
//        int qtdNok = 0;
//        for (PerguntaRespostaChecklist resposta : respostas) {
//            for (AlternativaChecklist alternativa : resposta.getAlternativasResposta()) {
//                if (alternativa.selected) {
//                    qtdNok++;
//                    break;
//                }
//            }
//        }
//        checklist.setQtdItensNok(qtdNok);
//        checklist.setQtdItensOk(respostas.size() - qtdNok);
    }

    @NotNull
    private static PerguntaRespostaChecklist createPergunta(ResultSet rSet) throws SQLException {
        final PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
        pergunta.setCodigo(rSet.getLong("COD_PERGUNTA"));
        pergunta.setOrdemExibicao(rSet.getInt("ORDEM_PERGUNTA"));
        pergunta.setPergunta(rSet.getString("PERGUNTA"));
        pergunta.setSingleChoice(rSet.getBoolean("SINGLE_CHOICE"));
        pergunta.setUrl(rSet.getString("URL_IMAGEM"));
        pergunta.setCodImagem(rSet.getLong("COD_IMAGEM"));
        pergunta.setPrioridade(rSet.getString("PRIORIDADE"));
        return pergunta;
    }

    @NotNull
    private static AlternativaChecklist createAlternativa(ResultSet rSet) throws SQLException {
        final AlternativaChecklist alternativa = new AlternativaChecklist();
        alternativa.setCodigo(rSet.getLong("COD_ALTERNATIVA"));
        alternativa.setAlternativa(rSet.getString("ALTERNATIVA"));
        if (alternativa.getAlternativa().equals("Outros")) {
            alternativa.setTipo(AlternativaChecklist.TIPO_OUTROS);
            alternativa.setRespostaOutros(rSet.getString("RESPOSTA"));
        }
        return alternativa;
    }

    // remonta as alternativas de uma Pergunta
    private static void setRespostaAlternativa(AlternativaChecklist alternativa, ResultSet rSet) throws SQLException {
        if (rSet.getString("RESPOSTA").equals("NOK")) {
            alternativa.selected = true;
        } else if (rSet.getString("RESPOSTA").equals("OK")) {
            alternativa.selected = false;
        } else {
            alternativa.selected = true;
            alternativa.tipo = AlternativaChecklist.TIPO_OUTROS;
            alternativa.respostaOutros = rSet.getString("RESPOSTA");
        }
    }
}