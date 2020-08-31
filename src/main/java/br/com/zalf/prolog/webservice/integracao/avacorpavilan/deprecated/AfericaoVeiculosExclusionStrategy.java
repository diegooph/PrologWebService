package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.ArrayOfVeiculo;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nonnull;

/**
 * Essa classe exclui veículos que não são aferíveis para que os mesmos não apareçam no cronograma no aplicativo.
 *
 * Created on 26/10/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class AfericaoVeiculosExclusionStrategy {
    private static final ImmutableSet<String> CODIGO_TIPOS_VEICULOS_REMOVER;

    static {
        CODIGO_TIPOS_VEICULOS_REMOVER = ImmutableSet.of(
                "EMP" /* EMPILHADEIRA */,
                "EMPPP" /* EMPILHADEIRA */,
                "EMPR" /* EMPILHADEIRA EIXO DIANTEIRO COM RODADO DUPLO */,
                "ME" /* MÁQUINAS/EQUIPAMENTOS */,
                "PAL" /* PALETEIRAS */,
                "112" /* TESTE */,
                "M" /* MOTO */);
    }

    AfericaoVeiculosExclusionStrategy() {

    }

    @Nonnull
    ArrayOfVeiculo applyStrategy(@Nonnull final ArrayOfVeiculo veiculos) {
        Preconditions.checkNotNull(veiculos, "veiculos não pode ser null!");

        veiculos.getVeiculo().removeIf(v -> CODIGO_TIPOS_VEICULOS_REMOVER.contains(v.getTipo().getCodigo()));

        return veiculos;
    }
}