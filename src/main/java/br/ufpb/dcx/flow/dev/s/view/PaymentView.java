package br.ufpb.dcx.flow.dev.s.view;

import br.ufpb.dcx.flow.dev.s.dto.SaleRequest;
import br.ufpb.dcx.flow.dev.s.model.Sale;
import br.ufpb.dcx.flow.dev.s.service.SaleService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.util.function.Consumer;

import static br.ufpb.dcx.flow.dev.s.view.CheckoutView.cart;

public class PaymentView {

    public static void display(SaleService saleService, BigDecimal totalAmount, Consumer<Sale> onFinish) {

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Finalizar Venda");

        // O segredo para não cortar nada é usar um VBox bem estruturado
        VBox container = new VBox(15);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.TOP_CENTER);
        container.setStyle("-fx-background-color: #f4f6f7;");

        Label lblHeader = new Label("Total: R$ " + totalAmount);
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        // Campos com Labels (Descrições)
        VBox form = new VBox(10);

        TextField txtSeller = createFieldWithLabel(form, "ID do Vendedor:", "Ex: 123");
        TextField txtDiscount = createFieldWithLabel(form, "Desconto (R$):", "0.00");
        txtDiscount.setText("0.00");

        TextField txtCpf = createFieldWithLabel(form, "CPF (Opcional):", "000.000.000-00");

        form.getChildren().add(new Separator());

        TextField txtCash = createFieldWithLabel(form, "Valor em Dinheiro:", "0.00");
        txtCash.setText("0.00");

        TextField txtPix = createFieldWithLabel(form, "Valor em PIX:", "0.00");
        txtPix.setText("0.00");

        TextField txtCard = createFieldWithLabel(form, "Valor em Cartão:", "0.00");
        txtCard.setText("0.00");

        Button btnConfirm = new Button("CONFIRMAR VENDA");
        btnConfirm.setMaxWidth(Double.MAX_VALUE);
        btnConfirm.setPrefHeight(50);
        btnConfirm.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        btnConfirm.setOnAction(e -> {
            try {
                SaleRequest request = new SaleRequest(
                        cart,
                        txtSeller.getText(),
                        new BigDecimal(txtDiscount.getText().replace(",", ".")),
                        txtCpf.getText(),
                        new BigDecimal(txtCash.getText().replace(",", ".")),
                        new BigDecimal(txtPix.getText().replace(",", ".")),
                        new BigDecimal(txtCard.getText().replace(",", "."))
                );

                saleService.finishSale(request);
                java.util.List<Sale> allSales = saleService.findAll();

                if (!allSales.isEmpty()) {
                    Sale lastSale = allSales.get(allSales.size() - 1);
                    stage.close();
                    onFinish.accept(lastSale);
                } else {
                    stage.close();
                }

            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Erro: " + ex.getMessage()).show();
            }
        });

        container.getChildren().addAll(lblHeader, new Separator(), form, btnConfirm);

        // Colocamos dentro de um ScrollPane para garantir que nada suma no Linux
        ScrollPane scroll = new ScrollPane(container);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: #f4f6f7;");

        // Aumentando bem a cena
        Scene scene = new Scene(scroll, 450, 700);
        stage.setScene(scene);

        // Força o Linux a respeitar o tamanho
        stage.setMinWidth(450);
        stage.setMinHeight(700);

        stage.showAndWait();
    }

    // Método auxiliar para criar o Label e o Campo juntos
    private static TextField createFieldWithLabel(VBox parent, String labelText, String prompt) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(35);
        parent.getChildren().addAll(label, field);
        return field;
    }
}