package br.com.zalf.prolog.webservice.frota.v3.pneu.pneuservico;

import br.com.zalf.prolog.webservice.frota.v3.pneu._model.PneuEntity;
import br.com.zalf.prolog.webservice.frota.v3.pneu.pneuservico.tiposervico.PneuTipoServicoEntity;
import br.com.zalf.prolog.webservice.frota.v3.pneu.pneuservico.tiposervico.PneuTipoServicoV3Service;
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
    private final PneuServicoRealizadoIncrementaVidaV3Dao pneuServicoRealizadoIncrementaVidaDao;
    @NotNull
    private final PneuServicoCadastroV3Dao pneuServicoCadastroDao;
    @NotNull
    private final PneuTipoServicoV3Service pneuTipoServicoService;

    @Autowired
    public PneuServicoV3Service(
            @NotNull final PneuServicoRealizadoV3Dao pneuServicoDao,
            @NotNull final PneuServicoRealizadoIncrementaVidaV3Dao pneuServicoRealizadoIncrementaVidaDao,
            @NotNull final PneuServicoCadastroV3Dao pneuServicoCadastroDao,
            @NotNull final PneuTipoServicoV3Service pneuTipoServicoService) {
        this.pneuServicoDao = pneuServicoDao;
        this.pneuServicoRealizadoIncrementaVidaDao = pneuServicoRealizadoIncrementaVidaDao;
        this.pneuServicoCadastroDao = pneuServicoCadastroDao;
        this.pneuTipoServicoService = pneuTipoServicoService;
    }

    @NotNull
    @Transactional
    public PneuServicoRealizadoEntity createServicoByPneu(@NotNull final PneuEntity pneuEntity,
                                                          @NotNull final BigDecimal valorBanda) {
        final PneuTipoServicoEntity tipoServico =
                this.pneuTipoServicoService.getInitialTipoServicoForVidaIncrementada();
        final PneuServicoRealizadoEntity savedServico = this.pneuServicoDao.save(
                PneuServicoRealizadoEntity.createPneuServicoForCadastro(tipoServico, pneuEntity, valorBanda));
        pneuServicoRealizadoIncrementaVidaDao.save(
                PneuServicoRealizadoIncrementaVidaEntity.createFromPneuServico(pneuEntity, savedServico));
        pneuServicoCadastroDao.save(PneuServicoCadastroEntity.createFromPneuServico(savedServico));
        return savedServico;
    }
}
