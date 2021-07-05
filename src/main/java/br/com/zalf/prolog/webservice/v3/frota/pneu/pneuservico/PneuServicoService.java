package br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico;

import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizado;
import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuEntity;
import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico._modal.PneuServicoRealizadoEntity;
import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico.tiposervico.PneuTipoServicoService;
import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico.tiposervico._modal.PneuTipoServicoEntity;
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
    private final PneuServicoRealizadoDao pneuServicoDao;
    @NotNull
    private final PneuServicoRealizadoIncrementaVidaDao pneuServicoRealizadoIncrementaVidaDao;
    @NotNull
    private final PneuServicoCadastroDao pneuServicoCadastroDao;
    @NotNull
    private final PneuTipoServicoService pneuTipoServicoService;

    @Autowired
    public PneuServicoService(
            @NotNull final PneuServicoRealizadoDao pneuServicoDao,
            @NotNull final PneuServicoRealizadoIncrementaVidaDao pneuServicoRealizadoIncrementaVidaDao,
            @NotNull final PneuServicoCadastroDao pneuServicoCadastroDao,
            @NotNull final PneuTipoServicoService pneuTipoServicoService) {
        this.pneuServicoDao = pneuServicoDao;
        this.pneuServicoRealizadoIncrementaVidaDao = pneuServicoRealizadoIncrementaVidaDao;
        this.pneuServicoCadastroDao = pneuServicoCadastroDao;
        this.pneuTipoServicoService = pneuTipoServicoService;
    }

    //TODO pros dois
    // --OK
    @NotNull
    @Transactional
    public PneuServicoRealizadoEntity insertServicoPneu(@NotNull final PneuEntity pneuServicoRealizado,
                                                        @NotNull final BigDecimal valorBanda,
                                                        final PneuTipoServicoEntity pneuTipoServicoEntity,
                                                        @NotNull final String fonteServicoRealizado) {
        final PneuServicoRealizadoEntity savedServicoRealizado =
                pneuServicoDao.save(
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

    @NotNull //TODO POSSO DEIXAR MAIS GENÉRICO
    public PneuTipoServicoEntity getPneuTipoServicoEntity() {
        final PneuTipoServicoEntity tipoServicoIncrementaVidaCadastroPneu =
                pneuTipoServicoService.getTipoServicoIncrementaVidaPneu();
        return tipoServicoIncrementaVidaCadastroPneu;
    }

    private void insertPneuServicoRealizadoIncrementaVida(@NotNull final PneuEntity pneuCadastrado,
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
                PneuServicoRealizadoCreator.createFromPneuServico(savedServicoRealizado,
                        fonteServicoRealizado));
    }
}
