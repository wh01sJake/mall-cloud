# Environment Variables Configuration

This document lists all required environment variables for the VapeMall application deployment.

## ⚠️ SECURITY NOTICE
**Never commit actual values of these environment variables to version control.**
**All sensitive data has been removed from configuration files and must be set as environment variables.**

## Required Environment Variables

### Database Configuration
```bash
DATABASE_URL=jdbc:mysql://[your-database-host]:[port]/[database-name]?[connection-params]
DATABASE_USERNAME=[your-database-username]
DATABASE_PASSWORD=[your-database-password]
```

### AWS S3 Configuration
```bash
AWS_ACCESS_KEY_ID=[your-aws-access-key]
AWS_SECRET_ACCESS_KEY=[your-aws-secret-key]
AWS_REGION=[your-aws-region]
S3_BUCKET_NAME=[your-s3-bucket-name]
S3_REGION=[your-s3-region]
```

### JWT Configuration
```bash
JWT_SECRET=[your-jwt-secret-key]
JWT_EXPIRATION=86400
```

### Email Service Configuration
```bash
SENDGRID_API_KEY=[your-sendgrid-api-key]
FROM_EMAIL=[your-from-email]
FROM_NAME=[your-from-name]
ADMIN_EMAIL=[admin-email-address]
```

### Service URLs (for inter-service communication)
```bash
ADMIN_SERVICE_URL=[admin-service-url]
PRODUCT_SERVICE_URL=[product-service-url]
CUSTOMER_SERVICE_URL=[customer-service-url]
ORDER_SERVICE_URL=[order-service-url]
EMAIL_SERVICE_URL=[email-service-url]
STATISTICS_SERVICE_URL=[statistics-service-url]
```

### Redis Configuration (Optional)
```bash
REDIS_URL=[your-redis-url]
```

## Heroku Deployment

For Heroku deployment, set these variables using:
```bash
heroku config:set VARIABLE_NAME=value --app your-app-name
```

## Local Development

For local development, create a `.env` file (not committed to git):
```bash
# Copy this template and fill in your values
cp .env.example .env
```

## Security Best Practices

1. **Rotate credentials regularly**
2. **Use different credentials for different environments**
3. **Never log sensitive environment variables**
4. **Use least-privilege access for all credentials**
5. **Monitor access logs for unusual activity**

## Verification

To verify all environment variables are set correctly:
```bash
# Check if all required variables are set
./scripts/check-env-vars.sh
```
