package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.webservice.colaborador.model.*;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.ValidationUtils;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.permissao.Permissao;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class ColaboradorValidator {

    private static final int ANO_MINIMO_PERMITIDO = 1900;
    private static final int ANO_MAXIMO_PERMITIDO = 2050;
    private static final int MAX_LENGTH_PIS = 11;

    private ColaboradorValidator() {
        throw new IllegalStateException(StringUtils.class.getSimpleName() + " cannot be instantiated!");
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
        } catch (GenericException e) {
            throw e;
        } catch (Exception e) {
            throw new GenericException(e.getMessage(), null);
        }
    }

    private static void validacaoRegional(Long regional) {
        Preconditions.checkNotNull(regional, "Você precisa selecionar a Regional");
        Preconditions.checkArgument(regional > 0, "Regional inválida");
    }


    private static void validacaoCpf(Long cpf) throws Exception {
        Preconditions.checkNotNull(cpf, "Você precisa fornecer o CPF");

        if (!ValidationUtils.isValidCpf(String.format("%011d", cpf))) {
            throw new GenericException("CPF inválido", null);
        }
    }

    private static void validacaoMatriculaAmbev(Integer matriculaAmbev) {
        if (matriculaAmbev != null) {
            Preconditions.checkArgument(matriculaAmbev > 0, "Matrícula Ambev inválida." +
                    "\nA Matrícula deve ser um número positivo");
        }
    }

    private static void validacaoMatriculaTrans(Integer matriculaTrans) {
        if (matriculaTrans != null) {
            Preconditions.checkArgument(matriculaTrans > 0, "Matrícula Transportadora inválida." +
                    "\nA Matrícula deve ser um número positivo");
        }
    }

    private static void validacaoDataNascimento(Date dataNascimento) throws Exception {
        Preconditions.checkNotNull(dataNascimento, "Você precisa fornecer a data de nascimento\n");

        if (DateUtils.verificaAno(dataNascimento, ANO_MAXIMO_PERMITIDO, ANO_MINIMO_PERMITIDO)) {
            throw new GenericException("Ano de Nascimento inválido", null);
        }
    }

    private static void validacaoDataAdmissao(Date dataAdmissao) throws Exception {
        Preconditions.checkNotNull(dataAdmissao, "Você precisa fornecer a data da admissão");

        if (DateUtils.verificaAno(dataAdmissao, ANO_MAXIMO_PERMITIDO, ANO_MINIMO_PERMITIDO)) {
            throw new GenericException("Ano de Admissão inválido", null);
        }
    }

    private static void validacaoNome(String nome) throws Exception {
        Preconditions.checkNotNull(nome, "Você precisa fornecer o nome");

        if (!StringUtils.isAlpabetsValue(nome)) {
            throw new GenericException("Nome inválido\nO Nome não pode conter números", "O campo 'nome' contém números");
        }
    }

    private static void validacaoSetor(Setor setor) {
        Preconditions.checkNotNull(setor, "Você precisa selecionar o Setor");
        Preconditions.checkNotNull(setor.getCodigo(), "Você precisa selecionar o Setor");
        Preconditions.checkArgument(setor.getCodigo() > 0, "Setor inválido");
    }

    private static void validacaoFuncao(Cargo funcao) {
        Preconditions.checkNotNull(funcao, "Você precisa selecionar o Cargo");
        Preconditions.checkNotNull(funcao.getCodigo(), "Você precisa selecionar a Cargo");
        Preconditions.checkArgument(funcao.getCodigo() > 0, "Cargo inválido");
    }

    private static void validacaoUnidade(Unidade unidade) {
        Preconditions.checkNotNull(unidade, "Você precisa selecionar a Unidade");
        Preconditions.checkNotNull(unidade.getCodigo(), "Você precisa fornecer a Unidade");
        Preconditions.checkArgument(unidade.getCodigo() > 0, "Unidade inválida");
    }

    private static void validacaoNivelPermissao(Integer codPermissao) throws Exception {
        Preconditions.checkNotNull(codPermissao, "Você precisa selecionar o Nível de Acesso");

        if (codPermissao < Permissao.LOCAL || codPermissao > Permissao.GERAL) {
            throw new GenericException("Nível de Acesso inválido", "Cód menor que 0 ou maior que 3");
        }
    }

    private static void validacaoEquipe(Equipe equipe) {
        Preconditions.checkNotNull(equipe, "Você precisa selecionar a Equipe");
        Preconditions.checkNotNull(equipe.getCodigo(), "Você precisa selecionar a Equipe");
        Preconditions.checkArgument((int) equipe.getCodigo().intValue() > 0, "Equipe inválida");
    }

    private static void validacaoPis(String pis) throws Exception {
        if (pis == null || pis.isEmpty())
            return;

        if (pis.length() < MAX_LENGTH_PIS) {
            throw new GenericException("PIS inválido\nO PIS deve conter 11 dígitos", null);
        } else if (!ValidationUtils.validaPIS(pis)) {
            throw new GenericException("PIS inválido", null);
        }
    }
}
