package br.com.zalf.prolog.webservice.geral.dispositivomovel;

import br.com.zalf.prolog.webservice.geral.dispositivomovel.model.DispositivoMovel;
import br.com.zalf.prolog.webservice.geral.dispositivomovel.model.DispositivoMovelInsercao;
import br.com.zalf.prolog.webservice.geral.dispositivomovel.model.MarcaDispositivoMovelSelecao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 16/07/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public interface DispositivoMovelDao {

    /**
     * Insere um {@link DispositivoMovelInsercao dispositivo móvel}.
     * Atualmente este método insere as informações baseado na seguinte lógica:
     * <p>
     * Não é possível inserir um IMEI já existente.
     *
     * @param dispositivoMovel Objeto contendo as informações para o dispositivo.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    @NotNull
    Long insertDispositivoMovel(@NotNull final DispositivoMovelInsercao dispositivoMovel) throws Throwable;

    /**
     * Atualiza as informações de um {@link DispositivoMovel dispositivo móvel}.
     * Atualmente este método atualiza as informações baseado na seguinte lógica:
     * <p>
     * Todos os IMEIs do dispositivo são deletados e os recebidos serão cadastrados.
     * Não é possível alterar o IMEI para um já existente.
     *
     * @param dispositivoMovel Objeto contendo as novas informações para o dispositivo.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    void updateDispositivoMovel(@NotNull final DispositivoMovel dispositivoMovel) throws Throwable;

    /**
     * Este método é utilizado para buscar todos os {@link DispositivoMovel dispositivos móveis} por empresa.
     *
     * @return Uma lista com os {@link DispositivoMovel dispositivos móveis}.
     * @throws Throwable Caso ocorra qualquer erro na busca dos dados.
     */
    @NotNull
    List<DispositivoMovel> getDispositivosPorEmpresa(@NotNull final Long codEmpresa) throws Throwable;

    /**
     * Este método é utilizado para buscar um {@link DispositivoMovel dispositivo móvel} específico pelo
     * código do registro e código da empresa.
     *
     * @param codEmpresa     Código da Empresa a qual o dispositivo móvel está cadastrado.
     * @param codDispositivo Código do registro do dispositivo móvel no banco de dados.
     * @return O objeto com as informações do {@link DispositivoMovel dispositivo móvel}.
     * @throws Throwable Caso ocorra qualquer erro na busca dos dados.
     */
    @NotNull
    DispositivoMovel getDispositivoMovel(@NotNull final Long codEmpresa, @NotNull final Long codDispositivo) throws Throwable;

    /**
     * Este método é utilizado para buscar todos as {@link MarcaDispositivoMovelSelecao marcas de dispositivos móveis}.
     * As marcas são a nível ProLog e não podem ser cadastradas pela empresa.
     *
     * @return Uma lista com todos as {@link MarcaDispositivoMovelSelecao marcas de dispositivos móveis}.
     * @throws Throwable Caso ocorra qualquer erro na busca dos dados.
     */
    @NotNull
    List<MarcaDispositivoMovelSelecao> getMarcasDispositivos() throws Throwable;

    /**
     * Deleta um dispositivo móvel.
     *
     * @param codEmpresa     Código da empresa que o dispositivo que será deletado pertence.
     * @param codDispositivo Código do dispositivo que será deletado.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    void deleteDispositivoMovel(@NotNull final Long codEmpresa,
                                @NotNull final Long codDispositivo) throws Throwable;

}