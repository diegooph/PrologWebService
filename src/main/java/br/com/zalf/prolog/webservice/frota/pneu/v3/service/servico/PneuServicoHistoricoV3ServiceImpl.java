package br.com.zalf.prolog.webservice.frota.pneu.v3.service.servico;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.PneuServicoEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3.dao.servico.ServicoHistoricoCadastroV3Dao;
import br.com.zalf.prolog.webservice.frota.pneu.v3.dao.servico.ServicoHistoricoVidaV3Dao;
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
public class PneuServicoHistoricoV3ServiceImpl implements PneuServicoHistoricoV3Service {

    private final ServicoHistoricoVidaV3Dao pneuServicoHistoricoVidaDao;
    private final ServicoHistoricoCadastroV3Dao pneuServicoHistoricoCadastroDao;

    @Autowired
    public PneuServicoHistoricoV3ServiceImpl(@NotNull final ServicoHistoricoVidaV3Dao pneuServicoHistoricoVidaDao,
                                             @NotNull final ServicoHistoricoCadastroV3Dao pneuServicoHistoricoCadastroDao) {
        this.pneuServicoHistoricoVidaDao = pneuServicoHistoricoVidaDao;
        this.pneuServicoHistoricoCadastroDao = pneuServicoHistoricoCadastroDao;
    }

    @Override
    @Transactional
    public void saveHistorico(@NotNull final PneuServicoEntity pneuServico) {
        this.pneuServicoHistoricoVidaDao.save(createHistoricoVida(pneuServico));
        if (isCadastro(pneuServico.getFonteServico())) {
            this.pneuServicoHistoricoCadastroDao.save(createHistoricoCadastro(pneuServico));
        }
    }

    private boolean isCadastro(final PneuServicoEntity.FonteServico fonteServico) {
        return fonteServico.equals(PneuServicoEntity.FonteServico.CADASTRO);
    }
}

