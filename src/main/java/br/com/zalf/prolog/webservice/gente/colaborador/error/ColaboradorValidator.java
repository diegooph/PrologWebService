package br.com.zalf.prolog.webservice.gente.colaborador.error;

import br.com.zalf.prolog.webservice.commons.util.CpfValidationUtils;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Equipe;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Setor;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import br.com.zalf.prolog.webservice.permissao.Permissao;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

@Deprecated
public class ColaboradorValidator {
    private static final int MAX_LENGTH_CPF = 11;
    private static final int ANO_MINIMO_PERMITIDO = 1900;
    private static final int ANO_MAXIMO_PERMITIDO = 2050;
    private static final int MAX_LENGTH_PIS = 11;

    private ColaboradorValidator() {
        throw new IllegalStateException(ColaboradorValidator.class.getSimpleName() + " cannot be instantiated!");
    }

    public static void validacaoAtributosColaborador(@NotNull final Colaborador colaborador) throws GenericException {
        try {
            validacaoRegional(colaborador.getRegional().getCodigo());
            validacaoCpf(colaborador.getCpf());
            validacaoMatriculaAmbev(colaborador.getMatriculaAmbev());
            validacaoMatriculaTrans(colaborador.getMatriculaTrans());
            validacaoDataNascimento(colaborador.getDataNascimento());
            validacaoDataAdmissao(colaborador.getDataAdmissao());
            validacaoNome(colaborador.getNome());
            validacaoSetor(colaborador.getSetor());
            validacaoFuncao(colaborador.getFuncao());
            validacaoUnidade(colaborador.getUnidade());
            validacaoNivelPermissao(colaborador.getCodPermissao());
            validacaoEquipe(colaborador.getEquipe());
            validacaoPis(colaborador.getPis());
        } catch (final GenericException e) {
            throw e;
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), null);
        }
    }

    private static void validacaoRegional(@Nullable final Long regional) {
        Preconditions.checkNotNull(regional, "Você precisa selecionar a regional");
        Preconditions.checkArgument(regional > 0, "Regional inválida");
    }


    private static void validacaoCpf(@Nullable final Long cpf) throws Exception {
        Preconditions.checkNotNull(cpf, "Você precisa fornecer o CPF");

        if (String.format("%011d", cpf).length() != MAX_LENGTH_CPF) {
            throw new GenericException(
                    String.format("CPF inválido\nO CPF precisa ter %d caracteres", MAX_LENGTH_CPF),
                    "CPF informado:" + cpf);
        }
    }

    private static void validacaoMatriculaAmbev(@Nullable final Integer matriculaAmbev) {
        if (matriculaAmbev != null) {
            Preconditions.checkArgument(matriculaAmbev > 0, "Matrícula Ambev inválida." +
                    "\nA matrícula deve ser um número positivo");
        }
    }

    private static void validacaoMatriculaTrans(@Nullable final Integer matriculaTrans) {
        if (matriculaTrans != null) {
            Preconditions.checkArgument(matriculaTrans > 0, "Matrícula transportadora inválida." +
                    "\nA matrícula deve ser um número positivo");
        }
    }

    private static void validacaoDataNascimento(@Nullable final Date dataNascimento) throws Exception {
        Preconditions.checkNotNull(dataNascimento, "Você precisa fornecer a data de nascimento\n");

        if (DateUtils.verificaAno(dataNascimento, ANO_MAXIMO_PERMITIDO, ANO_MINIMO_PERMITIDO)) {
            throw new GenericException("Ano de nascimento inválido", "Data de nascimento informada: " + dataNascimento);
        }
    }

    private static void validacaoDataAdmissao(@Nullable final Date dataAdmissao) throws Exception {
        Preconditions.checkNotNull(dataAdmissao, "Você precisa fornecer a data da admissão");

        if (DateUtils.verificaAno(dataAdmissao, ANO_MAXIMO_PERMITIDO, ANO_MINIMO_PERMITIDO)) {
            throw new GenericException("Ano de admissão inválido", "Data de admissão informada: " + dataAdmissao);
        }
    }

    private static void validacaoNome(@Nullable final String nome) throws Exception {
        Preconditions.checkNotNull(nome, "Você precisa fornecer o nome");

        if (StringUtils.isNullOrEmpty(nome.trim())) {
            throw new GenericException("Vocẽ precisa fornecer o nome", "nome com apenas espaços em branco" + nome);
        }
        if (StringUtils.getOnlyNumbers(nome).length() > 0) {
            throw new GenericException("Nome inválido\nO nome não pode conter números", "Nome informado: " + nome);
        }
    }

    private static void validacaoSetor(@Nullable final Setor setor) {
        Preconditions.checkNotNull(setor, "Você precisa selecionar o setor");
        Preconditions.checkNotNull(setor.getCodigo(), "Você precisa selecionar o setor");
        Preconditions.checkArgument(setor.getCodigo() > 0, "Setor inválido");
    }

    private static void validacaoFuncao(@Nullable final Cargo funcao) {
        Preconditions.checkNotNull(funcao, "Você precisa selecionar o cargo");
        Preconditions.checkNotNull(funcao.getCodigo(), "Você precisa selecionar a cargo");
        Preconditions.checkArgument(funcao.getCodigo() > 0, "Cargo inválido");
    }

    private static void validacaoUnidade(@Nullable final Unidade unidade) {
        Preconditions.checkNotNull(unidade, "Você precisa selecionar a unidade");
        Preconditions.checkNotNull(unidade.getCodigo(), "Você precisa fornecer a unidade");
        Preconditions.checkArgument(unidade.getCodigo() > 0, "Unidade inválida");
    }

    private static void validacaoNivelPermissao(@Nullable final Integer codPermissao) throws Exception {
        Preconditions.checkNotNull(codPermissao, "Você precisa selecionar o nível de acesso");

        if (codPermissao < Permissao.LOCAL || codPermissao > Permissao.GERAL) {
            throw new GenericException("Nível de acesso inválido", "Nivel de acesso informado: " + codPermissao + "" +
                    "\nO nivel de acesso deve ser um número maior ou igual a 0, ou menor ou igual 3");
        }
    }

    private static void validacaoEquipe(@Nullable final Equipe equipe) {
        Preconditions.checkNotNull(equipe, "Você precisa selecionar a equipe");
        Preconditions.checkNotNull(equipe.getCodigo(), "Você precisa selecionar a equipe");
        Preconditions.checkArgument((int) equipe.getCodigo().intValue() > 0, "Equipe inválida");
    }

    private static void validacaoPis(@Nullable final String pis) throws Exception {
        if (pis == null || pis.isEmpty()) {
            return;
        }

        if (pis.length() < MAX_LENGTH_PIS) {
            throw new GenericException("PIS inválido\nO PIS deve conter onze dígitos", "PIS informado:" + pis);
        } else if (!CpfValidationUtils.isPisPasepValid(pis)) {
            throw new GenericException("PIS inválido", "PIS informado: " + pis);
        }
    }
}
