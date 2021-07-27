package br.com.zalf.prolog.webservice.v3.frota.movimentacao.movimentacaoservico;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizado;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoEntity;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao.movimentacaoservico.validation.MovimentacaoValidator;
import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico.PneuServicoService;
import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico._model.PneuServicoRealizadoEntity;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created on 2021-06-23
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MovimentacaoServicoRealizadoService {
    @NotNull
    private final PneuServicoService pneuServicoService;
    @NotNull
    private final MovimentacaoPneuServicoRealizadoDao movimentacaoPneuServicoRealizadoDao;
    @NotNull
    private final MovimentacaoPneuServicoRealizadoRecapadoraDao movimentacaoPneuServicoRealizadoRecapadoraDao;
    @NotNull
    private final MovimentacaoValidator movimentacaoValidator;

    public void insertMovimentacaoServicoPneu(@NotNull final MovimentacaoEntity movimentacaoEntity,
                                              final int limit,
                                              final int offset) {
        movimentacaoValidator.validaServicosRealizados(movimentacaoEntity.getPneu().getCodigo(),
                                                       movimentacaoEntity.getServicosRealizados());
        movimentacaoEntity.getServicosRealizados()
                .forEach(pneuServicoRealizado -> insertServicoRealizadoPneu(movimentacaoEntity,
                                                                            pneuServicoRealizado,
                                                                            limit,
                                                                            offset));
    }

    private void insertServicoRealizadoPneu(@NotNull final MovimentacaoEntity movimentacaoEntity,
                                            @NotNull final PneuServicoRealizadoEntity pneuServicoRealizadoEntity,
                                            final int limit,
                                            final int offset) {
        final PneuServicoRealizadoEntity servicoRealizadoEntitySaved =
                pneuServicoService.insertServicoPneu(pneuServicoRealizadoEntity.getPneuServicoRealizado(),
                                                     pneuServicoRealizadoEntity.getCusto(),
                                                     pneuServicoRealizadoEntity.getTipoServico(),
                                                     PneuServicoRealizado.FONTE_MOVIMENTACAO);

        insertMovimentacaoPneuServicoRealizado(movimentacaoEntity.getCodigo(), servicoRealizadoEntitySaved.getCodigo());

        final Long codRecapadora = pneuServicoService.getCodigoRecapadora(movimentacaoEntity.getPneu().getCodigo(),
                                                                          OrigemDestinoEnum.ANALISE.asString(),
                                                                          limit, offset);
    }

    private void insertMovimentacaoPneuServicoRealizado(
            @NotNull final Long codMovimentacao,
            @NotNull final Long codPneuServicoRealizado) {
        movimentacaoPneuServicoRealizadoDao.save(
                MovimentacaoServicoRealizadoCreator.createMovimentacaoPneuServicoRealizado(
                        codMovimentacao,
                        codPneuServicoRealizado,
                        PneuServicoRealizado.FONTE_MOVIMENTACAO));
    }

    private void insertMovimentacaoPneuServicoRealizadoRecapadora(
            @NotNull final Long codMovimentacao,
            @NotNull final Long codPneuServicoRealizado,
            @NotNull final Long codRecapadora) {
        movimentacaoPneuServicoRealizadoRecapadoraDao.save(
                MovimentacaoServicoRealizadoCreator.createMovimentacaoPneuServicoRealizadoRecapadora(
                        codMovimentacao,
                        codPneuServicoRealizado,
                        codRecapadora));
    }
}
