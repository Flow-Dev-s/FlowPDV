package br.ufpb.dcx.flow.dev.s.util;

import br.ufpb.dcx.flow.dev.s.model.Sale;
import br.ufpb.dcx.flow.dev.s.model.SaleItem;

import java.time.format.DateTimeFormatter;

public class ReceiptFormatter {

    private static final int COLUMNS_80MM = 48;
    private static final int COLUMNS_58MM = 32;

    public static String buildReceipt80mm(Sale sale, String empresaNome, String vendedor) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        sb.append(centralizar(empresaNome, COLUMNS_80MM)).append("\n");
        sb.append(linhaSeparadora(COLUMNS_80MM)).append("\n");
        sb.append("Data: ").append(sale.getSaleDate().format(df)).append("\n");
        sb.append("Venda #: ").append(sale.getId()).append("\n");
        sb.append("Vendedor: ").append(vendedor).append("\n");
        sb.append(linhaSeparadora(COLUMNS_80MM)).append("\n");

        sb.append("QTD  DESCRICAO                      V.UN   TOTAL\n");
        sb.append(linhaSeparadora(COLUMNS_80MM)).append("\n");

        for (SaleItem item : sale.getItems()) {
            String qtd = String.valueOf(item.getQuantity());
            String nome = item.getProduct().getName();

            String vUn = String.format(java.util.Locale.US, "%.2f", item.getProduct().getPrice());

            String vTot = String.format(java.util.Locale.US, "%.2f",
                    item.getProduct().getPrice().multiply(item.getQuantity()));

            if (nome.length() > 26) nome = nome.substring(0, 26);
            sb.append(String.format("%-4s %-26s %8s %8s\n", qtd + "x", nome, vUn, vTot));
        }

        sb.append(linhaSeparadora(COLUMNS_80MM)).append("\n");

        String totalFormatado = String.format(java.util.Locale.US, "R$ %.2f", sale.getTotalAmount());
        sb.append(alinharEsquerdaDireita("TOTAL DA VENDA", totalFormatado, COLUMNS_80MM)).append("\n");

        sb.append("Forma de Pagto: ").append(sale.getPaymentMethod()).append("\n");
        sb.append(linhaSeparadora(COLUMNS_80MM)).append("\n");
        sb.append(centralizar("Obrigado pela preferencia!", COLUMNS_80MM)).append("\n");
        sb.append("\n\n\n\n\n");

        return sb.toString();
    }

    private static String linhaSeparadora(int tamanho) {
        return "-".repeat(tamanho);
    }

    private static String centralizar(String texto, int tamanho) {
        if (texto.length() >= tamanho) return texto.substring(0, tamanho);
        int espacosEsquerda = (tamanho - texto.length()) / 2;
        return " ".repeat(espacosEsquerda) + texto;
    }

    // Coloca texto na esquerda e valor na direita com espaços no meio
    private static String alinharEsquerdaDireita(String esquerda, String direita, int tamanho) {
        int espacos = tamanho - esquerda.length() - direita.length();
        if (espacos < 1) espacos = 1; // Garante pelo menos 1 espaço
        return esquerda + " ".repeat(espacos) + direita;
    }
}
