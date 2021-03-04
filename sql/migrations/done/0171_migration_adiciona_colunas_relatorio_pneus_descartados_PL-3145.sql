-- 2020-10-02 -> Adiciona colunas "observação movimentação" e "observação geral" (thaisksf - PL-3145).
drop function func_relatorio_pneus_descartados(text[], date, date);
create or replace function func_pneu_relatorio_pneus_descartados(f_cod_unidades text[],
                                                                 f_data_inicial date,
                                                                 f_data_final date)
    returns table
            (
                "unidade do descarte"               text,
                "responsável pelo descarte"         text,
                "data/hora do descarte"             text,
                "código do pneu"                    text,
                "marca do pneu"                     text,
                "modelo do pneu"                    text,
                "marca da banda"                    text,
                "modelo da banda"                   text,
                "dimensão do pneu"                  text,
                "última pressão"                    text,
                "origem descarte"                   text,
                "placa aplicado momento descarte"   text,
                "posição aplicado momento descarte" text,
                "total de vidas"                    text,
                "altura sulco interno"              text,
                "altura sulco central interno"      text,
                "altura sulco central externo"      text,
                "altura sulco externo"              text,
                "menor sulco"                       text,
                "dot"                               text,
                "motivo do descarte"                text,
                "foto 1"                            text,
                "foto 2"                            text,
                "foto 3"                            text,
                "observação movimentacao"           text,
                "observação geral"                  text
            )
    language sql
as
$$
select u.nome                                                           as unidade_do_descarte,
       c.nome                                                           as responsavel_pelo_descarte,
       to_char(mp.data_hora at time zone tz_unidade(p.cod_unidade),
               'DD/MM/YYYY HH24:MI')                                    as data_hora_descarte,
       p.codigo_cliente                                                 as codigo_pneu,
       map.nome                                                         as marca_pneu,
       mop.nome                                                         as modelo_pneu,
       mab.nome                                                         as marca_banda,
       mob.nome                                                         as modelo_banda,
       'Altura: ' || dp.altura || ' - Largura: ' || dp.largura || ' - Aro: ' || dp.aro
                                                                        as dimensao_pneu,
       replace(coalesce(trunc(p.pressao_atual) :: text, '-'), '.', ',') as ultima_pressao,
       mo.tipo_origem                                                   as origem_descarte,
       coalesce(mo.placa :: text, '-')                                  as placa_aplicado_momento_descarte,
       coalesce(ppne.nomenclatura :: text, '-')                         as posicao_aplicado_momento_descarte,
       p.vida_atual :: text                                             as total_vidas,
       func_pneu_format_sulco(p.altura_sulco_interno)                   as sulco_interno,
       func_pneu_format_sulco(p.altura_sulco_central_interno)           as sulco_central_interno,
       func_pneu_format_sulco(p.altura_sulco_central_externo)           as sulco_central_externo,
       func_pneu_format_sulco(p.altura_sulco_externo)                   as sulco_externo,
       func_pneu_format_sulco(least(p.altura_sulco_externo, p.altura_sulco_central_externo,
                                    p.altura_sulco_central_interno,
                                    p.altura_sulco_interno))            as menor_sulco,
       p.dot                                                            as dot,
       mmde.motivo                                                      as motivo_descarte,
       md.url_imagem_descarte_1                                         as foto_1,
       md.url_imagem_descarte_2                                         as foto_2,
       md.url_imagem_descarte_3                                         as foto_3,
       coalesce(m.observacao ::text, '-')                               as observacao_movimentacao,
       coalesce(mp.observacao :: text, '-')                             as observacao_geral
from pneu p
         join modelo_pneu mop on p.cod_modelo = mop.codigo
         join marca_pneu map on mop.cod_marca = map.codigo
         join dimensao_pneu dp on p.cod_dimensao = dp.codigo
         join unidade u on p.cod_unidade = u.codigo
         left join modelo_banda mob on p.cod_modelo_banda = mob.codigo
         left join marca_banda mab on mob.cod_marca = mab.codigo
         left join movimentacao_processo mp on p.cod_unidade = mp.cod_unidade
         left join movimentacao m on mp.codigo = m.cod_movimentacao_processo
         left join movimentacao_origem mo on m.codigo = mo.cod_movimentacao
         left join movimentacao_destino md on m.codigo = md.cod_movimentacao
         left join colaborador c on mp.cpf_responsavel = c.cpf
         left join movimentacao_motivo_descarte_empresa mmde
                   on md.cod_motivo_descarte = mmde.codigo and c.cod_empresa = mmde.cod_empresa
         left join pneu_posicao_nomenclatura_empresa ppne
                   on ppne.cod_diagrama = mo.cod_diagrama
                       and ppne.posicao_prolog = mo.posicao_pneu_origem
                       and ppne.cod_empresa = u.cod_empresa
where p.cod_unidade :: text like any (f_cod_unidades)
  and p.status = 'DESCARTE'
  and m.cod_pneu = p.codigo
  and md.tipo_destino = 'DESCARTE'
  and (mp.data_hora at time zone tz_unidade(mp.cod_unidade)) :: date >= f_data_inicial
  and (mp.data_hora at time zone tz_unidade(mp.cod_unidade)) :: date <= f_data_final
order by u.nome;
$$;
