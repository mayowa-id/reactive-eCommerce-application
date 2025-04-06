package org.example;

import io.vertx.core.Vertx;
import org.example.service.ProductServiceVerticle;

public class Main {
    public static void main (String [] args){
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ProductServiceVerticle());
    }
}
