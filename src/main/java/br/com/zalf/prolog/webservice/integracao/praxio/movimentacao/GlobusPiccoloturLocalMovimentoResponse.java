package br.com.zalf.prolog.webservice.integracao.praxio.movimentacao;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 2020-03-31
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Data
public final class GlobusPiccoloturLocalMovimentoResponse {
    private final boolean sucesso;
    @Nullable
    private final String usuarioGlobus;
    @Nullable
    private final List<GlobusPiccoloturLocalMovimento> locais;
}
