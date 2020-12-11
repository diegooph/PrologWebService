package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao;

import lombok.Data;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created on 2020-11-10
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class VeiculoAcoplamentoProcessoRealizacao {
    /**
     * O maior acoplamento possível pode conter 6 veículos, logo, a realização deve contemplar um acoplamento total
     * e uma remoção total ao mesmo tempo.
     */
    private static final int MAXIMO_ACOES_POR_PROCESSO = 12;
    /**
     * O menor acoplamento possível é composto por dois veículos. Por mais que o usuário edite apenas um, o outro
     * será enviado também.
     */
    private static final int MINIMO_ACOES_POR_PROCESSO = 2;

    @NotNull
    private final Long codUnidade;
    @Nullable
    private final String observacao;
    @NotNull
    @Size(min = MINIMO_ACOES_POR_PROCESSO, max = MAXIMO_ACOES_POR_PROCESSO)
    private final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas;
    @Nullable
    private final Long codProcessoAcoplamentoEditado;

    @NotNull
    public Optional<Long> estaEditandoProcessoAcoplamento() {
        return Optional.ofNullable(codProcessoAcoplamentoEditado);
    }

    @NotNull
    public Optional<List<VeiculoAcopladoMantido>> getVeiculosAcopladosOuMantidos(
            @NotNull final Long codProcessoAcoplamentoRealizado) {
        final List<VeiculoAcopladoMantido> acopladosOuMantidos = acoesRealizadas
                .stream()
                .filter(VeiculoAcoplamentoAcaoRealizada::foiAcopladoOuMantidoNaComposicao)
                .map(acao -> new VeiculoAcopladoMantido(
                        codProcessoAcoplamentoRealizado,
                        codUnidade,
                        acao.getCodVeiculo(),
                        acao.getCodDiagramaVeiculo(),
                        acao.getMotorizado(),
                        acao.getPosicaoAcaoRealizada(),
                        acao.getKmColetado()))
                .collect(Collectors.toList());
        return acopladosOuMantidos.isEmpty()
                ? Optional.empty()
                : Optional.of(acopladosOuMantidos);
    }
}
