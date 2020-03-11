package br.com.zalf.prolog.webservice.gente.quiz.modelo;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.AlternativaEscolhaQuiz;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.PerguntaQuiz;
import br.com.zalf.prolog.webservice.gente.treinamento.model.Treinamento;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 18/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class QuizModeloValidator {

    private QuizModeloValidator() {
        throw new IllegalStateException(QuizModeloValidator.class.getSimpleName() + " cannot be instantiated!");
    }

    static void validaQuizModelo(@Nullable final Long codUnidade,
                                 @Nullable final ModeloQuiz modeloQuiz) throws GenericException {
        try {
            if (modeloQuiz == null) {
                throw new GenericException("Erro ao criar quiz, tente enviar novamente" +
                        "\nSe o problema persistir, contate nosso suporte");
            }
            if (codUnidade == null) {
                throw new GenericException("Você precisa selecionar a unidade na qual o quiz vai estar disponível");
            }
            validaNomeModelo(modeloQuiz.getNome());
            validaPeriodoModeloQuizDisponivel(modeloQuiz.getDataHoraAbertura(), modeloQuiz.getDataHoraFechamento());
            validaPorcentagemAprovacao(modeloQuiz.getPorcentagemAprovacao());
            validaMaterialApoio(modeloQuiz.getMaterialApoio());
            validaCargosLiberados(modeloQuiz.getFuncoesLiberadas());
            validaPerguntas(modeloQuiz.getPerguntas());
        } catch (final GenericException e) {
            throw e;
        } catch (final Throwable t) {
            throw new GenericException("Erro ao validar os parâmetros do quiz, tente enviar novamente" +
                    "\nSe o problema persistir, contate nosso suporte", t.getMessage());
        }
    }

    private static void validaNomeModelo(@Nullable final String nomeModelo) throws Throwable {
        if (nomeModelo == null) {
            throw new GenericException("Você precisa fornecer o nome do quiz");
        } else {
            if (nomeModelo.trim().isEmpty()) {
                throw new GenericException("O nome do quiz não pode estar em branco");
            }
        }
    }

    private static void validaPeriodoModeloQuizDisponivel(@Nullable final LocalDateTime dataHoraAbertura,
                                                          @Nullable final LocalDateTime dataHoraFechamento) throws Throwable {
        if (dataHoraAbertura == null) {
            throw new GenericException("Você precisa fornecer a data de abertura do quiz");
        }
        if (dataHoraFechamento == null) {
            throw new GenericException("Você precisa fornecer a data de fechamento do quiz");
        }

        if (!dataHoraFechamento.isAfter(dataHoraAbertura)) {
            throw new GenericException("A data de fechamento do quiz precisa ser após a data de abertura");
        }
    }

    private static void validaCargosLiberados(@Nullable final List<Cargo> cargosLiberados) throws Throwable {
        if (cargosLiberados == null || cargosLiberados.isEmpty()) {
            throw new GenericException("Você precisa selecionar os cargos que terão acesso a este quiz");
        }

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < cargosLiberados.size(); i++) {
            final Cargo cargo = cargosLiberados.get(i);
            if (cargo == null || cargo.getCodigo() == null) {
                throw new GenericException("Erro ao processar os cargos com acesso a este quiz, tente enviar novamente" +
                        "\nSe o problema persistir, contate nosso suporte");
            }
        }
    }

    private static void validaMaterialApoio(@Nullable final Treinamento treinamento) throws Throwable {
        if (treinamento != null && treinamento.getCodigo() == null) {
            throw new GenericException("Selecione um material de apoio válido");
        }
    }

    private static void validaPerguntas(@Nullable final List<PerguntaQuiz> perguntas) throws Throwable {
        if (perguntas == null || perguntas.isEmpty()) {
            throw new GenericException("Você precisa fornecer as perguntas para este quiz");
        } else {
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < perguntas.size(); i++) {
                final PerguntaQuiz perguntaQuiz = perguntas.get(i);
                if (perguntaQuiz == null) {
                    throw new GenericException("Erro ao processar as pergutnas deste quiz, tente enviar novamente" +
                            "\nSe o problema persistir, contate nosso suporte");
                } else {
                    if (perguntaQuiz.getPergunta() == null || perguntaQuiz.getPergunta().trim().isEmpty()) {
                        throw new GenericException("Você precisa fornecer um título para cada pergunta");
                    }
                    if (perguntaQuiz.getTipo() == null) {
                        throw new GenericException(String.format("Você precisa selecionar o tipo de resposta para a pergunta '%s'",
                                perguntaQuiz.getPergunta()));
                    } else {
                        // O tipo TIPO_ORDERING não é suportado no momento
                        if (!perguntaQuiz.getTipo().equals(PerguntaQuiz.TIPO_SINGLE_CHOICE)
                                && !perguntaQuiz.getTipo().equals(PerguntaQuiz.TIPO_MULTIPLE_CHOICE)) {
                            throw new GenericException(String.format("Selecione um tipo válido de resposta para a pergunta '%s'",
                                    perguntaQuiz.getPergunta()));
                        }
                    }
                    validaAlternativasPergunta(perguntaQuiz);
                }
            }
        }
    }

    private static void validaAlternativasPergunta(@NotNull final PerguntaQuiz perguntaQuiz) throws GenericException {
        final List<Alternativa> alternativas = perguntaQuiz.getAlternativas();
        final boolean isSingleChoice = perguntaQuiz.getTipo().equals(PerguntaQuiz.TIPO_SINGLE_CHOICE);
        if (alternativas == null || alternativas.isEmpty()) {
            throw new GenericException(String.format("Você precisa adicionar opções de respostas para a pergunta '%s'",
                    perguntaQuiz.getPergunta()));
        } else {
            boolean possuiAlternativaCorreta = false;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < alternativas.size(); i++) {
                final Alternativa alternativa = alternativas.get(i);
                if (alternativa == null) {
                    throw new GenericException(String.format("Erro ao criar as opções de resposta para a pergunta '%s', tente enviar novamente" +
                            "\nSe o problema persistir, contate nosso suporte", perguntaQuiz.getPergunta()));
                } else {
                    if (alternativa.getAlternativa() == null || alternativa.getAlternativa().trim().isEmpty()) {
                        throw new GenericException(String.format("A pergunta '%s' não pode possuir opções de resposta em branco",
                                perguntaQuiz.getPergunta()));
                    } else {
                        // Por enquanto não suportamos o TIPO_ORDERING.
                        final AlternativaEscolhaQuiz escolha = (AlternativaEscolhaQuiz) alternativa;
                        if (isSingleChoice && possuiAlternativaCorreta && escolha.isCorreta()) {
                            throw new GenericException(String.format("O tipo da pergunta '%s' é de " +
                                    "escolha única e não pode possuir mais de uma opção de resposta marcada como correta",
                                    perguntaQuiz.getPergunta()));
                        }

                        // Garante que só alteraremos esse atributo uma vez para cada pergunta
                        if (!possuiAlternativaCorreta) {
                            possuiAlternativaCorreta = escolha.isCorreta();
                        }

                        // Verifica o caso de nenhuma alternativa estar marcada
                        if (!possuiAlternativaCorreta && i == alternativas.size() - 1) {
                            throw new GenericException(String.format("A pergunta '%s' deve possuir ao menos uma " +
                                            "opção de resposta marcada como correta", perguntaQuiz.getPergunta()));
                        }
                    }
                }
            }
        }
    }

    private static void validaPorcentagemAprovacao(final double porcentagemAprovacao) throws Throwable {
        if (porcentagemAprovacao < 0.01 || porcentagemAprovacao > 1) {
            throw new GenericException("Insira uma porcentagem (%) de aprovação entre 1 a 100");
        }
    }
}