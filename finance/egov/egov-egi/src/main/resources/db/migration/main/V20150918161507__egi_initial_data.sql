-----------------START--------------------
INSERT INTO eg_module (id, name, enabled, contextroot, parentmodule, displayname, ordernumber) VALUES (1, 'Administration', true, 'egi', NULL, 'Administration', 1);
INSERT INTO eg_module (id, name, enabled, contextroot, parentmodule, displayname, ordernumber) VALUES (2, 'Boundary Module', true, NULL, 1, 'Jurisdiction', 1);
INSERT INTO eg_module (id, name, enabled, contextroot, parentmodule, displayname, ordernumber) VALUES (3, 'Department', true, NULL, 1, 'Department', 4);
INSERT INTO eg_module (id, name, enabled, contextroot, parentmodule, displayname, ordernumber) VALUES (5, 'User Module', true, NULL, 1, 'User Management', 2);
INSERT INTO eg_module (id, name, enabled, contextroot, parentmodule, displayname, ordernumber) VALUES (115, 'Configuration', true, NULL, 1, 'Configuration', 3);
INSERT INTO eg_module (id, name, enabled, contextroot, parentmodule, displayname, ordernumber) VALUES (237, 'EGI-COMMON', false, 'egi', 1, 'EgiPortal', NULL);
INSERT INTO eg_module (id, name, enabled, contextroot, parentmodule, displayname, ordernumber) VALUES (379, 'Boundary Type', true, NULL, 2, 'Boundary Type', 2);
INSERT INTO eg_module (id, name, enabled, contextroot, parentmodule, displayname, ordernumber) VALUES (380, 'Hierarchy Type', true, NULL, 2, 'Hierarchy Type', 1);
INSERT INTO eg_module (id, name, enabled, contextroot, parentmodule, displayname, ordernumber) VALUES (381, 'Boundary', true, NULL, 2, 'Boundary', 3);
INSERT INTO eg_module (id, name, enabled, contextroot, parentmodule, displayname, ordernumber) VALUES (411, 'EGI-SETUP', true, NULL, 1, 'Setups', 5);
------------------END---------------------

-----------------START--------------------
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (1049, 'ajaxuserlist', '/userRole/ajax/userlist', NULL, 237, NULL, 'ajaxuserlist', false, 'egi', 0, 1, '2015-08-28 10:43:42.414686', 1, '2015-08-28 10:43:42.414686', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (33, 'CreateBoundaryTypeForm', '/boundarytype/create', NULL, 379, 0, 'Create Boundary Type', true, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (46, 'CreateBoundary', '/boundary/create', NULL, 2, 0, 'Create Boundary', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (54, 'ViewBoundary', '/view-boundary', NULL, 2, 0, 'ViewBoundary', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (55, 'UpdateBoundary', '/update-boundary', NULL, 2, 0, 'UpdateBoundary', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (34, 'ViewBoundaryTypeForm', '/boundarytype/view', NULL, 379, 0, 'View Boundary Type', true, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (35, 'UpdateBoundaryTypeForm', '/boundarytype/update', NULL, 379, 0, 'Update Boundary Type', true, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (39, 'AddChildBoundaryType', '/boundarytype/addchild', NULL, 379, 0, 'Add Child Boundary Type', true, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (30, 'CreateHierarchyTypeForm', '/hierarchytype/create', NULL, 380, 0, 'Create Hierarchy Type', true, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (32, 'UpdateHierarchyTypeForm', '/hierarchytype/update', NULL, 380, 0, 'Update Hierarchy Type', true, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (31, 'ViewHierarchyTypeForm', '/hierarchytype/view', NULL, 380, 0, 'View Hierarchy Type', true, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (44, 'SearchBoundaryForm', '/search-boundary', NULL, 381, 0, 'Search Boundary', true, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (69, 'ViewuserRoleForm', '/userrole/search', NULL, 5, 0, 'Search User Role', true, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);

INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (1015, 'AppConfigModuleAutocomplete', '/appConfig/modules', NULL, 115, 0, 'AppConfigModuleAutocomplete', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (1036, 'viewAppConfigAjaxResult', '/appConfig/ajax/result', NULL, 115, 3, 'ViewAppConfigAjax', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (1014, 'CreateAppConfig', '/appConfig/create', NULL, 115, 0, 'Create Configuration Value', true, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (1016, 'modifyAppConfig', '/appConfig/update', NULL, 115, 1, 'Modify Configuration Value', true, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (1035, 'viewAppConfig', '/appConfig/view', NULL, 115, 2, 'View Configuration Value', true, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (41, 'Inbox', '/inbox', NULL, 237, NULL, 'Inbox', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (42, 'InboxDraft', '/inbox/draft', NULL, 237, NULL, 'Inbox Draft', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (43, 'InboxHistory', '/inbox/history', NULL, 237, NULL, 'Inbox History', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (62, 'Role View', '/role/view', NULL, 237, 0, 'Role View', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (63, 'Role Update', '/role/update', NULL, 237, 0, 'Role View', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (64, 'ViewDepartmentForm', '/department/view', NULL, 3, 0, 'View Department', true, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (65, 'UpdateDepartmentForm', '/department/update', NULL, 3, 0, 'Update Department', true, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (40, 'CreateDepartmentForm', '/department/create', NULL, 3, 0, 'Create Department', true, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (47, 'AjaxAddChildBoundaryTypeCheck', '/boundarytype/ajax/checkchild', NULL, 2, 0, 'AjaxAddChildBoundaryTypeCheck', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (50, 'AjaxLoadBoundaryTypes', '/boundarytype/ajax/boundarytypelist-for-hierarchy', NULL, 2, 0, 'AjaxLoadBoundaryTypes', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (25, 'CreateRoleForm', '/role/create', NULL, 5, 0, 'Create Role New', true, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (26, 'ViewRoleForm', '/role/viewsearch', NULL, 5, 0, 'View Role', true, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (27, 'UpdateRoleForm', '/role/updatesearch', NULL, 5, 0, 'Update Role', true, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (70, 'UpdateuserRoleForm', '/userrole/update', NULL, 5, 0, 'Update User Role', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (71, 'UpdateuserRole', '/userrole/update/updateUserRole', NULL, 5, 0, 'Update User Roles ', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (72, 'viewuserRole', '/userrole/view', NULL, 5, 0, 'View User Roles ', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (73, 'AjaxLoadRoleByUser', '/userRole/ajax/rolelist-for-user', NULL, 5, 0, 'AjaxLoadRoleByUser', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (1041, 'AppconfigPopulateList', '/appConfig/ajax-appConfigpopulate', NULL, 115, 0, 'AppconfigPopulateList', false, 'egi', 0, 1, '2015-08-28 10:43:36.565418', 1, '2015-08-28 10:43:36.565418', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (1044, 'AppconfigValuesListForView', '/appConfig/viewList/', NULL, 115, 0, 'AppconfigValuesListForView', false, 'egi', 0, 1, '2015-08-28 10:43:38.270542', 1, '2015-08-28 10:43:38.270542', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (66, 'OfficialsProfileEdit', '/home/profile/edit', NULL, 237, NULL, 'OfficialsProfileEdit', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (67, 'OfficialSentFeedBack', '/home/feedback/sent', NULL, 237, NULL, 'OfficialSentFeedBack', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (68, 'OfficialChangePassword', '/home/password/update', NULL, 237, NULL, 'OfficialChangePassword', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (59, 'AddFavourite', '/home/favourite/add', NULL, 237, NULL, 'AddFavourite', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (58, 'RemoveFavourite', '/home/favourite/remove', NULL, 237, NULL, 'RemoveFavourite', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (1181, 'CitySetup', '/city/setup/view', NULL, 411, 1, 'City Setup', true, 'egi', 0, 1, '2015-08-28 10:45:30.565663', 1, '2015-08-28 10:45:30.565663', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (1182, 'CitySetupUpdate', '/city/setup/update', NULL, 411, 1, 'CitySetupUpdate', false, 'egi', 0, 1, '2015-08-28 10:45:30.565663', 1, '2015-08-28 10:45:30.565663', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (1192, 'GisFileDownload', '/downloadfile/gis', NULL, 411, 1, 'GisFileDownload', false, 'egi', 0, 1, '2015-08-28 10:45:31.664804', 1, '2015-08-28 10:45:31.664804', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (1224, 'AadhaarInfo', '/aadhaar', NULL, 237, 0, 'AadhaarInfo', false, 'egi', 0, 1, '2015-08-28 10:45:34.775828', 1, '2015-08-28 10:45:34.775828', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (37, 'Official Home Page', '/home', NULL, 237, 0, 'User Login', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);
INSERT INTO eg_action (id, name, url, queryparams, parentmodule, ordernumber, displayname, enabled, contextroot, version, createdby, createddate, lastmodifiedby, lastmodifieddate, application) VALUES (56, 'File Download', '/downloadfile', NULL, 237, 0, 'File Download', false, 'egi', 0, 1, '2015-08-28 10:43:35.552035', 1, '2015-08-28 10:43:35.552035', 1);


DROP SEQUENCE SEQ_EG_ACTION;

CREATE SEQUENCE SEQ_EG_ACTION
    START WITH 1343
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
------------------END---------------------

-----------------START--------------------
INSERT INTO eg_messagetemplate (id, templatename, template, locale, version) VALUES (1, 'user.pwd.recovery', 'Click the link below to reset your password {2} {0}{1} {2} If clicking the link above doesn''''t work, please copy and paste the URL in a new browser window instead.{2}If you''''ve received this in error, it''''s likely that another user entered your email address or mobile number by mistake while trying to reset a password. If you didn''''t initiate the request, you don''''t need to take any further action and can safely disregard this email.', 'en_IN', NULL);
INSERT INTO eg_messagetemplate (id, templatename, template, locale, version) VALUES (2, 'user.pwd.reset', 'Hi {0}, {2} Your password has been reset, your new password is {2} {1} {2} Please change this password as soon as you login for more security.', 'en_IN', NULL);
------------------END---------------------

-----------------START--------------------
INSERT INTO eg_role (id, name, description, createddate, createdby, lastmodifiedby, lastmodifieddate, version) VALUES (5, 'CSC Operator', 'Collection Operator mans the Citizen Service Centers.', '2010-01-01 00:00:00', 1, 1, '2015-01-01 00:00:00', 0);
INSERT INTO eg_role (id, name, description, createddate, createdby, lastmodifiedby, lastmodifieddate, version) VALUES (7, 'Citizen', 'Citizen who can raise complaint', '2010-01-01 00:00:00', 1, 1, '2015-01-01 00:00:00', 0);
INSERT INTO eg_role (id, name, description, createddate, createdby, lastmodifiedby, lastmodifieddate, version) VALUES (15, 'Employee', 'Default role for all employees', '2015-08-28 00:00:00', 1, 1, '2015-08-28 00:00:00', 0);
INSERT INTO eg_role (id, name, description, createddate, createdby, lastmodifiedby, lastmodifieddate, version) VALUES (16, 'ULB Operator', 'ULB Operator', '2015-08-28 10:45:17.567676', 1, 1, '2015-08-28 10:45:17.567676', 0);
INSERT INTO eg_role (id, name, description, createddate, createdby, lastmodifiedby, lastmodifieddate, version) VALUES (4, 'Super User', 'System Administrator. Can change all master data and has access to all the system screens.', '2010-01-01 00:00:00', 1, 1, '2015-01-01 00:00:00', 0);
------------------END---------------------

-----------------START--------------------
INSERT INTO eg_user (id, title, salutation, dob, locale, username, password, pwdexpirydate, mobilenumber, altcontactnumber, emailid, createddate, lastmodifieddate, createdby, lastmodifiedby, active, name, gender, pan, aadhaarnumber, type, version, guardian, guardianrelation) VALUES (1, NULL, NULL, NULL, 'en_IN', 'egovernments', '$2a$10$uheIOutTnD33x7CDqac1zOL8DMiuz7mWplToPgcf7oxAI9OzRKxmK', '2020-12-31 00:00:00', NULL, NULL, NULL, '2010-01-01 00:00:00', '2015-01-01 00:00:00', 1, 1, true, 'Admin', NULL, NULL, NULL, 'EMPLOYEE', 0, NULL, NULL);
INSERT INTO eg_user (id, title, salutation, dob, locale, username, password, pwdexpirydate, mobilenumber, altcontactnumber, emailid, createddate, lastmodifieddate, createdby, lastmodifiedby, active, name, gender, pan, aadhaarnumber, type, version, guardian, guardianrelation) VALUES (2, NULL, 'MR.', NULL, 'en_IN', 'anonymous', 'XYZ', '2099-01-01 00:00:00', NULL, NULL, NULL, '2010-01-01 00:00:00', '2015-01-01 00:00:00', 1, 1, true, 'Anonymous', NULL, NULL, NULL, 'EMPLOYEE', 0, NULL, NULL);
------------------END---------------------

-----------------START--------------------
INSERT into eg_userrole values((select id from eg_role  where name  = 'Super User'),(select id from eg_user where username ='egovernments'));
------------------END---------------------

Insert into EG_ROLEACTION (roleid, actionid) values (15,37);
Insert into EG_ROLEACTION (roleid, actionid) values (4,1049),(4,33),(4,46),(4,54),(4,55),(4,34),(4,35),(4,39),(4,30),(4,32),(4,31),(4,44),(4,69),(4,1015),(4,1036),(4,1014),(4,1016),(4,1035),(4,41),(4,42),(4,43),(4,62),(4,63),(4,64),(4,65),(4,40),(4,47),(4,50),(4,25),(4,26),(4,27),(4,70),(4,71),(4,72),(4,73),(4,1041),(4,1044),(4,66),(4,67),(4,68),(4,59),(4,58),(4,1181),(4,1182),(4,1192),(4,1224),(4,37),(4,56);
---Add super user role to all actions

INSERT INTO eg_hierarchy_type (id, name, code, createddate, lastmodifieddate, createdby, lastmodifiedby, version, localname) VALUES (1, 'ADMINISTRATION', 'ADMIN', '2010-01-01 00:00:00', '2015-01-01 00:00:00', 1, 1, 0, NULL);
INSERT INTO eg_hierarchy_type (id, name, code, createddate, lastmodifieddate, createdby, lastmodifiedby, version, localname) VALUES (2, 'LOCATION', 'LOCATION', '2010-01-01 00:00:00', '2015-01-01 00:00:00', 1, 1, 0, NULL);

INSERT INTO eg_boundary_type (id, hierarchy, parent, name, hierarchytype, createddate, lastmodifieddate, createdby, lastmodifiedby, version, localname) VALUES (1, 1, NULL, 'City', 1, '2010-01-01 00:00:00', '2015-01-01 00:00:00', 1, 1, 0, NULL);
INSERT INTO eg_boundary_type (id, hierarchy, parent, name, hierarchytype, createddate, lastmodifieddate, createdby, lastmodifiedby, version, localname) VALUES (6, 1, NULL, 'City', 2, '2015-08-28 10:44:03.831086', '2015-08-28 10:44:03.831086', 1, 1, 0, NULL);
