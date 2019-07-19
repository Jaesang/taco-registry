-- used in tests that use HSQL
create table oauth_client_details (client_id VARCHAR(256) PRIMARY KEY,resource_ids VARCHAR(256),client_secret VARCHAR(256),scope VARCHAR(256),authorized_grant_types VARCHAR(256),web_server_redirect_uri VARCHAR(256),authorities VARCHAR(256),access_token_validity INTEGER,refresh_token_validity INTEGER,additional_information VARCHAR(4096),autoapprove VARCHAR(256));
create table oauth_client_token (token_id VARCHAR(256),token BLOB,authentication_id VARCHAR(256) PRIMARY KEY,user_name VARCHAR(256),client_id VARCHAR(256));
create table oauth_access_token (token_id VARCHAR(256),token BLOB,authentication_id VARCHAR(256) PRIMARY KEY,user_name VARCHAR(256),client_id VARCHAR(256),authentication BLOB,refresh_token VARCHAR(256));
create table oauth_refresh_token (token_id VARCHAR(256),token BLOB,authentication BLOB);
create table oauth_code (code VARCHAR(256), authentication BLOB);
create table oauth_approvals (userId VARCHAR(256),clientId VARCHAR(256),scope VARCHAR(256),status VARCHAR(10),expiresAt TIMESTAMP,lastModifiedAt TIMESTAMP);

-- customized oauth_client_details table
create table ClientDetails (appId VARCHAR(256) PRIMARY KEY,resourceIds VARCHAR(256),appSecret VARCHAR(256),scope VARCHAR(256),grantTypes VARCHAR(256),redirectUrl VARCHAR(256),authorities VARCHAR(256),access_token_validity INTEGER,refresh_token_validity INTEGER,additionalInformation VARCHAR(4096),autoApproveScopes VARCHAR(256));

-- user & role
INSERT INTO user ( id, email, enabled, name, password, username, super_user ) values ( 1, 'admin@sktelecom.com', true, '관리자', '$2a$10$IDw2a0sOsi8Zp6myDhAbYOKT1uDr11XYuVoi6ltOi85WME.lL7M9O', 'admin', 1 );
INSERT INTO user_role ( id, name, user_id, created_date, updated_date ) values ( 1, 'USER', 1, now(), now() );
INSERT INTO user_role ( id, name, user_id, created_date, updated_date ) values ( 2, 'ADMIN', 1, now(), now() );
-- INSERT INTO user ( id, email, enabled, name, password, username ) values ( 2, 'user@bliex.com', true, 'user', '$2a$10$EY4vzIIiTYAGkxmugnEMhul.P22na3efGa5tw/o1G5ewVdm8WMMP2', 'user' );
-- INSERT INTO user_role ( id, name, user_id, created_date, updated_date ) values ( 3, 'USER', 2, now(), now() );
INSERT INTO common_code (group_code, code, code_name, comment, enabled) VALUES ('STATUS', 'S', 'SUCCESS', '실행 상태', 1);
INSERT INTO common_code (group_code, code, code_name, comment, enabled) VALUES ('STATUS', 'F', 'FAIL', '실행 상태', 1);
INSERT INTO common_code (group_code, code, code_name, comment, enabled) VALUES ('STATUS', 'R', 'RUNNING', '실행 상태', 1);
INSERT INTO common_code (group_code, code, code_name, comment, enabled) VALUES ('EXEC_TYPE', 'C', 'cluster', '실행 타입', 1);
INSERT INTO common_code (group_code, code, code_name, comment, enabled) VALUES ('EXEC_TYPE', 'U', 'upgrade-cluster', '실행 타입', 1);
INSERT INTO common_code (group_code, code, code_name, comment, enabled) VALUES ('EXEC_TYPE', 'S', 'scale', '실행 타입', 1);
INSERT INTO common_code (group_code, code, code_name, comment, enabled) VALUES ('EXEC_TYPE', 'R', 'reset', '실행 타입', 1);
INSERT INTO common_code (group_code, code, code_name, comment, enabled) VALUES ('EXEC_TYPE', 'M', 'monitoring', '실행 타입', 1);
INSERT INTO common_code (group_code, code, code_name, comment, enabled) VALUES ('EXEC_TYPE', 'RM', 'reset-monitoring', '실행 타입', 1);
INSERT INTO common_code (group_code, code, code_name, comment, enabled) VALUES ('URL_TYPE', 'K', 'kibana', 'monitoring URL 타입', 1);
INSERT INTO common_code (group_code, code, code_name, comment, enabled) VALUES ('URL_TYPE', 'G', 'grafana', 'monitoring URL 타입', 1);
INSERT INTO common_code (group_code, code, code_name, comment, enabled) VALUES ('ROLE', 'A', 'ADMIN', 'ROLE 타입', 1);
INSERT INTO common_code (group_code, code, code_name, comment, enabled) VALUES ('ROLE', 'U', 'USER', 'ROLE 타입', 1);

COMMIT;
