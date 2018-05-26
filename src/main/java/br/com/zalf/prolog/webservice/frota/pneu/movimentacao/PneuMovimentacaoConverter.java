package br.com.zalf.prolog.webservice.frota.pneu.movimentacao;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.PneuMovimentacaoAnalise;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuConverter;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.Recapadora;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 26/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class PneuMovimentacaoConverter {

    private PneuMovimentacaoConverter() {
        throw new IllegalStateException(PneuMovimentacaoConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static PneuMovimentacaoAnalise createPneuMovimentacaoAnaliseCompleto(@NotNull final ResultSet rSet)
            throws SQLException {
        final Pneu pneu = PneuConverter.createPneuCompleto(rSet);
        final PneuMovimentacaoAnalise pneuMovimentacaoAnalise = new PneuMovimentacaoAnalise();
        pneuMovimentacaoAnalise.setCodigo(pneu.getCodigo());
        pneuMovimentacaoAnalise.setCodigoCliente(pneu.getCodigoCliente());
        pneuMovimentacaoAnalise.setPosicao(pneu.getPosicao());
        pneuMovimentacaoAnalise.setDot(pneu.getDot());
        pneuMovimentacaoAnalise.setValor(pneu.getValor());
        pneuMovimentacaoAnalise.setCodUnidadeAlocado(pneu.getCodUnidadeAlocado());
        pneuMovimentacaoAnalise.setCodRegionalAlocado(pneu.getCodRegionalAlocado());
        pneuMovimentacaoAnalise.setPneuNovoNuncaRodado(pneu.isPneuNovoNuncaRodado());
        pneuMovimentacaoAnalise.setMarca(pneu.getMarca());
        pneuMovimentacaoAnalise.setModelo(pneu.getModelo());
        pneuMovimentacaoAnalise.setBanda(pneu.getBanda());
        pneuMovimentacaoAnalise.setDimensao(pneu.getDimensao());
        pneuMovimentacaoAnalise.setSulcosAtuais(pneu.getSulcosAtuais());
        pneuMovimentacaoAnalise.setPressaoCorreta(pneu.getPressaoCorreta());
        pneuMovimentacaoAnalise.setPressaoAtual(pneu.getPressaoAtual());
        pneuMovimentacaoAnalise.setStatus(pneu.getStatus());
        pneuMovimentacaoAnalise.setVidaAtual(pneu.getVidaAtual());
        pneuMovimentacaoAnalise.setVidasTotal(pneu.getVidasTotal());
        // Seta informações extras do pneu que está em Análise.
        pneuMovimentacaoAnalise.setRecapadora(createRecapadoraPneu(rSet));
        pneuMovimentacaoAnalise.setCodColeta(rSet.getString("COD_COLETA"));
        return pneuMovimentacaoAnalise;
    }

    @NotNull
    private static Recapadora createRecapadoraPneu(@NotNull final ResultSet rSet) throws SQLException {
        final Recapadora recapadora = new Recapadora();
        recapadora.setCodigo(rSet.getLong("COD_RECAPADORA"));
        recapadora.setNome(rSet.getString("NOME_RECAPADORA"));
        recapadora.setCodEmpresa(rSet.getLong("COD_EMPRESA_RECAPADORA"));
        recapadora.setAtiva(rSet.getBoolean("RECAPADORA_ATIVA"));
        return recapadora;
    }
}
