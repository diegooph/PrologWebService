package br.com.zalf.prolog.webservice.commons.util;

import com.google.common.collect.ImmutableMap;

/**
 * Created by luiz on 8/8/17.
 */

public final class ProLogPosicaoPneuOrdemMapper {

    private static final ImmutableMap<Integer, Integer> MAP_POSICAO_ORDEM_PNEU;

    static  {
        MAP_POSICAO_ORDEM_PNEU = new ImmutableMap.Builder<Integer, Integer>()
                .put(111, 1)
                .put(211, 2)
                .put(212, 3)
                .put(311, 4)
                .put(312, 5)
                .put(411, 6)
                .put(412, 7)
                .put(421, 8)
                .put(422, 9)
                .put(321, 10)
                .put(322, 11)
                .put(221, 12)
                .put(222, 13)
                .put(121, 14)
                .put(900, 90)
                .put(901, 91)
                .put(902, 92)
                .put(903, 93)
                .put(904, 94)
                .put(905, 95)
                .put(906, 96)
                .put(907, 97)
                .put(908, 98)
                .build();
    }

    private ProLogPosicaoPneuOrdemMapper() {
        throw new IllegalStateException(ProLogPosicaoPneuOrdemMapper.class.getSimpleName() + " cannot be instantiated!");
    }


    public static int fromPosicao(final int posicao) {
        return MAP_POSICAO_ORDEM_PNEU.get(posicao);
    }
}