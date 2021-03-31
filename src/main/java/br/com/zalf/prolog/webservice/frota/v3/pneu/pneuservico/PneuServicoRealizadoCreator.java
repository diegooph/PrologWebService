package br.com.zalf.prolog.webservice.frota.v3.pneu.pneuservico;

import br.com.zalf.prolog.webservice.frota.v3.pneu._model.PneuEntity;
import br.com.zalf.prolog.webservice.frota.v3.pneu.pneuservico.tiposervico.PneuTipoServicoEntity;
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
                .codTipoServico(tipoServicoIncrementaVidaCadastroPneu.getCodigo())
                .codUnidade(pneuCadastrado.getCodUnidade())
                .codPneu(pneuCadastrado.getCodigo())
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
                .codModeloBanda(pneuCadastrado.getCodModeloBanda())
                .vidaNovaPneu(pneuCadastrado.getVidaAtual())
                .fonteServicoRealizado(fonteCadastro)
                .build();
    }

    @NotNull
    public static PneuServicoCadastroEntity createFromPneuServico(
            @NotNull final PneuServicoRealizadoEntity servicoRealizado,
            @NotNull final String fonteCadastro) {
        return PneuServicoCadastroEntity.builder()
                .codPneu(servicoRealizado.getCodPneu())
                .codServicoRealizado(servicoRealizado.getCodigo())
                .fonteServicoRealizado(fonteCadastro)
                .build();
    }
}
