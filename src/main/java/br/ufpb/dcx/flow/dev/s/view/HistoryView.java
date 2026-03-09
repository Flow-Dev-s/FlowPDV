package br.ufpb.dcx.flow.dev.s.view;

import br.ufpb.dcx.flow.dev.s.model.Sale;
import br.ufpb.dcx.flow.dev.s.service.PrinterService;
import br.ufpb.dcx.flow.dev.s.util.TemplateFormatter;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class HistoryView {

    public static VBox display(TableView<Sale> historyTable, Consumer<Sale> onReopenSale, ObservableList<Sale> masterData) {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));

        Label title = new Label("📊 Histórico de Vendas");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        TextField searchInput = new TextField();
        searchInput.setPromptText("Buscar por ID...");
        searchInput.setStyle("-fx-font-size: 14px;");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Filtrar por Data");
        datePicker.setStyle("-fx-font-size: 14px;");

        Button btnClear = new Button("🧹 Limpar Filtros");
        btnClear.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        HBox filterBox = new HBox(15, new Label("Filtros:"), searchInput, datePicker, btnClear);
        filterBox.setStyle("-fx-alignment: center-left;");

        FilteredList<Sale> filteredData = new FilteredList<>(masterData, p -> true);

        searchInput.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFilter(filteredData, newValue, datePicker.getValue());
        });

        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateFilter(filteredData, searchInput.getText(), newValue);
        });

        btnClear.setOnAction(e -> {
            searchInput.clear();
            datePicker.setValue(null);
        });

        SortedList<Sale> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(historyTable.comparatorProperty());
        historyTable.setItems(sortedData);

        TableColumn<Sale, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Sale, String> dateCol = new TableColumn<>("Data/Hora");
        dateCol.setCellValueFactory(cell -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return new javafx.beans.property.SimpleStringProperty(cell.getValue().getSaleDate().format(formatter));
        });

        TableColumn<Sale, String> methodCol = new TableColumn<>("Pagamento");
        methodCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPaymentMethod().toString()));

        TableColumn<Sale, Integer> itemsCol = new TableColumn<>("Itens");
        itemsCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getItems().size()));

        TableColumn<Sale, String> totalCol = new TableColumn<>("Total");

        totalCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(String.format(java.util.Locale.US, "R$ %.2f", cell.getValue().getTotalAmount())));

        historyTable.getColumns().setAll(idCol, dateCol, methodCol, itemsCol, totalCol);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        Button btnPrint = new Button("🖨️ Imprimir 2ª Via");
        btnPrint.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
        btnPrint.setDisable(true);

        historyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btnPrint.setDisable(newSelection == null);
        });

        btnPrint.setOnAction(e -> {
            Sale selectedSale = historyTable.getSelectionModel().getSelectedItem();
            if (selectedSale != null) {
                String templateTxt = TemplateFormatter.readFileTemplate("/cupom_padrao.txt");

                String formattedReceipt = TemplateFormatter.formatReceipt(
                        templateTxt,
                        selectedSale,
                        "ALBIERE STORE",
                        "Reimpressão"
                );

                boolean success = PrinterService.printReceipt(formattedReceipt);

                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "A 2ª via da venda #" + selectedSale.getId() + " foi enviada para a impressora!");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Erro de comunicação com a impressora padrão.");
                    alert.showAndWait();
                }
            }
        });


        historyTable.setRowFactory(tv -> {
            TableRow<Sale> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Sale clickedSale = row.getItem();
                    onReopenSale.accept(clickedSale);
                }
            });
            return row;
        });

        view.getChildren().addAll(title, filterBox, new Label("(Dê duplo clique para visualizar os detalhes da venda)"), historyTable, btnPrint);
        return view;
    }

    private static void updateFilter(FilteredList<Sale> filteredData, String searchId, LocalDate searchDate) {
        filteredData.setPredicate(sale -> {
            boolean matchesId = true;
            boolean matchesDate = true;

            if (searchId != null && !searchId.trim().isEmpty()) {
                matchesId = String.valueOf(sale.getId()).contains(searchId);
            }

            if (searchDate != null) {
                LocalDate saleDate = sale.getSaleDate().toLocalDate();
                matchesDate = saleDate.equals(searchDate);
            }

            return matchesId && matchesDate;
        });
    }
}