package br.com.zalf.prolog.webservice.v3.fleet.tire.pneuservico;

import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tire.pneuservico.tiposervico.PneuTipoServicoEntity;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class PneuServicoRealizadoCreator {
    @NotNull
    public static PneuServicoRealizadoEntity createServicoRealizado(
            @NotNull final PneuTipoServicoEntity tipoServicoIncrementaVidaCadastroPneu,
            @NotNull final TireEntity tireCreated,
            @NotNull final String fonteCadastro,
            @NotNull final BigDecimal tireTreadPrice) {
        return PneuServicoRealizadoEntity.builder()
                .tipoServico(tipoServicoIncrementaVidaCadastroPneu)
                .codUnidade(tireCreated.getBranchEntity().getId())
                .pneuServicoRealizado(createTireEntity(tireCreated.getId()))
                .custo(tireTreadPrice)
                .vida(tireCreated.getPreviousRetread())
                .fonteServicoRealizado(fonteCadastro)
                .build();
    }

    @NotNull
    public static PneuServicoRealizadoIncrementaVidaEntity createServicoRealizadoIncrementaVida(
            @NotNull final TireEntity pneuCadastrado,
            @NotNull final PneuServicoRealizadoEntity servicoRealizado,
            @NotNull final String fonteCadastro) {
        return PneuServicoRealizadoIncrementaVidaEntity.builder()
                .codServicoRealizado(servicoRealizado.getCodigo())
                .codModeloBanda(pneuCadastrado.getTreadModelEntity().getId())
                .vidaNovaPneu(pneuCadastrado.getTimesRetreaded())
                .fonteServicoRealizado(fonteCadastro)
                .build();
    }

    @NotNull
    public static PneuServicoCadastroEntity createFromPneuServico(
            @NotNull final PneuServicoRealizadoEntity servicoRealizado,
            @NotNull final String fonteCadastro) {
        return PneuServicoCadastroEntity.builder()
                .codPneu(servicoRealizado.getPneuServicoRealizado().getId())
                .codServicoRealizado(servicoRealizado.getCodigo())
                .fonteServicoRealizado(fonteCadastro)
                .build();
    }

    @NotNull
    private static TireEntity createTireEntity(@NotNull final Long tireId) {
        return TireEntity.builder().withId(tireId).build();
    }
}
