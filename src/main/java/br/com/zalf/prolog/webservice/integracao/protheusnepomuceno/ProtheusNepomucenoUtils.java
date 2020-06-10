package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.error.ProtheusNepomucenoException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.ProtheusNepomucenoConstants.DEFAULT_CODIGOS_SEPARATOR;
import static br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.ProtheusNepomucenoConstants.DEFAULT_COD_AUXILIAR_TIPO_VEICULO_SEPARATOR;

/**
 * Created on 2020-03-23
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ProtheusNepomucenoUtils {
    private ProtheusNepomucenoUtils() {
        throw new IllegalStateException(ProtheusNepomucenoUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    static void validateCodAuxiliarTipoVeiculo(@NotNull final String codAuxiliar) {
        final String[] codigos = codAuxiliar.trim().split(DEFAULT_COD_AUXILIAR_TIPO_VEICULO_SEPARATOR);
        for (final String s : codigos) {
            final String[] codigo = s.trim().split(DEFAULT_CODIGOS_SEPARATOR);
            // Ao fazer o split deveremos ter duas String, que são o código da Familia e o código do Modelo.
            if (codigo.length != 2) {
                throw new ProtheusNepomucenoException("O código auxiliar cadastrado não está dento dos padrões.\n" +
                        "Os padrões aceitos são: AA001:BB002 ou AA001:BB002,CC001:DD002.\n" +
                        "Para apenas um código, utilizar dois pontos (:).\n" +
                        "Para vários códigos, utilizar dois pontos (:) e vírgula (,).");
            }
        }
    }

    public static void validatePosicoesMapeadasVeiculo(
            @NotNull final String codEstruturaVeiculo,
            @NotNull final List<String> posicoesPneusAplicados,
            @NotNull final ProtheusNepomucenoPosicaoPneuMapper posicaoPneuMapper) {
        final List<String> posicaoNaoMapeadas =
                posicoesPneusAplicados
                        .stream()
                        .filter(posicao -> posicaoPneuMapper.mapPosicaoToProlog(posicao) == null)
                        .collect(Collectors.toList());

        if (!posicaoNaoMapeadas.isEmpty()) {
            throw new ProtheusNepomucenoException("As posições " + posicaoNaoMapeadas + " não estão mapeadas para a " +
                    "estrutura " + codEstruturaVeiculo + ".\n" +
                    "Realize as configurações necessárias na tela Pneus -> Nomenclaturas.");
        }

        final List<Integer> posicoesMapeadas =
                posicoesPneusAplicados
                        .stream()
                        .filter(posicaoAplica -> posicaoPneuMapper.mapPosicaoToProlog(posicaoAplica) != null)
                        .map(posicaoPneuMapper::mapPosicaoToProlog)
                        .collect(Collectors.toList());

        final List<Integer> posicoesDuplicadas = posicoesMapeadas
                .stream()
                .filter(posicao -> Collections.frequency(posicoesMapeadas, posicao) > 1)
                .collect(Collectors.toList());
        if (!posicoesDuplicadas.isEmpty()) {
            throw new ProtheusNepomucenoException("As posições " + posicoesDuplicadas + " estão duplicadas para a " +
                    "estrutura " + codEstruturaVeiculo + ".\n" +
                    "Realize as configurações necessárias na tela Pneus -> Nomenclaturas.");
        }
    }

    @NotNull
    static List<String> getCodAuxiliarTipoVeiculoAsArray(@NotNull final String codAuxiliar) {
        final String[] codigos = codAuxiliar.trim().split(DEFAULT_COD_AUXILIAR_TIPO_VEICULO_SEPARATOR);
        return Arrays.asList(codigos);
    }
}
