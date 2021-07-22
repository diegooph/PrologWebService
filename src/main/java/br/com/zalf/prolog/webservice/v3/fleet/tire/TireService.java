package br.com.zalf.prolog.webservice.v3.fleet.tire;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.error.PneuValidator;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.integracao.BlockedOperationYaml;
import br.com.zalf.prolog.webservice.v3.OffsetBasedPageRequest;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.PneuEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireCreateDto;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireDto;
import br.com.zalf.prolog.webservice.v3.fleet.tire.pneuservico.PneuServicoService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TireService {
    @NotNull
    private final TireDao tireDao;
    @NotNull
    private final TireMapper tireMapper;
    @NotNull
    private final PneuServicoService pneuServicoService;
    @NotNull
    private final BlockedOperationYaml operacoesBloqueadas;

    @NotNull
    @Transactional
    public SuccessResponse insert(@Nullable final String integrationToken,
                                  @NotNull final TireCreateDto tireCreateDto,
                                  final boolean ignoreDotValidation) throws Throwable {
        operacoesBloqueadas.validateBlockedCompanyBranch(tireCreateDto.getCompanyId(),
                                                         tireCreateDto.getBranchId());
        validateTire(tireCreateDto, ignoreDotValidation);
        final PneuEntity pneuInsert = tireMapper.toEntity(tireCreateDto, getRegisterOrigin(integrationToken));
        final PneuEntity savedPneu = tireDao.save(pneuInsert);
        if (savedPneu.isRecapado()) {
            //noinspection ConstantConditions
            pneuServicoService.insertServicoCadastroPneu(savedPneu, tireCreateDto.getTireTreadPrice());
        }
        return new SuccessResponse(savedPneu.getCodigo(), "Pneu inserido com sucesso.");
    }

    @Transactional
    @NotNull
    public List<TireDto> getAllTires(@NotNull final List<Long> branchesId,
                                     @Nullable final StatusPneu tireStatus,
                                     final int limit,
                                     final int offset) {
        final List<PneuEntity> pneusByStatus =
                tireDao.getAllTires(branchesId, tireStatus, OffsetBasedPageRequest.of(limit, offset, Sort.unsorted()));
        return tireMapper.toDto(pneusByStatus);
    }

    @NotNull
    @Transactional
    public SuccessResponse updateTireStatus(@NotNull final Long tireId,
                                            @NotNull final StatusPneu tireStatus) {
        tireDao.updateTireStatus(tireId, tireStatus);
        return new SuccessResponse(tireId, "Alterado o status do pneu com sucesso.");
    }

    private void validateTire(@NotNull final TireCreateDto tireCreateDto,
                              final boolean ignoreDotValidation) throws Throwable {
        PneuValidator.validacaoVida(tireCreateDto.getTimesRetreaded(), tireCreateDto.getMaxRetreads());
        PneuValidator.validacaoVidaPneuNovoNuncaRodado(tireCreateDto.getTimesRetreaded(), tireCreateDto.getIsTireNew());
        if (!ignoreDotValidation) {
            PneuValidator.validacaoDot(tireCreateDto.getTireDot());
        }
        if (tireCreateDto.getTimesRetreaded() > 1) {
            PneuValidator.validacaoModeloDaBanda(tireCreateDto.getTireTreadId());
            PneuValidator.validacaoValorDaBanda(tireCreateDto.getTireTreadPrice());
        }
    }

    @NotNull
    private OrigemAcaoEnum getRegisterOrigin(@Nullable final String integrationToken) {
        return integrationToken != null ? OrigemAcaoEnum.API : OrigemAcaoEnum.PROLOG_WEB;
    }
}

