package br.com.zalf.prolog.webservice.frota.v3.pneuservico;

import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizado;
import br.com.zalf.prolog.webservice.frota.v3.pneu._model.PneuEntity;
import br.com.zalf.prolog.webservice.frota.v3.pneuservico.tiposervico.PneuTipoServicoEntity;
import br.com.zalf.prolog.webservice.frota.v3.pneuservico.tiposervico.PneuTipoServicoV3Service;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Service
public class PneuServicoV3Service {
    @NotNull
    private final PneuServicoRealizadoV3Dao pneuServicoDao;
    @NotNull
    private final PneuServicoRealizadoIncrementaVidaV3Dao pneuServicoRealizadoIncrementaVidaV3Dao;
    @NotNull
    private final PneuServicoCadastroV3Dao pneuServicoCadastroV3Dao;
    @NotNull
    private final PneuTipoServicoV3Service pneuTipoServicoService;

    @Autowired
    public PneuServicoV3Service(
            @NotNull final PneuServicoRealizadoV3Dao pneuServicoDao,
            @NotNull final PneuServicoRealizadoIncrementaVidaV3Dao pneuServicoRealizadoIncrementaVidaDao,
            @NotNull final PneuServicoCadastroV3Dao pneuServicoCadastroDao,
            @NotNull final PneuTipoServicoV3Service pneuTipoServicoService) {
        this.pneuServicoDao = pneuServicoDao;
        this.pneuServicoRealizadoIncrementaVidaV3Dao = pneuServicoRealizadoIncrementaVidaDao;
        this.pneuServicoCadastroV3Dao = pneuServicoCadastroDao;
        this.pneuTipoServicoService = pneuTipoServicoService;
    }

    @NotNull
    @Transactional
    public PneuServicoRealizadoEntity createServicoByPneu(@NotNull final PneuEntity pneuEntity,
                                                          @NotNull final BigDecimal valorBanda) {
        final PneuTipoServicoEntity tipoServico =
                this.pneuTipoServicoService.getInitialTipoServicoForVidaIncrementada();
        final PneuServicoRealizadoEntity savedServico =
                this.pneuServicoDao.save(createPneuServicoForCadastro(tipoServico,
                                                                      pneuEntity,
                                                                      valorBanda));
        final PneuServicoRealizadoIncrementaVidaEntity incrementaVida =
                PneuServicoRealizadoIncrementaVidaEntity.createFromPneuServico(pneuEntity);
        pneuServicoRealizadoIncrementaVidaV3Dao.save(incrementaVida);
        final PneuServicoCadastroEntity servicoCadastro =
                PneuServicoCadastroEntity.createFromPneuServico(savedServico);
        pneuServicoCadastroV3Dao.save(servicoCadastro);
        return savedServico;
    }

    @NotNull
    private PneuServicoRealizadoEntity createPneuServicoForCadastro(
            @NotNull final PneuTipoServicoEntity pneuTipoServico,
            @NotNull final PneuEntity pneu,
            @NotNull final BigDecimal valorBanda) {
        final PneuServicoRealizadoEntity.PK pkServicoRealizado = PneuServicoRealizadoEntity.PK.builder()
                .fonteServicoRealizado(PneuServicoRealizado.FONTE_CADASTRO)
                .build();
        return PneuServicoRealizadoEntity.builder()
                .pk(pkServicoRealizado)
                .codTipoServico(pneuTipoServico.getCodigo())
                .codUnidade(pneu.getCodUnidade())
                .codPneu(pneu.getCodigo())
                .custo(valorBanda)
                .vida(getVidaServicoFromPneu(pneu))
                .build();
    }

    @NotNull
    private Integer getVidaServicoFromPneu(@NotNull final PneuEntity pneu) {
        return pneu.getVidaAtual() - 1;
    }
}
