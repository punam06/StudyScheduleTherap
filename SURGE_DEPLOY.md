# Study Portal - Surge Deployment Guide

## 🌊 Deploy to Surge.sh

This guide will help you deploy the Study Portal as a static site to Surge and get a public web link.

## Prerequisites

1. **Node.js and npm** installed on your system
2. **Surge CLI** installed globally: `npm install -g surge`

## Quick Setup

### Step 1: Install Surge CLI

```bash
npm install -g surge
```

### Step 2: Build Static Version

```bash
# Build the static site
npm run build:static

# Or manually copy files
cp -r static-site/* build/static/
```

### Step 3: Deploy to Surge

```bash
# Navigate to static build directory
cd build/static

# Deploy to Surge (first time)
surge

# Follow prompts:
# - Email: your-email@example.com  
# - Password: create-a-password
# - Domain: study-portal-app.surge.sh (or your custom domain)
```

## Your Public Web Link

After deployment completes, your app will be available at:
**`https://study-portal-app.surge.sh`**

## Custom Domain Options

You can choose from:
- `study-portal.surge.sh`
- `your-name-study-app.surge.sh`
- `java-study-portal.surge.sh`
- Or bring your own domain

## Application Features

Your deployed Study Portal includes:

### 🏠 Main Pages
- **Home**: `https://your-app.surge.sh/`
- **Dashboard**: `https://your-app.surge.sh/ai-dashboard.html`
- **Create Schedule**: `https://your-app.surge.sh/create-schedule.html`
- **View Schedules**: `https://your-app.surge.sh/schedules.html`

### ⚡ Features
- Interactive scheduling interface
- AI-powered recommendations
- Responsive design
- Local storage for data persistence
- Modern Bootstrap UI

## Updating Your Site

```bash
# Make changes to your code
# Rebuild static files
npm run build:static

# Redeploy
cd build/static
surge --domain your-app.surge.sh
```

## Automatic Deployment Script

Use the included deployment script:
```bash
./deploy-surge.sh
```

## Custom Domain

To use your own domain:
1. Point your domain's CNAME to `na-west1.surge.sh`
2. Deploy with: `surge --domain yourdomain.com`

## Free Features
- ✅ Free hosting
- ✅ Custom domains
- ✅ HTTPS by default  
- ✅ Global CDN
- ✅ Instant deployments
- ✅ No build limits

## Support
- **Surge Docs**: https://surge.sh/help
- **Custom Domains**: https://surge.sh/help/adding-a-custom-domain
