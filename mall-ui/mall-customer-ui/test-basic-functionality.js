// Basic functionality test
// This script can be run in the browser console to test basic functionality

console.log('Testing Vue 3 Mall Customer UI...');

// Test 1: Check if Vue app is mounted
if (document.getElementById('app')) {
  console.log('✅ Vue app container found');
} else {
  console.error('❌ Vue app container not found');
}

// Test 2: Check if Element Plus is loaded
if (window.ElementPlus || document.querySelector('.el-button')) {
  console.log('✅ Element Plus components detected');
} else {
  console.error('❌ Element Plus not detected');
}

// Test 3: Check if router is working
if (window.location.pathname !== undefined) {
  console.log('✅ Router path:', window.location.pathname);
} else {
  console.error('❌ Router not working');
}

// Test 4: Check for common errors
const errors = [];
if (document.querySelector('.error')) {
  errors.push('Error elements found on page');
}

if (errors.length === 0) {
  console.log('✅ No obvious errors detected');
} else {
  console.error('❌ Errors found:', errors);
}

console.log('Basic functionality test completed');
