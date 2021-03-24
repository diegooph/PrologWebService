package br.com.zalf.prolog.webservice.frota.pneu.v3.service.servico;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.PneuServicoRealizadoEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.PneuServicoCadastroEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.PneuServicoRealizadoIncrementaVidaEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3.dao.servico.PneuServicoCadastroV3Dao;
import br.com.zalf.prolog.webservice.frota.pneu.v3.dao.servico.PneuServicoRealizadoIncrementaVidaV3Dao;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created on 2021-03-19
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Service
public class PneuServicoHistoricoV3Service {

    private final PneuServicoRealizadoIncrementaVidaV3Dao pneuServicoHistoricoVidaDao;
    private final PneuServicoCadastroV3Dao pneuServicoHistoricoCadastroDao;

    @Autowired
    public PneuServicoHistoricoV3Service(@NotNull final PneuServicoRealizadoIncrementaVidaV3Dao pneuServicoHistoricoVidaDao,
                                         @NotNull final PneuServicoCadastroV3Dao pneuServicoHistoricoCadastroDao) {
        this.pneuServicoHistoricoVidaDao = pneuServicoHistoricoVidaDao;
        this.pneuServicoHistoricoCadastroDao = pneuServicoHistoricoCadastroDao;
    }

    @Transactional
    public void saveHistorico(@NotNull final PneuServicoRealizadoEntity pneuServico) {
        this.pneuServicoHistoricoVidaDao.save(createHistoricoVida(pneuServico));
        if (isCadastro(pneuServico.getFonteServico())) {
            this.pneuServicoHistoricoCadastroDao.save(createHistoricoCadastro(pneuServico));
        }
    }

    private PneuServicoRealizadoIncrementaVidaEntity createHistoricoVida(final PneuServicoRealizadoEntity pneuServico) {
        final PneuServicoRealizadoIncrementaVidaEntity.Id id = PneuServicoRealizadoIncrementaVidaEntity.Id.builder()
                .servico(pneuServico)
                .build();
        return PneuServicoRealizadoIncrementaVidaEntity.builder()
                .id(id)
                .codModeloBanda(pneuServico.getPneu().getCodModeloBanda())
                .vidaNova(pneuServico.getPneu().getVidaAtual())
                .build();
    }

    private boolean isCadastro(final PneuServicoRealizadoEntity.FonteServico fonteServico) {
        return fonteServico.equals(PneuServicoRealizadoEntity.FonteServico.CADASTRO);
    }

    private PneuServicoCadastroEntity createHistoricoCadastro(final PneuServicoRealizadoEntity pneuServico) {
        final PneuServicoCadastroEntity.Id id = PneuServicoCadastroEntity.Id.builder()
                .pneu(pneuServico.getPneu())
                .servico(pneuServico)
                .build();
        return PneuServicoCadastroEntity.builder()
                .id(id)
                .build();
    }
}
