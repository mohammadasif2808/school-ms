-- Initialize identity_service database
-- This script is executed automatically when the MySQL container starts

-- Use the identity_service database
USE identity_service;

-- Create Users table
CREATE TABLE IF NOT EXISTS users (
    id BINARY(16) PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    avatar_url VARCHAR(512),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    is_super_admin BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create Roles table
CREATE TABLE IF NOT EXISTS roles (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create Permissions table
CREATE TABLE IF NOT EXISTS permissions (
    id BINARY(16) PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    module VARCHAR(100) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create User_Roles junction table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BINARY(16) NOT NULL,
    role_id BINARY(16) NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Create Role_Permissions junction table
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BINARY(16) NOT NULL,
    permission_id BINARY(16) NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- Create Password Reset Tokens table
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id BINARY(16) PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    is_used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_is_deleted ON users(is_deleted);
CREATE INDEX IF NOT EXISTS idx_roles_name ON roles(name);
CREATE INDEX IF NOT EXISTS idx_roles_is_deleted ON roles(is_deleted);
CREATE INDEX IF NOT EXISTS idx_permissions_code ON permissions(code);
CREATE INDEX IF NOT EXISTS idx_permissions_module ON permissions(module);
CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_user_id ON password_reset_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_token ON password_reset_tokens(token);
CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_expires_at ON password_reset_tokens(expires_at);

-- Create a default ADMIN role for initial setup
INSERT IGNORE INTO roles (id, name, description, status, is_deleted, created_at)
VALUES (
    UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440000', '-', '')),
    'ADMIN',
    'System Administrator with full access',
    'ACTIVE',
    FALSE,
    CURRENT_TIMESTAMP
);

-- Create default permissions
INSERT IGNORE INTO permissions (id, code, description, module, is_deleted, created_at)
VALUES
    (UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440001', '-', '')), 'USER_VIEW', 'View user profiles', 'USER', FALSE, CURRENT_TIMESTAMP),
    (UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440002', '-', '')), 'USER_CREATE', 'Create new users', 'USER', FALSE, CURRENT_TIMESTAMP),
    (UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440003', '-', '')), 'USER_EDIT', 'Edit user information', 'USER', FALSE, CURRENT_TIMESTAMP),
    (UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440004', '-', '')), 'USER_DELETE', 'Delete users', 'USER', FALSE, CURRENT_TIMESTAMP),
    (UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440005', '-', '')), 'ROLE_MANAGE', 'Manage roles', 'ROLE', FALSE, CURRENT_TIMESTAMP),
    (UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440006', '-', '')), 'PERMISSION_MANAGE', 'Manage permissions', 'PERMISSION', FALSE, CURRENT_TIMESTAMP),
    (UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440007', '-', '')), 'STUDENT_VIEW', 'View student information', 'STUDENT', FALSE, CURRENT_TIMESTAMP),
    (UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440008', '-', '')), 'ATTENDANCE_MARK', 'Mark attendance', 'ATTENDANCE', FALSE, CURRENT_TIMESTAMP);

-- Assign default permissions to ADMIN role
INSERT IGNORE INTO role_permissions (role_id, permission_id, assigned_at)
SELECT
    r.id,
    p.id,
    CURRENT_TIMESTAMP
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ADMIN'
AND p.is_deleted = FALSE;

COMMIT;

