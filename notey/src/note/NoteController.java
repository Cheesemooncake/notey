package note;

import com.jfoenix.controls.JFXTextArea;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import utils.Const;

public class NoteController implements Initializable {

    public static JFXTextArea textStatic;
    @FXML
    private JFXTextArea text;
    @FXML
    private Label id;
    public static Label idStaitc;

    static Connection con = null;
    @FXML
    private AnchorPane AP;

    public static AnchorPane APStatic;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("NoteController -> initialize() ...");
        textStatic = text;
        idStaitc = id;
        APStatic = AP;
    }

    @FXML
    private void editAction(KeyEvent event) {
        System.out.println("NoteController -> editAction() ...");

        if (text.getText().equals("")) {
            deleteAction();
        }
        else {
            app.notey.send(Const.EDIT);
            app.notey.waitForInt();

            app.notey.send(text.getText());
            app.notey.waitForInt();

            app.notey.send(Integer.parseInt(id.getText()));
        }
    }

    @FXML
    private void delete(ActionEvent event) {
        System.out.println("NoteController -> delete() ...");
        deleteAction();
    }

    private void deleteAction() {
        app.notey.send(Const.REMOVE);
        app.notey.waitForInt();
        app.notey.send(Integer.parseInt(id.getText()));
        app.notey.waitForInt();

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/app/Home.fxml"));
            id.getScene().setRoot(root);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
