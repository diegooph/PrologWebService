package br.com.zalf.prolog.webservice.v3.geral.unidade;

import br.com.zalf.prolog.webservice.v3.geral.unidade._model.UnidadeEntity;
import br.com.zalf.prolog.webservice.v3.geral.unidade._model.UnidadeProjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Repository
public interface UnidadeDao extends JpaRepository<UnidadeEntity, Long> {

    @NotNull
    @Query(value = "select * from func_unidade_visualizacao(:codUnidade);", nativeQuery = true)
    UnidadeProjection getUnidadeByCodigo(@NotNull final Long codUnidade);

    @NotNull
    @Query("select u from UnidadeEntity u " +
                   " join fetch u.grupo g " +
                   " join fetch u.empresaEntity e " +
                   " where (:codRegionais is null or g.codigo in :codRegionais) " +
                   " and e.codigo = :codEmpresa")
    List<UnidadeEntity> getUnidadesListagem(@NotNull final Long codEmpresa,
                                            @Nullable final List<Long> codRegionais);

    @NotNull
    @Query("select u from UnidadeEntity u " +
                   " join fetch u.empresaEntity e " +
                   " where e.codigo = :codEmpresa")
    List<UnidadeEntity> findAllByCodEmpresa(@NotNull final Long codEmpresa);

    @NotNull
    @Query("select u from TokenAutenticacaoEntity ta " +
               " join ta.colaborador c " +
               " join c.unidade u " +
               " where ta.token = :tokenUser")
    List<UnidadeEntity> findAllByTokenUser(@NotNull final String tokenUser);

    @NotNull
    @Query(value = "select u.* from unidade u " +
            "where u.cod_empresa = (select ti.cod_empresa " +
            "from integracao.token_integracao ti " +
            "where ti.token_integracao = :tokenApi);", nativeQuery = true)
    List<UnidadeEntity> findAllByTokenApi(@NotNull final String tokenApi);
}