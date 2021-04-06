package br.com.zalf.prolog.webservice.v3.frota.movimentacao;

import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.KmProcessoAtualizavel;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoDestinoEntity;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoEntity;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoOrigemEntity;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoProcessoEntity;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MovimentacaoProcessoService implements KmProcessoAtualizavel {
    @NotNull
    private final MovimentacaoProcessoDao movimentacaoProcessoDao;
    @NotNull
    private final MovimentacaoOrigemDao movimentacaoOrigemDao;
    @NotNull
    private final MovimentacaoDestinoDao movimentacaoDestinoDao;

    @NotNull
    @Override
    public EntityKmColetado getEntityKmColetado(@NotNull final Long entityId,
                                                @NotNull final Long codVeiculo) {
        return getByCodigo(entityId);
    }

    @Override
    public void updateKmColetadoProcesso(@NotNull final Long codProcesso,
                                         @NotNull final Long codVeiculo,
                                         final long novoKm) {
        updateKmColetado(codProcesso, novoKm);
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
                    if (origem.getCodVeiculo() != null) {
                        final MovimentacaoOrigemEntity novaOrigem = origem
                                .toBuilder()
                                .withKmColetadoVeiculo(novoKm)
                                .build();
                        movimentacaoOrigemDao.save(novaOrigem);
                    }
                    if (destino.getCodVeiculo() != null) {
                        final MovimentacaoDestinoEntity novoDestino = destino
                                .toBuilder()
                                .withKmColetadoVeiculo(novoKm)
                                .build();
                        movimentacaoDestinoDao.save(novoDestino);
                    }
                });
    }
}