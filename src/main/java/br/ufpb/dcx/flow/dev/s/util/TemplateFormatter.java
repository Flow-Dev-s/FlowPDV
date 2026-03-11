package br.ufpb.dcx.flow.dev.s.util;

import br.ufpb.dcx.flow.dev.s.model.Sale;
import br.ufpb.dcx.flow.dev.s.model.SaleItem;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TemplateFormatter {

    public static String formatReceipt(String templateTxt, Sale sale, String companyName, String sellerName) {

        int startIndex = templateTxt.indexOf("[ITEMS]");
        int endIndex = templateTxt.indexOf("[/ITEMS]");

        String formattedReceipt = templateTxt;

        if (startIndex != -1 && endIndex != -1) {
            String itemTemplate = templateTxt.substring(startIndex + 7, endIndex).trim();
            StringBuilder filledItems = new StringBuilder();

            for (SaleItem item : sale.getItems()) {
                String subtotal = String.format(Locale.US, "%.2f", item.getProduct().getPrice().multiply(item.getQuantity()));

                String itemLine = itemTemplate
                        .replace("%qty%", String.valueOf(item.getQuantity()))
                        .replace("%name%", item.getProduct().getName())
                        .replace("%u_price%", String.format(Locale.US, "%.2f", item.getProduct().getPrice()))
                        .replace("%subtotal%", subtotal);

                filledItems.append(itemLine).append("\n");
            }
            String topPart = templateTxt.substring(0, startIndex);
            String bottomPart = templateTxt.substring(endIndex + 8);

            formattedReceipt = topPart + filledItems.toString() + bottomPart;
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedTotal = String.format(Locale.US, "%.2f", sale.getTotalAmount());

        formattedReceipt = formattedReceipt
                .replace("%company%", companyName)
                .replace("%seller%", sellerName)
                .replace("%sale_id%", String.valueOf(sale.getId()))
                .replace("%date%", sale.getSaleDate().format(dateFormatter))
                .replace("%total%", formattedTotal)
                .replace("%payment%", sale.getPaymentMethod().toString());

        return formattedReceipt;
    }

    public static String readFileTemplate(String path) {
        try (InputStream is = TemplateFormatter.class.getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("❌ Arquivo de template não encontrado: " + path);
                return "ERRO: TEMPLATE NAO ENCONTRADO";
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return "ERRO AO LER TEMPLATE";
        }
    }
}