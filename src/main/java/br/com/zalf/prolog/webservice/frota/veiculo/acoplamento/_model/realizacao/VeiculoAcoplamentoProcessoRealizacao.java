package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao;

import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator.AcoplamentoRecebido;
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
    @NotNull
    private final Long codUnidade;
    @Nullable
    private final String observacao;
    @NotNull
    @Size(min = 2, max = 6)
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

    @NotNull
    public List<AcoplamentoRecebido> getAcoplamentosRecebidos() {
        return acoesRealizadas
                .stream()
                .filter(VeiculoAcoplamentoAcaoRealizada::foiAcopladoOuMantidoNaComposicao)
                .map(acao -> new AcoplamentoRecebido(
                        //TODO ver com o allan se esse código é o do acoplamento atual.
                        codProcessoAcoplamentoEditado,
                        codUnidade,
                        acao.getCodVeiculo(),
                        acao.getPosicaoAcaoRealizada(),
                        acao.getCodDiagramaVeiculo(),
                        acao.getMotorizado()))
                .collect(Collectors.toList());
    }
}
