# Portfolio Takip Uygulaması

## Proje Hakkında
Bu uygulama, kullanıcıların hisse senedi portfolyolarını yönetmelerini ve takip etmelerini sağlayan bir Spring Boot uygulamasıdır.

## Özellikler
- 📈 Hisse senedi fiyatlarını gerçek zamanlı takip (Yahoo Finance)
- 💱 Döviz kuru çevirisi (FreeCurrencyAPI)
- 📊 Portfolio yönetimi ve geçmiş takibi
- 📱 Kullanıcı dostu web arayüzü

## Teknolojiler
- Java 17
- Spring Boot 3.2.3
- PostgreSQL 42.7.2
- Maven

## Kurulum Adımları

### 1. Ön Gereksinimler
- Java 17 veya üzeri
- Maven
- PostgreSQL

### 2. PostgreSQL Kurulumu ve Yapılandırma

#### A. PostgreSQL Kurulumu:
- Mac için: `brew install postgresql`
- Windows için: https://www.postgresql.org/download/windows/
- Linux için: `sudo apt-get install postgresql`

#### B. PostgreSQL Servisini Başlatma:
- Mac için: 
```bash
brew services start postgresql
```
- Windows için: Servisler uygulamasından "PostgreSQL" servisini başlatın
- Linux için: 
```bash
sudo service postgresql start
```

#### C. Veritabanı ve Kullanıcı Oluşturma:
1. PostgreSQL'e bağlanın:
- Mac için:
```bash
psql postgres
```
- Windows için:
```bash
psql -U postgres
```
- Linux için:
```bash
sudo -u postgres psql
```

2. Aşağıdaki SQL komutlarını sırasıyla çalıştırın:
```sql
CREATE USER myuser WITH PASSWORD 'mypassword';
CREATE DATABASE portfolio_db;
GRANT ALL PRIVILEGES ON DATABASE portfolio_db TO myuser;
```

3. PostgreSQL konsolundan çıkmak için:
```bash
\q
```
```

### 3. Uygulama Kurulumu

1. Projeyi klonlayın:
```bash
git clone [repo-url]
cd demo
```

2. `application.yml` dosyasını düzenleyin:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/portfolio_db
    username: myuser       # Kullanıcı oluştururken yazdığınız username
    password: mypassword   # Kullanıcı oluşturuken yazdığınız password

api:
  currency:
    api-key: [freecurrencyapi-key]  # FreeCurrencyAPI'den alacağınız key
```

3. FreeCurrencyAPI Kurulumu:
   - https://app.freecurrencyapi.com/dashboard adresine gidin
   - Ücretsiz hesap oluşturun
   - Dashboard'dan API key'inizi alın

4. Uygulamayı başlatın:
```bash
mvn clean install
mvn spring-boot:run
```

## API Endpoints

### Portfolio İşlemleri
- `POST /api/portfolio/create` - Yeni portfolio oluşturma
- `POST /api/portfolio/add-stock` - Portfolio'ya hisse ekleme
- `GET /api/portfolio/history` - Portfolio geçmişini görüntüleme
- `DELETE /api/portfolio/delete` - Portfolio silme

### Finans İşlemleri
- `GET /api/finance/stock-price` - Hisse fiyatı sorgulama
- `GET /api/finance/exchange-rate` - Döviz kuru sorgulama
- `POST /api/finance/bulk-stock-prices` - Toplu hisse fiyatı sorgulama

## Test
```bash
mvn test
```

## Uygulama Kullanımı
1. `http://localhost:8080` adresine gidin
2. Portfolio oluşturmak için kullanıcı ID girin
3. Hisse senetleri ekleyin ve takip edin
4. Portfolio geçmişini görüntüleyin
