package br.com.zalf.prolog.webservice.geral.unidade;

import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEntity;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeVisualizacaoDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Repository
public interface UnidadeDao extends JpaRepository<UnidadeEntity, Long> {

    @Override
    @NotNull
    @EntityGraph(value = "graph.RegionalEmpresa", type = EntityGraph.EntityGraphType.LOAD)
    Optional<UnidadeEntity> findById(@NotNull final Long id);

    @NotNull
    @Query(name = "funcUnidadeVisualizacao", nativeQuery = true)
    UnidadeVisualizacaoDto getUnidadeByCodigo(@NotNull
                                              @Param("fCodUnidade") final Long codUnidade) throws Throwable;

    @NotNull
    @Query(name = "funcUnidadeListagem", nativeQuery = true)
    List<UnidadeVisualizacaoDto> getUnidadesListagem(@NotNull @Param("fCodEmpresa") final Long codEmpresa,
                                                     @Nullable @Param("fCodRegionais") final String codigosRegionais)
            throws Throwable;
}
