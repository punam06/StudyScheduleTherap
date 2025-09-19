#!/bin/bash

# Surge Deployment Script for Study Portal
echo "🌊 Building and deploying Study Portal to Surge..."

# Create build directory structure
mkdir -p build/static
mkdir -p build/static/css
mkdir -p build/static/js
mkdir -p build/static/images

# Copy static site files
echo "📁 Copying static files..."
cp -r static-site/* build/static/

# Copy additional templates as static HTML (but don't overwrite fixed files)
echo "📄 Converting templates to static HTML..."
# Skip login.html since we have a fixed version in static-site
# cp src/main/resources/templates/auth/login.html build/static/login.html
cp src/main/resources/templates/auth/register.html build/static/register.html 2>/dev/null || echo "register.html not found, using static-site version"
cp src/main/resources/templates/dashboard/index.html build/static/dashboard.html 2>/dev/null || echo "dashboard.html not found, using static-site version"
cp src/main/resources/templates/groups/form.html build/static/create-group.html 2>/dev/null || echo "create-group.html not found, using static-site version"
cp src/main/resources/templates/groups/list.html build/static/groups.html 2>/dev/null || echo "groups.html not found, using static-site version"
cp src/main/resources/templates/sessions/form.html build/static/create-session.html 2>/dev/null || echo "create-session.html not found, using static-site version"
cp src/main/resources/templates/sessions/list.html build/static/sessions.html 2>/dev/null || echo "sessions.html not found, using static-site version"

# Update paths in HTML files for static deployment
echo "🔧 Updating paths for static deployment..."
find build/static -name "*.html" -type f -exec sed -i '' 's|th:href="@{/|href="|g' {} \;
find build/static -name "*.html" -type f -exec sed -i '' 's|th:src="@{/|src="|g' {} \;
find build/static -name "*.html" -type f -exec sed -i '' 's|href="/|href="./|g' {} \;
find build/static -name "*.html" -type f -exec sed -i '' 's|src="/|src="./|g' {} \;

# Create a simple navigation for static site
cat > build/static/nav.html << 'EOF'
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="index.html">
            <i class="fas fa-graduation-cap me-2"></i>Study Portal
        </a>
        <div class="navbar-nav ms-auto">
            <a class="nav-link" href="dashboard.html">Dashboard</a>
            <a class="nav-link" href="groups.html">Study Groups</a>
            <a class="nav-link" href="sessions.html">Sessions</a>
            <a class="nav-link" href="login.html">Login</a>
        </div>
    </div>
</nav>
EOF

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
echo "📝 You'll be prompted for:"
echo "   - Email (for Surge account)"
echo "   - Password (for Surge account)"
echo "   - Domain (e.g., study-portal-app.surge.sh)"

surge

echo "✅ Deployment complete!"
echo "🌐 Your Study Portal is now live!"
echo "📱 Access your app at the domain you specified"
