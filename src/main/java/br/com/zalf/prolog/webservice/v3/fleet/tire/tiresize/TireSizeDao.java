package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize;

import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireSizeEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TireSizeDao extends JpaRepository<TireSizeEntity, Long> {
    @Query("select tse from TireSizeEntity tse " +
            "where tse.companyId = :companyId " +
            "and (:includeInactive = true or tse.active = true)")
    List<TireSizeEntity> findAll(@NotNull final Long companyId, @NotNull final Boolean includeInactive);
}
