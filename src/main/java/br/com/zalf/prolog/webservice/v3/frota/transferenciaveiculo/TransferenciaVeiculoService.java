package br.com.zalf.prolog.webservice.v3.frota.transferenciaveiculo;

import br.com.zalf.prolog.webservice.v3.frota.transferenciaveiculo._model.TransferenciaVeiculoInformacaoEntity;
import br.com.zalf.prolog.webservice.v3.frota.transferenciaveiculo._model.TransferenciaVeiculoProcessoEntity;
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
public class TransferenciaVeiculoService {

    @NotNull
    private final TransferenciaVeiculoDao transferenciaVeiculoDao;
    @NotNull
    private final TransferenciaVeiculoInformacoesDao transferenciaVeiculoInformacoesDao;

    @Autowired
    public TransferenciaVeiculoService(
            @NotNull final TransferenciaVeiculoDao transferenciaVeiculoDao,
            @NotNull final TransferenciaVeiculoInformacoesDao transferenciaVeiculoInformacoesDao) {
        this.transferenciaVeiculoDao = transferenciaVeiculoDao;
        this.transferenciaVeiculoInformacoesDao = transferenciaVeiculoInformacoesDao;
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
