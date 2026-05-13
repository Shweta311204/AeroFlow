CREATE DATABASE aeroflow;

USE aeroflow;

CREATE TABLE flights (
    id INT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(20) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    departure_time VARCHAR(5) NOT NULL,
    price DOUBLE NOT NULL
);
