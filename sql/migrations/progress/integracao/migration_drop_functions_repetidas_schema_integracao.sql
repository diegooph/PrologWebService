drop function integracao.func_pneu_atualiza_status_pneu_prolog(f_cod_pneu_sistema_integrado bigint,
    f_codigo_pneu_cliente character varying,
    f_cod_unidade_pneu bigint,
    f_cpf_colaborador_alteracao_status character varying,
    f_data_hora_alteracao_status timestamp without time zone,
    f_status_pneu character varying, f_trocou_de_banda boolean,
    f_cod_novo_modelo_banda_pneu bigint,
    f_valor_nova_banda_pneu numeric,
    f_placa_veiculo_pneu_aplicado character varying,
    f_posicao_veiculo_pneu_aplicado integer,
    f_token_integracao character varying);

drop function integracao.func_pneu_remove_vinculo_pneu_placa_posicao(f_cod_sistema_integrado_pneus bigint[]);

drop function integracao.func_veiculo_deleta_servicos_abertos_placa(f_placa_veiculo bigint, f_cod_unidade bigint);

