package br.com.zalf.prolog.webservice.v3.frota.movimentacao.movimentacaoservico;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizado;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoEntity;
import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico.PneuServicoService;
import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico._modal.PneuServicoRealizadoEntity;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

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

    public void insertMovimentacaoServicoPneu(@NotNull final MovimentacaoEntity movimentacaoEntity) {
        validaServicosRealizados(movimentacaoEntity.getPneu().getCodigo(), movimentacaoEntity.getServicosRealizados());
        movimentacaoEntity.getServicosRealizados()
                .forEach(pneuServicoRealizado -> saveServicoRealizadoPneu(movimentacaoEntity, pneuServicoRealizado));
    }

    private void saveServicoRealizadoPneu(@NotNull final MovimentacaoEntity movimentacaoEntity,
                                          @NotNull final PneuServicoRealizadoEntity pneuServicoRealizadoEntity) {
        final PneuServicoRealizadoEntity servicoRealizadoEntitySaved =
                pneuServicoService.insertServicoPneu(pneuServicoRealizadoEntity.getPneuServicoRealizado(),
                                                     pneuServicoRealizadoEntity.getCusto(),
                                                     pneuServicoRealizadoEntity.getTipoServico(),
                                                     PneuServicoRealizado.FONTE_MOVIMENTACAO);

        insertMovimentacaoPneuServicoRealizado(movimentacaoEntity.getCodigo(), servicoRealizadoEntitySaved.getCodigo());

        final Long codRecapadora = movimentacaoEntity.getMovimentacaoDestino().getRecapadora().getCodigo();
        insertMovimentacaoPneuServicoRealizadoRecapadora(movimentacaoEntity.getCodigo(),
                                                         servicoRealizadoEntitySaved.getCodigo(),
                                                         codRecapadora);
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

    private void validaServicosRealizados(@NotNull final Long codPneu,
                                          @NotNull final Set<PneuServicoRealizadoEntity> servicosRealizados) {
        if (servicosRealizados.isEmpty()) {
            throw new IllegalStateException(
                    "O pneu " + codPneu + " foi movido dá análise para o estoque e não teve nenhum serviço aplicado!");
        }
        final long totalServicosIncrementamVida = servicosRealizados.stream()
                .filter(PneuServicoRealizadoEntity::isIncrementaVida)
                .count();
        if (totalServicosIncrementamVida > 1) {
            throw new GenericException("Não é possível realizar dois serviços de troca de banda na mesma movimentação");
        }
    }
}
