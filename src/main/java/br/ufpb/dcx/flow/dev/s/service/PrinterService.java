package br.ufpb.dcx.flow.dev.s.service;

import javax.print.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PrinterService {

    public static boolean printReceipt(String textToPrint) {
        try {
            PrintService defaultPrinter = PrintServiceLookup.lookupDefaultPrintService();

            if (defaultPrinter == null) {
                System.err.println("❌ Nenhuma impressora padrão encontrada no sistema.");
                return false;
            }

            System.out.println("🖨️ Enviando para a impressora: " + defaultPrinter.getName());
            InputStream stream = new ByteArrayInputStream(textToPrint.getBytes(StandardCharsets.UTF_8));
            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
            Doc document = new SimpleDoc(stream, flavor, null);

            DocPrintJob printJob = defaultPrinter.createPrintJob();
            printJob.print(document, null);

            return true;

        } catch (Exception e) {
            System.err.println("❌ Erro ao tentar imprimir: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}