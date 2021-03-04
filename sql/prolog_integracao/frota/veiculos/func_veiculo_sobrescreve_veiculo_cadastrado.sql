-- Sobre:
--
-- Function disponível na API do ProLog para sobrescrever as informações de um veículo.
--
-- Esta function não atualiza a tabela 'integracao.veiculo_cadastrado'. Essa foi uma escolha proposital. Quem chamar
-- esta function, caso tenha interesse, deve atualizar a tabela 'integracao.veiculo_cadastrado'.
--
-- Precondições:
-- 1) Caso houver uma mudança da unidade onde o pneu está alocado, devemos deletar logicamente todos os serviços
-- refererentes a manutenção (calibragens, inspeções e movimentações) do pneu.
-- 2) Devemos sempre remover os pneus aplicados e move-los para o 'ESTOQUE'. O tratamento é feito antes de a function
-- ser chamada.
--
-- Histórico:
-- 2020-01-22 -> Function criada (diogenesvanzella - PLI-64).
-- 2020-02-26 -> Adiciona a manipulação do código de diagrama (wvinim - PL-1965).
-- 2020-09-11 -> Altera function para utilizar function de update de veículo padrão (luiz_fp - PL-3097).
-- 2020-11-09 -> Altera function  para adequar as mudanças na function de update (steinert999 - PL-3223).
create or replace function
    integracao.func_veiculo_sobrescreve_veiculo_cadastrado(f_placa_veiculo text,
                                                           f_cod_unidade_veiculo bigint,
                                                           f_km_atual_veiculo bigint,
                                                           f_cod_tipo_veiculo bigint,
                                                           f_cod_modelo_veiculo bigint)
    returns void
    language plpgsql
as
$$
declare
    v_cod_empresa_veiculo       bigint;
    v_cod_unidade_atual_veiculo bigint;
    v_cod_veiculo_prolog        bigint;
    v_novo_identificador_frota  text;
    v_novo_status_veiculo       boolean;
    v_novo_possui_hubodometro   boolean;
begin
    select vd.cod_empresa,
           vd.cod_unidade,
           vd.codigo,
           vd.identificador_frota,
           vd.status_ativo,
           vd.possui_hubodometro
    into strict
        v_cod_empresa_veiculo,
        v_cod_unidade_atual_veiculo,
        v_cod_veiculo_prolog,
        v_novo_identificador_frota,
        v_novo_status_veiculo,
        v_novo_possui_hubodometro
    from veiculo_data vd
    where vd.placa = f_placa_veiculo;

    -- Devemos tratar os serviços abertos para o veículo (setar fechado_integracao), apenas se a unidade mudar.
    if (v_cod_unidade_atual_veiculo <> f_cod_unidade_veiculo)
    then
        perform integracao.func_veiculo_deleta_servicos_abertos_placa(f_placa_veiculo,
                                                                      f_cod_unidade_veiculo);

        -- A function que atualiza veículo não atualiza o código da unidade, pois essa coluna não deve mesmo mudar em
        -- um update convencional, apenas através de uma transferência entre unidades, que é um outro processo.
        -- Como a integração precisa desse comportamento, precisamos fazer um novo update dessa coluna caso os códigos
        -- de unidade tenha sido alterados.
        update veiculo
        set cod_unidade = f_cod_unidade_veiculo
        where codigo = v_cod_veiculo_prolog
          and cod_unidade = v_cod_unidade_atual_veiculo;
    end if;

    perform func_veiculo_atualiza_veiculo(v_cod_veiculo_prolog,
                                          f_placa_veiculo,
                                          v_novo_identificador_frota,
                                          f_km_atual_veiculo,
                                          f_cod_tipo_veiculo,
                                          f_cod_modelo_veiculo,
                                          v_novo_status_veiculo,
                                          v_novo_possui_hubodometro,
                                          null,
                                          'API',
                                          now(),
                                          null);
end;
$$;