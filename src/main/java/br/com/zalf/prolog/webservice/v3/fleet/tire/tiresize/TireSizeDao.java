package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize;

import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TireSizeDao extends JpaRepository<TireSizeEntity, Long> {
    @NotNull
    @Query("select tse from TireSizeEntity tse " +
            "where tse.companyId = :companyId " +
            "and (:statusActive is null or tse.isActive = :statusActive) " +
            "order by tse.isActive desc, tse.id")
    List<TireSizeEntity> getAll(@NotNull final Long companyId, @Nullable final Boolean statusActive);

    @NotNull
    @Query("select tse from TireSizeEntity tse " +
            "left join fetch tse.createByUser cbu " +
            "left join fetch tse.lastedUpdateUser luu " +
            "where tse.companyId = :companyId " +
            "and tse.id = :id")
    Optional<TireSizeEntity> getByCompanyIdAndTireSizeId(@NotNull final Long companyId, @NotNull final Long id);
}
