import './LogoRestaurante.css';

export function LogoRestaurante({ nomeMarca = 'Hardees' }) {
  return (
    <div className="logo-restaurante" aria-label={nomeMarca}>
      <svg
        className="logo-restaurante__icone"
        viewBox="0 0 120 110"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
      >
        {/* Arcos / Swooshes da base */}
        <path
          d="M22 76C32 90 68 95 96 76C82 96 40 98 22 76Z"
          fill="#1b365d"
        />
        <path
          d="M28 83C45 95 78 93 98 72C86 89 52 93 28 83Z"
          fill="#eb980a"
        />

        {/* Garfo à esquerda */}
        <g fill="#eb980a">
          <rect x="27" y="45" width="2.8" height="26" rx="1.4" transform="rotate(-10 27 45)" />
          <path d="M22 33V43C22 45.5 24 47 26.5 47C29 47 31 45.5 31 43V33H29.3V41.5H27.4V33H25.7V41.5H23.8V33H22Z" transform="rotate(-10 26 40)" />
        </g>

        {/* Colher à direita */}
        <g fill="#eb980a">
          <rect x="89" y="46" width="2.8" height="25" rx="1.4" transform="rotate(10 89 46)" />
          <ellipse cx="92.5" cy="39" rx="4.8" ry="7" transform="rotate(10 92.5 39)" />
        </g>

        {/* Tablet / Prancheta central */}
        <rect
          x="41"
          y="30"
          width="38"
          height="48"
          rx="4"
          fill="#1b365d"
          stroke="#0f2342"
          strokeWidth="1.5"
        />
        <rect x="44.5" y="34.5" width="31" height="38" rx="2" fill="#f8fafc" />

        {/* Linhas e gráfico de barras dentro do tablet */}
        <rect x="48" y="39" width="16" height="2.2" rx="1" fill="#94a3b8" />
        <rect x="48" y="43.5" width="22" height="2.2" rx="1" fill="#cbd5e1" />
        <rect x="48" y="48" width="19" height="2.2" rx="1" fill="#cbd5e1" />

        {/* Barras de desempenho */}
        <rect x="49" y="62" width="4.5" height="6" rx="1" fill="#eb980a" />
        <rect x="55.5" y="58" width="4.5" height="10" rx="1" fill="#eb980a" />
        <rect x="62" y="54" width="4.5" height="14" rx="1" fill="#eb980a" />
        <rect x="68.5" y="50" width="4.5" height="18" rx="1" fill="#1b365d" />

        {/* Botão home do tablet */}
        <circle cx="60" cy="75.2" r="1.4" fill="#cbd5e1" />

        {/* Chapéu de Chef no topo */}
        <path
          d="M46 25C42 25 39 22 39 18C39 14.2 42 11.2 45.8 11.2C46.5 11.2 47.2 11.3 47.8 11.5C49.6 7.6 54.4 5 60 5C65.6 5 70.4 7.6 72.2 11.5C72.8 11.3 73.5 11.2 74.2 11.2C78 11.2 81 14.2 81 18C81 22 78 25 74 25V31H46V25Z"
          fill="#ffffff"
          stroke="#1b365d"
          strokeWidth="2"
          strokeLinejoin="round"
        />
        <path d="M46 27.5H74" stroke="#cbd5e1" strokeWidth="1.5" />
        <path d="M53 16V23M60 14V23M67 16V23" stroke="#cbd5e1" strokeWidth="1.3" strokeLinecap="round" />
      </svg>
      <span className="logo-restaurante__nome">{nomeMarca}</span>
      <span className="logo-restaurante__sublinhado" />
    </div>
  );
}
