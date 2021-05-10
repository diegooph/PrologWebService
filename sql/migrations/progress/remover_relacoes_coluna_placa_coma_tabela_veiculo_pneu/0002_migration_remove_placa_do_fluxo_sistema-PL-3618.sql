create or replace function func_pneu_get_listagem_pneus_by_status(f_cod_unidades bigint[],
                                                                  f_status_pneu text)
    returns table
            (
                CODIGO                       bigint,
                CODIGO_CLIENTE               text,
                DOT                          text,
                VALOR                        real,
                COD_UNIDADE_ALOCADO          bigint,
                NOME_UNIDADE_ALOCADO         text,
                COD_REGIONAL_ALOCADO         bigint,
                NOME_REGIONAL_ALOCADO        text,
                PNEU_NOVO_NUNCA_RODADO       boolean,
                COD_MARCA_PNEU               bigint,
                NOME_MARCA_PNEU              text,
                COD_MODELO_PNEU              bigint,
                NOME_MODELO_PNEU             text,
                QT_SULCOS_MODELO_PNEU        smallint,
                COD_MARCA_BANDA              bigint,
                NOME_MARCA_BANDA             text,
                ALTURA_SULCOS_MODELO_PNEU    real,
                COD_MODELO_BANDA             bigint,
                NOME_MODELO_BANDA            text,
                QT_SULCOS_MODELO_BANDA       smallint,
                ALTURA_SULCOS_MODELO_BANDA   real,
                VALOR_BANDA                  real,
                ALTURA                       integer,
                LARGURA                      integer,
                ARO                          real,
                COD_DIMENSAO                 bigint,
                ALTURA_SULCO_CENTRAL_INTERNO real,
                ALTURA_SULCO_CENTRAL_EXTERNO real,
                ALTURA_SULCO_INTERNO         real,
                ALTURA_SULCO_EXTERNO         real,
                PRESSAO_RECOMENDADA          real,
                PRESSAO_ATUAL                real,
                STATUS                       text,
                VIDA_ATUAL                   integer,
                VIDA_TOTAL                   integer,
                POSICAO_PNEU                 integer,
                POSICAO_APLICADO_CLIENTE     text,
                COD_VEICULO_APLICADO         bigint,
                PLACA_APLICADO               text,
                IDENTIFICADOR_FROTA          text
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

create or replace function func_pneu_get_pneu_by_cod_veiculo(f_cod_veiculo bigint)
    returns table
            (
                NOME_MARCA_PNEU              varchar(255),
                COD_MARCA_PNEU               bigint,
                CODIGO                       bigint,
                CODIGO_CLIENTE               varchar(255),
                COD_UNIDADE_ALOCADO          bigint,
                COD_REGIONAL_ALOCADO         bigint,
                PRESSAO_ATUAL                real,
                VIDA_ATUAL                   integer,
                VIDA_TOTAL                   integer,
                PNEU_NOVO_NUNCA_RODADO       boolean,
                NOME_MODELO_PNEU             varchar(255),
                COD_MODELO_PNEU              bigint,
                QT_SULCOS_MODELO_PNEU        smallint,
                ALTURA_SULCOS_MODELO_PNEU    real,
                ALTURA                       integer,
                LARGURA                      integer,
                ARO                          real,
                COD_DIMENSAO                 bigint,
                PRESSAO_RECOMENDADA          real,
                ALTURA_SULCO_CENTRAL_INTERNO real,
                ALTURA_SULCO_CENTRAL_EXTERNO real,
                ALTURA_SULCO_INTERNO         real,
                ALTURA_SULCO_EXTERNO         real,
                DOT                          varchar(20),
                VALOR                        real,
                COD_MODELO_BANDA             bigint,
                NOME_MODELO_BANDA            varchar(255),
                QT_SULCOS_MODELO_BANDA       smallint,
                ALTURA_SULCOS_MODELO_BANDA   real,
                COD_MARCA_BANDA              bigint,
                NOME_MARCA_BANDA             varchar(255),
                VALOR_BANDA                  real,
                POSICAO_PNEU                 integer,
                NOMENCLATURA                 varchar(255),
                COD_VEICULO_APLICADO         bigint,
                PLACA_APLICADO               varchar(7)
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

create or replace function func_pneu_get_pneu_by_codigo(f_cod_pneu bigint)
    returns table
            (
                CODIGO                       bigint,
                CODIGO_CLIENTE               text,
                DOT                          text,
                VALOR                        real,
                COD_UNIDADE_ALOCADO          bigint,
                COD_REGIONAL_ALOCADO         bigint,
                PNEU_NOVO_NUNCA_RODADO       boolean,
                COD_MARCA_PNEU               bigint,
                NOME_MARCA_PNEU              text,
                COD_MODELO_PNEU              bigint,
                NOME_MODELO_PNEU             text,
                QT_SULCOS_MODELO_PNEU        smallint,
                COD_MARCA_BANDA              bigint,
                NOME_MARCA_BANDA             text,
                ALTURA_SULCOS_MODELO_PNEU    real,
                COD_MODELO_BANDA             bigint,
                NOME_MODELO_BANDA            text,
                QT_SULCOS_MODELO_BANDA       smallint,
                ALTURA_SULCOS_MODELO_BANDA   real,
                VALOR_BANDA                  real,
                ALTURA                       integer,
                LARGURA                      integer,
                ARO                          real,
                COD_DIMENSAO                 bigint,
                ALTURA_SULCO_CENTRAL_INTERNO real,
                ALTURA_SULCO_CENTRAL_EXTERNO real,
                ALTURA_SULCO_INTERNO         real,
                ALTURA_SULCO_EXTERNO         real,
                PRESSAO_RECOMENDADA          real,
                PRESSAO_ATUAL                real,
                STATUS                       text,
                VIDA_ATUAL                   integer,
                VIDA_TOTAL                   integer,
                POSICAO_PNEU                 integer,
                POSICAO_APLICADO_CLIENTE     text,
                COD_VEICULO_APLICADO         bigint,
                PLACA_APLICADO               text,
                IDENTIFICADOR_FROTA          text
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

create or replace function func_pneu_get_pneu_by_placa(f_placa varchar(7))
  returns table (
    NOME_MARCA_PNEU              varchar(255),
    COD_MARCA_PNEU               bigint,
    CODIGO                       bigint,
    CODIGO_CLIENTE               varchar(255),
    COD_UNIDADE_ALOCADO          bigint,
    COD_REGIONAL_ALOCADO         bigint,
    PRESSAO_ATUAL                real,
    VIDA_ATUAL                   integer,
    VIDA_TOTAL                   integer,
    PNEU_NOVO_NUNCA_RODADO       boolean,
    NOME_MODELO_PNEU             varchar(255),
    COD_MODELO_PNEU              bigint,
    QT_SULCOS_MODELO_PNEU        smallint,
    ALTURA_SULCOS_MODELO_PNEU    real,
    ALTURA                       integer,
    LARGURA                      integer,
    ARO                          real,
    COD_DIMENSAO                 bigint,
    PRESSAO_RECOMENDADA          real,
    ALTURA_SULCO_CENTRAL_INTERNO real,
    ALTURA_SULCO_CENTRAL_EXTERNO real,
    ALTURA_SULCO_INTERNO         real,
    ALTURA_SULCO_EXTERNO         real,
    STATUS                       varchar(255),
    DOT                          varchar(20),
    VALOR                        real,
    COD_MODELO_BANDA             bigint,
    NOME_MODELO_BANDA            varchar(255),
    QT_SULCOS_MODELO_BANDA       smallint,
    ALTURA_SULCOS_MODELO_BANDA   real,
    COD_MARCA_BANDA              bigint,
    NOME_MARCA_BANDA             varchar(255),
    VALOR_BANDA                  real,
    POSICAO_PNEU                 integer,
    POSICAO_APLICADO_CLIENTE     varchar(255),
    COD_VEICULO_APLICADO         bigint,
    PLACA_APLICADO               varchar(7),
    IDENTIFICADOR_FROTA          text
  )
language sql
as $$
select
    mp.nome                                  as nome_marca_pneu,
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
         left join pneu_posicao_nomenclatura_empresa ppne on
            ppne.cod_empresa = p.cod_empresa and
            ppne.cod_diagrama = vd.codigo and
            ppne.posicao_prolog = vp.posicao
where vei.placa = f_placa
order by po.ordem_exibicao asc;
$$;

create or replace function func_veiculo_transferencia_veiculos_selecao(f_cod_unidade_origem bigint)
  returns table(
    COD_VEICULO                 bigint,
    PLACA_VEICULO               text,
    KM_ATUAL_VEICULO            bigint,
    QTD_PNEUS_APLICADOS_VEICULO bigint)
language plpgsql
as $$
begin
return query
select
    v.codigo                                 as cod_veiculo,
    v.placa :: text                          as placa_veiculo,
    v.km                                     as km_atual_veiculo,
    count(*)
    -- Com esse filter veículos sem pneu retornam 0 na quantidade e não 1.
    filter (where vp.cod_pneu is not null) as qtd_pneus_aplicados_veiculo
from veiculo v
         left join veiculo_pneu vp on vp.cod_veiculo = v.codigo and vp.cod_unidade = v.cod_unidade
where v.cod_unidade = f_cod_unidade_origem
group by v.codigo, v.placa, v.km;
end;
$$;