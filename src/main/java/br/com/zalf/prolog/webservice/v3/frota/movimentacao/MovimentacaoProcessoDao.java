package br.com.zalf.prolog.webservice.v3.frota.movimentacao;

import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoProcessoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface MovimentacaoProcessoDao extends JpaRepository<MovimentacaoProcessoEntity, Long> {
    @Query("select distinct mpe from MovimentacaoProcessoEntity mpe "
                   + "inner join fetch mpe.colaboradorRealizacaoProcesso "
                   + "inner join fetch mpe.movimentacoes m "
                   + "inner join fetch m.movimentacaoOrigem mo "
                   + "inner join fetch m.movimentacaoDestino md "
                   + "inner join fetch m.pneu "
                   + "left join fetch m.servicosRealizados sr "
                   + "left join fetch sr.tipoServico "
                   + "left join fetch mo.veiculo "
                   + "left join fetch md.veiculo "
                   + "left join fetch md.recapadora r "
                   + "left join fetch r.colaboradorCadastro "
                   + "left join fetch r.colaboradorAlteracaoStatus "
                   + "where mpe.codUnidade = :codUnidade "
                   + "order by mpe.codigo")
    List<MovimentacaoProcessoEntity> getAll(long codUnidade, Pageable pageable);
}
