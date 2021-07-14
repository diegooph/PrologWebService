package br.com.zalf.prolog.webservice.v3.fleet.kmprocessos;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.*;
import br.com.zalf.prolog.webservice.v3.fleet.veiculo.VeiculoService;
import br.com.zalf.prolog.webservice.v3.fleet.veiculo._model.VeiculoEntity;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created on 2021-03-30
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public final class AlteradorKmProcesso {
    @NotNull
    private final VeiculoService veiculoService;

    @NotNull
    public AlteracaoKmResponse updateKmProcesso(@NotNull final KmProcessoAtualizavel kmProcessoAtualizavel,
                                                @NotNull final AlteracaoKmProcesso processo) {
        final EntityKmColetado entity = kmProcessoAtualizavel.getEntityKmColetado(processo.getCodProcesso(),
                                                                                  processo.getCodVeiculo());
        final VeiculoKmColetado veiculoKmColetado = entity.getVeiculoKmColetado();
        applyValidations(processo.getCodEmpresa(),
                         processo.getCodVeiculo(),
                         veiculoKmColetado.getCodVeiculo());
        if (veiculoKmColetado.getKmColetado() != processo.getNovoKm()) {
            kmProcessoAtualizavel.updateKmColetadoProcesso(processo.getCodProcesso(),
                                                           processo.getCodVeiculo(),
                                                           processo.getNovoKm());
            return AlteracaoKmResponse.of(veiculoKmColetado.getKmColetado(), true);
        }

        return AlteracaoKmResponse.of(veiculoKmColetado.getKmColetado(), false);
    }

    private void applyValidations(@NotNull final Long codEmpresaRecebido,
                                  @NotNull final Long codVeiculoRecebido,
                                  @NotNull final Long codVeiculoBanco) {
        if (!codVeiculoRecebido.equals(codVeiculoBanco)) {
            fail();
        }
        final VeiculoEntity veiculo = veiculoService.getByCodigo(codVeiculoBanco);
        // Garantindo que a empresa do veículo é a mesma recebida já garantimos que o processo editado é da empresa
        // em questão.
        if (!codEmpresaRecebido.equals(veiculo.getCodEmpresa())) {
            fail();
        }
    }

    private void fail() {
        throw new GenericException("Só é possível alterar o KM de veículos e processos da sua empresa!");
    }
}
