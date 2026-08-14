import { Navigate, type RouteObject } from "react-router-dom";
import NotFoundPage from "@/pages/errors/not-found/page";
import DashboardPage from "@/pages/dashboard/page";
import LoginPage from "@/pages/auth/login/page";
import ClaimCreatePage from "@/pages/claims/create/page";
import ClaimListPage from "@/pages/claims/list/page";
import AdminAccountsPage from "@/pages/admin/accounts/page";
import AdminClaimsPage from "@/pages/admin/claims/page";

const routes: RouteObject[] = [
  {
    path: "/",
    element: <Navigate to="/login" replace />,
  },
  {
    path: "/login",
    element: <LoginPage />,
  },
  {
    path: "/dashboard",
    element: <DashboardPage />,
  },
  {
    path: "/claims/new",
    element: <ClaimCreatePage />,
  },
  {
    path: "/claims",
    element: <ClaimListPage />,
  },
  {
    path: "/admin/accounts",
    element: <AdminAccountsPage />,
  },
  {
    path: "/admin/claims",
    element: <AdminClaimsPage />,
  },
  {
    path: "*",
    element: <NotFoundPage />,
  },
];

export default routes;
