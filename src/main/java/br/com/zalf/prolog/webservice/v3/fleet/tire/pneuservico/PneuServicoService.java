package br.com.zalf.prolog.webservice.v3.fleet.tire.pneuservico;

import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizado;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tire.pneuservico.tiposervico.PneuTipoServicoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tire.pneuservico.tiposervico.PneuTipoServicoService;
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
public class PneuServicoService {
    @NotNull
    private final PneuServicoRealizadoDao pneuServicoRealizadoDao;
    @NotNull
    private final PneuServicoRealizadoIncrementaVidaDao pneuServicoRealizadoIncrementaVidaDao;
    @NotNull
    private final PneuServicoCadastroDao pneuServicoCadastroDao;
    @NotNull
    private final PneuTipoServicoService pneuTipoServicoService;

    @Autowired
    public PneuServicoService(
            @NotNull final PneuServicoRealizadoDao pneuServicoRealizadoDao,
            @NotNull final PneuServicoRealizadoIncrementaVidaDao pneuServicoRealizadoIncrementaVidaDao,
            @NotNull final PneuServicoCadastroDao pneuServicoCadastroDao,
            @NotNull final PneuTipoServicoService pneuTipoServicoService) {
        this.pneuServicoRealizadoDao = pneuServicoRealizadoDao;
        this.pneuServicoRealizadoIncrementaVidaDao = pneuServicoRealizadoIncrementaVidaDao;
        this.pneuServicoCadastroDao = pneuServicoCadastroDao;
        this.pneuTipoServicoService = pneuTipoServicoService;
    }

    @NotNull
    @Transactional
    public PneuServicoRealizadoEntity insertServicoPneu(@NotNull final TireEntity pneuServicoRealizado,
                                                        @NotNull final BigDecimal valorBanda,
                                                        @NotNull final PneuTipoServicoEntity pneuTipoServicoEntity,
                                                        @NotNull final String fonteServicoRealizado) {
        final PneuServicoRealizadoEntity savedServicoRealizado =
                pneuServicoRealizadoDao.save(
                        PneuServicoRealizadoCreator.createServicoRealizado(pneuTipoServicoEntity,
                                                                           pneuServicoRealizado,
                                                                           fonteServicoRealizado,
                                                                           valorBanda));
        if (pneuTipoServicoEntity.isIncrementaVida()) {
            insertPneuServicoRealizadoIncrementaVida(pneuServicoRealizado,
                                                     fonteServicoRealizado,
                                                     savedServicoRealizado);
        }
        if (fonteServicoRealizado.equals(PneuServicoRealizado.FONTE_CADASTRO)) {
            insertPneuServicoRealizadoCadastro(fonteServicoRealizado, savedServicoRealizado);
        }
        return savedServicoRealizado;
    }

    @NotNull
    public PneuTipoServicoEntity getPneuTipoServicoIncrementaVidaCadastroEntity() {
        return pneuTipoServicoService.getTipoServicoIncrementaVidaPneu();
    }

    @NotNull
    public Long getCodigoRecapadora(@NotNull final Long codPneu, @NotNull final String analise) {
        return pneuServicoRealizadoDao.getCodigoRecapadora(codPneu, analise);
    }

    private void insertPneuServicoRealizadoIncrementaVida(@NotNull final TireEntity pneuCadastrado,
                                                          @NotNull final String fonteServicoRealizado,
                                                          @NotNull final PneuServicoRealizadoEntity savedServicoRealizado) {
        pneuServicoRealizadoIncrementaVidaDao.save(
                PneuServicoRealizadoCreator.createServicoRealizadoIncrementaVida(pneuCadastrado,
                                                                                 savedServicoRealizado,
                                                                                 fonteServicoRealizado));
    }

    private void insertPneuServicoRealizadoCadastro(@NotNull final String fonteServicoRealizado,
                                                    @NotNull final PneuServicoRealizadoEntity savedServicoRealizado) {
        pneuServicoCadastroDao.save(
                PneuServicoRealizadoCreator.createFromPneuServico(savedServicoRealizado, fonteServicoRealizado));
    }
}