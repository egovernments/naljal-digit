import React from "react";
import ReactDOM from "react-dom";

import { initLibraries } from "@egovernments/digit-ui-libraries";
import { DigitUI } from "@egovernments/digit-ui-module-core";
import { initDSSComponents } from "@egovernments/digit-ui-module-dss";
import { initEngagementComponents } from "@egovernments/digit-ui-module-engagement";
import { initHRMSComponents } from "@egovernments/digit-ui-module-hrms";
import { initPGRComponents, PGRReducers } from "@egovernments/digit-ui-module-pgr";
import "@egovernments/digit-ui-css/example/index.css";

import { pgrCustomizations } from "./pgr";
import { UICustomizations } from "./UICustomizations";

var Digit = window.Digit || {};

const enabledModules = [
  "DSS",
  "HRMS",
  "PGR",
  //  "Engagement", "NDSS","QuickPayLinks", "Payment",
  // "Utilities",
  //added to check fsm
  // "FSM"
];

const initTokens = (stateCode) => {
  const userType = window.sessionStorage.getItem("userType") || process.env.REACT_APP_USER_TYPE || "CITIZEN";
  const token = window.localStorage.getItem("token") || process.env[`REACT_APP_${userType}_TOKEN`];

  const citizenInfo = window.localStorage.getItem("Citizen.user-info");

  const citizenTenantId = window.localStorage.getItem("Citizen.tenant-id") || stateCode;

  const employeeInfo = window.localStorage.getItem("Employee.user-info");
  const employeeTenantId = window.localStorage.getItem("Employee.tenant-id");

  const userTypeInfo = userType === "CITIZEN" || userType === "QACT" ? "citizen" : "employee";
  window.Digit.SessionStorage.set("user_type", userTypeInfo);
  window.Digit.SessionStorage.set("userType", userTypeInfo);

  if (userType !== "CITIZEN") {
    window.Digit.SessionStorage.set("User", { access_token: token, info: userType !== "CITIZEN" ? JSON.parse(employeeInfo) : citizenInfo });
  } else {
    // if (!window.Digit.SessionStorage.get("User")?.extraRoleInfo) window.Digit.SessionStorage.set("User", { access_token: token, info: citizenInfo });
  }

  window.Digit.SessionStorage.set("Citizen.tenantId", citizenTenantId);

  if (employeeTenantId && employeeTenantId.length) window.Digit.SessionStorage.set("Employee.tenantId", employeeTenantId);
};

const initDigitUI = () => {
  window.contextPath = window?.globalConfigs?.getConfig("CONTEXT_PATH") || "mgramseva-web";

  console.log("CONTEXTPATH",window.contextPath);
  console.log("CONTEXTPATH getConfig",window?.globalConfigs?.getConfig("CONTEXT_PATH"));
  

  // window?.Digit.ComponentRegistryService.setupRegistry({
  //   PaymentModule,
  //   ...paymentConfigs,
  //   PaymentLinks,
  // });

  initDSSComponents();
  initHRMSComponents();
  initEngagementComponents();
  initPGRComponents();

  const moduleReducers = (initData) => ({
    pgr: PGRReducers(initData),
  });

  window.Digit.Customizations = {
    PGR: pgrCustomizations,
    commonUiConfig: UICustomizations,
  };

  const stateCode = window?.globalConfigs?.getConfig("STATE_LEVEL_TENANT_ID") || "ka";
  initTokens(stateCode);

  ReactDOM.render(<DigitUI stateCode={stateCode} enabledModules={enabledModules} moduleReducers={moduleReducers} />, document.getElementById("root"));
};

initLibraries().then(() => {
  initDigitUI();
});
