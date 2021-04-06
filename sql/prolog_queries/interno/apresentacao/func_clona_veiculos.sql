create or replace function interno.func_clona_veiculos(f_cod_empresa_base bigint,
                                                       f_cod_unidade_base bigint,
                                                       f_cod_empresa_usuario bigint,
                                                       f_cod_unidade_usuario bigint)
    returns void
    language plpgsql
as
$$
declare
    v_placa_prefixo_padrao          text   := 'ZXY';
    v_placa_sufixo_padrao           bigint := 0;
    v_placa_verificacao             text;
    v_placas_validas_cadastro       text[];
    v_tentativa_buscar_placa_valida bigint := 0;

begin
    -- VERIFICA SE EXISTEM MODELOS DE VEÍCULOS PARA COPIAR.
    if not EXISTS(select mv.codigo from modelo_veiculo mv where mv.cod_empresa = f_cod_empresa_base)
    then
        raise exception
            'Não existem modelos de veículos para serem copiados da empresa de código: %.' , f_cod_empresa_base;
    end if;

    -- VERIFICA SE EXISTEM TIPOS DE VEÍCULOS PARA COPIAR.
    if not EXISTS(select vt.codigo from veiculo_tipo vt where vt.cod_empresa = f_cod_empresa_base)
    then
        raise exception
            'Não existem tipos de veículos para serem copiados da empresa de código: %.' , f_cod_empresa_base;
    end if;

    -- VERIFICA SE EXISTEM VEÍCULOS PARA COPIAR.
    if not EXISTS(select vd.codigo from veiculo_data vd where vd.cod_unidade = f_cod_unidade_base)
    then
        raise exception
            'Não existem veículos para serem copiados da unidade de código: %.' , f_cod_unidade_base;
    end if;

    -- COPIA OS MODELOS DE VEÍCULOS.
    insert into modelo_veiculo (nome,
                                cod_marca,
                                cod_empresa)
    select mv.nome,
           mv.cod_marca,
           f_cod_empresa_usuario
    from modelo_veiculo mv
    where mv.cod_empresa = f_cod_empresa_base
    on conflict on constraint nomes_unicos_por_empresa_e_marca do nothing;

    -- COPIA OS TIPOS DE VEÍCULOS.
    insert into veiculo_tipo(nome,
                             status_ativo,
                             cod_diagrama,
                             cod_empresa)
    select vt.nome,
           vt.status_ativo,
           vt.cod_diagrama,
           f_cod_empresa_usuario
    from veiculo_tipo vt
    where vt.cod_empresa = f_cod_empresa_base;

    --SELECIONA PLACAS VÁLIDAS PARA CADASTRO.
    while ((ARRAY_LENGTH(v_placas_validas_cadastro, 1) < (select COUNT(vd.placa)
                                                          from veiculo_data vd
                                                          where vd.cod_unidade = f_cod_unidade_base)) or
           (ARRAY_LENGTH(v_placas_validas_cadastro, 1) is null))
        loop
        --EXISTEM 10000 PLACAS DISPONÍVEIS PARA CADASTRO (DE ZXY0000 ATÉ ZXY9999),
        --CASO EXCEDA O NÚMERO DE TENTATIVAS - UM ERRO É MOSTRADO.
            if (v_tentativa_buscar_placa_valida = 10000)
            then
                raise exception
                    'Não existem placas válidas para serem cadastradas';
            end if;
            v_placa_verificacao := CONCAT(v_placa_prefixo_padrao, LPAD(v_placa_sufixo_padrao::text, 4, '0'));
            if not EXISTS(select vd.placa from veiculo_data vd where vd.placa ilike v_placa_verificacao)
            then
                -- PLACAS VÁLIDAS PARA CADASTRO.
                v_placas_validas_cadastro := ARRAY_APPEND(v_placas_validas_cadastro, v_placa_verificacao);
            end if;
            v_placa_sufixo_padrao := v_placa_sufixo_padrao + 1;
            v_tentativa_buscar_placa_valida := v_tentativa_buscar_placa_valida + 1;
        end loop;

    perform setval('veiculo_data_codigo_seq', (select max(vd.codigo + 1) from veiculo_data vd));

    with placas_validas_cadastro as (
        select ROW_NUMBER() over () as codigo,
               vdn                  as placa_cadastro
        from UNNEST(v_placas_validas_cadastro) vdn),
         veiculos_base as (
             select ROW_NUMBER() over () as codigo,
                    vd.placa             as placa_base,
                    vd.km                as km_base,
                    vd.cod_modelo        as modelo_base,
                    vd.cod_tipo          as tipo_base,
                    vd.cod_diagrama      as cod_diagrama_base,
                    vd.motorizado        as motorizado_base
             from veiculo_data vd
             where cod_unidade = f_cod_unidade_base
         ),
         dados_de_para as (
             select distinct on (pvc.placa_cadastro, vb.placa_base) pvc.placa_cadastro   as placa_cadastro,
                                                                    vb.placa_base        as placa_base,
                                                                    vb.km_base           as km_base,
                                                                    mva.codigo           as modelo_base,
                                                                    mvn.codigo           as modelo_novo,
                                                                    vta.codigo           as tipo_base,
                                                                    vtn.codigo           as tipo_novo,
                                                                    vb.cod_diagrama_base as cod_diagrama_base,
                                                                    vb.motorizado_base   as motorizado_base
             from veiculos_base vb
                      join modelo_veiculo mva on mva.codigo = vb.modelo_base
                      join modelo_veiculo mvn on mva.nome = mvn.nome and mva.cod_marca = mvn.cod_marca
                      join veiculo_tipo vta on vb.tipo_base = vta.codigo
                      join veiculo_tipo vtn on vta.nome = vtn.nome and vta.cod_diagrama = vtn.cod_diagrama
                      join placas_validas_cadastro pvc on pvc.codigo = vb.codigo
             where mva.cod_empresa = f_cod_empresa_base
               and mvn.cod_empresa = f_cod_empresa_usuario
               and vta.cod_empresa = f_cod_empresa_base
               and vtn.cod_empresa = f_cod_empresa_usuario)

         -- INSERE AS PLACAS DE->PARA.
    insert
    into veiculo_data(placa,
                      cod_unidade,
                      km,
                      status_ativo,
                      cod_tipo,
                      cod_modelo,
                      cod_eixos,
                      cod_unidade_cadastro,
                      deletado,
                      cod_empresa,
                      cod_diagrama,
                      motorizado)
    select ddp.placa_cadastro,
           f_cod_unidade_usuario,
           ddp.km_base,
           true,
           ddp.tipo_novo,
           ddp.modelo_novo,
           1,
           f_cod_unidade_usuario,
           false,
           f_cod_empresa_usuario,
           ddp.cod_diagrama_base,
           ddp.motorizado_base
    from dados_de_para ddp;
end ;
$$;
