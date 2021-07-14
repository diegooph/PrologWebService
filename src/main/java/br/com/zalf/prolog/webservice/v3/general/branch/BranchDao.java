package br.com.zalf.prolog.webservice.v3.general.branch;

import br.com.zalf.prolog.webservice.v3.general.branch._model.UnidadeEntity;
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
public interface BranchDao extends JpaRepository<UnidadeEntity, Long> {
    @NotNull
    @Query("select u from UnidadeEntity u " +
                   "join fetch u.group " +
                   "join fetch u.companyEntity " +
                   "where u.id = :branchId")
    Optional<UnidadeEntity> getUnidadeByCod(@NotNull final Long branchId);

    @NotNull
    @Query("select u from UnidadeEntity u " +
                   "join fetch u.group g " +
                   "join fetch u.companyEntity c " +
                   "where c.id = :companyId " +
                   "and ((:groupsId) is null or g.id in (:groupsId))")
    List<UnidadeEntity> getUnidadesListagem(@NotNull final Long companyId,
                                            @Nullable final List<Long> groupsId);

    @NotNull
    @Query("select u from UnidadeEntity u " +
                   "join fetch u.companyEntity c " +
                   "where c.id = :companyId")
    List<UnidadeEntity> findAllByCodEmpresa(@NotNull final Long companyId);

    @NotNull
    @Query("select u from AuthenticationTokenEntity at " +
                   "join at.user u " +
                   "join u.empresa e " +
                   "join e.branches " +
                   "where at.token = :tokenUser")
    List<UnidadeEntity> findAllByTokenUser(@NotNull final String tokenUser);

    @NotNull
    @Query("select b from IntegrationTokenEntity it " +
                   "join it.company c " +
                   "join c.branches b " +
                   "where it.token = :tokenApi")
    List<UnidadeEntity> findAllByTokenApi(@NotNull final String tokenApi);
}