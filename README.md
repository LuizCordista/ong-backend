# ong-donations

This project is the backend of a college assignment.

## Overview

This project is a donation management system for a non-governmental organization (NGO). It allows users to register, log in, and manage donations. The system supports both monetary and item donations, and provides features for creating, updating, deleting, and viewing donations. Additionally, it includes functionality for generating donation reports.

## Technologies Used

- **Java**: The primary programming language used.
- **Spring Boot**: A framework for building Java-based applications.
- **Spring Data JPA**: A part of the Spring Data family, used for data access.
- **Jakarta Persistence API (JPA)**: For ORM (Object-Relational Mapping).
- **Lombok**: A library to reduce boilerplate code.
- **Apache POI**: A library for reading and writing Microsoft Office documents.

## Project Structure

- [`src/main/java/luiz/cordista/ong_donations`](src/main/java/luiz/cordista/ong_donations): Contains the main application class.
- [`src/main/java/luiz/cordista/ong_donations/controller`](src/main/java/luiz/cordista/ong_donations/controller): Contains REST controllers for handling HTTP requests.
- [`src/main/java/luiz/cordista/ong_donations/model`](src/main/java/luiz/cordista/ong_donations/model): Contains entity classes representing the database tables.
- [`src/main/java/luiz/cordista/ong_donations/repository`](src/main/java/luiz/cordista/ong_donations/repository): Contains repository interfaces for data access.
- [`src/main/java/luiz/cordista/ong_donations/service`](src/main/java/luiz/cordista/ong_donations/service): Contains service classes for business logic.

## Features

- **User Authentication**: Register and log in to the system.
- **Donation Management**: Create, update, delete, and view donations.
- **Donation Reports**: Generate and download donation reports in Excel format.
- **Dashboard**: View donation statistics and charts.

## Endpoints

- **GET /donation/export/excel**: Export donations to an Excel file.
- **GET /donation/total**: Get the total donations.
- **PUT /donation/{id}**: Update a donation.
- **PUT /donation/{id}/status**: Update the status of a donation.
- **DELETE /donation/{id}**: Delete a donation.
- **POST /donation/create**: Create a new donation.
- **GET /donation/all**: Get all donations.
- **GET /donation/{id}**: Get a donation by ID.
- **GET /donation/report**: Get a donation report based on filters.

## Running the Application

To run the application, use the following command:

```sh
./mvnw spring-boot:run