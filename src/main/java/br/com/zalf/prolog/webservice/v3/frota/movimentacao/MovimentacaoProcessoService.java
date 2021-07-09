package br.com.zalf.prolog.webservice.v3.frota.movimentacao;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.KmProcessoAtualizavel;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoDestinoEntity;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoEntity;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoOrigemEntity;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoProcessoEntity;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao.movimentacaoservico.MovimentacaoServicoRealizadoService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
public class MovimentacaoProcessoService implements KmProcessoAtualizavel {
    @NotNull
    private final MovimentacaoProcessoDao movimentacaoProcessoDao;
    @NotNull
    private final MovimentacaoDao movimentacaoDao;
    @NotNull
    private final MovimentacaoOrigemDao movimentacaoOrigemDao;
    @NotNull
    private final MovimentacaoDestinoDao movimentacaoDestinoDao;
    @NotNull
    private final MovimentacaoServicoRealizadoService movimentacaoServicoRealizadoService;

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
                                                                PageRequest.of(offset, limit));
    }

    @NotNull
    @Transactional
    public SuccessResponse insertProcessoMovimentacao(
            @NotNull final MovimentacaoProcessoEntity movimentacaoProcessoEntity) {

        final MovimentacaoProcessoEntity processoEntitySaved = movimentacaoProcessoDao.save(movimentacaoProcessoEntity);
        processoEntitySaved.getMovimentacoes().forEach(this::saveMovimentacao);

        return new SuccessResponse(1L, "Vai dar boa raça!");
    }

    private void saveMovimentacao(@NotNull final MovimentacaoEntity movimentacaoEntity) {
        final MovimentacaoEntity movimentacaoEntitySaved = movimentacaoDao.save(movimentacaoEntity);
        if (movimentacaoEntitySaved.isFromTo(OrigemDestinoEnum.ANALISE, OrigemDestinoEnum.ESTOQUE)) {
            insertMovimentacaoAnaliseEstoque(movimentacaoEntitySaved);
        }
    }

    private void insertMovimentacaoAnaliseEstoque(@NotNull final MovimentacaoEntity movimentacaoEntity) {
        movimentacaoServicoRealizadoService.insertMovimentacaoServicoPneu(movimentacaoEntity);
    }
}