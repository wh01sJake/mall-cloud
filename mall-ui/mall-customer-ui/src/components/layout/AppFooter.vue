<template>
  <footer class="app-footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-section">
          <h3>VapeMall</h3>
          <p>Your trusted source for premium vaping products. Quality, safety, and customer satisfaction are our top priorities.</p>
          <div class="social-links">
            <a href="#" class="social-link">
              <el-icon><Share /></el-icon>
            </a>
            <a href="#" class="social-link">
              <el-icon><ChatDotRound /></el-icon>
            </a>
            <a href="#" class="social-link">
              <el-icon><Message /></el-icon>
            </a>
          </div>
        </div>

        <div class="footer-section">
          <h4>Quick Links</h4>
          <ul class="footer-links">
            <li><router-link to="/">Home</router-link></li>
            <li><router-link to="/products">All Products</router-link></li>
            <li><router-link to="/category/1">E-Cigarettes</router-link></li>
            <li><router-link to="/category/2">E-Liquids</router-link></li>
            <li><router-link to="/category/3">Accessories</router-link></li>
          </ul>
        </div>

        <div class="footer-section">
          <h4>Customer Service</h4>
          <ul class="footer-links">
            <li><a href="#contact">Contact Us</a></li>
            <li><a href="#shipping">Shipping Info</a></li>
            <li><a href="#returns">Returns & Exchanges</a></li>
            <li><a href="#faq">FAQ</a></li>
            <li><a href="#support">Support</a></li>
          </ul>
        </div>

        <div class="footer-section">
          <h4>Account</h4>
          <ul class="footer-links">
            <li v-if="!authStore.isLoggedIn"><router-link to="/login">Login</router-link></li>
            <li v-if="!authStore.isLoggedIn"><router-link to="/register">Register</router-link></li>
            <li v-if="authStore.isLoggedIn"><router-link to="/profile">My Account</router-link></li>
            <li v-if="authStore.isLoggedIn"><router-link to="/orders">Order History</router-link></li>
            <li v-if="authStore.isLoggedIn"><router-link to="/cart">Shopping Cart</router-link></li>
          </ul>
        </div>

        <div class="footer-section">
          <h4>Contact Info</h4>
          <div class="contact-info">
            <p><el-icon><Phone /></el-icon> +1 (555) 123-4567</p>
            <p><el-icon><Message /></el-icon> support@vapemall.com</p>
            <p><el-icon><Location /></el-icon> 123 Vape Street, City, State 12345</p>
            <p><el-icon><Clock /></el-icon> Mon-Fri: 9AM-6PM EST</p>
          </div>
        </div>
      </div>

      <div class="footer-bottom">
        <div class="footer-bottom-content">
          <p>&copy; 2024 VapeMall. All rights reserved.</p>
          <div class="footer-bottom-links">
            <a href="#privacy">Privacy Policy</a>
            <a href="#terms">Terms of Service</a>
            <a href="#cookies">Cookie Policy</a>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth'
import {
  Share,
  ChatDotRound,
  Message,
  Phone,
  Location,
  Clock
} from '@element-plus/icons-vue'

const authStore = useAuthStore()
</script>

<style scoped>
.app-footer {
  background: #2c3e50;
  color: white;
  margin-top: auto;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 40px;
  padding: 60px 0 40px;
}

.footer-section h3 {
  color: #409eff;
  margin-bottom: 20px;
  font-size: 24px;
}

.footer-section h4 {
  color: #ecf0f1;
  margin-bottom: 20px;
  font-size: 18px;
}

.footer-section p {
  line-height: 1.6;
  margin-bottom: 20px;
  color: #bdc3c7;
}

.footer-links {
  list-style: none;
}

.footer-links li {
  margin-bottom: 10px;
}

.footer-links a {
  color: #bdc3c7;
  text-decoration: none;
  transition: color 0.3s;
}

.footer-links a:hover {
  color: #409eff;
}

.social-links {
  display: flex;
  gap: 15px;
}

.social-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: #34495e;
  color: #bdc3c7;
  border-radius: 50%;
  text-decoration: none;
  transition: all 0.3s;
}

.social-link:hover {
  background: #409eff;
  color: white;
  transform: translateY(-2px);
}

.contact-info p {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
  color: #bdc3c7;
}

.footer-bottom {
  border-top: 1px solid #34495e;
  padding: 20px 0;
}

.footer-bottom-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.footer-bottom-links {
  display: flex;
  gap: 20px;
}

.footer-bottom-links a {
  color: #bdc3c7;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.footer-bottom-links a:hover {
  color: #409eff;
}

/* Mobile Styles */
@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: 1fr;
    gap: 30px;
    padding: 40px 0 30px;
  }

  .footer-bottom-content {
    flex-direction: column;
    text-align: center;
  }

  .footer-bottom-links {
    justify-content: center;
  }
}
</style>
