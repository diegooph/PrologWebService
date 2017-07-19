package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.ArrayOfMedidaPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.IncluirMedida2;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.MedidaPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.ArrayOfQuestionarioVeiculos;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.Questionario;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.QuestionarioVeiculos;
import com.sun.istack.internal.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by luiz on 18/07/17.
 */
final class AvaCorpAvilanConverter {

    private AvaCorpAvilanConverter() {
        throw new IllegalStateException(AvaCorpAvilanConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    static List<Veiculo> convert(@NotNull final ArrayOfVeiculo arrayOfVeiculo) {
        checkNotNull(arrayOfVeiculo, "arrayOfVeiculo não pode ser null!");

        final List<br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Veiculo> veiculosAvilan
                = arrayOfVeiculo.getVeiculo();

        final List<Veiculo> veiculos = new ArrayList<>();
        for (br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Veiculo v : veiculosAvilan) {
            Veiculo veiculo = new Veiculo();
            veiculo.setPlaca(v.getPlaca());
            veiculo.setKmAtual(v.getMarcador());
            veiculos.add(veiculo);
        }

        return veiculos;
    }

    static IncluirMedida2 convert(@NotNull final Afericao afericao) {
        checkNotNull(afericao, "afericao não pode ser null!");

        final IncluirMedida2 incluirMedida2 = new IncluirMedida2();

        // seta valores
        incluirMedida2.setVeiculo(afericao.getVeiculo().getPlaca());
        incluirMedida2.setTipoMarcador(1 /* hodômetro */);
        incluirMedida2.setMarcador(Math.toIntExact(afericao.getVeiculo().getKmAtual()));
        incluirMedida2.setDataMedida(createDatePattern(afericao.getDataHora()));
        // TODO: setar placas carreta?

        ArrayOfMedidaPneu medidas = new ArrayOfMedidaPneu();
        for (Pneu pneu : afericao.getVeiculo().getListPneus()) {
            final MedidaPneu medidaPneu = new MedidaPneu();
            medidaPneu.setCalibragem(pneu.getPressaoAtual());
            medidaPneu.setNumeroFogoPneu(String.valueOf(pneu.getCodigo()) /* TODO: parse para posições Avilan */);
            medidaPneu.setTriangulo1PrimeiroSulco(pneu.getSulcosAtuais().getExterno());
            medidaPneu.setTriangulo1SegundoSulco(pneu.getSulcosAtuais().getCentralExterno());
            medidaPneu.setTriangulo1TerceiroSulco(pneu.getSulcosAtuais().getCentralInterno());
            medidaPneu.setTriangulo1QuartoSulco(pneu.getSulcosAtuais().getInterno());
            medidas.getMedidaPneu().add(medidaPneu);
        }
        incluirMedida2.setMedidas(medidas);

        return incluirMedida2;
    }

    static Map<ModeloChecklist, List<String>> convert(ArrayOfQuestionarioVeiculos arrayOfQuestionarioVeiculos) {
        checkNotNull(arrayOfQuestionarioVeiculos, "arrayOfQuestionarioVeiculos não pode ser null!");

        final Map<ModeloChecklist, List<String>> map = new HashMap<>();

        for (QuestionarioVeiculos questionarioVeiculos : arrayOfQuestionarioVeiculos.getQuestionarioVeiculos()) {
            // Cria modelo de checklist
            final ModeloChecklist modeloChecklist = new ModeloChecklist();
            final Questionario questionario = questionarioVeiculos.getQuestionario();
            modeloChecklist.setCodigo(Long.valueOf(questionario.getCodigoQuestionario()));
            modeloChecklist.setNome(questionario.getDescricao());

            // cria placa dos veículos
            final List<String> placasVeiculo = new ArrayList<>();
            questionarioVeiculos.getVeiculos().getVeiculo().forEach(veiculo -> placasVeiculo.add(veiculo.getPlaca()));

            map.put(modeloChecklist, placasVeiculo);
        }

        return map;
    }

    /**
     * Converte uma data para a representação em texto esperado no web service de integração da Avilan: yyyy-MM-dd.
     *
     * @param date um {@link Date}.
     * @return uma {@link String} represetando a data.
     */
    private static String createDatePattern(@NotNull final Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}