# Portfolio Management System

## Projeyi Çalıştırma

1. **Konfigürasyon**
`src/main/resources/application.properties` dosyasını düzenleyin:
```properties
# API Anahtarları
exchangerate.api.key=YOUR_EXCHANGE_RATE_API_KEY
alphavantage.api.key=YOUR_ALPHA_VANTAGE_API_KEY
```

2. **Projeyi Derleme**
```
mvn clean install -DskipTests
```

3. **Uygulamayı Başlatma**
```
mvn spring-boot:run
```

4. **Uygulamaya Erişim**
- Web arayüzü: http://localhost:8080

## Test

1. **Portföy Oluşturma**
```
curl -X POST "http://localhost:8080/api/portfolio/create?userId=test123"
```

2. **Hisse Ekleme**
```
curl -X POST "http://localhost:8080/api/portfolio/add-stock?userId=test123&symbol=AAPL&quantity=5"
```

3. **Portföy Geçmişini Görüntüleme**
```
curl "http://localhost:8080/api/portfolio/history?userId=test123&startDate=2024-01-01&endDate=2024-12-31"
```

## Not
- Uygulama varsayılan olarak 8080 portunda çalışır
