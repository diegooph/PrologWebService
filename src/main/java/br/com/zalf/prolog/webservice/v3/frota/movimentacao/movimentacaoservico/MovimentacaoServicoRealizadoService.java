package br.com.zalf.prolog.webservice.v3.frota.movimentacao.movimentacaoservico;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizado;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoDestinoEntity;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoEntity;
import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuEntity;
import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico.PneuServicoService;
import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico._modal.PneuServicoRealizadoEntity;
import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico.tiposervico._modal.PneuTipoServicoEntity;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created on 2021-06-23
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Service
@Data
public class MovimentacaoServicoRealizadoService {
    @NotNull
    private final PneuServicoService pneuServicoService;
    @NotNull
    private final MovimentacaoPneuServicoRealizadoDao movimentacaoPneuServicoRealizadoDao;
    @NotNull
    private final MovimentacaoPneuServicoRealizadoRecapadoraDao movimentacaoPneuServicoRealizadoRecapadoraDao;

    @SuppressWarnings("checkstyle:FinalLocalVariable")
    @NotNull
    @Transactional
    public SuccessResponse inserMovimentacaoServicoPneu(
            @NotNull final MovimentacaoEntity movimentacaoEntity,
            @NotNull final MovimentacaoDestinoEntity movimentacaoDestinoEntity,
            @NotNull final PneuEntity pneuEntity,
            @NotNull final PneuTipoServicoEntity pneuTipoServicoEntity,
            @NotNull final PneuServicoRealizadoEntity pneuServicoRealizadoEntity) {
        this.pneuServicoService.insertServicoPneu(pneuEntity,
                pneuServicoRealizadoEntity.getCusto(),
                pneuTipoServicoEntity,
                PneuServicoRealizado.FONTE_MOVIMENTACAO);

        insertMovimentacaoPneuServicoRealizado(movimentacaoEntity.getCodigo(),
                pneuServicoRealizadoEntity.getCodigo());

        final Long codRecapadora = movimentacaoDestinoEntity.getRecapadora().getCodigo();
        if (codRecapadora != null) {
            insertMovimentacaoPneuServicoRealizadoRecapadora(movimentacaoEntity.getCodigo(),
                    pneuServicoRealizadoEntity.getCodigo(),
                    movimentacaoDestinoEntity.getRecapadora().getCodigo());
        }
        return null;
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
