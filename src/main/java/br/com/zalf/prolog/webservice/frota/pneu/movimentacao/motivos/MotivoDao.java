package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoTrocaInsercao;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-03-17
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public interface MotivoDao {

    /**
     * Insere um {@link MotivoTrocaInsercao colaborador} no banco de dados.
     * <p>
     * Todos os dados são obrigatórios.
     *
     * @param motivoTrocaInsercao Dados do motivo de troca a ser inserido.
     * @throws Throwable Caso qualquer erro ocorra.
     */
    void insert(@NotNull final MotivoTrocaInsercao motivoTrocaInsercao) throws Throwable;

}
