module com.example.collegeproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires javafx.graphics;
    //requires pdfbox.app;
    requires org.apache.pdfbox;
    requires javafx.base;

    opens com.example.collegeproject to javafx.fxml;
    exports com.example.collegeproject;
}