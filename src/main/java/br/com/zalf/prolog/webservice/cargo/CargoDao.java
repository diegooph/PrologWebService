package br.com.zalf.prolog.webservice.cargo;

import br.com.zalf.prolog.webservice.cargo.model.CargoEmUso;
import br.com.zalf.prolog.webservice.cargo.model.CargoNaoUtilizado;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 01/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface CargoDao {

    /**
     * Busca os cargos que estão em uso na unidade informada como parâmetro. Entende-se um cargo como <b>em uso</b>,
     * caso ele tenha pelo menos 1 colaborador vinculado. Colaboradores inativos também são considerados para validar
     * um cargo como <b>em uso</b>.
     *
     * @param codUnidade Código da unidade utilizada para filtrar os cargos.
     * @return Uma lista contendo os cargos que estão em uso.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    @NotNull
    List<CargoEmUso> getCargosEmUsoUnidade(@NotNull final Long codUnidade) throws Throwable;

    /**
     * Busca os cargos que não estão senso utilizados na unidade informada como parâmetro. Entende-se um cargo como
     * <b>não utilizado</b>, caso nenhum colaborador esteja vinculado ele, estando esse colaborador ativo ou não.
     *
     * @param codUnidade Código da unidade utilizada para filtrar os cargos.
     * @return Uma lista contendo os cargos que não estão sendo utilizados.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    @NotNull
    List<CargoNaoUtilizado> getCargosNaoUtilizadosUnidade(@NotNull final Long codUnidade) throws Throwable;
}