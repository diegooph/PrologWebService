begin transaction;
-- PLI-78
-- Criaremos uma tabela para salvar uma flag indicando se para a empresa em questão devemos abrir serviços de pneus
-- ou não.
-- Precisamos dessa configuração pois algumas empresa vêm através da Praxio e não devemos abrir OS de Pneus para estas,
-- enquanto para empresas que utilizam o Prolog e vieram através dos nossos contatos, devemos abrir serviços
-- normalmente.
create table if not exists integracao.empresa_config_abertura_servico_pneu
(
    codigo                  bigserial not null,
    cod_empresa             bigint    not null,
    deve_abrir_servico_pneu boolean   not null,
    constraint unique_empresa_config_abertura_servico_pneu unique (cod_empresa)
);
comment on table integracao.empresa_config_abertura_servico_pneu
    is 'Tabela para salvar flag indicando se para a empresa em questão devemos abrir serviços de pneus ou não.
    Precisamos dessa configuração pois algumas empresa vêm através da Praxio e não devemos abrir OS de Pneus para
    estas, enquanto para empresas que utilizam o Prolog e vieram através dos nossos contatos, devemos abrir serviços
    normalmente.
    Assumimos que se a empresa não possui entrada nessa tabela, por default, ela não abre serviços, pois esse é o
    comportamento comum na integração.';
-- #####################################################################################################################

-- #####################################################################################################################
-- Criamos uma function para buscar as configurações da empresa dado um código de unidade.
create or replace function integracao.func_geral_busca_config_abertura_servico_pneu(f_cod_unidade bigint)
    returns boolean
    language plpgsql
as
$$
begin
    if (select exists(select ecasp.cod_empresa
                      from integracao.empresa_config_abertura_servico_pneu ecasp
                      where ecasp.cod_empresa =
                            (select u.cod_empresa from public.unidade u where u.codigo = f_cod_unidade)))
    then
        return (select ecasp.deve_abrir_servico_pneu
                from integracao.empresa_config_abertura_servico_pneu ecasp
                where ecasp.cod_empresa = (select u.cod_empresa from public.unidade u where u.codigo = f_cod_unidade));
    else
        return false;
    end if;
end;
$$;
-- #####################################################################################################################
end transaction;