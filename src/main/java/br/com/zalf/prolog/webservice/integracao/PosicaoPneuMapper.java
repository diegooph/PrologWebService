package br.com.zalf.prolog.webservice.integracao;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;

import javax.annotation.Nonnull;

/**
 * Created on 16/10/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PosicaoPneuMapper {
    @Nonnull
    private final BiMap<String, Integer> posicoesPneuMapper;

    public PosicaoPneuMapper(@Nonnull BiMap<String, Integer> posicoesPneuMapper) {
        this.posicoesPneuMapper = posicoesPneuMapper;
    }

    public int mapToProLog(@Nonnull final String posicao) {
        Preconditions.checkNotNull(posicao, "posicao não pode ser null!");
        Preconditions.checkArgument(posicoesPneuMapper.containsKey(posicao),
                "posicao " + posicao + " não mapeada para as posições utilizadas no ProLog");

        return posicoesPneuMapper.get(posicao);
    }

    @Nonnull
    public String mapToClient(final int posicao) {
        final BiMap<Integer, String> inverseMap = posicoesPneuMapper.inverse();
        Preconditions.checkArgument(inverseMap.containsKey(posicao),
                "posicao " + posicao + " não mapeada para as posições utilizadas no ProLog");

        return inverseMap.get(posicao);
    }
}