package io.automatiko.examples.orders;

public class Item {

    private String articleId;

    private String name;

    private double price;

    private double tax;

    private double quantity;

    private double totalPrice;

    public Item() {

    }

    public Item(String articleId, String name, double price, double tax, double quantity) {
        this.articleId = articleId;
        this.name = name;
        this.price = price;
        this.tax = tax;
        this.quantity = quantity;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        totalPrice = price * quantity;
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((articleId == null) ? 0 : articleId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Item other = (Item) obj;
        if (articleId == null) {
            if (other.articleId != null)
                return false;
        } else if (!articleId.equals(other.articleId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Item [articleId=" + articleId + ", name=" + name + ", price=" + price + ", tax=" + tax + ", quantity="
                + quantity + "]";
    }

}
