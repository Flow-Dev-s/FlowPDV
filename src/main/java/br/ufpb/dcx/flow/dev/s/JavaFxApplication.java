package br.ufpb.dcx.flow.dev.s;

import br.ufpb.dcx.flow.dev.s.dto.ItemRequest;
import br.ufpb.dcx.flow.dev.s.model.CashRegister;
import br.ufpb.dcx.flow.dev.s.model.Product;
import br.ufpb.dcx.flow.dev.s.model.Sale;
import br.ufpb.dcx.flow.dev.s.model.Seller;
import br.ufpb.dcx.flow.dev.s.service.*;
import br.ufpb.dcx.flow.dev.s.view.CheckoutView;
import br.ufpb.dcx.flow.dev.s.view.HistoryView;
import br.ufpb.dcx.flow.dev.s.view.StockView;
import br.ufpb.dcx.lima.albiere.model.*;
import br.ufpb.dcx.lima.albiere.service.*;
import br.ufpb.dcx.lima.albiere.view.*;
import br.ufpb.dcx.flow.dev.s.view.seller.LoginView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext springContext;
    private BorderPane mainLayout;

    private VBox checkoutView;
    private VBox stockView;
    private VBox historyView;
    private HBox mainHeader;

    private TableView<Sale> historyTable = new TableView<>();
    private javafx.collections.ObservableList<Sale> masterSaleList = javafx.collections.FXCollections.observableArrayList();

    private final TableView<Product> stockTable = new TableView<>();
    private javafx.collections.ObservableList<Product> masterProductList = javafx.collections.FXCollections.observableArrayList();

    private MenuButton userMenu;
    private MenuItem tradeSeller;
    private MenuItem logoutSeller;
    private Button btnRegister;

    String menuStyle = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;";

    @Override
    public void init() {
        String[] args = getParameters().getRaw().toArray(new String[0]);
        this.springContext = new SpringApplicationBuilder()
                .sources(Main.class)
                .run(args);
    }

    @Override
    public void start(Stage stage) {
        mainLayout = new BorderPane();
        StockService stockService = springContext.getBean(StockService.class);
        ProductService productService = springContext.getBean(ProductService.class);
        SaleService saleService = springContext.getBean(SaleService.class);
        checkoutView = CheckoutView.display(productService, saleService, stockService);

        mainHeader = createHeader();

        historyView = HistoryView.display(historyTable, saleClicada -> {

            VBox checkoutNode = CheckoutView.display(productService, saleService, stockService);
            mainLayout.setCenter(checkoutNode);
            mainLayout.setTop(null);

            List<ItemRequest> novosItens = new ArrayList<>();
            saleClicada.getItems().forEach(item -> {
                novosItens.add(new ItemRequest(item.getProduct().getBarcode(), item.getQuantity()));
            });

            CheckoutView.resetCart(novosItens, productService);
            CheckoutView.setViewMode(true);
            CheckoutView.setTitle("Visualizando Venda #" + saleClicada.getId());

            CheckoutView.setOnBackAction(() -> {
                mainLayout.setCenter(historyView);
                mainLayout.setTop(mainHeader);
                CheckoutView.setViewMode(false);
            });
        }, masterSaleList);

        stockView = StockView.display(stockService, stockTable, masterProductList);

        mainLayout.setTop(mainHeader);
        mainLayout.setCenter(checkoutView);

        Scene scene = new Scene(mainLayout, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/css/MenuStyle.css").toExternalForm());
        stage.setTitle("Sistema de Gestão ERP/PDV");
        stage.setScene(scene);
        stage.show();

        updateSalePermission();
    }

    private void updateSalePermission() {
        Platform.runLater(() -> {
            CashRegisterService registerService = springContext.getBean(CashRegisterService.class);
            boolean isRegisterOpen = registerService.getCashRegisterOpen().isPresent();
            boolean isSellerAuthenticated = (this.logged != null);

            boolean canSell = isRegisterOpen && isSellerAuthenticated;

            if (isSellerAuthenticated) {
                userMenu.setText("👤 Vendedor: " + logged.getName());
                tradeSeller.setText("🔄 Trocar Usuário");
                logoutSeller.setVisible(true);
            } else {
                userMenu.setText("👤 Vendedor: Visitante");
                tradeSeller.setText("🔑 Fazer Login");
                logoutSeller.setVisible(false);
            }

            CheckoutView.setBarcodeDisabled(!canSell);

            if (!isRegisterOpen) {
                btnRegister = new Button("️⚙️ Abrir o Caixa");
                CheckoutView.setStatusLabel("⚠️ O Caixa está fechado. Abra-o para vender.", "-fx-text-fill: #e67e22;");
            } else {
                btnRegister = new Button("️⚙️ Fechar o Caixa");
            }
            if (!isSellerAuthenticated) {
                CheckoutView.setStatusLabel("👤 Identifique o vendedor para iniciar as vendas.", "-fx-text-fill: #3498db;");
            } else {
                CheckoutView.setStatusLabel("✅ Caixa pronto! Vendedor: " + logged.getName(), "-fx-text-fill: #27ae60;");
            }
        });
    }

    private HBox createHeader() {
        CashRegisterService registerService = springContext.getBean(CashRegisterService.class);
        boolean isRegisterOpen = registerService.getCashRegisterOpen().isPresent();
        HBox header = new HBox(15);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #2c3e50;");

        Button btnCheckout = new Button("🛒 Vender (PDV)");
        Button btnStock = new Button("📦 Estoque");
        Button btnHistory = new Button("📊 Histórico");
        if(!isRegisterOpen) {
            btnRegister = new Button("⚙️ Abrir o Caixa");
        } else {
            btnRegister = new Button("️⚙️ Fechar o Caixa");
        }

        btnCheckout.setStyle(menuStyle);
        btnStock.setStyle(menuStyle);
        btnHistory.setStyle(menuStyle);
        btnRegister.setStyle(menuStyle);

        btnCheckout.setOnAction(e -> mainLayout.setCenter(checkoutView));
        btnStock.setOnAction(e -> {
            refreshStockTable();
            mainLayout.setCenter(stockView);
        });
        btnHistory.setOnAction(e -> {
            refreshHistoryTable();
            mainLayout.setCenter(historyView);
        });

        btnRegister.setOnAction(e -> handleRegisterAction());

        userMenu = new MenuButton("👤 Vendedor: Visitante");
        userMenu.getStyleClass().add("menu-button");

        tradeSeller = new MenuItem("🔑 Fazer Login");
        logoutSeller = new MenuItem("❌ Sair");

        tradeSeller.setOnAction(e -> openLoginWindow());
        logoutSeller.setOnAction(e -> performLogout());

        userMenu.getItems().addAll(tradeSeller, new SeparatorMenuItem(), logoutSeller);
        header.getChildren().addAll(btnCheckout, btnStock, btnHistory, btnRegister, userMenu);
        return header;
    }

    private void handleRegisterAction() {
        CashRegisterService service = springContext.getBean(CashRegisterService.class);
        Optional<CashRegister> current = service.getCashRegisterOpen();

        if (current.isPresent()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Deseja fechar o caixa?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(res -> {
                if (res == ButtonType.YES) {
                    service.closeRegister();
                    updateSalePermission();
                }
            });
        } else {
            TextInputDialog nameDialog = new TextInputDialog("Albiere");
            nameDialog.setTitle("Abertura de Caixa");
            nameDialog.setHeaderText("Nome do Operador/Vendedor:");
            Optional<String> nameResult = nameDialog.showAndWait();

            if (nameResult.isPresent()) {
                String seller = nameResult.get();

                TextInputDialog balanceDialog = new TextInputDialog("0.00");
                balanceDialog.setTitle("Abertura de Caixa");
                balanceDialog.setHeaderText("Troco inicial:");
                balanceDialog.showAndWait().ifPresent(val -> {
                    try {
                        CheckoutView.setCurrentSeller(seller);
                        service.openRegister(new BigDecimal(val.replace(",", ".")));
                        updateSalePermission();
                    } catch (Exception ex) {
                        showError("Erro", "Valor inválido!");
                    }
                });
            }
        }
    }

    private void refreshStockTable() {
        ProductService productService = springContext.getBean(ProductService.class);
        List<Product> products = productService.findAll();

        if (products != null) {
            masterProductList.setAll(products);
        }
    }

    private void refreshHistoryTable() {
        SaleService service = springContext.getBean(SaleService.class);
        masterSaleList.setAll(service.findAll());
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void stop() {
        this.springContext.close();
        Platform.exit();
    }

    private Seller logged;

    private void openLoginWindow() {
        SellerService sellerService = springContext.getBean(SellerService.class);

        LoginView.display(sellerService, seller -> {
            this.logged = seller;
            updateSalePermission();
        });
    }

    private void performLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Sair do Sistema");
        alert.setHeaderText("Confirmação de Logout");
        alert.setContentText("Deseja realmente encerrar a sessão do vendedor?");

        ButtonType btnYes = new ButtonType("Sim");
        ButtonType btnNo = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnYes, btnNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == btnYes) {
                this.logged = null;
                updateSalePermission();
                mainLayout.setCenter(checkoutView);
            }
        });
    }
}