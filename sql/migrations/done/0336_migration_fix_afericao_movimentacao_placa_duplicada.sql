drop function if exists func_pneu_get_pneu_by_placa(varchar);
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
                altura                       integer,
                largura                      integer,
                aro                          real,
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


drop function if exists func_afericao_get_configuracoes_nova_afericao_placa(text);
create or replace function func_afericao_get_configuracoes_nova_afericao_by_cod_veiculo(f_cod_veiculo bigint)
    returns table
            (
                sulco_minimo_descarte                  real,
                sulco_minimo_recapagem                 real,
                tolerancia_calibragem                  real,
                tolerancia_inspecao                    real,
                periodo_afericao_sulco                 integer,
                periodo_afericao_pressao               integer,
                forma_coleta_dados_sulco               text,
                forma_coleta_dados_pressao             text,
                forma_coleta_dados_sulco_pressao       text,
                pode_aferir_estepe                     boolean,
                variacao_aceita_sulco_menor_milimetros double precision,
                variacao_aceita_sulco_maior_milimetros double precision,
                bloquear_valores_menores               boolean,
                bloquear_valores_maiores               boolean,
                variacoes_sulco_default_prolog         boolean
            )
    language plpgsql
as
$$
declare
    f_cod_unidade      bigint;
    f_cod_tipo_veiculo bigint;
begin
    select into f_cod_unidade, f_cod_tipo_veiculo v.cod_unidade,
                                                  v.cod_tipo
    from veiculo v
    where v.codigo = f_cod_veiculo;

    return query
        select pru.sulco_minimo_descarte,
               pru.sulco_minimo_recapagem,
               pru.tolerancia_inspecao,
               pru.tolerancia_calibragem,
               pru.periodo_afericao_sulco,
               pru.periodo_afericao_pressao,
               config_pode_aferir.forma_coleta_dados_sulco,
               config_pode_aferir.forma_coleta_dados_pressao,
               config_pode_aferir.forma_coleta_dados_sulco_pressao,
               config_pode_aferir.pode_aferir_estepe,
               config_alerta_sulco.variacao_aceita_sulco_menor_milimetros,
               config_alerta_sulco.variacao_aceita_sulco_maior_milimetros,
               config_alerta_sulco.bloquear_valores_menores,
               config_alerta_sulco.bloquear_valores_maiores,
               config_alerta_sulco.usa_default_prolog as variacoes_sulco_default_prolog
        from func_afericao_get_config_tipo_afericao_veiculo(f_cod_unidade) as config_pode_aferir
                 join view_afericao_configuracao_alerta_sulco as config_alerta_sulco
                      on config_pode_aferir.cod_unidade_configuracao = config_alerta_sulco.cod_unidade
                 join pneu_restricao_unidade pru
                      on pru.cod_unidade = config_pode_aferir.cod_unidade_configuracao
        where config_pode_aferir.cod_unidade_configuracao = f_cod_unidade
          and config_pode_aferir.cod_tipo_veiculo = f_cod_tipo_veiculo;
end;
$$;