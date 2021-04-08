alter table implantacao.veiculo_diagrama_usuario_prolog
    add constraint fk_cod_diagrama_motorizado foreign key (cod_veiculo_diagrama, motorizado)
        references public.veiculo_diagrama (codigo, motorizado);