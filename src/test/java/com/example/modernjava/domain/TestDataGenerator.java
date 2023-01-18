package com.example.modernjava.domain;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.abs;
import static java.lang.Math.round;

public class TestDataGenerator {

    private static final List<String> TEST_PRODUCT_LIST = List.of("Orbital Keys",
            "XPress Bottle",
            "InstaPress",
            "Uno Wear",
            "Allure Kit",
            "Swish Wallet",
            "Onovo Supply",
            "Towlee",
            "Rhino Case",
            "Mono",
            "Handy Mop",
            "ONEset",
            "Vortex Bottle",
            "Terra Shsave",
            "Gymr Kit",
            "Stickem",
            "Snap It",
            "Scruncho");

    private static List<Order> orders = null;


    /**
     * Generate a list of test orders using the test products and test customers defined in this class
     *
     * @return list of orders
     */
    public static List<Order> generateTestOrders() {
        return generateTestOrders(true);
    }

    /**
     * Generate a list of test orders using the test products and test customers defined in this class
     *
     * @param useCached if true, used cached orders if available, otherwise, generate new test orders
     * @return list of orders
     */
    public static List<Order> generateTestOrders(boolean useCached) {
        // Create the test orders on the first call and cache for later use, unless useCached
        // is false, in which case, create a new list of test orders
        Supplier<List<Order>> generateTestOrders = () -> generateTestCustomerList().stream()
                .map(customer -> Order.builder().customer(customer).items(generateOrderItemsForCustomer(customer))
                        .build()).toList();
        orders = useCached ? ObjectUtils.getIfNull(orders, generateTestOrders) : generateTestOrders.get();
        return orders;
    }

    private static List<Customer> generateTestCustomerList() {
        var customers = generateTestAddressList();
        return customers.entrySet().stream().map(e -> {
            return Customer.builder().name(e.getKey()).billingAddress(e.getValue()).shippingAddress(e.getValue())
                    .build();
        }).toList();
    }

    /**
     * Generate a list of test order items for a particular customer.
     * The order items are chosen based on characteristics of the customer such that
     * there is variety of items between customers, but the items are consistent per customer
     * unless the random flag is set, in which case the list of items is randomized per call
     *
     * @param customer the customer for which the order items will be created
     * @return list of orders
     */
    private static List<OrderItem> generateOrderItemsForCustomer(@NonNull Customer customer) {
        final List<BigDecimal> discounts = Stream.of(0.0d, 0.01d, 0.02d, 0.05d, 0.1d).map(d -> BigDecimal.valueOf(d)
                .setScale(2, RoundingMode.HALF_UP)).toList();
        return generateTestProductList().stream().filter(p -> ((customer.getName().hashCode() + p.getName()
                .hashCode()) % 2) != 0).map(p -> {
            int discountIndex = abs((customer.hashCode() + p.hashCode()) % discounts.size());
            int quantity = abs((customer.hashCode() + p.hashCode()) % 100);
            return OrderItem.builder().product(p).quantity(quantity).discount(discounts.get(discountIndex)).build();
        }).filter(o -> o.getQuantity() > 0).collect(Collectors.toList());
    }

    private static Map<String, Address> generateTestAddressList() {
        return Map.of("VMware",
                Address.builder() // VMware HQ
                        .postalCountry("US").addressLines(List.of("3401 Hillview Ave.")).administrativeArea("CA")
                        .locality("Palo Alto").postalCode("94304").build(),
                "Google",
                Address.builder() // Google HQ
                        .postalCountry("US").addressLines(List.of("1600 Amphitheatre Parkway")).administrativeArea("CA")
                        .locality("Mountain View").postalCode("94043").build(),
                "Tata",
                Address.builder() // Tata HQ
                        .postalCountry("IN").addressLines(List.of("21, D Sukhadwala Rd", "Azad Maidan"))
                        .administrativeArea("Maharashtra").locality("Mumbai").dependantLocality("Fort")
                        .postalCode("400001").build(),
                "Baidu",
                Address.builder() // Baidu HQ
                        .postalCountry("CN").addressLines(List.of("10 Shangdi 10th Street")).administrativeArea(
                                "Beijing").locality("Haidian District").postalCode("100085").build());
    }

    /**
     * Generates a list of test products based on a list of product names
     *
     * @return a list of test products
     */
    private static List<Product> generateTestProductList() {
        return TestDataGenerator.TEST_PRODUCT_LIST.stream().map(name -> {
            return Product.builder().sku(nameToSku(name)).name(name).description(nameToDescription(name)).price(
                    nameToPrice(name)).build();
        }).collect(Collectors.toList());
    }

    /**
     * Turn a product name into a product specific SKU
     *
     * @param name the product name
     * @return the generic product description
     */
    private static String nameToSku(@NonNull String name) {
        return StringUtils.truncate( // ensure max of 12 characters
                StringUtils.rightPad( // ensure min of 12 characters
                        StringUtils.deleteWhitespace(name).toUpperCase(), 12, "0"), 12);
    }

    /**
     * Turn a product name into a generic product description
     *
     * @param name the product name
     * @return the generic product description
     */
    private static String nameToDescription(@NonNull String name) {
        return "Product description for '" + name + "'";
    }

    /**
     * Turn a product name into a price with some variability
     * from other products, but consistent for each product
     *
     * @param name the product name
     * @return the price generated from the product name
     */
    private static BigDecimal nameToPrice(@NonNull String name) {
        var byteBuffer = ByteBuffer.wrap(name.getBytes());
        // Add up the integer values of the letters in the product name and divide by 100
        return BigDecimal.valueOf(Stream.generate(byteBuffer::get).limit(byteBuffer.capacity())
                .mapToInt(Byte::toUnsignedInt).sum() * 0.01d).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Normalize a value based on an input range and output range (e.g. 0..100 -> 0..1)
     *
     * @param inVal    the input value
     * @param inStart  the starting value of the input range
     * @param inEnd    the ending value of the input range
     * @param outStart the starting value of the output range
     * @param outEnd   the ending value of the output range
     * @return the normalized value
     */
    private static long normalize(long inVal, long inStart, long inEnd, long outStart, long outEnd) {
        double slope = 1.0 * (outEnd - outStart) / (inEnd - inStart);
        return outStart + round(slope * (inVal - inStart));
    }
}
