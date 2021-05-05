create or replace function
    interno.func_clona_vinculo_veiculos_pneus(f_cod_unidade_base bigint, f_cod_unidade_usuario bigint)
    returns void
    language plpgsql
as
$$
declare
    v_cod_veiculos_com_vinculo text := (select array_agg(vp.cod_veiculo)
                                        from veiculo_pneu vp
                                        where vp.cod_unidade = f_cod_unidade_base);
begin
    -- COPIA VÍNCULOS, CASO EXISTAM.
    if (v_cod_veiculos_com_vinculo is not null)
    then
        with veiculos_base as (
            select row_number() over () as codigo_comparacao,
                   v.placa,
                   vdpp.posicao_prolog
            from veiculo_data v
                     join veiculo_tipo vt on v.cod_tipo = vt.codigo and v.cod_empresa = vt.cod_empresa
                     join veiculo_diagrama_posicao_prolog vdpp
                          on vt.cod_diagrama = vdpp.cod_diagrama
            where v.cod_unidade = f_cod_unidade_base
        ),
             veiculos_novos as (
                 select row_number() over () as codigo_comparacao, v.placa, v.cod_diagrama, v.codigo, vdpp.posicao_prolog
                 from veiculo_data v
                          join veiculo_tipo vt on v.cod_tipo = vt.codigo and v.cod_empresa = vt.cod_empresa
                          join veiculo_diagrama_posicao_prolog vdpp on vt.cod_diagrama = vdpp.cod_diagrama
                 where v.cod_unidade = f_cod_unidade_usuario),
             dados_de_para as (
                 select vn.codigo         as cod_veiculo_novo,
                        vn.placa          as placa_nova,
                        vn.posicao_prolog as posicao_prolog_novo,
                        vn.cod_diagrama   as cod_diagrama_novo,
                        pdn.codigo        as cod_pneu_novo
                 from veiculos_base vb
                          join veiculos_novos vn on vb.codigo_comparacao = vn.codigo_comparacao and vb.posicao_prolog = vn.posicao_prolog
                          join veiculo_pneu vp on vb.placa = vp.placa and vb.posicao_prolog = vp.posicao
                          join pneu_data pdb
                               on vp.status_pneu = pdb.status and vp.cod_unidade = pdb.cod_unidade and
                                  vp.cod_pneu = pdb.codigo
                          join pneu_data pdn
                               on pdb.codigo_cliente = pdn.codigo_cliente and
                                  pdn.cod_unidade = f_cod_unidade_usuario and
                                  pdn.status = 'EM_USO')
        insert
        into veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, cod_diagrama, cod_veiculo)
        select ddp.placa_nova,
               ddp.cod_pneu_novo,
               f_cod_unidade_usuario,
               ddp.posicao_prolog_novo,
               ddp.cod_diagrama_novo,
               ddp.cod_veiculo_novo
        from dados_de_para ddp;
    end if;
end;
$$;