import { useEffect } from 'react';
import './Toast.css';

export function Toast({
  visivel,
  mensagem,
  posicao = 'topo-centro',
  duracaoMs = 1500,
  aoFechar,
}) {
  useEffect(() => {
    if (!visivel || !mensagem) return undefined;

    const timer = setTimeout(() => {
      aoFechar?.();
    }, duracaoMs);

    return () => clearTimeout(timer);
  }, [visivel, mensagem, duracaoMs, aoFechar]);

  if (!visivel || !mensagem) {
    return null;
  }

  return (
    <div className={`ui-toast-container ui-toast-container--${posicao}`}>
      <div className="ui-toast" role="status" aria-live="polite">
        <svg
          className="ui-toast__icone"
          viewBox="0 0 24 24"
          width="18"
          height="18"
          fill="none"
          stroke="currentColor"
          strokeWidth="2.5"
          strokeLinecap="round"
          strokeLinejoin="round"
        >
          <polyline points="20 6 9 17 4 12" />
        </svg>
        <span>{mensagem}</span>
      </div>
    </div>
  );
}
