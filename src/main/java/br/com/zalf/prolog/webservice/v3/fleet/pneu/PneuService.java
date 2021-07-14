package br.com.zalf.prolog.webservice.v3.fleet.pneu;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.error.PneuValidator;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.integracao.OperacoesBloqueadasYaml;
import br.com.zalf.prolog.webservice.v3.OffsetBasedPageRequest;
import br.com.zalf.prolog.webservice.v3.fleet.pneu._model.PneuCadastroDto;
import br.com.zalf.prolog.webservice.v3.fleet.pneu._model.PneuEntity;
import br.com.zalf.prolog.webservice.v3.fleet.pneu._model.PneuListagemDto;
import br.com.zalf.prolog.webservice.v3.fleet.pneu.pneuservico.PneuServicoService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
                                  final boolean ignoreDotValidation) throws Throwable {
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
            this.pneuServicoService.insertServicoCadastroPneu(savedPneu, pneuCadastroDto.getValorBandaPneu());
        }
        return new SuccessResponse(savedPneu.getCodigo(), "Pneu inserido com sucesso.");
    }

    @Transactional
    @NotNull
    public List<PneuListagemDto> getPneusByStatus(@NotNull final List<Long> codUnidades,
                                                  @Nullable final StatusPneu statusPneu,
                                                  final int limit,
                                                  final int offset) {
        final List<PneuEntity> pneusByStatus =
                pneuDao.getPneusByStatus(codUnidades,
                                         statusPneu,
                                         OffsetBasedPageRequest.of(limit, offset, Sort.unsorted()));
        return pneuMapper.toPneuListagemDto(pneusByStatus);
    }

    @NotNull
    @Transactional
    public SuccessResponse updateStatusPneu(@NotNull final Long codPneu,
                                            @NotNull final StatusPneu statusPneu) {
        pneuDao.updateStatus(codPneu, StatusPneu.valueOf(statusPneu.toString()));
        return new SuccessResponse(codPneu, "Alterado o status do pneu com sucesso.");
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
}

