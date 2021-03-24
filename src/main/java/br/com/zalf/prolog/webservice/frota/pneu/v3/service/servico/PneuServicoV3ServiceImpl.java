package br.com.zalf.prolog.webservice.frota.pneu.v3.service.servico;

import br.com.zalf.prolog.webservice.database._model.DadosDelecao;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.PneuServicoEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.PneuTipoServicoEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3.dao.servico.PneuServicoV3Dao;
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
public class PneuServicoV3ServiceImpl implements PneuServicoV3Service {

    private final PneuServicoV3Dao dao;
    private final PneuTipoServicoV3Service tipoServicoService;
    private final PneuServicoHistoricoV3Service pneuServicoHistoricoService;

    @Autowired
    public PneuServicoV3ServiceImpl(@NotNull final PneuServicoV3Dao dao,
                                    @NotNull final PneuTipoServicoV3Service tipoServicoService,
                                    @NotNull final PneuServicoHistoricoV3Service pneuServicoHistoricoService) {
        this.dao = dao;
        this.tipoServicoService = tipoServicoService;
        this.pneuServicoHistoricoService = pneuServicoHistoricoService;
    }

    @Override
    @NotNull
    @Transactional
    public PneuServicoEntity createServicoByPneu(@NotNull final PneuEntity pneuEntity,
                                                 @NotNull final Double custoAquisicaoBanda) {

        final var tipoServico = this.tipoServicoService.getInitialTipoServicoForVidaIncrementada();
        final var savedPneuServico = this.dao.save(createPneuServicoForCadastro(tipoServico,
                                                                                                    pneuEntity,
                                                                                                   custoAquisicaoBanda));
        this.pneuServicoHistoricoService.saveHistorico(savedPneuServico);
        return savedPneuServico;
    }

    private PneuServicoEntity createPneuServicoForCadastro(final PneuTipoServicoEntity pneuTipoServico,
                                                           final PneuEntity pneu,
                                                           final Double custoAquisicaoBanda) {
        return PneuServicoEntity.builder()
                .tipoServico(pneuTipoServico)
                .codUnidade(pneu.getCodUnidade())
                .pneu(pneu)
                .vida(getVidaServicoFromPneu(pneu))
                .custo(BigDecimal.valueOf(custoAquisicaoBanda))
                .fonteServico(PneuServicoEntity.FonteServico.CADASTRO)
                .dadosDelecao(DadosDelecao.createDefaultDadosDelecao())
                .build();
    }

    private Integer getVidaServicoFromPneu(final PneuEntity pneu) {
        return pneu.getVidaAtual() - 1;
    }
}
