# Shopme : E-Commerce Application

## Introduction
Shopme is a full-fledged e-commerce application developed using Spring Boot and MVC Architecture. The project is divided into two applications:
one for Admin to manage product listings, orders, and inventory, and another for Customers to browse products, add them to a shopping cart, and place orders.

This repository is for the Admin application.
Here is the repository for the Customer application: [Shopme Customer Application](https://github.com/001sudhanshu001/Shopme-Ecomm-FrontEnd.git)

## About the Admin Application

### Roles and Authorities

- **Admin**: Can access everything in the application.
- **Editor**: Manages categories, brands, and products.
- **Assistant**: Manages reviews.
- **Shipper**: Views products, views orders, and updates order statuses.
- **Salesperson**: Manages product prices, customers, shipping, and orders.

## Features

- **Product Management**: Add, update, delete, and manage products. Allows administrators to handle the entire product lifecycle.
- **Category Management**: Organize products into categories for easier navigation and management.
- **User Management**: Manage user accounts and roles, ensuring secure access and permissions.
- **Order Management**: View and manage customer orders, including order status updates and fulfillment.
- **Security**: Role-based access control ensures that only authorized personnel can perform administrative functions.
- **File Uploads**: Upload images seamlessly to AWS S3 for storage and accessibility. Use Presigned URLs to render the images.

## Technologies Used

- **Backend**: Spring Boot, Spring MVC
- **Frontend**: Thymeleaf
- **Database**: MySQL
- **Cloud**: AWS (S3, RDS)
- **Security**: Spring Security
- **ORM**: Hibernate / Spring Data JPA
- **Build Tools**: Maven
- **Containerization**: Docker

## Docker Image

The Docker image for the Shopme Admin application is available at: [Docker Hub - shopme-admin](https://hub.docker.com/repository/docker/sudhanshu00i/shopme-admin)

<br>
<br>
<br>
<br>