package br.com.zalf.prolog.webservice.cargo;

import br.com.zalf.prolog.webservice.cargo.model.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 01/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface CargoDao {

    /**
     * Este método é utilizado para buscar todos os {@link CargoSelecao cargos} da Unidade selecionada.
     *
     * @param codUnidade Código da Unidade a qual os cargos serão buscados.
     * @return Uma lista com todos os {@link CargoSelecao cargos} da unidade.
     * @throws Throwable Caso ocorra qualquer erro na busca dos dados.
     */
    @NotNull
    List<CargoSelecao> getTodosCargosUnidade(@NotNull final Long codUnidade) throws Throwable;

    /**
     * Este método é utilizado para buscar todos os {@link CargoListagemEmpresa cargos} da Empresa selecionada.
     *
     * @param codEmpresa Código da Empresa a qual os cargos serão buscados.
     * @return Uma lista com todos os {@link CargoListagemEmpresa cargos} da unidade.
     * @throws Throwable Caso ocorra qualquer erro na busca dos dados.
     */
    @NotNull
    List<CargoListagemEmpresa> getTodosCargosEmpresa(@NotNull final Long codEmpresa) throws Throwable;

    /**
     * Este método é utilizado para buscar um {@link CargoEdicao cargo} específico.
     *
     * @param codEmpresa Código da Empresa a qual o cargo buscado pertence.
     * @param codigo     Código do cargo.
     * @return O object do {@link CargoEdicao cargo} encontrado
     * @throws Throwable Caso ocorra qualquer erro na busca dos dados.
     */
    @NotNull
    CargoEdicao getByCod(@NotNull final Long codEmpresa,
                         @NotNull final Long codigo) throws Throwable;

    /**
     * Busca os cargos que estão em uso na unidade informada como parâmetro. Entende-se um cargo como <b>em uso</b>,
     * caso ele tenha pelo menos 1 colaborador vinculado. Colaboradores inativos também são considerados para validar
     * um cargo como <b>em uso</b>.
     *
     * @param codUnidade Código da unidade utilizada para filtrar os cargos.
     * @return Uma lista contendo os {@link CargoEmUso cargos} que estão em uso.
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

    /**
     * Busca as permissões do ProLog separadas por pilar. Esse método irá respeitar os pilares que a unidade tem acesso.
     * Os objetos retornados também destacam quais permissões estão liberadas para o cargo filtrado.
     *
     * @param codUnidade código da unidade.
     * @param codCargo   código do unidade.
     * @return Lista detalhada das permissões da unidade.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    @NotNull
    CargoVisualizacao getPermissoesDetalhadasUnidade(@NotNull final Long codUnidade,
                                                     @NotNull final Long codCargo) throws Throwable;

    /**
     * Insere um {@link CargoEdicao cargo}
     * Atualmente este método atualiza as informações baseado na seguinte lógica:
     * <p>
     * Não é possível inserir um cargo já existente (não deletado logicamente por empresa).
     * A detecção de nomes já existentes é case-insensitive.
     *
     * @param cargo     um cargo.
     * @param userToken o token do usuário que fez a requisição.
     * @return código do cargo recém cadastrado.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    @NotNull
    Long insertCargo(@NotNull final CargoInsercao cargo,
                     @NotNull final String userToken) throws Throwable;

    /**
     * Atualiza as informações de um {@link CargoEdicao cargo}.
     * Atualmente este método atualiza as informações baseado na seguinte lógica:
     * <p>
     * Não é possível alterar o nome para um já existente (não deletado logicamente por empresa).
     * A detecção de nomes já existentes é case-insensitive.
     *
     * @param cargo     Objeto contendo as novas informações para o cargo.
     * @param userToken o token do usuário que fez a requisição.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    void updateCargo(@NotNull final CargoEdicao cargo,
                     @NotNull final String userToken) throws Throwable;

    /**
     * Deleta logicamente um cargo.
     * Atualmente este método é baseado na seguinte lógica:
     * <p>
     * Não é possível deletar logicamente caso o cargo tenha colaboradores vinculados.
     *
     * @param codEmpresa Código da empresa que o cargo que será deletado pertence.
     * @param codigo     Código do cargo que será deletado.
     * @param userToken  o token do usuário que fez a requisição.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    void deleteCargo(@NotNull final Long codEmpresa,
                     @NotNull final Long codigo,
                     @NotNull final String userToken) throws Throwable;
}