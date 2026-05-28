import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext.jsx';
import PrivateRoute from './routes/PrivateRoute.jsx';
import RoleRoute from './routes/RoleRoute.jsx';

import LoginPage from './pages/LoginPage.jsx';
import RegisterClientPage from './pages/register/RegisterClientPage.jsx';
import RegisterProfessionalPage from './pages/register/RegisterProfessionalPage.jsx';
import NotFoundPage from './pages/NotFoundPage.jsx';

import ClientDashboard from './pages/ClientDashboard.jsx';
import CreateProjectPage from './pages/client/CreateProjectPage.jsx';
import MyProjectsPage from './pages/client/MyProjectsPage.jsx';
import ProjectDetailPage from './pages/client/ProjectDetailPage.jsx';
import ProjectQuotesPage from './pages/client/ProjectQuotesPage.jsx';
import ReceivedQuotesPage from './pages/client/ReceivedQuotesPage.jsx';
import ProjectEvidencesPage from './pages/client/ProjectEvidencesPage.jsx';
import ProjectPaymentPage from './pages/client/ProjectPaymentPage.jsx';
import MyPaymentsPage from './pages/client/MyPaymentsPage.jsx';
import ClientArbitrationsPage from './pages/client/ClientArbitrationsPage.jsx';
import CreateArbitrationPage from './pages/client/CreateArbitrationPage.jsx';

import ProfessionalDashboard from './pages/ProfessionalDashboard.jsx';
import OpenProjectsPage from './pages/professional/OpenProjectsPage.jsx';
import ProfessionalProjectDetailPage from './pages/professional/ProfessionalProjectDetailPage.jsx';
import MyQuotesPage from './pages/professional/MyQuotesPage.jsx';
import MyEvidencesPage from './pages/professional/MyEvidencesPage.jsx';
import UploadEvidencePage from './pages/professional/UploadEvidencePage.jsx';
import ProfessionalPaymentsPage from './pages/professional/ProfessionalPaymentsPage.jsx';
import ProfessionalArbitrationsPage from './pages/professional/ProfessionalArbitrationsPage.jsx';
import RespondArbitrationPage from './pages/professional/RespondArbitrationPage.jsx';

import AdminDashboard from './pages/AdminDashboard.jsx';
import AdminArbitrationsPage from './pages/admin/AdminArbitrationsPage.jsx';
import AdminArbitrationDetailPage from './pages/admin/AdminArbitrationDetailPage.jsx';

/**
 * Árbol de rutas.
 *
 * Convenciones:
 *  - PrivateRoute  ⇒ requiere sesión.
 *  - RoleRoute     ⇒ requiere rol específico.
 *  - "/"           ⇒ redirige a /login.
 *  - "*"           ⇒ 404.
 *
 * El acceso a /client/* solo lo tiene CLIENTE.
 * El acceso a /professional/* solo lo tiene PROFESIONAL.
 * El acceso a /admin solo lo tiene ADMIN.
 */
export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Navigate to="/login" replace />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register/client" element={<RegisterClientPage />} />
          <Route path="/register/professional" element={<RegisterProfessionalPage />} />

          {/* CLIENTE */}
          <Route
            path="/client"
            element={
              <PrivateRoute>
                <RoleRoute allow={['CLIENTE']}>
                  <ClientDashboard />
                </RoleRoute>
              </PrivateRoute>
            }
          />
          <Route
            path="/client/projects/new"
            element={
              <PrivateRoute>
                <RoleRoute allow={['CLIENTE']}>
                  <CreateProjectPage />
                </RoleRoute>
              </PrivateRoute>
            }
          />
          <Route
            path="/client/projects"
            element={
              <PrivateRoute>
                <RoleRoute allow={['CLIENTE']}>
                  <MyProjectsPage />
                </RoleRoute>
              </PrivateRoute>
            }
          />
          <Route
            path="/client/projects/:projectId"
            element={
              <PrivateRoute>
                <RoleRoute allow={['CLIENTE']}>
                  <ProjectDetailPage />
                </RoleRoute>
              </PrivateRoute>
            }
          />
          <Route
            path="/client/projects/:projectId/quotes"
            element={
              <PrivateRoute>
                <RoleRoute allow={['CLIENTE']}>
                  <ProjectQuotesPage />
                </RoleRoute>
              </PrivateRoute>
            }
          />

          <Route
            path="/client/quotes-received"
            element={
              <PrivateRoute>
                <RoleRoute allow={['CLIENTE']}>
                  <ReceivedQuotesPage />
                </RoleRoute>
              </PrivateRoute>
            }
          />

          <Route
            path="/client/projects/:projectId/evidences"
            element={
              <PrivateRoute>
                <RoleRoute allow={['CLIENTE']}>
                  <ProjectEvidencesPage />
                </RoleRoute>
              </PrivateRoute>
            }
          />

          <Route
            path="/client/projects/:projectId/payment"
            element={
              <PrivateRoute>
                <RoleRoute allow={['CLIENTE']}>
                  <ProjectPaymentPage />
                </RoleRoute>
              </PrivateRoute>
            }
          />
          <Route
            path="/client/payments"
            element={
              <PrivateRoute>
                <RoleRoute allow={['CLIENTE']}>
                  <MyPaymentsPage />
                </RoleRoute>
              </PrivateRoute>
            }
          />


          <Route
            path="/client/arbitrations"
            element={
              <PrivateRoute>
                <RoleRoute allow={['CLIENTE']}>
                  <ClientArbitrationsPage />
                </RoleRoute>
              </PrivateRoute>
            }
          />
          <Route
            path="/client/evidences/:evidenceId/arbitration/new"
            element={
              <PrivateRoute>
                <RoleRoute allow={['CLIENTE']}>
                  <CreateArbitrationPage />
                </RoleRoute>
              </PrivateRoute>
            }
          />

          {/* PROFESIONAL */}
          <Route
            path="/professional"
            element={
              <PrivateRoute>
                <RoleRoute allow={['PROFESIONAL']}>
                  <ProfessionalDashboard />
                </RoleRoute>
              </PrivateRoute>
            }
          />
          <Route
            path="/professional/projects/open"
            element={
              <PrivateRoute>
                <RoleRoute allow={['PROFESIONAL']}>
                  <OpenProjectsPage />
                </RoleRoute>
              </PrivateRoute>
            }
          />
          <Route
            path="/professional/projects/:projectId"
            element={
              <PrivateRoute>
                <RoleRoute allow={['PROFESIONAL']}>
                  <ProfessionalProjectDetailPage />
                </RoleRoute>
              </PrivateRoute>
            }
          />
          <Route
            path="/professional/quotes"
            element={
              <PrivateRoute>
                <RoleRoute allow={['PROFESIONAL']}>
                  <MyQuotesPage />
                </RoleRoute>
              </PrivateRoute>
            }
          />

          <Route
            path="/professional/evidences"
            element={
              <PrivateRoute>
                <RoleRoute allow={['PROFESIONAL']}>
                  <MyEvidencesPage />
                </RoleRoute>
              </PrivateRoute>
            }
          />
          <Route
            path="/professional/projects/:projectId/evidences/new"
            element={
              <PrivateRoute>
                <RoleRoute allow={['PROFESIONAL']}>
                  <UploadEvidencePage />
                </RoleRoute>
              </PrivateRoute>
            }
          />

          <Route
            path="/professional/payments"
            element={
              <PrivateRoute>
                <RoleRoute allow={['PROFESIONAL']}>
                  <ProfessionalPaymentsPage />
                </RoleRoute>
              </PrivateRoute>
            }
          />


          <Route
            path="/professional/arbitrations"
            element={
              <PrivateRoute>
                <RoleRoute allow={['PROFESIONAL']}>
                  <ProfessionalArbitrationsPage />
                </RoleRoute>
              </PrivateRoute>
            }
          />
          <Route
            path="/professional/arbitrations/:arbitrationId/respond"
            element={
              <PrivateRoute>
                <RoleRoute allow={['PROFESIONAL']}>
                  <RespondArbitrationPage />
                </RoleRoute>
              </PrivateRoute>
            }
          />

          {/* ADMIN */}
          <Route
            path="/admin"
            element={
              <PrivateRoute>
                <RoleRoute allow={['ADMIN']}>
                  <AdminDashboard />
                </RoleRoute>
              </PrivateRoute>
            }
          />


          <Route
            path="/admin/arbitrations"
            element={
              <PrivateRoute>
                <RoleRoute allow={['ADMIN']}>
                  <AdminArbitrationsPage />
                </RoleRoute>
              </PrivateRoute>
            }
          />
          <Route
            path="/admin/arbitrations/:id"
            element={
              <PrivateRoute>
                <RoleRoute allow={['ADMIN']}>
                  <AdminArbitrationDetailPage />
                </RoleRoute>
              </PrivateRoute>
            }
          />

          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
