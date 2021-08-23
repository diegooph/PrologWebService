create or replace function func_pneus_get_listagem_pneus_movimentacoes_analise(f_cod_unidade bigint)
  returns table(
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
    identificador_frota          text,
    cod_movimentacao             bigint,
    cod_recapadora               bigint,
    nome_recapadora              text,
    cod_empresa_recapadora       bigint,
    recapadora_ativa             boolean,
    cod_coleta                   text)
language sql
as $$
with movimentacoes_analise as (
    select
        inner_table.codigo                 as cod_movimentacao,
        inner_table.cod_pneu               as cod_pneu,
        inner_table.cod_recapadora_destino as cod_recapadora,
        inner_table.nome                   as nome_recapadora,
        inner_table.cod_empresa            as cod_empresa_recapadora,
        inner_table.ativa                  as recapadora_ativa,
        inner_table.cod_coleta             as cod_coleta
    from (select
              mov.codigo,
              mov.cod_pneu,
              max(mov.codigo)
                  over (
                      partition by cod_pneu ) as max_cod_movimentacao,
              md.cod_recapadora_destino,
              rec.nome,
              rec.cod_empresa,
              rec.ativa,
              md.cod_coleta
          from movimentacao as mov
                   join movimentacao_destino as md on mov.codigo = md.cod_movimentacao
                   left join recapadora as rec on md.cod_recapadora_destino = rec.codigo
          where cod_unidade = f_cod_unidade and md.tipo_destino = 'ANALISE') as inner_table
    where codigo = inner_table.max_cod_movimentacao
)

select
    func.*,
    ma.cod_movimentacao,
    ma.cod_recapadora,
    ma.nome_recapadora,
    ma.cod_empresa_recapadora,
    ma.recapadora_ativa,
    ma.cod_coleta
from func_pneu_get_listagem_pneus_by_status(array[f_cod_unidade], 'ANALISE') as func
         join movimentacoes_analise ma on ma.cod_pneu = func.codigo;
$$;