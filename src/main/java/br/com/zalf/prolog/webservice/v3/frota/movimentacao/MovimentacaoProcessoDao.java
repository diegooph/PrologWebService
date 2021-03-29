package br.com.zalf.prolog.webservice.v3.frota.movimentacao;

import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoProcessoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface MovimentacaoProcessoDao extends JpaRepository<MovimentacaoProcessoEntity, Long> {

    @Modifying
    @Query(value = "update movimentacao_origem set km_veiculo = :novoKm " +
            "where cod_movimentacao = :codMovimentacao and cod_veiculo = :codVeiculo", nativeQuery = true)
    void updateKmColetadoOrigem(final long codMovimentacao, final long codVeiculo, final long novoKm);

    @Modifying
    @Query(value = "update movimentacao_destino set km_veiculo = :novoKm " +
            "where cod_movimentacao = :codMovimentacao and cod_veiculo = :codVeiculo", nativeQuery = true)
    void updateKmColetadoDestino(final long codMovimentacao, final long codVeiculo, final long novoKm);
}
