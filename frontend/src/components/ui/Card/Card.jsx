import './Card.css';

export function Card({ children, className = '' }) {
  return (
    <div className={`ui-card ${className}`.trim()}>
      {children}
    </div>
  );
}
