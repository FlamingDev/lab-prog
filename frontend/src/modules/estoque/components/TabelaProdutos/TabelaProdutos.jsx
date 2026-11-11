import { useState } from 'react';
import '../TabelaEstoque.css';

function formatarMoeda(valor) {
  return Number(valor || 0).toLocaleString('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  });
}

function formatarData(dataIso) {
  if (!dataIso) return '-';
  try {
    return new Date(dataIso).toLocaleDateString('pt-BR');
  } catch {
    return '-';
  }
}

function formatarQuantidade(valor) {
  return Number(valor || 0).toLocaleString('pt-BR', {
    maximumFractionDigits: 3,
  });
}

export function TabelaProdutos({ produtos, aoEditar, aoExcluir }) {
  const [termoBusca, setTermoBusca] = useState('');

  const produtosFiltrados = produtos.filter((produto) => {
    const termo = termoBusca.trim().toLowerCase();
    if (!termo) return true;

    const bateNome = produto.nome?.toLowerCase().includes(termo);
    const bateIngrediente = produto.ingredientes?.some((ing) =>
      ing.nomeIngrediente?.toLowerCase().includes(termo)
    );
    return bateNome || bateIngrediente;
  });

  return (
    <div className="painel-tabela-card">
      <div className="painel-tabela__busca-wrapper">
        <svg
          className="painel-tabela__busca-icone"
          viewBox="0 0 24 24"
          width="16"
          height="16"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
        >
          <circle cx="11" cy="11" r="8" />
          <line x1="21" y1="21" x2="16.65" y2="16.65" />
        </svg>
        <input
          type="text"
          className="painel-tabela__busca-input"
          placeholder="Buscar produto ou insumo da ficha técnica..."
          value={termoBusca}
          onChange={(e) => setTermoBusca(e.target.value)}
        />
      </div>

      <div className="painel-tabela__container">
        <table className="painel-tabela">
          <thead>
            <tr>
              <th>Nome do Produto</th>
              <th>Ficha Técnica (Receita)</th>
              <th>Qtd. Insumos</th>
              <th>Custo de Produção</th>
              <th>Data de Criação</th>
              <th className="painel-tabela__col-acoes">Ações</th>
            </tr>
          </thead>
          <tbody>
            {produtosFiltrados.length === 0 ? (
              <tr>
                <td colSpan={6} className="painel-tabela__vazio">
                  Nenhum produto ou ficha técnica cadastrado.
                </td>
              </tr>
            ) : (
              produtosFiltrados.map((produto) => (
                <tr key={produto.id}>
                  <td className="painel-tabela__celula-forte">{produto.nome}</td>
                  <td>
                    <div className="painel-tabela__tags-ficha">
                      {produto.ingredientes?.map((item) => (
                        <span
                          key={item.ingredienteId}
                          className="painel-tabela__tag-ingrediente"
                        >
                          {item.nomeIngrediente}:{' '}
                          {formatarQuantidade(item.quantidade)}{' '}
                          {item.unidade?.toLowerCase()}
                        </span>
                      ))}
                    </div>
                  </td>
                  <td>{produto.ingredientes?.length || 0} itens</td>
                  <td>{formatarMoeda(produto.custoProducao)}</td>
                  <td>{formatarData(produto.dataCriacao)}</td>
                  <td className="painel-tabela__col-acoes">
                    <div className="painel-tabela__acoes">
                      <button
                        type="button"
                        className="painel-tabela__btn-acao painel-tabela__btn-acao--editar"
                        onClick={() => aoEditar(produto)}
                        title="Editar produto e ficha técnica"
                      >
                        <svg
                          viewBox="0 0 24 24"
                          width="15"
                          height="15"
                          fill="none"
                          stroke="currentColor"
                          strokeWidth="2"
                          strokeLinecap="round"
                          strokeLinejoin="round"
                        >
                          <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
                          <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
                        </svg>
                      </button>

                      <button
                        type="button"
                        className="painel-tabela__btn-acao painel-tabela__btn-acao--excluir"
                        onClick={() => aoExcluir(produto)}
                        title="Remover produto"
                      >
                        <svg
                          viewBox="0 0 24 24"
                          width="15"
                          height="15"
                          fill="none"
                          stroke="currentColor"
                          strokeWidth="2"
                          strokeLinecap="round"
                          strokeLinejoin="round"
                        >
                          <polyline points="3 6 5 6 21 6" />
                          <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
                          <line x1="10" y1="11" x2="10" y2="17" />
                          <line x1="14" y1="11" x2="14" y2="17" />
                        </svg>
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
