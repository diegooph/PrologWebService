package br.com.zalf.prolog.webservice.entrega.mapa.validator;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.entrega.mapa._model.CelulaPlanilhaMapaErro;
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
    @Nullable
    private List<CelulaPlanilhaMapaErro> errors;

    public Optional<List<CelulaPlanilhaMapaErro>> findErrors(@NotNull final List<String[]> planilhaMapa,
                                                             @NotNull final RegrasValidacaoPlanilhaMapa mapa) {
        final Map<Integer, CampoPlanilhaMapa> campos = mapa.getCampos();
        for (int i = 0; i < planilhaMapa.size(); i++) {
            final String[] row = planilhaMapa.get(i);

            // Se a coluna "Data" estiver nula ou vazia, a linha toda é invalida, podemos pular.
            if (StringUtils.isNullOrEmpty(row[INDEX_COLUNA_DATA])) {
                continue;
            }

            final int rowIndex = i;
            campos.forEach((column, campo) -> {
                final String value = StringUtils.trimToNull(row[column]);
                if (StringUtils.isNullOrEmpty(value) && campo.isCampoObrigatorio()) {
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

    private void addError(@NotNull final CampoPlanilhaMapa campo,
                          @Nullable final String valorRecebido,
                          final int rowIndex) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(new CelulaPlanilhaMapaErro(
                String.format(
                        "A coluna \"%s\" na linha %d está com o valor incorreto!",
                        campo.getNomeCampoPlanilha(),
                        // + 1 para bater com a linha ao visualizar o arquivo no excel.
                        rowIndex + 1),
                StringUtils.isNullOrEmpty(valorRecebido) ? "valor não fornecido" : valorRecebido,
                campo.getExemploPreenchimento()));
    }
}
