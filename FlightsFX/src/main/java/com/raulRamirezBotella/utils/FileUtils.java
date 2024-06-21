package com.raulRamirezBotella.utils;

import com.raulRamirezBotella.model.Flight;
import javafx.scene.control.Alert;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FileUtils {
    private static final String FILE_PATH = "flights.txt";
    // Permitir horas con uno o dos dígitos para la hora y el minuto
    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("dd/MM/yyyy[ H:mm][ HH:mm]")
            .toFormatter(Locale.ENGLISH);
    private static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("[H:mm][HH:mm]")
            .toFormatter(Locale.ENGLISH);

    // Datos predeterminados para el archivo flights.txt
    private static final Flight[] DEFAULT_FLIGHTS = {
            new Flight("IB601N", "Oviedo", LocalDateTime.of(2017, 10, 28, 11, 33), LocalTime.of(0, 50)),
            new Flight("RY112A", "Edinburgh", LocalDateTime.of(2017, 10, 31, 16, 5), LocalTime.of(2, 35)),
            new Flight("AA225H", "New York", LocalDateTime.of(2017, 11, 1, 10, 10), LocalTime.of(8, 12)),
            new Flight("FEB11Y", "Dubai", LocalDateTime.of(2017, 11, 12, 15, 55), LocalTime.of(6, 17)),
            new Flight("IB13U", "Santander", LocalDateTime.of(2017, 9, 30, 8, 55), LocalTime.of(0, 55)),
            new Flight("RY13P", "London", LocalDateTime.of(2017, 12, 20, 11, 15), LocalTime.of(2, 28)),
            new Flight("AA392Y", "Seattle", LocalDateTime.of(2017, 11, 30, 6, 45), LocalTime.of(12, 14))
    };

    private static boolean isEmptyFile = false; // Indica si el archivo estaba inicialmente vacío

    // Método para cargar los vuelos desde un archivo
    public static List<Flight> loadFlights() {
        List<Flight> flights = new ArrayList<>();
        Path path = Paths.get(FILE_PATH);

        try {
            if (!Files.exists(path)) {
                isEmptyFile = true; // Marcamos que el archivo estaba vacío
                createDefaultFlightsFile(); // Crear el archivo con datos predeterminados
            } else {
                List<String> lines = Files.readAllLines(path);
                if (lines.isEmpty()) {
                    isEmptyFile = true; // Marcamos que el archivo estaba vacío
                } else {
                    for (String line : lines) {
                        String[] parts = line.split(";");
                        if (parts.length == 4) {
                            try {
                                // Intentar analizar la fecha y hora con el nuevo formato flexible
                                LocalDateTime departure = LocalDateTime.parse(parts[2], DATE_TIME_FORMATTER);
                                LocalTime duration = LocalTime.parse(parts[3], TIME_FORMATTER);
                                flights.add(new Flight(parts[0], parts[1], departure, duration));
                            } catch (DateTimeParseException e) {
                                showError("Error de formato en la línea del archivo: " + line);
                            }
                        } else {
                            throw new IllegalArgumentException("Línea con formato incorrecto: " + line);
                        }
                    }
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            showError("Error al procesar el archivo: " + e.getMessage());
        }
        return flights;
    }

    // Método para guardar una lista de vuelos en un archivo
    public static void saveFlights(List<Flight> flights) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_PATH))) {
            for (Flight flight : flights) {
                writer.write(String.format("%s;%s;%s;%s%n",
                        flight.getFlightNumber(),
                        flight.getDestination(),
                        flight.getDeparture().format(DATE_TIME_FORMATTER),
                        flight.getDuration().format(TIME_FORMATTER)));
            }
        } catch (IOException e) {
            showError("Error al guardar los vuelos: " + e.getMessage());
        }
    }

    // Método que se llamará al finalizar el programa para guardar los datos predeterminados si el archivo estaba vacío
    public static void saveDefaultDataIfNeeded() {
        if (isEmptyFile) {
            saveFlights(Arrays.asList(DEFAULT_FLIGHTS));
        }
    }

    // Método para registrar un shutdown hook
    public static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(FileUtils::saveDefaultDataIfNeeded));
    }

    // Método para mostrar mensajes de error
    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Método para crear el archivo flights.txt con datos predeterminados
    private static void createDefaultFlightsFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_PATH))) {
            for (Flight flight : DEFAULT_FLIGHTS) {
                String line = String.format("%s;%s;%s;%s%n",
                        flight.getFlightNumber(),
                        flight.getDestination(),
                        flight.getDeparture().format(DATE_TIME_FORMATTER),
                        flight.getDuration().format(TIME_FORMATTER));
                writer.write(line);
            }
        } catch (IOException e) {
            showError("Error al crear el archivo de vuelos: " + e.getMessage());
        }
    }
}
