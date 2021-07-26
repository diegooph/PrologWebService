package br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico;

import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuEntity;
import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico._model.PneuServicoCadastroEntity;
import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico._model.PneuServicoRealizadoEntity;
import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico._model.PneuServicoRealizadoIncrementaVidaEntity;
import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico.tiposervico._modal.PneuTipoServicoEntity;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class PneuServicoRealizadoCreator {
    @NotNull
    public static PneuServicoRealizadoEntity createServicoRealizado(
            @NotNull final PneuTipoServicoEntity tipoServicoIncrementaVidaCadastroPneu,
            @NotNull final PneuEntity pneuServicoRealizado,
            @NotNull final String fonteServicoRealizado,
            @NotNull final BigDecimal valorBanda) {
        return PneuServicoRealizadoEntity.builder()
                .tipoServico(tipoServicoIncrementaVidaCadastroPneu)
                .codUnidade(pneuServicoRealizado.getUnidade().getCodigo())
                .pneuServicoRealizado(createPneuEntity(pneuServicoRealizado.getCodigo()))
                .custo(valorBanda)
                .vida(pneuServicoRealizado.getVidaAnterior())
                .fonteServicoRealizado(fonteServicoRealizado)
                .build();
    }

    @NotNull
    public static PneuServicoRealizadoIncrementaVidaEntity createServicoRealizadoIncrementaVida(
            @NotNull final PneuEntity pneuCadastrado,
            @NotNull final PneuServicoRealizadoEntity servicoRealizado,
            @NotNull final String fonteServicoRealizado) {
        return PneuServicoRealizadoIncrementaVidaEntity.builder()
                .codServicoRealizado(servicoRealizado.getCodigo())
                .codModeloBanda(pneuCadastrado.getModeloBanda().getCodigo())
                .vidaNovaPneu(pneuCadastrado.getVidaAtual())
                .fonteServicoRealizado(fonteServicoRealizado)
                .build();
    }

    @NotNull
    public static PneuServicoCadastroEntity createFromPneuServico(
            @NotNull final PneuServicoRealizadoEntity servicoRealizado,
            @NotNull final String fonteServicoRealizado) {
        return PneuServicoCadastroEntity.builder()
                .codPneu(servicoRealizado.getPneuServicoRealizado().getCodigo())
                .codServicoRealizado(servicoRealizado.getCodigo())
                .fonteServicoRealizado(fonteServicoRealizado)
                .build();
    }

    @NotNull
    private static PneuEntity createPneuEntity(@NotNull final Long codigo) {
        return PneuEntity.builder().codigo(codigo).build();
    }
}
