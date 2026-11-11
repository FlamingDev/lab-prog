import { useCallback, useEffect, useState } from 'react';
import { Button, Toast } from '@/components/ui';
import { useAuth } from '@/modules/autenticacao/contexts/AuthContext';
import { BarraLateralEstoque } from '@/modules/estoque/components/BarraLateralEstoque/BarraLateralEstoque';
import { ModalIngrediente } from '@/modules/estoque/components/ModalIngrediente/ModalIngrediente';
import { ModalProdutoFichaTecnica } from '@/modules/estoque/components/ModalProdutoFichaTecnica/ModalProdutoFichaTecnica';
import { TabelaIngredientes } from '@/modules/estoque/components/TabelaIngredientes/TabelaIngredientes';
import { TabelaProdutos } from '@/modules/estoque/components/TabelaProdutos/TabelaProdutos';
import {
  atualizarIngrediente,
  criarIngrediente,
  excluirIngrediente,
  listarIngredientes,
  movimentarEstoqueIngrediente,
} from '@/modules/estoque/services/IngredienteService';
import {
  atualizarFichaTecnicaProduto,
  atualizarNomeProduto,
  criarProduto,
  listarProdutos,
} from '@/modules/estoque/services/ProdutoService';
import { buscarPrimeiroRestauranteDoUsuario } from '@/modules/restaurante/services/RestauranteService';
import './PaginaEstoque.css';

export function PaginaEstoque() {
  const { token } = useAuth();

  const [sidebarRecolhida, setSidebarRecolhida] = useState(false);
  const [abaAtiva, setAbaAtiva] = useState('ingredientes');

  const [restaurante, setRestaurante] = useState(null);
  const [ingredientes, setIngredientes] = useState([]);
  const [produtos, setProdutos] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erroGeral, setErroGeral] = useState('');

  // Estados dos modais e notificação
  const [modalIngredienteAberto, setModalIngredienteAberto] = useState(false);
  const [ingredienteEmEdicao, setIngredienteEmEdicao] = useState(null);

  const [modalProdutoAberto, setModalProdutoAberto] = useState(false);
  const [produtoEmEdicao, setProdutoEmEdicao] = useState(null);
  const [mensagemToast, setMensagemToast] = useState('');

  const carregarDadosDoRestaurante = useCallback(async () => {
    if (!token) return;

    try {
      setCarregando(true);
      setErroGeral('');

      const primeiroRestaurante = await buscarPrimeiroRestauranteDoUsuario(token);
      if (!primeiroRestaurante) {
        setRestaurante(null);
        setErroGeral('Nenhum restaurante vinculado à sua conta foi encontrado.');
        return;
      }

      setRestaurante(primeiroRestaurante);

      const [listaIngredientes, listaProdutos] = await Promise.all([
        listarIngredientes(primeiroRestaurante.id, token),
        listarProdutos(primeiroRestaurante.id, token),
      ]);

      setIngredientes(listaIngredientes);
      setProdutos(listaProdutos);
    } catch (err) {
      setErroGeral(err.message || 'Erro ao carregar os dados do estoque.');
    } finally {
      setCarregando(false);
    }
  }, [token]);

  useEffect(() => {
    carregarDadosDoRestaurante();
  }, [carregarDadosDoRestaurante]);

  // ==================== HANDLERS DE INGREDIENTES ====================
  function abrirModalNovoIngrediente() {
    setIngredienteEmEdicao(null);
    setModalIngredienteAberto(true);
  }

  function abrirModalEditarIngrediente(ingrediente) {
    setIngredienteEmEdicao(ingrediente);
    setModalIngredienteAberto(true);
  }

  async function salvarIngrediente(dadosFormulario) {
    if (!restaurante) return;

    const eraCriacao = !ingredienteEmEdicao;

    if (eraCriacao) {
      await criarIngrediente(restaurante.id, dadosFormulario, token);
    } else {
      await atualizarIngrediente(
        restaurante.id,
        ingredienteEmEdicao.id,
        dadosFormulario,
        token
      );

      if (
        dadosFormulario.operacaoEstoque &&
        dadosFormulario.operacaoEstoque !== 'NENHUMA'
      ) {
        await movimentarEstoqueIngrediente(
          restaurante.id,
          ingredienteEmEdicao.id,
          dadosFormulario.operacaoEstoque,
          dadosFormulario.quantidadeMovimentacao,
          token
        );
      }
    }

    const [novosIngredientes, novosProdutos] = await Promise.all([
      listarIngredientes(restaurante.id, token),
      listarProdutos(restaurante.id, token),
    ]);
    setIngredientes(novosIngredientes);
    setProdutos(novosProdutos);

    if (eraCriacao) {
      setMensagemToast('Ingrediente criado com sucesso');
    }
  }

  async function aoExcluirIngrediente(ingrediente) {
    if (!restaurante) return;
    const confirmar = window.confirm(
      `Deseja realmente excluir o ingrediente "${ingrediente.nome}"?`
    );
    if (!confirmar) return;

    try {
      await excluirIngrediente(restaurante.id, ingrediente.id, token);
      setIngredientes((atual) =>
        atual.filter((item) => item.id !== ingrediente.id)
      );
    } catch (err) {
      window.alert(err.message || 'Erro ao excluir ingrediente.');
    }
  }

  // ==================== HANDLERS DE PRODUTOS E FICHAS TÉCNICAS ====================
  function abrirModalNovoProduto() {
    setProdutoEmEdicao(null);
    setModalProdutoAberto(true);
  }

  function abrirModalEditarProduto(produto) {
    setProdutoEmEdicao(produto);
    setModalProdutoAberto(true);
  }

  async function salvarProduto(dadosFormulario) {
    if (!restaurante) return;

    if (!produtoEmEdicao) {
      const novoProduto = await criarProduto(
        restaurante.id,
        dadosFormulario,
        token
      );
      setProdutos((atual) => [...atual, novoProduto]);
      setMensagemToast('Produto criado com sucesso');
    } else {
      if (dadosFormulario.nome !== produtoEmEdicao.nome) {
        await atualizarNomeProduto(
          restaurante.id,
          produtoEmEdicao.id,
          dadosFormulario.nome,
          token
        );
      }

      const produtoAtualizado = await atualizarFichaTecnicaProduto(
        restaurante.id,
        produtoEmEdicao.id,
        dadosFormulario.ingredientes,
        token
      );

      setProdutos((atual) =>
        atual.map((item) =>
          item.id === produtoEmEdicao.id ? produtoAtualizado : item
        )
      );
    }
  }

  function aoExcluirProduto(produto) {
    const confirmar = window.confirm(
      `Deseja remover o produto "${produto.nome}" da listagem?`
    );
    if (!confirmar) return;

    setProdutos((atual) => atual.filter((item) => item.id !== produto.id));
  }

  const naAbaIngredientes = abaAtiva === 'ingredientes';

  return (
    <div className="pagina-estoque">
      <BarraLateralEstoque
        nomeRestaurante={restaurante?.nome}
        abaAtiva={abaAtiva}
        aoMudarAba={setAbaAtiva}
        recolhida={sidebarRecolhida}
      />

      <div className="pagina-estoque__area-principal">
        {/* Barra Superior Branca */}
        <header className="pagina-estoque__topbar">
          <button
            type="button"
            className="pagina-estoque__btn-sidebar"
            onClick={() => setSidebarRecolhida((v) => !v)}
            aria-label="Alternar menu lateral"
          >
            <svg
              viewBox="0 0 24 24"
              width="18"
              height="18"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2" />
              <line x1="9" y1="3" x2="9" y2="21" />
            </svg>
          </button>

          <span className="pagina-estoque__topbar-titulo">
            {naAbaIngredientes
              ? 'Estoque'
              : 'Produtos e Fichas Técnicas'}
          </span>
        </header>

        {/* Conteúdo Principal */}
        <main className="pagina-estoque__conteudo">
          <div className="pagina-estoque__cabecalho">
            <div>
              <h1 className="pagina-estoque__titulo">
                {naAbaIngredientes
                  ? 'Gestão de Estoque'
                  : 'Produtos e Fichas Técnicas'}
              </h1>
              <p className="pagina-estoque__subtitulo">
                {naAbaIngredientes
                  ? 'Acompanhe e gerencie os insumos do estoque do restaurante'
                  : 'Cadastre produtos e defina os insumos consumidos em cada ficha técnica'}
              </p>
            </div>

            {restaurante && (
              <Button
                type="button"
                variant="primario"
                onClick={
                  naAbaIngredientes
                    ? abrirModalNovoIngrediente
                    : abrirModalNovoProduto
                }
              >
                <span>+</span>
                <span>
                  {naAbaIngredientes ? 'Adicionar Item' : 'Adicionar Produto'}
                </span>
              </Button>
            )}
          </div>

          {/* Seletor rápido de abas */}
          <div className="pagina-estoque__abas">
            <button
              type="button"
              className={`pagina-estoque__aba ${
                naAbaIngredientes ? 'pagina-estoque__aba--ativa' : ''
              }`.trim()}
              onClick={() => setAbaAtiva('ingredientes')}
            >
              Insumos do Estoque ({ingredientes.length})
            </button>
            <button
              type="button"
              className={`pagina-estoque__aba ${
                !naAbaIngredientes ? 'pagina-estoque__aba--ativa' : ''
              }`.trim()}
              onClick={() => setAbaAtiva('produtos')}
            >
              Produtos e Fichas Técnicas ({produtos.length})
            </button>
          </div>

          {erroGeral && (
            <div className="pagina-estoque__alerta-erro">{erroGeral}</div>
          )}

          {carregando ? (
            <div className="pagina-estoque__carregando">
              Carregando dados do restaurante...
            </div>
          ) : (
            restaurante &&
            (naAbaIngredientes ? (
              <TabelaIngredientes
                ingredientes={ingredientes}
                aoEditar={abrirModalEditarIngrediente}
                aoExcluir={aoExcluirIngrediente}
              />
            ) : (
              <TabelaProdutos
                produtos={produtos}
                aoEditar={abrirModalEditarProduto}
                aoExcluir={aoExcluirProduto}
              />
            ))
          )}
        </main>
      </div>

      {/* Modal de Criar / Editar Ingrediente */}
      <ModalIngrediente
        aberto={modalIngredienteAberto}
        ingredienteEmEdicao={ingredienteEmEdicao}
        aoFechar={() => setModalIngredienteAberto(false)}
        aoSalvar={salvarIngrediente}
      />

      {/* Modal de Criar / Editar Produto + Ficha Técnica */}
      <ModalProdutoFichaTecnica
        aberto={modalProdutoAberto}
        produtoEmEdicao={produtoEmEdicao}
        ingredientesDisponiveis={ingredientes}
        aoFechar={() => setModalProdutoAberto(false)}
        aoSalvar={salvarProduto}
      />

      {/* Notificação verde temporária no topo central (1.5s) */}
      <Toast
        visivel={Boolean(mensagemToast)}
        mensagem={mensagemToast}
        posicao="topo-centro"
        duracaoMs={1500}
        aoFechar={() => setMensagemToast('')}
      />
    </div>
  );
}
