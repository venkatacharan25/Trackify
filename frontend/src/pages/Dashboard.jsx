import React, { useEffect, useState } from 'react';
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip, Legend } from 'recharts';
import { ArrowUpRight, TrendingUp, CreditCard, Activity } from 'lucide-react';
import api from '../api/axios';

const COLORS = ['#8b5cf6', '#10b981', '#f59e0b', '#ef4444', '#3b82f6', '#ec4899'];

const Dashboard = () => {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedMonth, setSelectedMonth] = useState('');

  useEffect(() => {
    const fetchSummary = async () => {
      try {
        const response = await api.get('/expenses/summary');
        setSummary(response.data);
        
        // Auto-select the latest month
        if (response.data.monthlySummary?.length > 0) {
          const latest = [...response.data.monthlySummary].sort((a,b) => b.month.localeCompare(a.month))[0].month;
          setSelectedMonth(latest);
        }
      } catch (err) {
        console.error("Failed to fetch summary", err);
      } finally {
        setLoading(false);
      }
    };
    fetchSummary();
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      </div>
    );
  }

  if (!summary || !summary.monthlySummary) {
    return (
      <div className="flex flex-col items-center justify-center h-full text-gray-500">
        <Activity className="w-12 h-12 mb-4 text-gray-300" />
        <p className="text-xl font-medium">No financial data available yet.</p>
        <p className="text-sm mt-2">Add your first expense to get started!</p>
      </div>
    );
  }

  // Get data for selected month
  const currentMonthSummary = (summary && summary.monthlySummary) ? summary.monthlySummary.find(m => m.month === selectedMonth) : null;
  
  // Get data for previous month (for percentage calculation)
  const sortedMonths = (summary && summary.monthlySummary) ? [...summary.monthlySummary].sort((a,b) => a.month.localeCompare(b.month)) : [];
  const selectedIndex = sortedMonths.findIndex(m => m.month === selectedMonth);
  const previousMonthSummary = selectedIndex > 0 ? sortedMonths[selectedIndex - 1] : null;

  const categoryData = currentMonthSummary?.categorySummary 
    ? currentMonthSummary.categorySummary.map((c) => ({ name: c.categoryName, value: c.totalAmount }))
    : [];

  const calculatePercentChange = () => {
    if (!currentMonthSummary || !previousMonthSummary) return null;
    const current = currentMonthSummary.totalAmount;
    const previous = previousMonthSummary.totalAmount;
    if (previous === 0) return null;
    const percent = ((current - previous) / previous) * 100;
    return percent.toFixed(1);
  };
  const pctChange = calculatePercentChange();

  // Format month name for display
  const formatMonth = (monthStr) => {
    if (!monthStr) return '';
    const [year, month] = monthStr.split('-');
    return new Date(year, month - 1).toLocaleString('default', { month: 'long', year: 'numeric' });
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center bg-white p-6 rounded-2xl border border-border shadow-sm">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Dashboard Overview</h1>
          <p className="text-sm text-gray-500 mt-1">Viewing expenses for {formatMonth(selectedMonth)}</p>
        </div>
        <div className="flex items-center gap-3">
          <label className="text-sm font-medium text-gray-500">Select Month:</label>
          <select 
            value={selectedMonth} 
            onChange={(e) => setSelectedMonth(e.target.value)}
            className="px-4 py-2 bg-gray-50 border border-border rounded-xl focus:ring-2 focus:ring-primary outline-none text-sm font-semibold text-gray-700"
          >
            {sortedMonths.map(m => (
              <option key={m.month} value={m.month}>{formatMonth(m.month)}</option>
            )).reverse()}
          </select>
        </div>
      </div>

      {/* Analytics Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-border flex items-center justify-between">
          <div>
            <p className="text-sm font-medium text-gray-500 mb-1">Monthly Total</p>
            <h3 className="text-3xl font-bold text-gray-900">
              ₹{currentMonthSummary?.totalAmount?.toFixed(2) || '0.00'}
            </h3>
            {pctChange !== null && (
              <p className={`text-xs flex items-center gap-1 mt-2 font-medium ${pctChange > 0 ? 'text-danger' : 'text-success'}`}>
                <TrendingUp className={`w-3 h-3 ${pctChange <= 0 && 'rotate-180'}`} /> 
                {pctChange > 0 ? '+' : ''}{pctChange}% from {formatMonth(previousMonthSummary?.month).split(' ')[0]}
              </p>
            )}
          </div>
          <div className="w-12 h-12 rounded-xl bg-violet-100 flex items-center justify-center text-primary">
            <CreditCard className="w-6 h-6" />
          </div>
        </div>
        
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-border flex items-center justify-between">
          <div>
            <p className="text-sm font-medium text-gray-500 mb-1">Recent Transactions</p>
            <h3 className="text-3xl font-bold text-gray-900">
              {summary?.recentExpenses?.length || 0}
            </h3>
            <p className="text-xs text-gray-500 flex items-center gap-1 mt-2">
              <Activity className="w-3 h-3" /> Latest activity
            </p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-emerald-100 flex items-center justify-center text-success">
            <ArrowUpRight className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-white p-6 rounded-2xl shadow-sm border border-border flex items-center justify-between">
          <div>
            <p className="text-sm font-medium text-gray-500 mb-1">Lifetime Total</p>
            <h3 className="text-3xl font-bold text-gray-900">
              ₹{summary?.totalAmount?.toFixed(2) || '0.00'}
            </h3>
            <p className="text-xs text-gray-500 flex items-center gap-1 mt-2">
              All time spending
            </p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-blue-100 flex items-center justify-center text-blue-600">
            <TrendingUp className="w-6 h-6" />
          </div>
        </div>
      </div>

      {/* Chart Section */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-border h-[400px]">
          <h3 className="text-lg font-bold text-gray-900 mb-4">{formatMonth(selectedMonth).split(' ')[0]} Breakdown</h3>
          {categoryData.length > 0 ? (
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={categoryData}
                  cx="50%"
                  cy="50%"
                  innerRadius={80}
                  outerRadius={120}
                  paddingAngle={5}
                  dataKey="value"
                >
                  {categoryData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip formatter={(value) => `₹${value}`} />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          ) : (
            <div className="flex items-center justify-center h-full text-gray-500 italic">
              No category data for {formatMonth(selectedMonth)}
            </div>
          )}
        </div>

        <div className="bg-white p-6 rounded-2xl shadow-sm border border-border overflow-hidden">
          <h3 className="text-lg font-bold text-gray-900 mb-4">Recent Activity</h3>
          {summary?.recentExpenses?.length > 0 ? (
            <div className="space-y-4">
              {summary.recentExpenses.map((expense) => (
                <div key={expense.id} className="flex items-center justify-between p-3 rounded-xl hover:bg-gray-50 transition-colors border border-transparent hover:border-border">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center text-primary">
                      <CreditCard className="w-5 h-5" />
                    </div>
                    <div>
                      <p className="font-semibold text-gray-900 text-sm">{expense.title}</p>
                      <div className="flex items-center gap-2 text-xs text-gray-500">
                        <span>{expense.categoryName}</span>
                        <span>•</span>
                        <span>{new Date(expense.date).toLocaleDateString()}</span>
                      </div>
                    </div>
                  </div>
                  <p className="font-bold text-gray-900">₹{expense.amount?.toFixed(2)}</p>
                </div>
              ))}
            </div>
          ) : (
            <div className="flex items-center justify-center h-48 text-gray-400 italic">
              (Add some expenses to see recent activity!)
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
