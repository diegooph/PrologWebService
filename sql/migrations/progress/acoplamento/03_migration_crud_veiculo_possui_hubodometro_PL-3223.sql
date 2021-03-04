alter table veiculo_data
    add column motorizado boolean;

update veiculo_data
set motorizado = vd.motorizado
from veiculo_diagrama as vd
where cod_diagrama = vd.codigo;

alter table veiculo_diagrama
    add constraint unique_codigo_motorizado_veiculo_diagrama
        unique (codigo, motorizado);

alter table veiculo_data
    add constraint fk_diagrama_motorizado
        foreign key (cod_diagrama, motorizado) references veiculo_diagrama (codigo, motorizado);

alter table veiculo_data
    alter column motorizado set not null;

alter table veiculo_data
    add column possui_hubodometro boolean default false not null;
alter table veiculo_data
    add constraint check_hubodometro
        check ((possui_hubodometro = true and motorizado = false) or (possui_hubodometro = false));

alter table veiculo_data
    alter column possui_hubodometro drop default;

create or replace view veiculo
            (codigo,
             placa,
             identificador_frota,
             cod_unidade,
             cod_empresa,
             km,
             status_ativo,
             cod_diagrama,
             cod_tipo,
             cod_modelo,
             cod_eixos,
             data_hora_cadastro,
             cod_unidade_cadastro,
             foi_editado,
             possui_hubodometro,
             motorizado)
as
SELECT v.codigo,
       v.placa,
       v.identificador_frota,
       v.cod_unidade,
       v.cod_empresa,
       v.km,
       v.status_ativo,
       v.cod_diagrama,
       v.cod_tipo,
       v.cod_modelo,
       v.cod_eixos,
       v.data_hora_cadastro,
       v.cod_unidade_cadastro,
       v.foi_editado,
       v.possui_hubodometro,
       v.motorizado
FROM veiculo_data v
WHERE v.deletado = false;

------------------------------------------------------- LISTAGEM ------------------------------------------------------
drop function func_veiculo_get_all_by_unidades(f_cod_unidades bigint[],
    f_apenas_ativos boolean,
    f_cod_tipo_veiculo bigint);

CREATE OR REPLACE FUNCTION FUNC_VEICULO_GET_ALL_BY_UNIDADES(F_COD_UNIDADES BIGINT[],
                                                            F_APENAS_ATIVOS BOOLEAN,
                                                            F_COD_TIPO_VEICULO BIGINT)
    RETURNS TABLE
            (
                CODIGO              BIGINT,
                PLACA               TEXT,
                COD_REGIONAL        BIGINT,
                NOME_REGIONAL       TEXT,
                COD_UNIDADE         BIGINT,
                NOME_UNIDADE        TEXT,
                KM                  BIGINT,
                STATUS_ATIVO        BOOLEAN,
                COD_TIPO            BIGINT,
                COD_MODELO          BIGINT,
                COD_DIAGRAMA        BIGINT,
                IDENTIFICADOR_FROTA TEXT,
                MODELO              TEXT,
                POSSUI_HUBODOMETRO  BOOLEAN,
                MOTORIZADO          BOOLEAN,
                NOME_DIAGRAMA       TEXT,
                DIANTEIRO           BIGINT,
                TRASEIRO            BIGINT,
                TIPO                TEXT,
                MARCA               TEXT,
                COD_MARCA           BIGINT
            )
    LANGUAGE SQL
AS
$$
SELECT V.CODIGO                                                AS CODIGO,
       V.PLACA                                                 AS PLACA,
       R.CODIGO                                                AS COD_REGIONAL,
       R.REGIAO                                                AS NOME_REGIONAL,
       U.CODIGO                                                AS COD_UNIDADE,
       U.NOME                                                  AS NOME_UNIDADE,
       V.KM                                                    AS KM,
       V.STATUS_ATIVO                                          AS STATUS_ATIVO,
       V.COD_TIPO                                              AS COD_TIPO,
       V.COD_MODELO                                            AS COD_MODELO,
       V.COD_DIAGRAMA                                          AS COD_DIAGRAMA,
       V.IDENTIFICADOR_FROTA                                   AS IDENTIFICADOR_FROTA,
       MV.NOME                                                 AS MODELO,
       V.POSSUI_HUBODOMETRO                                    AS POSSUI_HUBODOMETRO,
       V.MOTORIZADO                                            AS MOTORIZADO,
       VD.NOME                                                 AS NOME_DIAGRAMA,
       COUNT(VDE.TIPO_EIXO) FILTER (WHERE VDE.TIPO_EIXO = 'D') AS DIANTEIRO,
       COUNT(VDE.TIPO_EIXO) FILTER (WHERE VDE.TIPO_EIXO = 'T') AS TRASEIRO,
       VT.NOME                                                 AS TIPO,
       MAV.NOME                                                AS MARCA,
       MAV.CODIGO                                              AS COD_MARCA
FROM VEICULO V
         JOIN MODELO_VEICULO MV ON MV.CODIGO = V.COD_MODELO
         JOIN VEICULO_DIAGRAMA VD ON VD.CODIGO = V.COD_DIAGRAMA
         JOIN VEICULO_DIAGRAMA_EIXOS VDE ON VDE.COD_DIAGRAMA = VD.CODIGO
         JOIN VEICULO_TIPO VT ON VT.CODIGO = V.COD_TIPO
         JOIN MARCA_VEICULO MAV ON MAV.CODIGO = MV.COD_MARCA
         JOIN UNIDADE U ON U.CODIGO = V.COD_UNIDADE
         JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
WHERE V.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND CASE
          WHEN F_APENAS_ATIVOS IS NULL OR F_APENAS_ATIVOS = FALSE
              THEN TRUE
          ELSE V.STATUS_ATIVO = TRUE
    END
  AND CASE
          WHEN F_COD_TIPO_VEICULO IS NULL
              THEN TRUE
          ELSE V.COD_TIPO = F_COD_TIPO_VEICULO
    END
GROUP BY V.PLACA, V.CODIGO, V.CODIGO, V.PLACA, U.CODIGO, V.KM, V.STATUS_ATIVO, V.COD_TIPO, V.COD_MODELO,
         V.MOTORIZADO, V.POSSUI_HUBODOMETRO, V.COD_DIAGRAMA, V.IDENTIFICADOR_FROTA, R.CODIGO, MV.NOME, VD.NOME,
         VT.NOME, MAV.NOME, MAV.CODIGO
ORDER BY V.PLACA;
$$;

---------------------------------------------------BUSCA ESPECIFICO ---------------------------------------------------
drop function func_veiculo_get_veiculo(f_cod_veiculo bigint);

create or replace function func_veiculo_get_veiculo(f_cod_veiculo bigint)
    returns table
            (
                codigo               bigint,
                placa                text,
                cod_unidade          bigint,
                cod_empresa          bigint,
                km                   bigint,
                status_ativo         boolean,
                cod_tipo             bigint,
                cod_modelo           bigint,
                cod_diagrama         bigint,
                identificador_frota  text,
                cod_regional_alocado bigint,
                modelo               text,
                possui_hubodometro   boolean,
                motorizado           boolean,
                nome_diagrama        text,
                dianteiro            bigint,
                traseiro             bigint,
                tipo                 text,
                marca                text,
                cod_marca            bigint
            )
    language sql
as
$$
select v.codigo                                                as codigo,
       v.placa                                                 as placa,
       v.cod_unidade::bigint                                   as cod_unidade,
       v.cod_empresa::bigint                                   as cod_empresa,
       v.km                                                    as km,
       v.status_ativo                                          as status_ativo,
       v.cod_tipo                                              as cod_tipo,
       v.cod_modelo                                            as cod_modelo,
       v.cod_diagrama                                          as cod_diagrama,
       v.identificador_frota                                   as identificador_frota,
       r.codigo                                                as cod_regional_alocado,
       mv.nome                                                 as modelo,
       v.possui_hubodometro                                    as possui_hubodometro,
       v.motorizado                                            as motorizado,
       vd.nome                                                 as nome_diagrama,
       count(vde.tipo_eixo) filter (where vde.tipo_eixo = 'D') as dianteiro,
       count(vde.tipo_eixo) filter (where vde.tipo_eixo = 'T') as traseiro,
       vt.nome                                                 as tipo,
       mav.nome                                                as marca,
       mav.codigo                                              as cod_marca
from veiculo v
         join modelo_veiculo mv on mv.codigo = v.cod_modelo
         join veiculo_diagrama vd on vd.codigo = v.cod_diagrama
         join veiculo_diagrama_eixos vde on vde.cod_diagrama = vd.codigo
         join veiculo_tipo vt on vt.codigo = v.cod_tipo
         join marca_veiculo mav on mav.codigo = mv.cod_marca
         join unidade u on u.codigo = v.cod_unidade
         join regional r on u.cod_regional = r.codigo
where v.codigo = f_cod_veiculo
group by v.placa,
         v.codigo,
         v.codigo,
         v.placa,
         v.cod_unidade,
         v.cod_empresa,
         v.km,
         v.status_ativo,
         v.cod_tipo,
         v.cod_modelo,
         v.possui_hubodometro,
         v.motorizado,
         v.cod_diagrama,
         v.identificador_frota,
         r.codigo,
         mv.nome,
         vd.nome,
         vt.nome,
         mav.nome,
         mav.codigo
order by v.placa;
$$;

------------------------------------------------------- INSERÇÃO ------------------------------------------------------
drop function func_veiculo_insere_veiculo(f_cod_unidade bigint,
    f_placa text,
    f_identificador_frota text,
    f_km_atual bigint,
    f_cod_modelo bigint,
    f_cod_tipo bigint);

CREATE OR REPLACE FUNCTION FUNC_VEICULO_INSERE_VEICULO(F_COD_UNIDADE BIGINT,
                                                       F_PLACA TEXT,
                                                       F_IDENTIFICADOR_FROTA TEXT,
                                                       F_KM_ATUAL BIGINT,
                                                       F_COD_MODELO BIGINT,
                                                       F_COD_TIPO BIGINT,
                                                       F_POSSUI_HUBODOMETRO BOOLEAN)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_EMPRESA           BIGINT;
    V_STATUS_ATIVO CONSTANT BOOLEAN := TRUE;
    V_COD_DIAGRAMA          BIGINT;
    V_COD_VEICULO_PROLOG    BIGINT;
    V_MOTORIZADO            BOOLEAN;
BEGIN
    -- Busca o código da empresa de acordo com a unidade
    V_COD_EMPRESA := (SELECT U.COD_EMPRESA
                      FROM UNIDADE U
                      WHERE U.CODIGO = F_COD_UNIDADE);

    -- Validamos se o KM foi inputado corretamente.
    IF (F_KM_ATUAL < 0)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                'A quilometragem do veículo não pode ser um número negativo.');
    END IF;

    -- Validamos se o modelo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM MODELO_VEICULO MV
                          WHERE MV.COD_EMPRESA = V_COD_EMPRESA
                            AND MV.CODIGO = F_COD_MODELO))
    THEN
        PERFORM THROW_GENERIC_ERROR(
                'Por favor, verifique o modelo do veículo e tente novamente.');
    END IF;

    -- Validamos se o tipo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM VEICULO_TIPO VT
                          WHERE VT.CODIGO = F_COD_TIPO
                            AND VT.COD_EMPRESA = V_COD_EMPRESA))
    THEN
        PERFORM THROW_GENERIC_ERROR(
                'Por favor, verifique o tipo do veículo e tente novamente.');
    END IF;

    -- Busca o código do diagrama de acordo com o tipo de veículo.
    V_COD_DIAGRAMA := (SELECT VT.COD_DIAGRAMA
                       FROM VEICULO_TIPO VT
                       WHERE VT.CODIGO = F_COD_TIPO
                         AND VT.COD_EMPRESA = V_COD_EMPRESA);

    V_MOTORIZADO := (SELECT VD.MOTORIZADO
                     FROM VEICULO_DIAGRAMA VD
                     WHERE VD.CODIGO = V_COD_DIAGRAMA);

    if (V_MOTORIZADO AND F_POSSUI_HUBODOMETRO)
    then
        perform throw_generic_error('Não é possivel cadastrar um veiculo motorizado com hubodometro, favor verificar.');
    end if;

    -- Aqui devemos apenas inserir o veículo no Prolog.
    INSERT INTO VEICULO(COD_EMPRESA,
                        COD_UNIDADE,
                        PLACA,
                        KM,
                        STATUS_ATIVO,
                        COD_TIPO,
                        COD_MODELO,
                        COD_DIAGRAMA,
                        MOTORIZADO,
                        COD_UNIDADE_CADASTRO,
                        IDENTIFICADOR_FROTA,
                        POSSUI_HUBODOMETRO)
    VALUES (V_COD_EMPRESA,
            F_COD_UNIDADE,
            F_PLACA,
            F_KM_ATUAL,
            V_STATUS_ATIVO,
            F_COD_TIPO,
            F_COD_MODELO,
            V_COD_DIAGRAMA,
            V_MOTORIZADO,
            F_COD_UNIDADE,
            F_IDENTIFICADOR_FROTA,
            F_POSSUI_HUBODOMETRO)
    RETURNING CODIGO INTO V_COD_VEICULO_PROLOG;

    -- Verificamos se o insert funcionou.
    IF V_COD_VEICULO_PROLOG IS NULL OR V_COD_VEICULO_PROLOG <= 0
    THEN
        PERFORM THROW_GENERIC_ERROR(
                'Não foi possível inserir o veículo, tente novamente');
    END IF;

    RETURN V_COD_VEICULO_PROLOG;
END;
$$;
---------------------------------------------------- ATUALIZAÇÃO ------------------------------------------------------
alter table veiculo_edicao_historico
    add column if not exists possui_hubodometro boolean not null default false;

alter table veiculo_edicao_historico
    alter column possui_hubodometro drop default;

drop function if exists func_veiculo_gera_historico_atualizacao(f_cod_empresa_veiculo bigint,
    f_cod_unidade_veiculo bigint,
    f_cod_veiculo bigint,
    f_antiga_placa text,
    f_antigo_identificador_frota text,
    f_antigo_km bigint,
    f_antigo_cod_diagrama bigint,
    f_antigo_cod_tipo bigint,
    f_antigo_cod_modelo bigint,
    f_antigo_status boolean,
    f_antigo_possui_hubodometro boolean,
    f_total_edicoes smallint,
    f_cod_colaborador_edicao bigint,
    f_origem_edicao text,
    f_data_hora_edicao timestamp with time zone,
    f_informacoes_extras_edicao text);

create or replace function func_veiculo_gera_historico_atualizacao(f_cod_empresa_veiculo bigint,
                                                                   f_cod_unidade_veiculo bigint,
                                                                   f_cod_veiculo bigint,
                                                                   f_antiga_placa text,
                                                                   f_antigo_identificador_frota text,
                                                                   f_antigo_km bigint,
                                                                   f_antigo_cod_diagrama bigint,
                                                                   f_antigo_cod_tipo bigint,
                                                                   f_antigo_cod_modelo bigint,
                                                                   f_antigo_status boolean,
                                                                   f_antigo_possui_hubodometro boolean,
                                                                   f_total_edicoes smallint,
                                                                   f_cod_colaborador_edicao bigint,
                                                                   f_origem_edicao text,
                                                                   f_data_hora_edicao timestamp with time zone,
                                                                   f_informacoes_extras_edicao text)
    returns bigint
    language plpgsql
as
$$
declare
    v_cod_edicao_historico bigint;
begin
    insert into veiculo_edicao_historico (cod_empresa_veiculo,
                                          cod_veiculo_edicao,
                                          cod_colaborador_edicao,
                                          data_hora_edicao_tz_aplicado,
                                          origem_edicao,
                                          total_edicoes_processo,
                                          informacoes_extras,
                                          placa,
                                          identificador_frota,
                                          km,
                                          status,
                                          cod_diagrama_veiculo,
                                          cod_tipo_veiculo,
                                          cod_modelo_veiculo,
                                          possui_hubodometro)
    values (f_cod_empresa_veiculo,
            f_cod_veiculo,
            f_cod_colaborador_edicao,
            f_data_hora_edicao at time zone tz_unidade(f_cod_unidade_veiculo),
            f_origem_edicao,
            f_total_edicoes,
            f_informacoes_extras_edicao,
            f_antiga_placa,
            f_antigo_identificador_frota,
            f_antigo_km,
            f_antigo_status,
            f_antigo_cod_diagrama,
            f_antigo_cod_tipo,
            f_antigo_cod_modelo,
            f_antigo_possui_hubodometro)
    returning codigo into v_cod_edicao_historico;

    if v_cod_edicao_historico is null or v_cod_edicao_historico <= 0
    then
        perform throw_generic_error('Erro ao gerar o histórico de edição, tente novamente.');
    end if;

    return v_cod_edicao_historico;
end;
$$;


drop function func_veiculo_atualiza_veiculo(f_cod_veiculo bigint,
    f_nova_placa text,
    f_novo_identificador_frota text,
    f_novo_km bigint,
    f_novo_cod_tipo bigint,
    f_novo_cod_modelo bigint,
    f_novo_status boolean,
    f_cod_colaborador_edicao bigint,
    f_origem_edicao text,
    f_data_hora_edicao timestamp with time zone,
    f_informacoes_extras_edicao text);

create or replace function func_veiculo_atualiza_veiculo(f_cod_veiculo bigint,
                                                         f_nova_placa text,
                                                         f_novo_identificador_frota text,
                                                         f_novo_km bigint,
                                                         f_novo_cod_tipo bigint,
                                                         f_novo_cod_modelo bigint,
                                                         f_novo_status boolean,
                                                         f_novo_possui_hubodometro boolean,
                                                         f_cod_colaborador_edicao bigint,
                                                         f_origem_edicao text,
                                                         f_data_hora_edicao timestamp with time zone,
                                                         f_informacoes_extras_edicao text)
    returns table
            (
                cod_edicao_historico       bigint,
                total_edicoes              smallint,
                antiga_placa               text,
                antigo_identificador_frota text,
                antigo_km                  bigint,
                antigo_cod_diagrama        bigint,
                antigo_cod_tipo            bigint,
                antigo_cod_modelo          bigint,
                antigo_status              boolean,
                antigo_possui_hubodometro  boolean
            )
    language plpgsql
as
$$
declare
    v_cod_empresa       constant bigint not null  := (select v.cod_empresa
                                                      from veiculo v
                                                      where v.codigo = f_cod_veiculo);
    v_novo_cod_diagrama constant bigint not null  := (select vt.cod_diagrama
                                                      from veiculo_tipo vt
                                                      where vt.codigo = f_novo_cod_tipo
                                                        and vt.cod_empresa = v_cod_empresa);
    v_novo_cod_marca    constant bigint not null  := (select mv.cod_marca
                                                      from modelo_veiculo mv
                                                      where mv.codigo = f_novo_cod_modelo);
    v_novo_motorizado   constant boolean not null := (select vd.motorizado
                                                      from veiculo_diagrama vd
                                                      where vd.codigo = v_novo_cod_diagrama);
    v_cod_edicao_historico       bigint;
    v_total_edicoes              smallint;
    v_cod_unidade                bigint;
    v_antiga_placa               text;
    v_antigo_identificador_frota text;
    v_antigo_km                  bigint;
    v_antigo_cod_diagrama        bigint;
    v_antigo_cod_tipo            bigint;
    v_antigo_cod_marca           bigint;
    v_antigo_cod_modelo          bigint;
    v_antigo_status              boolean;
    v_antigo_possui_hubodometro  boolean;
begin
    select v.cod_unidade,
           v.placa,
           v.identificador_frota,
           v.km,
           v.cod_diagrama,
           v.cod_tipo,
           mv.cod_marca,
           v.cod_modelo,
           v.status_ativo,
           v.possui_hubodometro
    into strict
        v_cod_unidade,
        v_antiga_placa,
        v_antigo_identificador_frota,
        v_antigo_km,
        v_antigo_cod_diagrama,
        v_antigo_cod_tipo,
        v_antigo_cod_marca,
        v_antigo_cod_modelo,
        v_antigo_status,
        v_antigo_possui_hubodometro
    from veiculo v
             join modelo_veiculo mv on v.cod_modelo = mv.codigo
    where v.codigo = f_cod_veiculo;

    -- Validamos se o km foi inputado corretamente.
    if (f_novo_km < 0)
    then
        perform throw_generic_error(
                'A quilometragem do veículo não pode ser um número negativo.');
    end if;

    -- Validamos se o tipo foi alterado mesmo com o veículo contendo pneus aplicados.
    if ((v_antigo_cod_tipo <> f_novo_cod_tipo)
        and (select count(vp.*)
             from veiculo_pneu vp
             where vp.placa = (select v.placa from veiculo v where v.codigo = f_cod_veiculo)) > 0)
    then
        perform throw_generic_error(
                'O tipo do veículo não pode ser alterado se a placa contém pneus aplicados.');
    end if;

    -- Agora que passou nas verificações, calcula quantas alterações foram feitas:
    -- hstore é uma estrutura que salva os dados como chave => valor. Fazendo hstore(novo) - hstore(antigo) irá
    -- sobrar apenas as entradas (chave => valor) que mudaram. Depois, aplicamos um akeys(hstore), que retorna um
    -- array das chaves (apenas as que mudaram) (poderia ser um avalues(hstore) também). Por fim, fazemos um
    -- f_size_array para saber o tamanho desse array: isso nos dá o número de edições realizadas.
    -- IMPORTANTE: como a placa não é atualiza no update abaixo, também ignoramos ela na contagem de total de edições.
    v_total_edicoes := f_size_array(akeys(hstore((f_novo_identificador_frota,
                                                  f_novo_km,
                                                  v_novo_cod_diagrama,
                                                  f_novo_cod_tipo,
                                                  v_novo_cod_marca,
                                                  f_novo_cod_modelo,
                                                  f_novo_status,
                                                  f_novo_possui_hubodometro)) - hstore((v_antigo_identificador_frota,
                                                                                        v_antigo_km,
                                                                                        v_antigo_cod_diagrama,
                                                                                        v_antigo_cod_tipo,
                                                                                        v_antigo_cod_marca,
                                                                                        v_antigo_cod_modelo,
                                                                                        v_antigo_status,
                                                                                        v_antigo_possui_hubodometro))));

    -- O update no veículo só será feito se algo de fato mudou. E algo só mudou se o total de edições for maior que 0.
    if (v_total_edicoes is not null and v_total_edicoes > 0)
    then
        v_cod_edicao_historico := func_veiculo_gera_historico_atualizacao(v_cod_empresa,
                                                                          v_cod_unidade,
                                                                          f_cod_veiculo,
                                                                          v_antiga_placa,
                                                                          v_antigo_identificador_frota,
                                                                          v_antigo_km,
                                                                          v_antigo_cod_diagrama,
                                                                          v_antigo_cod_tipo,
                                                                          v_antigo_cod_modelo,
                                                                          v_antigo_status,
                                                                          v_antigo_possui_hubodometro,
                                                                          v_total_edicoes,
                                                                          f_cod_colaborador_edicao,
                                                                          f_origem_edicao,
                                                                          f_data_hora_edicao,
                                                                          f_informacoes_extras_edicao);

        update veiculo
        set identificador_frota = f_novo_identificador_frota,
            km                  = f_novo_km,
            cod_modelo          = f_novo_cod_modelo,
            cod_tipo            = f_novo_cod_tipo,
            cod_diagrama        = v_novo_cod_diagrama,
            status_ativo        = f_novo_status,
            motorizado          = v_novo_motorizado,
            possui_hubodometro  = f_novo_possui_hubodometro,
            foi_editado         = true
        where codigo = f_cod_veiculo
          and cod_empresa = v_cod_empresa;

        -- Verificamos se o update na tabela de veículos ocorreu com êxito.
        if (not found)
        then
            perform throw_generic_error('Não foi possível atualizar o veículo, tente novamente.');
        end if;
    end if;

    return query
        select v_cod_edicao_historico,
               v_total_edicoes,
               v_antiga_placa,
               v_antigo_identificador_frota,
               v_antigo_km,
               v_antigo_cod_diagrama,
               v_antigo_cod_tipo,
               v_antigo_cod_modelo,
               v_antigo_status,
               v_antigo_possui_hubodometro;
end;
$$;
----------------------------------------------------- INTEGRAÇÕES -----------------------------------------------------
create or replace function
    integracao.func_veiculo_ativa_desativa_veiculo_prolog(f_placa_veiculo text,
                                                          f_ativar_desativar_veiculo boolean,
                                                          f_data_hora_edicao_veiculo timestamp with time zone,
                                                          f_token_integracao text)
    returns bigint
    language plpgsql
as
$$
declare
    v_cod_empresa_veiculo     bigint;
    v_cod_unidade_veiculo     bigint;
    v_cod_veiculo_prolog      bigint;
    v_identificador_frota     text;
    v_novo_km_veiculo         bigint;
    v_novo_cod_tipo_veiculo   bigint;
    v_novo_cod_modelo_veiculo bigint;
    v_novo_possui_hubodometro boolean;
begin
    -- Não usamos 'strict' propositalmente pois não queremos quebrar no select. Deixamos as próprias validações da
    -- function verificarem e quebrarem.
    select vd.cod_empresa,
           vd.cod_unidade,
           vd.codigo,
           vd.identificador_frota,
           vd.km,
           vd.cod_tipo,
           vd.cod_modelo,
           vd.possui_hubodometro
    into
        v_cod_empresa_veiculo,
        v_cod_unidade_veiculo,
        v_cod_veiculo_prolog,
        v_identificador_frota,
        v_novo_km_veiculo,
        v_novo_cod_tipo_veiculo,
        v_novo_cod_modelo_veiculo,
        v_novo_possui_hubodometro
    from veiculo_data vd
    where vd.placa = f_placa_veiculo;

    -- Validamos se a unidade pertence a mesma empresa do token.
    perform integracao.func_garante_token_empresa(
            v_cod_empresa_veiculo,
            f_token_integracao,
            format('[ERRO DE VÍNCULO] O token "%s" não está autorizado a atualizar dados da unidade "%s",
                   verificar vínculos', f_token_integracao, v_cod_unidade_veiculo));

    -- Validamos se a placa já existe no Prolog.
    if (select not exists(select v.codigo from public.veiculo_data v where v.placa::text = f_placa_veiculo))
    then
        perform public.throw_generic_error(
                format('[ERRO DE DADOS] A placa "%s" não existe no Sistema ProLog', f_placa_veiculo));
    end if;

    perform func_veiculo_atualiza_veiculo(v_cod_veiculo_prolog,
                                          f_placa_veiculo,
                                          v_identificador_frota,
                                          v_novo_km_veiculo,
                                          v_novo_cod_tipo_veiculo,
                                          v_novo_cod_modelo_veiculo,
                                          f_ativar_desativar_veiculo,
                                          v_novo_possui_hubodometro,
                                          null,
                                          'API',
                                          f_data_hora_edicao_veiculo,
                                          f_token_integracao);

    update integracao.veiculo_cadastrado
    set data_hora_ultima_edicao = f_data_hora_edicao_veiculo
    where cod_empresa_cadastro = v_cod_empresa_veiculo
      and placa_veiculo_cadastro = f_placa_veiculo;

    -- Verificamos se o update na tabela de mapeamento de veículos cadastrados na integração ocorreu com êxito.
    if not found
    then
        perform throw_generic_error(
                format('Não foi possível atualizar a placa "%s" na tabela de mapeamento', f_placa_veiculo));
    end if;

    return v_cod_veiculo_prolog;
end;
$$;

create or replace function
    integracao.func_veiculo_sobrescreve_veiculo_cadastrado(f_placa_veiculo text,
                                                           f_cod_unidade_veiculo bigint,
                                                           f_km_atual_veiculo bigint,
                                                           f_cod_tipo_veiculo bigint,
                                                           f_cod_modelo_veiculo bigint)
    returns void
    language plpgsql
as
$$
declare
    v_cod_empresa_veiculo       bigint;
    v_cod_unidade_atual_veiculo bigint;
    v_cod_veiculo_prolog        bigint;
    v_novo_identificador_frota  text;
    v_novo_status_veiculo       boolean;
    v_novo_possui_hubodometro   boolean;
begin
    select vd.cod_empresa,
           vd.cod_unidade,
           vd.codigo,
           vd.identificador_frota,
           vd.status_ativo,
           vd.possui_hubodometro
    into strict
        v_cod_empresa_veiculo,
        v_cod_unidade_atual_veiculo,
        v_cod_veiculo_prolog,
        v_novo_identificador_frota,
        v_novo_status_veiculo,
        v_novo_possui_hubodometro
    from veiculo_data vd
    where vd.placa = f_placa_veiculo;

    -- Devemos tratar os serviços abertos para o veículo (setar fechado_integracao), apenas se a unidade mudar.
    if (v_cod_unidade_atual_veiculo <> f_cod_unidade_veiculo)
    then
        perform integracao.func_veiculo_deleta_servicos_abertos_placa(f_placa_veiculo,
                                                                      f_cod_unidade_veiculo);

        -- A function que atualiza veículo não atualiza o código da unidade, pois essa coluna não deve mesmo mudar em
        -- um update convencional, apenas através de uma transferência entre unidades, que é um outro processo.
        -- Como a integração precisa desse comportamento, precisamos fazer um novo update dessa coluna caso os códigos
        -- de unidade tenha sido alterados.
        update veiculo
        set cod_unidade = f_cod_unidade_veiculo
        where codigo = v_cod_veiculo_prolog
          and cod_unidade = v_cod_unidade_atual_veiculo;
    end if;

    perform func_veiculo_atualiza_veiculo(v_cod_veiculo_prolog,
                                          f_placa_veiculo,
                                          v_novo_identificador_frota,
                                          f_km_atual_veiculo,
                                          f_cod_tipo_veiculo,
                                          f_cod_modelo_veiculo,
                                          v_novo_status_veiculo,
                                          v_novo_possui_hubodometro,
                                          null,
                                          'API',
                                          now(),
                                          null);
end;
$$;

drop function integracao.func_veiculo_atualiza_veiculo_prolog(f_cod_unidade_original_alocado bigint,
    f_placa_original_veiculo text,
    f_novo_cod_unidade_alocado bigint,
    f_nova_placa_veiculo text,
    f_novo_km_veiculo bigint,
    f_novo_cod_modelo_veiculo bigint,
    f_novo_cod_tipo_veiculo bigint,
    f_data_hora_edicao_veiculo timestamp with time zone,
    f_token_integracao text);

create or replace function
    integracao.func_veiculo_atualiza_veiculo_prolog(f_cod_unidade_original_alocado bigint,
                                                    f_placa_original_veiculo text,
                                                    f_novo_cod_unidade_alocado bigint,
                                                    f_nova_placa_veiculo text,
                                                    f_novo_km_veiculo bigint,
                                                    f_novo_cod_modelo_veiculo bigint,
                                                    f_novo_cod_tipo_veiculo bigint,
                                                    f_data_hora_edicao_veiculo timestamp with time zone,
                                                    f_token_integracao text,
                                                    f_novo_possui_hubodometro boolean default false)
    returns bigint
    language plpgsql
as
$$
declare
    v_cod_empresa_veiculo constant bigint not null := (select u.cod_empresa
                                                       from public.unidade u
                                                       where u.codigo = f_cod_unidade_original_alocado);
    v_cod_veiculo_prolog           bigint;
    v_identificador_frota          text;
    v_status_ativo                 boolean;
begin
    -- Validamos se o usuário trocou a unidade alocada do veículo.
    if (f_cod_unidade_original_alocado <> f_novo_cod_unidade_alocado)
    then
        perform public.throw_generic_error(
                '[ERRO DE OPERAÇÃO] Para mudar a Unidade do veículo, utilize a transferência de veículo');
    end if;

    -- Validamos se o usuário trocou a placa do veículo.
    if (f_placa_original_veiculo <> f_nova_placa_veiculo)
    then
        perform public.throw_generic_error(
                '[ERRO DE OPERAÇÃO] O ProLog não permite a edição da placa do veículo');
    end if;

    -- Validamos se a Unidade do veículo trocou.
    if ((select v.cod_unidade
         from public.veiculo_data v
         where v.placa = f_placa_original_veiculo) <> f_cod_unidade_original_alocado)
    then
        perform public.throw_generic_error(
                '[ERRO DE OPERAÇÃO] Para mudar a Unidade do veículo, utilize a transferência de veículo');
    end if;

    -- Validamos se a Unidade pertence a mesma empresa do token.
    if ((select u.cod_empresa from public.unidade u where u.codigo = f_novo_cod_unidade_alocado)
        not in (select ti.cod_empresa
                from integracao.token_integracao ti
                where ti.token_integracao = f_token_integracao))
    then
        perform public.throw_generic_error(
                format(
                        '[ERRO DE VÍNCULO] O token "%s" não está autorizado a atualizar dados da unidade "%s", verificar vínculos',
                        f_token_integracao,
                        f_novo_cod_unidade_alocado));
    end if;

    -- Validamos se a placa já existe no ProLog.
    if (select not exists(select v.codigo from public.veiculo_data v where v.placa::text = f_nova_placa_veiculo))
    then
        perform public.throw_generic_error(
                format('[ERRO DE DADOS] A placa "%s" não existe no Sistema ProLog', f_nova_placa_veiculo));
    end if;

    -- Validamos se o modelo do veículo está mapeado.
    if (select not exists(select codigo
                          from public.modelo_veiculo mv
                          where mv.cod_empresa = v_cod_empresa_veiculo
                            and mv.codigo = f_novo_cod_modelo_veiculo))
    then
        perform public.throw_generic_error(
                '[ERRO DE VINCULO] O modelo do veículo não está mapeado corretamente, verificar vínculos');
    end if;

    -- Validamos se o tipo do veículo está mapeado.
    if (select not exists(select codigo
                          from public.veiculo_tipo vt
                          where vt.codigo = f_novo_cod_tipo_veiculo
                            and vt.cod_empresa = v_cod_empresa_veiculo))
    then
        perform public.throw_generic_error(
                '[ERRO DE VINCULO] O tipo do veículo não está mapeado corretamente, verificar vínculos');
    end if;

    select vd.codigo,
           vd.identificador_frota,
           vd.status_ativo
    into strict
        v_cod_veiculo_prolog,
        v_identificador_frota,
        v_status_ativo
    from veiculo_data vd
    where vd.placa = f_placa_original_veiculo
      and vd.cod_unidade = f_cod_unidade_original_alocado;

    perform func_veiculo_atualiza_veiculo(v_cod_veiculo_prolog,
                                          f_nova_placa_veiculo,
                                          v_identificador_frota,
                                          f_novo_km_veiculo,
                                          f_novo_cod_tipo_veiculo,
                                          f_novo_cod_modelo_veiculo,
                                          v_status_ativo,
                                          f_novo_possui_hubodometro,
                                          null,
                                          'API',
                                          f_data_hora_edicao_veiculo,
                                          f_token_integracao);

    update integracao.veiculo_cadastrado
    set data_hora_ultima_edicao = f_data_hora_edicao_veiculo
    where cod_empresa_cadastro = v_cod_empresa_veiculo
      and placa_veiculo_cadastro = f_placa_original_veiculo;

    -- Verificamos se o update na tabela de mapeamento de veículos cadastrados na integração ocorreu com êxito.
    if not found
    then
        perform throw_generic_error(
                format('Não foi possível atualizar a placa "%s" na tabela de mapeamento', F_PLACA_ORIGINAL_VEICULO));
    end if;

    return v_cod_veiculo_prolog;
end;
$$;

drop function integracao.func_veiculo_insere_veiculo_prolog(f_cod_unidade_veiculo_alocado bigint,
    f_placa_veiculo_cadastrado text,
    f_km_atual_veiculo_cadastrado bigint,
    f_cod_modelo_veiculo_cadastrado bigint,
    f_cod_tipo_veiculo_cadastrado bigint,
    f_data_hora_veiculo_cadastro timestamp with time zone,
    f_token_integracao text);
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_VEICULO_INSERE_VEICULO_PROLOG(F_COD_UNIDADE_VEICULO_ALOCADO BIGINT,
                                                  F_PLACA_VEICULO_CADASTRADO TEXT,
                                                  F_KM_ATUAL_VEICULO_CADASTRADO BIGINT,
                                                  F_COD_MODELO_VEICULO_CADASTRADO BIGINT,
                                                  F_COD_TIPO_VEICULO_CADASTRADO BIGINT,
                                                  F_DATA_HORA_VEICULO_CADASTRO TIMESTAMP WITH TIME ZONE,
                                                  F_TOKEN_INTEGRACAO TEXT,
                                                  F_POSSUI_HUBODOMETRO BOOLEAN DEFAULT FALSE)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_EMPRESA_VEICULO       CONSTANT BIGINT  := (SELECT U.COD_EMPRESA
                                                   FROM PUBLIC.UNIDADE U
                                                   WHERE U.CODIGO = F_COD_UNIDADE_VEICULO_ALOCADO);
    DEVE_SOBRESCREVER_VEICULO CONSTANT BOOLEAN := (SELECT *
                                                   FROM INTEGRACAO.FUNC_EMPRESA_GET_CONFIG_SOBRESCREVE_VEICULOS(
                                                           COD_EMPRESA_VEICULO));
    VEICULO_ESTA_NO_PROLOG    CONSTANT BOOLEAN := (SELECT EXISTS(SELECT V.CODIGO
                                                                 FROM PUBLIC.VEICULO_DATA V
                                                                 WHERE V.PLACA::TEXT = F_PLACA_VEICULO_CADASTRADO));
    STATUS_ATIVO_VEICULO      CONSTANT BOOLEAN := TRUE;
    COD_VEICULO_PROLOG                 BIGINT;
    F_QTD_ROWS_ALTERADAS               BIGINT;
    V_INSERT_COD_DIAGRAMA              BIGINT;
    V_INSERT_MOTORIZADO                BOOLEAN;
BEGIN
    -- Validamos se a Unidade pertence a mesma empresa do token.
    PERFORM INTEGRACAO.FUNC_GARANTE_TOKEN_EMPRESA(
            COD_EMPRESA_VEICULO,
            F_TOKEN_INTEGRACAO,
            FORMAT('[ERRO DE VÍNCULO] O token "%s" não está autorizado a inserir dados da unidade "%s",
                     confira se está usando o token correto', F_TOKEN_INTEGRACAO, F_COD_UNIDADE_VEICULO_ALOCADO));

    -- Validamos se o KM foi inputado corretamente.
    IF (F_KM_ATUAL_VEICULO_CADASTRADO < 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                '[ERRO DE DADOS] A quilometragem do veículo não pode ser um número negativo');
    END IF;

    -- Validamos se o modelo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM PUBLIC.MODELO_VEICULO MV
                          WHERE MV.COD_EMPRESA = COD_EMPRESA_VEICULO
                            AND MV.CODIGO = F_COD_MODELO_VEICULO_CADASTRADO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                '[ERRO DE VINCULO] O modelo do veículo não está mapeado corretamente, verificar vinculos');
    END IF;

    -- Validamos se o tipo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM PUBLIC.VEICULO_TIPO VT
                          WHERE VT.CODIGO = F_COD_TIPO_VEICULO_CADASTRADO
                            AND VT.COD_EMPRESA = COD_EMPRESA_VEICULO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                '[ERRO DE VINCULO] O tipo do veículo não está mapeado corretamente, verificar vinculos');
    END IF;

    -- Validamos se a placa já existe no ProLog.
    IF (VEICULO_ESTA_NO_PROLOG AND NOT DEVE_SOBRESCREVER_VEICULO)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                FORMAT('[ERRO DE DADOS] A placa "%s" já está cadastrada no Sistema ProLog',
                       F_PLACA_VEICULO_CADASTRADO));
    END IF;

    IF (VEICULO_ESTA_NO_PROLOG AND DEVE_SOBRESCREVER_VEICULO)
    THEN
        -- Buscamos o código do veículo que será sobrescrito.
        SELECT V.CODIGO
        FROM VEICULO V
        WHERE V.PLACA = F_PLACA_VEICULO_CADASTRADO
          AND V.COD_EMPRESA = COD_EMPRESA_VEICULO
        INTO COD_VEICULO_PROLOG;

        -- Removemos os pneus aplicados na placa, para que ela possa receber novos pneus.
        PERFORM INTEGRACAO.FUNC_PNEU_REMOVE_VINCULO_PNEU_PLACA_POSICAO_BY_PLACA(F_PLACA_VEICULO_CADASTRADO);

        -- Sebrescrevemos os dados do veículo.
        PERFORM INTEGRACAO.FUNC_VEICULO_SOBRESCREVE_VEICULO_CADASTRADO(
                F_PLACA_VEICULO_CADASTRADO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                F_KM_ATUAL_VEICULO_CADASTRADO,
                F_COD_TIPO_VEICULO_CADASTRADO,
                F_COD_MODELO_VEICULO_CADASTRADO);

    ELSE
        SELECT VT.COD_DIAGRAMA, VD.MOTORIZADO
        FROM PUBLIC.VEICULO_TIPO VT
                 JOIN VEICULO_DIAGRAMA VD
                      ON VD.CODIGO = VT.COD_DIAGRAMA
        WHERE VT.CODIGO = F_COD_TIPO_VEICULO_CADASTRADO
          AND VT.COD_EMPRESA = COD_EMPRESA_VEICULO
        INTO V_INSERT_COD_DIAGRAMA, V_INSERT_MOTORIZADO;
        -- Aqui devemos apenas inserir o veículo no ProLog.
        INSERT INTO PUBLIC.VEICULO(COD_EMPRESA,
                                   COD_UNIDADE,
                                   PLACA,
                                   KM,
                                   STATUS_ATIVO,
                                   COD_TIPO,
                                   COD_DIAGRAMA,
                                   MOTORIZADO,
                                   COD_MODELO,
                                   COD_UNIDADE_CADASTRO,
                                   POSSUI_HUBODOMETRO)
        VALUES (COD_EMPRESA_VEICULO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                F_PLACA_VEICULO_CADASTRADO,
                F_KM_ATUAL_VEICULO_CADASTRADO,
                STATUS_ATIVO_VEICULO,
                F_COD_TIPO_VEICULO_CADASTRADO,
                V_INSERT_COD_DIAGRAMA,
                V_INSERT_MOTORIZADO,
                F_COD_MODELO_VEICULO_CADASTRADO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                F_POSSUI_HUBODOMETRO)
        RETURNING CODIGO INTO COD_VEICULO_PROLOG;
    END IF;

    IF (DEVE_SOBRESCREVER_VEICULO)
    THEN
        -- Se permite sobrescrita de dados, então tentamos inserir, caso a constraint estourar,
        -- apenas atualizamos os dados. Tentamos inserir antes, pois, em cenários onde o veículo já encontra-se no
        -- ProLog, não temos nenhuma entrada para ele na tabela de mapeamento.
        INSERT INTO INTEGRACAO.VEICULO_CADASTRADO(COD_EMPRESA_CADASTRO,
                                                  COD_UNIDADE_CADASTRO,
                                                  COD_VEICULO_CADASTRO_PROLOG,
                                                  PLACA_VEICULO_CADASTRO,
                                                  DATA_HORA_CADASTRO_PROLOG)
        VALUES (COD_EMPRESA_VEICULO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                COD_VEICULO_PROLOG,
                F_PLACA_VEICULO_CADASTRADO,
                F_DATA_HORA_VEICULO_CADASTRO)
        ON CONFLICT ON CONSTRAINT UNIQUE_PLACA_CADASTRADA_EMPRESA_INTEGRACAO
            DO UPDATE SET COD_VEICULO_CADASTRO_PROLOG = COD_VEICULO_PROLOG,
                          COD_UNIDADE_CADASTRO        = F_COD_UNIDADE_VEICULO_ALOCADO,
                          DATA_HORA_ULTIMA_EDICAO     = F_DATA_HORA_VEICULO_CADASTRO;
    ELSE
        -- Se não houve sobrescrita de dados, significa que devemos apenas inserir os dados na tabela de mapeamento.
        INSERT INTO INTEGRACAO.VEICULO_CADASTRADO(COD_EMPRESA_CADASTRO,
                                                  COD_UNIDADE_CADASTRO,
                                                  COD_VEICULO_CADASTRO_PROLOG,
                                                  PLACA_VEICULO_CADASTRO,
                                                  DATA_HORA_CADASTRO_PROLOG)
        VALUES (COD_EMPRESA_VEICULO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                COD_VEICULO_PROLOG,
                F_PLACA_VEICULO_CADASTRADO,
                F_DATA_HORA_VEICULO_CADASTRO);
    END IF;

    GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

    -- VERIFICAMOS SE O INSERT NA TABELA DE MAPEAMENTO DE VEÍCULOS CADASTRADOS NA INTEGRAÇÃO OCORREU COM ÊXITO.
    IF (F_QTD_ROWS_ALTERADAS <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível inserir a placa "%" na tabela de mapeamento', F_PLACA_VEICULO_CADASTRADO;
    END IF;

    RETURN COD_VEICULO_PROLOG;
END;
$$;
