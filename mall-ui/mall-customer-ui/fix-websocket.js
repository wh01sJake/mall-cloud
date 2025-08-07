#!/usr/bin/env node

/**
 * WebSocket Connection Fix Script
 * This script helps diagnose and fix WebSocket connection issues with Vite dev server
 */

import { execSync } from 'child_process'
import { existsSync, readFileSync, writeFileSync } from 'fs'
import { join } from 'path'

console.log('🔧 VapeMall WebSocket Connection Fix')
console.log('=====================================')

// Check if we're in the right directory
if (!existsSync('package.json')) {
  console.error('❌ Error: package.json not found. Please run this script from the project root.')
  process.exit(1)
}

// Read package.json
const packageJson = JSON.parse(readFileSync('package.json', 'utf8'))
console.log(`📦 Project: ${packageJson.name}`)

// Check if Vite is installed
if (!packageJson.devDependencies?.vite && !packageJson.dependencies?.vite) {
  console.error('❌ Error: Vite not found in dependencies.')
  process.exit(1)
}

console.log('✅ Vite found in dependencies')

// Check for running processes on port 3000
console.log('\n🔍 Checking for processes on port 3000...')
try {
  const result = execSync('lsof -ti:3000', { encoding: 'utf8' }).trim()
  if (result) {
    console.log(`⚠️  Process found on port 3000: PID ${result}`)
    console.log('💡 You may need to kill this process first:')
    console.log(`   kill -9 ${result}`)
  } else {
    console.log('✅ Port 3000 is available')
  }
} catch (error) {
  console.log('✅ Port 3000 appears to be available')
}

// Check Vite config
console.log('\n📝 Checking Vite configuration...')
if (existsSync('vite.config.js')) {
  console.log('✅ vite.config.js found')
  const config = readFileSync('vite.config.js', 'utf8')
  
  if (config.includes('hmr:')) {
    console.log('✅ HMR configuration found')
  } else {
    console.log('⚠️  HMR configuration not found - this may cause WebSocket issues')
  }
  
  if (config.includes('host:')) {
    console.log('✅ Host configuration found')
  } else {
    console.log('⚠️  Host configuration not found')
  }
} else {
  console.log('❌ vite.config.js not found')
}

// Check environment files
console.log('\n🌍 Checking environment configuration...')
const envFiles = ['.env', '.env.development', '.env.local']
envFiles.forEach(file => {
  if (existsSync(file)) {
    console.log(`✅ ${file} found`)
  } else {
    console.log(`⚠️  ${file} not found`)
  }
})

// Provide solutions
console.log('\n🛠️  WebSocket Connection Solutions:')
console.log('=====================================')

console.log('\n1. 🔄 Restart the development server:')
console.log('   npm run dev')

console.log('\n2. 🧹 Clear cache and restart:')
console.log('   rm -rf node_modules/.vite')
console.log('   npm run dev')

console.log('\n3. 🌐 Try different host configuration:')
console.log('   Add to vite.config.js server section:')
console.log('   host: "0.0.0.0"')
console.log('   hmr: { host: "localhost", port: 3000 }')

console.log('\n4. 🔥 Force kill any processes on port 3000:')
console.log('   npx kill-port 3000')
console.log('   # or manually: lsof -ti:3000 | xargs kill -9')

console.log('\n5. 🚀 Try alternative port:')
console.log('   npm run dev -- --port 3001')

console.log('\n6. 🔧 Browser-specific fixes:')
console.log('   - Clear browser cache and cookies')
console.log('   - Disable browser extensions')
console.log('   - Try incognito/private mode')
console.log('   - Check browser console for specific errors')

console.log('\n7. 🐳 If using Docker or containers:')
console.log('   - Ensure ports are properly mapped')
console.log('   - Check firewall settings')
console.log('   - Verify network connectivity')

console.log('\n8. 🔍 Debug WebSocket connection:')
console.log('   - Open browser DevTools → Network tab')
console.log('   - Look for WebSocket connections (WS filter)')
console.log('   - Check for 101 Switching Protocols response')

console.log('\n✨ Quick Fix Command:')
console.log('=====================================')
console.log('npx kill-port 3000 && rm -rf node_modules/.vite && npm run dev')

console.log('\n📚 Additional Resources:')
console.log('- Vite HMR Guide: https://vitejs.dev/guide/api-hmr.html')
console.log('- Vite Server Options: https://vitejs.dev/config/server-options.html')

console.log('\n🎯 If issues persist, check:')
console.log('- Network proxy settings')
console.log('- Antivirus/firewall blocking WebSocket connections')
console.log('- Corporate network restrictions')
console.log('- VPN interference')

console.log('\n✅ Fix script completed!')
