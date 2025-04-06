package org.example.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.http.HttpServerResponse;
import org.example.api.ProductHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ProductHandlerTest {

    private MongoClient mongoClient;
    private ProductHandler productHandler;
    private RoutingContext routingContext;
    private HttpServerResponse response;

    @BeforeEach
    public void setup() {
        mongoClient = mock(MongoClient.class);
        productHandler = new ProductHandler(mongoClient);

        routingContext = mock(RoutingContext.class);
        response = mock(HttpServerResponse.class);

        when(routingContext.response()).thenReturn(response);
        when(response.putHeader(anyString(), anyString())).thenReturn(response);
    }
    @Test
    public void testAddProduct_withInvalidName_shouldReturn400() {
        JsonObject badRequest = new JsonObject().put("name", "").put("price", 10.0);
        when(routingContext.getBodyAsJson()).thenReturn(badRequest);

        productHandler.addProduct(routingContext);
        verify(response).setStatusCode(400);
        verify(response).end("Product name is required and must be a non-empty string.");
    }

    @Test
    public void testAddProduct_withInvalidPrice_shouldReturn400() {
        JsonObject badRequest = new JsonObject().put("name", "Phone").put("price", -5.0);
        when(routingContext.getBodyAsJson()).thenReturn(badRequest);

        productHandler.addProduct(routingContext);
        verify(response).setStatusCode(400);
        verify(response).end("Product price is required and must be a positive number.");
    }

    @Test
    public void testAddProduct_withValidData_shouldInsertAndReturn201() {
        JsonObject goodRequest = new JsonObject()
                .put("name", "Smartwatch")
                .put("price", 99.99);

        when(routingContext.getBodyAsJson()).thenReturn(goodRequest);

        // Mock async Mongo insert callback
        doAnswer(invocation -> {
            Handler<AsyncResult<String>> handler = invocation.getArgument(2);
            AsyncResult<String> asyncResult = mock(AsyncResult.class);
            when(asyncResult.succeeded()).thenReturn(true);
            when(asyncResult.result()).thenReturn("mocked-product-id");
            handler.handle(asyncResult);
            return null;
        }).when(mongoClient).insert(eq("products"), eq(goodRequest), any());

        productHandler.addProduct(routingContext);

        verify(response).setStatusCode(201);
        verify(response).putHeader("content-type", "application/json");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(response).end(captor.capture());

        String responseBody = captor.getValue();
        assertTrue(responseBody.contains("Product added successfully"));
        assertTrue(responseBody.contains("mocked-product-id"));
    }

    @Test
    public void testAddProduct_whenMongoInsertFails_shouldReturn500() {
        JsonObject request = new JsonObject()
                .put("name", "Headphones")
                .put("price", 49.99);

        when(routingContext.getBodyAsJson()).thenReturn(request);

        doAnswer(invocation -> {
            Handler<AsyncResult<String>> handler = invocation.getArgument(2);
            AsyncResult<String> asyncResult = mock(AsyncResult.class);
            when(asyncResult.succeeded()).thenReturn(false);
            when(asyncResult.cause()).thenReturn(new RuntimeException("Mongo error"));
            handler.handle(asyncResult);
            return null;
        }).when(mongoClient).insert(eq("products"), eq(request), any());

        productHandler.addProduct(routingContext);

        verify(response).setStatusCode(500);
        verify(response).end("Mongo error");
    }
    @Test
    public void testAddProduct_withMalformedJson_shouldReturn400() {
        when(routingContext.getBodyAsJson()).thenThrow(new RuntimeException("Malformed JSON"));

        productHandler.addProduct(routingContext);

        verify(response).setStatusCode(400);
        verify(response).end(startsWith("Invalid request format:"));
    }


}
