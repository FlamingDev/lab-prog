import { useEffect, useState } from 'react';
import { Button, Modal } from '@/components/ui';
import '@/modules/estoque/components/ModalEstoque.css';
import './ModalCategoria.css';

export function ModalCategoria({
  aberto,
  categoriaEmEdicao,
  aoFechar,
  aoSalvar,
}) {
  const modoEdicao = Boolean(categoriaEmEdicao);

  const [nome, setNome] = useState('');
  const [descricao, setDescricao] = useState('');
  const [erro, setErro] = useState('');
  const [salvando, setSalvando] = useState(false);

  useEffect(() => {
    if (categoriaEmEdicao) {
      setNome(categoriaEmEdicao.nome || '');
      setDescricao(categoriaEmEdicao.descricao || '');
    } else {
      setNome('');
      setDescricao('');
    }
    setErro('');
  }, [categoriaEmEdicao, aberto]);

  async function aoSubmeter(evento) {
    evento.preventDefault();
    setErro('');

    if (!nome.trim()) {
      setErro('Informe o nome da categoria.');
      return;
    }

    if (nome.trim().length > 80) {
      setErro('O nome da categoria deve ter no máximo 80 caracteres.');
      return;
    }

    try {
      setSalvando(true);
      await aoSalvar({
        nome: nome.trim(),
        descricao: descricao.trim(),
      });
      aoFechar();
    } catch (err) {
      setErro(err.message || 'Erro ao salvar categoria.');
    } finally {
      setSalvando(false);
    }
  }

  return (
    <Modal
      aberto={aberto}
      titulo={modoEdicao ? 'Editar Categoria' : 'Adicionar Categoria'}
      subtitulo={
        modoEdicao
          ? 'Atualize os detalhes da categoria'
          : 'Cadastre uma nova categoria para utilizar nos cardápios'
      }
      aoFechar={aoFechar}
    >
      <form className="form-modal" onSubmit={aoSubmeter} noValidate>
        {erro && <div className="form-modal__erro">{erro}</div>}

        <div className="form-modal__campo">
          <label htmlFor="cat-nome" className="form-modal__label">
            Nome da Categoria *
          </label>
          <input
            id="cat-nome"
            type="text"
            maxLength={80}
            className="form-modal__input"
            placeholder="Ex: Hambúrgueres"
            value={nome}
            onChange={(e) => setNome(e.target.value)}
            autoFocus
          />
        </div>

        <div className="form-modal__campo">
          <label htmlFor="cat-descricao" className="form-modal__label">
            Descrição
          </label>
          <textarea
            id="cat-descricao"
            rows={3}
            className="form-modal__input form-modal__textarea"
            placeholder="Ex: Hambúrgueres artesanais e sanduíches"
            value={descricao}
            onChange={(e) => setDescricao(e.target.value)}
          />
        </div>

        <div className="form-modal__rodape">
          <Button type="button" variant="secundario" onClick={aoFechar}>
            Cancelar
          </Button>
          <Button type="submit" variant="primario" disabled={salvando}>
            {salvando
              ? 'Salvando...'
              : modoEdicao
              ? 'Atualizar Categoria'
              : 'Adicionar Categoria'}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
