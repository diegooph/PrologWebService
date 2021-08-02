package br.com.zalf.prolog.webservice.v3.general.branch;

import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Repository
public interface BranchDao extends JpaRepository<BranchEntity, Long> {
    @NotNull
    @Query("select u from BranchEntity u " +
                   "join fetch u.groupEntity " +
                   "join fetch u.companyEntity " +
                   "where u.id = :branchId")
    Optional<BranchEntity> getBranchById(@NotNull final Long branchId);

    @NotNull
    @Query("select u from BranchEntity u " +
                   "join fetch u.groupEntity g " +
                   "join fetch u.companyEntity c " +
                   "where c.id = :companyId " +
                   "and ((:groupsId) is null or g.id in (:groupsId))")
    List<BranchEntity> getAllBranches(@NotNull final Long companyId,
                                      @Nullable final List<Long> groupsId);

    @NotNull
    @Query("select b from AuthenticationTokenEntity at " +
                   "join at.user u " +
                   "join u.companyEntity c " +
                   "join c.branches b " +
                   "where at.token = :tokenUser")
    List<BranchEntity> findAllByTokenUser(@NotNull final String tokenUser);

    @NotNull
    @Query("select b from IntegrationTokenEntity it " +
                   "join it.company c " +
                   "join c.branches b " +
                   "where it.token = :tokenApi")
    List<BranchEntity> findAllByTokenApi(@NotNull final String tokenApi);
}