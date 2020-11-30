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
    Optional<UnidadeEntity> findById(Long id);

    /**
     * Busca uma unidade baseado no seu código.
     *
     * @param codUnidade um código de uma unidade.
     * @return uma {@link UnidadeVisualizacaoDto unidade}.
     *
     * @throws Throwable caso qualquer erro ocorrer.
     */
    @NotNull
    @Query(name = "funcUnidadeVisualizacao", nativeQuery = true)
    UnidadeVisualizacaoDto getUnidadeByCodigo(@NotNull
                                              @Param("fCodUnidade") final Long codUnidade) throws Throwable;

    /**
     * Busca todas as unidades baseado no código da empresa e da regional.
     * <p>
     * A lista de códigos de regionais pode ser {@code null}, significando que o usuário quer trazer de todas as
     * regionais.
     *
     * @param codEmpresa       um código de uma empresa;
     * @param codigosRegionais códigos das regionais para as quais se quer filtrar, ou {@code null}.
     * @return uma {@link List< UnidadeVisualizacaoDto > lista de unidades}.
     *
     * @throws Throwable caso qualquer erro ocorrer.
     */
    @NotNull
    @Query(name = "funcUnidadeListagem", nativeQuery = true)
    List<UnidadeVisualizacaoDto> getUnidadesListagem(@NotNull
                                                     @Param("fCodEmpresa") final Long codEmpresa,
                                                     @Nullable
                                                     @Param("fCodRegionais") final List<Long> codigosRegionais)
            throws Throwable;
}
