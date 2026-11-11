import { useEffect, useState } from 'react';
import { Button, Modal } from '@/components/ui';
import '../ModalEstoque.css';

const UNIDADES_DISPONIVEIS = [
  { valor: 'UN', rotulo: 'Unidades (UN)' },
  { valor: 'KG', rotulo: 'Quilogramas (KG)' },
  { valor: 'G', rotulo: 'Gramas (G)' },
  { valor: 'L', rotulo: 'Litros (L)' },
  { valor: 'ML', rotulo: 'Mililitros (ML)' },
];

export function ModalIngrediente({
  aberto,
  ingredienteEmEdicao,
  aoFechar,
  aoSalvar,
}) {
  const modoEdicao = Boolean(ingredienteEmEdicao);

  const [nome, setNome] = useState('');
  const [unidade, setUnidade] = useState('UN');
  const [custoUnidade, setCustoUnidade] = useState('');
  const [quantidadeInicial, setQuantidadeInicial] = useState('');
  const [operacaoEstoque, setOperacaoEstoque] = useState('NENHUMA');
  const [quantidadeMovimentacao, setQuantidadeMovimentacao] = useState('');
  const [erro, setErro] = useState('');
  const [salvando, setSalvando] = useState(false);

  useEffect(() => {
    if (ingredienteEmEdicao) {
      setNome(ingredienteEmEdicao.nome || '');
      setUnidade(ingredienteEmEdicao.unidade || 'UN');
      setCustoUnidade(String(ingredienteEmEdicao.custoUnidade ?? ''));
      setQuantidadeInicial('');
      setOperacaoEstoque('NENHUMA');
      setQuantidadeMovimentacao('');
    } else {
      setNome('');
      setUnidade('UN');
      setCustoUnidade('');
      setQuantidadeInicial('');
      setOperacaoEstoque('NENHUMA');
      setQuantidadeMovimentacao('');
    }
    setErro('');
  }, [ingredienteEmEdicao, aberto]);

  async function aoSubmeter(evento) {
    evento.preventDefault();
    setErro('');

    if (!nome.trim()) {
      setErro('Informe o nome do ingrediente.');
      return;
    }

    if (custoUnidade === '' || Number(custoUnidade) < 0) {
      setErro('Informe um custo por unidade válido.');
      return;
    }

    const ehUnidadeInteira = unidade === 'UN';

    if (
      !modoEdicao &&
      ehUnidadeInteira &&
      quantidadeInicial !== '' &&
      !Number.isInteger(Number(quantidadeInicial))
    ) {
      setErro('Para a unidade "UN", a quantidade inicial deve ser um número inteiro.');
      return;
    }

    if (
      modoEdicao &&
      operacaoEstoque !== 'NENHUMA' &&
      (!quantidadeMovimentacao || Number(quantidadeMovimentacao) <= 0)
    ) {
      setErro('Informe uma quantidade maior que zero para movimentar o estoque.');
      return;
    }

    if (
      modoEdicao &&
      operacaoEstoque !== 'NENHUMA' &&
      ehUnidadeInteira &&
      !Number.isInteger(Number(quantidadeMovimentacao))
    ) {
      setErro('Para a unidade "UN", a quantidade movimentada deve ser um número inteiro.');
      return;
    }

    try {
      setSalvando(true);
      await aoSalvar({
        nome: nome.trim(),
        unidade,
        custoUnidade: Number(custoUnidade),
        quantidadeInicial: Number(quantidadeInicial || 0),
        operacaoEstoque,
        quantidadeMovimentacao: Number(quantidadeMovimentacao || 0),
      });
      aoFechar();
    } catch (err) {
      setErro(err.message || 'Erro ao salvar ingrediente.');
    } finally {
      setSalvando(false);
    }
  }

  const passoQuantidade = unidade === 'UN' ? '1' : '0.001';
  const minimoMovimentacao = unidade === 'UN' ? '1' : '0.001';

  return (
    <Modal
      aberto={aberto}
      titulo={modoEdicao ? 'Editar Item do Estoque' : 'Adicionar Item ao Estoque'}
      subtitulo={
        modoEdicao
          ? 'Atualize os dados do insumo ou registre entrada/saída de estoque'
          : 'Cadastre um novo insumo no estoque do restaurante'
      }
      aoFechar={aoFechar}
    >
      <form className="form-modal" onSubmit={aoSubmeter} noValidate>
        {erro && <div className="form-modal__erro">{erro}</div>}

        <div className="form-modal__grade">
          <div className="form-modal__campo">
            <label htmlFor="ing-nome" className="form-modal__label">
              Nome do Item *
            </label>
            <input
              id="ing-nome"
              type="text"
              className="form-modal__input"
              placeholder="Ex: Pão Brioche"
              value={nome}
              onChange={(e) => setNome(e.target.value)}
            />
          </div>

          <div className="form-modal__campo">
            <label htmlFor="ing-unidade" className="form-modal__label">
              Unidade *
            </label>
            <select
              id="ing-unidade"
              className="form-modal__select"
              value={unidade}
              onChange={(e) => setUnidade(e.target.value)}
              disabled={modoEdicao}
              title={
                modoEdicao
                  ? 'A unidade de medida é definida na criação do ingrediente'
                  : ''
              }
            >
              {UNIDADES_DISPONIVEIS.map((op) => (
                <option key={op.valor} value={op.valor}>
                  {op.rotulo}
                </option>
              ))}
            </select>
          </div>

          <div className="form-modal__campo">
            <label htmlFor="ing-custo" className="form-modal__label">
              Custo por Unidade (R$) *
            </label>
            <input
              id="ing-custo"
              type="number"
              step="0.01"
              min="0"
              className="form-modal__input"
              placeholder="Ex: 1.50"
              value={custoUnidade}
              onChange={(e) => setCustoUnidade(e.target.value)}
            />
          </div>

          {!modoEdicao ? (
            <div className="form-modal__campo">
              <label htmlFor="ing-qtd-inicial" className="form-modal__label">
                Quantidade Inicial *
              </label>
              <input
                id="ing-qtd-inicial"
                type="number"
                step={passoQuantidade}
                min="0"
                className="form-modal__input"
                placeholder="Ex: 100"
                value={quantidadeInicial}
                onChange={(e) => setQuantidadeInicial(e.target.value)}
              />
            </div>
          ) : (
            <div className="form-modal__campo">
              <label htmlFor="ing-operacao" className="form-modal__label">
                Movimentar Estoque
              </label>
              <select
                id="ing-operacao"
                className="form-modal__select"
                value={operacaoEstoque}
                onChange={(e) => setOperacaoEstoque(e.target.value)}
              >
                <option value="NENHUMA">Sem movimentação</option>
                <option value="ENTRADA">Entrada (+ Repor estoque)</option>
                <option value="SAIDA">Saída (- Baixar estoque)</option>
              </select>
            </div>
          )}

          {modoEdicao && operacaoEstoque !== 'NENHUMA' && (
            <div className="form-modal__campo form-modal__campo--largura-total">
              <label htmlFor="ing-qtd-mov" className="form-modal__label">
                Quantidade da Movimentação ({unidade}) *
              </label>
              <input
                id="ing-qtd-mov"
                type="number"
                step={passoQuantidade}
                min={minimoMovimentacao}
                className="form-modal__input"
                placeholder="Informe a quantidade a adicionar ou consumir"
                value={quantidadeMovimentacao}
                onChange={(e) => setQuantidadeMovimentacao(e.target.value)}
              />
            </div>
          )}
        </div>

        <div className="form-modal__rodape">
          <Button type="button" variant="secundario" onClick={aoFechar}>
            Cancelar
          </Button>
          <Button type="submit" variant="primario" disabled={salvando}>
            {salvando
              ? 'Salvando...'
              : modoEdicao
              ? 'Salvar Alterações'
              : 'Adicionar Item'}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
