package br.com.zalf.prolog.webservice.geral.dispositivo_movel;

import br.com.zalf.prolog.webservice.geral.dispositivo_movel.model.DispositivoMovel;
import br.com.zalf.prolog.webservice.geral.dispositivo_movel.model.MarcaDispositivoMovelSelecao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 16/07/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public interface DispositivoMovelDao {

    /**
     * Este método é utilizado para buscar todos as {@link MarcaDispositivoMovelSelecao marcas de celular}.
     *
     * @return Uma lista com todos as {@link MarcaDispositivoMovelSelecao marcas de celular}.
     * @throws Throwable Caso ocorra qualquer erro na busca dos dados.
     */
    @NotNull
    List<MarcaDispositivoMovelSelecao> getMarcasDispositivos() throws Throwable;


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
     * @param codEmpresa Código da Empresa a qual o dispositivo móvel está cadastrado
     * @param codDispositivo Código do registro do dispositivo móvel no banco de dados.
     * @return O objeto com as informações do {@link DispositivoMovel dispositivo móvel}.
     * @throws Throwable Caso ocorra qualquer erro na busca dos dados.
     */
    @NotNull
    DispositivoMovel getDispositivoMovel(@NotNull final Long codEmpresa, @NotNull final Long codDispositivo) throws Throwable;

    /**
     * Atualiza as informações de um {@link DispositivoMovel dispositivo móvel}.
     * Atualmente este método atualiza as informações baseado na seguinte lógica:
     * <p>
     * Não é possível alterar o IMEI para um já existente.
     *
     * @param dispositivoMovel Objeto contendo as novas informações para o cargo.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    void updateDispositivoMovel(@NotNull final DispositivoMovel dispositivoMovel) throws Throwable;

}