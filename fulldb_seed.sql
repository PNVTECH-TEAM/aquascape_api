-- ==========================================
-- SCRIPT INSERT SAMPLE DATA CHO TẤT CẢ CÁC BẢNG (FULL DUMP)
-- Dùng được trong PostgreSQL (authdb)
-- ==========================================

-- 1. Xóa sạch dữ liệu cũ (Tùy chọn, tùy vào khóa ngoại DELETE CASCADE)
-- TRUNCATE TABLE users, tank_presets, aquarium_catalog, tanks, tank_layouts, tank_layout_items CASCADE;

-- ==========================================
-- 2. INSERT USERS
-- ==========================================
INSERT INTO users (id, email, username, password, is_active) VALUES
(1, 'test@example.com', 'testuser', '$2a$10$wK1F5p.E3r0Q2e3k7b9XLuL9E3d4.K8v0N0c8F.qjB2LXZA4/W6mS', true)
ON CONFLICT (id) DO NOTHING;

-- Đặt lại sequence cho users nếu insert record cứng id=1 (để tránh lỗi sequence khi auto_increment tiếp)
SELECT setval(pg_get_serial_sequence('users', 'id'), coalesce(max(id), 1), false) FROM users;

-- ==========================================
-- 3. INSERT TANK PRESETS
-- ==========================================
INSERT INTO tank_presets (id, name, width_cm, height_cm, depth_cm) VALUES
('small', 'Small', 60.00, 30.00, 30.00),
('medium', 'Medium', 90.00, 45.00, 45.00),
('large', 'Large', 120.00, 60.00, 60.00),
('extra', 'Extra Large', 150.00, 60.00, 60.00),
('cube', 'Cube', 60.00, 60.00, 60.00),
('wide', 'Wide', 120.00, 45.00, 30.00)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    width_cm = EXCLUDED.width_cm,
    height_cm = EXCLUDED.height_cm,
    depth_cm = EXCLUDED.depth_cm;

-- ==========================================
-- 4. INSERT AQUARIUM CATALOG (PLANTS, ROCKS, WOODS, EQUIPMENTS)
-- ==========================================
INSERT INTO aquarium_catalog (id, name, image_url, model_url, category, type, is_active) VALUES
-- Cây thủy sinh
('plant-hc-cuba', 'Hemianthus callitrichoides (HC Cuba)', 'https://example.com/hc-cuba.png', 'https://example.com/models/hc-cuba.glb', 'Plants', 'Foreground', true),
('plant-monte-carlo', 'Micranthemum Monte Carlo', 'https://example.com/monte-carlo.png', 'https://example.com/models/monte-carlo.glb', 'Plants', 'Foreground', true),
('plant-java-fern', 'Java Fern', 'https://example.com/java-fern.png', 'https://example.com/models/java-fern.glb', 'Plants', 'Midground', true),
('plant-rotala-rotundifolia', 'Rotala Rotundifolia', 'https://example.com/rotala.png', 'https://example.com/models/rotala.glb', 'Plants', 'Background', true),
('plant-anubias-nana', 'Anubias Nana', 'https://example.com/anubias.png', 'https://example.com/models/anubias.glb', 'Plants', 'Midground', true),

-- Đá cảnh
('rock-seiryu-01', 'Seiryu Stone Small', 'https://example.com/seiryu-s.png', 'https://example.com/models/seiryu-s.glb', 'Hardscape', 'Rock', true),
('rock-seiryu-02', 'Seiryu Stone Medium', 'https://example.com/seiryu-m.png', 'https://example.com/models/seiryu-m.glb', 'Hardscape', 'Rock', true),
('rock-dragon-01', 'Dragon Stone Small', 'https://example.com/dragon-s.png', 'https://example.com/models/dragon-s.glb', 'Hardscape', 'Rock', true),
('3d-rock', 'Demo 3D Rock', 'https://example.com/demo-rock.png', 'https://example.com/models/demo-rock.glb', 'Hardscape', 'Rock', true),

-- Lũa (Gỗ)
('wood-spider-01', 'Spider Wood Small', 'https://example.com/spider-s.png', 'https://example.com/models/spider-s.glb', 'Hardscape', 'Wood', true),
('wood-driftwood-01', 'Driftwood Medium', 'https://example.com/driftwood-m.png', 'https://example.com/models/driftwood-m.glb', 'Hardscape', 'Wood', true)
ON CONFLICT (id) DO NOTHING;

-- ==========================================
-- 5. INSERT USER TANKS
-- ==========================================
-- Dùng ID chuỗi nếu trường là UUID (hoặc bạn có thể dùng gen_random_uuid()).
-- Dưới đây sử dụng các UUID fix cứng để có thể thiết lập Layout và Layout Items refer UUID này rõ ràng.

INSERT INTO tanks (id, user_id, name, preset_id, latest_layout_version) VALUES
('11111111-1111-1111-1111-111111111111', 1, 'My Awesome Tank', 'small', 1)
ON CONFLICT (id) DO NOTHING;

-- ==========================================
-- 6. INSERT TANK LAYOUTS
-- ==========================================
INSERT INTO tank_layouts (id, tank_id, version, preview_image_url) VALUES
('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 1, 'https://example.com/preview.png')
ON CONFLICT (id) DO NOTHING;

-- ==========================================
-- 7. INSERT TANK LAYOUT ITEMS
-- ==========================================
-- Lỗi do 'rock-seiryu-01' và '3d-rock' đều mapping thành một catalog item, 
-- ở đây dùng cái catalogItemId = '3d-rock' theo spec của bạn.

INSERT INTO tank_layout_items (id, layout_id, instance_id, catalog_item_id, pos_x, pos_y, pos_z, rot_x, rot_y, rot_z, scale_x, scale_y, scale_z) VALUES
('33333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', 'instance-demo-rock-1', '3d-rock', -8.0, 1.2, -4.0, 0.0, 0.6, 0.0, 1.0, 1.0, 1.0),
('44444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222', 'instance-demo-plant-1', 'plant-hc-cuba', 2.0, 0.0, 3.5, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
ON CONFLICT (id) DO NOTHING;

-- Hoàn tất!
