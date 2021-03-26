package br.com.zalf.prolog.webservice.frota.pneu.v3.service;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.dto.PneuCadastroDto;
import br.com.zalf.prolog.webservice.frota.pneu.v3.dao.PneuV3Dao;
import br.com.zalf.prolog.webservice.frota.pneu.v3.mapper.PneuCadastroMapper;
import br.com.zalf.prolog.webservice.frota.pneu.v3.service.servico.PneuServicoV3Service;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.interceptors.v3.OperacoesBloqueadasYaml;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created on 2021-03-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Service
public class PneuV3Service {
    @NotNull
    private static final String TAG = PneuV3Service.class.getSimpleName();
    @NotNull
    private final PneuV3Dao pneuDao;
    @NotNull
    private final OperacoesBloqueadasYaml operacoesBloqueadas;
    @NotNull
    private final PneuCadastroMapper pneuCadastroMapper;
    @NotNull
    private final PneuServicoV3Service pneuServicoService;

    @Autowired
    public PneuV3Service(@NotNull final PneuV3Dao pneuDao,
                         @NotNull final PneuServicoV3Service pneuServicoService,
                         @NotNull final OperacoesBloqueadasYaml operacoesBloqueadas,
                         @NotNull final PneuCadastroMapper pneuCadastroMapper) {
        this.pneuDao = pneuDao;
        this.pneuServicoService = pneuServicoService;
        this.operacoesBloqueadas = operacoesBloqueadas;
        this.pneuCadastroMapper = pneuCadastroMapper;
    }

    @NotNull
    @Transactional
    public SuccessResponse insert(@Nullable final String tokenIntegracao,
                                  @NotNull final PneuCadastroDto pneuCadastroDto) {
        try {
            final PneuEntity pneuEntity = this.pneuCadastroMapper.toEntity(pneuCadastroDto);
            final PneuEntity pneuInsert = pneuEntity.toBuilder()
                    .origemCadastro(getOrigemCadastro(tokenIntegracao))
                    .build();
            final PneuEntity savedPneu = this.pneuDao.save(pneuInsert);
            if (savedPneu.isRecapado()) {
                //noinspection ConstantConditions
                this.pneuServicoService.createServicoByPneu(savedPneu, pneuCadastroDto.getValorBandaPneu());
            }
            return new SuccessResponse(savedPneu.getCodigo(), "Pneu inserido com sucesso.");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir pneu.", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir pneu, tente novamente.");
        }
    }

    @NotNull
    private OrigemAcaoEnum getOrigemCadastro(@Nullable final String tokenIntegracao) {
        return tokenIntegracao != null ? OrigemAcaoEnum.API : OrigemAcaoEnum.PROLOG_WEB;
    }
}

