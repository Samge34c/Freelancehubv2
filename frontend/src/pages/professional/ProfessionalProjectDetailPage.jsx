import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import Layout from '../../components/Layout.jsx';
import Loading from '../../components/Loading.jsx';
import ErrorMessage from '../../components/ErrorMessage.jsx';
import StatusBadge from '../../components/StatusBadge.jsx';
import FormField from '../../components/FormField.jsx';
import AttachmentList from '../../components/AttachmentList.jsx';
import projectService from '../../api/projectService.js';
import quoteService from '../../api/quoteService.js';
import {
  formatCurrency,
  formatDateTime,
  formatDays,
  labelEstadoProyecto,
  parseApiError,
  variantEstadoProyecto,
} from '../../utils/format.js';

const INITIAL = { precio: '', plazo: '', descripcion: '' };

export default function ProfessionalProjectDetailPage() {
  const { projectId } = useParams();
  const navigate = useNavigate();

  const [project, setProject] = useState(null);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState(null);

  const [form, setForm] = useState(INITIAL);
  const [formErrors, setFormErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState(null);

  useEffect(() => {
    let alive = true;
    setLoading(true);
    setLoadError(null);
    projectService
      .getById(projectId)
      .then((p) => alive && setProject(p))
      .catch((err) => alive && setLoadError(parseApiError(err)))
      .finally(() => alive && setLoading(false));
    return () => {
      alive = false;
    };
  }, [projectId]);

  function update(field) {
    return (e) => {
      const value = e.target.value;
      setForm((prev) => ({ ...prev, [field]: value }));
      setFormErrors((prev) => ({ ...prev, [field]: undefined }));
    };
  }

  function validate() {
    const errs = {};
    const precio = Number(form.precio);
    if (!form.precio || Number.isNaN(precio) || precio <= 0)
      errs.precio = 'El precio debe ser mayor a cero.';
    const plazo = Number(form.plazo);
    if (!form.plazo || !Number.isInteger(plazo) || plazo <= 0)
      errs.plazo = 'El plazo debe ser un entero positivo (días).';
    if (!form.descripcion.trim())
      errs.descripcion = 'La descripción es obligatoria.';
    else if (form.descripcion.trim().length < 10)
      errs.descripcion = 'Detalla tu propuesta con al menos 10 caracteres.';

    setFormErrors(errs);
    return Object.keys(errs).length === 0;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setSubmitError(null);
    if (!validate()) return;

    setSubmitting(true);
    try {
      const payload = {
        precio: Number(form.precio),
        plazo: Number(form.plazo),
        descripcion: form.descripcion.trim(),
      };
      await quoteService.create(projectId, payload);
      navigate('/professional/quotes', {
        state: { created: true },
        replace: true,
      });
    } catch (err) {
      setSubmitError(parseApiError(err));
    } finally {
      setSubmitting(false);
    }
  }

  const isOpen = project?.estado === 'ABIERTO';

  return (
    <Layout>
      <section className="page">
        <header className="page-header">
          <div className="page-header-row">
            <div>
              <h2>Detalle del proyecto</h2>
              <p className="page-subtitle">
                Revisa el proyecto y envía una cotización si te interesa.
              </p>
            </div>
            <Link to="/professional/projects/open" className="btn btn-ghost">
              ← Proyectos abiertos
            </Link>
          </div>
        </header>

        {loading && <Loading text="Cargando proyecto…" />}
        <ErrorMessage message={loadError} />

        {!loading && !loadError && project && (
          <>
            <article className="detail-card">
              <header className="detail-head">
                <h3>{project.titulo}</h3>
                <StatusBadge
                  label={labelEstadoProyecto(project.estado)}
                  variant={variantEstadoProyecto(project.estado)}
                />
              </header>

              <p className="detail-desc">{project.descripcion}</p>

              <dl className="detail-meta">
                <div>
                  <dt>Presupuesto</dt>
                  <dd>{formatCurrency(project.presupuesto)}</dd>
                </div>
                <div>
                  <dt>Plazo</dt>
                  <dd>{formatDays(project.plazo)}</dd>
                </div>
                <div>
                  <dt>Categoría</dt>
                  <dd>{project.categoriaNombre || '—'}</dd>
                </div>
                <div>
                  <dt>Cliente</dt>
                  <dd>{project.clienteNombre || '—'}</dd>
                </div>
                <div>
                  <dt>Publicado</dt>
                  <dd>{formatDateTime(project.fechaCreacion)}</dd>
                </div>
              </dl>
            </article>

            <AttachmentList
              projectId={project.id}
              title="Archivos de requisitos del cliente"
            />

            {!isOpen && (
              <div className="alert alert-info" role="status">
                Este proyecto ya no acepta nuevas cotizaciones (estado:{' '}
                {labelEstadoProyecto(project.estado)}).
              </div>
            )}

            {isOpen && (
              <form className="form-card" onSubmit={handleSubmit} noValidate>
                <h3 className="form-card-title">Enviar cotización</h3>

                <div className="form-row">
                  <FormField
                    name="precio"
                    label="Precio propuesto (COP)"
                    type="number"
                    min="1"
                    step="1000"
                    value={form.precio}
                    onChange={update('precio')}
                    placeholder="1500000"
                    disabled={submitting}
                    error={formErrors.precio}
                    required
                  />
                  <FormField
                    name="plazo"
                    label="Plazo estimado (días)"
                    type="number"
                    min="1"
                    step="1"
                    value={form.plazo}
                    onChange={update('plazo')}
                    placeholder="20"
                    disabled={submitting}
                    error={formErrors.plazo}
                    required
                  />
                </div>

                <FormField
                  name="descripcion"
                  label="Descripción / propuesta"
                  as="textarea"
                  rows={4}
                  value={form.descripcion}
                  onChange={update('descripcion')}
                  placeholder="Cuenta cómo abordarías el proyecto, entregables, etc."
                  disabled={submitting}
                  error={formErrors.descripcion}
                  required
                />

                <ErrorMessage message={submitError} />

                <div className="form-actions">
                  <button
                    type="submit"
                    className="btn btn-primary"
                    disabled={submitting}
                  >
                    {submitting ? (
                      <Loading variant="inline" text="Enviando…" />
                    ) : (
                      'Enviar cotización'
                    )}
                  </button>
                  <Link to="/professional/projects/open" className="btn btn-ghost">
                    Cancelar
                  </Link>
                </div>
              </form>
            )}
          </>
        )}
      </section>
    </Layout>
  );
}
