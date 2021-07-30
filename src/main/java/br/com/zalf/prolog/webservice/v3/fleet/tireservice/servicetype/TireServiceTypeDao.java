package br.com.zalf.prolog.webservice.v3.fleet.tireservice.servicetype;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Repository
public interface TireServiceTypeDao extends JpaRepository<TireServiceTypeEntity, Long> {
    @NotNull
    @Query("select tst from TireServiceTypeEntity tst " +
                   "where tst.companyId is null " +
                   "and tst.isActive = true " +
                   "and tst.increaseLifeCycle = true " +
                   "and tst.usedInTireRegister = true")
    TireServiceTypeEntity getTireServiceTypeIncreaseLifeCycle();
}
