-- Insert sample data

-- Users (password: '123456' encoded with BCrypt)
INSERT INTO sys_user (id, username, password, email, phone, status, created_at) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', 'admin@rbac.com', '13800138000', 1, CURRENT_TIMESTAMP),
(2, 'user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', 'user@rbac.com', '13800138001', 1, CURRENT_TIMESTAMP),
(3, 'guest', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', 'guest@rbac.com', '13800138002', 1, CURRENT_TIMESTAMP);

-- Roles
INSERT INTO sys_role (id, role_name, role_code, description, status, created_at) VALUES
(1, 'Super Admin', 'ROLE_SUPER_ADMIN', 'Full system access', 1, CURRENT_TIMESTAMP),
(2, 'Admin', 'ROLE_ADMIN', 'System administrator', 1, CURRENT_TIMESTAMP),
(3, 'User', 'ROLE_USER', 'Regular user', 1, CURRENT_TIMESTAMP),
(4, 'Guest', 'ROLE_GUEST', 'Guest user with limited access', 1, CURRENT_TIMESTAMP);

-- User-Role Relationships
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 3),
(3, 4);

-- Permissions (Menu Tree Structure)
-- Dashboard
INSERT INTO sys_permission (id, permission_name, permission_code, type, parent_id, sort_order, icon, path, component, status) VALUES
(1, 'Dashboard', 'dashboard', 'menu', NULL, 1, 'Dashboard', '/dashboard', 'Dashboard', 1);

-- System Management
INSERT INTO sys_permission (id, permission_name, permission_code, type, parent_id, sort_order, icon, path, component, status) VALUES
(10, 'System Management', 'system', 'menu', NULL, 10, 'Setting', '/system', 'Layout', 1);

-- User Management
INSERT INTO sys_permission (id, permission_name, permission_code, type, parent_id, sort_order, icon, path, component, status) VALUES
(11, 'User Management', 'system:user', 'menu', 10, 1, 'User', '/system/user', 'system/user/index', 1);
INSERT INTO sys_permission (id, permission_name, permission_code, type, parent_id, sort_order, status) VALUES
(12, 'User List', 'system:user:list', 'button', 11, 1, 1);
INSERT INTO sys_permission (id, permission_name, permission_code, type, parent_id, sort_order, status) VALUES
(13, 'User Create', 'system:user:create', 'button', 11, 2, 1);
INSERT INTO sys_permission (id, permission_name, permission_code, type, parent_id, sort_order, status) VALUES
(14, 'User Update', 'system:user:update', 'button', 11, 3, 1);
INSERT INTO sys_permission (id, permission_name, permission_code, type, parent_id, sort_order, status) VALUES
(15, 'User Delete', 'system:user:delete', 'button', 11, 4, 1);
INSERT INTO sys_permission (id, permission_name, permission_code, type, parent_id, sort_order, status) VALUES
(16, 'User Export', 'system:user:export', 'button', 11, 5, 1);

-- Role Management
INSERT INTO sys_permission (id, permission_name, permission_code, type, parent_id, sort_order, icon, path, component, status) VALUES
(21, 'Role Management', 'system:role', 'menu', 10, 2, 'UserFilled', '/system/role', 'system/role/index', 1);
INSERT INTO sys_permission (id, permission_name, permission_code, type, parent_id, sort_order, status) VALUES
(22, 'Role List', 'system:role:list', 'button', 21, 1, 1);
INSERT INTO sys_permission (id, permission_name, permission_code, type, parent_id, sort_order, status) VALUES
(23, 'Role Create', 'system:role:create', 'button', 21, 2, 1);
INSERT INTO sys_permission (id, permission_name, permission_code, type, parent_id, sort_order, status) VALUES
(24, 'Role Update', 'system:role:update', 'button', 21, 3, 1);
INSERT INTO sys_permission (id, permission_name, permission_code, type, parent_id, sort_order, status) VALUES
(25, 'Role Delete', 'system:role:delete', 'button', 21, 4, 1);
INSERT INTO sys_permission (id, permission_name, permission_code, type, parent_id, sort_order, status) VALUES
(26, 'Role Permission', 'system:role:permission', 'button', 21, 5, 1);

-- Menu Management
INSERT INTO sys_permission (id, permission_name, permission_code, type, parent_id, sort_order, icon, path, component, status) VALUES
(31, 'Menu Management', 'system:menu', 'menu', 10, 3, 'Menu', '/system/menu', 'system/menu/index', 1);
INSERT INTO sys_permission (id, permission_name, permission_code, type, parent_id, sort_order, status) VALUES
(32, 'Menu List', 'system:menu:list', 'button', 31, 1, 1);
INSERT INTO sys_permission (id, permission_name, permission_code, type, parent_id, sort_order, status) VALUES
(33, 'Menu Create', 'system:menu:create', 'button', 31, 2, 1);
INSERT INTO sys_permission (id, permission_name, permission_code, type, parent_id, sort_order, status) VALUES
(34, 'Menu Update', 'system:menu:update', 'button', 31, 3, 1);
INSERT INTO sys_permission (id, permission_name, permission_code, type, parent_id, sort_order, status) VALUES
(35, 'Menu Delete', 'system:menu:delete', 'button', 31, 4, 1);

-- Role-Permission Assignments for SUPER_ADMIN (all permissions)
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission WHERE status = 1;

-- Role-Permission Assignments for ROLE_USER
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(3, 1),
(3, 11),
(3, 12),
(3, 31),
(3, 32);

-- Role-Permission Assignments for ROLE_GUEST
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(4, 1),
(4, 11),
(4, 12);
