package br.com.zalf.prolog.webservice.v3.fleet.movimentacao.movimentacaoservico;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizado;
import br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model.MovimentacaoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.movimentacao.movimentacaoservico.validation.MovimentacaoValidator;
import br.com.zalf.prolog.webservice.v3.fleet.tire.pneuservico.PneuServicoRealizadoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tire.pneuservico.PneuServicoService;
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

    public void insertMovimentacaoServicoPneu(@NotNull final MovimentacaoEntity movimentacaoEntity) {
        movimentacaoValidator.validaServicosRealizados(movimentacaoEntity.getPneu().getId(),
                                                       movimentacaoEntity.getServicosRealizados());
        movimentacaoEntity.getServicosRealizados()
                .forEach(pneuServicoRealizado -> insertServicoRealizadoPneu(movimentacaoEntity, pneuServicoRealizado));
    }

    private void insertServicoRealizadoPneu(@NotNull final MovimentacaoEntity movimentacaoEntity,
                                            @NotNull final PneuServicoRealizadoEntity pneuServicoRealizadoEntity) {
        final PneuServicoRealizadoEntity servicoRealizadoEntitySaved =
                pneuServicoService.insertServicoPneu(pneuServicoRealizadoEntity.getPneuServicoRealizado(),
                                                     pneuServicoRealizadoEntity.getCusto(),
                                                     pneuServicoRealizadoEntity.getTipoServico(),
                                                     PneuServicoRealizado.FONTE_MOVIMENTACAO);

        insertMovimentacaoPneuServicoRealizado(movimentacaoEntity.getCodigo(), servicoRealizadoEntitySaved.getCodigo());

        final Long codRecapadora = pneuServicoService.getCodigoRecapadora(movimentacaoEntity.getPneu().getId(),
                                                                          OrigemDestinoEnum.ANALISE.asString());

        insertMovimentacaoPneuServicoRealizadoRecapadora(movimentacaoEntity.getCodigo(),
                                                         servicoRealizadoEntitySaved.getCodigo(),
                                                         codRecapadora);
    }

    private void insertMovimentacaoPneuServicoRealizado(@NotNull final Long codMovimentacao,
                                                        @NotNull final Long codPneuServicoRealizado) {
        movimentacaoPneuServicoRealizadoDao.save(
                MovimentacaoServicoRealizadoCreator.createMovimentacaoPneuServicoRealizado(
                        codMovimentacao,
                        codPneuServicoRealizado,
                        PneuServicoRealizado.FONTE_MOVIMENTACAO));
    }

    private void insertMovimentacaoPneuServicoRealizadoRecapadora(@NotNull final Long codMovimentacao,
                                                                  @NotNull final Long codPneuServicoRealizado,
                                                                  @NotNull final Long codRecapadora) {
        movimentacaoPneuServicoRealizadoRecapadoraDao.save(
                MovimentacaoServicoRealizadoCreator.createMovimentacaoPneuServicoRealizadoRecapadora(
                        codMovimentacao,
                        codPneuServicoRealizado,
                        codRecapadora));
    }
}
