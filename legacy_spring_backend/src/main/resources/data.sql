-- HariMart Seed Data (Categories, Users, Products)

-- Demo Users (Passwords hashed with BCrypt for 'admin123' and 'customer123')
-- admin123 hash: $2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW
-- customer123 hash: $2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW

INSERT INTO users (id, name, email, phone, password, role, status, created_at)
VALUES 
(1, 'HariMart Administrator', 'admin@harimart.com', '9876543210', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'ROLE_ADMIN', 'ACTIVE', CURRENT_TIMESTAMP),
(2, 'Rahul Sharma', 'customer@harimart.com', '9876543211', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'ROLE_CUSTOMER', 'ACTIVE', CURRENT_TIMESTAMP),
(3, 'Priya Patel', 'priya@example.com', '9876543212', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'ROLE_CUSTOMER', 'ACTIVE', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE id=id;

-- Categories (8 categories)
INSERT INTO categories (id, name, description, image, status)
VALUES
(1, 'Electronics', 'Laptops, audio, smart devices and tech essentials', 'https://images.unsplash.com/photo-1498049794561-7780e7231661?w=500&auto=format&fit=crop', 'ACTIVE'),
(2, 'Fashion', 'Ethnic wear, casual outfits, shoes and modern apparel', 'https://images.unsplash.com/photo-1445205170230-053b83016050?w=500&auto=format&fit=crop', 'ACTIVE'),
(3, 'Home & Kitchen', 'Cookware, decor, dining and smart living essentials', 'https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=500&auto=format&fit=crop', 'ACTIVE'),
(4, 'Beauty', 'Skincare, haircare, makeup and fragrance', 'https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=500&auto=format&fit=crop', 'ACTIVE'),
(5, 'Grocery', 'Daily staples, snacks, dry fruits and organic pantry', 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=500&auto=format&fit=crop', 'ACTIVE'),
(6, 'Mobiles', 'Latest smartphones, 5G devices and accessories', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500&auto=format&fit=crop', 'ACTIVE'),
(7, 'Sports', 'Fitness equipment, sportswear, gym gear and games', 'https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=500&auto=format&fit=crop', 'ACTIVE'),
(8, 'Books', 'Bestsellers, fiction, self-help, business and academics', 'https://images.unsplash.com/photo-1495446815901-a7297e633e8d?w=500&auto=format&fit=crop', 'ACTIVE')
ON DUPLICATE KEY UPDATE id=id;
