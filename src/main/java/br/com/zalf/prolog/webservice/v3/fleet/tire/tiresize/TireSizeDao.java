package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize;

import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireSizeEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TireSizeDao extends JpaRepository<TireSizeEntity, Long> {
    List<TireSizeEntity> findAllByCompanyIdAndActiveIsTrue(@NotNull final Long companyId);
}
