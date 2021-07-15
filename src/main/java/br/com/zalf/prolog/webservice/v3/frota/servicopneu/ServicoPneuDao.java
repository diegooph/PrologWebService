package br.com.zalf.prolog.webservice.v3.frota.servicopneu;

import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Repository
public interface ServicoPneuDao extends JpaRepository<ServicoPneuEntity, Long> {
    @Query("select sp " +
                   "from ServicoPneuEntity sp join fetch sp.pneu p                                               " +
                   "                          join fetch sp.afericao a                                           " +
                   "                          join fetch a.veiculo v                                             " +
                   "                          join fetch a.valoresAfericao va                                    " +
                   "                          join fetch va.pneu vapkp                                           " +
                   "                          left join fetch sp.mecanico m                                      " +
                   "                          left join fetch sp.alternativa al                                  " +
                   "where sp.codUnidade in :codUnidades                                                          " +
                   "and (:filtroFechado is null or (:filtroFechado = true                                        " +
                   "                                   and (sp.fechadoAfericao = true                            " +
                   "                                   or sp.fechadoIntegracao = true                            " +
                   "                                   or sp.fechadoMovimentacao = true or m is not null ))      " +
                   "                                or (:filtroFechado = false                                   " +
                   "                                   and (sp.fechadoAfericao = false                           " +
                   "                                        and sp.fechadoIntegracao = false                     " +
                   "                                        and sp.fechadoMovimentacao = false and m is null ))) " +
                   "and (:codVeiculo is null or :codVeiculo = v.codigo)                                          " +
                   "and (:codPneu is null or :codPneu = p.codigo)                                                ")
    @NotNull
    List<ServicoPneuEntity> findServicosPneuByUnidades(@NotNull final List<Long> codUnidades,
                                                       @Nullable final Long codVeiculo,
                                                       @Nullable final Long codPneu,
                                                       @Nullable final Boolean filtroFechado,
                                                       @NotNull final Pageable pageable);
}