import { useAuth } from '@/modules/autenticacao/contexts/AuthContext';
import './BarraLateralEstoque.css';

export function BarraLateralEstoque({
  nomeRestaurante,
  abaAtiva,
  aoMudarAba,
  recolhida = false,
}) {
  const { usuarioLogado, encerrarSessao } = useAuth();

  return (
    <aside
      className={`barra-lateral ${
        recolhida ? 'barra-lateral--recolhida' : ''
      }`.trim()}
    >
      {/* Topo: Marca / Restaurante */}
      <div className="barra-lateral__topo">
        <div className="barra-lateral__logo" aria-hidden="true">
          <svg
            viewBox="0 0 24 24"
            width="20"
            height="20"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <path d="M3 2v7c0 1.1.9 2 2 2h4a2 2 0 0 0 2-2V2" />
            <path d="M7 2v20" />
            <path d="M21 15V2v0a5 5 0 0 0-5 5v6c0 1.1.9 2 2 2h3Zm0 0v7" />
          </svg>
        </div>
        {!recolhida && (
          <div className="barra-lateral__marca-textos">
            <span className="barra-lateral__marca-nome">
              {nomeRestaurante || 'Hardees'}
            </span>
            <span className="barra-lateral__marca-sub">Sistema PDV</span>
          </div>
        )}
      </div>

      {/* Menu Principal */}
      <nav className="barra-lateral__nav">
        {!recolhida && (
          <span className="barra-lateral__secao-titulo">Menu Principal</span>
        )}

        <ul className="barra-lateral__lista">
          <li>
            <button
              type="button"
              className={`barra-lateral__item ${
                abaAtiva === 'ingredientes' ? 'barra-lateral__item--ativo' : ''
              }`.trim()}
              onClick={() => aoMudarAba('ingredientes')}
            >
              <svg
                viewBox="0 0 24 24"
                width="17"
                height="17"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z" />
                <polyline points="3.27 6.96 12 12.01 20.73 6.96" />
                <line x1="12" y1="22.08" x2="12" y2="12" />
              </svg>
              {!recolhida && <span>Estoque (Insumos)</span>}
            </button>
          </li>

          <li>
            <button
              type="button"
              className={`barra-lateral__item ${
                abaAtiva === 'produtos' ? 'barra-lateral__item--ativo' : ''
              }`.trim()}
              onClick={() => aoMudarAba('produtos')}
            >
              <svg
                viewBox="0 0 24 24"
                width="17"
                height="17"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                <polyline points="14 2 14 8 20 8" />
                <line x1="16" y1="13" x2="8" y2="13" />
                <line x1="16" y1="17" x2="8" y2="17" />
              </svg>
              {!recolhida && <span>Produtos e Fichas Técnicas</span>}
            </button>
          </li>
        </ul>
      </nav>

      {/* Rodapé com o usuário realmente logado */}
      <div className="barra-lateral__rodape">
        <div className="barra-lateral__usuario">
          <div className="barra-lateral__avatar">
            {usuarioLogado?.inicial || 'U'}
          </div>
          {!recolhida && (
            <div className="barra-lateral__usuario-dados">
              <span
                className="barra-lateral__usuario-email"
                title={usuarioLogado?.identificador}
              >
                {usuarioLogado?.identificador || 'Usuário'}
              </span>
              <span className="barra-lateral__usuario-cargo">Admin</span>
            </div>
          )}
        </div>

        <button
          type="button"
          className="barra-lateral__sair"
          onClick={encerrarSessao}
          title="Sair da conta"
        >
          <svg
            viewBox="0 0 24 24"
            width="16"
            height="16"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
            <polyline points="16 17 21 12 16 7" />
            <line x1="21" y1="12" x2="9" y2="12" />
          </svg>
          {!recolhida && <span>Sair</span>}
        </button>
      </div>
    </aside>
  );
}
