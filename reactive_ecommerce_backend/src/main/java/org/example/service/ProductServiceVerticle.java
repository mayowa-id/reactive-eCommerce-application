package org.example.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.mongo.MongoClient;
import org.example.api.ProductHandler;

public class ProductServiceVerticle extends AbstractVerticle {
    private MongoClient mongoClient;

    @Override
    public void start(Promise<Void> startPromise) {
        // Configure MongoDB client
        JsonObject config = new JsonObject()
                .put("connection_string", "mongodb://localhost:27017")
                .put("db_name", "vertxmart");
        mongoClient = MongoClient.createShared(vertx, config);

        // Create handler
        ProductHandler productHandler = new ProductHandler(mongoClient);

        // Set up router with API endpoints
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        // Enable CORS for Swagger UI
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(io.vertx.core.http.HttpMethod.GET)
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Content-Type"));

        // Expose OpenAPI spec for external Swagger UI
        router.route("/openapi.yaml").handler(ctx -> {
            ctx.response()
                    .putHeader("Content-Type", "text/yaml")
                    .putHeader("Access-Control-Allow-Origin", "*")
                    .sendFile("openapi.yaml");
        });

        // API routes
        router.get("/products").handler(productHandler::getAllProducts);
        router.post("/products").handler(productHandler::addProduct);
        router.get("/products/:id").handler(productHandler::getProductById);
        router.put("/products/:id").handler(productHandler::updateProduct);
        router.delete("/products/:id").handler(productHandler::deleteProduct);

        // Start HTTP server
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8081, http -> {
                    if (http.succeeded()) {
                        startPromise.complete();
                        System.out.println("Product Service started on port 8081 with MongoDB");
                        System.out.println("Swagger UI available at: https://petstore.swagger.io/?url=http://localhost:8081/openapi.yaml");
                    } else {
                        startPromise.fail(http.cause());
                    }
                });
    }
}