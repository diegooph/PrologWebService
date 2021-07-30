package br.com.zalf.prolog.webservice.v3.fleet.tire;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.error.PneuValidator;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizado;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.integracao.BlockedOperationYaml;
import br.com.zalf.prolog.webservice.v3.OffsetBasedPageRequest;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireCreateDto;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireDto;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice.TireServiceService;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice.servicetype.TireServiceTypeEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice.servicetype.TireServiceTypeService;
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
    private final TireServiceService tireServiceService;
    @NotNull
    private final TireServiceTypeService tireServiceTypeService;
    @NotNull
    private final BlockedOperationYaml operacoesBloqueadas;

    @NotNull
    @Transactional
    public SuccessResponse insert(@Nullable final String integrationToken,
                                  @NotNull final TireCreateDto tireCreateDto,
                                  final boolean ignoreDotValidation) throws Throwable {
        operacoesBloqueadas.validateBlockedCompanyBranch(tireCreateDto.getCodEmpresaAlocado(),
                                                         tireCreateDto.getCodUnidadeAlocado());
        validateTire(tireCreateDto, ignoreDotValidation);
        final TireEntity tireInsert = tireMapper.toEntity(tireCreateDto, getRegisterOrigin(integrationToken));
        final TireEntity savedTire = tireDao.save(tireInsert);
        if (savedTire.isRetreaded()) {
            final TireServiceTypeEntity tireServiceTypeIncreaseLifeCycle =
                    tireServiceTypeService.getTireServiceTypeIncreaseLifeCycle();
            //noinspection ConstantConditions
            tireServiceService.insertTireService(savedTire,
                                                 tireCreateDto.getValorBandaPneu(),
                                                 tireServiceTypeIncreaseLifeCycle,
                                                 PneuServicoRealizado.FONTE_CADASTRO);
        }
        return new SuccessResponse(savedTire.getId(), "Pneu inserido com sucesso.");
    }

    @Transactional
    @NotNull
    public List<TireDto> getAllTires(@NotNull final List<Long> branchesId,
                                     @Nullable final StatusPneu tireStatus,
                                     final int limit,
                                     final int offset) {
        final List<TireEntity> pneusByStatus =
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
        PneuValidator.validacaoVida(tireCreateDto.getVidaAtualPneu(), tireCreateDto.getVidaTotalPneu());
        PneuValidator.validacaoVidaPneuNovoNuncaRodado(tireCreateDto.getVidaAtualPneu(),
                                                       tireCreateDto.getPneuNovoNuncaRodado());
        if (!ignoreDotValidation) {
            PneuValidator.validacaoDot(tireCreateDto.getDotPneu());
        }
        if (tireCreateDto.getVidaAtualPneu() > 1) {
            PneuValidator.validacaoModeloDaBanda(tireCreateDto.getCodModeloBanda());
            PneuValidator.validacaoValorDaBanda(tireCreateDto.getValorBandaPneu());
        }
    }

    @NotNull
    private OrigemAcaoEnum getRegisterOrigin(@Nullable final String integrationToken) {
        return integrationToken != null ? OrigemAcaoEnum.API : OrigemAcaoEnum.PROLOG_WEB;
    }
}

