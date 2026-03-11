package br.ufpb.dcx.flow.dev.s.view;

import br.ufpb.dcx.flow.dev.s.dto.SaleRequest;
import br.ufpb.dcx.flow.dev.s.model.Sale;
import br.ufpb.dcx.flow.dev.s.service.SaleService;
import br.ufpb.dcx.flow.dev.s.service.MercadoPagoService;
import com.mercadopago.resources.payment.Payment;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.function.Consumer;

import static br.ufpb.dcx.flow.dev.s.view.CheckoutView.cart;

public class PaymentView {

    public static void display(SaleService saleService, BigDecimal totalAmount, Consumer<Sale> onFinish) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Finalizar Pagamento - FlowPDV");

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f4f6f7;");

        Label lblHeader = new Label("Total: R$ " + totalAmount);
        lblHeader.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        TextField txtSeller = new TextField();
        grid.add(new Label("Vendedor ID:"), 0, 0);
        grid.add(txtSeller, 1, 0);

        TextField txtDiscount = new TextField("0.00");
        grid.add(new Label("Desconto (R$):"), 0, 1);
        grid.add(txtDiscount, 1, 1);

        txtDiscount.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                String val = newValue.replace(",", ".");
                if (val.isBlank()) val = "0";

                BigDecimal discount = new BigDecimal(val);
                BigDecimal finalTotal = totalAmount.subtract(discount);
                if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
                    finalTotal = BigDecimal.ZERO;
                }

                lblHeader.setText(String.format("Total com Desconto: R$ %.2f", finalTotal));
            } catch (NumberFormatException ex) {
            }
        });


        TextField txtCpf = new TextField();
        grid.add(new Label("CPF Cliente:"), 0, 2);
        grid.add(txtCpf, 1, 2);

        TextField txtCash = new TextField("0.00");
        grid.add(new Label("Dinheiro:"), 0, 3);
        grid.add(txtCash, 1, 3);

        TextField txtCard = new TextField("0.00");
        grid.add(new Label("Cartão:"), 0, 4);
        grid.add(txtCard, 1, 4);

        TextField txtPix = new TextField("0.00");
        grid.add(new Label("PIX:"), 0, 5);
        grid.add(txtPix, 1, 5);

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
                var allSales = saleService.findAll();
                if (!allSales.isEmpty()) {
                    stage.close();
                    onFinish.accept(allSales.get(allSales.size() - 1));
                }
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Erro ao finalizar venda: " + ex.getMessage()).show();
            }
        });

        Button btnQrCode = new Button("📱 Gerar QR Code Dinâmico");
        btnQrCode.setStyle("-fx-background-color: #00b894; -fx-text-fill: white; -fx-font-weight: bold;");
        grid.add(btnQrCode, 2, 5);

        btnQrCode.setOnAction(e -> {
            try {
                BigDecimal pixAmount = new BigDecimal(txtPix.getText().replace(",", "."));

                if (pixAmount.compareTo(BigDecimal.ZERO) == 0) {
                    String discountStr = txtDiscount.getText().replace(",", ".");
                    BigDecimal discount = discountStr.isBlank() ? BigDecimal.ZERO : new BigDecimal(discountStr);

                    pixAmount = totalAmount.subtract(discount);
                    if (pixAmount.compareTo(BigDecimal.ZERO) < 0) pixAmount = BigDecimal.ZERO;

                    txtPix.setText(pixAmount.toString());
                }

                String accessToken = Dotenv.load().get("CREDENCIAL");
                MercadoPagoService.configure(accessToken);

                Payment mpPayment = MercadoPagoService.generatePixPayment(pixAmount);
                Long paymentId = mpPayment.getId();

                String qrCodeBase64 = mpPayment.getPointOfInteraction().getTransactionData().getQrCodeBase64();

                byte[] imageBytes = Base64.getDecoder().decode(qrCodeBase64);
                Image qrImage = new Image(new ByteArrayInputStream(imageBytes));

                showQrCodeWindowWithRadar(qrImage, pixAmount, paymentId, btnConfirm);

            } catch (com.mercadopago.exceptions.MPApiException ex) {
                System.out.println("Erro detalhado da API: " + ex.getApiResponse().getContent());
                new Alert(Alert.AlertType.ERROR, "Erro API: " + ex.getApiResponse().getContent()).show();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        root.getChildren().addAll(lblHeader, new Separator(), grid, btnConfirm);

        Scene scene = new Scene(root, 600, 700);
        stage.setScene(scene);
        stage.setMinWidth(600);
        stage.setMinHeight(700);
        stage.showAndWait();
    }

    private static void showQrCodeWindowWithRadar(Image qrImage, BigDecimal amount, Long paymentId, Button btnConfirm) {
        Stage qrStage = new Stage();
        qrStage.initModality(Modality.APPLICATION_MODAL);
        qrStage.setTitle("Aguardando Pagamento PIX...");

        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: white;");

        Label lblStatus = new Label("À espera do pagamento...");
        lblStatus.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #f39c12;");

        Label lblValor = new Label(String.format("Valor: R$ %.2f", amount));
        lblValor.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00b894;");

        ImageView imageView = new ImageView(qrImage);
        imageView.setFitWidth(300);
        imageView.setFitHeight(300);

        Button btnCancelar = new Button("Cancelar Cobrança");

        box.getChildren().addAll(lblStatus, imageView, lblValor, btnCancelar);

        Scene scene = new Scene(box, 500, 650);
        qrStage.setScene(scene);

        qrStage.setMinWidth(500);
        qrStage.setMinHeight(650);
        qrStage.sizeToScene();

        Timeline radar = new Timeline();

        KeyFrame checkStatusFrame = new KeyFrame(Duration.seconds(3), ev -> {
            try {
                Payment check = MercadoPagoService.checkPaymentStatus(paymentId);

                if ("approved".equals(check.getStatus())) {
                    radar.stop();

                    lblStatus.setText("✅ PAGAMENTO APROVADO!");
                    lblStatus.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

                    Platform.runLater(() -> {
                        qrStage.close();
                        btnConfirm.fire();
                    });
                }
            } catch (Exception ex) {
                System.err.println("Erro no radar do PIX: " + ex.getMessage());
            }
        });

        radar.getKeyFrames().add(checkStatusFrame);
        radar.setCycleCount(Animation.INDEFINITE);
        radar.play();

        btnCancelar.setOnAction(e -> {
            radar.stop();
            qrStage.close();
        });
        qrStage.setOnCloseRequest(e -> radar.stop());

        qrStage.showAndWait();
    }
}