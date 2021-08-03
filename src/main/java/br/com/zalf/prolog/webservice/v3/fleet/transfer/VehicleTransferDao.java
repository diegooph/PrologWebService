package br.com.zalf.prolog.webservice.v3.fleet.transfer;

import br.com.zalf.prolog.webservice.v3.fleet.transfer._model.VehicleTransferProcessEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface VehicleTransferDao extends JpaRepository<VehicleTransferProcessEntity, Long> {

}
