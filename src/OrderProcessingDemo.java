import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

// Інтерфейси для стратегій доставки
interface DeliveryStrategy {
    boolean validateDelivery(Order order);
    void processDelivery(Order order);
}

class CourierDeliveryStrategy implements DeliveryStrategy {
    @Override
    public boolean validateDelivery(Order order) {
        // Перевірка адреси доставки
        return order.getDeliveryAddress() != null && !order.getDeliveryAddress().isEmpty();
    }

    @Override
    public void processDelivery(Order order) {
        System.out.println("Доставка кур'єром за адресою: " + order.getDeliveryAddress());
    }
}

class PostDeliveryStrategy implements DeliveryStrategy {
    @Override
    public boolean validateDelivery(Order order) {
        // Перевірка поштового відділення
        return order.getPostOffice() != null && !order.getPostOffice().isEmpty();
    }

    @Override
    public void processDelivery(Order order) {
        System.out.println("Доставка поштою до відділення: " + order.getPostOffice());
    }
}

// Інтерфейси для стратегій оплати
interface PaymentStrategy {
    boolean validatePayment(Order order);
    void processPayment(Order order);
}

class CreditCardPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean validatePayment(Order order) {
        // Перевірка даних банківської картки
        return order.getCreditCardNumber() != null &&
                order.getCreditCardNumber().length() == 16;
    }

    @Override
    public void processPayment(Order order) {
        System.out.println("Оплата банківською карткою: " +
                order.getCreditCardNumber());
    }
}

class PayPalPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean validatePayment(Order order) {
        // Перевірка email PayPal
        return order.getPayPalEmail() != null &&
                order.getPayPalEmail().contains("@");
    }

    @Override
    public void processPayment(Order order) {
        System.out.println("Перенаправлення на сторінку PayPal: " +
                order.getPayPalEmail());
    }
}

class CashPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean validatePayment(Order order) {
        // Підтвердження оплати при доставці
        return true; // Завжди дозволено
    }

    @Override
    public void processPayment(Order order) {
        System.out.println("Оплата готівкою при доставці");
    }
}

// Клас замовлення
class Order {
    private String orderId;
    private List<Product> products;
    private DeliveryStrategy deliveryStrategy;
    private PaymentStrategy paymentStrategy;

    // Додаткові поля для різних стратегій
    private String deliveryAddress;
    private String postOffice;
    private String creditCardNumber;
    private String payPalEmail;

    public Order(List<Product> products,
                 DeliveryStrategy deliveryStrategy,
                 PaymentStrategy paymentStrategy) {
        this.orderId = UUID.randomUUID().toString();
        this.products = products;
        this.deliveryStrategy = deliveryStrategy;
        this.paymentStrategy = paymentStrategy;
    }

    // Геттери та сеттери
    public String getOrderId() { return orderId; }
    public List<Product> getProducts() { return products; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    public String getPostOffice() { return postOffice; }
    public void setPostOffice(String postOffice) {
        this.postOffice = postOffice;
    }
    public String getCreditCardNumber() { return creditCardNumber; }
    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }
    public String getPayPalEmail() { return payPalEmail; }
    public void setPayPalEmail(String payPalEmail) {
        this.payPalEmail = payPalEmail;
    }

    // Головний метод обробки замовлення
    public boolean processOrder() {
        // 1. Перевірка товарів
        if (products == null || products.isEmpty()) {
            System.out.println("Помилка: порожнє замовлення");
            return false;
        }

        // 2. Перевірка оплати
        if (!paymentStrategy.validatePayment(this)) {
            System.out.println("Помилка валідації оплати");
            return false;
        }

        // 3. Перевірка доставки
        if (!deliveryStrategy.validateDelivery(this)) {
            System.out.println("Помилка валідації доставки");
            return false;
        }

        // 4. Обробка оплати
        paymentStrategy.processPayment(this);

        // 5. Обробка доставки
        deliveryStrategy.processDelivery(this);

        // 6. Завершення замовлення
        System.out.println("Замовлення " + orderId + " успішно оброблено");
        return true;
    }
}

// Клас продукту
class Product {
    private String name;
    private double price;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
}

// Приклад використання системи
public class OrderProcessingDemo {
    public static void main(String[] args) {
        // Створення списку продуктів
        List<Product> products = Collections.unmodifiableList(Arrays.asList(
                new Product("Ноутбук", 25000),
                new Product("Навушники", 3000)
        ));

        // Замовлення з кур'єрською доставкою та оплатою банківською карткою
        Order courierCardOrder = new Order(
                products,
                new CourierDeliveryStrategy(),
                new CreditCardPaymentStrategy()
        );
        courierCardOrder.setDeliveryAddress("вул. Перемоги, 10");
        courierCardOrder.setCreditCardNumber("1234567890123456");
        courierCardOrder.processOrder();

        System.out.println("\n---\n");

        // Замовлення з поштовою доставкою та оплатою PayPal
        Order postPayPalOrder = new Order(
                products,
                new PostDeliveryStrategy(),
                new PayPalPaymentStrategy()
        );
        postPayPalOrder.setPostOffice("Відділення № 5");
        postPayPalOrder.setPayPalEmail("user@example.com");
        postPayPalOrder.processOrder();
    }
}
