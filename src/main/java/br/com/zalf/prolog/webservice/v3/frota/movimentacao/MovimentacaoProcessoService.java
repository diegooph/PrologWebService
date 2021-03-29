package br.com.zalf.prolog.webservice.v3.frota.movimentacao;

import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoDestinoEntity;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoEntity;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoOrigemEntity;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoProcessoEntity;
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
public class MovimentacaoProcessoService {

    @NotNull
    private final MovimentacaoProcessoDao movimentacaoProcessoDao;
    @NotNull
    private final MovimentacaoOrigemDao movimentacaoOrigemDao;
    @NotNull
    private final MovimentacaoDestinoDao movimentacaoDestinoDao;

    @Autowired
    public MovimentacaoProcessoService(@NotNull final MovimentacaoProcessoDao movimentacaoProcessoDao,
                                       @NotNull final MovimentacaoOrigemDao movimentacaoOrigemDao,
                                       @NotNull final MovimentacaoDestinoDao movimentacaoDestinoDao) {
        this.movimentacaoProcessoDao = movimentacaoProcessoDao;
        this.movimentacaoOrigemDao = movimentacaoOrigemDao;
        this.movimentacaoDestinoDao = movimentacaoDestinoDao;
    }

    @NotNull
    public MovimentacaoProcessoEntity getByCodigo(@NotNull final Long codigo) {
        return movimentacaoProcessoDao.getOne(codigo);
    }

    public void update(@NotNull final MovimentacaoProcessoEntity movimentacaoEntity) {
        movimentacaoProcessoDao.save(movimentacaoEntity);
    }

    @Transactional
    public void updateKmColetado(@NotNull final MovimentacaoProcessoEntity movimentacaoProcesso,
                                 final long novoKm) {
        movimentacaoProcesso
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
