package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
public class MainVerticle extends AbstractVerticle {
 public void start(Promise<Void> startPromise){
     vertx.createHttpServer()
             .requestHandler(req -> req.response()
                     .putHeader("content-type", "text/plain")
                     .end("Greetings from Mayowa's Vert.x HTTP Server"))
             .listen(8080, http -> {
              if (http.succeeded()){
               startPromise.complete();
               System.out.println("HTTP Server started on port  8080");
              }else {
               startPromise.fail(http.cause());
              }
             });
 }
}
