import './InputField.css';

export function InputField({
  id,
  label,
  type = 'text',
  placeholder,
  value,
  onChange,
  autoComplete,
}) {
  return (
    <div className="ui-input-group">
      {label && (
        <label htmlFor={id} className="ui-input-label">
          {label}
        </label>
      )}
      <input
        id={id}
        type={type}
        className="ui-input-control"
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        autoComplete={autoComplete}
      />
    </div>
  );
}
