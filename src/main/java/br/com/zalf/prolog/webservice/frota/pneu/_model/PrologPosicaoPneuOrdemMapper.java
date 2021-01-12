package br.com.zalf.prolog.webservice.frota.pneu._model;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 8/8/17
 *
 * @author luiz (https://github.com/luizfp)
 */
public final class PrologPosicaoPneuOrdemMapper {
    @NotNull
    private static final ImmutableMap<Integer, Integer> MAP_POSICAO_ORDEM_PNEU;

    static {
        MAP_POSICAO_ORDEM_PNEU = new ImmutableMap.Builder<Integer, Integer>()
                .put(111, 1)
                .put(112, 2)
                .put(113, 3)
                .put(114, 4)
                .put(211, 5)
                .put(212, 6)
                .put(213, 7)
                .put(214, 8)
                .put(311, 9)
                .put(312, 10)
                .put(313, 11)
                .put(314, 12)
                .put(411, 13)
                .put(412, 14)
                .put(413, 15)
                .put(414, 16)
                .put(511, 17)
                .put(512, 18)
                .put(513, 19)
                .put(514, 20)
                .put(611, 21)
                .put(612, 22)
                .put(613, 23)
                .put(614, 24)
                .put(711, 25)
                .put(712, 26)
                .put(713, 27)
                .put(714, 28)
                .put(811, 29)
                .put(812, 30)
                .put(813, 31)
                .put(814, 32)
                .put(821, 33)
                .put(822, 34)
                .put(823, 35)
                .put(824, 36)
                .put(721, 37)
                .put(722, 38)
                .put(723, 39)
                .put(724, 40)
                .put(621, 41)
                .put(622, 42)
                .put(623, 43)
                .put(624, 44)
                .put(521, 45)
                .put(522, 46)
                .put(523, 47)
                .put(524, 48)
                .put(421, 49)
                .put(422, 50)
                .put(423, 51)
                .put(424, 52)
                .put(321, 53)
                .put(322, 54)
                .put(323, 55)
                .put(324, 56)
                .put(221, 57)
                .put(222, 58)
                .put(223, 59)
                .put(224, 60)
                .put(121, 61)
                .put(122, 62)
                .put(123, 63)
                .put(124, 64)
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

    private PrologPosicaoPneuOrdemMapper() {
        throw new IllegalStateException(
                PrologPosicaoPneuOrdemMapper.class.getSimpleName() + " cannot be instantiated!");
    }

    public static int fromPosicao(final int posicao) {
        return MAP_POSICAO_ORDEM_PNEU.get(posicao);
    }
}