package br.com.zalf.prolog.webservice.v3.frota.pneu;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.error.PneuValidator;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizado;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.integracao.OperacoesBloqueadasYaml;
import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuCadastroDto;
import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuEntity;
import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuListagemDto;
import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico.PneuServicoService;
import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico.tiposervico._modal.PneuTipoServicoEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created on 2021-03-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Service
public class PneuService {
    @NotNull
    private static final String TAG = PneuService.class.getSimpleName();
    @NotNull
    private final PneuDao pneuDao;
    @NotNull
    private final PneuMapper pneuMapper;
    @NotNull
    private final PneuServicoService pneuServicoService;
    @NotNull
    private final OperacoesBloqueadasYaml operacoesBloqueadas;

    @Autowired
    public PneuService(@NotNull final PneuDao pneuDao,
                       @NotNull final PneuServicoService pneuServicoService,
                       @NotNull final PneuMapper pneuMapper,
                       @NotNull final OperacoesBloqueadasYaml operacoesBloqueadas) {
        this.pneuDao = pneuDao;
        this.pneuServicoService = pneuServicoService;
        this.operacoesBloqueadas = operacoesBloqueadas;
        this.pneuMapper = pneuMapper;
    }

    @NotNull
    @Transactional
    public SuccessResponse insert(@Nullable final String tokenIntegracao,
                                  @NotNull final PneuCadastroDto pneuCadastroDto,
                                  final boolean ignoreDotValidation) {
        try {
            operacoesBloqueadas.validateEmpresaUnidadeBloqueada(pneuCadastroDto.getCodEmpresaAlocado(),
                    pneuCadastroDto.getCodUnidadeAlocado());
            validatePneu(pneuCadastroDto, ignoreDotValidation);
            final PneuEntity pneuEntity = pneuMapper.toEntity(pneuCadastroDto);
            final PneuEntity pneuInsert = pneuEntity.toBuilder()
                    .origemCadastro(getOrigemCadastro(tokenIntegracao))
                    .build();
            final PneuEntity savedPneu = pneuDao.save(pneuInsert);
            if (savedPneu.isRecapado()) {
                //noinspection ConstantConditions
                final PneuTipoServicoEntity tipoServicoIncrementaVidaPneu =
                        this.pneuServicoService.getPneuTipoServicoEntity();
                this.pneuServicoService.insertServicoPneu(savedPneu, pneuCadastroDto.getValorBandaPneu(),
                        tipoServicoIncrementaVidaPneu,
                        PneuServicoRealizado.FONTE_CADASTRO);
            }
            return new SuccessResponse(savedPneu.getCodigo(), "Pneu inserido com sucesso.");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir pneu.", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir pneu, tente novamente.");
        }
    }

    @Transactional
    @NotNull
    public List<PneuListagemDto> getPneusByStatus(@NotNull final List<Long> codUnidades,
                                                  @Nullable final StatusPneu statusPneu,
                                                  final int limit,
                                                  final int offset) {
        try {
            final List<PneuEntity> pneusByStatus =
                    pneuDao.getPneusByStatus(codUnidades, statusPneu, PageRequest.of(offset, limit));
            return pneuMapper.toPneuListagemDto(pneusByStatus);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar pneus.", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar pneus, tente novamente.");
        }
    }

    private void validatePneu(@NotNull final PneuCadastroDto pneuCadastroDto,
                              final boolean ignoreDotValidation) throws Throwable {
        PneuValidator.validacaoVida(pneuCadastroDto.getVidaAtualPneu(), pneuCadastroDto.getVidaTotalPneu());
        PneuValidator.validacaoVidaPneuNovoNuncaRodado(pneuCadastroDto.getVidaAtualPneu(),
                                                       pneuCadastroDto.getPneuNovoNuncaRodado());
        if (!ignoreDotValidation) {
            PneuValidator.validacaoDot(pneuCadastroDto.getDotPneu());
        }
        if (pneuCadastroDto.getVidaAtualPneu() > 1) {
            PneuValidator.validacaoModeloDaBanda(pneuCadastroDto.getCodModeloBanda());
            PneuValidator.validacaoValorDaBanda(pneuCadastroDto.getValorBandaPneu());
        }
    }

    @NotNull
    private OrigemAcaoEnum getOrigemCadastro(@Nullable final String tokenIntegracao) {
        return tokenIntegracao != null ? OrigemAcaoEnum.API : OrigemAcaoEnum.PROLOG_WEB;
    }

    @NotNull
    @Transactional
    public SuccessResponse updateStatusPneu(@NotNull final Long codPneu,
                                  @NotNull final StatusPneu statusPneu) {
        try {
            pneuDao.updateStatus(codPneu, statusPneu.valueOf(statusPneu.toString()));
            return new SuccessResponse(codPneu, "Alterado o status do pneu com sucesso.");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao atualizar o status do pneu.", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao atualizar o status do pneu, tente novamente.");
        }
    }
}

