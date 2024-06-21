package com.raulRamirezBotella.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Flight {
    // Atributos de la clase
    private String flightNumber;
    private String destination;
    private LocalDateTime departure;
    private LocalTime duration;

    // Constructor con solo el número de vuelo
    public Flight(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    // Constructor con todos los atributos
    public Flight(String flightNumber, String destination, LocalDateTime departure, LocalTime duration) {
        this.flightNumber = flightNumber;
        this.destination = destination;
        this.departure = departure;
        this.duration = duration;
    }

    // Getters y setters
    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getDeparture() {
        return departure;
    }

    public void setDeparture(LocalDateTime departure) {
        this.departure = departure;
    }

    public LocalTime getDuration() {
        return duration;
    }

    public void setDuration(LocalTime duration) {
        this.duration = duration;
    }

    // Método toString (opcional pero útil para depuración)
    @Override
    public String toString() {
        return "Flight{" +
                "flightNumber='" + flightNumber + '\'' +
                ", destination='" + destination + '\'' +
                ", departure=" + departure +
                ", duration=" + duration +
                '}';
    }
}
