package br.com.zalf.prolog.webservice.entrega.mapa.validator;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.entrega.mapa._model.CelulaPlanilhaMapaErro;
import com.google.common.annotations.VisibleForTesting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created on 2020-05-22
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PlanilhaMapaValidator {
    private static final int INDEX_COLUNA_DATA = 0;
    @NotNull
    private final RegrasValidacaoPlanilhaMapa regrasValidacao;
    @Nullable
    private List<CelulaPlanilhaMapaErro> errors;

    public PlanilhaMapaValidator(@NotNull final RegrasValidacaoPlanilhaMapa regrasValidacao) {
        this.regrasValidacao = regrasValidacao;
    }

    @NotNull
    public Optional<List<CelulaPlanilhaMapaErro>> findErrors(@NotNull final List<String[]> planilhaMapa) {
        clearErrors();

        final Map<Integer, ColunaPlanilhaMapa> campos = regrasValidacao.getColunas();
        for (int i = 0; i < planilhaMapa.size(); i++) {
            final String[] row = planilhaMapa.get(i);

            // Se a coluna "Data" estiver nula ou vazia, a linha toda é invalida, podemos pular.
            if (StringUtils.isNullOrEmpty(StringUtils.trimToNull(row[INDEX_COLUNA_DATA]))) {
                continue;
            }

            final int rowIndex = i;
            campos.forEach((column, campo) -> {
                final String value = StringUtils.trimToNull(row[column]);
                if (StringUtils.isNullOrEmpty(value) && campo.isColunaObrigatoria()) {
                    // Está vazio ou nulo e é obrigatório.
                    addError(campo, value, rowIndex);
                } else if (!StringUtils.isNullOrEmpty(value)) {
                    // Não está vazio, então validamos independente de ser obrigatório ou não.
                    if (!value.matches(campo.getRegexValidacaoPadraoPreenchimento())) {
                        addError(campo, value, rowIndex);
                    }
                }
            });
        }

        return Optional.ofNullable(errors);
    }

    @VisibleForTesting
    public int getTotalColunasObrigatorias() {
        return (int) regrasValidacao
                .getColunas()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().isColunaObrigatoria())
                .count();
    }

    @VisibleForTesting
    public int getTotalColunasQueNaoSaoTexto() {
        return (int) regrasValidacao
                .getColunas()
                .entrySet()
                .stream()
                .filter(entry -> !entry.getValue().getPadraoPreenchimentoColuna().equals(PadraoPrenchimentoCampo.TEXTO))
                .count();
    }

    @VisibleForTesting
    public boolean isColunaTipoTexto(final int indexColuna) {
        return regrasValidacao
                .getColunas()
                .get(indexColuna)
                .getPadraoPreenchimentoColuna()
                .equals(PadraoPrenchimentoCampo.TEXTO);
    }

    private void addError(@NotNull final ColunaPlanilhaMapa campo,
                          @Nullable final String valorRecebido,
                          final int rowIndex) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(new CelulaPlanilhaMapaErro(
                String.format(
                        "A coluna \"%s\" na linha %d está com o valor incorreto!",
                        campo.getNomeColunaPlanilha(),
                        // + 2 para bater com a linha ao visualizar o arquivo no excel.
                        // Somamos 1 por conta do arquivo ter um header de uma linha.
                        // Somamos mais 1 por conta do java começar o index no 0.
                        rowIndex + 2),
                StringUtils.isNullOrEmpty(valorRecebido) ? "valor não fornecido" : valorRecebido,
                campo.getExemploPreenchimento()));
    }

    private void clearErrors() {
        if (errors != null) {
            errors.clear();
        }
    }
}
