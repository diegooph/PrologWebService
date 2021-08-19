alter table dimensao_pneu add column cod_empresa bigint;
alter table dimensao_pneu add constraint fk_cod_empresa foreign key (cod_empresa) references empresa (codigo);
alter table dimensao_pneu add column cod_auxiliar text;
alter table dimensao_pneu add constraint unique_dimensao_cod_empresa unique (codigo, cod_empresa);
alter table dimensao_pneu add column status_ativo boolean;
alter table dimensao_pneu add column origem_cadastro text;
drop view view_pneu_analise_vida_atual;
drop view view_analise_pneus;
alter table dimensao_pneu alter column aro type numeric(7,2);
alter table dimensao_pneu alter column altura type numeric(7,2);
alter table dimensao_pneu alter column largura type numeric(7,2);

create or replace view view_pneu_analise_vida_atual as
with dados as (
    select vpav.cod_pneu,
           vpav.vida_analisada_pneu,
           vpav.status,
           vpav.valor_pneu,
           vpav.valor_banda,
           vpav.quantidade_afericoes_pneu_vida,
           vpav.data_hora_primeira_afericao,
           vpav.data_hora_ultima_afericao,
           vpav.total_dias_ativo,
           vpav.total_km_rodado_vida,
           vpav.maior_sulco_aferido_vida,
           vpav.menor_sulco_aferido_vida,
           vpav.sulco_gasto,
           vpav.sulco_restante,
           vpav.km_por_mm_vida,
           vpav.valor_por_km_vida
    from view_pneu_analise_vidas vpav
)
select p.cod_unidade                                                 as cod_unidade,
       u.nome                                                        as nome_unidade_alocado,
       p.codigo                                                      as cod_pneu,
       p.codigo_cliente                                              as cod_cliente_pneu,
       p.valor + sum(pvv.valor)                                      as valor_acumulado,
       sum(v.total_km_rodado_todas_vidas)                            as km_acumulado,
       p.vida_atual                                                  as vida_atual,
       p.status                                                      as status_pneu,
       p.valor                                                       as valor_pneu,
       case
           when dados.vida_analisada_pneu = 1
               then dados.valor_pneu
           else dados.valor_banda
           end                                                       as valor_vida_atual,
       map.nome                                                      as nome_marca,
       mp.nome                                                       as nome_modelo,
       dp.largura || '/' || dp.altura || ' R' || dp.aro              as medidas,
       dados.quantidade_afericoes_pneu_vida                          as qtd_afericoes,
       dados.data_hora_primeira_afericao                             as data_hora_primeira_afericao,
       dados.data_hora_ultima_afericao                               as data_hora_ultima_afericao,
       dados.total_dias_ativo                                        as dias_ativo,
       round(
               case
                   when dados.total_dias_ativo > 0
                       then dados.total_km_rodado_vida / dados.total_dias_ativo::numeric
                   else null::numeric
                   end)                                              as media_km_por_dia,
       round(dados.maior_sulco_aferido_vida::numeric, 2)             as maior_sulco_vida,
       round(dados.menor_sulco_aferido_vida::numeric, 2)             as menor_sulco_vida,
       round(dados.sulco_gasto::numeric, 2)                          as milimetros_gastos,
       round(dados.km_por_mm_vida::numeric, 2)                       as kms_por_milimetro,
       round(dados.valor_por_km_vida::numeric, 2)                    as valor_por_km,
       round((
                 case
                     when (sum(v.total_km_rodado_todas_vidas) > 0::numeric)
                         then (p.valor + sum(pvv.valor)) /
                              sum(v.total_km_rodado_todas_vidas)::double precision
                     else 0
                     end)::numeric, 2)                               as valor_por_km_acumulado,
       round((dados.km_por_mm_vida * dados.sulco_restante)::numeric) as kms_a_percorrer,
       trunc(
               case
                   when (((dados.total_km_rodado_vida > 0::numeric) and (dados.total_dias_ativo > 0)) and
                         ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric) > 0::numeric)) then (
                           (dados.km_por_mm_vida * dados.sulco_restante) /
                           ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric))::double precision)
                   else 0::double precision
                   end)                                              as dias_restantes_pneu,
       case
           when (((dados.total_km_rodado_vida > 0::numeric) and (dados.total_dias_ativo > 0)) and
                 ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric) > 0::numeric)) then (
                   (((dados.km_por_mm_vida * dados.sulco_restante) /
                     ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric))::double precision))::integer +
                   current_date)
           else null::date
           end                                                       as data_prevista_troca,
       case
           when (p.vida_atual = p.vida_total) then 'DESCARTE'::text
           else 'ANÁLISE'::text
           end                                                       as destino_pneu
from pneu p
         join dados on dados.cod_pneu = p.codigo and dados.vida_analisada_pneu = p.vida_atual
         join dimensao_pneu dp on dp.codigo = p.cod_dimensao
         join unidade u on u.codigo = p.cod_unidade
         join modelo_pneu mp on mp.codigo = p.cod_modelo
         join marca_pneu map on map.codigo = mp.cod_marca
         join view_pneu_km_rodado_total v on p.codigo = v.cod_pneu and p.vida_atual = v.vida_pneu
         left join pneu_valor_vida pvv on pvv.cod_pneu = p.codigo
group by u.nome, p.codigo, p.valor, p.vida_atual, p.status, p.vida_total, p.codigo_cliente, p.cod_unidade,
         dados.valor_banda, dados.valor_pneu, map.nome,
         mp.nome, dp.largura, dp.altura, dp.aro, dados.quantidade_afericoes_pneu_vida,
         dados.data_hora_primeira_afericao, dados.data_hora_ultima_afericao, dados.total_dias_ativo,
         dados.total_km_rodado_vida, dados.maior_sulco_aferido_vida, dados.menor_sulco_aferido_vida, dados.sulco_gasto,
         dados.km_por_mm_vida, dados.valor_por_km_vida, dados.sulco_restante, dados.vida_analisada_pneu;

create or replace view view_analise_pneus as
select p.cod_unidade                                                                  as cod_unidade_alocado,
       u.nome                                                                         as nome_unidade_alocado,
       p.codigo                                                                       as cod_pneu,
       p.codigo_cliente                                                               as cod_cliente_pneu,
       p.status                                                                       as status_pneu,
       map.nome                                                                       as nome_marca,
       mp.nome                                                                        as nome_modelo,
       dp.largura || '/' || dp.altura || ' R' || dp.aro                               as medidas,
       dados.qt_afericoes                                                             as qtd_afericoes,
       to_char(dados.primeira_afericao::timestamp with time zone, 'DD/MM/YYYY'::text) as data_primeira_afericao,
       to_char(dados.ultima_afericao::timestamp with time zone, 'DD/MM/YYYY'::text)   as data_ultima_afericao,
       dados.total_dias                                                               as dias_ativo,
       round(case
                 when dados.total_dias > 0 then dados.total_km / dados.total_dias::numeric
                 else null::numeric
           end)                                                                       as media_km_por_dia,
       p.altura_sulco_interno,
       p.altura_sulco_central_interno,
       p.altura_sulco_central_externo,
       p.altura_sulco_externo,
       round(dados.maior_sulco::numeric, 2)                                           as maior_sulco_vida,
       round(dados.menor_sulco::numeric, 2)                                           as menor_sulco_vida,
       round(dados.sulco_gasto::numeric, 2)                                           as milimetros_gastos,
       round(dados.km_por_mm::numeric, 2)                                             as kms_por_milimetro,
       round((dados.km_por_mm * dados.sulco_restante)::numeric)                       as kms_a_percorrer,
       trunc(
               case
                   when dados.total_km > 0::numeric and dados.total_dias > 0 and
                        (dados.total_km / dados.total_dias::numeric) > 0::numeric then
                               dados.km_por_mm * dados.sulco_restante /
                               (dados.total_km / dados.total_dias::numeric)::double precision
                   else 0::double precision
                   end)                                                               as dias_restantes,
       case
           when dados.total_km > 0::numeric and dados.total_dias > 0 and
                (dados.total_km / dados.total_dias::numeric) > 0::numeric
               then (dados.km_por_mm * dados.sulco_restante /
                     (dados.total_km / dados.total_dias::numeric)::double precision)::integer +
                    current_date
           else null::date
           end                                                                        as previsao_troca
from pneu p
         join (select av.cod_pneu,
                      av.cod_unidade,
                      count(av.altura_sulco_central_interno)                                     as qt_afericoes,
                      min(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date                as primeira_afericao,
                      max(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date                as ultima_afericao,
                      max(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date -
                      min(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date                as total_dias,
                      max(total_km.total_km)                                                     as total_km,
                      max(greatest(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                   av.altura_sulco_central_externo, av.altura_sulco_externo))    as maior_sulco,
                      min(least(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                av.altura_sulco_central_externo, av.altura_sulco_externo))       as menor_sulco,
                      max(greatest(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                   av.altura_sulco_central_externo, av.altura_sulco_externo))
                          - min(least(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                      av.altura_sulco_central_externo, av.altura_sulco_externo)) as sulco_gasto,
                      case
                          when
                                  case
                                      when p_1.vida_atual = p_1.vida_total then min(least(av.altura_sulco_interno,
                                                                                          av.altura_sulco_central_interno,
                                                                                          av.altura_sulco_central_externo,
                                                                                          av.altura_sulco_externo)) -
                                                                                pru.sulco_minimo_descarte
                                      when p_1.vida_atual < p_1.vida_total then min(least(av.altura_sulco_interno,
                                                                                          av.altura_sulco_central_interno,
                                                                                          av.altura_sulco_central_externo,
                                                                                          av.altura_sulco_externo)) -
                                                                                pru.sulco_minimo_recapagem
                                      else null::real
                                      end < 0::double precision then 0::real
                          else
                              case
                                  when p_1.vida_atual = p_1.vida_total then min(least(av.altura_sulco_interno,
                                                                                      av.altura_sulco_central_interno,
                                                                                      av.altura_sulco_central_externo,
                                                                                      av.altura_sulco_externo)) -
                                                                            pru.sulco_minimo_descarte
                                  when p_1.vida_atual < p_1.vida_total then min(least(av.altura_sulco_interno,
                                                                                      av.altura_sulco_central_interno,
                                                                                      av.altura_sulco_central_externo,
                                                                                      av.altura_sulco_externo)) -
                                                                            pru.sulco_minimo_recapagem
                                  else null::real
                                  end
                          end                                                                    as sulco_restante,
                      case
                          when (max(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date -
                                min(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date) > 0 then
                                      max(total_km.total_km)::double precision / max(
                                          greatest(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                                   av.altura_sulco_central_externo, av.altura_sulco_externo)) - min(
                                              least(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                                    av.altura_sulco_central_externo, av.altura_sulco_externo))
                          else 0::double precision
                          end                                                                    as km_por_mm
               from afericao_valores av
                        join afericao a on a.codigo = av.cod_afericao
                        join pneu p_1 on p_1.codigo::text = av.cod_pneu::text and p_1.status::text = 'EM_USO'::text
                        join pneu_restricao_unidade pru on pru.cod_unidade = av.cod_unidade
                        join (select total_km_rodado.cod_pneu,
                                     total_km_rodado.cod_unidade,
                                     sum(total_km_rodado.km_rodado) as total_km
                              from (select av_1.cod_pneu,
                                           av_1.cod_unidade,
                                           max(a_1.km_veiculo) - min(a_1.km_veiculo) as km_rodado
                                    from afericao_valores av_1
                                             join afericao a_1 on a_1.codigo = av_1.cod_afericao
                                    group by av_1.cod_pneu, av_1.cod_unidade, a_1.cod_veiculo) total_km_rodado
                              group by total_km_rodado.cod_pneu, total_km_rodado.cod_unidade) total_km
                             on total_km.cod_pneu = av.cod_pneu and total_km.cod_unidade = av.cod_unidade
               group by av.cod_pneu, av.cod_unidade, p_1.vida_atual, p_1.vida_total, pru.sulco_minimo_descarte,
                        pru.sulco_minimo_recapagem) dados on dados.cod_pneu = p.codigo
         join dimensao_pneu dp on dp.codigo = p.cod_dimensao
         join unidade u on u.codigo = p.cod_unidade
         join modelo_pneu mp on mp.codigo = p.cod_modelo and mp.cod_empresa = u.cod_empresa
         join marca_pneu map on map.codigo = mp.cod_marca;

alter table dimensao_pneu add column data_hora_cadastro timestamp with time zone default now();
alter table dimensao_pneu add column cod_colaborador_cadastro bigint;
alter table dimensao_pneu add column data_hora_ultima_atualizacao timestamp with time zone;
alter table dimensao_pneu add column cod_colaborador_ultima_atualizacao bigint;

create or replace function suporte.func_pneu_cadastra_dimensao_pneu(f_altura bigint,
                                                                    f_largura bigint,
                                                                    f_aro real,
                                                                    out aviso_dimensao_criada text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    cod_dimensao_existente bigint := (select codigo
                                      from dimensao_pneu
                                      where largura = f_largura
                                        and altura = f_altura
                                        and aro = f_aro);
    cod_dimensao_criada    bigint;
begin
    perform throw_generic_error('o cadastro de dimensão deve ser realizado pelo site ou pelo app');
    perform suporte.func_historico_salva_execucao();
    --verifica se os dados informados são maiores que 0.
    if(f_altura < 0)
    then
        raise exception 'o valor atribuído para altura deve ser maior que 0(zero). valor informado: %', f_altura;
    end if;

    if(f_largura < 0)
    then
        raise exception 'o valor atribuído para largura deve ser maior que 0(zero). valor informado: %', f_largura;
    end if;

    if(f_aro < 0)
    then
        raise exception 'o valor atribuído para aro deve ser maior que 0(zero). valor informado: %', f_aro;
    end if;

    --verifica se essa dimensão existe na base de dados.
    if (cod_dimensao_existente is not null)
    then
        raise exception 'erro! essa dimensão já está cadastrada, possui o código = %.', cod_dimensao_existente;
    end if;

    --adiciona nova dimensão e retorna seu id.
    insert into dimensao_pneu(altura, largura, aro)
    values (f_altura, f_largura, f_aro) returning codigo into cod_dimensao_criada;

    --mensagem de sucesso.
    select 'dimensão cadastrada com sucesso! dimensão: ' || f_largura || '/' || f_altura || 'r' || f_aro ||
           ' com código: '
               || cod_dimensao_criada || '.'
    into aviso_dimensao_criada;
end
$$;

alter table dimensao_pneu drop constraint unique_dimensao_pneu;
alter table dimensao_pneu add constraint  unique_dimensao_pneu unique (altura, largura, aro, cod_empresa);
insert into dimensao_pneu (altura, largura, aro, cod_empresa, status_ativo,
                           cod_colaborador_cadastro, data_hora_ultima_atualizacao, cod_colaborador_ultima_atualizacao)
select distinct d.altura,
                d.largura,
                d.aro,
                e.codigo,
                true,
                2316,
                now(),
                2316
from dimensao_pneu d
         join pneu_data pd on
    d.codigo = pd.cod_dimensao
         join empresa e on pd.cod_empresa = e.codigo
where pd.cod_empresa not in (select ti.cod_empresa
                            from integracao.token_integracao ti);

insert into dimensao_pneu (altura, largura, aro, cod_empresa, status_ativo,
                           cod_colaborador_cadastro, data_hora_ultima_atualizacao, cod_colaborador_ultima_atualizacao,
                           cod_auxiliar)
select distinct d.altura,
                d.largura,
                d.aro,
                e.codigo,
                true,
                2316,
                now(),
                2316,
                d.codigo
from dimensao_pneu d
         join pneu_data pd on
        d.codigo = pd.cod_dimensao
         join empresa e on pd.cod_empresa = e.codigo
where pd.cod_empresa in (select ti.cod_empresa
                            from integracao.token_integracao ti);

update pneu_data pd
set cod_dimensao = (
    select d.codigo
    from dimensao_pneu d
             join dimensao_pneu d2 on d.aro = d2.aro and d.altura = d2.altura and d.largura = d2.largura
    where d.cod_empresa = pd.cod_empresa and d2.codigo = pd.cod_dimensao);

delete from dimensao_pneu where cod_empresa is null;

CREATE OR REPLACE FUNCTION IMPLANTACAO.TG_FUNC_PNEU_CONFERE_PLANILHA_IMPORTA_PNEU()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_ORIGEM_PNEU_CADASTRO        CONSTANT     TEXT     := 'INTERNO';
    F_VALOR_SIMILARIDADE          CONSTANT     REAL     := 0.4;
    F_VALOR_SIMILARIDADE_DIMENSAO CONSTANT     REAL     := 0.55;
    F_SEM_SIMILARIDADE            CONSTANT     REAL     := 0.0;
    F_QTD_ERROS                                SMALLINT := 0;
    F_MSGS_ERROS                               TEXT;
    F_QUEBRA_LINHA                             TEXT     := CHR(10);
    F_COD_MARCA_BANCO                          BIGINT;
    F_SIMILARIDADE_MARCA                       REAL;
    F_MARCA_MODELO                             TEXT;
    F_COD_MODELO_BANCO                         BIGINT;
    F_SIMILARIDADE_MODELO                      REAL;
    F_COD_MARCA_BANDA_BANCO                    BIGINT;
    F_SIMILARIDADE_MARCA_BANDA                 REAL;
    F_MARCA_MODELO_BANDA                       TEXT;
    F_COD_MODELO_BANDA_BANCO                   BIGINT;
    F_SIMILARIDADE_MODELO_BANDA                REAL;
    DATE_CONVERTER                             TEXT     := 'YYYYWW';
    PREFIXO_ANO                                TEXT     := SUBSTRING(CURRENT_TIMESTAMP::TEXT, 1, 2);
    DOT_EM_DATA                                DATE;
    F_COD_DIMENSAO                             BIGINT;
    F_SIMILARIDADE_DIMENSAO                    REAL;
    F_ALTURA_MIN_SULCOS                        REAL     := 1;
    F_ALTURA_MAX_SULCOS                        REAL     := 50;
    F_QTD_MIN_SULCOS                           SMALLINT := 1;
    F_QTD_MAX_SULCOS                           SMALLINT := 6;
    F_QTD_SULCOS_DEFAULT                       SMALLINT := 4;
    F_ERRO_SULCOS                              BOOLEAN  := FALSE;
    F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO      REAL     := 0;
    F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO_LOOP REAL;
    F_PNEU_NOVO_NUNCA_RODADO_ARRAY             TEXT[]   := ('{"SIM", "OK", "TRUE"}');
    F_PNEU_NOVO_NUNCA_RODADO                   TEXT;
    F_COD_TIPO_SERVICO                         BIGINT;
    F_COD_SERVICO_REALIZADO                    BIGINT;
    F_COD_PNEU                                 BIGINT;
BEGIN
    IF (TG_OP = 'UPDATE' AND OLD.STATUS_IMPORT_REALIZADO IS TRUE)
    THEN
        RETURN OLD;
    ELSE
        IF (TG_OP = 'UPDATE')
        THEN
            NEW.COD_UNIDADE = OLD.COD_UNIDADE;
            NEW.COD_EMPRESA = OLD.COD_EMPRESA;
        END IF;
        NEW.USUARIO_UPDATE := SESSION_USER;
        NEW.NUMERO_FOGO_FORMATADO_IMPORT = UPPER(REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.NUMERO_FOGO_EDITAVEL));
        NEW.MARCA_FORMATADA_IMPORT = REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MARCA_EDITAVEL);
        NEW.MODELO_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MODELO_EDITAVEL);
        NEW.DOT_FORMATADO_IMPORT := REMOVE_ALL_SPACES(NEW.DOT_EDITAVEL);
        NEW.DIMENSAO_FORMATADA_IMPORT := REMOVE_ALL_SPACES(NEW.DIMENSAO_EDITAVEL);
        NEW.MARCA_BANDA_FORMATADA_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MARCA_BANDA_EDITAVEL);
        NEW.MODELO_BANDA_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MODELO_BANDA_EDITAVEL);

        -- VERIFICA SE EMPRESA EXISTE
        IF NOT EXISTS(SELECT E.CODIGO FROM EMPRESA E WHERE E.CODIGO = NEW.COD_EMPRESA)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS =
                    concat(F_MSGS_ERROS, F_QTD_ERROS, '- NÃO EXISTE EMPRESA COM CÓDIGO INFORMADO', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICA SE UNIDADE EXISTE
        IF NOT EXISTS(SELECT U.CODIGO FROM UNIDADE U WHERE U.CODIGO = NEW.COD_UNIDADE)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS =
                    concat(F_MSGS_ERROS, F_QTD_ERROS, '- NÃO EXISTE UNIDADE COM CÓDIGO INFORMADO', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICA SE UNIDADE PERTENCE A EMPRESA
        IF NOT EXISTS(
                SELECT U.CODIGO FROM UNIDADE U WHERE U.CODIGO = NEW.COD_UNIDADE AND U.COD_EMPRESA = NEW.COD_EMPRESA)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS =
                    concat(F_MSGS_ERROS, F_QTD_ERROS, '- A UNIDADE NÃO PERTENCE A EMPRESA', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICAÇÕES NÚMERO DE FOGO.
        -- Número de fogo nulo: Erro.
        -- Número de fogo cadastrado em outra unidade da mesma empresa: Erro.
        -- Número de fogo cadastrado na mesma unidade: Erro.
        IF (NEW.NUMERO_FOGO_FORMATADO_IMPORT IS NOT NULL)
        THEN
            IF EXISTS(SELECT P.CODIGO_CLIENTE
                      FROM PNEU P
                      WHERE REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(P.CODIGO_CLIENTE) ILIKE
                            NEW.NUMERO_FOGO_FORMATADO_IMPORT
                        AND P.COD_EMPRESA = NEW.COD_EMPRESA
                        AND P.COD_UNIDADE != NEW.COD_UNIDADE)
            THEN
                F_QTD_ERROS = F_QTD_ERROS + 1;
                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                      '- O PNEU JÁ ESTÁ CADASTRADO E PERTENCE A OUTRA UNIDADE',
                                      F_QUEBRA_LINHA);
                NEW.STATUS_IMPORT_REALIZADO = TRUE;
            ELSE
                IF EXISTS(SELECT P.CODIGO_CLIENTE
                          FROM PNEU P
                          WHERE REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(P.CODIGO_CLIENTE) ILIKE
                                NEW.NUMERO_FOGO_FORMATADO_IMPORT
                            AND P.COD_EMPRESA = NEW.COD_EMPRESA
                            AND P.COD_UNIDADE = NEW.COD_UNIDADE)
                THEN
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                          '- O PNEU JÁ ESTÁ CADASTRADO NA UNIDADE INFORMADA',
                                          F_QUEBRA_LINHA);
                    NEW.STATUS_IMPORT_REALIZADO = TRUE;
                END IF;
            END IF;
        ELSE
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- O NÚMERO DE FOGO NÃO PODE SER NULO', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICAÇÕES MARCA.
        -- Marca nula: Erro.
        IF (NEW.MARCA_FORMATADA_IMPORT IS NOT NULL)
        THEN
            -- Procura marca similar no banco.
            SELECT DISTINCT ON (NEW.MARCA_FORMATADA_IMPORT) MAP.CODIGO                                                        AS COD_MARCA_BANCO,
                                                            MAX(FUNC_GERA_SIMILARIDADE(NEW.MARCA_FORMATADA_IMPORT, MAP.NOME)) AS SIMILARIEDADE_MARCA
            INTO F_COD_MARCA_BANCO, F_SIMILARIDADE_MARCA
            FROM MARCA_PNEU MAP
            GROUP BY NEW.MARCA_FORMATADA_IMPORT, NEW.MARCA_EDITAVEL, MAP.NOME, MAP.CODIGO
            ORDER BY NEW.MARCA_FORMATADA_IMPORT, SIMILARIEDADE_MARCA DESC;

            F_MARCA_MODELO := CONCAT(F_COD_MARCA_BANCO, NEW.MODELO_FORMATADO_IMPORT);
            -- Se a similaridade da marca for maior ou igual ao exigido: procura modelo.
            -- Se não for: Mostra erro de marca não encontrada (Não cadastra pois é nível Prolog).
            IF (F_SIMILARIDADE_MARCA >= F_VALOR_SIMILARIDADE)
            THEN
                -- VERIFICAÇÕES DE MODELO: Procura modelo similar no banco.
                IF (NEW.MODELO_FORMATADO_IMPORT IS NOT NULL)
                THEN
                    SELECT DISTINCT ON (F_MARCA_MODELO) MOP.CODIGO AS COD_MODELO_PNEU,
                                                        CASE
                                                            WHEN F_COD_MARCA_BANCO = MOP.COD_MARCA
                                                                THEN
                                                                MAX(FUNC_GERA_SIMILARIDADE(F_MARCA_MODELO,
                                                                                           CONCAT(MOP.COD_MARCA, MOP.NOME)))
                                                            ELSE F_SEM_SIMILARIDADE
                                                            END    AS SIMILARIEDADE_MODELO
                    INTO F_COD_MODELO_BANCO, F_SIMILARIDADE_MODELO
                    FROM MODELO_PNEU MOP
                    WHERE MOP.COD_EMPRESA = NEW.COD_EMPRESA
                    GROUP BY F_MARCA_MODELO, MOP.NOME, MOP.CODIGO
                    ORDER BY F_MARCA_MODELO, SIMILARIEDADE_MODELO DESC;

                    -- Se a similaridade do modelo for menor do que o exigido: cadastra novo modelo.
                    IF (F_SIMILARIDADE_MODELO < F_VALOR_SIMILARIDADE OR F_SIMILARIDADE_MODELO IS NULL)
                    THEN
                        BEGIN
                            -- VERIFICAÇÃO DE SULCOS.
                            -- Parse para smallint.
                            NEW.QTD_SULCOS_FORMATADA_IMPORT :=
                                    REPLACE(REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(
                                                    NEW.QTD_SULCOS_EDITAVEL), ',', '.')::SMALLINT;
                            IF (NEW.QTD_SULCOS_FORMATADA_IMPORT IS NULL)
                            THEN
                                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                      '- A QUANTIDADE DE SULCOS ESTAVA NULA, PORTANTO ASSUMIU O ' ||
                                                      'VALOR DEFAULT = 4',
                                                      F_QUEBRA_LINHA);
                                NEW.QTD_SULCOS_FORMATADA_IMPORT := F_QTD_SULCOS_DEFAULT;
                            ELSE
                                IF (NEW.QTD_SULCOS_FORMATADA_IMPORT < F_QTD_MIN_SULCOS)
                                THEN
                                    F_ERRO_SULCOS := TRUE;
                                    F_QTD_ERROS = F_QTD_ERROS + 1;
                                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                          '- A QUANTIDADE DE SULCOS NÃO PODE SER MENOR QUE 1',
                                                          F_QUEBRA_LINHA);
                                ELSE
                                    IF (NEW.QTD_SULCOS_FORMATADA_IMPORT > F_QTD_MAX_SULCOS)
                                    THEN
                                        F_ERRO_SULCOS := TRUE;
                                        F_QTD_ERROS = F_QTD_ERROS + 1;
                                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                              '- A QUANTIDADE DE SULCOS NÃO PODE SER MAIOR QUE 6',
                                                              F_QUEBRA_LINHA);
                                    END IF;
                                END IF;
                            END IF;
                        EXCEPTION
                            WHEN invalid_text_representation THEN -- error that can be handled
                                F_ERRO_SULCOS := TRUE;
                                F_QTD_ERROS = F_QTD_ERROS + 1;
                                F_MSGS_ERROS =
                                        concat(F_MSGS_ERROS, F_QTD_ERROS,
                                               '- QUANTIDADE DE SULCOS COM VALOR INCORRETO',
                                               F_QUEBRA_LINHA);
                        END;
                        IF (NEW.ALTURA_SULCOS_EDITAVEL IS NOT NULL)
                        THEN
                            BEGIN
                                NEW.ALTURA_SULCOS_FORMATADA_IMPORT :=
                                        REPLACE(REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(
                                                        NEW.ALTURA_SULCOS_EDITAVEL), ',', '.')::REAL;
                                IF (NEW.ALTURA_SULCOS_FORMATADA_IMPORT < F_ALTURA_MIN_SULCOS)
                                THEN
                                    F_ERRO_SULCOS := TRUE;
                                    F_QTD_ERROS = F_QTD_ERROS + 1;
                                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                          '- A ALTURA DOS SULCOS NÃO PODE SER MENOR QUE 1mm',
                                                          F_QUEBRA_LINHA);
                                ELSE
                                    IF (NEW.ALTURA_SULCOS_FORMATADA_IMPORT > F_ALTURA_MAX_SULCOS)
                                    THEN
                                        F_ERRO_SULCOS := TRUE;
                                        F_QTD_ERROS = F_QTD_ERROS + 1;
                                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                              '- A ALTURA DOS SULCOS NÃO PODE SER MAIOR QUE 50mm',
                                                              F_QUEBRA_LINHA);
                                    END IF;
                                END IF;
                            EXCEPTION
                                WHEN invalid_text_representation THEN -- error that can be handled
                                    F_ERRO_SULCOS := TRUE;
                                    F_QTD_ERROS = F_QTD_ERROS + 1;
                                    F_MSGS_ERROS =
                                            concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                   '- ALTURA DOS SULCOS COM VALOR INCORRETO',
                                                   F_QUEBRA_LINHA);
                            END;
                        ELSE
                            F_ERRO_SULCOS := TRUE;
                            F_QTD_ERROS = F_QTD_ERROS + 1;
                            F_MSGS_ERROS =
                                    concat(F_MSGS_ERROS, F_QTD_ERROS, '- A ALTURA DOS SULCOS NÃO PODE SER NULA',
                                           F_QUEBRA_LINHA);

                        END IF;
                        IF (F_ERRO_SULCOS = FALSE)
                        THEN
                            INSERT INTO MODELO_PNEU (NOME, COD_MARCA, COD_EMPRESA, QT_SULCOS, ALTURA_SULCOS)
                            VALUES (NEW.MODELO_EDITAVEL, F_COD_MARCA_BANCO, NEW.COD_EMPRESA,
                                    NEW.QTD_SULCOS_FORMATADA_IMPORT,
                                    NEW.ALTURA_SULCOS_FORMATADA_IMPORT)
                            RETURNING CODIGO INTO F_COD_MODELO_BANCO;
                        END IF;
                    END IF;
                ELSE
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                          '- O MODELO DE PNEU NÃO PODE SER NULO', F_QUEBRA_LINHA);
                END IF;
            ELSE
                F_QTD_ERROS = F_QTD_ERROS + 1;
                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A MARCA NÃO FOI ENCONTRADA', F_QUEBRA_LINHA);
            END IF;
        ELSE
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A MARCA NÃO PODE SER NULA', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICAÇÕES DOT
        IF (CHAR_LENGTH(NEW.DOT_FORMATADO_IMPORT) > 4)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- O DOT DEVE POSSUIR NO MÁXIMO 4 DÍGITOS',
                                  F_QUEBRA_LINHA);

        ELSE
            IF (CHAR_LENGTH(NEW.DOT_FORMATADO_IMPORT) < 4)
            THEN
                NEW.DOT_FORMATADO_IMPORT = LPAD(NEW.DOT_FORMATADO_IMPORT, 4, '0');
            END IF;
            IF (CHAR_LENGTH(NEW.DOT_FORMATADO_IMPORT) = 4)
            THEN
                BEGIN
                    -- Transforma o DOT_FORMATADO em data
                    DOT_EM_DATA := TO_DATE(CONCAT(PREFIXO_ANO, (SUBSTRING(NEW.DOT_FORMATADO_IMPORT, 3, 4)),
                                                  (SUBSTRING(NEW.DOT_FORMATADO_IMPORT, 1, 2))),
                                           DATE_CONVERTER);
                    -- Verifica se a data do DOT que foi transformado é maior que a data atual, se for está errado.
                    IF (DOT_EM_DATA > CURRENT_DATE)
                    THEN
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS =
                                concat(F_MSGS_ERROS, F_QTD_ERROS, '- O DOT NÃO PODE SER MAIOR QUE A DATA ATUAL',
                                       F_QUEBRA_LINHA);
                    END IF;
                EXCEPTION
                    WHEN invalid_datetime_format THEN -- error that can be handled
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS =
                                concat(F_MSGS_ERROS, F_QTD_ERROS,
                                       '- DOT COM CARACTERES INCORRETOS - DEVE POSSUIR APENAS NÚMEROS',
                                       F_QUEBRA_LINHA);
                END;
            END IF;
        END IF;

        -- VERIFICAÇÕES DIMENSÃO
        IF (NEW.DIMENSAO_FORMATADA_IMPORT IS NULL)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A DIMENSÃO NÃO PODE SER NULA',
                                  F_QUEBRA_LINHA);
        ELSE
            SELECT DISTINCT ON (NEW.DIMENSAO_FORMATADA_IMPORT) DP.CODIGO                                                                    AS COD_DIMENSAO_BANCO,
                                                               MAX(func_gera_similaridade(NEW.DIMENSAO_FORMATADA_IMPORT,
                                                                                          CONCAT(DP.LARGURA, '/', DP.ALTURA, 'R', DP.ARO))) AS SIMILARIDADE_DIMENSAO
            INTO F_COD_DIMENSAO, F_SIMILARIDADE_DIMENSAO
            FROM DIMENSAO_PNEU DP WHERE DP.COD_EMPRESA = NEW.COD_EMPRESA
            GROUP BY NEW.DIMENSAO_FORMATADA_IMPORT, NEW.DIMENSAO_EDITAVEL,
                     CONCAT(DP.LARGURA, '/', DP.ALTURA, 'R', DP.ARO),
                     DP.CODIGO
            ORDER BY NEW.DIMENSAO_FORMATADA_IMPORT, SIMILARIDADE_DIMENSAO DESC;

            IF (F_SIMILARIDADE_DIMENSAO < F_VALOR_SIMILARIDADE_DIMENSAO)
            THEN
                F_QTD_ERROS = F_QTD_ERROS + 1;
                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A DIMENSÃO NÃO FOI ENCONTRADA', F_QUEBRA_LINHA);
            END IF;
        END IF;

        -- VERIFICAÇÕES PRESSÃO IDEAL
        IF (NEW.PRESSAO_RECOMENDADA_EDITAVEL IS NULL)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A PRESSÃO RECOMENDADA NÃO PODE SER NULA',
                                  F_QUEBRA_LINHA);
        ELSE
            BEGIN
                NEW.PRESSAO_RECOMENDADA_FORMATADA_IMPORT :=
                        REPLACE(REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(
                                        NEW.PRESSAO_RECOMENDADA_EDITAVEL), ',', '.')::REAL;
                IF (NEW.PRESSAO_RECOMENDADA_FORMATADA_IMPORT < 0)
                THEN
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                          '- A PRESSÃO RECOMENDADA NÃO PODE SER NEGATIVA',
                                          F_QUEBRA_LINHA);
                ELSE
                    IF (NEW.PRESSAO_RECOMENDADA_FORMATADA_IMPORT > 150)
                    THEN
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                              '- A PRESSÃO RECOMENDADA NÃO PODE SER MAIOR QUE 150',
                                              F_QUEBRA_LINHA);
                    END IF;
                END IF;
            EXCEPTION
                WHEN invalid_text_representation THEN -- error that can be handled
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS =
                            concat(F_MSGS_ERROS, F_QTD_ERROS, '- PRESSÃO IDEAL COM CARACTERES INCORRETOS',
                                   F_QUEBRA_LINHA);
            END;
        END IF;

        -- VERIFICAÇÕES VALOR PNEU
        IF (NEW.VALOR_PNEU_EDITAVEL IS NULL)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- O VALOR DO PNEU NÃO PODE SER NULO',
                                  F_QUEBRA_LINHA);
        ELSE
            BEGIN
                NEW.VALOR_PNEU_FORMATADO_IMPORT :=
                        REPLACE(REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(
                                        NEW.VALOR_PNEU_EDITAVEL), ',', '.')::REAL;
                IF (NEW.VALOR_PNEU_FORMATADO_IMPORT < 0)
                THEN
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- O VALOR DO PNEU NÃO PODE SER NEGATIVO',
                                          F_QUEBRA_LINHA);
                END IF;
            EXCEPTION
                WHEN invalid_text_representation THEN -- error that can be handled
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS =
                            concat(F_MSGS_ERROS, F_QTD_ERROS, '- VALOR DO PNEU COM CARACTERES INCORRETOS',
                                   F_QUEBRA_LINHA);
            END;
        END IF;

        -- VERIFICAÇÕES VIDA TOTAL.
        IF (NEW.VIDA_TOTAL_EDITAVEL IS NULL)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A VIDA TOTAL DO PNEU NÃO PODE SER NULA',
                                  F_QUEBRA_LINHA);
        ELSE
            BEGIN
                -- ACRESCENTA +1 NA VIDA_TOTAL_FORMATADA_IMPORT.
                -- Acrescentado +1 à vida_total devido ao prolog considerar que a vida_atual do pneu novo é = 1, não 0.
                NEW.VIDA_TOTAL_FORMATADA_IMPORT := (NEW.VIDA_TOTAL_EDITAVEL :: INTEGER + 1);
                IF (NEW.VIDA_TOTAL_FORMATADA_IMPORT < 1)
                THEN
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                          '- A VIDA TOTAL DO PNEU NÃO PODE SER NEGATIVA',
                                          F_QUEBRA_LINHA);
                ELSE
                    IF (NEW.VIDA_TOTAL_FORMATADA_IMPORT > 10)
                    THEN
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                              '- A VIDA TOTAL DO PNEU NÃO PODE SER MAIOR QUE 10',
                                              F_QUEBRA_LINHA);
                    END IF;
                END IF;
            EXCEPTION
                WHEN invalid_text_representation THEN -- error that can be handled
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS =
                            concat(F_MSGS_ERROS, F_QTD_ERROS, '- VIDA TOTAL COM CARACTERES INCORRETOS',
                                   F_QUEBRA_LINHA);
            END;
        END IF;

        -- VERIFICAÇÕES VIDA ATUAL
        IF (NEW.VIDA_ATUAL_EDITAVEL IS NULL)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A VIDA ATUAL DO PNEU NÃO PODE SER NULA',
                                  F_QUEBRA_LINHA);
        ELSE
            BEGIN
                -- ACRESCENTA +1 NA VIDA_ATUAL_FORMATADA_IMPORT.
                -- É incrementado +1 à vida_atual devido ao prolog considerar que a vida do pneu novo é = 1 e não 0.
                NEW.VIDA_ATUAL_FORMATADA_IMPORT := (NEW.VIDA_ATUAL_EDITAVEL :: INTEGER + 1);

                --VIDA_ATUAL FOR MAIOR QUE A VIDA TOTAL: Erro.
                IF (NEW.VIDA_ATUAL_FORMATADA_IMPORT > NEW.VIDA_TOTAL_FORMATADA_IMPORT)
                THEN
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                          '- A VIDA ATUAL NÃO PODE SER MAIOR QUE A VIDA TOTAL',
                                          F_QUEBRA_LINHA);
                ELSE
                    IF (NEW.VIDA_ATUAL_FORMATADA_IMPORT < 1)
                    THEN
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                              '- A VIDA ATUAL DO PNEU NÃO PODE SER NEGATIVA',
                                              F_QUEBRA_LINHA);
                    END IF;
                END IF;

                IF (NEW.VIDA_ATUAL_FORMATADA_IMPORT = 1)
                THEN
                    IF (NEW.PNEU_NOVO_NUNCA_RODADO_EDITAVEL IS NOT NULL)
                    THEN
                        FOREACH F_PNEU_NOVO_NUNCA_RODADO IN ARRAY F_PNEU_NOVO_NUNCA_RODADO_ARRAY
                            LOOP
                                F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO_LOOP :=
                                        MAX(FUNC_GERA_SIMILARIDADE(NEW.PNEU_NOVO_NUNCA_RODADO_EDITAVEL,
                                                                   F_PNEU_NOVO_NUNCA_RODADO));
                                IF (F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO_LOOP >
                                    F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO)
                                THEN
                                    F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO := F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO_LOOP;
                                END IF;
                            END LOOP;
                    END IF;
                END IF;
                IF (F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO >= F_VALOR_SIMILARIDADE)
                THEN
                    NEW.PNEU_NOVO_NUNCA_RODADO_FORMATADO_IMPORT := TRUE;
                ELSE
                    NEW.PNEU_NOVO_NUNCA_RODADO_FORMATADO_IMPORT := FALSE;
                END IF;
            EXCEPTION
                WHEN invalid_text_representation THEN -- error that can be handled
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS =
                            concat(F_MSGS_ERROS, F_QTD_ERROS, '- VIDA ATUAL COM CARACTERES INCORRETOS',
                                   F_QUEBRA_LINHA);
            END;
        END IF;

        --VERIFICAÇÕES BANDA
        IF (NEW.VIDA_ATUAL_FORMATADA_IMPORT IS NOT NULL AND NEW.VIDA_ATUAL_FORMATADA_IMPORT > 1)
        THEN
            IF (NEW.MARCA_BANDA_FORMATADA_IMPORT IS NULL)
            THEN
                F_QTD_ERROS = F_QTD_ERROS + 1;
                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                      '- A MARCA DE BANDA NÃO PODE SER NULA PARA PNEUS ACIMA DA PRIMEIRA VIDA',
                                      F_QUEBRA_LINHA);
            ELSE
                -- VERIFICAÇÕES MARCA DE BANDA: Procura marca de banda similar no banco.
                SELECT DISTINCT ON (NEW.MARCA_BANDA_FORMATADA_IMPORT) MAB.CODIGO                                                                  AS COD_MARCA_BANDA_BANCO,
                                                                      MAX(
                                                                              FUNC_GERA_SIMILARIDADE(NEW.MARCA_BANDA_FORMATADA_IMPORT, MAB.NOME)) AS SIMILARIEDADE_MARCA_BANDA
                INTO F_COD_MARCA_BANDA_BANCO, F_SIMILARIDADE_MARCA_BANDA
                FROM MARCA_BANDA MAB
                WHERE MAB.COD_EMPRESA = NEW.COD_EMPRESA
                GROUP BY NEW.MARCA_BANDA_FORMATADA_IMPORT, NEW.MARCA_BANDA_EDITAVEL, MAB.NOME, MAB.CODIGO
                ORDER BY NEW.MARCA_BANDA_FORMATADA_IMPORT, SIMILARIEDADE_MARCA_BANDA DESC;

                F_MARCA_MODELO_BANDA := CONCAT(F_COD_MARCA_BANDA_BANCO, NEW.MODELO_BANDA_FORMATADO_IMPORT);
                -- Se a similaridade da marca de banda for menor que o exigido: Cadastra.
                IF (F_SIMILARIDADE_MARCA_BANDA < F_VALOR_SIMILARIDADE OR F_SIMILARIDADE_MARCA_BANDA IS NULL)
                THEN
                    INSERT INTO MARCA_BANDA (NOME, COD_EMPRESA)
                    VALUES (NEW.MARCA_BANDA_EDITAVEL, NEW.COD_EMPRESA)
                    RETURNING CODIGO INTO F_COD_MARCA_BANDA_BANCO;
                END IF;

                IF (NEW.MODELO_BANDA_FORMATADO_IMPORT IS NULL)
                THEN
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                          '- O MODELO DE BANDA NÃO PODE SER NULO PARA PNEUS ACIMA DA PRIMEIRA VIDA',
                                          F_QUEBRA_LINHA);
                ELSE
                    -- VERIFICAÇÕES MODELO DE BANDA: Procura modelo SIMILAR NO banco.
                    SELECT DISTINCT ON (F_MARCA_MODELO_BANDA) MOB.CODIGO AS COD_MODELO_BANDA,
                                                              CASE
                                                                  WHEN F_COD_MARCA_BANDA_BANCO = MOB.COD_MARCA
                                                                      THEN
                                                                      MAX(FUNC_GERA_SIMILARIDADE(
                                                                              F_MARCA_MODELO_BANDA,
                                                                              CONCAT(MOB.COD_MARCA, MOB.NOME)))
                                                                  ELSE F_SEM_SIMILARIDADE
                                                                  END    AS SIMILARIEDADE_MODELO_BANDA
                    INTO F_COD_MODELO_BANDA_BANCO, F_SIMILARIDADE_MODELO_BANDA
                    FROM MODELO_BANDA MOB
                    WHERE MOB.COD_EMPRESA = NEW.COD_EMPRESA
                    GROUP BY F_MARCA_MODELO_BANDA, MOB.NOME, MOB.CODIGO
                    ORDER BY F_MARCA_MODELO_BANDA, SIMILARIEDADE_MODELO_BANDA DESC;

                    -- Se a similaridade do modelo de banda for menor do que o exigido: cadastra novo modelo de banda.
                    IF (F_SIMILARIDADE_MODELO_BANDA < F_VALOR_SIMILARIDADE OR F_SIMILARIDADE_MODELO_BANDA IS NULL)
                    THEN
                        BEGIN
                            NEW.QTD_SULCOS_BANDA_FORMATADA_IMPORT :=
                                    REPLACE(REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(
                                                    NEW.QTD_SULCOS_BANDA_EDITAVEL), ',', '.')::SMALLINT;
                            IF (NEW.QTD_SULCOS_BANDA_FORMATADA_IMPORT IS NULL)
                            THEN
                                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                      '- A QUANTIDADE DE SULCOS ESTAVA NULA, PORTANTO ASSUMIU O' ||
                                                      ' VALOR DEFAULT = 4',
                                                      F_QUEBRA_LINHA);
                                NEW.QTD_SULCOS_BANDA_FORMATADA_IMPORT := F_QTD_SULCOS_DEFAULT;
                            ELSE
                                IF (NEW.QTD_SULCOS_BANDA_FORMATADA_IMPORT < F_QTD_MIN_SULCOS)
                                THEN
                                    F_ERRO_SULCOS := TRUE;
                                    F_QTD_ERROS = F_QTD_ERROS + 1;
                                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                          '- A QUANTIDADE DE SULCOS DE BANDA NÃO PODE SER MENOR QUE 1',
                                                          F_QUEBRA_LINHA);
                                ELSE
                                    IF (NEW.QTD_SULCOS_BANDA_FORMATADA_IMPORT > F_QTD_MAX_SULCOS)
                                    THEN
                                        F_ERRO_SULCOS := TRUE;
                                        F_QTD_ERROS = F_QTD_ERROS + 1;
                                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                              '- A QUANTIDADE DE SULCOS DE BANDA NÃO PODE SER MAIOR' ||
                                                              ' QUE 6',
                                                              F_QUEBRA_LINHA);
                                    END IF;
                                END IF;
                            END IF;
                        EXCEPTION
                            WHEN invalid_text_representation THEN -- error that can be handled
                                F_ERRO_SULCOS := TRUE;
                                F_QTD_ERROS = F_QTD_ERROS + 1;
                                F_MSGS_ERROS =
                                        concat(F_MSGS_ERROS, F_QTD_ERROS,
                                               '- QUANTIDADE DE SULCOS DE BANDA COM VALOR INCORRETO',
                                               F_QUEBRA_LINHA);
                        END;
                        IF (NEW.ALTURA_SULCOS_BANDA_EDITAVEL IS NOT NULL)
                        THEN
                            BEGIN
                                NEW.ALTURA_SULCOS_BANDA_FORMATADA_IMPORT :=
                                        REPLACE(REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(
                                                        NEW.ALTURA_SULCOS_BANDA_EDITAVEL), ',', '.')::REAL;
                                IF (NEW.ALTURA_SULCOS_BANDA_FORMATADA_IMPORT < F_ALTURA_MIN_SULCOS)
                                THEN
                                    F_ERRO_SULCOS := TRUE;
                                    F_QTD_ERROS = F_QTD_ERROS + 1;
                                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                          '- A ALTURA DOS SULCOS DA BANDA NÃO PODE SER MENOR QUE 1mm',
                                                          F_QUEBRA_LINHA);
                                ELSE
                                    IF (NEW.ALTURA_SULCOS_BANDA_FORMATADA_IMPORT > F_ALTURA_MAX_SULCOS)
                                    THEN
                                        F_ERRO_SULCOS := TRUE;
                                        F_QTD_ERROS = F_QTD_ERROS + 1;
                                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                              '- A ALTURA DOS SULCOS DA BANDA NÃO PODE SER MAIOR ' ||
                                                              'QUE 50mm',
                                                              F_QUEBRA_LINHA);
                                    END IF;
                                END IF;
                            EXCEPTION
                                WHEN invalid_text_representation THEN -- error that can be handled
                                    F_ERRO_SULCOS := TRUE;
                                    F_QTD_ERROS = F_QTD_ERROS + 1;
                                    F_MSGS_ERROS =
                                            concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                   '- ALTURA DOS SULCOS DE BANDA COM VALOR INCORRETO',
                                                   F_QUEBRA_LINHA);
                            END;
                        ELSE
                            F_ERRO_SULCOS := TRUE;
                            F_QTD_ERROS = F_QTD_ERROS + 1;
                            F_MSGS_ERROS =
                                    concat(F_MSGS_ERROS, F_QTD_ERROS,
                                           '- A ALTURA DOS SULCOS DE BANDA NÃO PODE SER NULA',
                                           F_QUEBRA_LINHA);
                        END IF;
                        IF (F_ERRO_SULCOS = FALSE)
                        THEN
                            INSERT INTO MODELO_BANDA (NOME, COD_MARCA, COD_EMPRESA, QT_SULCOS, ALTURA_SULCOS)
                            VALUES (NEW.MODELO_BANDA_EDITAVEL, F_COD_MARCA_BANDA_BANCO, NEW.COD_EMPRESA,
                                    NEW.QTD_SULCOS_BANDA_FORMATADA_IMPORT,
                                    NEW.ALTURA_SULCOS_BANDA_FORMATADA_IMPORT)
                            RETURNING CODIGO INTO F_COD_MODELO_BANDA_BANCO;
                        END IF;
                    END IF;
                END IF;
                --ELSE MARCA DE BANDA
            END IF;

            --VERIFICAÇÕES VALOR DE BANDA.
            IF (NEW.VALOR_BANDA_EDITAVEL IS NULL)
            THEN
                F_QTD_ERROS = F_QTD_ERROS + 1;
                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                      '- O VALOR DA BANDA NÃO PODE SER NULO PARA PNEUS ACIMA DA PRIMEIRA VIDA',
                                      F_QUEBRA_LINHA);
            ELSE
                BEGIN
                    NEW.VALOR_BANDA_FORMATADO_IMPORT :=
                            REPLACE(REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(
                                            NEW.VALOR_BANDA_EDITAVEL), ',', '.')::REAL;
                    IF (NEW.VALOR_BANDA_FORMATADO_IMPORT < 0)
                    THEN
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- O VALOR DA BANDA NÃO PODE SER NEGATIVO',
                                              F_QUEBRA_LINHA);
                    END IF;
                EXCEPTION
                    WHEN invalid_text_representation THEN -- error that can be handled
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS =
                                concat(F_MSGS_ERROS, F_QTD_ERROS, '- VALOR DO PNEU COM CARACTERES INCORRETOS',
                                       F_QUEBRA_LINHA);
                END;
            END IF;
        END IF;

        IF (F_QTD_ERROS > 0)
        THEN
            NEW.ERROS_ENCONTRADOS = F_MSGS_ERROS;
        ELSE
            INSERT INTO PNEU (CODIGO_CLIENTE,
                              COD_MODELO,
                              COD_DIMENSAO,
                              PRESSAO_RECOMENDADA,
                              COD_UNIDADE,
                              STATUS,
                              VIDA_ATUAL,
                              VIDA_TOTAL,
                              PNEU_NOVO_NUNCA_RODADO,
                              COD_MODELO_BANDA,
                              DOT,
                              VALOR,
                              COD_EMPRESA,
                              COD_UNIDADE_CADASTRO,
                              ORIGEM_CADASTRO)
            VALUES (NEW.NUMERO_FOGO_FORMATADO_IMPORT,
                    F_COD_MODELO_BANCO,
                    F_COD_DIMENSAO,
                    NEW.PRESSAO_RECOMENDADA_FORMATADA_IMPORT,
                    NEW.COD_UNIDADE,
                    'ESTOQUE',
                    NEW.VIDA_ATUAL_FORMATADA_IMPORT,
                    NEW.VIDA_TOTAL_FORMATADA_IMPORT,
                    NEW.PNEU_NOVO_NUNCA_RODADO_FORMATADO_IMPORT,
                    F_COD_MODELO_BANDA_BANCO,
                    NEW.DOT_FORMATADO_IMPORT,
                    NEW.VALOR_PNEU_FORMATADO_IMPORT,
                    NEW.COD_EMPRESA,
                    NEW.COD_UNIDADE,
                    F_ORIGEM_PNEU_CADASTRO)
            RETURNING CODIGO INTO F_COD_PNEU;


            IF (NEW.VIDA_ATUAL_FORMATADA_IMPORT > 1)
            THEN
                SELECT PTS.CODIGO AS CODIGO
                FROM PNEU_TIPO_SERVICO AS PTS
                WHERE PTS.COD_EMPRESA IS NULL
                  AND PTS.STATUS_ATIVO = TRUE
                  AND PTS.INCREMENTA_VIDA = TRUE
                  AND PTS.UTILIZADO_CADASTRO_PNEU = TRUE
                INTO F_COD_TIPO_SERVICO;

                INSERT INTO PNEU_SERVICO_REALIZADO (COD_TIPO_SERVICO,
                                                    COD_UNIDADE,
                                                    COD_PNEU,
                                                    CUSTO,
                                                    VIDA,
                                                    FONTE_SERVICO_REALIZADO)
                VALUES (F_COD_TIPO_SERVICO,
                        NEW.COD_UNIDADE,
                        F_COD_PNEU,
                        NEW.VALOR_BANDA_FORMATADO_IMPORT,
                        (NEW.VIDA_ATUAL_FORMATADA_IMPORT - 1),
                        'FONTE_CADASTRO')
                RETURNING CODIGO INTO F_COD_SERVICO_REALIZADO;

                INSERT INTO PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA (COD_SERVICO_REALIZADO,
                                                                    COD_MODELO_BANDA,
                                                                    VIDA_NOVA_PNEU,
                                                                    FONTE_SERVICO_REALIZADO)
                VALUES (F_COD_SERVICO_REALIZADO,
                        F_COD_MODELO_BANDA_BANCO,
                        NEW.VIDA_ATUAL_FORMATADA_IMPORT,
                        'FONTE_CADASTRO');

                INSERT INTO PNEU_SERVICO_CADASTRO (COD_PNEU,
                                                   COD_SERVICO_REALIZADO)
                VALUES (F_COD_PNEU, F_COD_SERVICO_REALIZADO);
            END IF;
            NEW.STATUS_IMPORT_REALIZADO = TRUE;
            NEW.ERROS_ENCONTRADOS = '-';
        END IF;
    END IF;
    RETURN NEW;
END;
$$;

create or replace function suporte.func_pneu_altera_pressao_ideal_by_dimensao(f_cod_empresa bigint,
                                                                              f_cod_unidade bigint,
                                                                              f_cod_dimensao bigint,
                                                                              f_nova_pressao_recomendada bigint,
                                                                              f_qtd_pneus_impactados bigint,
                                                                              out aviso_pressao_alterada text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    qtd_real_pneus_impactados  bigint;
    pressao_minima_recomendada bigint := 0;
    pressao_maxima_recomendada bigint := 150;
begin
    perform suporte.func_historico_salva_execucao();
    -- Verifica se a pressao informada está dentro das recomendadas.
    if (f_nova_pressao_recomendada not between pressao_minima_recomendada and pressao_maxima_recomendada)
    then
        raise exception 'Pressão recomendada não está dentro dos valores pré-estabelecidos.
                        Mínima Recomendada: % ---- Máxima Recomendada: %', pressao_minima_recomendada,
            pressao_maxima_recomendada;
    end if;

    -- Verifica se a empresa existe.
    if not exists(select e.codigo
                  from empresa e
                  where e.codigo = f_cod_empresa)
    then
        raise exception 'Empresa de código % não existe!', f_cod_empresa;
    end if;

    -- Verifica se a unidade existe.
    if not exists(select u.codigo
                  from unidade u
                  where u.codigo = f_cod_unidade)
    then
        raise exception 'Unidade de código % não existe!', f_cod_unidade;
    end if;

    -- Verifica se existe a dimensão informada.
    if not exists(select dm.codigo
                  from dimensao_pneu dm
                  where dm.codigo = f_cod_dimensao and dm.cod_empresa = f_cod_empresa)
    then
        raise exception 'Dimensao de código % não existe!', f_cod_dimensao;
    end if;

    -- Verifica se a unidade é da empresa informada.
    if not exists(select u.codigo
                  from unidade u
                  where u.codigo = f_cod_unidade
                    and u.cod_empresa = f_cod_empresa)
    then
        raise exception 'A unidade % não pertence a empresa %!', f_cod_unidade, f_cod_empresa;
    end if;

    -- Verifica se algum pneu possui dimensão informada.
    if not exists(select p.cod_dimensao
                  from pneu p
                  where p.cod_dimensao = f_cod_dimensao
                    and p.cod_unidade = f_cod_unidade
                    and p.cod_empresa = f_cod_empresa)
    then
        raise exception 'Não existem pneus com a dimensão % na unidade %', f_cod_dimensao, f_cod_unidade;
    end if;

    -- Verifica quantidade de pneus impactados.
    select count(p.codigo)
    from pneu p
    where p.cod_dimensao = f_cod_dimensao
      and p.cod_unidade = f_cod_unidade
      and p.cod_empresa = f_cod_empresa
    into qtd_real_pneus_impactados;
    if (qtd_real_pneus_impactados <> f_qtd_pneus_impactados)
    then
        raise exception 'A quantidade de pneus informados como impactados pela mudança de pressão (%) não condiz com a
                       quantidade real de pneus que serão afetados!', f_qtd_pneus_impactados;
    end if;

    update pneu
    set pressao_recomendada = f_nova_pressao_recomendada
    where cod_dimensao = f_cod_dimensao
      and cod_unidade = f_cod_unidade
      and cod_empresa = f_cod_empresa;

    select concat('Pressão recomendada dos pneus com dimensão ',
                  f_cod_dimensao,
                  ' da unidade ',
                  f_cod_unidade,
                  ' alterada para ',
                  f_nova_pressao_recomendada,
                  ' psi')
    into aviso_pressao_alterada;
end;
$$;

CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_ATUALIZA_PNEU_PROLOG(F_COD_PNEU_SISTEMA_INTEGRADO BIGINT,
                                                                     F_NOVO_CODIGO_PNEU_CLIENTE TEXT,
                                                                     F_NOVO_COD_MODELO_PNEU BIGINT,
                                                                     F_NOVO_COD_DIMENSAO_PNEU BIGINT,
                                                                     F_NOVO_DOT_PNEU TEXT,
                                                                     F_NOVO_VALOR_PNEU REAL,
                                                                     F_NOVO_COD_MODELO_BANDA_PNEU BIGINT,
                                                                     F_NOVO_VALOR_BANDA_PNEU REAL,
                                                                     F_DATA_HORA_EDICAO_PNEU TIMESTAMP WITH TIME ZONE,
                                                                     F_TOKEN_INTEGRACAO TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    -- Precisamos utilizar o código do modelo de pneu para chegar até o código da empresa.
    COD_EMPRESA_PNEU     BIGINT  := (SELECT MP.COD_EMPRESA
                                     FROM PUBLIC.MODELO_PNEU MP
                                     WHERE MP.CODIGO = F_NOVO_COD_MODELO_PNEU);
    COD_PNEU_PROLOG      BIGINT  := (SELECT PC.COD_PNEU_CADASTRO_PROLOG
                                     FROM INTEGRACAO.PNEU_CADASTRADO PC
                                     WHERE PC.COD_PNEU_SISTEMA_INTEGRADO = F_COD_PNEU_SISTEMA_INTEGRADO
                                       AND PC.COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU);
    PNEU_POSSUI_BANDA    BOOLEAN := F_IF(((SELECT P.COD_MODELO_BANDA
                                           FROM PUBLIC.PNEU P
                                           WHERE P.CODIGO = COD_PNEU_PROLOG) IS NULL), FALSE, TRUE);
    TROCOU_BANDA_PNEU    BOOLEAN := F_IF(F_NOVO_COD_MODELO_BANDA_PNEU IS NULL, FALSE, TRUE);
    F_QTD_ROWS_ALTERADAS BIGINT;
    V_COD_DIMENSAO BIGINT;
BEGIN
    PERFORM INTEGRACAO.FUNC_GARANTE_TOKEN_EMPRESA(COD_EMPRESA_PNEU, F_TOKEN_INTEGRACAO);

    -- Validamos se a Empresa é válida.
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(
            COD_EMPRESA_PNEU,
            FORMAT('O token %s não é de uma Empresa válida', F_TOKEN_INTEGRACAO));

    -- Validamos se o código do pneu no sistema integrado está mapeado na tabela interna do ProLog.
    IF (COD_PNEU_PROLOG IS NULL)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                FORMAT('O pneu de código interno %s não está mapeado no Sistema ProLog',
                       F_COD_PNEU_SISTEMA_INTEGRADO));
    END IF;

    -- Validamos se o novo_codigo_cliente é um código válido ou já possui um igual na base dados.
    IF (SELECT EXISTS(SELECT PC.COD_PNEU_CADASTRO_PROLOG
                      FROM INTEGRACAO.PNEU_CADASTRADO PC
                      WHERE PC.COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU
                        AND PC.COD_CLIENTE_PNEU_CADASTRO = F_NOVO_CODIGO_PNEU_CLIENTE
                        AND PC.COD_PNEU_SISTEMA_INTEGRADO != F_COD_PNEU_SISTEMA_INTEGRADO))
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR(FORMAT('Já existe um pneu com o código %s cadastrado no Sistema ProLog',
                                              F_NOVO_CODIGO_PNEU_CLIENTE));
    END IF;

    -- Validamos se o modelo do pneu está mapeado.
    IF (SELECT NOT EXISTS(SELECT MP.CODIGO
                          FROM PUBLIC.MODELO_PNEU MP
                          WHERE MP.COD_EMPRESA = COD_EMPRESA_PNEU
                            AND MP.CODIGO = F_NOVO_COD_MODELO_PNEU))
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo do pneu %s não está mapeado', F_NOVO_COD_MODELO_PNEU));
    END IF;

    -- Validamos se a dimensão do pneu está mapeada.
    IF (SELECT NOT EXISTS(SELECT DP.COD_AUXILIAR
                          FROM PUBLIC.DIMENSAO_PNEU DP
                          WHERE DP.COD_AUXILIAR = F_NOVO_COD_DIMENSAO_PNEU))
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR(FORMAT('A dimensão de código %s do pneu não está mapeada',
                                              F_NOVO_COD_DIMENSAO_PNEU));
    ELSE
        SELECT DP.CODIGO
        INTO V_COD_DIMENSAO
        FROM DIMENSAO_PNEU DP
        WHERE DP.COD_AUXILIAR = F_NOVO_COD_DIMENSAO_PNEU
          AND DP.COD_EMPRESA = COD_EMPRESA_PNEU;
    END IF;

    -- Validamos se o valor do pneu é um valor válido.
    IF (F_NOVO_VALOR_PNEU < 0)
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR('O valor do pneu não pode ser um número negativo');
    END IF;

    -- Validamos se o pneu possui banda e se ela não foi removida na atualização.
    IF (PNEU_POSSUI_BANDA AND F_NOVO_COD_MODELO_BANDA_PNEU IS NULL)
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR('O modelo da banda do pneu deve ser informado');
    END IF;

    -- Validamos se o código do modelo da banda é válido. Apenas validamos se o pneu possuir banda.
    IF (PNEU_POSSUI_BANDA AND (SELECT NOT EXISTS(SELECT MB.CODIGO
                                                 FROM PUBLIC.MODELO_BANDA MB
                                                 WHERE MB.COD_EMPRESA = COD_EMPRESA_PNEU
                                                   AND MB.CODIGO = F_NOVO_COD_MODELO_BANDA_PNEU)))
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo da banda do pneu %s não está mapeado',
                                              F_NOVO_COD_MODELO_BANDA_PNEU));
    END IF;

    -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda e a mesma tiver sido
    -- atualizada.
    IF (PNEU_POSSUI_BANDA AND TROCOU_BANDA_PNEU AND F_NOVO_VALOR_BANDA_PNEU IS NULL)
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR(
                    'Você está trocando a banda, deve ser informado o valor da nova banda aplicada');
    END IF;

    -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda.
    IF (PNEU_POSSUI_BANDA AND TROCOU_BANDA_PNEU AND F_NOVO_VALOR_BANDA_PNEU IS NULL AND F_NOVO_VALOR_BANDA_PNEU < 0)
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR('O valor da nova banda do pneu não pode ser um número negativo');
    END IF;

    UPDATE PUBLIC.PNEU
    SET CODIGO_CLIENTE   = F_NOVO_CODIGO_PNEU_CLIENTE,
        COD_MODELO       = F_NOVO_COD_MODELO_PNEU,
        COD_DIMENSAO     = V_COD_DIMENSAO,
        DOT              = F_NOVO_DOT_PNEU,
        VALOR            = F_NOVO_VALOR_PNEU,
        COD_MODELO_BANDA = F_IF(PNEU_POSSUI_BANDA AND TROCOU_BANDA_PNEU, F_NOVO_COD_MODELO_BANDA_PNEU, NULL)
    WHERE CODIGO = COD_PNEU_PROLOG;

    UPDATE INTEGRACAO.PNEU_CADASTRADO
    SET COD_CLIENTE_PNEU_CADASTRO = F_NOVO_CODIGO_PNEU_CLIENTE,
        DATA_HORA_ULTIMA_EDICAO   = F_DATA_HORA_EDICAO_PNEU
    WHERE COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU
      AND COD_PNEU_SISTEMA_INTEGRADO = F_COD_PNEU_SISTEMA_INTEGRADO;

    GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

    -- Verificamos se a atualização na tabela de mapeamento ocorreu com sucesso.
    IF (F_QTD_ROWS_ALTERADAS <= 0)
    THEN
        RAISE EXCEPTION 'Não foi possível atualizar o pneu % na tabela de mapeamento', COD_PNEU_PROLOG;
    END IF;

    IF (PNEU_POSSUI_BANDA
        AND NOT (SELECT *
                 FROM PUBLIC.FUNC_PNEUS_UPDATE_BANDA_PNEU(COD_PNEU_PROLOG,
                                                          F_NOVO_COD_MODELO_BANDA_PNEU,
                                                          F_NOVO_VALOR_BANDA_PNEU)))
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR('Não foi possível atualizar a banda do pneu');
    END IF;

    RETURN COD_PNEU_PROLOG;
END;
$$;

CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_INSERE_PNEU_PROLOG(F_COD_PNEU_SISTEMA_INTEGRADO BIGINT,
                                                                   F_CODIGO_PNEU_CLIENTE CHARACTER VARYING,
                                                                   F_COD_UNIDADE_PNEU BIGINT,
                                                                   F_COD_MODELO_PNEU BIGINT,
                                                                   F_COD_DIMENSAO_PNEU BIGINT,
                                                                   F_PRESSAO_CORRETA_PNEU DOUBLE PRECISION,
                                                                   F_VIDA_ATUAL_PNEU INTEGER,
                                                                   F_VIDA_TOTAL_PNEU INTEGER,
                                                                   F_DOT_PNEU CHARACTER VARYING,
                                                                   F_VALOR_PNEU NUMERIC,
                                                                   F_PNEU_NOVO_NUNCA_RODADO BOOLEAN,
                                                                   F_COD_MODELO_BANDA_PNEU BIGINT,
                                                                   F_VALOR_BANDA_PNEU NUMERIC,
                                                                   F_DATA_HORA_PNEU_CADASTRO TIMESTAMP WITH TIME ZONE,
                                                                   F_TOKEN_INTEGRACAO CHARACTER VARYING,
                                                                   F_DEVE_SOBRESCREVER_PNEU BOOLEAN DEFAULT FALSE)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    PNEU_ORIGEM_CADASTRO CONSTANT TEXT    := 'API';
    PNEU_PRIMEIRA_VIDA   CONSTANT BIGINT  := 1;
    PNEU_STATUS_ESTOQUE  CONSTANT TEXT    := 'ESTOQUE';
    PNEU_POSSUI_BANDA    CONSTANT BOOLEAN := F_IF(F_VIDA_ATUAL_PNEU > PNEU_PRIMEIRA_VIDA, TRUE, FALSE);
    COD_EMPRESA_PNEU     CONSTANT BIGINT  := (SELECT U.COD_EMPRESA
                                              FROM PUBLIC.UNIDADE U
                                              WHERE U.CODIGO = F_COD_UNIDADE_PNEU);
    PNEU_ESTA_NO_PROLOG  CONSTANT BOOLEAN := (SELECT EXISTS(SELECT P.CODIGO
                                                            FROM PUBLIC.PNEU P
                                                            WHERE P.CODIGO_CLIENTE = F_CODIGO_PNEU_CLIENTE
                                                              AND P.COD_EMPRESA = COD_EMPRESA_PNEU));
    COD_PNEU_PROLOG               BIGINT;
    F_QTD_ROWS_AFETADAS           BIGINT;
    V_COD_DIMENSAO                BIGINT;
BEGIN
    PERFORM INTEGRACAO.FUNC_GARANTE_TOKEN_EMPRESA(COD_EMPRESA_PNEU, F_TOKEN_INTEGRACAO);

    -- Validamos se a Empresa existe.
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(COD_EMPRESA_PNEU,
                                        FORMAT('O token %s não é de uma Empresa válida', F_TOKEN_INTEGRACAO));

    -- Validamos se a Unidade repassada existe.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_PNEU,
                                        FORMAT('A Unidade %s repassada não existe no Sistema ProLog',
                                               F_COD_UNIDADE_PNEU));

    -- Validamos se a Unidade pertence a Empresa do token repassado.
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(COD_EMPRESA_PNEU,
                                                F_COD_UNIDADE_PNEU,
                                                FORMAT('A Unidade %s não está configurada para esta empresa',
                                                       F_COD_UNIDADE_PNEU));

    -- Validamos se o modelo do pneu está mapeado.
    IF (SELECT NOT EXISTS(SELECT MP.CODIGO
                          FROM PUBLIC.MODELO_PNEU MP
                          WHERE MP.COD_EMPRESA = COD_EMPRESA_PNEU
                            AND MP.CODIGO = F_COD_MODELO_PNEU))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo do pneu %s não está mapeado no Sistema ProLog',
                                                  F_COD_MODELO_PNEU));
    END IF;

    -- Validamos se a dimensão do pneu está mapeada.
    IF (SELECT NOT EXISTS(SELECT DP.COD_AUXILIAR
                          FROM PUBLIC.DIMENSAO_PNEU DP
                          WHERE DP.COD_AUXILIAR = F_COD_DIMENSAO_PNEU))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('A dimensão de código %s do pneu não está mapeada no Sistema ProLog',
                                                  F_COD_DIMENSAO_PNEU));
    ELSE
        SELECT DP.CODIGO
        INTO V_COD_DIMENSAO
        FROM DIMENSAO_PNEU DP
        WHERE DP.COD_EMPRESA = COD_EMPRESA_PNEU;
    END IF;

    -- Validamos se a pressão recomendada é válida.
    IF (F_PRESSAO_CORRETA_PNEU < 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR('A pressão recomendada para o pneu não pode ser um número negativo');
    END IF;

    -- Validamos se a vida atual é correta.
    IF (F_VIDA_ATUAL_PNEU < PNEU_PRIMEIRA_VIDA)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR('A vida atual do pneu deve ser no mínimo 1 (caso novo)');
    END IF;

    -- Validamos se a vida total é válida.
    IF (F_VIDA_TOTAL_PNEU < F_VIDA_ATUAL_PNEU)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR('A vida total do pneu não pode ser menor que a vida atual');
    END IF;

    -- Validamos se o valor do pneu é um valor válido.
    IF (F_VALOR_PNEU < 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR('O valor do pneu não pode ser um número negativo');
    END IF;

    -- Validamos se o código do modelo de banda é válido. Apenas validamos se o pneu possuir banda.
    IF (PNEU_POSSUI_BANDA AND F_COD_MODELO_BANDA_PNEU IS NULL)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT(
                'O pneu %s não está na primeira vida, deve ser informado um modelo de banda',
                F_COD_PNEU_SISTEMA_INTEGRADO));
    END IF;

    -- Validamos se o código do modelo da banda é válido. Apenas validamos se o pneu possuir banda.
    IF (PNEU_POSSUI_BANDA AND (SELECT NOT EXISTS(SELECT MB.CODIGO
                                                 FROM PUBLIC.MODELO_BANDA MB
                                                 WHERE MB.COD_EMPRESA = COD_EMPRESA_PNEU
                                                   AND MB.CODIGO = F_COD_MODELO_BANDA_PNEU)))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo da banda %s do pneu não está mapeado no Sistema ProLog',
                                                  F_COD_MODELO_BANDA_PNEU));
    END IF;

    -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda.
    IF (PNEU_POSSUI_BANDA AND F_VALOR_BANDA_PNEU IS NULL)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                'O pneu não está na primeira vida, deve ser informado o valor da banda aplicada');
    END IF;

    -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda.
    IF (PNEU_POSSUI_BANDA AND F_VALOR_BANDA_PNEU < 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR('O valor da banda do pneu não pode ser um número negativo');
    END IF;

    -- Validamos se o código do sistema integrado já está mapeado na tabela, apenas se não estiver devemos sobrescrever.
    -- Pode acontecer o caso onde o pneu está na base do ProLog e é rodado a sobrecarga. Neste cenário o pneu deve
    -- apenas ter as informações sobrescritas e a tabela de vínculo atualizada.
    IF (SELECT EXISTS(SELECT PC.COD_PNEU_CADASTRO_PROLOG
                      FROM INTEGRACAO.PNEU_CADASTRADO PC
                      WHERE PC.COD_PNEU_SISTEMA_INTEGRADO = F_COD_PNEU_SISTEMA_INTEGRADO
                        AND PC.COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU) AND NOT F_DEVE_SOBRESCREVER_PNEU)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O pneu de código interno %s já está cadastrado no Sistema ProLog',
                                                  F_COD_PNEU_SISTEMA_INTEGRADO));
    END IF;

    -- Já validamos se o pneu existe no ProLog através código do sistema integrado, então sobrescrevemos as
    -- informações dele ou, caso não deva sobrescrever, inserimos no base. Validamos também se o pneu já está na base
    -- do ProLog, caso ele não esteja, deveremos inserir e não sobrescrever.
    IF (PNEU_ESTA_NO_PROLOG AND F_DEVE_SOBRESCREVER_PNEU)
    THEN
        -- Pegamos o código do pneu que iremos sobrescrever.
        SELECT P.CODIGO
        FROM PUBLIC.PNEU P
        WHERE P.CODIGO_CLIENTE = F_CODIGO_PNEU_CLIENTE
          AND P.COD_EMPRESA = COD_EMPRESA_PNEU
        INTO COD_PNEU_PROLOG;

        -- Sebrescrevemos os dados do pneu.
        PERFORM INTEGRACAO.FUNC_PNEU_SOBRESCREVE_PNEU_CADASTRADO(COD_PNEU_PROLOG,
                                                                 F_COD_UNIDADE_PNEU,
                                                                 F_COD_MODELO_PNEU,
                                                                 F_COD_DIMENSAO_PNEU,
                                                                 F_PRESSAO_CORRETA_PNEU,
                                                                 F_VIDA_ATUAL_PNEU,
                                                                 F_VIDA_TOTAL_PNEU,
                                                                 F_DOT_PNEU,
                                                                 F_VALOR_PNEU,
                                                                 F_PNEU_NOVO_NUNCA_RODADO,
                                                                 F_COD_MODELO_BANDA_PNEU,
                                                                 F_VALOR_BANDA_PNEU,
                                                                 F_DATA_HORA_PNEU_CADASTRO);
    ELSEIF (NOT PNEU_ESTA_NO_PROLOG)
    THEN
        -- Deveremos inserir os dados na base.
        INSERT INTO PUBLIC.PNEU(COD_EMPRESA,
                                COD_UNIDADE_CADASTRO,
                                COD_UNIDADE,
                                CODIGO_CLIENTE,
                                COD_MODELO,
                                COD_DIMENSAO,
                                PRESSAO_RECOMENDADA,
                                PRESSAO_ATUAL,
                                ALTURA_SULCO_INTERNO,
                                ALTURA_SULCO_CENTRAL_INTERNO,
                                ALTURA_SULCO_CENTRAL_EXTERNO,
                                ALTURA_SULCO_EXTERNO,
                                STATUS,
                                VIDA_ATUAL,
                                VIDA_TOTAL,
                                DOT,
                                VALOR,
                                COD_MODELO_BANDA,
                                PNEU_NOVO_NUNCA_RODADO,
                                DATA_HORA_CADASTRO,
                                ORIGEM_CADASTRO)
        VALUES (COD_EMPRESA_PNEU,
                F_COD_UNIDADE_PNEU,
                F_COD_UNIDADE_PNEU,
                F_CODIGO_PNEU_CLIENTE,
                F_COD_MODELO_PNEU,
                V_COD_DIMENSAO,
                F_PRESSAO_CORRETA_PNEU,
                0, -- PRESSAO_ATUAL
                NULL, -- ALTURA_SULCO_INTERNO
                NULL, -- ALTURA_SULCO_CENTRAL_INTERNO
                NULL, -- ALTURA_SULCO_CENTRAL_EXTERNO
                NULL, -- ALTURA_SULCO_EXTERNO
                PNEU_STATUS_ESTOQUE,
                F_VIDA_ATUAL_PNEU,
                F_VIDA_TOTAL_PNEU,
                F_DOT_PNEU,
                F_VALOR_PNEU,
                F_IF(PNEU_POSSUI_BANDA, F_COD_MODELO_BANDA_PNEU, NULL),
                   -- Forçamos FALSE caso o pneu já possua uma banda aplicada.
                F_IF(PNEU_POSSUI_BANDA, FALSE, F_PNEU_NOVO_NUNCA_RODADO),
                F_DATA_HORA_PNEU_CADASTRO,
                PNEU_ORIGEM_CADASTRO)
        RETURNING CODIGO INTO COD_PNEU_PROLOG;

        -- Precisamos criar um serviço de incremento de vida para o pneu cadastrado já possuíndo uma banda.
        IF (PNEU_POSSUI_BANDA)
        THEN
            PERFORM FUNC_PNEU_REALIZA_INCREMENTO_VIDA_CADASTRO(F_COD_UNIDADE_PNEU,
                                                               COD_PNEU_PROLOG,
                                                               F_COD_MODELO_BANDA_PNEU,
                                                               F_VALOR_BANDA_PNEU,
                                                               F_VIDA_ATUAL_PNEU);
        END IF;
    ELSE
        -- Pneu está no ProLog e não deve sobrescrever.
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                FORMAT('O pneu %s já está cadastrado no Sistema ProLog', F_CODIGO_PNEU_CLIENTE));
    END IF;

    IF (F_DEVE_SOBRESCREVER_PNEU)
    THEN
        -- Se houve uma sobrescrita de dados, então tentamos inserir, caso a constraint estourar,
        -- apenas atualizamos os dados. Tentamos inserir antes, pois, em cenários onde o pneu já encontra-se no ProLog,
        -- não temos nenhuma entrada para ele na tabela de mapeamento.
        INSERT INTO INTEGRACAO.PNEU_CADASTRADO(COD_PNEU_CADASTRO_PROLOG,
                                               COD_PNEU_SISTEMA_INTEGRADO,
                                               COD_EMPRESA_CADASTRO,
                                               COD_UNIDADE_CADASTRO,
                                               COD_CLIENTE_PNEU_CADASTRO,
                                               TOKEN_AUTENTICACAO_CADASTRO,
                                               DATA_HORA_CADASTRO_PROLOG)
        VALUES (COD_PNEU_PROLOG,
                F_COD_PNEU_SISTEMA_INTEGRADO,
                COD_EMPRESA_PNEU,
                F_COD_UNIDADE_PNEU,
                F_CODIGO_PNEU_CLIENTE,
                F_TOKEN_INTEGRACAO,
                F_DATA_HORA_PNEU_CADASTRO)
        ON CONFLICT ON CONSTRAINT UNIQUE_PNEU_CADASTRADO_EMPRESA_INTEGRACAO
            DO UPDATE SET COD_PNEU_SISTEMA_INTEGRADO  = F_COD_PNEU_SISTEMA_INTEGRADO,
                          COD_UNIDADE_CADASTRO        = F_COD_UNIDADE_PNEU,
                          COD_CLIENTE_PNEU_CADASTRO   = F_CODIGO_PNEU_CLIENTE,
                          TOKEN_AUTENTICACAO_CADASTRO = F_TOKEN_INTEGRACAO,
                          DATA_HORA_ULTIMA_EDICAO     = F_DATA_HORA_PNEU_CADASTRO;
    ELSE
        -- Se não houve sobrescrita de dados, significa que devemos apenas inserir os dados na tabela de mapeamento e
        -- deixar um erro estourar caso pneu já exista.
        INSERT INTO INTEGRACAO.PNEU_CADASTRADO(COD_PNEU_CADASTRO_PROLOG,
                                               COD_PNEU_SISTEMA_INTEGRADO,
                                               COD_EMPRESA_CADASTRO,
                                               COD_UNIDADE_CADASTRO,
                                               COD_CLIENTE_PNEU_CADASTRO,
                                               TOKEN_AUTENTICACAO_CADASTRO,
                                               DATA_HORA_CADASTRO_PROLOG)
        VALUES (COD_PNEU_PROLOG,
                F_COD_PNEU_SISTEMA_INTEGRADO,
                COD_EMPRESA_PNEU,
                F_COD_UNIDADE_PNEU,
                F_CODIGO_PNEU_CLIENTE,
                F_TOKEN_INTEGRACAO,
                F_DATA_HORA_PNEU_CADASTRO);
    END IF;

    GET DIAGNOSTICS F_QTD_ROWS_AFETADAS = ROW_COUNT;

    -- Verificamos se a inserção na tabela de mapeamento ocorreu com sucesso.
    IF (F_QTD_ROWS_AFETADAS <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível inserir o pneu "%" na tabela de mapeamento', F_CODIGO_PNEU_CLIENTE;
    END IF;

    RETURN COD_PNEU_PROLOG;
END;
$$;

drop function func_pneu_get_pneu_by_placa(character varying,bigint);
create or replace function func_pneu_get_pneu_by_placa(f_placa varchar(7), f_cod_unidade bigint)
    returns table
            (
                nome_marca_pneu              varchar(255),
                cod_marca_pneu               bigint,
                codigo                       bigint,
                codigo_cliente               varchar(255),
                cod_unidade_alocado          bigint,
                cod_regional_alocado         bigint,
                pressao_atual                real,
                vida_atual                   integer,
                vida_total                   integer,
                pneu_novo_nunca_rodado       boolean,
                nome_modelo_pneu             varchar(255),
                cod_modelo_pneu              bigint,
                qt_sulcos_modelo_pneu        smallint,
                altura_sulcos_modelo_pneu    real,
                altura                       numeric,
                largura                      numeric,
                aro                          numeric,
                cod_dimensao                 bigint,
                pressao_recomendada          real,
                altura_sulco_central_interno real,
                altura_sulco_central_externo real,
                altura_sulco_interno         real,
                altura_sulco_externo         real,
                status                       varchar(255),
                dot                          varchar(20),
                valor                        real,
                cod_modelo_banda             bigint,
                nome_modelo_banda            varchar(255),
                qt_sulcos_modelo_banda       smallint,
                altura_sulcos_modelo_banda   real,
                cod_marca_banda              bigint,
                nome_marca_banda             varchar(255),
                valor_banda                  real,
                posicao_pneu                 integer,
                posicao_aplicado_cliente     varchar(255),
                cod_veiculo_aplicado         bigint,
                placa_aplicado               varchar(7),
                identificador_frota          text
            )
    language sql
as
$$
select mp.nome                                  as nome_marca_pneu,
       mp.codigo                                as cod_marca_pneu,
       p.codigo,
       p.codigo_cliente,
       u.codigo                                 as cod_unidade_alocado,
       r.codigo                                 as cod_regional_alocado,
       p.pressao_atual,
       p.vida_atual,
       p.vida_total,
       p.pneu_novo_nunca_rodado,
       mop.nome                                 as nome_modelo_pneu,
       mop.codigo                               as cod_modelo_pneu,
       mop.qt_sulcos                            as qt_sulcos_modelo_pneu,
       mop.altura_sulcos                        as altura_sulcos_modelo_pneu,
       pd.altura,
       pd.largura,
       pd.aro,
       pd.codigo                                as cod_dimensao,
       p.pressao_recomendada,
       p.altura_sulco_central_interno,
       p.altura_sulco_central_externo,
       p.altura_sulco_interno,
       p.altura_sulco_externo,
       p.status,
       p.dot,
       p.valor,
       mob.codigo                               as cod_modelo_banda,
       mob.nome                                 as nome_modelo_banda,
       mob.qt_sulcos                            as qt_sulcos_modelo_banda,
       mob.altura_sulcos                        as altura_sulcos_modelo_banda,
       mab.codigo                               as cod_marca_banda,
       mab.nome                                 as nome_marca_banda,
       pvv.valor                                as valor_banda,
       po.posicao_prolog                        as posicao_pneu,
       coalesce(ppne.nomenclatura :: text, '-') as posicao_aplicado_cliente,
       vei.codigo                               as cod_veiculo_aplicado,
       vei.placa                                as placa_aplicado,
       vei.identificador_frota                  as identificador_frota
from pneu p
         join modelo_pneu mop on mop.codigo = p.cod_modelo
         join marca_pneu mp on mp.codigo = mop.cod_marca
         join dimensao_pneu pd on pd.codigo = p.cod_dimensao
         join unidade u on u.codigo = p.cod_unidade
         join regional r on u.cod_regional = r.codigo
         left join veiculo_pneu vp on p.codigo = vp.cod_pneu
         left join veiculo vei on vei.codigo = vp.cod_veiculo
         left join veiculo_tipo vt on vt.codigo = vei.cod_tipo and vt.cod_empresa = p.cod_empresa
         left join veiculo_diagrama vd on vt.cod_diagrama = vd.codigo
         left join pneu_ordem po on vp.posicao = po.posicao_prolog
         left join modelo_banda mob on mob.codigo = p.cod_modelo_banda and mob.cod_empresa = u.cod_empresa
         left join marca_banda mab on mab.codigo = mob.cod_marca and mab.cod_empresa = mob.cod_empresa
         left join pneu_valor_vida pvv on pvv.cod_pneu = p.codigo and pvv.vida = p.vida_atual
         left join pneu_posicao_nomenclatura_empresa ppne
                   on ppne.cod_empresa = p.cod_empresa and ppne.cod_diagrama = vd.codigo and
                      ppne.posicao_prolog = vp.posicao
where vei.placa = f_placa
  and vei.cod_empresa = (select u.cod_empresa from unidade u where u.codigo = f_cod_unidade)
order by po.ordem_exibicao;
$$;

drop function func_pneu_get_pneu_by_codigo(bigint);
create or replace function func_pneu_get_pneu_by_codigo(f_cod_pneu bigint)
    returns table
            (
                codigo                       bigint,
                codigo_cliente               text,
                dot                          text,
                valor                        real,
                cod_unidade_alocado          bigint,
                cod_regional_alocado         bigint,
                pneu_novo_nunca_rodado       boolean,
                cod_marca_pneu               bigint,
                nome_marca_pneu              text,
                cod_modelo_pneu              bigint,
                nome_modelo_pneu             text,
                qt_sulcos_modelo_pneu        smallint,
                cod_marca_banda              bigint,
                nome_marca_banda             text,
                altura_sulcos_modelo_pneu    real,
                cod_modelo_banda             bigint,
                nome_modelo_banda            text,
                qt_sulcos_modelo_banda       smallint,
                altura_sulcos_modelo_banda   real,
                valor_banda                  real,
                altura                       numeric,
                largura                      numeric,
                aro                          numeric,
                cod_dimensao                 bigint,
                altura_sulco_central_interno real,
                altura_sulco_central_externo real,
                altura_sulco_interno         real,
                altura_sulco_externo         real,
                pressao_recomendada          real,
                pressao_atual                real,
                status                       text,
                vida_atual                   integer,
                vida_total                   integer,
                posicao_pneu                 integer,
                posicao_aplicado_cliente     text,
                cod_veiculo_aplicado         bigint,
                placa_aplicado               text,
                identificador_frota          text
            )
    language sql
as
$$
select p.codigo,
       p.codigo_cliente,
       p.dot,
       p.valor,
       u.codigo                         as cod_unidade_alocado,
       r.codigo                         as cod_regional_alocado,
       p.pneu_novo_nunca_rodado,
       mp.codigo                        as cod_marca_pneu,
       mp.nome                          as nome_marca_pneu,
       mop.codigo                       as cod_modelo_pneu,
       mop.nome                         as nome_modelo_pneu,
       mop.qt_sulcos                    as qt_sulcos_modelo_pneu,
       mab.codigo                       as cod_marca_banda,
       mab.nome                         as nome_marca_banda,
       mop.altura_sulcos                as altura_sulcos_modelo_pneu,
       mob.codigo                       as cod_modelo_banda,
       mob.nome                         as nome_modelo_banda,
       mob.qt_sulcos                    as qt_sulcos_modelo_banda,
       mob.altura_sulcos                as altura_sulcos_modelo_banda,
       pvv.valor                        as valor_banda,
       pd.altura,
       pd.largura,
       pd.aro,
       pd.codigo                        as cod_dimensao,
       p.altura_sulco_central_interno,
       p.altura_sulco_central_externo,
       p.altura_sulco_interno,
       p.altura_sulco_externo,
       p.pressao_recomendada,
       p.pressao_atual,
       p.status,
       p.vida_atual,
       p.vida_total,
       vp.posicao                       as posicao_pneu,
       coalesce(ppne.nomenclatura, '-') as posicao_aplicado_cliente,
       vei.codigo                       as cod_veiculo_aplicado,
       vei.placa                        as placa_aplicado,
       vei.identificador_frota          as identificador_frota
from pneu p
         join modelo_pneu mop on mop.codigo = p.cod_modelo
         join marca_pneu mp on mp.codigo = mop.cod_marca
         join dimensao_pneu pd on pd.codigo = p.cod_dimensao
         join unidade u on u.codigo = p.cod_unidade
         join empresa e on u.cod_empresa = e.codigo
         join regional r on u.cod_regional = r.codigo
         left join modelo_banda mob on mob.codigo = p.cod_modelo_banda and mob.cod_empresa = u.cod_empresa
         left join marca_banda mab on mab.codigo = mob.cod_marca and mab.cod_empresa = mob.cod_empresa
         left join pneu_valor_vida pvv on pvv.cod_pneu = p.codigo and pvv.vida = p.vida_atual
         left join veiculo_pneu vp on vp.cod_pneu = p.codigo
         left join veiculo vei on vei.codigo = vp.cod_veiculo
         left join veiculo_tipo vt on vt.codigo = vei.cod_tipo and vt.cod_empresa = e.codigo
         left join veiculo_diagrama vd on vt.cod_diagrama = vd.codigo
         left join pneu_posicao_nomenclatura_empresa ppne on ppne.cod_empresa = e.codigo
    and ppne.cod_diagrama = vd.codigo
    and ppne.posicao_prolog = vp.posicao
where p.codigo = f_cod_pneu
order by p.codigo_cliente asc;
$$;

drop function func_pneu_get_pneu_by_cod_veiculo(bigint);
create or replace function func_pneu_get_pneu_by_cod_veiculo(f_cod_veiculo bigint)
    returns table
            (
                nome_marca_pneu              varchar(255),
                cod_marca_pneu               bigint,
                codigo                       bigint,
                codigo_cliente               varchar(255),
                cod_unidade_alocado          bigint,
                cod_regional_alocado         bigint,
                pressao_atual                real,
                vida_atual                   integer,
                vida_total                   integer,
                pneu_novo_nunca_rodado       boolean,
                nome_modelo_pneu             varchar(255),
                cod_modelo_pneu              bigint,
                qt_sulcos_modelo_pneu        smallint,
                altura_sulcos_modelo_pneu    real,
                altura                       numeric,
                largura                      numeric,
                aro                          numeric,
                cod_dimensao                 bigint,
                pressao_recomendada          real,
                altura_sulco_central_interno real,
                altura_sulco_central_externo real,
                altura_sulco_interno         real,
                altura_sulco_externo         real,
                dot                          varchar(20),
                valor                        real,
                cod_modelo_banda             bigint,
                nome_modelo_banda            varchar(255),
                qt_sulcos_modelo_banda       smallint,
                altura_sulcos_modelo_banda   real,
                cod_marca_banda              bigint,
                nome_marca_banda             varchar(255),
                valor_banda                  real,
                posicao_pneu                 integer,
                nomenclatura                 varchar(255),
                cod_veiculo_aplicado         bigint,
                placa_aplicado               varchar(7)
            )
    language sql
as
$$
select mp.nome                                  as nome_marca_pneu,
       mp.codigo                                as cod_marca_pneu,
       p.codigo,
       p.codigo_cliente,
       u.codigo                                 as cod_unidade_alocado,
       r.codigo                                 as cod_regional_alocado,
       p.pressao_atual,
       p.vida_atual,
       p.vida_total,
       p.pneu_novo_nunca_rodado,
       mop.nome                                 as nome_modelo_pneu,
       mop.codigo                               as cod_modelo_pneu,
       mop.qt_sulcos                            as qt_sulcos_modelo_pneu,
       mop.altura_sulcos                        as altura_sulcos_modelo_pneu,
       pd.altura,
       pd.largura,
       pd.aro,
       pd.codigo                                as cod_dimensao,
       p.pressao_recomendada,
       p.altura_sulco_central_interno,
       p.altura_sulco_central_externo,
       p.altura_sulco_interno,
       p.altura_sulco_externo,
       coalesce(p.dot :: text, '-')             as dot,
       p.valor,
       mob.codigo                               as cod_modelo_banda,
       mob.nome                                 as nome_modelo_banda,
       mob.qt_sulcos                            as qt_sulcos_modelo_banda,
       mob.altura_sulcos                        as altura_sulcos_modelo_banda,
       mab.codigo                               as cod_marca_banda,
       mab.nome                                 as nome_marca_banda,
       pvv.valor                                as valor_banda,
       po.posicao_prolog                        as posicao_pneu,
       coalesce(ppne.nomenclatura :: text, '-') as nomenclatura,
       vei.codigo                               as cod_veiculo_aplicado,
       vei.placa                                as placa_aplicado
from pneu p
         join modelo_pneu mop on mop.codigo = p.cod_modelo
         join marca_pneu mp on mp.codigo = mop.cod_marca
         join dimensao_pneu pd on pd.codigo = p.cod_dimensao
         join unidade u on u.codigo = p.cod_unidade
         join regional r on u.cod_regional = r.codigo
         left join veiculo_pneu vp on p.codigo = vp.cod_pneu
         left join veiculo vei on vei.codigo = vp.cod_veiculo
         left join veiculo_tipo vt on vt.codigo = vei.cod_tipo and vt.cod_empresa = p.cod_empresa
         left join veiculo_diagrama vd on vt.cod_diagrama = vd.codigo
         left join pneu_ordem po on vp.posicao = po.posicao_prolog
         left join modelo_banda mob on mob.codigo = p.cod_modelo_banda and mob.cod_empresa = u.cod_empresa
         left join marca_banda mab on mab.codigo = mob.cod_marca and mab.cod_empresa = mob.cod_empresa
         left join pneu_valor_vida pvv on pvv.cod_pneu = p.codigo and pvv.vida = p.vida_atual
         left join pneu_posicao_nomenclatura_empresa ppne on
            ppne.cod_empresa = p.cod_empresa and
            ppne.cod_diagrama = vd.codigo and
            ppne.posicao_prolog = vp.posicao
where vei.codigo = f_cod_veiculo
order by po.ordem_exibicao asc;
$$;

drop function func_pneu_get_listagem_pneus_by_status(bigint[],text);
create or replace function func_pneu_get_listagem_pneus_by_status(f_cod_unidades bigint[],
                                                                  f_status_pneu text)
    returns table
            (
                codigo                       bigint,
                codigo_cliente               text,
                dot                          text,
                valor                        real,
                cod_unidade_alocado          bigint,
                nome_unidade_alocado         text,
                cod_regional_alocado         bigint,
                nome_regional_alocado        text,
                pneu_novo_nunca_rodado       boolean,
                cod_marca_pneu               bigint,
                nome_marca_pneu              text,
                cod_modelo_pneu              bigint,
                nome_modelo_pneu             text,
                qt_sulcos_modelo_pneu        smallint,
                cod_marca_banda              bigint,
                nome_marca_banda             text,
                altura_sulcos_modelo_pneu    real,
                cod_modelo_banda             bigint,
                nome_modelo_banda            text,
                qt_sulcos_modelo_banda       smallint,
                altura_sulcos_modelo_banda   real,
                valor_banda                  real,
                altura                       numeric,
                largura                      numeric,
                aro                          numeric,
                cod_dimensao                 bigint,
                altura_sulco_central_interno real,
                altura_sulco_central_externo real,
                altura_sulco_interno         real,
                altura_sulco_externo         real,
                pressao_recomendada          real,
                pressao_atual                real,
                status                       text,
                vida_atual                   integer,
                vida_total                   integer,
                posicao_pneu                 integer,
                posicao_aplicado_cliente     text,
                cod_veiculo_aplicado         bigint,
                placa_aplicado               text,
                identificador_frota          text
            )
    language sql
as
$$
select p.codigo,
       p.codigo_cliente,
       p.dot,
       p.valor,
       u.codigo                         as cod_unidade_alocado,
       u.nome                           as nome_unidade_alocado,
       r.codigo                         as cod_regional_alocado,
       r.regiao                         as nome_regional_alocado,
       p.pneu_novo_nunca_rodado,
       mp.codigo                        as cod_marca_pneu,
       mp.nome                          as nome_marca_pneu,
       mop.codigo                       as cod_modelo_pneu,
       mop.nome                         as nome_modelo_pneu,
       mop.qt_sulcos                    as qt_sulcos_modelo_pneu,
       mab.codigo                       as cod_marca_banda,
       mab.nome                         as nome_marca_banda,
       mop.altura_sulcos                as altura_sulcos_modelo_pneu,
       mob.codigo                       as cod_modelo_banda,
       mob.nome                         as nome_modelo_banda,
       mob.qt_sulcos                    as qt_sulcos_modelo_banda,
       mob.altura_sulcos                as altura_sulcos_modelo_banda,
       pvv.valor                        as valor_banda,
       pd.altura,
       pd.largura,
       pd.aro,
       pd.codigo                        as cod_dimensao,
       p.altura_sulco_central_interno,
       p.altura_sulco_central_externo,
       p.altura_sulco_interno,
       p.altura_sulco_externo,
       p.pressao_recomendada,
       p.pressao_atual,
       p.status,
       p.vida_atual,
       p.vida_total,
       vp.posicao                       as posicao_pneu,
       coalesce(ppne.nomenclatura, '-') as posicao_aplicado,
       vei.codigo                       as cod_veiculo,
       vei.placa                        as placa_aplicado,
       vei.identificador_frota          as identificador_frota
from pneu p
         join modelo_pneu mop on mop.codigo = p.cod_modelo
         join marca_pneu mp on mp.codigo = mop.cod_marca
         join dimensao_pneu pd on pd.codigo = p.cod_dimensao
         join unidade u on u.codigo = p.cod_unidade
         join empresa e on u.cod_empresa = e.codigo
         join regional r on u.cod_regional = r.codigo
         left join veiculo_pneu vp on vp.cod_pneu = p.codigo and vp.cod_unidade = p.cod_unidade
         left join veiculo vei on vei.codigo = vp.cod_veiculo
         left join veiculo_tipo vt on vt.codigo = vei.cod_tipo and vt.cod_empresa = e.codigo
         left join veiculo_diagrama vd on vt.cod_diagrama = vd.codigo
         left join modelo_banda mob on mob.codigo = p.cod_modelo_banda and mob.cod_empresa = u.cod_empresa
         left join marca_banda mab on mab.codigo = mob.cod_marca and mab.cod_empresa = mob.cod_empresa
         left join pneu_valor_vida pvv on pvv.cod_pneu = p.codigo and pvv.vida = p.vida_atual
         left join pneu_posicao_nomenclatura_empresa ppne on ppne.cod_empresa = e.codigo
    and ppne.cod_diagrama = vd.codigo
    and ppne.posicao_prolog = vp.posicao
where p.cod_unidade = any (f_cod_unidades)
  and p.status like f_status_pneu
order by p.codigo_cliente asc;
$$;