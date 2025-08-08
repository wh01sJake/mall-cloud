# VapeMall E-commerce Platform

A comprehensive microservices-based e-commerce platform built with Spring Cloud and Vue.js, specifically designed for vaping products retail.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen)
![Vue.js](https://img.shields.io/badge/Vue.js-3-green)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Heroku](https://img.shields.io/badge/Deployed%20on-Heroku-purple)

## 🏗️ Architecture Overview

VapeMall follows a distributed microservices architecture with 7 independent services, each handling specific business capabilities:

```
┌─────────────────┐    ┌─────────────────┐
│   Admin UI      │    │  Customer UI    │
│   (Vue 3)       │    │   (Vue 3)       │
└─────────┬───────┘    └─────────┬───────┘
          │                      │
          └──────────┬───────────┘
                     │
            ┌────────▼────────┐
            │  API Gateway    │
            │   (Port 9000)   │
            └─────────┬───────┘
                      │
        ┌─────────────┼─────────────┐
        │             │             │
   ┌────▼───┐   ┌────▼───┐   ┌────▼───┐
   │ Admin  │   │Product │   │Customer│
   │ (8081) │   │ (8082) │   │ (8083) │
   └────────┘   └────────┘   └────────┘
        │             │             │
   ┌────▼───┐   ┌────▼───┐   ┌────▼───┐
   │ Order  │   │ Email  │   │Statistics│
   │ (8084) │   │ (8085) │   │ (8086) │
   └────────┘   └────────┘   └────────┘
```

## 🚀 Features

### 🛒 **E-commerce Core**
- **Product Catalog** - Browse and search vaping products with categories
- **Shopping Cart** - Add, remove, and manage cart items
- **Order Processing** - Complete order placement and tracking
- **User Management** - Customer registration, authentication, and profiles
- **Admin Panel** - Comprehensive management dashboard

### 🔧 **Technical Features**
- **Microservices Architecture** - Independent, scalable services
- **API Gateway** - Centralized routing and authentication
- **JWT Authentication** - Secure token-based authentication
- **File Upload** - AWS S3 integration for product images
- **Email Notifications** - Order confirmations and updates
- **Analytics** - Sales statistics and reporting

### 🎨 **User Interfaces**
- **Responsive Design** - Mobile-first approach
- **Modern UI** - Vue 3 with Element Plus components
- **Real-time Updates** - Dynamic cart and order status
- **Professional Admin** - Comprehensive management tools

## 🛠️ Technology Stack

### **Backend**
- **Framework:** Spring Boot 3.2.5, Spring Cloud 2023.0.1
- **Language:** Java 17 LTS
- **Database:** MySQL 8.0 with MyBatis-Plus ORM
- **Authentication:** JWT (JSON Web Tokens)
- **Communication:** OpenFeign for service-to-service calls
- **Build Tool:** Maven 3.9.4

### **Frontend**
- **Framework:** Vue 3 with Composition API
- **UI Library:** Element Plus
- **State Management:** Pinia
- **Build Tool:** Vite
- **HTTP Client:** Axios

### **Infrastructure**
- **Cloud Platform:** Heroku (EU region)
- **Database:** DigitalOcean Managed MySQL
- **File Storage:** AWS S3 (eu-west-1)
- **Frontend Hosting:** Vercel
- **Email Service:** SendGrid

## 📦 Services Overview

| Service | Port | Description | Key Features |
|---------|------|-------------|--------------|
| **Gateway** | 9000 | API routing and authentication | JWT validation, CORS, rate limiting |
| **Admin** | 8081 | Administrative operations | User management, system configuration |
| **Product** | 8082 | Product catalog management | CRUD operations, image upload, categories |
| **Customer** | 8083 | User management | Registration, authentication, profiles |
| **Order** | 8084 | Order processing | Cart to order, status tracking, history |
| **Email** | 8085 | Notification system | Order confirmations, status updates |
| **Statistics** | 8086 | Analytics and reporting | Sales data, user metrics, dashboards |

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0
- Node.js 16+ (for frontend)

### 1. Clone the Repository
```bash
git clone https://github.com/wh01sJake/mall-cloud.git
cd mall-cloud
```

### 2. Environment Setup
Copy the environment variables template and configure your settings:
```bash
# See ENVIRONMENT_VARIABLES.md for complete setup guide
export DATABASE_URL="jdbc:mysql://localhost:3306/vapemall"
export DATABASE_USERNAME="your_username"
export DATABASE_PASSWORD="your_password"
export AWS_ACCESS_KEY_ID="your_aws_key"
export AWS_SECRET_ACCESS_KEY="your_aws_secret"
export JWT_SECRET="your_jwt_secret"
```

### 3. Database Setup
```sql
CREATE DATABASE vapemall;
-- Run the SQL scripts in each service's resources folder
```

### 4. Build and Run Services
```bash
# Build all services
mvn clean package -DskipTests

# Run services (in separate terminals)
cd mall-gateway && mvn spring-boot:run
cd mall-product && mvn spring-boot:run
cd mall-customer && mvn spring-boot:run
cd mall-order && mvn spring-boot:run
cd mall-admin && mvn spring-boot:run
cd mall-email && mvn spring-boot:run
cd mall-statistics && mvn spring-boot:run
```

### 5. Frontend Setup
```bash
# Admin UI
cd mall-ui/mall-admin-ui
npm install
npm run dev

# Customer UI
cd mall-ui/mall-customer-ui
npm install
npm run dev
```

## 🌐 Live Demo

- **Customer UI:** [Live Demo](https://vape-mall-customer.vercel.app)
- **Admin UI:** [Admin Panel](https://vape-mall-admin.vercel.app)
- (use testadmin:testpassword for Admin UI)

## 📚 Documentation

- **[Environment Variables Setup](ENVIRONMENT_VARIABLES.md)** - Complete configuration guide
- **[Security Notice](SECURITY.md)** - Security best practices and guidelines
- **[Technical Report](VapeMall_Technical_Project_Report.md)** - Comprehensive technical documentation

## 🔒 Security

This repository has been thoroughly sanitized for public sharing:
- ✅ All sensitive data externalized to environment variables
- ✅ No hardcoded credentials or API keys
- ✅ Professional security documentation included
- ✅ GitHub security scan passed

## 🚀 Deployment

### Heroku Deployment
Each service includes Heroku deployment configuration:
```bash
# Deploy individual services
git subtree push --prefix=mall-gateway heroku-gateway main
git subtree push --prefix=mall-product heroku-product main
# ... repeat for other services
```

### Frontend Deployment (Vercel)
```bash
cd mall-ui/mall-customer-ui
vercel --prod

cd mall-ui/mall-admin-ui
vercel --prod
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**VapeMall Development Team**
- Comprehensive e-commerce platform
- Microservices architecture implementation
- Modern web development practices

## 🙏 Acknowledgments

- Spring Boot and Spring Cloud communities
- Vue.js ecosystem contributors
- Cloud service providers (Heroku, AWS, DigitalOcean)
- Open source libraries and frameworks used

---

**⭐ Star this repository if you find it helpful!**
