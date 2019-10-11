package br.com.zalf.prolog.webservice.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.PerguntaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2019-09-19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ChecklistModeloValidator {
    //TODO ENCONTRAR A MELHOR FORMA DE LIDAR COM A STRING PADRÃO DA ALTERNATIVA OUTROS
    private static final String DEFAULT_DESCRICAO_TIPO_OUTROS = "Outros";

    private ChecklistModeloValidator() {
        throw new IllegalStateException(ChecklistModeloValidator.class.getSimpleName() + " cannot be instantiated!");
    }

    public static void validaModelo(@NotNull final ModeloChecklistInsercao modelo) {
        if (modelo.getPerguntas().isEmpty()) {
            throw new GenericException(String.format("O modelo '%s' não pode ser salvo sem perguntas", modelo.getNome()));
        }

        //noinspection unchecked
        internalValidate((List<PerguntaModeloChecklist>) (List<?>) modelo.getPerguntas());
    }

    public static void validaModelo(@NotNull final ModeloChecklistEdicao modelo) {
        if (modelo.getPerguntas().isEmpty()) {
            throw new GenericException(String.format("O modelo '%s' não pode ser salvo sem perguntas", modelo.getNome()));
        }

        //noinspection unchecked
        internalValidate((List<PerguntaModeloChecklist>) (List<?>) modelo.getPerguntas());
    }

    private static void internalValidate(@NotNull final List<PerguntaModeloChecklist> perguntas) {
        for (final PerguntaModeloChecklist p : perguntas) {
            if (p.getAlternativas().isEmpty()) {
                throw new GenericException(String.format("A pergunta '%s' está sem alternativas", p.getDescricao()));
            }

            final long totalTipoOutros = p
                    .getAlternativas()
                    .stream()
                    .filter(AlternativaModeloChecklist::isTipoOutros)
                    .count();
            if (totalTipoOutros != 1) {
                throw new GenericException("Toda pergunta deve ter uma alternativa 'Outros' com digitação livre");
            }

            final boolean tipoOutrosSemDescricaoPadrao = p.getAlternativas()
                    .stream()
                    .anyMatch(a ->
                            a.isTipoOutros() && !a.getDescricao().equals(DEFAULT_DESCRICAO_TIPO_OUTROS));
            if (tipoOutrosSemDescricaoPadrao) {
                throw new GenericException(String.format(
                        "A alternativa que requer a digitação do usuário precisa ter o nome '%s'",
                        DEFAULT_DESCRICAO_TIPO_OUTROS));
            }
        }
    }
}
