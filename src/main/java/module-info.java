module com.project.hundirlaflota {
    requires javafx.controls;
    requires javafx.fxml;
    requires Java.WebSocket;
    requires org.jline;
    requires org.json;
    requires annotations;
    requires jdk.jshell;

    // Exporta el paquete Client al módulo javafx.graphics para que pueda acceder a Main
    exports com.project.hundirlaflota.Client to javafx.graphics;

    // Si necesitas abrir el paquete Client para fxml (ya que usas FXMLLoader)
    opens com.project.hundirlaflota.Client to javafx.fxml;

    exports com.project.hundirlaflota.Server;
    opens com.project.hundirlaflota.Server to javafx.fxml;
}
