import React from 'react';
import ReactDOM from 'react-dom/client';
import StudyScheduleDashboard from './StudyScheduleDashboard.jsx';
import MUIScheduleDashboard from './MUIScheduleDashboard.jsx';

// Mount React components when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    // Mount regular React dashboard
    const reactRoot = document.getElementById('react-dashboard');
    if (reactRoot) {
        const root = ReactDOM.createRoot(reactRoot);
        root.render(<StudyScheduleDashboard />);
    }

    // Mount Material-UI dashboard
    const muiRoot = document.getElementById('mui-dashboard');
    if (muiRoot) {
        const root = ReactDOM.createRoot(muiRoot);
        root.render(<MUIScheduleDashboard />);
    }
});
