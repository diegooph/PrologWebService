--Busca os cargos que tem acesso a funcionalidades específicas do ProLog

SELECT cod_unidade, cod_funcao_colaborador FROM cargo_funcao_prolog_v11
GROUP BY cod_unidade, cod_funcao_colaborador
HAVING '{15,17,110}' <@ array_agg(cod_funcao_prolog)
ORDER BY cod_unidade, cod_funcao_colaborador;