package br.com.zalf.prolog.webservice.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.PerguntaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

/**
 * Created on 2019-09-19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class ChecklistModeloValidator {
    private static final String DEFAULT_DESCRICAO_TIPO_OUTROS_BACKEND = "Outros";
    private static final String DEFAULT_DESCRICAO_TIPO_OUTROS_WEB = "Outros (Opção padrão)";

    private ChecklistModeloValidator() {
        throw new IllegalStateException(ChecklistModeloValidator.class.getSimpleName() + " cannot be instantiated!");
    }

    static void validaModelo(@NotNull final ModeloChecklistInsercao modelo) {
        //noinspection unchecked
        internalValidate(modelo.getNome(), (List<PerguntaModeloChecklist>) (List<?>) modelo.getPerguntas());
    }

    static void validaModelo(@NotNull final ModeloChecklistEdicao modelo) {
        //noinspection unchecked
        internalValidate(modelo.getNome(), (List<PerguntaModeloChecklist>) (List<?>) modelo.getPerguntas());
    }

    private static void internalValidate(@NotNull final String nomeModelo,
                                         @NotNull final List<PerguntaModeloChecklist> perguntas) {
        // Verifica se temos perguntas no modelo.
        if (perguntas.isEmpty()) {
            throw new GenericException(String.format("O modelo '%s' não pode ser salvo sem perguntas", nomeModelo,
                                                     GenericException.NO_LOGS_INTO_SENTRY));
        }

        for (final PerguntaModeloChecklist p : perguntas) {
            // Verifica se a pergunta tem alternativas.
            if (p.getAlternativas().isEmpty()) {
                throw new GenericException(String.format("A pergunta '%s' está sem alternativas", p.getDescricao()),
                                           GenericException.NO_LOGS_INTO_SENTRY);
            }

            // Verifica se a pergunta tem uma, e apenas uma, alternativa do tipo_outros.
            final long totalTipoOutros = p
                    .getAlternativas()
                    .stream()
                    .filter(AlternativaModeloChecklist::isTipoOutros)
                    .count();
            if (totalTipoOutros != 1) {
                throw new GenericException(String.format(
                        "Toda pergunta deve ter uma alternativa '%s' com digitação livre",
                        DEFAULT_DESCRICAO_TIPO_OUTROS_WEB),
                                           GenericException.NO_LOGS_INTO_SENTRY);
            }

            // Verifica se a alternativa do tipo_outros contém a descrição padrão dessa alternativa aceita pelo ProLog.
            final boolean tipoOutrosSemDescricaoPadrao = p.getAlternativas()
                    .stream()
                    .anyMatch(a ->
                            a.isTipoOutros() && !a.getDescricao().equals(DEFAULT_DESCRICAO_TIPO_OUTROS_BACKEND));
            if (tipoOutrosSemDescricaoPadrao) {
                throw new GenericException(String.format(
                        "A alternativa que requer a digitação do usuário precisa ter o nome '%s'",
                        DEFAULT_DESCRICAO_TIPO_OUTROS_WEB),
                                           GenericException.NO_LOGS_INTO_SENTRY);
            }

            // Verifica se a alternativa do tipo_outros está por último na pergunta.
            p.getAlternativas().sort(Comparator.comparing(AlternativaModeloChecklist::getOrdemExibicao));
            if (!p.getAlternativas().get(p.getAlternativas().size() - 1).isTipoOutros()) {
                throw new GenericException(
                        String.format(
                                "A alternativa '%s' da pergunta '%s' deve ser a última",
                                DEFAULT_DESCRICAO_TIPO_OUTROS_WEB,
                                p.getDescricao()),
                        GenericException.NO_LOGS_INTO_SENTRY);
            }
        }
    }
}
