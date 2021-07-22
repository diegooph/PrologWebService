package br.com.zalf.prolog.webservice.v3.fleet.tire.pneuservico;

import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizado;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.PneuEntity;
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

    @NotNull
    @Transactional
    public PneuServicoRealizadoEntity insertServicoCadastroPneu(@NotNull final PneuEntity pneuCadastrado,
                                                                @NotNull final BigDecimal valorBanda) {
        final PneuTipoServicoEntity tipoServicoIncrementaVidaCadastroPneu =
                pneuTipoServicoService.getTipoServicoIncrementaVidaCadastroPneu();
        final PneuServicoRealizadoEntity savedServicoRealizado =
                pneuServicoDao.save(
                        PneuServicoRealizadoCreator.createServicoRealizado(tipoServicoIncrementaVidaCadastroPneu,
                                                                           pneuCadastrado,
                                                                           PneuServicoRealizado.FONTE_CADASTRO,
                                                                           valorBanda));
        pneuServicoRealizadoIncrementaVidaDao.save(
                PneuServicoRealizadoCreator.createServicoRealizadoIncrementaVida(pneuCadastrado,
                                                                                 savedServicoRealizado,
                                                                                 PneuServicoRealizado.FONTE_CADASTRO));
        pneuServicoCadastroDao.save(
                PneuServicoRealizadoCreator.createFromPneuServico(savedServicoRealizado,
                                                                  PneuServicoRealizado.FONTE_CADASTRO));
        return savedServicoRealizado;
    }
}
