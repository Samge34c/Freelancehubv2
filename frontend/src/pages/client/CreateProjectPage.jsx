import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Layout from '../../components/Layout.jsx';
import FormField from '../../components/FormField.jsx';
import Loading from '../../components/Loading.jsx';
import ErrorMessage from '../../components/ErrorMessage.jsx';
import projectService from '../../api/projectService.js';
import categoryService from '../../api/categoryService.js';
import attachmentService from '../../api/attachmentService.js';
import { parseApiError } from '../../utils/format.js';

const INITIAL = {
  categoriaId: '',
  titulo: '',
  descripcion: '',
  presupuesto: '',
  plazo: '',
};

const MAX_ATTACHMENT_SIZE = 10 * 1024 * 1024;
const ALLOWED_EXTENSIONS = ['pdf', 'png', 'jpg', 'jpeg', 'docx'];

function extensionOf(file) {
  const name = file?.name || '';
  const idx = name.lastIndexOf('.');
  return idx >= 0 ? name.slice(idx + 1).toLowerCase() : '';
}

export default function CreateProjectPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState(INITIAL);
  const [attachment, setAttachment] = useState(null);
  const [categories, setCategories] = useState([]);
  const [loadingCats, setLoadingCats] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [formErrors, setFormErrors] = useState({});
  const [apiError, setApiError] = useState(null);

  useEffect(() => {
    let alive = true;
    setLoadingCats(true);
    categoryService
      .listAll()
      .then((list) => {
        if (!alive) return;
        const active = Array.isArray(list)
          ? list.filter((c) => c.activo !== false)
          : [];
        setCategories(active);
      })
      .catch((err) => {
        if (!alive) return;
        setApiError(parseApiError(err));
      })
      .finally(() => {
        if (alive) setLoadingCats(false);
      });
    return () => {
      alive = false;
    };
  }, []);

  function update(field) {
    return (e) => {
      const value = e.target.value;
      setForm((prev) => ({ ...prev, [field]: value }));
      setFormErrors((prev) => ({ ...prev, [field]: undefined }));
    };
  }

  function handleAttachmentChange(e) {
    const file = e.target.files?.[0] || null;
    setAttachment(file);
    setFormErrors((prev) => ({ ...prev, attachment: undefined }));
  }

  function validate() {
    const errs = {};
    if (!form.categoriaId) errs.categoriaId = 'Selecciona una categoría.';
    if (!form.titulo.trim()) errs.titulo = 'El título es obligatorio.';
    else if (form.titulo.trim().length < 5)
      errs.titulo = 'El título debe tener al menos 5 caracteres.';
    if (!form.descripcion.trim())
      errs.descripcion = 'La descripción es obligatoria.';
    else if (form.descripcion.trim().length < 20)
      errs.descripcion = 'Describe el proyecto con al menos 20 caracteres.';

    const pres = Number(form.presupuesto);
    if (!form.presupuesto || Number.isNaN(pres) || pres <= 0)
      errs.presupuesto = 'El presupuesto debe ser mayor a cero.';

    const plazo = Number(form.plazo);
    if (!form.plazo || !Number.isInteger(plazo) || plazo <= 0)
      errs.plazo = 'El plazo debe ser un número entero positivo (días).';

    if (attachment) {
      const ext = extensionOf(attachment);
      if (!ALLOWED_EXTENSIONS.includes(ext)) {
        errs.attachment = 'Formato no permitido. Usa PDF, PNG, JPG, JPEG o DOCX.';
      } else if (attachment.size > MAX_ATTACHMENT_SIZE) {
        errs.attachment = 'El archivo no puede superar 10 MB.';
      }
    }

    setFormErrors(errs);
    return Object.keys(errs).length === 0;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setApiError(null);
    if (!validate()) return;

    setSubmitting(true);
    try {
      const payload = {
        categoriaId: Number(form.categoriaId),
        titulo: form.titulo.trim(),
        descripcion: form.descripcion.trim(),
        presupuesto: Number(form.presupuesto),
        plazo: Number(form.plazo),
      };
      const created = await projectService.create(payload);

      if (attachment) {
        try {
          await attachmentService.upload(created.id, attachment);
          navigate('/client/projects', {
            state: { created: true, attachmentUploaded: true },
            replace: true,
          });
          return;
        } catch (err) {
          navigate('/client/projects', {
            state: {
              created: true,
              attachmentWarning:
                'El proyecto fue creado, pero no se pudo subir el archivo adjunto: ' +
                parseApiError(err),
            },
            replace: true,
          });
          return;
        }
      }

      navigate('/client/projects', { state: { created: true }, replace: true });
    } catch (err) {
      setApiError(parseApiError(err));
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
              <h2>Crear proyecto</h2>
              <p className="page-subtitle">
                Publica un proyecto para que los profesionales coticen.
              </p>
            </div>
            <Link to="/client" className="btn btn-ghost">
              ← Volver
            </Link>
          </div>
        </header>

        <form className="form-card" onSubmit={handleSubmit} noValidate>
          <FormField
            name="titulo"
            label="Título"
            type="text"
            value={form.titulo}
            onChange={update('titulo')}
            placeholder="Ej. Desarrollo de landing page corporativa"
            maxLength={200}
            disabled={submitting}
            error={formErrors.titulo}
            required
          />

          <FormField
            name="descripcion"
            label="Descripción"
            as="textarea"
            rows={5}
            value={form.descripcion}
            onChange={update('descripcion')}
            placeholder="Cuenta a detalle qué necesitas, alcances, entregables, etc."
            disabled={submitting}
            error={formErrors.descripcion}
            required
          />

          <div className="form-row">
            <FormField
              name="presupuesto"
              label="Presupuesto (COP)"
              type="number"
              min="1"
              step="1000"
              value={form.presupuesto}
              onChange={update('presupuesto')}
              placeholder="2000000"
              disabled={submitting}
              error={formErrors.presupuesto}
              required
            />

            <FormField
              name="plazo"
              label="Plazo (días)"
              type="number"
              min="1"
              step="1"
              value={form.plazo}
              onChange={update('plazo')}
              placeholder="30"
              disabled={submitting}
              error={formErrors.plazo}
              required
            />
          </div>

          <FormField
            name="categoriaId"
            label="Categoría"
            as="select"
            value={form.categoriaId}
            onChange={update('categoriaId')}
            disabled={submitting || loadingCats}
            error={formErrors.categoriaId}
            required
          >
            <option value="">
              {loadingCats ? 'Cargando categorías…' : 'Selecciona una categoría'}
            </option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>
                {c.nombre}
              </option>
            ))}
          </FormField>

          <label className="field" htmlFor="projectAttachment">
            <span className="field-label">Archivo de instrucciones o referencias</span>
            <input
              id="projectAttachment"
              name="projectAttachment"
              type="file"
              accept=".pdf,.png,.jpg,.jpeg,.docx"
              onChange={handleAttachmentChange}
              disabled={submitting}
            />
            <span className="field-help">
              Opcional. Puedes adjuntar PDF, Word o imagen con requisitos del proyecto. Máximo 10 MB.
            </span>
            {formErrors.attachment && <span className="field-error">{formErrors.attachment}</span>}
          </label>

          <ErrorMessage message={apiError} />

          <div className="form-actions">
            <button
              type="submit"
              className="btn btn-primary"
              disabled={submitting || loadingCats}
            >
              {submitting ? <Loading variant="inline" text="Creando…" /> : 'Crear proyecto'}
            </button>
            <Link to="/client/projects" className="btn btn-ghost">
              Cancelar
            </Link>
          </div>
        </form>
      </section>
    </Layout>
  );
}
