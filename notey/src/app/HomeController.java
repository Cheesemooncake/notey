package app;

import note.NoteFX;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.Const;

public class HomeController implements Initializable {
    private static int user_id;

    private double xOffset = 0;
    private double yOffset = 0;

    public static JFXDialog aboutDialog;

    @FXML
    public FlowPane FP;

    static Connection con = null;
    @FXML
    private JFXTextField searchComBox;
    @FXML
    private StackPane AP;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu file;
    @FXML
    private MenuItem About;
    @FXML
    private MenuItem close;

    public static void setUserId(int id) {
        user_id = id;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("HomeController -> initialize() ...");
        user_id = -1;
        InitializeNotes();
        try {
            AnchorPane aboutPane = FXMLLoader.load(getClass().getResource("about.fxml"));
            aboutDialog = new JFXDialog(AP, aboutPane, JFXDialog.DialogTransition.TOP);
        } catch (IOException ex) {
            System.out.println("error in loading about.fxml file");
        }

    }

    @FXML
    private void logout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));

            Scene scene = new Scene(root);
            ((Stage) (searchComBox.getScene().getWindow())).setScene(scene);
        }
        catch (IOException e) {

        }
    }

    @FXML
    public void RootMousePressed(Event event) {
        MouseEvent e = (MouseEvent) event;
        xOffset = e.getSceneX();
        yOffset = e.getSceneY();
    }

    @FXML
    public void RootMouseDragged(Event event) {
        MouseEvent e = (MouseEvent) event;
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).setX(e.getScreenX() - xOffset);
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).setY(e.getScreenY() - yOffset);
    }

    @FXML
    private void closeWindow(Event event) {
        System.exit(0);
    }

    @FXML
    private void AddNoteAction(ActionEvent event) {
        System.out.println("HomeController -> AddNoteAction() ...");
        try {
            Parent newNoteFXML = FXMLLoader.load(getClass().getResource("/note/newNote.fxml"));
            Scene sc = new Scene(newNoteFXML);
            Stage stage = new Stage();
            stage.setScene(sc);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Создание задачи");
            stage.showAndWait();
            InitializeNotes();
            System.gc();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    @FXML
    private void searchEvent() {
        System.out.println("HomeController -> searchEvent() ...");
        if ((searchComBox.getText().trim().equals(""))) {
            InitializeNotes();
        } else {
            FP.getChildren().clear();
            List<NoteFX> noteList = search();
            int count = noteList.size();
            for (int i = 0; i < count; i++) {
                try {
                    Parent NoteFXML = FXMLLoader.load(getClass().getResource("/note/Note.fxml"));
                    note.NoteController.textStatic.setText(noteList.get(i).getText());
                    note.NoteController.idStaitc.setText(noteList.get(i).getid() + "");
                    note.NoteController.APStatic.setStyle("-fx-background-color : " + noteList.get(i).getHexaColor() + ";");
                    FP.getChildren().add(NoteFXML);
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        }
    }

    public void InitializeNotes() {
        System.out.println("HomeController -> InitializeNotes() ...");
        FP.getChildren().clear();
        List<NoteFX> noteList = select();
        int count = noteList.size();
        for (int i = 0; i < count; i++) {
            try {
                Parent NoteFXML = FXMLLoader.load(getClass().getResource("/note/Note.fxml"));
                note.NoteController.textStatic.setText(noteList.get(i).getText());
                note.NoteController.idStaitc.setText(noteList.get(i).getid() + "");
                note.NoteController.APStatic.setStyle("-fx-background-color : " + noteList.get(i).getHexaColor() + ";");

                FP.getChildren().add(NoteFXML);
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }

    public static int getNumOfNotes() {
        System.out.println("HomeController -> getNumOfNotes() ...");
        int n = 0;
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            con = DriverManager.getConnection("jdbc:hsqldb:file:db/NoteDatabase", "SA", "");
        } catch (Exception ex) {
            System.out.println(ex);
        }
        String sqlString = "SELECT COUNT(*) AS total FROM notes";
        try {
            PreparedStatement prepareStatement = con.prepareStatement(sqlString);
            ResultSet resultSet = prepareStatement.executeQuery();
            resultSet.next();
            n = resultSet.getInt("total");
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
        System.out.println("n = " + n);
        return n;
    }

    public static void insert(String text, String color) {
        System.out.println("HomeController -> insert() ...");
        app.notey.send(Const.ADD);
        app.notey.waitForInt();

        app.notey.send(text);
        app.notey.waitForInt();

        app.notey.send(color);
        app.notey.waitForInt();
    }

    public static List select() {
        System.out.println("HomeController -> select() ...");

        app.notey.send(Const.GET);

        boolean stop = false;
        List listNotes = new ArrayList<NoteFX>();

        do {
            int id = app.notey.waitForInt();
            if (id < 0) stop = true;
            else {
                app.notey.send(1);

                String text = app.notey.waitForString();
                app.notey.send(1);

                String color = app.notey.waitForString();
                app.notey.send(1);

                listNotes.add(new NoteFX(id, text, color));
            }
        } while (!stop);

        printList(listNotes);

        return listNotes;
    }

    public List search() {
        System.out.println("HomeController -> select() ...");

        app.notey.send(Const.SEARCH);
        app.notey.waitForInt();
        app.notey.send(searchComBox.getText().trim());

        boolean stop = false;
        List listNotes = new ArrayList<NoteFX>();

        do {
            int id = app.notey.waitForInt();
            if (id < 0) stop = true;
            else {
                app.notey.send(1);

                String text = app.notey.waitForString();
                app.notey.send(1);

                String color = app.notey.waitForString();
                app.notey.send(1);

                listNotes.add(new NoteFX(id, text, color));
            }
        } while (!stop);

        printList(listNotes);

        return listNotes;
    }

    @FXML
    public void refresh(ActionEvent event) {
        InitializeNotes();
        System.gc();
    }

    private static void printList(List list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }

    @FXML
    private void aboutWindow(ActionEvent event) {
        if (aboutDialog.isVisible()) {
            return;
        }
        aboutDialog.show();
    }
}