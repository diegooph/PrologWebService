package br.com.zalf.prolog.webservice.v3.frota.checklist;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface ChecklistDao extends JpaRepository<ChecklistEntity, Long> {

}
