package br.com.zalf.prolog.webservice.v3.fleet.movimentacao;

import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.v3.OffsetBasedPageRequest;
import br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model.MovimentacaoDestinoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model.MovimentacaoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model.MovimentacaoOrigemEntity;
import br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model.MovimentacaoProcessoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.KmCollectedEntity;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.ProcessKmUpdatable;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MovimentacaoProcessoService implements ProcessKmUpdatable {
    @NotNull
    private final MovimentacaoProcessoDao movimentacaoProcessoDao;
    @NotNull
    private final MovimentacaoOrigemDao movimentacaoOrigemDao;
    @NotNull
    private final MovimentacaoDestinoDao movimentacaoDestinoDao;

    @NotNull
    @Override
    public KmCollectedEntity getEntityKmCollected(@NotNull final Long entityId,
                                                  @NotNull final Long vehicleId) {
        return getByCodigo(entityId);
    }

    @Override
    public void updateProcessKmCollected(@NotNull final Long processId,
                                         @NotNull final Long vehicleId,
                                         final long newKm) {
        updateKmColetado(processId, newKm);
    }

    @NotNull
    public MovimentacaoProcessoEntity getByCodigo(@NotNull final Long codigo) {
        return movimentacaoProcessoDao.getOne(codigo);
    }

    public void update(@NotNull final MovimentacaoProcessoEntity movimentacaoEntity) {
        movimentacaoProcessoDao.save(movimentacaoEntity);
    }

    @Transactional
    public void updateKmColetado(@NotNull final Long codProcessoMovimentacao,
                                 final long novoKm) {
        getByCodigo(codProcessoMovimentacao)
                .getMovimentacoes()
                .stream()
                .filter(MovimentacaoEntity::isMovimentacaoNoVeiculo)
                .forEach(movimentacao -> {
                    final MovimentacaoOrigemEntity origem = movimentacao.getMovimentacaoOrigem();
                    final MovimentacaoDestinoEntity destino = movimentacao.getMovimentacaoDestino();
                    if (origem.getVeiculo() != null) {
                        final MovimentacaoOrigemEntity novaOrigem = origem
                                .toBuilder()
                                .withKmColetadoVeiculo(novoKm)
                                .build();
                        movimentacaoOrigemDao.save(novaOrigem);
                    }
                    if (destino.getVeiculo() != null) {
                        final MovimentacaoDestinoEntity novoDestino = destino
                                .toBuilder()
                                .withKmColetadoVeiculo(novoKm)
                                .build();
                        movimentacaoDestinoDao.save(novoDestino);
                    }
                });
    }

    @NotNull
    @Transactional
    public List<MovimentacaoProcessoEntity> getListagemMovimentacoes(@NotNull final List<Long> codUnidades,
                                                                     @NotNull final String dataInicial,
                                                                     @NotNull final String dataFinal,
                                                                     @Nullable final Long codColaborador,
                                                                     @Nullable final Long codVeiculo,
                                                                     @Nullable final Long codPneu,
                                                                     final int limit,
                                                                     final int offset) {
        return movimentacaoProcessoDao.getListagemMovimentacoes(codUnidades,
                                                                DateUtils.parseDate(dataInicial),
                                                                DateUtils.parseDate(dataFinal),
                                                                codColaborador,
                                                                codVeiculo,
                                                                codPneu,
                                                                OffsetBasedPageRequest.of(limit,
                                                                                          offset,
                                                                                          Sort.unsorted()));
    }
}
