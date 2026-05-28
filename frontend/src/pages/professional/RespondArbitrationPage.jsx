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

export default function RespondArbitrationPage() {
  const { arbitrationId } = useParams();
  const navigate = useNavigate();
  const [respuesta, setRespuesta] = useState('');
  const [file, setFile] = useState(null);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    const clean = respuesta.trim();
    if (clean.length < 10) {
      setError('La respuesta debe tener al menos 10 caracteres.');
      return;
    }
    const fileError = validateFile(file);
    if (fileError) {
      setError(fileError);
      return;
    }
    setBusy(true);
    try {
      await arbitrationService.respond(arbitrationId, { respuesta: clean, file });
      navigate('/professional/arbitrations');
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
              <h2>Responder arbitraje</h2>
              <p className="page-subtitle">Explica tu versión de la entrega y adjunta un soporte si lo consideras necesario.</p>
            </div>
            <Link to="/professional/arbitrations" className="btn btn-ghost">← Arbitrajes</Link>
          </div>
        </header>
        <form className="form-card" onSubmit={handleSubmit}>
          <label className="form-field">
            <span>Respuesta del profesional</span>
            <textarea value={respuesta} onChange={(e) => setRespuesta(e.target.value)} rows={6} placeholder="Explica por qué la entrega cumple o qué corrección realizarás..." />
          </label>
          <label className="form-field">
            <span>Archivo de soporte opcional</span>
            <input type="file" accept=".pdf,.png,.jpg,.jpeg,.docx" onChange={(e) => setFile(e.target.files?.[0] || null)} />
          </label>
          <ErrorMessage message={error} />
          <button className="btn btn-primary" disabled={busy}>{busy ? 'Enviando…' : 'Enviar respuesta'}</button>
        </form>
      </section>
    </Layout>
  );
}
