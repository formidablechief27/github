import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { ArrowUpRight, ArrowDownRight, Clock } from 'lucide-react';
import { Progress } from "@/components/ui/progress";
import { BarChart, Bar, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

const AssessmentDashboard = () => {
  const [selectedSection, setSelectedSection] = useState(null);
  
  const calculateWeightedScore = (score, maxScore, weightage) => {
    return (score / maxScore) * weightage;
  };
  
  const assessmentData = {
    overall: 57,
    sections: {
      dsa: {
        score: 25,
        maxScore: 50,
        timeSpent: 45,
        avgTime: 35,
        currentRating: 1200,
        newRating: 1145,
        ratingDelta: -55,
        totalQuestions: 20,
        correctAnswers: 10,
        weightage: 50,
        questionTimeData: [
          { question: 'Q1', time: 2.5, avgTime: 2 },
          { question: 'Q2', time: 3.2, avgTime: 2.5 },
          { question: 'Q3', time: 1.8, avgTime: 2.2 },
          { question: 'Q4', time: 4.0, avgTime: 2.8 },
          { question: 'Q5', time: 2.1, avgTime: 2.3 }
        ],
        performanceHistory: [
          { month: 'Sep', rating: 1150 },
          { month: 'Oct', rating: 1200 },
          { month: 'Nov', rating: 1180 },
          { month: 'Dec', rating: 1145 }
        ]
      },
      technical: {
        score: 22,
        maxScore: 25,
        timeSpent: 30,
        avgTime: 25,
        currentRating: 1150,
        newRating: 1210,
        ratingDelta: 60,
        totalQuestions: 15,
        correctAnswers: 12,
        weightage: 25,
        questionTimeData: [
          { question: 'Q1', time: 1.5, avgTime: 2 },
          { question: 'Q2', time: 2.2, avgTime: 2.5 },
          { question: 'Q3', time: 1.8, avgTime: 2.2 },
          { question: 'Q4', time: 2.0, avgTime: 2.8 },
          { question: 'Q5', time: 1.9, avgTime: 2.3 }
        ],
        performanceHistory: [
          { month: 'Sep', rating: 1100 },
          { month: 'Oct', rating: 1120 },
          { month: 'Nov', rating: 1150 },
          { month: 'Dec', rating: 1210 }
        ]
      },
      aptitude: {
        score: 10,
        maxScore: 25,
        timeSpent: 20,
        avgTime: 30,
        currentRating: 1300,
        newRating: 1248,
        ratingDelta: -52,
        totalQuestions: 10,
        correctAnswers: 4,
        weightage: 25,
        questionTimeData: [
          { question: 'Q1', time: 3.5, avgTime: 2 },
          { question: 'Q2', time: 3.2, avgTime: 2.5 },
          { question: 'Q3', time: 2.8, avgTime: 2.2 },
          { question: 'Q4', time: 3.0, avgTime: 2.8 },
          { question: 'Q5', time: 2.9, avgTime: 2.3 }
        ],
        performanceHistory: [
          { month: 'Sep', rating: 1350 },
          { month: 'Oct', rating: 1320 },
          { month: 'Nov', rating: 1300 },
          { month: 'Dec', rating: 1248 }
        ]
      }
    }
  };

  const calculatePercentage = (score, maxScore) => {
    return Math.round((score / maxScore) * 100);
  };

  const getScoreColor = (percentage) => {
    if (percentage >= 80) return 'text-green-600';
    if (percentage >= 50) return 'text-yellow-600';
    return 'text-red-600';
  };

  const getProgressColor = (percentage) => {
    if (percentage >= 80) return 'bg-green-600';
    if (percentage >= 50) return 'bg-yellow-600';
    return 'bg-red-600';
  };

  const getSectionTitle = (section) => {
    return section.charAt(0).toUpperCase() + section.slice(1);
  };

  const RatingChange = ({ currentRating, newRating, ratingDelta }) => (
    <div className="space-y-1">
      <div className="flex items-center justify-between">
        <span className="text-sm text-gray-500">Current Rating</span>
        <span className="font-medium">{currentRating}</span>
      </div>
      <div className="flex items-center justify-between">
        <span className="text-sm text-gray-500">New Rating</span>
        <span className="font-medium">{newRating}</span>
      </div>
      <div className="flex items-center justify-end mt-1">
        {ratingDelta > 0 ? (
          <ArrowUpRight className="w-4 h-4 mr-1 text-green-600" />
        ) : (
          <ArrowDownRight className="w-4 h-4 mr-1 text-red-600" />
        )}
        <span className={`font-bold ${ratingDelta > 0 ? 'text-green-600' : 'text-red-600'}`}>
          {ratingDelta > 0 ? '+' : ''}{ratingDelta}
        </span>
      </div>
    </div>
  );

  const PerformanceGraphs = ({ data }) => (
    <div className="space-y-6 mt-4">
      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Time per Question vs Average</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={data.questionTimeData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="question" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="time" fill="#4f46e5" name="Your Time" />
                <Bar dataKey="avgTime" fill="#94a3b8" name="Average Time" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Rating History</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={data.performanceHistory}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip />
                <Line 
                  type="monotone" 
                  dataKey="rating" 
                  stroke="#4f46e5" 
                  strokeWidth={2}
                  dot={{ fill: '#4f46e5' }}
                />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </CardContent>
      </Card>
    </div>
  );

  const DetailedAnalysis = ({ section, data }) => {
    const percentage = calculatePercentage(data.score, data.maxScore);
    
    return (
      <div className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <Card>
            <CardContent className="pt-6">
              <div className="text-sm text-gray-500">Time Spent</div>
              <div className="flex items-center mt-2">
                <Clock className="w-4 h-4 mr-2" />
                <span className="text-xl font-bold">{data.timeSpent} min</span>
                <span className="text-sm ml-2 text-gray-500">/ {data.avgTime} min avg</span>
              </div>
            </CardContent>
          </Card>
          
          <Card>
            <CardContent className="pt-6">
              <div className="text-sm text-gray-500">Rating</div>
              <RatingChange 
                currentRating={data.currentRating}
                newRating={data.newRating}
                ratingDelta={data.ratingDelta}
              />
            </CardContent>
          </Card>
        </div>

        <Card>
          <CardContent className="pt-6">
            <div className="space-y-4">
              <div className="flex justify-between items-center">
                <div>
                  <div className="text-sm text-gray-500">Questions</div>
                  <div className="text-xl font-bold mt-1">
                    {data.correctAnswers} / {data.totalQuestions}
                  </div>
                </div>
                <div className="text-2xl font-bold flex flex-col items-end">
                  <span className={getScoreColor(percentage)}>
                    {data.score} / {data.maxScore}
                  </span>
                  <span className={`text-sm ${getScoreColor(percentage)}`}>
                    ({percentage}%)
                  </span>
                </div>
              </div>
              <div className="space-y-2">
                <Progress 
                  value={percentage} 
                  className={`h-2 ${getProgressColor(percentage)}`}
                />
                <div className="text-sm text-gray-500 text-right">
                  Progress: {percentage}%
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        <PerformanceGraphs data={data} />
      </div>
    );
  };

  return (
    <div className="max-w-4xl mx-auto p-6">
      <Card className="mb-6">
        <CardHeader>
          <CardTitle>Assessment Results</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-center mb-6">
            <div className="text-sm text-gray-500">Overall Score</div>
            <div className={`text-4xl font-bold ${getScoreColor(assessmentData.overall)}`}>
              {assessmentData.overall}%
            </div>
            <div className="mt-4 mx-auto max-w-md">
              <Progress 
                value={assessmentData.overall} 
                className={`h-2 ${getProgressColor(assessmentData.overall)}`}
              />
            </div>
          </div>
          
          <div className="grid grid-cols-3 gap-4">
            {Object.entries(assessmentData.sections).map(([section, data]) => {
              const percentage = calculatePercentage(data.score, data.maxScore);
              return (
                <Card 
                  key={section}
                  className="cursor-pointer hover:shadow-lg transition-shadow"
                  onClick={() => setSelectedSection(section)}
                >
                  <CardContent className="pt-6">
                    <div className="text-sm font-medium">{getSectionTitle(section)}</div>
                    <div className={`text-2xl font-bold mt-2 ${getScoreColor(percentage)}`}>
                      {data.score} / {data.maxScore}
                    </div>
                    <div className={`text-sm ${getScoreColor(percentage)} mb-2`}>
                      ({percentage}%)
                    </div>
                    <Progress 
                      value={percentage} 
                      className={`h-2 ${getProgressColor(percentage)}`}
                    />
                  </CardContent>
                </Card>
              );
            })}
          </div>
        </CardContent>
      </Card>

      <Dialog open={!!selectedSection} onOpenChange={() => setSelectedSection(null)}>
        <DialogContent className="max-w-3xl">
          <DialogHeader>
            <DialogTitle>
              {selectedSection && getSectionTitle(selectedSection)} Analysis
            </DialogTitle>
          </DialogHeader>
          {selectedSection && (
            <DetailedAnalysis 
              section={selectedSection}
              data={assessmentData.sections[selectedSection]}
            />
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default AssessmentDashboard;
