package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.webservice.colaborador.model.*;
import br.com.zalf.prolog.webservice.commons.util.ValidationUtils;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.permissao.Visao;
import com.amazonaws.util.StringUtils;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import sun.security.validator.ValidatorException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Calendar;
import java.util.Date;

import static java.sql.JDBCType.INTEGER;

public class ColaboradorValidator {

    public static void validacaoAtributosColaborador(@NotNull final Colaborador colaborador) throws GenericException {
        try {
            validacaoCpf(colaborador.getCpf());
            validacaoDataNascimento(colaborador.getDataNascimento());
            validacaoPis(colaborador.getPis());
        } catch (GenericException e) {
            throw e;
        } catch (Exception e) {
            throw new GenericException(e.getMessage(), null);
        }
    }

    private static void validacaoCpf(Long cpf) throws Exception {
        Preconditions.checkNotNull(cpf, "Você precisa fornecer o CPF");

        if (!ValidationUtils.isValidCpf(String.format("%011d", cpf))) {
            throw new GenericException("CPF inválido", null);
        }
    }

    private static void validacaoPis(String pis) throws Exception{
        
        if(pis.length()<11){
            throw new GenericException("Pis inválido", null);
        }

    }

    private static void validacaoDataNascimento (Date dataNascimento) throws Exception {
        final int anoMinimoPermitido = 1918;

        Preconditions.checkNotNull(dataNascimento, "Você precisa fornecer a data de nascimento");
        if(!verificarDataInserida(dataNascimento)){
            throw new GenericException("A data inserida é inválida", null);

        }else if (!calculariIdade(dataNascimento)){
            throw new GenericException("A idade do funcionário não pode ser menor que 16 anos", null);

        }else if(dataNascimento.getYear() < anoMinimoPermitido){
            throw new GenericException("Você precisa fornecer um ano válido", null);
        }

    }

    private static boolean verificarDataInserida(Date data){

        SimpleDateFormat simpleData = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = simpleData.format(data);

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dataValida = LocalDate.parse(dataFormatada, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static boolean calculariIdade(Date dataNascimento){


        return true;
    }

    private static void validacaoFuncao (Cargo funcao){

    }

    private static void validacaoSetor (Setor setor){

    }

    private static void validacaoNome (String nome){

    }

    private static void validacaoMatriculaAmbev (Integer matriculaAmbev){

    }

    private static void validacaoMatriculaTrans (Integer matriculaTrans){

    }

    private static void validacaoDataAdmissao (Date dataAdmissao){
        Preconditions.checkNotNull(dataAdmissao, "Você precisa fornecer a data da admissão");

    }

    private static void validacaoDataDemissao (Date dataDemissao){

    }

  /*  private static void validacaoAtivo (Boolean ativo){

    }*/

    private static void validacaoEmpresa (Empresa empresa){

    }

    private static void validacaoUnidade (Unidade unidade){

    }

    private static void validacaoRegional (Regional regional){

    }

    private static void validacaoEquipe (Equipe equipe){

    }

    private static void validacaoVisao (Visao visao){

    }

    private static void validacaoCodPermissao (Integer codPermissao){

    }


}
