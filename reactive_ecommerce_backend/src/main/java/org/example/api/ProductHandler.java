package org.example.api;


import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;

public class ProductHandler {
    private final MongoClient mongoClient;
        public ProductHandler(MongoClient mongoClient){
            this.mongoClient = mongoClient;
        }

        public void getAllProducts(RoutingContext context){
            mongoClient.find("products", new JsonObject(), res -> {
                if (res.succeeded()) {
                    context.response()
                            .putHeader("content-type", "application/json")
                            .end(res.result().toString());
                } else {
                    context.response().setStatusCode(500).end(res.cause().getMessage());
                }
            });
        }

        public void addProduct(RoutingContext context) {
            try {
                JsonObject product = context.getBodyAsJson();
                if (product.getString("name", "").trim().isEmpty()) {
                    context.response().setStatusCode(400).end("Product name is required and must be a non-empty string.");
                    return;
                }
                Double price = product.getDouble("price", 0.0);
                if (price <= 0) {
                    context.response().setStatusCode(400).end("Product price is required and must be a positive number.");
                    return;
                }

                mongoClient.insert("products", product, res -> {
                    if (res.succeeded()) {
                        context.response()
                                .setStatusCode(201)
                                .putHeader("content-type", "application/json")
                                .end(new JsonObject()
                                        .put("id", res.result())
                                        .put("message", "Product added successfully")
                                        .encode());
                    } else {
                        context.response().setStatusCode(500).end(res.cause().getMessage());
                    }
                });
            } catch (Exception e) {
                context.response().setStatusCode(400).end("Invalid request format: " + e.getMessage());
            }
        }

        public void getProductById(RoutingContext context) {
            String id = context.pathParam("id");
            JsonObject query = new JsonObject().put("_id", id);

            mongoClient.findOne("products", query, null, res -> {
                if (res.succeeded()) {
                    JsonObject product = res.result();
                    if (product != null) {
                        context.response()
                                .putHeader("content-type", "application/json")
                                .end(product.encode());
                    } else {
                        context.response().setStatusCode(404).end("Product not found");
                    }
                } else {
                    context.response().setStatusCode(500).end(res.cause().getMessage());
                }
            });
        }

        public void updateProduct(RoutingContext context) {
            try {
                String id = context.pathParam("id");
                JsonObject updatedProduct = context.getBodyAsJson();

                JsonObject query = new JsonObject().put("_id", id);
                JsonObject update = new JsonObject().put("$set", updatedProduct);
                mongoClient.updateCollection("products", query, update, res -> {
                    if (res.succeeded()) {
                        context.response().end("Product updated");
                    } else {
                        context.response().setStatusCode(500).end(res.cause().getMessage());
                    }
                });
            } catch(Exception e) {
                context.response().setStatusCode(400).end("Invalid JSON format: " + e.getMessage());
            }
        }

        public void deleteProduct(RoutingContext context) {
            String id = context.pathParam("id");
            JsonObject query = new JsonObject().put("_id", id);

            mongoClient.removeDocument("products", query, res -> {
                if (res.succeeded()) {
                    context.response().end("Product deleted");
                } else {
                    context.response().setStatusCode(500).end(res.cause().getMessage());
                }
            });
        }
}


