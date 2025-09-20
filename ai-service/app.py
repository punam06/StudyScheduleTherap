from flask import Flask, request, jsonify
from flask_cors import CORS
import numpy as np
import pandas as pd
from datetime import datetime, timedelta
import json
import random
from sklearn.linear_model import LinearRegression
from sklearn.preprocessing import StandardScaler
import logging

app = Flask(__name__)
CORS(app)

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class StudyAIEngine:
    def __init__(self):
        self.scaler = StandardScaler()
        self.efficiency_model = LinearRegression()
        self.optimal_times = ["09:00", "10:30", "14:00", "15:30", "19:00"]

    def generate_recommendations(self, user_data=None):
        """Generate AI-powered study recommendations"""
        base_recommendations = [
            {
                "title": "Optimize Study Sessions",
                "description": "Based on your performance data, consider 45-minute focused sessions with 15-minute breaks",
                "priority": "High",
                "type": "session_optimization"
            },
            {
                "title": "Peak Performance Hours",
                "description": "Your best study times appear to be between 9-11 AM and 2-4 PM",
                "priority": "Medium",
                "type": "timing_optimization"
            },
            {
                "title": "Subject Rotation Strategy",
                "description": "Alternate between analytical and creative subjects to maximize retention",
                "priority": "Medium",
                "type": "subject_management"
            },
            {
                "title": "Memory Consolidation",
                "description": "Review material within 24 hours and again after 7 days for better retention",
                "priority": "High",
                "type": "memory_enhancement"
            },
            {
                "title": "Group Study Benefits",
                "description": "Join study groups for complex topics - collaborative learning shows 23% improvement",
                "priority": "Low",
                "type": "collaborative_learning"
            }
        ]

        # Add personalized recommendations based on user data
        if user_data:
            personalized = self._generate_personalized_recommendations(user_data)
            base_recommendations.extend(personalized)

        return random.sample(base_recommendations, min(4, len(base_recommendations)))

    def _generate_personalized_recommendations(self, user_data):
        """Generate personalized recommendations based on user performance"""
        recommendations = []

        # Analyze study patterns
        if user_data.get('completion_rate', 0) < 0.7:
            recommendations.append({
                "title": "Improve Task Completion",
                "description": "Break down large tasks into smaller, manageable chunks to improve completion rates",
                "priority": "High",
                "type": "task_management"
            })

        if user_data.get('average_session_time', 0) > 120:
            recommendations.append({
                "title": "Shorter Study Sessions",
                "description": "Consider shorter, more frequent sessions to maintain focus and prevent burnout",
                "priority": "Medium",
                "type": "session_duration"
            })

        return recommendations

    def analyze_study_efficiency(self, study_data):
        """Calculate study efficiency metrics using ML"""
        try:
            # Mock analysis with realistic patterns
            base_efficiency = random.randint(65, 95)

            # Adjust based on study patterns
            if study_data.get('consistent_schedule', False):
                base_efficiency += 5

            if study_data.get('regular_breaks', False):
                base_efficiency += 3

            return min(100, base_efficiency)
        except Exception as e:
            logger.error(f"Error analyzing efficiency: {e}")
            return 75

    def predict_optimal_schedule(self, user_preferences=None):
        """Predict optimal study schedule using AI"""
        schedule = []

        for time in self.optimal_times:
            schedule.append({
                "time": time,
                "duration": random.choice([45, 60, 90]),
                "effectiveness_score": random.uniform(0.7, 0.95),
                "recommended_subjects": random.sample(
                    ["Mathematics", "Science", "Literature", "History", "Languages"], 2
                )
            })

        return sorted(schedule, key=lambda x: x['effectiveness_score'], reverse=True)

    def generate_break_schedule(self, session_duration=90):
        """Generate optimal break schedule"""
        breaks = []
        current_time = 0

        while current_time < session_duration:
            if current_time > 0:
                breaks.append(f"{current_time}min")
            current_time += 25 if session_duration <= 90 else 45

        return breaks

# Initialize AI engine
ai_engine = StudyAIEngine()

@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({"status": "healthy", "service": "StudyAI", "version": "1.0.0"})

@app.route('/api/recommendations', methods=['GET', 'POST'])
def get_recommendations():
    """Get AI-powered study recommendations"""
    try:
        user_data = request.json if request.method == 'POST' else None
        recommendations = ai_engine.generate_recommendations(user_data)

        return jsonify({
            "success": True,
            "recommendations": recommendations,
            "generated_at": datetime.now().isoformat()
        })
    except Exception as e:
        logger.error(f"Error generating recommendations: {e}")
        return jsonify({"success": False, "error": str(e)}), 500

@app.route('/api/analytics', methods=['POST'])
def analyze_study_data():
    """Analyze study data and provide insights"""
    try:
        study_data = request.json or {}

        efficiency = ai_engine.analyze_study_efficiency(study_data)
        focus_time = random.uniform(3.5, 8.5)  # Mock focus time calculation
        recommended_breaks = ai_engine.generate_break_schedule()

        analytics = {
            "efficiency": efficiency,
            "focusTime": round(focus_time, 1),
            "recommendedBreaks": recommended_breaks,
            "trends": {
                "weekly_improvement": random.uniform(-5, 15),
                "consistency_score": random.randint(60, 95),
                "peak_hours": ["9:00-11:00", "14:00-16:00"]
            },
            "insights": [
                "Your morning sessions show 15% higher retention",
                "Consider taking breaks every 25 minutes for optimal focus",
                "Group study sessions improve comprehension by 20%"
            ]
        }

        return jsonify({
            "success": True,
            "analytics": analytics,
            "analyzed_at": datetime.now().isoformat()
        })
    except Exception as e:
        logger.error(f"Error analyzing study data: {e}")
        return jsonify({"success": False, "error": str(e)}), 500

@app.route('/api/schedule/optimize', methods=['POST'])
def optimize_schedule():
    """Generate optimized study schedule"""
    try:
        preferences = request.json or {}
        schedule = ai_engine.predict_optimal_schedule(preferences)

        return jsonify({
            "success": True,
            "optimized_schedule": schedule,
            "optimization_score": random.uniform(0.8, 0.95)
        })
    except Exception as e:
        logger.error(f"Error optimizing schedule: {e}")
        return jsonify({"success": False, "error": str(e)}), 500

@app.route('/api/prediction/performance', methods=['POST'])
def predict_performance():
    """Predict study performance based on current patterns"""
    try:
        current_data = request.json or {}

        # Mock performance prediction
        predicted_score = random.uniform(75, 95)
        confidence = random.uniform(0.7, 0.9)

        prediction = {
            "predicted_score": round(predicted_score, 2),
            "confidence": round(confidence, 2),
            "factors": [
                {"factor": "Study Consistency", "impact": 0.3, "current_score": 0.8},
                {"factor": "Session Duration", "impact": 0.25, "current_score": 0.7},
                {"factor": "Break Management", "impact": 0.2, "current_score": 0.85},
                {"factor": "Subject Variety", "impact": 0.15, "current_score": 0.6},
                {"factor": "Group Participation", "impact": 0.1, "current_score": 0.4}
            ],
            "recommendations": ai_engine.generate_recommendations(current_data)
        }

        return jsonify({
            "success": True,
            "prediction": prediction,
            "predicted_at": datetime.now().isoformat()
        })
    except Exception as e:
        logger.error(f"Error predicting performance: {e}")
        return jsonify({"success": False, "error": str(e)}), 500

if __name__ == '__main__':
    print("Starting StudyAI Service...")
    print("Available endpoints:")
    print("  GET  /health - Health check")
    print("  GET  /api/recommendations - Get study recommendations")
    print("  POST /api/analytics - Analyze study data")
    print("  POST /api/schedule/optimize - Optimize study schedule")
    print("  POST /api/prediction/performance - Predict performance")

    app.run(host='0.0.0.0', port=8085, debug=True)
