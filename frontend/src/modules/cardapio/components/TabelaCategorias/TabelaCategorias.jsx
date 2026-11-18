import { useState } from 'react';
import '@/modules/estoque/components/TabelaEstoque.css';

function formatarData(dataIso) {
  if (!dataIso) return '-';
  try {
    return new Date(dataIso).toLocaleDateString('pt-BR');
  } catch {
    return '-';
  }
}

export function TabelaCategorias({ categorias, aoEditar, aoExcluir }) {
  const [termoBusca, setTermoBusca] = useState('');

  const categoriasFiltradas = categorias.filter((categoria) => {
    const termo = termoBusca.trim().toLowerCase();
    if (!termo) return true;

    const bateNome = categoria.nome?.toLowerCase().includes(termo);
    const bateDescricao = categoria.descricao?.toLowerCase().includes(termo);
    return bateNome || bateDescricao;
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
          placeholder="Buscar categorias..."
          value={termoBusca}
          onChange={(e) => setTermoBusca(e.target.value)}
        />
      </div>

      <div className="painel-tabela__container">
        <table className="painel-tabela">
          <thead>
            <tr>
              <th>Nome</th>
              <th>Descrição</th>
              <th>Criada em</th>
              <th className="painel-tabela__col-acoes">Ações</th>
            </tr>
          </thead>
          <tbody>
            {categoriasFiltradas.length === 0 ? (
              <tr>
                <td colSpan={4} className="painel-tabela__vazio">
                  Nenhuma categoria cadastrada.
                </td>
              </tr>
            ) : (
              categoriasFiltradas.map((categoria) => (
                <tr key={categoria.id}>
                  <td className="painel-tabela__celula-forte">
                    {categoria.nome}
                  </td>
                  <td>{categoria.descricao?.trim() || '—'}</td>
                  <td>{formatarData(categoria.dataCriacao)}</td>
                  <td className="painel-tabela__col-acoes">
                    <div className="painel-tabela__acoes">
                      <button
                        type="button"
                        className="painel-tabela__btn-acao painel-tabela__btn-acao--editar"
                        onClick={() => aoEditar(categoria)}
                        title="Editar categoria"
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
                        onClick={() => aoExcluir(categoria)}
                        title="Excluir categoria"
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
