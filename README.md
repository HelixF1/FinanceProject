# Finance Project

Bu proje, hisse senedi portföyü yönetimi için geliştirilmiş bir Spring Boot uygulamasıdır.

## Gereksinimler

- Java 17 veya üzeri
- Maven 3.6 veya üzeri
- PostgreSQL 14 veya üzeri
- Free Currency API anahtarı (https://freecurrencyapi.com/)

## Kurulum

### PostgreSQL Kurulumu

1. PostgreSQL'i indirin ve kurun:
   - Windows: https://www.postgresql.org/download/windows/
   - macOS: `brew install postgresql`
   - Linux: `sudo apt-get install postgresql`

2. PostgreSQL servisini başlatın:
   - Windows: Otomatik başlar
   - macOS: `brew services start postgresql`
   - Linux: `sudo service postgresql start`

3. Veritabanını oluşturun:
```bash
# PostgreSQL komut satırına girin
psql -U postgres

# Veritabanını oluşturun
CREATE DATABASE portfolio_db;

# Veritabanı oluşturulduğunu kontrol edin
\l

# Çıkış yapın
\q
```

### API Anahtarı Alma

1. Free Currency API için:
   - https://freecurrencyapi.com/ adresine gidin
   - Ücretsiz hesap oluşturun
   - API anahtarınızı alın (günlük 5000 istek hakkı)

### Projeyi Klonlama ve Yapılandırma

1. Projeyi klonlayın:
```bash
git clone https://github.com/HelixF1/FinanceProject.git
cd FinanceProject
```

2. `src/main/resources/application.yml` dosyasını düzenleyin:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/portfolio_db
    username: postgres  # PostgreSQL kullanıcı adınız
    password: password # PostgreSQL şifreniz

api:
  currency:
    api-key: your_currency_api_key
```

## Çalıştırma

### Terminal ile Çalıştırma

1. Projeyi derleyin:
```bash
mvn clean install -DskipTests
```

2. Uygulamayı başlatın:
```bash
mvn spring-boot:run
```

### IDE ile Çalıştırma (IntelliJ IDEA, Eclipse, VS Code)

1. Projeyi IDE'nizde açın
2. `DemoApplication.java` dosyasını bulun (`src/main/java/com/example/demo/DemoApplication.java`)
3. Dosyayı açın ve "Run" (Çalıştır) butonuna tıklayın
   - IntelliJ IDEA: Sol taraftaki yeşil "Run" butonu veya `Shift + F10`
   - Eclipse: "Run As > Spring Boot App" seçeneği
   - VS Code: "Run and Debug" sekmesinden "Run" butonu

4. Uygulama başladıktan sonra tarayıcınızda `http://localhost:8080` adresine gidin

## API Endpoints

### Portföy İşlemleri

1. Portföy Oluşturma:
```bash
curl -X POST "http://localhost:8080/api/portfolio/create?userId=user123"
```

2. Portföye Hisse Senedi Ekleme:
```bash
curl -X POST "http://localhost:8080/api/portfolio/add-stock?userId=user123&symbol=AAPL&quantity=5&currency=USD"
```

3. Portföy Geçmişi Görüntüleme:
```bash
curl "http://localhost:8080/api/portfolio/history?userId=user123"
```

### Hisse Senedi Fiyatı Sorgulama

```bash
curl "http://localhost:8080/api/finance/stock-price?symbol=AAPL&currency=USD&date=2024-03-03"
```

## Veritabanı Şeması

Uygulama Flyway ile otomatik olarak aşağıdaki tabloları oluşturur:
- `portfolios`: Kullanıcı portföylerini tutar
- `portfolio_stocks`: Portföylerdeki hisse senetlerini tutar
- `stock_history`: Hisse senedi fiyat geçmişini tutar

## Test

Uygulamayı test etmek için:
```bash
mvn test
```

## Teknolojiler

- Spring Boot 3.2.3
- Spring Data JPA
- PostgreSQL 14+
- Maven
- Flyway (Database migration)
- Yahoo Finance API
- Free Currency API
