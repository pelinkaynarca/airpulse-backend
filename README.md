# AirPulse - Air Quality Monitoring System

## 🌍 Project Overview

AirPulse is a real-time air quality monitoring and anomaly detection system that:
- Collects air quality measurements (PM2.5, PM10, NO2, SO2, O3)
- Detects anomalies using WHO thresholds and sudden increase algorithms
- Provides RESTful APIs for data access and analysis
- Processes data asynchronously with RabbitMQ
- Stores data in PostgreSQL (with TimescaleDB ready for future enhancements)

## 🏗️ System Architecture

```
┌─────────────────────┐     ┌─────────────────────┐     ┌─────────────────────┐
│   Python Scripts    │────▶│     RabbitMQ        │────▶│   Spring Boot API   │
│  (Data Simulation)  │     │  (Message Queue)    │     │    (Backend)        │
└─────────────────────┘     └─────────────────────┘     └──────────┬──────────┘
                                                                   │
                                                        ┌──────────▼──────────┐
                                                        │    PostgreSQL       │
                                                        │   (TimescaleDB)     │
                                                        └─────────────────────┘
```

### Components

- **Spring Boot Backend**: RESTful API with anomaly detection algorithms
- **PostgreSQL with TimescaleDB**: Data storage (time-series features planned)
- **RabbitMQ**: Asynchronous message processing
- **Python Scripts**: Data generation and testing tools

## 🛠️ Technology Stack

### Backend
- **Java 17 & Spring Boot 3.4.4**: Modern Java framework for building REST APIs quickly, lots of built-in features
- **MapStruct**: Automatically converts between different object types (DTOs ↔ Entities)
- **Lombok**: Generates common code like getters/setters to keep code clean

### Database
- **PostgreSQL 17**: Reliable database for storing air quality data, handles complex queries well
- **TimescaleDB**: Time-series extension (time-series features planned)
- **JPA/Hibernate**: Handles database operations without writing SQL

### Messaging
- **RabbitMQ 4.0**: Queue system that processes measurements asynchronously
    - Prevents data loss during high traffic
    - Ensures measurements are processed even if the system is busy
    - Allows the API to respond quickly

### Documentation & Tools
- **Swagger/OpenAPI**: Auto-generated API documentation with testing interface
- **Docker & Docker Compose**: Packages everything into containers for easy deployment, makes it easy to run the same setup on any computer
- **Maven**: Manages Java dependencies and builds the application

## 🚀 Quick Start

1. **Clone the repository**
```bash
git clone https://github.com/pelinkaynarca/airpulse-backend
cd airpulse
```

2. **Setup environment**
```bash
cp .env.example .env
# Edit .env with your passwords
```

3. **Start services**
```bash
docker-compose up -d
```

4. **Verify services are running**
```bash
docker-compose ps
```

6. **Access the application**
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- RabbitMQ: http://localhost:15672

## 📊 Mock Data Setup

The project includes sample data in `scripts/mock_data.sql` containing:
- Air quality measurements from major cities worldwide
- Pre-detected anomalies (WHO threshold violations, sudden increases)
- Historical data for testing time-based queries

### Loading Mock Data

**Option 1: Command Line**
```bash
cat scripts/mock_data.sql | docker-compose exec -T timescaledb psql -U airpulse_user -d airpulse_db
```

**Option 2: Database Client (pgAdmin, DBeaver, etc.)**
1. Connect to `localhost:5433`
2. Database: `airpulse_db`
3. Username: `airpulse_user`
4. Run the `scripts/mock_data.sql` file

**Option 3: Direct psql**
```bash
docker-compose exec timescaledb psql -U airpulse_user -d airpulse_db -f /path/to/mock_data.sql
```

## 📚 API Documentation

Access the full API documentation at http://localhost:8080/swagger-ui/index.html

### Key Endpoints

**Air Quality Measurements**
- `POST /api/air-quality-measurements` - Add new measurement
- `GET /api/air-quality-measurements/all` - Get all measurements
- `GET /api/air-quality-measurements/latest?limit=10` - Get recent data
- `GET /api/air-quality-measurements/parameter?parameter=PM2.5` - Filter by parameter
- `GET /api/air-quality-measurements/region` - Query by location

**Anomaly Detection**
- `GET /api/anomalies/latest?limit=10` - Recent anomalies
- `GET /api/anomalies/type/{type}` - Filter by anomaly type
- `GET /api/anomalies/parameter/{parameter}` - Anomalies for specific pollutant
- `GET /api/anomalies/region` - Anomalies in geographical area

## 🐍 Data Generation Scripts

### auto-test.py
Generates continuous test data with configurable parameters.

```bash
# Basic usage
python scripts/auto-test.py

# Custom configuration
python scripts/auto-test.py --duration 300 --rate 2 --anomaly-chance 0.15 --cluster-mode
```

Options:
- `--duration`: Runtime in seconds (default: 60)
- `--rate`: Requests per second (default: 1)
- `--anomaly-chance`: Anomaly probability 0-1 (default: 0.1)
- `--cluster-mode`: Generate data around major cities

### manual-input.py
Send individual measurements for testing.

```bash
python scripts/manual-input.py 40.7128 -74.0060 PM2.5 35.5
```

### Environment Configuration

Create `.env` file with:
```env
POSTGRES_DB=airpulse_db
POSTGRES_USER=airpulse_user
POSTGRES_PASSWORD=your_password
DB_HOST=timescaledb
DB_PORT=5432

RABBITMQ_USER=rabbitmq_user
RABBITMQ_PASSWORD=your_password
```

### Building & Running

```bash
# Build and start
docker-compose up -d --build

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Reset everything
docker-compose down -v
```

## 🐛 Troubleshooting

### Common Issues

**Port conflicts**
```bash
# Check what's using port 8080
lsof -i :8080
```

**Database connection issues**
```bash
# Check database logs
docker-compose logs timescaledb

# Test connection
docker-compose exec timescaledb psql -U airpulse_user -d airpulse_db
```

**API errors**
```bash
# Check backend logs
docker-compose logs backend
```

### Useful Commands

```bash
# Access database
docker-compose exec timescaledb psql -U airpulse_user -d airpulse_db

# Monitor RabbitMQ
# Open http://localhost:15672

# Rebuild after code changes
docker-compose build backend
docker-compose restart backend
```

## 🚧 Future Enhancements

- Implement time-series specific features using TimescaleDB
- Add real-time data visualization dashboard
- Implement additional anomaly detection algorithms
- Add email/SMS alerts for critical anomalies