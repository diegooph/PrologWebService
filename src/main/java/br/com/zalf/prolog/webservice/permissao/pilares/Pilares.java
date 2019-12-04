package br.com.zalf.prolog.webservice.permissao.pilares;

public final class Pilares {

    private Pilares() {
        // prevent instatiation
    }

    public static final int FROTA = 1;

    public static final class Frota extends Pilar {
        /**
         * Essa permissão libera tanto a visualização das transferências de veículos/pneus quanto a realização das
         * mesmas.
         */
        public static final int TRANSFERENCIA_PNEUS_VEICULOS = 141;

        public final class FarolStatusPlacas {
            private FarolStatusPlacas() {
            }

            public static final int VISUALIZAR = 10;
        }

        public final class Checklist {
            private Checklist() {
            }

            public static final int REALIZAR = 11;
            public static final int VISUALIZAR_TODOS = 118;

            public final class Modelo {
                private Modelo() {
                }

                public static final int VISUALIZAR = 112;
                public static final int CADASTRAR = 113;
                public static final int ALTERAR = 114;
            }
        }

        public final class Veiculo {
            private Veiculo() {
            }

            public static final int CADASTRAR = 14;
            public static final int ALTERAR = 16;
            public static final int VISUALIZAR = 115;
            public static final int VISUALIZAR_RELATORIOS = 122;
        }

        public final class Pneu {
            private Pneu() {
            }

            public static final int CADASTRAR = 15;
            public static final int ALTERAR = 17;
            public static final int VINCULAR_VEICULO = 111;
            public static final int VISUALIZAR = 116;

            public final class Movimentacao {
                private Movimentacao() {
                }

                //-- ‘Movimentação - Veículo / Estoque (Veículo -> Estoque • Estoque -> Veículo • Veículo -> Veículo)’
                public static final int MOVIMENTAR_VEICULO_ESTOQUE = 142;

                //-- ‘Movimentação - Análise (Estoque ou Veículo -> Análise • Análise -> Estoque)’
                public static final int MOVIMENTAR_ANALISE = 143;

                //-- ‘Movimentação - Descarte (Estoque ou Veículo ou Análise -> Descarte)’
                public static final int MOVIMENTAR_DESCARTE = 144;

                public static final int CADASTRAR_MOTIVOS_DESCARTE = 123;
                public static final int EDITAR_MOTIVOS_DESCARTE = 124;
            }
        }

        public final class Afericao {
            private Afericao() {
            }

            public static final int REALIZAR_AFERICAO_PLACA = 18;
            public static final int REALIZAR_AFERICAO_PNEU_AVULSO = 140;
            public static final int VISUALIZAR_TODAS_AFERICOES = 117;

            public final class ConfiguracaoAfericao {

                public static final int CONFIGURAR = 100;
            }
        }

        public final class OrdemServico {
            private OrdemServico() {
            }

            public final class Checklist {
                private Checklist() {
                }

                public static final int VISUALIZAR = 12;
                public static final int RESOLVER_ITEM = 13;
            }

            public final class Pneu {
                private Pneu() {
                }

                public static final int VISUALIZAR = 119;
                public static final int CONSERTAR_ITEM = 19;
            }
        }

        public final class Relatorios {
            private Relatorios() {
            }

            public static final int PNEU = 110;
            public static final int CHECKLIST = 121;
        }

        public final class Recapadora {
            private Recapadora() {
            }

            public static final int CADASTRO = 130;
            public static final int VISUALIZACAO = 131;
            public static final int EDICAO = 132;

            public final class TipoServico {
                private TipoServico() {}

                public static final int CADASTRO = 133;
                public static final int VISUALIZACAO = 134;
                public static final int EDICAO = 135;
            }
        }

        public final class SocorroRota {
            private SocorroRota() {
            }

            public static final int SOLICITAR_SOCORRO = 145;

            // Assumir um socorro. Invalidar um socorro. Finalizar um socorro.
            public static final int TRATAR_SOCORRO = 146;

            public static final int VISUALIZAR_SOCORROS_E_RELATORIOS = 147;
        }
    }

    public static final int SEGURANCA = 2;

    public static final class Seguranca extends Pilar {

        public static final int GSD = 20;

        public final class Relato {
            private Relato() {
            }

            public static final int REALIZAR = 21;
            public static final int CLASSIFICAR = 23;
            public static final int FECHAR = 24;
            public static final int VISUALIZAR = 25;
            public static final int RELATORIOS = 26;
        }
    }

    public static final int GENTE = 3;

    public static final class Gente extends Pilar {

        public final class Intervalo {
            private Intervalo() {
            }

            /**
             * Quem tiver essa permissão, automaticamente está liberado para visualizar suas pŕoprias marcações.
             */
            public static final int MARCAR_INTERVALO = 336;
            public static final int VISUALIZAR_TODAS_MARCACOES = 337;
            /**
             * Permite ao usuário criar, editar, ativar ou inativar marcações.
             */
            public static final int AJUSTE_MARCACOES = 338;

            public static final int CRIAR_TIPO_INTERVALO = 340;
            public static final int ALTERAR_TIPO_INTERVALO = 344;
            public static final int ATIVAR_INATIVAR_TIPO_INTERVALO = 341;
        }

        public final class ProntuarioCondutor {
            private ProntuarioCondutor() {
            }

            public static final int UPLOAD = 333;
            public static final int VISUALIZAR_PROPRIO = 334;
            public static final int VISUALIZAR_TODOS = 335;
        }

        public final class Relatorios {
            private Relatorios() {
            }

            public static final int QUIZ = 330;
            public static final int FALE_CONOSCO = 331;
            public static final int SOLICITACAO_FOLGA = 332;
            public static final int INTERVALOS = 342;
            public static final int TREINAMENTOS = 343;
        }

        public final class Treinamentos {
            private Treinamentos() {
            }

            public static final int VISUALIZAR_PROPRIOS = 30;
            public static final int CRIAR = 323;
            public static final int ALTERAR = 318;
        }

        public final class Calendario {
            private Calendario() {
            }

            public static final int VISUALIZAR_PROPRIOS = 32;
            public static final int CRIAR_EVENTO = 324;
            public static final int ALTERAR_EVENTO = 319;
        }

        public final class PreContracheque {
            private PreContracheque() {
            }

            public static final int UPLOAD_E_EDICAO = 34;
            public static final int VISUALIZAR = 35;
        }

        public final class Quiz {
            private Quiz() {
            }

            public static final int REALIZAR = 36;
            public static final int VISUALIZAR = 326;

            public final class Modelo {
                private Modelo() {
                }

                public static final int VISUALIZAR = 320;
                public static final int CRIAR = 37;
                public static final int ALTERAR = 321;
            }
        }

        public final class SolicitacaoFolga {
            private SolicitacaoFolga() {
            }

            public static final int REALIZAR = 38;
            public static final int FEEDBACK_SOLICITACAO = 39;
            public static final int VISUALIZAR = 327;
        }

        public final class Colaborador {
            private Colaborador() {
            }

            public static final int CADASTRAR = 310;
            public static final int EDITAR = 325;
            public static final int VISUALIZAR = 316;
        }

        public final class Permissao {
            private Permissao() {
            }

            public static final int VISUALIZAR = 328;
            public static final int VINCULAR_CARGO = 329;
        }

        public final class Equipe {
            private Equipe() {
            }

            public static final int CADASTRAR = 311;
            public static final int EDITAR = 313;
            public static final int VISUALIZAR = 317;
        }

        public final class FaleConosco {
            private FaleConosco() {
            }

            public static final int VISUALIZAR_TODOS = 322;
            public static final int REALIZAR = 314;
            public static final int FEEDBACK = 315;
        }
    }

    public static final int ENTREGA = 4;

    public static final class Entrega extends Pilar {

        public final class Indicadores {
            private Indicadores() {
            }

            public static final int INDICADORES = 40;
        }

        public final class Relatorios {
            private Relatorios() {
            }

            public static final int INDICADORES = 41;
            public static final int PRODUTIVIDADE = 48;
        }

        public final class Upload {
            private Upload() {
            }

            public static final int MAPA_TRACKING = 42;
            public static final int VERIFICACAO_DADOS = 43;
        }

        public final class EscalaDiaria {
            private EscalaDiaria() {
            }

            public static final int DELETAR = 410;
            public static final int INSERIR_REGISTRO = 411;
            public static final int VISUALIZAR = 412;
            public static final int EDITAR = 413;
        }

        public final class Meta {
            private Meta() {
            }

            public static final int EDITAR = 44;
            public static final int VISUALIZAR = 47;
        }

        public final class Produtividade {
            private Produtividade() {
            }

            public static final int INDIVIDUAL = 45;
            public static final int CONSOLIDADO = 46;
        }

        public final class RaizenProdutividade {
            private RaizenProdutividade() {
            }

            public static final int INSERIR_REGISTROS = 417;
            public static final int VISUALIZAR_TODOS = 414;
            public static final int VISUALIZAR_PROPRIOS = 415;
            public static final int EDITAR = 416;
            public static final int DELETAR = 418;
            public static final int VISUALIZAR_RELATORIOS = 419;
        }
    }

    public static final int GERAL = 5;

    public static final class Geral extends Pilar {

        public final class DispositivosMoveis {
            private DispositivosMoveis() {
            }

            public static final int GESTAO = 501;
        }
    }
}