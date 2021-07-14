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
                   "join fetch u.grupo g " +
                   "join fetch u.empresaEntity e " +
                   "where u.codigo = :codUnidade")
    Optional<UnidadeEntity> getUnidadeByCod(@NotNull final Long codUnidade);

    @NotNull
    @Query("select u from UnidadeEntity u " +
                   "join fetch u.grupo g " +
                   "join fetch u.empresaEntity e " +
                   "where e.codigo = :codEmpresa " +
                   "and ((:codRegionais) is null or g.codigo in (:codRegionais))")
    List<UnidadeEntity> getUnidadesListagem(@NotNull final Long codEmpresa,
                                            @Nullable final List<Long> codRegionais);

    @NotNull
    @Query("select u from UnidadeEntity u " +
                   "join fetch u.empresaEntity e " +
                   "where e.codigo = :codEmpresa")
    List<UnidadeEntity> findAllByCodEmpresa(@NotNull final Long codEmpresa);

    @NotNull
    @Query("select u from TokenAuthenticationEntity ta " +
                   "join ta.colaborador c " +
                   "join c.empresa e " +
                   "join e.unidades u " +
                   "where ta.token = :tokenUser")
    List<UnidadeEntity> findAllByTokenUser(@NotNull final String tokenUser);

    @NotNull
    @Query("select u from TokenIntegrationEntity tai " +
                   "join tai.empresa e " +
                   "join e.unidades u " +
                   "where tai.token = :tokenApi")
    List<UnidadeEntity> findAllByTokenApi(@NotNull final String tokenApi);
}