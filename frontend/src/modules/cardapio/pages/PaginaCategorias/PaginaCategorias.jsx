import { useCallback, useEffect, useState } from 'react';
import { Button, Toast } from '@/components/ui';
import { useAuth } from '@/modules/autenticacao/contexts/AuthContext';
import { ModalCategoria } from '@/modules/cardapio/components/ModalCategoria/ModalCategoria';
import { TabelaCategorias } from '@/modules/cardapio/components/TabelaCategorias/TabelaCategorias';
import {
  atualizarCategoria,
  criarCategoria,
  excluirCategoria,
  listarCategorias,
} from '@/modules/cardapio/services/CategoriaService';
import { BarraLateralEstoque } from '@/modules/estoque/components/BarraLateralEstoque/BarraLateralEstoque';
import { buscarPrimeiroRestauranteDoUsuario } from '@/modules/restaurante/services/RestauranteService';
import '@/modules/estoque/pages/PaginaEstoque/PaginaEstoque.css';

export function PaginaCategorias() {
  const { token } = useAuth();

  const [sidebarRecolhida, setSidebarRecolhida] = useState(false);
  const [restaurante, setRestaurante] = useState(null);
  const [categorias, setCategorias] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erroGeral, setErroGeral] = useState('');

  const [modalAberto, setModalAberto] = useState(false);
  const [categoriaEmEdicao, setCategoriaEmEdicao] = useState(null);
  const [mensagemToast, setMensagemToast] = useState('');

  const carregarDados = useCallback(async () => {
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
      const listaCategorias = await listarCategorias(
        primeiroRestaurante.id,
        token
      );
      setCategorias(listaCategorias);
    } catch (err) {
      setErroGeral(err.message || 'Erro ao carregar categorias do restaurante.');
    } finally {
      setCarregando(false);
    }
  }, [token]);

  useEffect(() => {
    carregarDados();
  }, [carregarDados]);

  function abrirModalNovaCategoria() {
    setCategoriaEmEdicao(null);
    setModalAberto(true);
  }

  function abrirModalEditarCategoria(categoria) {
    setCategoriaEmEdicao(categoria);
    setModalAberto(true);
  }

  async function salvarCategoria(dadosFormulario) {
    if (!restaurante) return;

    if (!categoriaEmEdicao) {
      const nova = await criarCategoria(restaurante.id, dadosFormulario, token);
      setCategorias((atual) => [nova, ...atual]);
      setMensagemToast('Categoria criada com sucesso');
    } else {
      const atualizada = await atualizarCategoria(
        restaurante.id,
        categoriaEmEdicao.id,
        dadosFormulario,
        token
      );
      setCategorias((atual) =>
        atual.map((item) =>
          item.id === categoriaEmEdicao.id ? atualizada : item
        )
      );
      setMensagemToast('Categoria atualizada com sucesso');
    }
  }

  async function aoExcluirCategoria(categoria) {
    if (!restaurante) return;
    const confirmar = window.confirm(
      `Deseja realmente excluir a categoria "${categoria.nome}"?`
    );
    if (!confirmar) return;

    try {
      await excluirCategoria(restaurante.id, categoria.id, token);
      setCategorias((atual) =>
        atual.filter((item) => item.id !== categoria.id)
      );
    } catch (err) {
      window.alert(err.message || 'Erro ao excluir categoria.');
    }
  }

  return (
    <div className="pagina-estoque">
      <BarraLateralEstoque
        nomeRestaurante={restaurante?.nome}
        abaAtiva="categorias"
        recolhida={sidebarRecolhida}
      />

      <div className="pagina-estoque__area-principal">
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

          <span className="pagina-estoque__topbar-titulo">Categorias</span>
        </header>

        <main className="pagina-estoque__conteudo">
          <div className="pagina-estoque__cabecalho">
            <div>
              <h1 className="pagina-estoque__titulo">Categorias</h1>
              <p className="pagina-estoque__subtitulo">
                Gerencie as categorias disponíveis para os cardápios do restaurante
              </p>
            </div>

            {restaurante && (
              <Button
                type="button"
                variant="primario"
                onClick={abrirModalNovaCategoria}
              >
                <span>+</span>
                <span>Adicionar Categoria</span>
              </Button>
            )}
          </div>

          {erroGeral && (
            <div className="pagina-estoque__alerta-erro">{erroGeral}</div>
          )}

          {carregando ? (
            <div className="pagina-estoque__carregando">
              Carregando categorias...
            </div>
          ) : (
            restaurante && (
              <TabelaCategorias
                categorias={categorias}
                aoEditar={abrirModalEditarCategoria}
                aoExcluir={aoExcluirCategoria}
              />
            )
          )}
        </main>
      </div>

      <ModalCategoria
        aberto={modalAberto}
        categoriaEmEdicao={categoriaEmEdicao}
        aoFechar={() => setModalAberto(false)}
        aoSalvar={salvarCategoria}
      />

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
