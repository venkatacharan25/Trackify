# 💰 Expense Tracker - Full-Stack Application

A modern, responsive, and secure personal finance management tool built with **Spring Boot** and **React**. Track your spending, analyze monthly budgets, and visualize your financial health with interactive charts.

---

##  Features

### ** Smart Dashboard**
- **Month-Centric View**: Automatically filters data for the current month.
- **Month Selector**: Jump between different months to review past spending habits.
- **Category Breakdown**: Interactive Pie Chart showing exactly where your money goes each month.
- **Analytics Cards**: Real-time stats for Monthly Total, Lifetime Spending, and Transaction Count.
- **Trend Indicators**: Visual comparison of current month vs previous month spending (+/- %).

### ** Expense Management**
- **Full CRUD**: Create, read, update, and delete expenses easily.
- **Dynamic Categories**: Create custom categories on the fly while adding expenses.
- **Recent Activity**: A live feed of your latest 5 transactions on the dashboard.
- **Currency Support**: Fully localized with the **₹ (Rupee)** symbol.

### ** Security & Authentication**
- **JWT Authentication**: Secure login and registration using JSON Web Tokens.
- **Protected Routes**: Frontend routes are guarded; only logged-in users can access data.
- **Role-Based Access**: Backend architecture ready for ADMIN/USER roles.

---

##  Tech Stack

### **Backend**
- **Framework**: Spring Boot 4.0.5 (Java 23)
- **Security**: Spring Security + JWT (jjwt)
- **Database**: H2 (In-Memory for development)
- **Data Access**: Spring Data JPA + Hibernate 7
- **Project Model**: Maven (Layered Architecture: Controller -> Service -> Repository)

### **Frontend**
- **Framework**: React 19 (Vite)
- **Styling**: Tailwind CSS v4
- **Visualization**: Recharts (Pie charts, dynamic tooltips)
- **Icons**: Lucide React
- **API Client**: Axios with Request/Response interceptors

---

##  Project Setup & Installation

### **1. Prerequisites**
- **Java JDK 17+** (Developed with Java 23)
- **Maven** (for backend)
- **Node.js & npm** (for frontend)

### **2. Running the Backend**
```bash
cd backend
mvn spring-boot:run
```
- **API URL**: `http://localhost:8081/api`
- **H2 Console**: `http://localhost:8081/h2-console`
  - *JDBC URL*: `jdbc:h2:mem:expensetracker`
  - *Credentials*: `sa` / `password`

### **3. Running the Frontend**
```bash
cd frontend
npm install
npm run dev
```
- **Local URL**: `http://localhost:5173`

---

## Project Structure

```text
Expense tracker/
├── backend/                # Spring Boot Application
│   ├── src/main/java/      # Java Source Code
│   │   ├── config/         # Security & App configuration
│   │   ├── controller/     # API Endpoints (Auth, Expense, Category)
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── model/          # JPA Entities (User, Expense, Category)
│   │   ├── repository/     # Spring Data Repositories
│   │   └── service/        # Business Logic
│   └── src/main/resources/ # application.properties & SQL scripts
├── frontend/               # React SPA
│   ├── src/api/            # Axios instance & interceptors
│   ├── src/pages/          # Dashboard, Expenses, Auth pages
│   ├── src/components/     # Reusable UI components
│   └── src/assets/         # Static assets
└── README.md               # You are here!
```

---

## 🛡️ API Documentation

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | User registration | No |
| `POST` | `/api/auth/login` | User login (returns JWT) | No |
| `GET` | `/api/expenses/summary` | Dashboard summary data | Yes |
| `GET` | `/api/expenses` | List all expenses | Yes |
| `POST` | `/api/expenses` | Create new expense | Yes |
| `GET` | `/api/categories` | List user categories | Yes |

---

## 📝 Future Roadmap
- [ ] **MySQL Persistence**: Move from H2 to a persistent MySQL database.
- [ ] **Export to PDF/CSV**: Allow users to download their financial reports.
- [ ] **Email Alerts**: Notify users when they exceed a monthly budget limit.
- [ ] **Dark Mode**: Fully themed dark mode UI.

---

**Developed with ❤️ by Antigravity AI**
