package br.com.zalf.prolog.webservice.frota.pneu.v3.service.servico;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.PneuServicoCadastroEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.PneuServicoRealizadoEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.PneuServicoRealizadoIncrementaVidaEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.PneuTipoServicoEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3.dao.servico.PneuServicoCadastroV3Dao;
import br.com.zalf.prolog.webservice.frota.pneu.v3.dao.servico.PneuServicoRealizadoIncrementaVidaV3Dao;
import br.com.zalf.prolog.webservice.frota.pneu.v3.dao.servico.PneuServicoRealizadoV3Dao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    private final PneuServicoRealizadoV3Dao dao;
    @NotNull
    private final PneuServicoRealizadoIncrementaVidaV3Dao pneuServicoRealizadoIncrementaVidaV3Dao;
    @NotNull
    private final PneuServicoCadastroV3Dao pneuServicoCadastroV3Dao;
    @NotNull
    private final PneuTipoServicoV3Service tipoServicoService;

    @Autowired
    public PneuServicoV3Service(@NotNull final PneuServicoRealizadoV3Dao dao,
                                @NotNull final PneuServicoRealizadoIncrementaVidaV3Dao pneuServicoRealizadoIncrementaVidaDao,
                                @NotNull final PneuServicoCadastroV3Dao pneuServicoCadastroDao,
                                @NotNull final PneuTipoServicoV3Service tipoServicoService) {
        this.dao = dao;
        this.pneuServicoRealizadoIncrementaVidaV3Dao = pneuServicoRealizadoIncrementaVidaDao;
        this.pneuServicoCadastroV3Dao = pneuServicoCadastroDao;
        this.tipoServicoService = tipoServicoService;
    }

    @NotNull
    @Transactional
    public PneuServicoRealizadoEntity createServicoByPneu(@NotNull final PneuEntity pneuEntity,
                                                          @Nullable final BigDecimal valorBanda) {

        final var tipoServico = this.tipoServicoService.getInitialTipoServicoForVidaIncrementada();
        final var savedPneuServico = this.dao.save(createPneuServicoForCadastro(tipoServico,
                                                                                                    pneuEntity,
                                                                                                   valorBanda.doubleValue()));
        this.pneuServicoRealizadoIncrementaVidaV3Dao.save(PneuServicoRealizadoIncrementaVidaEntity
                                                                  .createFromPneuServico(savedPneuServico));
        if (savedPneuServico.isCadastro()) {
           this.pneuServicoCadastroV3Dao.save(PneuServicoCadastroEntity
                                                      .createFromPneuServico(savedPneuServico));
        }
        return savedPneuServico;
    }

    private PneuServicoRealizadoEntity createPneuServicoForCadastro(final PneuTipoServicoEntity pneuTipoServico,
                                                                    final PneuEntity pneu,
                                                                    final Double custoAquisicaoBanda) {
        return PneuServicoRealizadoEntity.builder()
                .tipoServico(pneuTipoServico)
                .codUnidade(pneu.getCodUnidade())
                .pneu(pneu)
                .vida(getVidaServicoFromPneu(pneu))
                .custo(BigDecimal.valueOf(custoAquisicaoBanda))
                .fonteServico(PneuServicoRealizadoEntity.FonteServico.CADASTRO)
                .build();
    }
    @NotNull
    private Integer getVidaServicoFromPneu(@NotNull final PneuEntity pneu) {
        return pneu.getVidaAtual() - 1;
    }
}
