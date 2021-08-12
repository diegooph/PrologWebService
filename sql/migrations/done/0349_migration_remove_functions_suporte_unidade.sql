drop function suporte.func_unidade_altera_latitude_longitude(f_cod_unidade bigint,
    f_latitude_unidade text,
    f_longitude_unidade text);
drop function suporte.func_unidade_altera_regional(f_cod_unidade bigint,
    f_cod_regional bigint);
drop function suporte.func_unidade_cadastra_unidade(f_cod_empresa bigint,
    f_cod_regional bigint,
    f_nome_unidade text,
    f_timezone text,
    f_pilares_liberados integer[]);