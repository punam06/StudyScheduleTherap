<template>
  <div class="container mt-4">
    <div class="row mb-4">
      <div class="col-12">
        <h2 class="bengali-title">
          <i class="fas fa-users me-2"></i>
          অধ্যয়ন সঙ্ঘ - Vue Study Groups
        </h2>
        <p class="text-muted">Powered by Vue.js</p>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="d-flex justify-content-center mt-5">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>

    <!-- Groups List -->
    <div v-else>
      <div class="row mb-3">
        <div class="col-md-4">
          <input
            v-model="searchTerm"
            @input="filterGroups"
            type="text"
            class="form-control"
            placeholder="Search groups..."
          />
        </div>
        <div class="col-md-2">
          <button @click="createNewGroup" class="btn btn-primary">
            <i class="fas fa-plus me-1"></i>New Group
          </button>
        </div>
      </div>

      <div class="row">
        <div v-for="group in filteredGroups" :key="group.id" class="col-md-6 mb-3">
          <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
              <h5 class="mb-0">{{ group.name }}</h5>
              <span class="badge bg-info">{{ group.memberCount }} members</span>
            </div>
            <div class="card-body">
              <p class="card-text">{{ group.description }}</p>
              <div class="mb-2">
                <small class="text-muted">
                  <i class="fas fa-calendar me-1"></i>
                  Created: {{ formatDate(group.createdAt) }}
                </small>
              </div>
              <div class="mb-2">
                <small class="text-muted">
                  <i class="fas fa-user me-1"></i>
                  Admin: {{ group.adminName }}
                </small>
              </div>
              <div class="d-flex gap-2">
                <button @click="joinGroup(group.id)" class="btn btn-sm btn-success">
                  Join Group
                </button>
                <button @click="viewGroup(group.id)" class="btn btn-sm btn-info">
                  View Details
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-if="filteredGroups.length === 0" class="text-center mt-5">
        <i class="fas fa-users fa-3x text-muted mb-3"></i>
        <h4>No study groups found</h4>
        <p class="text-muted">Create a new group to get started!</p>
        <button @click="createNewGroup" class="btn btn-primary">
          <i class="fas fa-plus me-1"></i>Create First Group
        </button>
      </div>
    </div>

    <!-- Success Message -->
    <div v-if="message" class="alert alert-success alert-dismissible fade show mt-3">
      {{ message }}
      <button type="button" class="btn-close" @click="message = ''"></button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'StudyGroups',
  data() {
    return {
      groups: [],
      filteredGroups: [],
      loading: true,
      searchTerm: '',
      message: ''
    };
  },
  mounted() {
    this.fetchGroups();
  },
  methods: {
    async fetchGroups() {
      try {
        const response = await fetch('/api/groups');
        this.groups = await response.json();
        this.filteredGroups = [...this.groups];
      } catch (error) {
        console.error('Error fetching groups:', error);
      } finally {
        this.loading = false;
      }
    },
    filterGroups() {
      if (!this.searchTerm.trim()) {
        this.filteredGroups = [...this.groups];
      } else {
        this.filteredGroups = this.groups.filter(group =>
          group.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
          group.description.toLowerCase().includes(this.searchTerm.toLowerCase())
        );
      }
    },
    async joinGroup(groupId) {
      try {
        const response = await fetch(`/api/groups/${groupId}/join`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          }
        });

        if (response.ok) {
          this.message = 'Successfully joined the group!';
          this.fetchGroups(); // Refresh the list
        } else {
          throw new Error('Failed to join group');
        }
      } catch (error) {
        console.error('Error joining group:', error);
        this.message = 'Error joining group. Please try again.';
      }
    },
    viewGroup(groupId) {
      window.location.href = `/groups/${groupId}`;
    },
    createNewGroup() {
      window.location.href = '/groups/new';
    },
    formatDate(dateString) {
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    }
  }
};
</script>

<style scoped>
.bengali-title {
  font-family: 'Noto Sans Bengali', sans-serif;
  font-weight: 600;
}

.card {
  transition: transform 0.2s ease-in-out;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.spinner-border {
  width: 3rem;
  height: 3rem;
}
</style>
