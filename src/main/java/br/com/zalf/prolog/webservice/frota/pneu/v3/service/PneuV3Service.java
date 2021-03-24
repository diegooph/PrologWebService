package br.com.zalf.prolog.webservice.frota.pneu.v3.service;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.dto.PneuCadastroDto;
import br.com.zalf.prolog.webservice.frota.pneu.v3.dao.PneuV3Dao;
import br.com.zalf.prolog.webservice.frota.pneu.v3.mapper.PneuCadastroMapper;
import br.com.zalf.prolog.webservice.frota.pneu.v3.service.servico.PneuServicoV3Service;
import br.com.zalf.prolog.webservice.interceptors.v3.OperacoesBloqueadasYaml;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.transaction.Transactional;

/**
 * Created on 2021-03-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Service
public class PneuV3Service {

    private static final String TAG = PneuV3Service.class.getSimpleName();

    private final PneuV3Dao dao;
    private final OperacoesBloqueadasYaml operacoesBloqueadas;
    private final PneuCadastroMapper pneuCadastroMapper;
    private final PneuServicoV3Service pneuServicoV3Service;

    @Autowired
    public PneuV3Service(@NotNull final PneuV3Dao dao,
                         @NotNull final OperacoesBloqueadasYaml operacoesBloqueadas,
                         @NotNull final PneuCadastroMapper pneuCadastroMapper,
                         @NotNull final PneuServicoV3Service pneuServicoV3Service) {
        this.dao = dao;
        this.operacoesBloqueadas = operacoesBloqueadas;
        this.pneuCadastroMapper = pneuCadastroMapper;
        this.pneuServicoV3Service = pneuServicoV3Service;
    }

    @NotNull
    @Transactional
    public SuccessResponse insert(@NotNull final PneuCadastroDto pneuCadastroDto) {
        try {
            final PneuEntity pneu = this.pneuCadastroMapper.toEntity(pneuCadastroDto);
            this.operacoesBloqueadas.validateEmpresa(pneu.getCodEmpresa());
            this.operacoesBloqueadas.validateUnidade(pneu.getCodUnidade());
            validatePneuToInsert(pneu);
            final PneuEntity savedPneu = this.dao.save(pneu);
            if (savedPneu.isRecapado()) {
                this.pneuServicoV3Service.createServicoByPneu(savedPneu, pneuCadastroDto.getCustoAquisicaoBanda());
            }
            return new SuccessResponse(savedPneu.getId(), "Pneu inserido com sucesso.");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir pneu.", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir pneu, tente novamente.");
        }
    }

    private void validatePneuToInsert(@NotNull final PneuEntity pneu) {
        if (this.dao.exists(Example.of(pneu))) {
            throw new EntityExistsException("Pneu já existe no banco de dados.");
        }
    }
}

