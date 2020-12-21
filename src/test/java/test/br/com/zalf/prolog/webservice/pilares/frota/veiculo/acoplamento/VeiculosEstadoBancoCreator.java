package test.br.com.zalf.prolog.webservice.pilares.frota.veiculo.acoplamento;

import br.com.zalf.prolog.webservice.commons.util.ListUtils;
import br.com.zalf.prolog.webservice.commons.util.ObjectUtils;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator.AcomplamentoValidacaoHolder;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator.VeiculoEstadoAcoplamento;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 2020-12-18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class VeiculosEstadoBancoCreator {
    private List<Long> codProcessosAcoplamentosVinculados;
    private List<Long> codVeiculos;
    private List<Short> posicoesAcoplados;
    private List<Boolean> motorizados;
    private List<Boolean> possuemHubodometro;

    private VeiculosEstadoBancoCreator() {

    }

    @NotNull
    public static VeiculosEstadoBancoCreator builder() {
        return new VeiculosEstadoBancoCreator();
    }

    @NotNull
    public VeiculosEstadoBancoCreator codVeiculos(@NotNull final Long... codVeiculos) {
        this.codVeiculos = Arrays.asList(codVeiculos);
        return this;
    }

    @NotNull
    public VeiculosEstadoBancoCreator codProcessosAcoplamentosVinculados(
            @NotNull final Long... codProcessosAcoplamentosVinculados) {
        this.codProcessosAcoplamentosVinculados = Arrays.asList(codProcessosAcoplamentosVinculados);
        return this;
    }

    @NotNull
    public VeiculosEstadoBancoCreator posicoesAcoplados(@NotNull final Integer... posicoesAcoplados) {
        this.posicoesAcoplados = Arrays.stream(posicoesAcoplados)
                .map(integer -> {
                    if (integer != null) {
                        return integer.shortValue();
                    }
                    return (short) 0;
                })
                .collect(Collectors.toList());
        return this;
    }

    @NotNull
    public VeiculosEstadoBancoCreator motorizados(@NotNull final Boolean... motorizados) {
        this.motorizados = Arrays.asList(motorizados);
        return this;
    }

    @NotNull
    public VeiculosEstadoBancoCreator possuemHubodometro(@NotNull final Boolean... possuemHubodometro) {
        this.possuemHubodometro = Arrays.asList(possuemHubodometro);
        return this;
    }

    @NotNull
    public AcomplamentoValidacaoHolder build() {
        validaTodosNotNull();
        validaTodosMesmoTamanho();

        final List<VeiculoEstadoAcoplamento> veiculos = new ArrayList<>();
        for (int i = 0; i < codVeiculos.size(); i++) {
            veiculos.add(VeiculoEstadoAcoplamento.builder()
                                 .withCodVeiculo(codVeiculos.get(i))
                                 .withCodProcessoAcoplamentoVinculado(codProcessosAcoplamentosVinculados.get(i))
                                 .withPosicaoAcoplado(posicoesAcoplados.get(i))
                                 .withMotorizado(motorizados.get(i))
                                 .withPossuiHubodometro(possuemHubodometro.get(i))
                                 .build());
        }

        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        veiculos.forEach(veiculo -> veiculosEstadoAcoplamento.put(veiculo.getCodVeiculo(), veiculo));
        return new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);
    }

    private void validaTodosNotNull() {
        if (!ObjectUtils.allNotNull(codProcessosAcoplamentosVinculados,
                                    codVeiculos,
                                    posicoesAcoplados,
                                    motorizados,
                                    possuemHubodometro)) {
            throw new IllegalArgumentException("Todos os atributos precisam ser diferentes de null.");
        }
    }

    private void validaTodosMesmoTamanho() {
        if (!ListUtils.allSameSize(codVeiculos,
                                   codProcessosAcoplamentosVinculados,
                                   posicoesAcoplados,
                                   motorizados,
                                   possuemHubodometro)) {
            throw new IllegalArgumentException("Todas as listas precisam ser do mesmo tamanho.");
        }
    }
}
