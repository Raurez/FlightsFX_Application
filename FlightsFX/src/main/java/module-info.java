module com.raulRamirezBotella.flightsfx {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.raulRamirezBotella.flightsfx to javafx.fxml;
    opens com.raulRamirezBotella.model to javafx.base;
    exports com.raulRamirezBotella.flightsfx;
}

