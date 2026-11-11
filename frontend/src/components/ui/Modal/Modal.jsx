import './Modal.css';

export function Modal({ aberto, titulo, subtitulo, aoFechar, children }) {
  if (!aberto) {
    return null;
  }

  function aoClicarNoFundo(evento) {
    if (evento.target === evento.currentTarget) {
      aoFechar();
    }
  }

  return (
    <div
      className="ui-modal-overlay"
      onClick={aoClicarNoFundo}
      role="dialog"
      aria-modal="true"
    >
      <div className="ui-modal">
        <header className="ui-modal__cabecalho">
          <div>
            <h2 className="ui-modal__titulo">{titulo}</h2>
            {subtitulo && <p className="ui-modal__subtitulo">{subtitulo}</p>}
          </div>

          <button
            type="button"
            className="ui-modal__fechar"
            onClick={aoFechar}
            aria-label="Fechar janela"
          >
            &times;
          </button>
        </header>

        <div className="ui-modal__corpo">{children}</div>
      </div>
    </div>
  );
}
