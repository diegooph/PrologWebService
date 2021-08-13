package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize;

import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireSizeEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeCreation;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeStatusChange;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeUpdating;
import br.com.zalf.prolog.webservice.v3.user.UserEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TireSizeService {
    @NotNull
    private final TireSizeDao dao;
    @NotNull
    private final TireSizeMapper mapper;

    @Autowired
    public TireSizeService(@NotNull final TireSizeDao dao,
                           @NotNull final TireSizeMapper mapper) {
        this.dao = dao;
        this.mapper = mapper;
    }

    @NotNull
    public TireSizeEntity insert(@NotNull final TireSizeCreation tireSizeCreation,
                                 @NotNull final ColaboradorAutenticado colaboradorAutenticado) {
        return dao.save(mapper.toEntity(tireSizeCreation, colaboradorAutenticado));
    }

    public List<TireSizeEntity> getAll(@NotNull final Long companyId,
                                       @Nullable final Boolean statusActive) {
        return dao.findAll(companyId, statusActive);
    }

    @Transactional
    public void updateStatus(@NotNull final TireSizeStatusChange tireSizeStatusChange,
                             @NotNull final ColaboradorAutenticado colaboradorAutenticado) {
        final int rowsUpdated = dao.updateStatus(
                tireSizeStatusChange.getCompanyId(),
                tireSizeStatusChange.getTireSizeId(),
                tireSizeStatusChange.getActive(),
                UserEntity.builder()
                        .withId(colaboradorAutenticado.getCodigo())
                        .build(),
                LocalDateTime.now()
        );
        if (rowsUpdated == 0) {
            throw new EntityNotFoundException(
                    String.format("The tire size of id %d was not found!", tireSizeStatusChange.getTireSizeId()));
        }
    }

    @Transactional
    public TireSizeEntity updateTireSize(@NotNull final TireSizeUpdating tireSizeUpdating,
                                         @NotNull final ColaboradorAutenticado colaboradorAutenticado) {
        final TireSizeEntity tireSize =
                dao.findByCompanyIdAndId(tireSizeUpdating.getCompanyId(), tireSizeUpdating.getTireSizeId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                String.format(
                                        "The tire size of id %d was not found!",
                                        tireSizeUpdating.getTireSizeId())));
        tireSize.setHeight(tireSizeUpdating.getTireSizeHeight());
        tireSize.setWidth(tireSizeUpdating.getTireSizeWidth());
        tireSize.setRim(tireSizeUpdating.getTireSizeRim());
        tireSize.setAdditionalId(tireSizeUpdating.getAdditionalId());
        tireSize.setActive(tireSizeUpdating.getActive());
        tireSize.setLastedUpdateUser(
                UserEntity.builder()
                        .withId(colaboradorAutenticado.getCodigo())
                        .build()
        );
        tireSize.setLastedUpdateAt(LocalDateTime.now());
        return dao.save(tireSize);
    }
}
