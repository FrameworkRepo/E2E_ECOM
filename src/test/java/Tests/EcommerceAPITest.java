package Tests;

import POJO.*;
import POJO.CreateOrderResponse;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.*;

public class EcommerceAPITest {

    @Test
    public void placeOrder(){

        //Login
        //Request Specification - RequestSpecBuilder - base uri, headers common details
        System.out.println("-----------------------Login-----------------------");
        RequestSpecification reqSpec = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
               .setContentType(ContentType.JSON).build();

        ResponseSpecification resSpec = new ResponseSpecBuilder().expectContentType(ContentType.JSON)
               .build();

        LoginRequest lr = new LoginRequest();
        lr.setUserEmail("Practiceforframework@gmail.com");
        lr.setUserPassword("Framework@1");

        LoginResponse loginResponse = given().relaxedHTTPSValidation().spec(reqSpec).body(lr)
               .when().post("api/ecom/auth/login")
               .then().spec(resSpec).extract().response().as(LoginResponse.class);

        String token = loginResponse.getToken();
        String userId =loginResponse.getUserId();

        System.out.println("Token Id : "+token);
        System.out.println("User Id : "+userId);

//        AddProduct
        System.out.println("-----------------------AddProduct-----------------------");
        RequestSpecification addProductReqSpecs = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                .addHeader("Authorization",token).build();

        RequestSpecification addProductBaseres = given().spec(addProductReqSpecs)
                .param("productName","Tshirt")
                .param("productAddedBy",userId)
                .param("productCategory","Fashion")
                .param("productSubCategory","Shirts")
                .param("productPrice","11500")
                .param("productDescription","Adidas Original")
                .param("productFor","men")
                .multiPart("productImage",new File("C://Users//851101//E2E_ECOM//TShirt.png"));

        AddProductResponse productResponse =addProductBaseres.when().post("api/ecom/product/add-product")
                .then().extract().response().as(AddProductResponse.class);
        String productId = productResponse.getProductId();
        System.out.println("ProductId : "+productResponse.getProductId());

        System.out.println("-----------------------AddProductSuccessful-----------------------");

        //CreateOrder
        System.out.println("-----------------------CreateOrder-----------------------");

        //Base class
        CreateOrderRequest cr = new CreateOrderRequest();
      //SubClass
        Orders orderDetails = new Orders();
        orderDetails.setCountry("india");
        orderDetails.setProductOrderedId(productResponse.getProductId());

        //List
        List<Orders> orderDetailsList = new ArrayList<Orders>();
        orderDetailsList.add(orderDetails);

         cr.setOrders(orderDetailsList);

        RequestSpecification createOrderRequestSpec = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                .addHeader("Authorization",token).setContentType(ContentType.JSON).build();

        CreateOrderResponse orderResponse= given().spec(createOrderRequestSpec)
                .body(cr)
                .when().post("api/ecom/order/create-order")
                .then().extract().response().as(CreateOrderResponse.class);

        //Array of String
        String[] ListoforderId =  orderResponse.getOrders();
        //List of Array
        List<String> order = Arrays.asList(ListoforderId);

        //view order
        System.out.println("-----------------------ViewProduct-----------------------");

        ViewOrderResponse view = null;
        String orderId = null;
        for(int i=0;i<order.size();i++) {
            view = given().spec(addProductReqSpecs)
                    .queryParam("id", order.get(i))
                    .when().get("api/ecom/order/get-orders-details")
                    .then().extract().response().as(ViewOrderResponse.class);
            
            orderId = view.getData().get_id();
            
            System.out.println("Order Id : "+view.getData().get_id());
            System.out.println("Order By : "+view.getData().getOrderById());
        }

        //Delete Product after placing order
        System.out.println("-----------------------DeleteProduct-----------------------");

        RequestSpecification deleteProductRequest= given().spec(addProductReqSpecs).pathParam("productId",productId);

        String deleteProductResponse = deleteProductRequest.when().delete("api/ecom/product/delete-product/{productId}")
                .then().extract().response().asString();

        JsonPath js = new JsonPath(deleteProductResponse);

        Assert.assertEquals("Product Deleted Successfully",js.getString("message"));

        //Delete order after placing order
        System.out.println("-----------------------DeleteOrder-----------------------");

        RequestSpecification deleteOrderRequest= given().spec(addProductReqSpecs).pathParam("orderId",orderId);

        String deleteOrderResponse = deleteOrderRequest.when().delete("api/ecom/order/delete-order/{orderId}")
                .then().extract().response().asString();

        JsonPath js1 = new JsonPath(deleteOrderResponse);

        Assert.assertEquals("Orders Deleted Successfully",js1.getString("message"));

    }
}
