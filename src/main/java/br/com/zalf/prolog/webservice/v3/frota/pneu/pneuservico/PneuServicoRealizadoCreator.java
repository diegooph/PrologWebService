package br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico;

import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuEntity;
import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico.tiposervico.PneuTipoServicoEntity;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class PneuServicoRealizadoCreator {
    @NotNull
    public static PneuServicoRealizadoEntity createServicoRealizado(
            @NotNull final PneuTipoServicoEntity tipoServicoIncrementaVidaCadastroPneu,
            @NotNull final PneuEntity pneuCadastrado,
            @NotNull final String fonteCadastro,
            @NotNull final BigDecimal valorBanda) {
        return PneuServicoRealizadoEntity.builder()
                .tipoServico(tipoServicoIncrementaVidaCadastroPneu)
                .codUnidade(pneuCadastrado.getUnidade().getCodigo())
                .pneuServicoRealizado(createPneuEntity(pneuCadastrado.getCodigo()))
                .custo(valorBanda)
                .vida(pneuCadastrado.getVidaAnterior())
                .fonteServicoRealizado(fonteCadastro)
                .build();
    }

    @NotNull
    public static PneuServicoRealizadoIncrementaVidaEntity createServicoRealizadoIncrementaVida(
            @NotNull final PneuEntity pneuCadastrado,
            @NotNull final PneuServicoRealizadoEntity servicoRealizado,
            @NotNull final String fonteCadastro) {
        return PneuServicoRealizadoIncrementaVidaEntity.builder()
                .codServicoRealizado(servicoRealizado.getCodigo())
                .codModeloBanda(pneuCadastrado.getModeloBanda().getCodigo())
                .vidaNovaPneu(pneuCadastrado.getVidaAtual())
                .fonteServicoRealizado(fonteCadastro)
                .build();
    }

    @NotNull
    public static PneuServicoCadastroEntity createFromPneuServico(
            @NotNull final PneuServicoRealizadoEntity servicoRealizado,
            @NotNull final String fonteCadastro) {
        return PneuServicoCadastroEntity.builder()
                .codPneu(servicoRealizado.getPneuServicoRealizado().getCodigo())
                .codServicoRealizado(servicoRealizado.getCodigo())
                .fonteServicoRealizado(fonteCadastro)
                .build();
    }

    @NotNull
    private static PneuEntity createPneuEntity(@NotNull final Long codigo) {
        return PneuEntity.builder().codigo(codigo).build();
    }
}
