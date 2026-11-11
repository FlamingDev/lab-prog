import { useEffect, useMemo, useState } from 'react';
import { Button, Modal } from '@/components/ui';
import '../ModalEstoque.css';

function formatarMoeda(valor) {
  return Number(valor || 0).toLocaleString('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  });
}

export function ModalProdutoFichaTecnica({
  aberto,
  produtoEmEdicao,
  ingredientesDisponiveis,
  aoFechar,
  aoSalvar,
}) {
  const modoEdicao = Boolean(produtoEmEdicao);

  const [nome, setNome] = useState('');
  const [itensFicha, setItensFicha] = useState([
    { ingredienteId: '', quantidade: '' },
  ]);
  const [erro, setErro] = useState('');
  const [salvando, setSalvando] = useState(false);

  useEffect(() => {
    if (produtoEmEdicao) {
      setNome(produtoEmEdicao.nome || '');
      const itensExistentes =
        produtoEmEdicao.ingredientes?.length > 0
          ? produtoEmEdicao.ingredientes.map((ing) => ({
              ingredienteId: ing.ingredienteId,
              quantidade: String(ing.quantidade ?? ''),
            }))
          : [{ ingredienteId: '', quantidade: '' }];
      setItensFicha(itensExistentes);
    } else {
      setNome('');
      setItensFicha([
        {
          ingredienteId: ingredientesDisponiveis[0]?.id || '',
          quantidade: '',
        },
      ]);
    }
    setErro('');
  }, [produtoEmEdicao, ingredientesDisponiveis, aberto]);

  const custoEstimadoTotal = useMemo(() => {
    return itensFicha.reduce((acumulador, item) => {
      const ing = ingredientesDisponiveis.find(
        (i) => i.id === item.ingredienteId
      );
      const qtd = Number(item.quantidade || 0);
      if (!ing || qtd <= 0) return acumulador;
      return acumulador + Number(ing.custoUnidade || 0) * qtd;
    }, 0);
  }, [itensFicha, ingredientesDisponiveis]);

  function atualizarLinha(indice, campo, valor) {
    setItensFicha((atual) =>
      atual.map((linha, i) =>
        i === indice ? { ...linha, [campo]: valor } : linha
      )
    );
  }

  function adicionarLinha() {
    setItensFicha((atual) => [
      ...atual,
      { ingredienteId: '', quantidade: '' },
    ]);
  }

  function removerLinha(indice) {
    setItensFicha((atual) => atual.filter((_, i) => i !== indice));
  }

  async function aoSubmeter(evento) {
    evento.preventDefault();
    setErro('');

    if (!nome.trim()) {
      setErro('O nome do produto é obrigatório.');
      return;
    }

    if (itensFicha.length === 0) {
      setErro('A ficha técnica deve possuir pelo menos um ingrediente.');
      return;
    }

    for (const item of itensFicha) {
      if (!item.ingredienteId) {
        setErro('Selecione o ingrediente em todas as linhas da ficha técnica.');
        return;
      }
      if (!item.quantidade || Number(item.quantidade) <= 0) {
        setErro('A quantidade de cada ingrediente deve ser maior que zero.');
        return;
      }

      const ingSelecionado = ingredientesDisponiveis.find(
        (i) => i.id === item.ingredienteId
      );
      if (
        ingSelecionado?.unidade === 'UN' &&
        !Number.isInteger(Number(item.quantidade))
      ) {
        setErro(
          `O ingrediente "${ingSelecionado.nome}" é medido em Unidades (UN) e deve ter quantidade inteira.`
        );
        return;
      }
    }

    const idsUnicos = new Set(itensFicha.map((i) => i.ingredienteId));
    if (idsUnicos.size !== itensFicha.length) {
      setErro(
        'Não é permitido informar o mesmo ingrediente mais de uma vez na ficha técnica.'
      );
      return;
    }

    try {
      setSalvando(true);
      await aoSalvar({
        nome: nome.trim(),
        ingredientes: itensFicha.map((i) => ({
          ingredienteId: i.ingredienteId,
          quantidade: Number(i.quantidade),
        })),
      });
      aoFechar();
    } catch (err) {
      setErro(err.message || 'Erro ao salvar produto e ficha técnica.');
    } finally {
      setSalvando(false);
    }
  }

  return (
    <Modal
      aberto={aberto}
      titulo={
        modoEdicao
          ? 'Editar Produto e Ficha Técnica'
          : 'Adicionar Produto e Ficha Técnica'
      }
      subtitulo="Defina o nome do produto e os insumos consumidos no seu preparo"
      aoFechar={aoFechar}
    >
      <form className="form-modal" onSubmit={aoSubmeter} noValidate>
        {erro && <div className="form-modal__erro">{erro}</div>}

        <div className="form-modal__campo">
          <label htmlFor="prod-nome" className="form-modal__label">
            Nome do Produto *
          </label>
          <input
            id="prod-nome"
            type="text"
            className="form-modal__input"
            placeholder="Ex: X-Burger Clássico"
            value={nome}
            onChange={(e) => setNome(e.target.value)}
          />
        </div>

        <div className="form-modal__secao-ficha">
          <div className="form-modal__secao-topo">
            <span className="form-modal__label">
              Ficha Técnica (Ingredientes e Quantidades) *
            </span>
            <button
              type="button"
              className="form-modal__btn-adicionar-linha"
              onClick={adicionarLinha}
            >
              + Adicionar Ingrediente
            </button>
          </div>

          {ingredientesDisponiveis.length === 0 ? (
            <div className="form-modal__erro">
              Nenhum ingrediente cadastrado no estoque! Cadastre primeiro os
              insumos na aba &quot;Estoque (Insumos)&quot;.
            </div>
          ) : (
            itensFicha.map((linha, indice) => {
              const ingDaLinha = ingredientesDisponiveis.find(
                (i) => i.id === linha.ingredienteId
              );
              const ehUnidadeInteira = ingDaLinha?.unidade === 'UN';
              const passoLinha = ehUnidadeInteira ? '1' : '0.001';

              return (
                <div key={indice} className="form-modal__linha-ficha">
                  <div className="form-modal__campo">
                    {indice === 0 && (
                      <span className="form-modal__label">Ingrediente *</span>
                    )}
                    <select
                      className="form-modal__select"
                      value={linha.ingredienteId}
                      onChange={(e) =>
                        atualizarLinha(indice, 'ingredienteId', e.target.value)
                      }
                    >
                      <option value="">Selecione um insumo...</option>
                      {ingredientesDisponiveis.map((ing) => (
                        <option key={ing.id} value={ing.id}>
                          {ing.nome} ({ing.unidade}) —{' '}
                          {formatarMoeda(ing.custoUnidade)}/{ing.unidade}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="form-modal__campo">
                    {indice === 0 && (
                      <span className="form-modal__label">Quantidade *</span>
                    )}
                    <input
                      type="number"
                      step={passoLinha}
                      min={passoLinha}
                      className="form-modal__input"
                      placeholder="Qtd"
                      value={linha.quantidade}
                      onChange={(e) =>
                        atualizarLinha(indice, 'quantidade', e.target.value)
                      }
                    />
                  </div>

                  <button
                    type="button"
                    className="form-modal__btn-remover-linha"
                    onClick={() => removerLinha(indice)}
                    disabled={itensFicha.length === 1}
                    title="Remover ingrediente da ficha técnica"
                  >
                    &times;
                  </button>
                </div>
              );
            })
          )}
        </div>

        <div className="form-modal__resumo-custo">
          <span>Custo de Produção Calculado:</span>
          <strong>{formatarMoeda(custoEstimadoTotal)}</strong>
        </div>

        <div className="form-modal__rodape">
          <Button type="button" variant="secundario" onClick={aoFechar}>
            Cancelar
          </Button>
          <Button
            type="submit"
            variant="primario"
            disabled={salvando || ingredientesDisponiveis.length === 0}
          >
            {salvando
              ? 'Salvando...'
              : modoEdicao
              ? 'Salvar Alterações'
              : 'Adicionar Produto'}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
