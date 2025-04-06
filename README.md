
# Vert.x Product Service API 
I implemented a simple RESTful API for managing products using Vert.x backend, MongoDB for product storage, input validation, CRUD operations and Swagger for API documentation. 

# Features
CRUD Operations for products (Create, Read, Update, Delete)

MongoDB integration for data storage

Input validation for product name and price

Swagger UI for API documentation and easy exploration of the endpoints


Unit Tests with JUnit and Mockito for the backend logic

CI/CD pipeline for automatic testing and deployment (using GitHub Actions)

# Technologies Used
Java: Backend programming language

Vert.x: Framework for building reactive and asynchronous applications

MongoDB: Database for storing product information

Swagger/OpenAPI: For API documentation

JUnit/Mockito: For unit testing and mocking dependencies

# API Endpoints
GET /products: Get all products

POST /products: Add a new product

GET /products/{id}: Get a product by ID

PUT /products/{id}: Update a product by ID

DELETE /products/{id}: Delete a product by ID

# Request and Response Formats
POST /products:

Request body:

json
Copy code
{
  "name": "Product A",
  "price": 10.99
}
Response:

json
Copy code
{
  "id": "product_id",
  "message": "Product added successfully"
}
GET /products/{id}:

Response:

json
Copy code
{
  "_id": "product_id",
  "name": "Product A",
  "price": 10.99
}
# Unit Testing
This project includes unit tests for the ProductHandler class, ensuring that the core logic works as expected. To run the tests:

bash
Copy code
mvn test

# CI/CD Pipeline
A GitHub Actions workflow is set up to automatically run tests and build the project on every push to the repository.

# Setup Instructions

1. Clone the Repository
bash
Copy code
git clone https://github.com/your-username/vertx-product-service.git
cd vertx-product-service

2. Set Up MongoDB
You can either run MongoDB locally or use Docker to spin up a containerized MongoDB instance.


3. Build the Project
bash
Copy code
mvn clean install

4. Run the Application
bash
Copy code
mvn exec:java
The application will start on http://localhost:8081.

5. Access Swagger UI
Once the application is running, you can access the API documentation at:
