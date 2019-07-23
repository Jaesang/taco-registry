-- used in tests that use HSQL
create table oauth_client_details (client_id VARCHAR(256) PRIMARY KEY,resource_ids VARCHAR(256),client_secret VARCHAR(256),scope VARCHAR(256),authorized_grant_types VARCHAR(256),web_server_redirect_uri VARCHAR(256),authorities VARCHAR(256),access_token_validity INTEGER,refresh_token_validity INTEGER,additional_information VARCHAR(4096),autoapprove VARCHAR(256));
create table oauth_client_token (token_id VARCHAR(256),token bytea,authentication_id VARCHAR(256) PRIMARY KEY,user_name VARCHAR(256),client_id VARCHAR(256));
create table oauth_access_token (token_id VARCHAR(256),token bytea,authentication_id VARCHAR(256) PRIMARY KEY,user_name VARCHAR(256),client_id VARCHAR(256),authentication bytea,refresh_token VARCHAR(256));
create table oauth_refresh_token (token_id VARCHAR(256),token bytea,authentication bytea);
create table oauth_code (code VARCHAR(256), authentication bytea);
create table oauth_approvals (userId VARCHAR(256),clientId VARCHAR(256),scope VARCHAR(256),status VARCHAR(10),expiresAt TIMESTAMP,lastModifiedAt TIMESTAMP);

-- customized oauth_client_details table
-- create table ClientDetails (appId VARCHAR(256) PRIMARY KEY,resourceIds VARCHAR(256),appSecret VARCHAR(256),scope VARCHAR(256),grantTypes VARCHAR(256),redirectUrl VARCHAR(256),authorities VARCHAR(256),access_token_validity INTEGER,refresh_token_validity INTEGER,additionalInformation VARCHAR(4096),autoApproveScopes VARCHAR(256));

-- user & role
INSERT INTO cm_user ( email, enabled, del_yn, name, password, username, super_user ) values ( 'admin@sktelecom.com', true, false, '관리자', '$2a$10$IDw2a0sOsi8Zp6myDhAbYOKT1uDr11XYuVoi6ltOi85WME.lL7M9O', 'admin', true );
INSERT INTO common_code (group_code, code, code_name, comment, enabled) VALUES ('ROLE', 'A', 'ADMIN', 'ROLE 타입', true);
INSERT INTO common_code (group_code, code, code_name, comment, enabled) VALUES ('ROLE', 'U', 'USER', 'ROLE 타입', true);

COMMIT;
