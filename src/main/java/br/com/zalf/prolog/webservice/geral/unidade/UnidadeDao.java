package br.com.zalf.prolog.webservice.geral.unidade;

import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEntity;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeProjection;
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
    @Query(value = "select * " +
            "from func_unidade_listagem(" +
            "f_cod_empresa => :codEmpresa," +
            "f_cod_regionais => to_bigint_array(:codRegionais));", nativeQuery = true)
    List<UnidadeProjection> getUnidadesListagem(@NotNull final Long codEmpresa,
                                                @Nullable final List<Long> codRegionais);
}
