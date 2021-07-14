package br.com.zalf.prolog.webservice.v3.fleet.transferenciaveiculo;

import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.KmProcessoAtualizavel;
import br.com.zalf.prolog.webservice.v3.fleet.transferenciaveiculo._model.TransferenciaVeiculoInformacaoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.transferenciaveiculo._model.TransferenciaVeiculoProcessoEntity;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransferenciaVeiculoService implements KmProcessoAtualizavel {
    @NotNull
    private final TransferenciaVeiculoDao transferenciaVeiculoDao;
    @NotNull
    private final TransferenciaVeiculoInformacoesDao transferenciaVeiculoInformacoesDao;

    @NotNull
    @Override
    public EntityKmColetado getEntityKmColetado(@NotNull final Long entityId,
                                                @NotNull final Long codVeiculo) {
        final TransferenciaVeiculoProcessoEntity processo = getByCodigo(entityId);
        final Optional<TransferenciaVeiculoInformacaoEntity> optional =
                processo.getInformacoesTransferenciaVeiculo(codVeiculo);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new IllegalStateException(String.format(
                    "O veículo %d não foi transferido no processo %d.",
                    codVeiculo,
                    entityId));
        }
    }

    @Override
    public void updateKmColetadoProcesso(@NotNull final Long codProcesso,
                                         @NotNull final Long codVeiculo,
                                         final long novoKm) {
        updateKmColetadoMomentoTransferencia(codProcesso, codVeiculo, novoKm);
    }

    @NotNull
    public TransferenciaVeiculoProcessoEntity getByCodigo(@NotNull final Long codigo) {
        return transferenciaVeiculoDao.getOne(codigo);
    }

    public void updateKmColetadoMomentoTransferencia(@NotNull final Long codProcessoTransferencia,
                                                     @NotNull final Long codVeiculo,
                                                     final long novoKm) {
        final TransferenciaVeiculoProcessoEntity entity = getByCodigo(codProcessoTransferencia);
        final Optional<TransferenciaVeiculoInformacaoEntity> informacoesTransferenciaVeiculo =
                entity.getInformacoesTransferenciaVeiculo(codVeiculo);
        if (informacoesTransferenciaVeiculo.isPresent()) {
            final TransferenciaVeiculoInformacaoEntity infoVeiculo = informacoesTransferenciaVeiculo.get();
            final TransferenciaVeiculoInformacaoEntity updateEntity = infoVeiculo
                    .toBuilder()
                    .withKmColetadoVeiculoMomentoTransferencia(novoKm)
                    .build();
            transferenciaVeiculoInformacoesDao.save(updateEntity);
        } else {
            throw new IllegalStateException(
                    String.format("O veículo %d não está presente no processo de transferência %d.",
                                  codVeiculo,
                                  codProcessoTransferencia));
        }
    }
}
