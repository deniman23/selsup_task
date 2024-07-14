package org.example;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CrptApi {
    private final TimeUnit timeUnit;
    private final int requestLimit;
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final Lock lock = new ReentrantLock();
    private long lastResetTime;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        this.lastResetTime = System.currentTimeMillis();
    }

    public void createDocument(Document document, String signature) throws InterruptedException {
        lock.lock();
        try {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastResetTime;

            if (elapsedTime > timeUnit.toMillis(1)) {
                requestCount.set(0);
                lastResetTime = currentTime;
            }

            while (requestCount.get() >= requestLimit) {
                Thread.sleep(100); // Ждем, чтобы не превысить лимит запросов
                currentTime = System.currentTimeMillis();
                elapsedTime = currentTime - lastResetTime;

                if (elapsedTime > timeUnit.toMillis(1)) {
                    requestCount.set(0);
                    lastResetTime = currentTime;
                }
            }

            requestCount.incrementAndGet();
        } finally {
            lock.unlock();
        }

        // Имитация вызова API
        System.out.println("Документ создан: " + document + ", Подпись: " + signature);
    }

    public static class Document {
        // Поля документа
        public String participantInn;
        public String docId;
        public String docStatus;
        public String docType;
        public boolean importRequest;
        public String ownerInn;
        public String producerInn;
        public String productionDate;
        public String productionType;
        public Product[] products;
        public String regDate;
        public String regNumber;

        public static class Product {
            // Поля продукта
            public String certificateDocument;
            public String certificateDocumentDate;
            public String certificateDocumentNumber;
            public String ownerInn;
            public String producerInn;
            public String productionDate;
            public String tnvedCode;
            public String uitCode;
            public String uituCode;
        }
    }

    public static void main(String[] args) {
        try {
            CrptApi api = new CrptApi(TimeUnit.MINUTES, 10);
            Document document = new Document();
            document.participantInn = "1234567890";
            document.docId = "doc123";
            document.docStatus = "NEW";
            document.docType = "LP_INTRODUCE_GOODS";
            document.importRequest = true;
            document.ownerInn = "0987654321";
            document.producerInn = "1122334455";
            document.productionDate = "2020-01-23";
            document.productionType = "TYPE";
            document.products = new Document.Product[]{new Document.Product()};
            document.products[0].certificateDocument = "cert123";
            document.products[0].certificateDocumentDate = "2020-01-23";
            document.products[0].certificateDocumentNumber = "certNum123";
            document.products[0].ownerInn = "0987654321";
            document.products[0].producerInn = "1122334455";
            document.products[0].productionDate = "2020-01-23";
            document.products[0].tnvedCode = "tnved123";
            document.products[0].uitCode = "uit123";
            document.products[0].uituCode = "uitu123";
            document.regDate = "2020-01-23";
            document.regNumber = "reg123";

            api.createDocument(document, "signature123");
            System.out.println("Документ создан успешно");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
