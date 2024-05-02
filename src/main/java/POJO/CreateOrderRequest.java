package POJO;

import java.util.List;

public class CreateOrderRequest {

    //List of array
    List<Orders> orders;

    public List<Orders> getOrders() {
        return orders;
    }

    public void setOrders(List<Orders> orders) {
        this.orders = orders;
    }

    //    Request payload-->
//    {
//        "orders": [
//        {
//            "country": "India",
//                "productOrderedId": "6581ca399fd99c85e8ee7f45"
//        }
//    ]
//    }
}
