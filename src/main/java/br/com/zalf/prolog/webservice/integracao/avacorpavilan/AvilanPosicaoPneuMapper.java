package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import javax.validation.constraints.NotNull;

/**
 * Classe responsável por mapear as posições utilizadas nos pneus pela Avilan para o equivalente no ProLog.
 */
public class AvilanPosicaoPneuMapper {
    private static final BiMap<String, Integer> BI_MAP_POSICOES;

    static {
        BI_MAP_POSICOES = new ImmutableBiMap.Builder<String, Integer>()
                .put("M", 111)
                .put("M", 111)
                .put("M", 111)
                .put("M", 111)
                .put("M", 111)
                .put("M", 111)
                .put("M", 111)
                .build();
    }


    public static int map(@NotNull final String posicao) {
        Preconditions.checkNotNull(posicao, "posicao não pode ser null!");
        Preconditions.checkArgument(BI_MAP_POSICOES.containsKey(posicao),
                "posicao " + posicao + " não mapeada para as posições utilizadas no ProLog");

        return BI_MAP_POSICOES.get(posicao);
    }

    public static String map(final int posicao) {
        Preconditions.checkArgument(BI_MAP_POSICOES.inverse().containsKey(posicao),
                "posicao " + posicao + " do ProLog não mapeada para as posições do cliente");

        return BI_MAP_POSICOES.inverse().get(posicao);
    }

}