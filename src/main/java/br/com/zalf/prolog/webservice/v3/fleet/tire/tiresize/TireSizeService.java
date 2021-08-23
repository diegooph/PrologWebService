package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize;

import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeCreateDto;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeStatusChangeDto;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeUpdateDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class TireSizeService {
    @NotNull
    private final TireSizeDao dao;
    @NotNull
    private final TireSizeMapper mapper;

    @Autowired
    public TireSizeService(@NotNull final TireSizeDao dao, @NotNull final TireSizeMapper mapper) {
        this.dao = dao;
        this.mapper = mapper;
    }

    @NotNull
    public TireSizeEntity insert(@NotNull final TireSizeCreateDto tireSizeCreateDto,
                                 @NotNull final Optional<ColaboradorAutenticado> colaboradorAutenticado,
                                 @Nullable final Integer appVersion) {
        final OrigemAcaoEnum registerOrigin = getRegisterOrigin(colaboradorAutenticado, appVersion);
        final TireSizeEntity tireSizeEntity =
                mapper.toEntity(tireSizeCreateDto, colaboradorAutenticado, registerOrigin);
        return dao.save(tireSizeEntity);
    }

    @NotNull
    public List<TireSizeEntity> getAll(@NotNull final Long companyId, @Nullable final Boolean statusActive) {
        return dao.getAll(companyId, statusActive);
    }

    @NotNull
    public TireSizeEntity getById(@NotNull final Long companyId, @NotNull final Long tireSizeId) {
        return dao.getByCompanyIdAndTireSizeId(companyId, tireSizeId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("The tire size of id %d was not found!", tireSizeId)));
    }

    @Transactional
    public void updateStatus(@NotNull final TireSizeStatusChangeDto tireSizeStatusChangeDto,
                             @NotNull final Optional<ColaboradorAutenticado> colaboradorAutenticado) {
        final TireSizeEntity tireSizeEntity =
                getById(tireSizeStatusChangeDto.getCompanyId(), tireSizeStatusChangeDto.getTireSizeId());
        final TireSizeEntity updateTireSize =
                mapper.toTireSizeEntityUpdate(tireSizeStatusChangeDto, tireSizeEntity, colaboradorAutenticado);
        dao.save(updateTireSize);
    }

    @Transactional
    public void updateTireSize(@NotNull final TireSizeUpdateDto tireSizeUpdateDto,
                               @NotNull final Optional<ColaboradorAutenticado> colaboradorAutenticado) {
        final TireSizeEntity tireSize = getById(tireSizeUpdateDto.getCompanyId(), tireSizeUpdateDto.getId());
        final TireSizeEntity updateTireSize =
                mapper.toTireSizeEntityUpdate(tireSizeUpdateDto, tireSize, colaboradorAutenticado);
        dao.save(updateTireSize);
    }

    @NotNull
    private OrigemAcaoEnum getRegisterOrigin(@NotNull final Optional<ColaboradorAutenticado> colaboradorAutenticado,
                                             @Nullable final Integer appVersion) {
        return colaboradorAutenticado.isPresent()
                ? OrigemAcaoEnum.API
                : (appVersion != null ? OrigemAcaoEnum.PROLOG_ANDROID : OrigemAcaoEnum.PROLOG_WEB);
    }
}
