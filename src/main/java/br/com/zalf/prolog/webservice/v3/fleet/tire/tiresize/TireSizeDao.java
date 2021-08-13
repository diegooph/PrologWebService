package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize;

import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireSizeEntity;
import br.com.zalf.prolog.webservice.v3.user.UserEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TireSizeDao extends JpaRepository<TireSizeEntity, Long> {
    @Query("select tse from TireSizeEntity tse " +
            "where tse.companyId = :companyId " +
            "and (:statusActive is null or tse.active = :statusActive)")
    List<TireSizeEntity> findAll(@NotNull final Long companyId, @Nullable final Boolean statusActive);

    @Modifying
    @Query("update TireSizeEntity tse set tse.active = :isActive, " +
            "tse.lastedUpdateUser = :userUpdating " +
            "where tse.companyId = :companyId " +
            "and tse.id = :tireSizeId")
    int updateStatus(@NotNull final Long companyId,
                     @NotNull final Long tireSizeId,
                     @NotNull final Boolean isActive,
                     @NotNull final UserEntity userUpdating);
}
