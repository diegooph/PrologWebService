package br.com.zalf.prolog.webservice.v3.frota.kmprocessos;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.veiculo.v3.VeiculoV3Service;
import br.com.zalf.prolog.webservice.frota.veiculo.v3._model.VeiculoEntity;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.*;
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
    private final VeiculoV3Service veiculoService;

    @NotNull
    public AlteracaoKmResponse updateKmProcesso(@NotNull final KmProcessoAtualizavel kmProcessoAtualizavel,
                                                @NotNull final AlteracaoKmProcesso processo) {
        final EntityKmColetado entity = kmProcessoAtualizavel.getEntityKmColetado(processo.getCodProcesso(),
                                                                                  processo.getCodVeiculo());
        final VeiculoKmColetado veiculoKmColetado = entity.getVeiculoKmColetado();
        applyValidations(processo.getCodEmpresa(),
                         processo.getCodVeiculo(),
                         veiculoKmColetado.getCodVeiculo());
        kmProcessoAtualizavel.updateKmColetadoProcesso(processo.getCodProcesso(),
                                                       processo.getCodVeiculo(),
                                                       processo.getNovoKm());
        return AlteracaoKmResponse.of(veiculoKmColetado.getKmColetado());
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
