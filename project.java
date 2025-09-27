import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// --- 1. Product (Encapsulation) ---

class Product {
    private String name;
    private double price;
    private int quantity;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getLineTotal() {
        return price * quantity;
    }

    @Override
    public String toString() {
        return "Product: " + name + ", Price: $" + String.format("%.2f", price) + ", Quantity: " + quantity;
    }
}

// ------------------------------------
// --- 2. Discount Strategies (Polymorphism) ---

interface DiscountStrategy {
    double applyDiscount(double totalAmount, List<Product> products);
}

class FestiveDiscount implements DiscountStrategy {
    @Override
    public double applyDiscount(double totalAmount, List<Product> products) {
        double discountRate = 0.10;
        return totalAmount * discountRate;
    }
}

class BulkDiscount implements DiscountStrategy {
    @Override
    public double applyDiscount(double totalAmount, List<Product> products) {
        boolean qualifiesForBulk = false;
        for (Product product : products) {
            if (product.getQuantity() > 5) {
                qualifiesForBulk = true;
                break;
            }
        }

        if (qualifiesForBulk) {
            double discountRate = 0.20;
            return totalAmount * discountRate;
        }
        return 0.0;
    }
}

// ------------------------------------
// --- 3. Payment Interface ---

interface Payment {
    void pay(double amount);
}

class BasicPayment implements Payment {
    @Override
    public void pay(double amount) {
        // Payment logic placeholder
    }
}

// ------------------------------------
// --- 4. ShoppingCartSystem (Main Logic) ---

public class ShoppingCartSystem {

    private List<Product> cartProducts = new ArrayList<>();
    private DiscountStrategy discountStrategy;
    private Payment paymentMethod = new BasicPayment();

    public void addProduct(Product product) {
        cartProducts.add(product);
    }

    public void setDiscountStrategy(String type) {
        if (type.equalsIgnoreCase("festive")) {
            this.discountStrategy = new FestiveDiscount();
        } else if (type.equalsIgnoreCase("bulk")) {
            this.discountStrategy = new BulkDiscount();
        } else {
            this.discountStrategy = (total, products) -> 0.0;
        }
    }

    private double calculateTotal() {
        double subTotal = 0.0;
        for (Product product : cartProducts) {
            subTotal += product.getLineTotal();
        }
        return subTotal;
    }

    public void checkout() {
        double subTotal = calculateTotal();
        double discountAmount = 0.0;

        if (discountStrategy != null) {
            discountAmount = discountStrategy.applyDiscount(subTotal, cartProducts);
        }

        double finalAmount = subTotal - discountAmount;

        System.out.println("\n--- Checkout Details ---");
        for (Product product : cartProducts) {
            System.out.println(product.toString());
        }

        System.out.println("-------------------------");
        System.out.println("Subtotal: $" + String.format("%.2f", subTotal));
        System.out.println("Discount Applied: $" + String.format("%.2f", discountAmount));
        System.out.println("-------------------------");
        
        paymentMethod.pay(finalAmount); 

        System.out.println("Total Amount Payable: $" + String.format("%.2f", finalAmount));
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ShoppingCartSystem cart = new ShoppingCartSystem();

        try {
            System.out.print("Enter number of products: ");
            int N = scanner.nextInt();
            scanner.nextLine();

            for (int i = 0; i < N; i++) {
                System.out.print("Enter details for product " + (i + 1) + " (Name Price Quantity): ");
                String line = scanner.nextLine();
                String[] parts = line.split(" ");

                if (parts.length < 3) {
                    System.err.println("Invalid input format. Skipping product.");
                    i--; 
                    continue;
                }

                String name = parts[0];
                double price = Double.parseDouble(parts[1]);
                int quantity = Integer.parseInt(parts[2]);

                cart.addProduct(new Product(name, price, quantity));
            }

            System.out.print("Enter discount type (festive or bulk): ");
            String discountType = scanner.nextLine();
            
            cart.setDiscountStrategy(discountType);

            cart.checkout();

        } catch (java.util.InputMismatchException | NumberFormatException e) {
            System.err.println("\nError: Invalid input format for numbers. Please check your input values.");
        } finally {
            scanner.close();
        }
    }
}