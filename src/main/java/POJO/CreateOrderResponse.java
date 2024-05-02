package POJO;

public class CreateOrderResponse {

    String[] orders;
    String[] productOrderId;
    String message;

    public String[] getOrders() {
        return orders;
    }

    public void setOrders(String[] orders) {
        this.orders = orders;
    }

    public String[] getProductOrderId() {
        return productOrderId;
    }

    public void setProductOrderId(String[] productOrderId) {
        this.productOrderId = productOrderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
