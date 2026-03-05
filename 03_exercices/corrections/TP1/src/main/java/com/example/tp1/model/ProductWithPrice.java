package com.example.tp1.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProductWithPrice {
    private Product product;
    private BigDecimal originalPrice;
    private Integer discountPercentage;
    private BigDecimal finalPrice;

    public ProductWithPrice() {}

    public ProductWithPrice(Product product, Integer discountPercentage) {
        this.product = product;
        this.originalPrice = product.getPrice();
        this.discountPercentage = discountPercentage;
        this.finalPrice = calculateFinalPrice();
    }

    private BigDecimal calculateFinalPrice() {
        if (discountPercentage == null || discountPercentage == 0) {
            return originalPrice;
        }
        BigDecimal discountFactor = BigDecimal.ONE
                .subtract(BigDecimal.valueOf(discountPercentage).divide(BigDecimal.valueOf(100)));
        return originalPrice.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
    }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }

    public Integer getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Integer discountPercentage) { this.discountPercentage = discountPercentage; }

    public BigDecimal getFinalPrice() { return finalPrice; }
    public void setFinalPrice(BigDecimal finalPrice) { this.finalPrice = finalPrice; }

    @Override
    public String toString() {
        return "ProductWithPrice{product=" + product.getName()
                + ", originalPrice=" + originalPrice
                + ", discount=" + discountPercentage + "%"
                + ", finalPrice=" + finalPrice + "}";
    }
}
