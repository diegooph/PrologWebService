package br.com.zalf.prolog.webservice.frota.pneu.servico._model;

import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.frota.pneu._model.Restricao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Data
public final class ServicoHolder {
    @NotNull
    private final Long codVeiculo;
    /**
     * Placa do {@link Veiculo} no qual os {@link #servicos} são baseados.
     */
    @NotNull
    private final String placaVeiculo;
    @NotNull
    private final List<Servico> servicos;
    /**
     * As restrições utilizadas para verificar os valores coletados no fechamento do serviço.
     * <p>
     * Só será diferente de {@code null} caso a lista de serviços deste objeto possua algum serviço.
     */
    @Nullable
    private final Restricao restricao;
    /**
     * Indica quais as formas de coleta possíveis para o fechamento de serviço. Irá conter um dos
     * três valoers:
     * {@link FormaColetaDadosAfericaoEnum#EQUIPAMENTO}.
     * {@link FormaColetaDadosAfericaoEnum#MANUAL}.
     * {@link FormaColetaDadosAfericaoEnum#EQUIPAMENTO_MANUAL}.
     * <p>
     * As formas de coleta possíveis são as formas parametrizadas para o tipo de veículo do qual
     * este ServicoHolder contém serviços.
     * <p>
     * Só será diferente de {@code null} caso a lista de serviços deste objeto possua algum serviço.
     */
    @Nullable
    private final FormaColetaDadosAfericaoEnum formaColetaDadosFechamentoServico;
    /**
     * Utilizado para os serviços de inspeção.
     * <p>
     * Só será diferente de {@code null} caso a lista de serviços deste objeto possua algum serviço do tipo
     * {@link TipoServico#INSPECAO}.
     */
    @Nullable
    private final List<Alternativa> alternativasInspecao;
    private String identificadorFrota;
}