import React, { useState, useEffect } from 'react';

const StudyScheduleDashboard = () => {
    const [schedules, setSchedules] = useState([]);
    const [loading, setLoading] = useState(true);
    const [stats, setStats] = useState({
        total: 0,
        completed: 0,
        pending: 0,
        groups: 0
    });

    useEffect(() => {
        fetchSchedules();
        fetchStats();
    }, []);

    const fetchSchedules = async () => {
        try {
            const response = await fetch('/api/schedules');
            const data = await response.json();
            setSchedules(data);
        } catch (error) {
            console.error('Error fetching schedules:', error);
        } finally {
            setLoading(false);
        }
    };

    const fetchStats = async () => {
        try {
            const response = await fetch('/api/dashboard/stats');
            const data = await response.json();
            setStats(data);
        } catch (error) {
            console.error('Error fetching stats:', error);
        }
    };

    const markCompleted = async (id) => {
        try {
            await fetch(`/api/schedules/${id}/complete`, { method: 'PATCH' });
            fetchSchedules();
            fetchStats();
        } catch (error) {
            console.error('Error updating schedule:', error);
        }
    };

    if (loading) {
        return (
            <div className="d-flex justify-content-center mt-5">
                <div className="spinner-border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </div>
            </div>
        );
    }

    return (
        <div className="container mt-4">
            <div className="row mb-4">
                <div className="col-12">
                    <h2 className="bengali-title">
                        <i className="fas fa-graduation-cap me-2"></i>
                        অধ্যয়ন সঙ্ঘ - React Dashboard
                    </h2>
                    <p className="text-muted">Powered by React.js</p>
                </div>
            </div>

            {/* Stats Cards */}
            <div className="row mb-4">
                <div className="col-md-3">
                    <div className="card bg-primary text-white">
                        <div className="card-body">
                            <div className="d-flex justify-content-between">
                                <div>
                                    <h6>Total Schedules</h6>
                                    <h3>{stats.total}</h3>
                                </div>
                                <i className="fas fa-calendar-alt fa-2x"></i>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card bg-success text-white">
                        <div className="card-body">
                            <div className="d-flex justify-content-between">
                                <div>
                                    <h6>Completed</h6>
                                    <h3>{stats.completed}</h3>
                                </div>
                                <i className="fas fa-check-circle fa-2x"></i>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card bg-warning text-white">
                        <div className="card-body">
                            <div className="d-flex justify-content-between">
                                <div>
                                    <h6>Pending</h6>
                                    <h3>{stats.pending}</h3>
                                </div>
                                <i className="fas fa-clock fa-2x"></i>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card bg-info text-white">
                        <div className="card-body">
                            <div className="d-flex justify-content-between">
                                <div>
                                    <h6>Study Groups</h6>
                                    <h3>{stats.groups}</h3>
                                </div>
                                <i className="fas fa-users fa-2x"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Schedules List */}
            <div className="row">
                <div className="col-12">
                    <div className="card">
                        <div className="card-header">
                            <h5>Recent Schedules</h5>
                        </div>
                        <div className="card-body">
                            {schedules.length === 0 ? (
                                <p className="text-muted">No schedules found.</p>
                            ) : (
                                <div className="table-responsive">
                                    <table className="table table-hover">
                                        <thead>
                                            <tr>
                                                <th>Subject</th>
                                                <th>Scheduled Time</th>
                                                <th>Duration</th>
                                                <th>Priority</th>
                                                <th>Status</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {schedules.map((schedule) => (
                                                <tr key={schedule.id}>
                                                    <td>{schedule.subject}</td>
                                                    <td>{new Date(schedule.scheduledTime).toLocaleString()}</td>
                                                    <td>{schedule.duration} minutes</td>
                                                    <td>
                                                        <span className={`badge bg-${
                                                            schedule.priority === 'HIGH' ? 'danger' : 
                                                            schedule.priority === 'MEDIUM' ? 'warning' : 'info'
                                                        }`}>
                                                            {schedule.priority}
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <span className={`badge bg-${schedule.completed ? 'success' : 'secondary'}`}>
                                                            {schedule.completed ? 'Completed' : 'Pending'}
                                                        </span>
                                                    </td>
                                                    <td>
                                                        {!schedule.completed && (
                                                            <button
                                                                className="btn btn-sm btn-success me-1"
                                                                onClick={() => markCompleted(schedule.id)}
                                                            >
                                                                Mark Complete
                                                            </button>
                                                        )}
                                                        <button className="btn btn-sm btn-info">
                                                            View
                                                        </button>
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default StudyScheduleDashboard;
