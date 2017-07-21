package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.frota.checklist.model.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Sulcos;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.ArrayOfMedidaPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.IncluirMedida2;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.MedidaPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.*;
import com.google.common.collect.MoreCollectors;
import com.sun.istack.internal.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by luiz on 18/07/17.
 */
final class AvaCorpAvilanConverter {

    private AvaCorpAvilanConverter() {
        throw new IllegalStateException(AvaCorpAvilanConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    static List<Veiculo> convert(@NotNull final ArrayOfVeiculo arrayOfVeiculo) {
        checkNotNull(arrayOfVeiculo, "arrayOfVeiculo não pode ser null!");

        final List<br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Veiculo> veiculosAvilan
                = arrayOfVeiculo.getVeiculo();

        final List<Veiculo> veiculos = new ArrayList<>();
        for (br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Veiculo v : veiculosAvilan) {
            Veiculo veiculo = new Veiculo();
            veiculo.setPlaca(v.getPlaca());
            veiculo.setKmAtual(v.getMarcador());
            veiculos.add(veiculo);
        }

        return veiculos;
    }

    static IncluirMedida2 convert(@NotNull final Afericao afericao) {
        checkNotNull(afericao, "afericao não pode ser null!");

        final IncluirMedida2 incluirMedida2 = new IncluirMedida2();

        // seta valores
        incluirMedida2.setVeiculo(afericao.getVeiculo().getPlaca());
        incluirMedida2.setTipoMarcador(1 /* hodômetro */);
        incluirMedida2.setMarcador(Math.toIntExact(afericao.getVeiculo().getKmAtual()));
        incluirMedida2.setDataMedida(createDatePattern(afericao.getDataHora()));
        // TODO: setar placas carreta?

        ArrayOfMedidaPneu medidas = new ArrayOfMedidaPneu();
        for (Pneu pneu : afericao.getVeiculo().getListPneus()) {
            final MedidaPneu medidaPneu = new MedidaPneu();
            medidaPneu.setCalibragem(pneu.getPressaoAtual());
            medidaPneu.setNumeroFogoPneu(String.valueOf(pneu.getCodigo()));
            medidaPneu.setTriangulo1PrimeiroSulco(pneu.getSulcosAtuais().getExterno());
            medidaPneu.setTriangulo1SegundoSulco(pneu.getSulcosAtuais().getCentralExterno());
            medidaPneu.setTriangulo1TerceiroSulco(pneu.getSulcosAtuais().getCentralInterno());
            medidaPneu.setTriangulo1QuartoSulco(pneu.getSulcosAtuais().getInterno());
            medidas.getMedidaPneu().add(medidaPneu);
        }
        incluirMedida2.setMedidas(medidas);

        return incluirMedida2;
    }

    static Map<ModeloChecklist, List<String>> convert(ArrayOfQuestionarioVeiculos arrayOfQuestionarioVeiculos) {
        checkNotNull(arrayOfQuestionarioVeiculos, "arrayOfQuestionarioVeiculos não pode ser null!");

        final Map<ModeloChecklist, List<String>> map = new HashMap<>();

        for (QuestionarioVeiculos questionarioVeiculos : arrayOfQuestionarioVeiculos.getQuestionarioVeiculos()) {
            // Cria modelo de checklist
            final ModeloChecklist modeloChecklist = new ModeloChecklist();
            final Questionario questionario = questionarioVeiculos.getQuestionario();
            modeloChecklist.setCodigo(Long.valueOf(questionario.getCodigoQuestionario()));
            modeloChecklist.setNome(questionario.getDescricao());

            // cria placa dos veículos
            final List<String> placasVeiculo = new ArrayList<>();
            questionarioVeiculos.getVeiculos().getVeiculo().forEach(veiculo -> placasVeiculo.add(veiculo.getPlaca()));

            map.put(modeloChecklist, placasVeiculo);
        }

        return map;
    }

    static NovoChecklistHolder convert(ArrayOfVeiculoQuestao veiculoQuestoes, String placaVeiculo) {
        checkNotNull(veiculoQuestoes, "veiculoQuestoes não pode ser null!");
        checkNotNull(placaVeiculo, "placaVeiculo não pode ser null!");

        final NovoChecklistHolder novoChecklistHolder = new NovoChecklistHolder();

        // Seta informações Veículo
        final Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(placaVeiculo);
        // Seta o km do veículo buscando algum veículo que possua a placa informada
        veiculo.setKmAtual(veiculoQuestoes
                .getVeiculoQuestao()
                .stream()
                .filter(veiculoQuestao -> veiculoQuestao.getVeiculo().getPlaca().equals(placaVeiculo))
                .collect(MoreCollectors.onlyElement())
                .getVeiculo()
                .getKmAtual());
        novoChecklistHolder.setVeiculo(veiculo);


        final List<PerguntaRespostaChecklist> perguntas = new ArrayList<>();
        for (VeiculoQuestao veiculoQuestao : veiculoQuestoes.getVeiculoQuestao()) {
            final ArrayOfQuestao arrayOfQuestao = veiculoQuestao.getQuestoes();
            for (Questao questao : arrayOfQuestao.getQuestao()) {
                final PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
                pergunta.setCodigo((long) questao.getCodigoAvaliacao());
                pergunta.setOrdemExibicao(questao.getSequenciaQuestao());
                pergunta.setPergunta(questao.getDescricao());
                final List<AlternativaChecklist> alternativas = new ArrayList<>();
                for (Resposta resposta : questao.getRespostas().getResposta()) {
                    final AlternativaChecklist alternativa = new AlternativaChecklist();
                    alternativa.setCodigo(resposta.getCodigoResposta());
                    alternativa.setAlternativa(resposta.getDescricao());
                    alternativas.add(alternativa);
                }
                pergunta.setAlternativasResposta(alternativas);
                perguntas.add(pergunta);
            }
        }
        novoChecklistHolder.setListPerguntas(perguntas);

        return novoChecklistHolder;
    }

    static RespostasAvaliacao convert(Checklist checklist) {
        checkNotNull(checklist, "checklist não pode ser null!");

        final RespostasAvaliacao respostasAvaliacao = new RespostasAvaliacao();
        respostasAvaliacao.setCpf(String.valueOf(checklist.getColaborador().getCpf()));
        respostasAvaliacao.setDtNascimento(createDatePattern(checklist.getColaborador().getDataNascimento()));
        respostasAvaliacao.setOdometro(Math.toIntExact(checklist.getKmAtualVeiculo()));
        respostasAvaliacao.setCodigoAvaliacao(Math.toIntExact(checklist.getCodModelo()));
        final ArrayOfRespostaAval arrayOfRespostaAval = new ArrayOfRespostaAval();
        for (PerguntaRespostaChecklist resposta : checklist.getListRespostas()) {
            final RespostaAval respostaAval = new RespostaAval();
            respostaAval.setSequenciaQuestao(resposta.getOrdemExibicao());
            for (AlternativaChecklist alternativa : resposta.getAlternativasResposta()) {
                respostaAval.setCodigoResposta(Math.toIntExact(alternativa.getCodigo()));
                if (alternativa.getTipo() == Alternativa.TIPO_OUTROS) {
                    respostaAval.setObservacao(alternativa.getRespostaOutros());
                }
            }
            arrayOfRespostaAval.getRespostaAval().add(respostaAval);
        }

        return respostasAvaliacao;
    }

    static List<Pneu> convert(ArrayOfPneu arrayOfPneu) {
        checkNotNull(arrayOfPneu, "arrayOfPneu não pode ser null!");
        final List<Pneu> pneus = new ArrayList<>();

        for (br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Pneu p : arrayOfPneu.getPneu()) {
            final Pneu pneu = new Pneu();
            pneu.setCodigo(1 /* TODO: alterar código  */);
            pneu.setPosicao(AvilanPosicaoPneuMapper.mapToProLog(p.getPosicao()));
            // A vida atual do pneu começa em 1 quando ele é novo, por isso somamos 1 ao total de recapagens
            // para ter a informação correta
            pneu.setVidaAtual(p.getQtdRecapagens() + 1);
            final Sulcos sulcos = new Sulcos();
            sulcos.setExterno(p.getSulco1());
            sulcos.setCentralExterno(p.getSulco2());
            sulcos.setCentralInterno(p.getSulco3());
            sulcos.setInterno(p.getSulco4());
            pneu.setSulcosAtuais(sulcos);
            pneus.add(pneu);
        }

        return pneus;
    }

    /**
     * Converte uma data para a representação em texto esperado no web service de integração da Avilan: yyyy-MM-dd.
     *
     * @param date um {@link Date}.
     * @return uma {@link String} represetando a data.
     */
    private static String createDatePattern(@NotNull final Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}