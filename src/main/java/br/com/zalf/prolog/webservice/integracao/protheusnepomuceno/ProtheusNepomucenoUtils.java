package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.ProtheusNepomucenoConstants.DEFAULT_CODIGOS_SEPARERTOR;
import static br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.ProtheusNepomucenoConstants.DEFAULT_COD_AUXILIAR_TIPO_VEICULO_SEPARERTOR;

/**
 * Created on 2020-03-23
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class ProtheusNepomucenoUtils {
    private ProtheusNepomucenoUtils() {
        throw new IllegalStateException(ProtheusNepomucenoUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    static void validateCodAuxiliarTipoVeiculo(@NotNull final String codAuxiliar) {
        final String[] codigos = codAuxiliar.trim().split(DEFAULT_COD_AUXILIAR_TIPO_VEICULO_SEPARERTOR);
        for (final String s : codigos) {
            final String[] codigo = s.trim().split(DEFAULT_CODIGOS_SEPARERTOR);
            // Ao fazer o split deveremos ter duas String, que são o código da Familia e o código do Modelo.
            if (codigo.length != 2) {
                throw new GenericException("O código auxiliar cadastrado não está dento dos padrões");
            }
        }
    }

    @NotNull
    static List<String> getCodAuxiliarTipoVeiculoAsArray(@NotNull final String codAuxiliar) {
        final String[] codigos = codAuxiliar.trim().split(DEFAULT_COD_AUXILIAR_TIPO_VEICULO_SEPARERTOR);
        return Arrays.asList(codigos);
    }
}
