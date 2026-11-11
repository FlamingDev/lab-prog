import './Button.css';

export function Button({
  children,
  type = 'button',
  variant = 'primario',
  larguraTotal = false,
  disabled = false,
  onClick,
}) {
  const classes = [
    'ui-button',
    `ui-button--${variant}`,
    larguraTotal ? 'ui-button--full' : '',
  ]
    .filter(Boolean)
    .join(' ');

  return (
    <button
      type={type}
      className={classes}
      disabled={disabled}
      onClick={onClick}
    >
      {children}
    </button>
  );
}
