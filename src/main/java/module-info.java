module com.example.demofinalplease {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.demofinalplease to javafx.fxml;
    exports com.example.demofinalplease;
}