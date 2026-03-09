package br.ufpb.dcx.flow.dev.s.view.seller;

import br.ufpb.dcx.flow.dev.s.model.Seller;
import br.ufpb.dcx.flow.dev.s.service.SellerService;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class LoginView {

    public static void display(SellerService sellerService, Consumer<Seller> onLoginSuccess) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Login de Vendedor");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        TextField txtId = new TextField();
        txtId.setPromptText("ID do Vendedor");

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Senha");

        Button btnLogin = new Button("Entrar");
        btnLogin.setMaxWidth(Double.MAX_VALUE);

        btnLogin.setOnAction(e -> {
            try {
                Seller seller = sellerService.findById(Long.parseLong(txtId.getText())).orElseGet(null);

                if (seller != null && seller.getPassword().equals(txtPass.getText())) {
                    onLoginSuccess.accept(seller);
                    stage.close();
                } else {
                    new Alert(Alert.AlertType.ERROR, "ID ou Senha incorretos!").show();
                }
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Erro ao fazer login, por favor, use apenas ID's").show();
            }
        });

        root.getChildren().addAll(new Label("Identifique-se:"), txtId, txtPass, btnLogin);
        stage.setScene(new Scene(root, 300, 250));
        stage.showAndWait();
    }
}