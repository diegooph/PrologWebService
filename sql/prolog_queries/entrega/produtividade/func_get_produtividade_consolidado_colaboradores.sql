create or replace function func_get_produtividade_consolidado_colaboradores(f_data_inicial date,
                                                                            f_data_final date,
                                                                            f_cod_unidade bigint,
                                                                            f_equipe text,
                                                                            f_funcao text)
    returns table
            (
                cpf             bigint,
                matricula_ambev integer,
                nome            text,
                data_nascimento date,
                funcao          text,
                mapas           bigint,
                caixas          real,
                valor           double precision
            )
    language sql
as
$$
select vpe.cpf              as cpf,
       vpe.matricula_ambev  as matricula_ambev,
       vpe.nome_colaborador as nome,
       vpe.data_nascimento  as data_nascimento,
       vpe.funcao           as funcao,
       count(vpe.mapa)      as mapas,
       sum(vpe.cxentreg)    as caixas,
       sum(vpe.valor)       as valor
from view_produtividade_extrato_com_total vpe
where vpe.data between f_data_inicial and f_data_final
  and cod_unidade = f_cod_unidade
  and nome_equipe like f_equipe
  and cod_funcao::text like f_funcao
group by vpe.cpf, vpe.matricula_ambev, vpe.nome_colaborador, vpe.data_nascimento, vpe.funcao
order by vpe.funcao, valor desc, vpe.nome_colaborador
$$;