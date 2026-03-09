package br.ufpb.dcx.flow.dev.s.view;

import br.ufpb.dcx.flow.dev.s.model.Product;
import br.ufpb.dcx.flow.dev.s.service.StockService;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.math.BigDecimal;
import java.util.Locale;

public class StockView {

    public static VBox display(StockService stockService, TableView<Product> stockTable, ObservableList<Product> masterData) {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));

        Label title = new Label("📦 Controle de Estoque");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        TextField searchInput = new TextField();
        searchInput.setPromptText("Bipe o código ou digite o nome...");
        searchInput.setPrefWidth(350);
        searchInput.setStyle("-fx-font-size: 14px;");

        Button btnClear = new Button("🧹 Limpar Busca");
        btnClear.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        HBox filterBox = new HBox(15, new Label("Pesquisar:"), searchInput, btnClear);
        filterBox.setStyle("-fx-alignment: center-left;");


        FilteredList<Product> filteredData = new FilteredList<>(masterData, p -> true);


        searchInput.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(product -> {

                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }

                String textoBusca = newValue.toLowerCase();

                if (product.getName() != null && product.getName().toLowerCase().contains(textoBusca)) {
                    return true;
                }

                if (product.getBarcode() != null && product.getBarcode().toLowerCase().contains(textoBusca)) {
                    return true;
                }

                return false;
            });
        });

        btnClear.setOnAction(e -> searchInput.clear());

        SortedList<Product> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(stockTable.comparatorProperty());
        stockTable.setItems(sortedData);

        TableColumn<Product, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setMaxWidth(80);

        TableColumn<Product, String> barcodeCol = new TableColumn<>("Cód. Barras");
        barcodeCol.setCellValueFactory(new PropertyValueFactory<>("barcode"));

        TableColumn<Product, String> nameCol = new TableColumn<>("Nome do Produto");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, String> priceCol = new TableColumn<>("Preço Unit. (R$)");
        priceCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                String.format(java.util.Locale.US, "R$ %.2f", cell.getValue().getPrice())
        ));

        TableColumn<Product, String> qtyCol = new TableColumn<>("Qtd. em Estoque");
        qtyCol.setCellValueFactory(cell -> {

            BigDecimal totalStock = stockService.getTotalStock(cell.getValue().getId());
            return new javafx.beans.property.SimpleStringProperty(String.format(Locale.US, "%.2f", totalStock != null ? BigDecimal.valueOf(totalStock.doubleValue()) : BigDecimal.ZERO));
        });

        qtyCol.setStyle("-fx-font-weight: bold; -fx-alignment: CENTER;");

        stockTable.getColumns().setAll(idCol, barcodeCol, nameCol, priceCol, qtyCol);
        stockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        view.getChildren().addAll(title, filterBox, stockTable);
        return view;
    }
}