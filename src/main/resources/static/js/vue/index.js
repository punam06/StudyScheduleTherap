import { createApp } from 'vue';
import StudyGroups from './StudyGroups.vue';

// Mount Vue components when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    // Mount Vue Study Groups component
    const vueRoot = document.getElementById('vue-study-groups');
    if (vueRoot) {
        const app = createApp(StudyGroups);
        app.mount(vueRoot);
    }
});
