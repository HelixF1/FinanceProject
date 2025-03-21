# Portfolio Tracking Application

## About the Project
This is a Spring Boot application that allows users to manage and track their stock portfolios.

## Features
- 📈 Real-time stock price tracking (Yahoo Finance)
- 💱 Currency conversion (FreeCurrencyAPI)
- 📊 Portfolio management and history tracking
- 📱 User-friendly web interface

## Technologies
- Java 17
- Spring Boot 3.2.3
- PostgreSQL 42.7.2
- Maven

## Installation Steps

### 1. Prerequisites
- Java 17 or higher
- Maven
- PostgreSQL

### 2. PostgreSQL Installation and Configuration

#### A. PostgreSQL Installation:
- For Mac: `brew install postgresql`
- For Windows: https://www.postgresql.org/download/windows/
- For Linux: `sudo apt-get install postgresql`

#### B. Starting PostgreSQL Service:
- For Mac: 
```bash
brew services start postgresql
```
- For Windows: Start "PostgreSQL" service from Services application
- For Linux: 
```bash
sudo service postgresql start
```

#### C. Creating Database and User:
1. Connect to PostgreSQL:
- For Mac:
```bash
psql postgres
```
- For Windows:
```bash
psql -U postgres
```
- For Linux:
```bash
sudo -u postgres psql
```

2. Execute the following SQL commands in order:
```sql
CREATE USER myuser WITH PASSWORD 'mypassword';
CREATE DATABASE portfolio_db;
GRANT ALL PRIVILEGES ON DATABASE portfolio_db TO myuser;
```

3. To exit PostgreSQL console:
```bash
\q
```

### 3. Application Installation

1. Clone the project:
```bash
git clone [repo-url]
cd demo
```

2. Edit `application.yml` file:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/portfolio_db
    username: myuser       # Username you created
    password: mypassword   # Password you created

api:
  currency:
    api-key: [freecurrencyapi-key]  # Key from FreeCurrencyAPI
```

3. FreeCurrencyAPI Setup:
   - Go to https://app.freecurrencyapi.com/dashboard
   - Create a free account
   - Get your API key from Dashboard

4. Start the application:
```bash
mvn clean install
mvn spring-boot:run
```

## API Endpoints

### Portfolio Operations
- `POST /api/portfolio/create` - Create new portfolio
- `POST /api/portfolio/add-stock` - Add stock to portfolio
- `GET /api/portfolio/history` - View portfolio history
- `DELETE /api/portfolio/delete` - Delete portfolio

### Finance Operations
- `GET /api/finance/stock-price` - Query stock price
- `GET /api/finance/exchange-rate` - Query exchange rate
- `POST /api/finance/bulk-stock-prices` - Query bulk stock prices

## Testing
```bash
mvn test
```

## Using the Application
1. Go to `http://localhost:8080`
2. Enter user ID to create portfolio
3. Add and track stocks
4. View portfolio history
