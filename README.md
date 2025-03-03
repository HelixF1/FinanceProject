# Finance Project

Bu proje, hisse senedi portföyü yönetimi için geliştirilmiş bir Spring Boot uygulamasıdır.

## Gereksinimler

- Java 17 veya üzeri
- Maven 3.6 veya üzeri
- Alpha Vantage API anahtarı
- ExchangeRate API anahtarı

## Kurulum

1. Projeyi klonlayın:
```bash
git clone https://github.com/HelixF1/FinanceProject.git
cd FinanceProject
```

2. `src/main/resources/application.properties` dosyasını düzenleyin ve API anahtarlarınızı ekleyin:
```properties
alpha.vantage.api.key=your_alpha_vantage_api_key
exchangerate.api.key=your_exchangerate_api_key
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

## Test

Uygulamayı test etmek için:
```bash
mvn test
```

## Teknolojiler

- Spring Boot 3.2.3
- Spring Data JPA
- H2 Database
- Maven
- Alpha Vantage API
- ExchangeRate API
