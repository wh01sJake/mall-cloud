# WebSocket Connection Troubleshooting Guide

## 🚨 Problem: WebSocket connection to 'ws://localhost:3000/' failed

This error occurs when Vite's Hot Module Replacement (HMR) cannot establish a WebSocket connection to the development server.

## 🔧 Quick Fixes (Try in order)

### 1. **Restart Development Server**
```bash
# Stop the current server (Ctrl+C)
# Then restart
npm run dev
```

### 2. **Clear Cache and Restart**
```bash
npm run clean
npm run dev
```

### 3. **Kill Port and Restart**
```bash
npm run restart
# This runs: kill-port 3000 && clean && dev
```

### 4. **Try Alternative Port**
```bash
npm run dev:port
# Runs on port 3001 instead
```

### 5. **Use Host Mode**
```bash
npm run dev:host
# Allows external connections
```

## 🔍 Diagnostic Commands

### Check What's Running on Port 3000
```bash
# macOS/Linux
lsof -ti:3000

# Windows
netstat -ano | findstr :3000
```

### Run WebSocket Fix Script
```bash
npm run fix-websocket
```

### Debug Mode
```bash
npm run dev:debug
```

## 🛠️ Advanced Solutions

### 1. **Manual Port Cleanup**
```bash
# Find and kill process on port 3000
lsof -ti:3000 | xargs kill -9

# Or use the helper
npx kill-port 3000
```

### 2. **Browser-Specific Fixes**
- **Clear browser cache** (Ctrl+Shift+R or Cmd+Shift+R)
- **Disable browser extensions** temporarily
- **Try incognito/private mode**
- **Check browser console** for specific error messages

### 3. **Network Configuration**
If you're behind a corporate firewall or using a VPN:
- **Disable VPN** temporarily
- **Check proxy settings**
- **Whitelist localhost:3000** in firewall
- **Try different network** (mobile hotspot)

### 4. **Environment Variables**
Check your `.env` files:
```bash
# .env.development
VITE_DEV_SERVER_HOST=localhost
VITE_DEV_SERVER_PORT=3000
VITE_HMR_HOST=localhost
VITE_HMR_PORT=3000
```

### 5. **Vite Configuration**
Ensure `vite.config.js` has proper HMR settings:
```javascript
export default defineConfig({
  server: {
    port: 3000,
    host: 'localhost',
    hmr: {
      port: 3000,
      host: 'localhost',
      clientPort: 3000
    }
  }
})
```

## 🐳 Docker/Container Issues

If running in Docker:
```yaml
# docker-compose.yml
services:
  frontend:
    ports:
      - "3000:3000"
    environment:
      - VITE_HMR_HOST=localhost
```

## 🌐 Alternative Solutions

### 1. **Use Different Host**
```bash
vite --host 0.0.0.0 --port 3000
```

### 2. **Disable HMR** (Last resort)
```javascript
// vite.config.js
export default defineConfig({
  server: {
    hmr: false // Disables hot reload
  }
})
```

### 3. **Use Polling** (For file system issues)
```javascript
// vite.config.js
export default defineConfig({
  server: {
    watch: {
      usePolling: true
    }
  }
})
```

## 🔍 Debugging Steps

### 1. **Check Browser DevTools**
1. Open DevTools (F12)
2. Go to **Network** tab
3. Filter by **WS** (WebSocket)
4. Look for failed WebSocket connections
5. Check the error details

### 2. **Check Console Logs**
Look for these error patterns:
- `WebSocket connection failed`
- `HMR connection lost`
- `[vite] connecting...`
- `[vite] connected.`

### 3. **Verify Server Status**
```bash
curl http://localhost:3000
# Should return the HTML page
```

## 📱 Mobile/Remote Access

If accessing from mobile or remote device:
```bash
# Find your IP address
ipconfig getifaddr en0  # macOS
ip route get 1.2.3.4 | awk '{print $7}'  # Linux

# Start server with host binding
npm run dev:host

# Access via: http://YOUR_IP:3000
```

## ⚡ Performance Optimization

If WebSocket works but is slow:
```javascript
// vite.config.js
export default defineConfig({
  server: {
    hmr: {
      overlay: false // Disable error overlay
    }
  },
  optimizeDeps: {
    include: ['vue', 'vue-router', 'pinia']
  }
})
```

## 🆘 Still Having Issues?

1. **Check system resources** (CPU, memory)
2. **Update Node.js** to latest LTS version
3. **Update Vite** to latest version
4. **Try different browser**
5. **Restart your computer** (clears all ports)

## 📞 Getting Help

When reporting issues, include:
- Operating system and version
- Node.js version (`node --version`)
- Browser and version
- Complete error message
- Output of `npm run fix-websocket`

## ✅ Success Indicators

You'll know it's working when you see:
```
[vite] connected.
✨ ready in XXXms
```

And in browser console:
```
[vite] connecting...
[vite] connected.
```

---

**💡 Pro Tip**: Bookmark this guide and run `npm run fix-websocket` whenever you encounter WebSocket issues!
