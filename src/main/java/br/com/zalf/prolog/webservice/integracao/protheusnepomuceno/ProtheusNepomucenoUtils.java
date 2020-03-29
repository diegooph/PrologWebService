package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.integracao.PosicaoPneuMapper;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.error.ProtheusNepomucenoException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
                throw new ProtheusNepomucenoException("O código auxiliar cadastrado não está dento dos padrões.\n" +
                        "Os padrões aceitos são: AA001:BB002 ou AA001:BB002,CC001:DD002.\n" +
                        "Para apenas um código, utilizar dois pontos (:).\n" +
                        "Para váris códigos, utilizar dois pontos (:) e vírgula (,)");
            }
        }
    }

    static void validatePosicoesMapeadasVeiculo(@NotNull final String codEstruturaVeiculo,
                                                @NotNull final List<String> posicoesPneusAplicados,
                                                @NotNull final PosicaoPneuMapper posicaoPneuMapper) {
        final List<String> posicaoNaoMapeadas =
                posicoesPneusAplicados
                        .stream()
                        .filter(posicao -> posicaoPneuMapper.mapToProLog(posicao) <= 0)
                        .collect(Collectors.toList());

        if (!posicaoNaoMapeadas.isEmpty()) {
            throw new ProtheusNepomucenoException("As posições " + posicaoNaoMapeadas + " não estão mapeadas para a " +
                    "estrutura " + codEstruturaVeiculo + ".\n" +
                    "Realize as configurações necessárias na tela Pneus -> Nomenclaturas");
        }
    }

    @NotNull
    static List<String> getCodAuxiliarTipoVeiculoAsArray(@NotNull final String codAuxiliar) {
        final String[] codigos = codAuxiliar.trim().split(DEFAULT_COD_AUXILIAR_TIPO_VEICULO_SEPARERTOR);
        return Arrays.asList(codigos);
    }
}