# Study Portal - Render Deployment Guide

## 🚀 Deploy to Render

This guide will help you deploy the Study Portal application to Render and get a public web link.

## Prerequisites

1. **GitHub Repository**: Your code must be in a GitHub repository
2. **Render Account**: Create a free account at [render.com](https://render.com)

## Deployment Steps

### Step 1: Prepare Your Repository

1. **Commit all changes to GitHub:**
   ```bash
   git add .
   git commit -m "Complete Render deployment setup"
   git push origin main
   ```

### Step 2: Deploy on Render

1. **Go to [render.com](https://render.com)** and sign in
2. **Click "New +"** → **"Web Service"**
3. **Connect your GitHub repository**
4. **Configure the deployment:**
   - **Name**: `study-portal-app`
   - **Environment**: `Docker`
   - **Branch**: `main`
   - **Plan**: `Free` (or `Starter` for better performance)

### Step 3: Environment Variables

Set these environment variables in Render:
- `SPRING_PROFILES_ACTIVE` = `prod`
- `PORT` = `8080`
- `JAVA_OPTS` = `-Xms256m -Xmx512m`

### Step 4: Advanced Settings

- **Health Check Path**: `/actuator/health`
- **Auto-Deploy**: `Yes`

## Your Public Web Link

After deployment completes (5-10 minutes), your app will be available at:
**`https://study-portal-app.onrender.com`**

*(Replace `study-portal-app` with your chosen service name)*

## Quick Deploy Button

[![Deploy to Render](https://render.com/images/deploy-to-render-button.svg)](https://render.com/deploy?repo=https://github.com/YOUR_USERNAME/YOUR_REPO_NAME)

## Application URLs

Once deployed, your Study Portal will have these endpoints:

### 🏠 Main Pages
- **Home**: `https://your-app.onrender.com/`
- **Dashboard**: `https://your-app.onrender.com/dashboard`

### 🔐 Authentication
- **Login**: `https://your-app.onrender.com/auth/login`
- **Register**: `https://your-app.onrender.com/auth/register`
- **Profile**: `https://your-app.onrender.com/auth/profile`

### 👥 Study Groups
- **Browse Groups**: `https://your-app.onrender.com/groups`
- **Create Group**: `https://your-app.onrender.com/groups/new`

### 📅 Sessions
- **View Sessions**: `https://your-app.onrender.com/sessions`
- **Create Session**: `https://your-app.onrender.com/sessions/new`

### 🔧 Health Check
- **Health Status**: `https://your-app.onrender.com/actuator/health`

## Monitoring

- **Render Dashboard**: Monitor logs, metrics, and deployments
- **Application Logs**: Available in Render console
- **Health Monitoring**: Automatic health checks every 30 seconds

## Troubleshooting

If deployment fails:
1. Check Render logs in the dashboard
2. Verify all files are committed to GitHub
3. Ensure Dockerfile is in root directory
4. Check environment variables are set correctly

## Performance Tips

For better performance on the free tier:
- App sleeps after 15 minutes of inactivity
- First request after sleep may take 30-60 seconds
- Consider upgrading to Starter plan for always-on service

## Support

- **Render Docs**: https://render.com/docs
- **Spring Boot on Render**: https://render.com/docs/deploy-spring-boot
