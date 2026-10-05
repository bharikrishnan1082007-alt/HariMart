package com.harimart.config;

import com.harimart.entity.*;
import com.harimart.enums.*;
import com.harimart.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           AddressRepository addressRepository,
                           CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           ProductImageRepository productImageRepository,
                           CartRepository cartRepository,
                           OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository,
                           ReviewRepository reviewRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0 && productRepository.count() >= 30) {
            log.info("Database already contains seed data. Skipping initialization.");
            return;
        }

        log.info("Initializing HariMart seed data...");

        // 1. Users
        User admin = userRepository.findByEmail("admin@harimart.com").orElseGet(() -> {
            User u = new User();
            u.setName("HariMart Administrator");
            u.setEmail("admin@harimart.com");
            u.setPhone("9876543210");
            u.setPassword(passwordEncoder.encode("admin123"));
            u.setRole(Role.ROLE_ADMIN);
            u.setStatus(UserStatus.ACTIVE);
            return userRepository.save(u);
        });

        User customer = userRepository.findByEmail("customer@harimart.com").orElseGet(() -> {
            User u = new User();
            u.setName("Rahul Sharma");
            u.setEmail("customer@harimart.com");
            u.setPhone("9876543211");
            u.setPassword(passwordEncoder.encode("customer123"));
            u.setRole(Role.ROLE_CUSTOMER);
            u.setStatus(UserStatus.ACTIVE);
            return userRepository.save(u);
        });

        // Ensure shopping carts
        if (cartRepository.findByUserId(customer.getId()).isEmpty()) {
            cartRepository.save(new Cart(customer));
        }

        // Customer addresses
        Address addr1 = null;
        if (addressRepository.findByUserId(customer.getId()).isEmpty()) {
            addr1 = new Address(null, customer, "Flat 402, Green Valley Heights, MG Road", "Bengaluru", "Karnataka", "560001", "India", true);
            Address addr2 = new Address(null, customer, "Plot 18, Sector 15, HSR Layout", "Bengaluru", "Karnataka", "560102", "India", false);
            addr1 = addressRepository.save(addr1);
            addressRepository.save(addr2);
        }

        // 2. Categories (8 categories)
        Category catElectronics = getOrCreateCategory("Electronics", "Laptops, audio, smart home devices and tech essentials", "https://images.unsplash.com/photo-1498049794561-7780e7231661?w=500&auto=format&fit=crop");
        Category catFashion = getOrCreateCategory("Fashion", "Ethnic wear, casual outfits, shoes and modern apparel", "https://images.unsplash.com/photo-1445205170230-053b83016050?w=500&auto=format&fit=crop");
        Category catHome = getOrCreateCategory("Home & Kitchen", "Cookware, decor, dining and smart living essentials", "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=500&auto=format&fit=crop");
        Category catBeauty = getOrCreateCategory("Beauty", "Skincare, haircare, makeup and luxury fragrance", "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=500&auto=format&fit=crop");
        Category catGrocery = getOrCreateCategory("Grocery", "Daily staples, snacks, dry fruits and organic pantry", "https://images.unsplash.com/photo-1542838132-92c53300491e?w=500&auto=format&fit=crop");
        Category catMobiles = getOrCreateCategory("Mobiles", "Latest smartphones, 5G devices, cases and power banks", "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500&auto=format&fit=crop");
        Category catSports = getOrCreateCategory("Sports", "Fitness equipment, sportswear, gym gear and outdoor accessories", "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=500&auto=format&fit=crop");
        Category catBooks = getOrCreateCategory("Books", "Bestsellers, fiction, self-help, business and academics", "https://images.unsplash.com/photo-1495446815901-a7297e633e8d?w=500&auto=format&fit=crop");

        // 3. Products (32 high quality products across categories)
        if (productRepository.count() < 30) {
            // Electronics
            createProduct("UltraClean Wireless Noise-Cancelling Headphones",
                    "Premium over-ear wireless headphones featuring active noise cancellation, 40-hour battery life, and spatial audio with dynamic head tracking.",
                    new BigDecimal("4999.00"), new BigDecimal("15.00"), 45, "SonicPulse", catElectronics,
                    "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop",
                    List.of("https://images.unsplash.com/photo-1484704849700-f032a568e944?w=600&auto=format&fit=crop",
                            "https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=600&auto=format&fit=crop"));

            createProduct("Smart Pulse Watch Pro 2",
                    "Advanced fitness smartwatch with AMOLED display, ECG monitoring, heart-rate variability tracking, and 7-day battery life in a titanium chassis.",
                    new BigDecimal("3499.00"), new BigDecimal("20.00"), 60, "FitVibe", catElectronics,
                    "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop",
                    List.of("https://images.unsplash.com/photo-1579586337278-3befd40fd17a?w=600&auto=format&fit=crop"));

            createProduct("AeroBass Portable Bluetooth Speaker",
                    "Rugged IPX7 waterproof Bluetooth speaker with deep punchy bass, 24-hour battery, and dual wireless stereo pairing for outdoor adventures.",
                    new BigDecimal("1999.00"), new BigDecimal("10.00"), 80, "SonicPulse", catElectronics,
                    "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=600&auto=format&fit=crop",
                    List.of("https://images.unsplash.com/photo-1545454675-3531b543be5d?w=600&auto=format&fit=crop"));

            createProduct("Apex 4K Mechanical Gaming Keyboard",
                    "Ultra-responsive RGB backlit mechanical keyboard with hot-swappable switches, PBT keycaps, and detachable braided Type-C cable.",
                    new BigDecimal("2899.00"), new BigDecimal("12.00"), 35, "HyperGear", catElectronics,
                    "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=600&auto=format&fit=crop",
                    List.of("https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?w=600&auto=format&fit=crop"));

            // Mobiles
            createProduct("Nova 5G Smartphone (8GB RAM, 128GB)",
                    "Lightning-fast 5G device powered by octa-core AI chipset, 6.7-inch 120Hz FHD+ AMOLED screen, and 64MP triple camera with 67W Turbo Charge.",
                    new BigDecimal("18999.00"), new BigDecimal("8.00"), 25, "NovaTech", catMobiles,
                    "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&auto=format&fit=crop",
                    List.of("https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=600&auto=format&fit=crop"));

            createProduct("Titan Edge Fold 5G (12GB RAM, 256GB)",
                    "Revolutionary folding display phone with edge-to-edge flexible OLED glass, aerospace-grade hinge, and dual stereo speakers tuned by Dolby Atmos.",
                    new BigDecimal("59999.00"), new BigDecimal("10.00"), 12, "Titan", catMobiles,
                    "https://images.unsplash.com/photo-1580910051074-3eb694886505?w=600&auto=format&fit=crop",
                    List.of("https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=600&auto=format&fit=crop"));

            createProduct("MagPower 20000mAh Fast Charging Power Bank",
                    "Heavy-duty dual USB-C power bank supporting 65W Power Delivery for laptops, tablets, and phones with integrated digital LED battery indicator.",
                    new BigDecimal("1499.00"), new BigDecimal("25.00"), 110, "PowerVolt", catMobiles,
                    "https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=600&auto=format&fit=crop",
                    List.of("https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=600&auto=format&fit=crop"));

            createProduct("UltraShield Shockproof Clear Case",
                    "Military grade drop-tested protective clear phone case with anti-yellowing German TPU and raised camera lips.",
                    new BigDecimal("399.00"), new BigDecimal("15.00"), 200, "ArmorPro", catMobiles,
                    "https://images.unsplash.com/photo-1601784551446-20c9e07cdbdb?w=600&auto=format&fit=crop",
                    List.of());

            // Fashion
            createProduct("Royal Emerald Handloom Silk Saree",
                    "Authentic traditional handwoven pure silk saree with opulent zari border and unstitched matching blouse piece for festive celebrations.",
                    new BigDecimal("3299.00"), new BigDecimal("30.00"), 40, "KalaKriti", catFashion,
                    "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=600&auto=format&fit=crop",
                    List.of("https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?w=600&auto=format&fit=crop"));

            createProduct("Tailored Linen Mandarin Collar Shirt",
                    "Breathable 100% natural European linen casual full-sleeve shirt crafted with premium mother-of-pearl buttons and relaxed comfort fit.",
                    new BigDecimal("1299.00"), new BigDecimal("20.00"), 85, "UrbanGent", catFashion,
                    "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600&auto=format&fit=crop",
                    List.of("https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=600&auto=format&fit=crop"));

            createProduct("Vintage Indigo Washed Slim Denim Jeans",
                    "Classic stretch denim jeans with whiskers washing, 5-pocket styling, and reinforced rivets designed for effortless all-day mobility.",
                    new BigDecimal("1599.00"), new BigDecimal("25.00"), 70, "DenimCraft", catFashion,
                    "https://images.unsplash.com/photo-1542272604-787c3835535d?w=600&auto=format&fit=crop",
                    List.of("https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=600&auto=format&fit=crop"));

            createProduct("Artisanal Embroidered Anarkali Kurta Set",
                    "Flowy georgette Anarkali kurta accompanied by comfortable churidar pants and a delicate embroidered organza dupatta.",
                    new BigDecimal("2199.00"), new BigDecimal("18.00"), 50, "KalaKriti", catFashion,
                    "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?w=600&auto=format&fit=crop",
                    List.of());

            // Home & Kitchen
            createProduct("ChefChoice Tri-Ply Stainless Steel Cookware Set",
                    "Professional 5-piece tri-ply induction cookware set with scratch-resistant surface, stay-cool ergonomic riveted handles, and tempered glass lids.",
                    new BigDecimal("3999.00"), new BigDecimal("22.00"), 30, "ChefChoice", catHome,
                    "https://images.unsplash.com/photo-1584990347449-397a66b96b02?w=600&auto=format&fit=crop",
                    List.of("https://images.unsplash.com/photo-1583778176476-4a8b04a64771?w=600&auto=format&fit=crop"));

            createProduct("AromaMist Ultrasonic Ceramic Essential Oil Diffuser",
                    "Handcrafted ceramic room diffuser featuring ambient warm LED nightlight, auto shut-off protection, and whisper-quiet ultrasonic atomization.",
                    new BigDecimal("1199.00"), new BigDecimal("15.00"), 65, "ZenLiving", catHome,
                    "https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?w=600&auto=format&fit=crop",
                    List.of());

            createProduct("Nordic Minimalist Ceramic Dinnerware (16 Pieces)",
                    "Matte finish stoneware dinner set comprising dinner plates, side plates, cereal bowls, and coffee mugs, dishwasher and microwave safe.",
                    new BigDecimal("2799.00"), new BigDecimal("12.00"), 25, "NordicHome", catHome,
                    "https://images.unsplash.com/photo-1614088448834-315bc828cf5e?w=600&auto=format&fit=crop",
                    List.of());

            createProduct("Ergonomic Memory Foam Cervical Support Pillow",
                    "Orthopedic cooling gel infused contour pillow designed to relieve neck strain and promote optimal spine alignment during sleep.",
                    new BigDecimal("899.00"), new BigDecimal("20.00"), 90, "RestWell", catHome,
                    "https://images.unsplash.com/photo-1584100936595-c0654b55a2e2?w=600&auto=format&fit=crop",
                    List.of());

            // Beauty & Personal Care
            createProduct("HydraGlow Pure Vitamin C & Hyaluronic Radiance Serum",
                    "Dermatologist tested 15% active Vitamin C facial serum enriched with Hyaluronic Acid and Ferulic Acid to brighten dull skin and diminish spots.",
                    new BigDecimal("699.00"), new BigDecimal("15.00"), 120, "GlowBotany", catBeauty,
                    "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=600&auto=format&fit=crop",
                    List.of("https://images.unsplash.com/photo-1608248543803-ba4f8c70ae0b?w=600&auto=format&fit=crop"));

            createProduct("Organic Cold-Pressed Moroccan Argan Hair Elixir",
                    "Pure golden Moroccan argan oil rich in fatty acids and Vitamin E to tame frizz, repair heat damage, and impart salon shine.",
                    new BigDecimal("849.00"), new BigDecimal("10.00"), 80, "PureEssence", catBeauty,
                    "https://images.unsplash.com/photo-1535585209827-a15fcdbc4c2d?w=600&auto=format&fit=crop",
                    List.of());

            createProduct("Matte Velvet Long-Wear Liquid Lipstick Set",
                    "Set of 4 smudge-proof transfer-resistant velvety matte nude lip colors infused with jojoba oil and Vitamin E for non-drying wear.",
                    new BigDecimal("599.00"), new BigDecimal("25.00"), 150, "LuxeCosmetics", catBeauty,
                    "https://images.unsplash.com/photo-1586495777744-4413f21062fa?w=600&auto=format&fit=crop",
                    List.of());

            createProduct("Bamboo Charcoal Deep Detox Face Wash",
                    "Natural detoxifying gentle foaming cleanser formulated with activated bamboo charcoal and green tea extracts to unclog pores.",
                    new BigDecimal("299.00"), new BigDecimal("10.00"), 140, "EcoCleanse", catBeauty,
                    "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=600&auto=format&fit=crop",
                    List.of());

            // Grocery
            createProduct("Royal Kashmiri Jumbo Walnuts & Almonds Combo (1kg)",
                    "Farm-fresh premium grade crispy Kashmiri walnuts and California almonds packed in resealable freshness ziplock pouches.",
                    new BigDecimal("999.00"), new BigDecimal("18.00"), 75, "NaturesHarvest", catGrocery,
                    "https://images.unsplash.com/photo-1508746829417-e6f548d8d6ed?w=600&auto=format&fit=crop",
                    List.of("https://images.unsplash.com/photo-1509914398867-15d2a67e2a9b?w=600&auto=format&fit=crop"));

            createProduct("Organic Raw Forest Honey with Honeycomb (500g)",
                    "100% pure unprocessed wild forest flower honey harvested ethically from the deep Nilgiri hills with high pollen and natural enzymes.",
                    new BigDecimal("449.00"), new BigDecimal("10.00"), 95, "BeePure", catGrocery,
                    "https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=600&auto=format&fit=crop",
                    List.of());

            createProduct("Single Origin Arabica Roasted Coffee Beans (500g)",
                    "Artisan medium-dark roast specialty coffee beans with notes of dark chocolate, toasted nuts, and subtle berry undertones.",
                    new BigDecimal("599.00"), new BigDecimal("12.00"), 60, "BeanCraft", catGrocery,
                    "https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=600&auto=format&fit=crop",
                    List.of());

            createProduct("Cold-Pressed Extra Virgin Mustard Oil (1L)",
                    "Traditional wooden kachi ghani extracted pure mustard oil with robust pungent aroma and essential Omega-3 fatty acids.",
                    new BigDecimal("249.00"), new BigDecimal("5.00"), 120, "NaturesHarvest", catGrocery,
                    "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=600&auto=format&fit=crop",
                    List.of());

            // Sports
            createProduct("Apex Grip Pro Non-Slip TPE Yoga Mat with Alignment Lines",
                    "Eco-friendly 6mm extra-thick high density dual-layer yoga mat with laser engraved body alignment guide and shoulder carry strap.",
                    new BigDecimal("899.00"), new BigDecimal("20.00"), 85, "FitCore", catSports,
                    "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=600&auto=format&fit=crop",
                    List.of());

            createProduct("Adjustable Cast Iron Hex Dumbbell Pair (10kg)",
                    "Solid cast iron hexagonal fitness dumbbells encased in durable odorless rubber coating to safeguard floors and prevent rolling.",
                    new BigDecimal("1899.00"), new BigDecimal("15.00"), 40, "TitanIron", catSports,
                    "https://images.unsplash.com/photo-1583454110551-21f2fa2afe61?w=600&auto=format&fit=crop",
                    List.of());

            createProduct("HydraFlask 1000ml Insulated Stainless Steel Sports Bottle",
                    "Vacuum insulated double-wall flask keeping beverages ice cold for 24 hours or piping hot for 12 hours with leakproof straw lid.",
                    new BigDecimal("649.00"), new BigDecimal("20.00"), 130, "HydroPeak", catSports,
                    "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=600&auto=format&fit=crop",
                    List.of());

            createProduct("SpeedPro Skipping Rope with Weighted Handles",
                    "Smooth 360-degree ball bearing jump rope with adjustable steel wire cable for high-intensity cardio, HIIT, and boxing workouts.",
                    new BigDecimal("349.00"), new BigDecimal("15.00"), 160, "FitCore", catSports,
                    "https://images.unsplash.com/photo-1599058917212-d750089bc07e?w=600&auto=format&fit=crop",
                    List.of());

            // Books
            createProduct("Atomic Habits by James Clear (Hardcover Edition)",
                    "The world-renowned framework for building good habits, breaking bad ones, and mastering the tiny behaviors that lead to remarkable results.",
                    new BigDecimal("599.00"), new BigDecimal("25.00"), 100, "Penguin Random House", catBooks,
                    "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600&auto=format&fit=crop",
                    List.of());

            createProduct("The Psychology of Money by Morgan Housel",
                    "Timeless lessons on wealth, greed, and happiness exploring how people think about money and practical rules for financial independence.",
                    new BigDecimal("399.00"), new BigDecimal("20.00"), 110, "Harriman House", catBooks,
                    "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=600&auto=format&fit=crop",
                    List.of());

            createProduct("Ikigai: The Japanese Secret to a Long and Happy Life",
                    "Inspiring bestselling guide revealing the daily habits, dietary wisdom, and purposeful philosophy of Okinawa's centenarians.",
                    new BigDecimal("349.00"), new BigDecimal("15.00"), 80, "Penguin Life", catBooks,
                    "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=600&auto=format&fit=crop",
                    List.of());

            createProduct("Deep Work: Rules for Focused Success by Cal Newport",
                    "Actionable strategies for cultivating superhuman focus in a distracted world to produce elite value in your professional career.",
                    new BigDecimal("499.00"), new BigDecimal("18.00"), 70, "Grand Central Publishing", catBooks,
                    "https://images.unsplash.com/photo-1495446815901-a7297e633e8d?w=600&auto=format&fit=crop",
                    List.of());
        }

        // 4. Seed Reviews for products
        List<Product> products = productRepository.findAll();
        if (!products.isEmpty() && reviewRepository.count() == 0) {
            Product p1 = products.get(0);
            reviewRepository.save(new Review(null, p1, customer, 5, "Absolutely sensational sound quality! The active noise cancellation blocks out all ambient office noise."));
            reviewRepository.save(new Review(null, p1, admin, 4, "High build quality and deep bass. Battery easily lasted full 3 days of heavy use."));

            if (products.size() > 1) {
                Product p2 = products.get(1);
                reviewRepository.save(new Review(null, p2, customer, 5, "Best smartwatch for the price! Accurate step counting and the AMOLED screen is super vivid outdoors."));
            }
        }

        // 5. Seed a sample past order for the customer
        if (orderRepository.count() == 0 && !products.isEmpty()) {
            Product sampleP1 = products.get(0);
            Product sampleP2 = products.get(1);

            Order sampleOrder = new Order();
            sampleOrder.setOrderNumber("HM2609014521");
            sampleOrder.setUser(customer);
            sampleOrder.setTotalAmount(sampleP1.getFinalPrice().add(sampleP2.getFinalPrice()));
            sampleOrder.setDiscount(BigDecimal.ZERO);
            sampleOrder.setDeliveryCharge(BigDecimal.ZERO);
            sampleOrder.setFinalAmount(sampleOrder.getTotalAmount());
            sampleOrder.setPaymentMethod(PaymentMethod.ONLINE_MOCK);
            sampleOrder.setPaymentStatus(PaymentStatus.COMPLETED);
            sampleOrder.setOrderStatus(OrderStatus.DELIVERED);
            sampleOrder.setShippingAddress("Flat 402, Green Valley Heights, MG Road, Bengaluru, Karnataka - 560001, India");

            Order savedOrder = orderRepository.save(sampleOrder);

            orderItemRepository.save(new OrderItem(null, savedOrder, sampleP1, 1, sampleP1.getFinalPrice()));
            orderItemRepository.save(new OrderItem(null, savedOrder, sampleP2, 1, sampleP2.getFinalPrice()));

            // Seed a second order in SHIPPED status for tracking preview
            Order trackingOrder = new Order();
            trackingOrder.setOrderNumber("HM2609208832");
            trackingOrder.setUser(customer);
            trackingOrder.setTotalAmount(sampleP1.getFinalPrice());
            trackingOrder.setDiscount(new BigDecimal("100.00"));
            trackingOrder.setDeliveryCharge(BigDecimal.ZERO);
            trackingOrder.setFinalAmount(sampleP1.getFinalPrice().subtract(new BigDecimal("100.00")));
            trackingOrder.setPaymentMethod(PaymentMethod.COD);
            trackingOrder.setPaymentStatus(PaymentStatus.PENDING);
            trackingOrder.setOrderStatus(OrderStatus.SHIPPED);
            trackingOrder.setShippingAddress("Flat 402, Green Valley Heights, MG Road, Bengaluru, Karnataka - 560001, India");

            Order savedTrackingOrder = orderRepository.save(trackingOrder);
            orderItemRepository.save(new OrderItem(null, savedTrackingOrder, sampleP1, 1, sampleP1.getFinalPrice()));
        }

        log.info("HariMart seed data initialization completed successfully.");
    }

    private Category getOrCreateCategory(String name, String description, String image) {
        return categoryRepository.findByNameIgnoreCase(name).orElseGet(() -> {
            Category c = new Category(null, name, description, image, "ACTIVE");
            return categoryRepository.save(c);
        });
    }

    private void createProduct(String name, String description, BigDecimal price, BigDecimal discount,
                               int stock, String brand, Category category, String imageUrl, List<String> extraImages) {
        Product p = new Product(null, name, description, price, discount, stock, brand, category, imageUrl, "ACTIVE");
        Product saved = productRepository.save(p);

        if (extraImages != null) {
            for (String img : extraImages) {
                ProductImage pi = new ProductImage(null, saved, img);
                productImageRepository.save(pi);
            }
        }
    }
}
