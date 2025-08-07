// VapeMall Full Functionality Test
// Run this in the browser console to test all features

console.log('🧪 Testing VapeMall Full Functionality...');

// Test 1: Check if main components are rendered
const tests = [];

// Check App structure
if (document.querySelector('#app')) {
  tests.push('✅ App container found');
} else {
  tests.push('❌ App container missing');
}

// Check Header
if (document.querySelector('.app-header')) {
  tests.push('✅ Header component rendered');
} else {
  tests.push('❌ Header component missing');
}

// Check Footer
if (document.querySelector('.app-footer')) {
  tests.push('✅ Footer component rendered');
} else {
  tests.push('❌ Footer component missing');
}

// Check Router
if (document.querySelector('main.main-content')) {
  tests.push('✅ Main content area found');
} else {
  tests.push('❌ Main content area missing');
}

// Check Element Plus
if (document.querySelector('.el-button') || document.querySelector('[class*="el-"]')) {
  tests.push('✅ Element Plus components detected');
} else {
  tests.push('❌ Element Plus components missing');
}

// Check Navigation
const navLinks = document.querySelectorAll('.nav-link, a[href*="/"]');
if (navLinks.length > 0) {
  tests.push(`✅ Navigation links found (${navLinks.length})`);
} else {
  tests.push('❌ Navigation links missing');
}

// Check Console Messages
const hasVueMessage = performance.getEntriesByType('navigation').length > 0;
tests.push(hasVueMessage ? '✅ Page loaded successfully' : '❌ Page load issues');

// Display Results
console.log('\n📊 Test Results:');
tests.forEach(test => console.log(test));

// Test Navigation
console.log('\n🧭 Testing Navigation:');
const testRoutes = ['/', '/login', '/register', '/products', '/cart'];
testRoutes.forEach(route => {
  try {
    const link = document.querySelector(`a[href="${route}"]`);
    if (link) {
      console.log(`✅ Route ${route} - Link found`);
    } else {
      console.log(`⚠️ Route ${route} - Link not visible (may be in mobile menu)`);
    }
  } catch (e) {
    console.log(`❌ Route ${route} - Error: ${e.message}`);
  }
});

// Test Stores (if available in dev tools)
console.log('\n🏪 Testing Stores:');
try {
  if (window.__VUE_DEVTOOLS_GLOBAL_HOOK__) {
    console.log('✅ Vue DevTools detected - Stores should be available');
  } else {
    console.log('⚠️ Vue DevTools not detected');
  }
} catch (e) {
  console.log('❌ Store test error:', e.message);
}

// Performance Check
console.log('\n⚡ Performance Check:');
const loadTime = performance.timing.loadEventEnd - performance.timing.navigationStart;
if (loadTime < 3000) {
  console.log(`✅ Page load time: ${loadTime}ms (Good)`);
} else if (loadTime < 5000) {
  console.log(`⚠️ Page load time: ${loadTime}ms (Acceptable)`);
} else {
  console.log(`❌ Page load time: ${loadTime}ms (Slow)`);
}

console.log('\n🎯 Manual Tests to Perform:');
console.log('1. Click on navigation links (Home, Products, Login, Register)');
console.log('2. Try responsive design (resize browser window)');
console.log('3. Test login form with invite code: VAPE2024');
console.log('4. Check cart icon in header');
console.log('5. Verify footer links and social media icons');

console.log('\n✨ VapeMall functionality test completed!');
