package br.com.zalf.prolog.webservice.v3.fleet.pneu.pneuservico.tiposervico;

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
public interface PneuTipoServicoDao extends JpaRepository<PneuTipoServicoEntity, Long> {
    @NotNull
    @Query("select pts from PneuTipoServicoEntity pts " +
                   "where pts.codEmpresa is null " +
                   "and pts.statusAtivo = true " +
                   "and pts.incrementaVida = true " +
                   "and pts.utilizadoCadastroPneu = true")
    PneuTipoServicoEntity getTipoServicoIncrementaVidaCadastroPneu();
}
