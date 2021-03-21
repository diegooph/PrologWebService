package br.com.zalf.prolog.webservice.frota.pneu.v3.dao.servico;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.PneuTipoServicoEntity;
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
public interface PneuTipoServicoV3Dao extends JpaRepository<PneuTipoServicoEntity, Long> {

    @Query("select pts from PneuTipoServicoEntity pts " +
                   "where pts.codEmpresa is null " +
                   "and pts.ativo = true " +
                   "and pts.incremental = true " +
                   "and pts.utilizadoCadastroPneu = true")
    @NotNull
    PneuTipoServicoEntity getInitialTipoServicoForVidaIncrementada();

}
