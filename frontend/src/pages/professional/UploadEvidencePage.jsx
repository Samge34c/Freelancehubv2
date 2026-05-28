import { useCallback, useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import Layout from '../../components/Layout.jsx';
import Loading from '../../components/Loading.jsx';
import ErrorMessage from '../../components/ErrorMessage.jsx';
import FormField from '../../components/FormField.jsx';
import StatusBadge from '../../components/StatusBadge.jsx';
import evidenceService from '../../api/evidenceService.js';
import paymentService from '../../api/paymentService.js';
import {
  formatCurrency,
  labelEstadoPago,
  parseApiError,
  variantEstadoPago,
} from '../../utils/format.js';

const MAX_SIZE = 10 * 1024 * 1024;
const ALLOWED_EXTENSIONS = ['pdf', 'png', 'jpg', 'jpeg', 'docx'];

export default function UploadEvidencePage() {
  const { projectId } = useParams();
  const navigate = useNavigate();

  const [payment, setPayment] = useState(null);
  const [paymentLoading, setPaymentLoading] = useState(true);
  const [paymentError, setPaymentError] = useState(null);
  const [file, setFile] = useState(null);
  const [descripcion, setDescripcion] = useState('');
  const [errors, setErrors] = useState({});
  const [submitError, setSubmitError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const loadPayment = useCallback(() => {
    setPaymentLoading(true);
    setPaymentError(null);
    paymentService
      .getForProject(projectId)
      .then((pay) => setPayment(pay))
      .catch((err) => {
        if (err.response?.status === 404) {
          setPayment(null);
          setPaymentError('El cliente todavía no ha creado el pago simulado de este proyecto.');
        } else {
          setPaymentError(parseApiError(err));
        }
      })
      .finally(() => setPaymentLoading(false));
  }, [projectId]);

  useEffect(() => {
    loadPayment();
  }, [loadPayment]);

  const uploadBlocked = paymentLoading || !payment || payment.estado !== 'RETENIDO';

  function validate() {
    const next = {};

    if (uploadBlocked) {
      next.form = 'Solo puedes subir evidencia cuando el pago simulado está RETENIDO.';
    }

    if (!file) {
      next.file = 'Debes seleccionar un archivo.';
    } else {
      const ext = file.name.split('.').pop()?.toLowerCase();
      if (!ALLOWED_EXTENSIONS.includes(ext)) {
        next.file = 'Formato no permitido. Usa PDF, PNG, JPG, JPEG o DOCX.';
      } else if (file.size > MAX_SIZE) {
        next.file = 'El archivo no puede superar 10 MB.';
      }
    }

    const desc = descripcion.trim();
    if (!desc) next.descripcion = 'La descripción es obligatoria.';
    else if (desc.length < 10)
      next.descripcion = 'Describe la entrega con al menos 10 caracteres.';

    setErrors(next);
    return Object.keys(next).length === 0;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setSubmitError(null);
    if (!validate()) return;

    setSubmitting(true);
    try {
      await evidenceService.upload(projectId, {
        file,
        descripcion: descripcion.trim(),
      });
      navigate('/professional/evidences', {
        replace: true,
        state: { uploaded: true },
      });
    } catch (err) {
      setSubmitError(parseApiError(err));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <Layout>
      <section className="page">
        <header className="page-header">
          <div className="page-header-row">
            <div>
              <h2>Subir evidencia</h2>
              <p className="page-subtitle">
                Adjunta un documento o imagen de entrega para el proyecto contratado.
              </p>
            </div>
            <Link to="/professional/quotes" className="btn btn-ghost">
              ← Mis cotizaciones
            </Link>
          </div>
        </header>

        {paymentLoading && <Loading text="Verificando pago simulado…" />}

        {!paymentLoading && payment && (
          <article className="detail-card payment-guard-card">
            <div className="project-summary-info">
              <h3>Pago simulado del proyecto</h3>
              <p className="muted">
                Monto retenido: <strong>{formatCurrency(payment.monto)}</strong> · Referencia:{' '}
                {payment.referencia || '—'}
              </p>
              <p className="detail-desc">
                Este pago es simulado. Sirve para demostrar que el valor queda retenido hasta que
                el cliente apruebe la evidencia de entrega.
              </p>
            </div>
            <StatusBadge
              label={labelEstadoPago(payment.estado)}
              variant={variantEstadoPago(payment.estado)}
            />
          </article>
        )}

        {!paymentLoading && paymentError && (
          <div className="alert alert-warning" role="status">
            {paymentError}
          </div>
        )}

        {!paymentLoading && payment?.estado === 'LIBERADO' && (
          <div className="alert alert-success" role="status">
            ✓ El pago ya fue liberado. No es necesario subir nuevas evidencias para este proyecto.
          </div>
        )}

        {!paymentLoading && payment?.estado === 'RETENIDO' && (
          <div className="alert alert-info" role="status">
            El pago está retenido. Puedes subir la evidencia para que el cliente la revise y luego
            libere el pago simulado.
          </div>
        )}

        <form className="form-card" onSubmit={handleSubmit} noValidate>
          <h3 className="form-card-title">Documento de entrega</h3>

          {errors.form && <div className="alert alert-error">{errors.form}</div>}

          <label className="field" htmlFor="file">
            <span className="field-label">Archivo</span>
            <input
              id="file"
              name="file"
              type="file"
              accept=".pdf,.png,.jpg,.jpeg,.docx"
              disabled={submitting || uploadBlocked}
              onChange={(e) => {
                setFile(e.target.files?.[0] || null);
                setErrors((p) => ({ ...p, file: undefined }));
              }}
            />
            {file && <span className="field-help">Seleccionado: {file.name}</span>}
            {errors.file && <span className="field-error">{errors.file}</span>}
          </label>

          <FormField
            name="descripcion"
            label="Descripción de la evidencia"
            as="textarea"
            rows={5}
            value={descripcion}
            onChange={(e) => {
              setDescripcion(e.target.value);
              setErrors((p) => ({ ...p, descripcion: undefined }));
            }}
            placeholder="Ejemplo: Se adjunta prototipo final, capturas y documento de entrega..."
            disabled={submitting || uploadBlocked}
            error={errors.descripcion}
            required
          />

          <ErrorMessage message={submitError} />

          <div className="form-actions">
            <button type="submit" className="btn btn-primary" disabled={submitting || uploadBlocked}>
              {submitting ? <Loading variant="inline" text="Subiendo…" /> : 'Subir evidencia'}
            </button>
            <Link to="/professional/quotes" className="btn btn-ghost">
              Cancelar
            </Link>
          </div>
        </form>
      </section>
    </Layout>
  );
}
