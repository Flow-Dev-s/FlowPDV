package br.ufpb.dcx.flow.dev.s.view;

import br.ufpb.dcx.flow.dev.s.dto.ItemRequest;
import br.ufpb.dcx.flow.dev.s.model.Product;
import br.ufpb.dcx.flow.dev.s.model.Sale;
import br.ufpb.dcx.flow.dev.s.service.PrinterService;
import br.ufpb.dcx.flow.dev.s.service.ProductService;
import br.ufpb.dcx.flow.dev.s.service.SaleService;
import br.ufpb.dcx.flow.dev.s.service.StockService;
import br.ufpb.dcx.flow.dev.s.util.TemplateFormatter;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckoutView {

    private static BigDecimal currentSaleTotal = BigDecimal.ZERO;
    static List<ItemRequest> cart = new ArrayList<>();
    private static List<BigDecimal> cartPrices = new ArrayList<>();

    private static Label statusLabel;
    private static Label totalDisplay;
    private static TextField barcodeInput;
    private static String currentSeller;
    private static Label title;
    private static Button btnFinish;
    private static Button btnBack;
    private static Button btnRemove;
    private static Button btnCancel;
    private static Runnable onBackAction;
    private static ListView<String> cartDisplay;

    public static String getCurrentSeller() {
        return currentSeller;
    }

    public static void setCurrentSeller(String currentSeller) {
        CheckoutView.currentSeller = currentSeller;
    }

    public static VBox display(ProductService productService, SaleService saleService, StockService stockService) {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        view.setStyle("-fx-background-color: #f4f6f7;");

        btnBack = new Button("⬅ Voltar ao Histórico");
        btnBack.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold;");
        btnBack.setVisible(false);

        btnBack.setOnAction(e -> {
            if (onBackAction != null) {
                onBackAction.run();
            }
        });

        title = new Label("Frente de Caixa");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        cartDisplay = new ListView<>();
        statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        barcodeInput = new TextField();
        barcodeInput.setPromptText("Bipe o código de barras...");
        barcodeInput.setStyle("-fx-font-size: 18px;");

        totalDisplay = new Label("Total: R$ 0.00");
        totalDisplay.setFont(Font.font("Arial", FontWeight.BOLD, 35));
        totalDisplay.setStyle("-fx-text-fill: #27ae60;");

        btnFinish = new Button("Finalizar Venda");
        btnFinish.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

        btnRemove = new Button("Remover Item");
        btnRemove.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");

        btnCancel = new Button("Cancelar Venda");
        btnCancel.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

        HBox actions = new HBox(15, btnFinish, btnRemove, btnCancel);

        barcodeInput.setOnAction(e -> {
            String code = barcodeInput.getText();
            if (code.isBlank()) return;
            try {
                Product product = productService.findByCode(code);
                BigDecimal existingQty = cart.stream()
                        .filter(i -> i.code().equals(code))
                        .map(ItemRequest::quantity)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal available = stockService.getTotalStock(product.getId());

                if (existingQty.add(BigDecimal.ONE).compareTo(available) > 0) {
                    statusLabel.setText("⚠️ Sem estoque! Disponível: " + available.subtract(existingQty));
                    statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                    barcodeInput.clear();
                    return;
                }

                cartDisplay.getItems().add(product.getName() + " | R$ " + product.getPrice());
                cart.add(new ItemRequest(code, BigDecimal.ONE));
                cartPrices.add(product.getPrice());

                currentSaleTotal = currentSaleTotal.add(product.getPrice());
                totalDisplay.setText("Total: R$ " + currentSaleTotal);
                barcodeInput.clear();
                statusLabel.setText("Adicionado: " + product.getName());
                statusLabel.setStyle("-fx-text-fill: #2c3e50;");
            } catch (Exception ex) {
                statusLabel.setText("❌ Produto não encontrado!");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                barcodeInput.clear();
            }
        });

        btnRemove.setOnAction(e -> {
            int index = cartDisplay.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                currentSaleTotal = currentSaleTotal.subtract(cartPrices.get(index));
                cartDisplay.getItems().remove(index);
                cart.remove(index);
                cartPrices.remove(index);
                totalDisplay.setText("Total: R$ " + currentSaleTotal);
            }
        });

        btnCancel.setOnAction(e -> {
            cart.clear();
            cartPrices.clear();
            cartDisplay.getItems().clear();
            currentSaleTotal = BigDecimal.ZERO;
            totalDisplay.setText("Total: R$ 0.00");
            statusLabel.setText("Venda cancelada.");
        });

        btnFinish.setOnAction(e -> {
            if (cart.isEmpty()) return;

            PaymentView.display(saleService, currentSaleTotal, (Sale savedSale) -> {

                Alert receiptAlert = new Alert(Alert.AlertType.CONFIRMATION);
                DialogPane dialogPane = receiptAlert.getDialogPane();
                dialogPane.setStyle("-fx-background-color: #2c3e50;");

                Label warningLabel = new Label("Deseja imprimir o cupom agora?");
                warningLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

                VBox content = new VBox(warningLabel);
                content.setPadding(new Insets(20));
                dialogPane.setContent(content);

                receiptAlert.setTitle("Impressão");
                receiptAlert.setHeaderText("✅ Venda Finalizada!");

                dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #1a252f;");
                Label header = (Label) dialogPane.lookup(".header-panel .label");
                if (header != null) header.setStyle("-fx-text-fill: white;");

                ButtonType btnYes = new ButtonType("🖨️ Sim, Imprimir");
                ButtonType btnNo = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);
                receiptAlert.getButtonTypes().setAll(btnYes, btnNo);

                receiptAlert.showAndWait().ifPresent(response -> {
                    if (response == btnYes) {
                        String templateText = TemplateFormatter.readFileTemplate("/cupom_padrao.txt");

                        String formattedReceipt = TemplateFormatter.formatReceipt(
                                templateText,
                                savedSale,
                                "ALBIERE STORE",
                                currentSeller == null ? savedSale.getSellerName() : currentSeller
                        );

                        boolean success = PrinterService.printReceipt(formattedReceipt);

                        if (success) {
                            setStatusLabel("✅ Cupom enviado para a impressora!", "-fx-text-fill: #27ae60;");
                        } else {
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Não foi possível imprimir. Verifique se a impressora padrão está conectada.");
                            errorAlert.getDialogPane().setStyle("-fx-background-color: #2c3e50;");
                            errorAlert.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill: white;");
                            errorAlert.show();

                            setStatusLabel("⚠️ Erro na impressora.", "-fx-text-fill: #e74c3c;");
                        }
                    }
                });

                cart.clear();
                cartPrices.clear();
                cartDisplay.getItems().clear();
                currentSaleTotal = BigDecimal.ZERO;
                updateTotalDisplay(); // Nome atualizado aqui

                if (statusLabel.getText().contains("Adicionado")) {
                    setStatusLabel("✅ Caixa livre para a próxima venda.", "-fx-text-fill: #27ae60;");
                }
            });
        });
        view.getChildren().add(0, btnBack);
        view.getChildren().addAll(title, statusLabel, barcodeInput, cartDisplay, totalDisplay, actions);
        return view;
    }

    public static void setBarcodeDisabled(boolean disabled) {
        if (barcodeInput != null) {
            barcodeInput.setDisable(disabled);
        }
    }

    public static void setOnBackAction(Runnable action) {
        CheckoutView.onBackAction = action;
        btnBack.setVisible(action != null);
    }

    public static BigDecimal getCurrentSaleTotal() {
        return currentSaleTotal;
    }

    public static void setCurrentSaleTotal(BigDecimal currentSaleTotal) {
        CheckoutView.currentSaleTotal = currentSaleTotal;
    }

    public static List<ItemRequest> getCart() {
        return cart;
    }

    public static void setCart(List<ItemRequest> cart) {
        CheckoutView.cart = cart;
    }

    public static List<BigDecimal> getCartPrices() {
        return cartPrices;
    }

    public static void setCartPrices(List<BigDecimal> cartPrices) {
        CheckoutView.cartPrices = cartPrices;
    }

    public static Label getStatusLabel() {
        return statusLabel;
    }

    public static void setStatusLabel(String title, String style) {
        CheckoutView.statusLabel.setText(title);
        CheckoutView.statusLabel.setStyle(style);
    }

    public static void setStatusLabel(String title) {
        CheckoutView.statusLabel.setText(title);
    }

    public static Label getTotalDisplay() {
        return totalDisplay;
    }

    public static void setTotalDisplay(Label totalDisplay) {
        CheckoutView.totalDisplay = totalDisplay;
    }

    public static TextField getBarcodeInput() {
        return barcodeInput;
    }

    public static void setBarcodeInput(TextField barcodeInput) {
        CheckoutView.barcodeInput = barcodeInput;
    }

    public static void setTitle(String a) {
        title.setText(a);
    }

    public static void setBtnSaleToDisable(boolean disable) {
        btnFinish.setDisable(disable);
    }

    public static void resetCart(List<ItemRequest> items, ProductService productService) {
        cart.clear();
        cartPrices.clear();
        cartDisplay.getItems().clear();
        currentSaleTotal = BigDecimal.ZERO;

        for (ItemRequest item : items) {
            try {
                Product p = productService.findByCode(item.code());
                cart.add(item);
                BigDecimal subtotal = p.getPrice().multiply(item.quantity());
                cartPrices.add(subtotal);

                cartDisplay.getItems().add(p.getName() + " (x" + item.quantity() + ") | R$ " + p.getPrice());
                currentSaleTotal = currentSaleTotal.add(subtotal);
            } catch (Exception e) {
                System.err.println("Erro ao carregar item: " + item.code());
            }
        }
        updateTotalDisplay();
    }

    public static void updateTotalDisplay() {
        totalDisplay.setText(String.format(Locale.US, "Total: R$ %.2f", currentSaleTotal));
    }

    public static void setViewMode(boolean isViewMode) {
        btnFinish.setDisable(isViewMode);
        btnCancel.setDisable(isViewMode);
        btnRemove.setDisable(isViewMode);
        barcodeInput.setDisable(isViewMode);

        if (isViewMode) {
            statusLabel.setText("📌 MODO DE VISUALIZAÇÃO (Histórico)");
            statusLabel.setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold;");
        }
    }
}