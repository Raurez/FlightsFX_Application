package com.raulRamirezBotella.flightsfx;

// Importaciones necesarias

import com.raulRamirezBotella.model.Flight;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class ChartViewController {
    // Botón para volver a la ventana principal
    public Button btnBackMain;

    // El gráfico de pastel para mostrar los datos
    @FXML
    private PieChart pieChart;

    // Método para volver a la ventana principal cuando se pulsa el botón "Back"
    @FXML
    private void back() {
        // Obtiene la etapa actual y la cierra, lo que efectivamente vuelve a la vista principal
        Stage stage = (Stage) pieChart.getScene().getWindow();
        stage.close();
    }

    // Método para inicializar los datos del gráfico de pastel
    public void initData(ObservableList<Flight> flights) {
        // Crea un mapa para contar los vuelos por destino
        Map<String, Integer> flightCountByDestination = new HashMap<>();
        for (Flight flight : flights) {
            String destination = flight.getDestination();
            flightCountByDestination.put(destination, flightCountByDestination.getOrDefault(destination, 0) + 1);
        }

        // Convierte el mapa a una lista observable de datos para el gráfico de pastel
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : flightCountByDestination.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        // Establece los datos del gráfico de pastel para ser visualizados
        pieChart.setData(pieChartData);
    }
}
