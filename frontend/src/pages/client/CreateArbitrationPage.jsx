import { useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import Layout from '../../components/Layout.jsx';
import ErrorMessage from '../../components/ErrorMessage.jsx';
import arbitrationService from '../../api/arbitrationService.js';
import { parseApiError } from '../../utils/format.js';

const allowedExtensions = ['pdf', 'png', 'jpg', 'jpeg', 'docx'];
const maxSize = 10 * 1024 * 1024;

function validateFile(file) {
  if (!file) return null;
  if (file.size > maxSize) return 'El archivo no puede superar 10 MB.';
  const ext = file.name.split('.').pop()?.toLowerCase();
  if (!allowedExtensions.includes(ext)) return 'Formato no permitido. Usa PDF, PNG, JPG, JPEG o DOCX.';
  return null;
}

export default function CreateArbitrationPage() {
  const { evidenceId } = useParams();
  const navigate = useNavigate();
  const [motivo, setMotivo] = useState('');
  const [file, setFile] = useState(null);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    const cleanMotivo = motivo.trim();
    if (cleanMotivo.length < 10) {
      setError('El motivo debe tener al menos 10 caracteres.');
      return;
    }
    const fileError = validateFile(file);
    if (fileError) {
      setError(fileError);
      return;
    }
    setBusy(true);
    try {
      await arbitrationService.create(evidenceId, { motivo: cleanMotivo, file });
      navigate('/client/arbitrations', { state: { created: true } });
    } catch (err) {
      setError(parseApiError(err));
    } finally {
      setBusy(false);
    }
  }

  return (
    <Layout>
      <section className="page">
        <header className="page-header">
          <div className="page-header-row">
            <div>
              <h2>Solicitar arbitraje</h2>
              <p className="page-subtitle">
                Explica por qué no estás conforme con la evidencia recibida. El pago simulado quedará retenido mientras el administrador revisa el caso.
              </p>
            </div>
            <Link to="/client/projects" className="btn btn-ghost">← Mis proyectos</Link>
          </div>
        </header>

        <form className="form-card" onSubmit={handleSubmit}>
          <label className="form-field">
            <span>Motivo del reclamo</span>
            <textarea
              value={motivo}
              onChange={(e) => setMotivo(e.target.value)}
              rows={6}
              placeholder="Describe qué parte de la entrega no cumple con lo acordado..."
            />
          </label>
          <label className="form-field">
            <span>Archivo de soporte opcional</span>
            <input type="file" accept=".pdf,.png,.jpg,.jpeg,.docx" onChange={(e) => setFile(e.target.files?.[0] || null)} />
            <small className="muted">Formatos permitidos: PDF, PNG, JPG, JPEG o DOCX. Máximo 10 MB.</small>
          </label>
          <ErrorMessage message={error} />
          <button className="btn btn-primary" disabled={busy}>{busy ? 'Enviando…' : 'Enviar arbitraje'}</button>
        </form>
      </section>
    </Layout>
  );
}
