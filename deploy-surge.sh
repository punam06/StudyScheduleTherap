#!/bin/bash

# Surge Deployment Script for Study Portal
echo "🌊 Building and deploying Study Portal to Surge..."

# Create build directory structure
mkdir -p build/static

# Copy static site files (this contains all our working files)
echo "📁 Copying static files..."
cp -r static-site/* build/static/

# DON'T copy any template files that could overwrite our working files
echo "🚫 Skipping template copying to prevent file overwrites..."

# Deploy to Surge
echo "🚀 Deploying to Surge..."
cd build/static

# Check if surge is installed
if ! command -v surge &> /dev/null; then
    echo "❌ Surge CLI not found. Installing..."
    npm install -g surge
fi

# Deploy
echo "🌊 Starting Surge deployment..."
surge . studyscheduletherap.surge.sh

echo "✅ Deployment complete!"
echo "🌐 Your Study Portal is now live at studyscheduletherap.surge.sh"
