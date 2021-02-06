-- Adicionaremos um campo auxiliar na nomenclatura, será utilizado para integrações.
-- O código auxiliar pode ser nulo.
alter table pneu_posicao_nomenclatura_empresa
    add column cod_auxiliar text;

-- Passamos a inserir também o código auxiliar de cada posição.
DROP FUNCTION
    FUNC_PNEU_NOMENCLATURA_INSERE_EDITA_NOMENCLATURA(BIGINT, BIGINT, BIGINT, VARCHAR, TEXT, TIMESTAMP WITH TIME ZONE);
CREATE OR REPLACE FUNCTION
    FUNC_PNEU_NOMENCLATURA_INSERE_EDITA_NOMENCLATURA(F_COD_EMPRESA BIGINT,
                                                     F_COD_DIAGRAMA BIGINT,
                                                     F_POSICAO_PROLOG BIGINT,
                                                     F_NOMENCLATURA TEXT,
                                                     F_COD_AUXILIAR TEXT,
                                                     F_TOKEN_RESPONSAVEL_INSERCAO TEXT,
                                                     F_DATA_HORA_CADASTRO TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_COLABORADOR_INSERCAO BIGINT := (SELECT CODIGO
                                          FROM COLABORADOR
                                          WHERE CPF = (SELECT CPF_COLABORADOR
                                                       FROM TOKEN_AUTENTICACAO
                                                       WHERE TOKEN = F_TOKEN_RESPONSAVEL_INSERCAO));
BEGIN
    INSERT INTO PNEU_POSICAO_NOMENCLATURA_EMPRESA (COD_DIAGRAMA,
                                                   COD_EMPRESA,
                                                   POSICAO_PROLOG,
                                                   NOMENCLATURA,
                                                   COD_AUXILIAR,
                                                   COD_COLABORADOR_CADASTRO,
                                                   DATA_HORA_CADASTRO)
    VALUES (F_COD_DIAGRAMA,
            F_COD_EMPRESA,
            F_POSICAO_PROLOG,
            F_NOMENCLATURA,
            F_COD_AUXILIAR,
            V_COD_COLABORADOR_INSERCAO,
            F_DATA_HORA_CADASTRO)
    ON CONFLICT ON CONSTRAINT UNIQUE_DIAGRAMA_EMPRESA_POSICAO_PROLOG
        DO UPDATE SET NOMENCLATURA             = F_NOMENCLATURA,
                      COD_AUXILIAR             = F_COD_AUXILIAR,
                      COD_COLABORADOR_CADASTRO = V_COD_COLABORADOR_INSERCAO,
                      DATA_HORA_CADASTRO       = F_DATA_HORA_CADASTRO;
END;
$$;

-- Alteramos a query que busca das nomenclaturas para buscar os códigos auxiliares também.
DROP FUNCTION FUNC_PNEU_NOMENCLATURA_GET_NOMENCLATURA(BIGINT, BIGINT);
CREATE OR REPLACE FUNCTION FUNC_PNEU_NOMENCLATURA_GET_NOMENCLATURA(F_COD_EMPRESA BIGINT,
                                                                   F_COD_DIAGRAMA BIGINT)
    RETURNS TABLE
            (
                NOMENCLATURA   TEXT,
                COD_AUXILIAR   TEXT,
                POSICAO_PROLOG INTEGER
            )
    LANGUAGE SQL
AS
$$
SELECT PPNE.NOMENCLATURA::TEXT AS NOMENCLATURA,
       PPNE.COD_AUXILIAR       AS COD_AUXILIAR,
       PPNE.POSICAO_PROLOG     AS POSICAO_PROLOG
FROM PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
WHERE PPNE.COD_EMPRESA = F_COD_EMPRESA
  AND PPNE.COD_DIAGRAMA = F_COD_DIAGRAMA
$$;

-- Na busca das posições, mapeamos o código auxiliar para retornar junto com a posição prolog.
drop function integracao.func_pneu_afericao_get_mapeamento_posicoes_prolog(bigint, text);
create or replace function
    integracao.func_pneu_afericao_get_mapeamento_posicoes_prolog(f_cod_empresa bigint,
                                                                 f_cod_auxiliar_tipo_veiculo text)
    returns table
            (
                posicao_prolog                    integer,
                nomenclatura_cliente              text,
                cod_auxiliar_nomenclatura_cliente text
            )
    language sql
as
$$
with cod_auxiliares as (
    select vt.codigo                                   as cod_tipo_veiculo,
           regexp_split_to_table(vt.cod_auxiliar, ',') as cod_auxiliar
    from veiculo_tipo vt
    where vt.cod_empresa = f_cod_empresa
)
select ppne.posicao_prolog                           as posicao_prolog,
       ppne.nomenclatura                             as nomenclatura_cliente,
       regexp_split_to_table(ppne.cod_auxiliar, ',') as cod_auxiliar_nomenclatura_cliente
from veiculo_tipo vt
         join pneu_posicao_nomenclatura_empresa ppne on vt.cod_diagrama = ppne.cod_diagrama
         join cod_auxiliares ca on ca.cod_tipo_veiculo = vt.codigo
where ca.cod_auxiliar = f_cod_auxiliar_tipo_veiculo
  and ppne.cod_empresa = f_cod_empresa;
$$;

-- Criamos uma function para buscar um código de diagrama dado um cod_auxiliar.
create or replace function
    integracao.func_pneu_afericao_get_cod_diagrama_by_cod_auxiliar(f_cod_empresa bigint,
                                                                   f_cod_auxiliar_tipo_veiculo text)
    returns table
            (
                cod_diagrama smallint
            )
    language sql
as
$$
select vt.cod_diagrama as cod_diagrama
from (select vt.cod_diagrama                             as cod_diagrama,
             regexp_split_to_table(vt.cod_auxiliar, ',') as cod_auxiliar
      from veiculo_tipo vt
      where vt.cod_empresa = f_cod_empresa) as vt
where vt.cod_auxiliar = f_cod_auxiliar_tipo_veiculo;
$$;