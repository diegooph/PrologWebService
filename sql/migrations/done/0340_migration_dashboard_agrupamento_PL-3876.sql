drop function if exists func_dashboard_get_componentes_colaborador(f_user_token text);

create or replace function func_dashboard_get_componentes_colaborador(f_user_token text)
    returns table
            (
                codigo_componente           smallint,
                identificador_tipo          text,
                cod_pilar_prolog_componente smallint,
                titulo_componente           text,
                subtitulo_componente        text,
                descricao_componente        text,
                qtd_blocos_horizontais      smallint,
                qtd_blocos_verticais        smallint,
                url_endpoint_dados          text,
                codigo_agrupamento          smallint,
                nome_agrupamento            text
            )
    language sql
as
$$
select dc.codigo                      as codigo_componente,
       dct.identificador_tipo::text   as identificador_tipo,
       dc.cod_pilar_prolog_componente as cod_pilar_prolog_componente,
       dc.titulo::text                as titulo_componente,
       dc.subtitulo::text             as subtitulo_componente,
       dc.descricao                   as descricao_componente,
       dc.qtd_blocos_horizontais      as qtd_blocos_horizontais,
       dc.qtd_blocos_verticais        as qtd_blocos_verticais,
       dc.url_endpoint_dados          as url_endpoint_dados,
       fpa.codigo                     as codigo_agrupamento,
       fpa.nome                       as nome_agrupamento
from dashboard_componente dc
         join token_autenticacao ta on ta.token = f_user_token
         join colaborador c on ta.cpf_colaborador = c.cpf
         join cargo_funcao_prolog_v11 cfp
              on c.cod_funcao = cfp.cod_funcao_colaborador and c.cod_unidade = cfp.cod_unidade
         join dashboard_componente_funcao_prolog dcfp
              on cfp.cod_funcao_prolog = dcfp.cod_funcao_prolog and dc.codigo = dcfp.cod_componente
         join dashboard_componente_tipo dct on dc.cod_tipo_componente = dct.codigo
         join funcao_prolog_v11 f on dcfp.cod_funcao_prolog = f.codigo and dcfp.cod_pilar_prolog = f.cod_pilar
         join funcao_prolog_agrupamento fpa on f.cod_agrupamento = fpa.codigo and f.cod_pilar = fpa.cod_pilar
where dc.ativo = true
order by cod_pilar_prolog_componente;
$$;