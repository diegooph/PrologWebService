create or replace function suporte.func_pneu_cadastra_dimensao_pneu(f_altura bigint,
                                                                    f_largura bigint,
                                                                    f_aro real,
                                                                    out aviso_dimensao_criada text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    cod_dimensao_existente bigint := (select codigo
                                      from dimensao_pneu
                                      where largura = f_largura
                                        and altura = f_altura
                                        and aro = f_aro);
    cod_dimensao_criada    bigint;
begin
    perform throw_generic_error('o cadastro de dimensão deve ser realizado pelo site ou pelo app');
    perform suporte.func_historico_salva_execucao();
    --verifica se os dados informados são maiores que 0.
    if(f_altura < 0)
    then
        raise exception 'o valor atribuído para altura deve ser maior que 0(zero). valor informado: %', f_altura;
    end if;

    if(f_largura < 0)
    then
        raise exception 'o valor atribuído para largura deve ser maior que 0(zero). valor informado: %', f_largura;
    end if;

    if(f_aro < 0)
    then
        raise exception 'o valor atribuído para aro deve ser maior que 0(zero). valor informado: %', f_aro;
    end if;

    --verifica se essa dimensão existe na base de dados.
    if (cod_dimensao_existente is not null)
    then
        raise exception 'erro! essa dimensão já está cadastrada, possui o código = %.', cod_dimensao_existente;
    end if;

    --adiciona nova dimensão e retorna seu id.
    insert into dimensao_pneu(altura, largura, aro)
    values (f_altura, f_largura, f_aro) returning codigo into cod_dimensao_criada;

    --mensagem de sucesso.
    select 'dimensão cadastrada com sucesso! dimensão: ' || f_largura || '/' || f_altura || 'r' || f_aro ||
           ' com código: '
               || cod_dimensao_criada || '.'
    into aviso_dimensao_criada;
end
$$;