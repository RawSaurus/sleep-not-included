-- =============================================================================
-- init-db.sql  —  runs once when the PostgreSQL container is first created
-- Creates dedicated schemas for each microservice + Keycloak
-- =============================================================================

-- Microservice schemas
CREATE SCHEMA IF NOT EXISTS user_service;
CREATE SCHEMA IF NOT EXISTS build_service;
CREATE SCHEMA IF NOT EXISTS comment_service;
CREATE SCHEMA IF NOT EXISTS tag_service;
CREATE SCHEMA IF NOT EXISTS gameres_service;
CREATE SCHEMA IF NOT EXISTS image_service;
-- Keycloak
CREATE SCHEMA IF NOT EXISTS keycloak;

-- Grant all to the app user
GRANT ALL PRIVILEGES ON SCHEMA user_service    TO sni_user;
GRANT ALL PRIVILEGES ON SCHEMA build_service   TO sni_user;
GRANT ALL PRIVILEGES ON SCHEMA comment_service TO sni_user;
GRANT ALL PRIVILEGES ON SCHEMA tag_service     TO sni_user;
GRANT ALL PRIVILEGES ON SCHEMA gameres_service TO sni_user;
GRANT ALL PRIVILEGES ON SCHEMA image_service   TO sni_user;
GRANT ALL PRIVILEGES ON SCHEMA keycloak        TO sni_user;