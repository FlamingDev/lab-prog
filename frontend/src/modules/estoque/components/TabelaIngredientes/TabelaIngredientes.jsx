import { useState } from 'react';
import '../TabelaEstoque.css';

const NOMES_UNIDADES = {
  UN: 'Unidade (UN)',
  KG: 'Quilograma (KG)',
  G: 'Grama (G)',
  L: 'Litro (L)',
  ML: 'Mililitro (ML)',
};

function formatarMoeda(valor) {
  return Number(valor || 0).toLocaleString('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  });
}

function formatarQuantidade(valor) {
  return Number(valor || 0).toLocaleString('pt-BR', {
    maximumFractionDigits: 3,
  });
}

export function TabelaIngredientes({
  ingredientes,
  aoEditar,
  aoExcluir,
}) {
  const [termoBusca, setTermoBusca] = useState('');

  const ingredientesFiltrados = ingredientes.filter((item) =>
    item.nome.toLowerCase().includes(termoBusca.trim().toLowerCase())
  );

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
          placeholder="Buscar ingrediente no estoque..."
          value={termoBusca}
          onChange={(e) => setTermoBusca(e.target.value)}
        />
      </div>

      <div className="painel-tabela__container">
        <table className="painel-tabela">
          <thead>
            <tr>
              <th>Nome do Item</th>
              <th>Quantidade</th>
              <th>Unidade</th>
              <th>Custo por Unidade</th>
              <th>Valor Total em Estoque</th>
              <th className="painel-tabela__col-acoes">Ações</th>
            </tr>
          </thead>
          <tbody>
            {ingredientesFiltrados.length === 0 ? (
              <tr>
                <td colSpan={6} className="painel-tabela__vazio">
                  Nenhum ingrediente encontrado no estoque.
                </td>
              </tr>
            ) : (
              ingredientesFiltrados.map((ingrediente) => {
                const valorTotalEstoque =
                  Number(ingrediente.quantidadeTotal || 0) *
                  Number(ingrediente.custoUnidade || 0);

                return (
                  <tr key={ingrediente.id}>
                    <td className="painel-tabela__celula-forte">
                      {ingrediente.nome}
                    </td>
                    <td>
                      {formatarQuantidade(ingrediente.quantidadeTotal)}{' '}
                      {ingrediente.unidade.toLowerCase()}
                    </td>
                    <td>{NOMES_UNIDADES[ingrediente.unidade] || ingrediente.unidade}</td>
                    <td>{formatarMoeda(ingrediente.custoUnidade)}</td>
                    <td>{formatarMoeda(valorTotalEstoque)}</td>
                    <td className="painel-tabela__col-acoes">
                      <div className="painel-tabela__acoes">
                        <button
                          type="button"
                          className="painel-tabela__btn-acao painel-tabela__btn-acao--editar"
                          onClick={() => aoEditar(ingrediente)}
                          title="Editar ingrediente / movimentar estoque"
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
                          onClick={() => aoExcluir(ingrediente)}
                          title="Excluir ingrediente"
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
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
