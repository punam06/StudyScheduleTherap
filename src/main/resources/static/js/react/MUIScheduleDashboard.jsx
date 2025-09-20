import React, { useState, useEffect } from 'react';
import {
  Card,
  CardContent,
  Typography,
  Grid,
  Button,
  TextField,
  Box,
  Chip,
  Avatar,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  CircularProgress,
  Alert
} from '@mui/material';
import {
  Schedule,
  Group,
  Psychology,
  TrendingUp,
  School,
  Timer
} from '@mui/icons-material';

const MUIScheduleDashboard = () => {
  const [aiRecommendations, setAiRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [studyData, setStudyData] = useState({
    efficiency: 0,
    focusTime: 0,
    recommendedBreaks: []
  });

  useEffect(() => {
    fetchAIRecommendations();
    fetchStudyAnalytics();
  }, []);

  const fetchAIRecommendations = async () => {
    try {
      const response = await fetch('/api/ai/recommendations');
      const data = await response.json();
      setAiRecommendations(data);
    } catch (error) {
      console.error('Error fetching AI recommendations:', error);
    }
  };

  const fetchStudyAnalytics = async () => {
    try {
      const response = await fetch('/api/ai/analytics');
      const data = await response.json();
      setStudyData(data);
    } catch (error) {
      console.error('Error fetching study analytics:', error);
    } finally {
      setLoading(false);
    }
  };

  const generateNewRecommendations = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/ai/recommendations/generate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        }
      });
      const data = await response.json();
      setAiRecommendations(data);
    } catch (error) {
      console.error('Error generating recommendations:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress size={60} />
      </Box>
    );
  }

  return (
    <Box sx={{ flexGrow: 1, p: 3 }}>
      <Typography variant="h4" component="h1" gutterBottom sx={{
        fontFamily: 'Noto Sans Bengali, sans-serif',
        fontWeight: 600,
        mb: 3
      }}>
        <Psychology sx={{ mr: 1, verticalAlign: 'middle' }} />
        অধ্যয়ন সঙ্ঘ - AI Dashboard (Material-UI)
      </Typography>

      {/* AI Analytics Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{
            backgroundColor: '#1976d2',
            color: 'white',
            transition: 'transform 0.2s',
            '&:hover': {
              transform: 'scale(1.05)'
            }
          }}>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center">
                <Box>
                  <Typography variant="h6">Study Efficiency</Typography>
                  <Typography variant="h4">{studyData.efficiency}%</Typography>
                </Box>
                <TrendingUp fontSize="large" />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{
            backgroundColor: '#388e3c',
            color: 'white',
            transition: 'transform 0.2s',
            '&:hover': {
              transform: 'scale(1.05)'
            }
          }}>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center">
                <Box>
                  <Typography variant="h6">Focus Time</Typography>
                  <Typography variant="h4">{studyData.focusTime}h</Typography>
                </Box>
                <Timer fontSize="large" />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{
            backgroundColor: '#f57c00',
            color: 'white',
            transition: 'transform 0.2s',
            '&:hover': {
              transform: 'scale(1.05)'
            }
          }}>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center">
                <Box>
                  <Typography variant="h6">AI Recommendations</Typography>
                  <Typography variant="h4">{aiRecommendations.length}</Typography>
                </Box>
                <Psychology fontSize="large" />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{
            backgroundColor: '#7b1fa2',
            color: 'white',
            transition: 'transform 0.2s',
            '&:hover': {
              transform: 'scale(1.05)'
            }
          }}>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center">
                <Box>
                  <Typography variant="h6">Learning Score</Typography>
                  <Typography variant="h4">A+</Typography>
                </Box>
                <School fontSize="large" />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* AI Recommendations */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                <Typography variant="h6" component="h2">
                  AI Study Recommendations
                </Typography>
                <Button
                  variant="contained"
                  startIcon={<Psychology />}
                  onClick={generateNewRecommendations}
                  disabled={loading}
                >
                  Generate New
                </Button>
              </Box>

              {aiRecommendations.length === 0 ? (
                <Alert severity="info">
                  No AI recommendations available. Click "Generate New" to get personalized suggestions.
                </Alert>
              ) : (
                <List>
                  {aiRecommendations.map((recommendation, index) => (
                    <ListItem key={index} divider>
                      <ListItemAvatar>
                        <Avatar sx={{ bgcolor: 'primary.main' }}>
                          <Psychology />
                        </Avatar>
                      </ListItemAvatar>
                      <ListItemText
                        primary={recommendation.title || `Recommendation ${index + 1}`}
                        secondary={recommendation.description || 'AI-generated study suggestion'}
                      />
                      <Chip
                        label={recommendation.priority || 'Medium'}
                        color={
                          recommendation.priority === 'High' ? 'error' :
                          recommendation.priority === 'Medium' ? 'warning' : 'default'
                        }
                        size="small"
                      />
                    </ListItem>
                  ))}
                </List>
              )}
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" component="h2" gutterBottom>
                Study Analytics
              </Typography>
              <Box mb={2}>
                <Typography variant="body2" color="text.secondary">
                  Optimal Study Times
                </Typography>
                <Chip label="9:00 AM - 11:00 AM" color="primary" size="small" sx={{ mr: 1, mt: 1 }} />
                <Chip label="2:00 PM - 4:00 PM" color="primary" size="small" sx={{ mt: 1 }} />
              </Box>

              <Box mb={2}>
                <Typography variant="body2" color="text.secondary">
                  Recommended Breaks
                </Typography>
                {studyData.recommendedBreaks.map((breakTime, index) => (
                  <Chip
                    key={index}
                    label={breakTime}
                    variant="outlined"
                    size="small"
                    sx={{ mr: 1, mt: 1 }}
                  />
                ))}
              </Box>

              <Button
                variant="outlined"
                fullWidth
                startIcon={<Schedule />}
                sx={{ mt: 2 }}
              >
                View Detailed Analytics
              </Button>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default MUIScheduleDashboard;
