package com.company.helperClasses;

import com.company.models.Receipt;

import java.io.*;
import java.util.Locale;

public class ReceiptsFileSaver {

    // .TXT
    public static void saveAsFileTxt(Receipt receipt) {
        String filePath = CONFIG.PATH_TO_STORES_DIR + receipt.getSupermarket().getName() + CONFIG.PATH_TO_RECEIPTS_DIR + "\\" + receipt.getId() + CONFIG.TXT;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(getFormattedReceiptTxt(receipt, "-"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void readFromFileTxt(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // .HTML
    public static void saveAsFileHtml(Receipt receipt) {
        String filePath = CONFIG.PATH_TO_STORES_DIR + receipt.getSupermarket().getName() + CONFIG.PATH_TO_RECEIPTS_DIR + "\\" + receipt.getId() + ".html";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(getFormattedReceiptHtml(receipt));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // .SER
    public static void saveAsFileSer(Receipt receipt) {
        String filePath = CONFIG.PATH_TO_STORES_DIR + receipt.getSupermarket().getName() + CONFIG.PATH_TO_RECEIPTS_DIR + receipt.getId() + CONFIG.SER;

        try (FileOutputStream fout = new FileOutputStream(filePath); ObjectOutputStream outputStream = new ObjectOutputStream(fout);) {
            outputStream.writeObject(receipt);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Receipt readFromFileSer(String filePath) {
        Receipt receipt = null;

        try (FileInputStream fin = new FileInputStream(filePath); ObjectInputStream inputStream = new ObjectInputStream(fin);) {
            receipt = (Receipt) inputStream.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return receipt;
    }

    // formatters
    private static String getFormattedReceiptTxt(Receipt receipt, String divider) {
        final int fullLength = 40;

        final String startEndLine = new String(new char[fullLength]).replace("\0", "#") + "\n";
        final String fill = new String(new char[fullLength]).replace("\0", divider) + "\n";
        StringBuilder sb = new StringBuilder();
        // Касовата бележка трябва да съдържат минимум следната информация: пореден номер,
        // касиер, който издава касовата бележка, дата и час на издаване на касовата бележка, списък със
        // стоки, които се включват в касовата бележка включително цената и количеството им и общата
        // стойност, която трябва да се заплати от клиента.

        sb.append(startEndLine);
        // header
        sb.append("\n");
        sb.append(new String(new char[(fullLength - "RECEIPT FROM".length()) / 2]).replace("\0", " "));
        sb.append("RECEIPT FROM");
        sb.append(new String(new char[(fullLength - "RECEIPT FROM".length()) / 2]).replace("\0", " "));
        sb.append("\n");
        int nameLength = receipt.getSupermarket().getName().length();
        sb.append(new String(new char[(fullLength - nameLength) / 2]).replace("\0", " "));
        sb.append(receipt.getSupermarket().getName().toUpperCase(Locale.ROOT));
        sb.append(new String(new char[(fullLength - nameLength) / 2]).replace("\0", " "));
        sb.append("\n\n");
        sb.append(fill);

        // meta data
        String idMetadata = "Receipt Nr. " + receipt.getId();
        sb.append(new String(new char[(fullLength - idMetadata.length()) / 2]).replace("\0", " "));
        sb.append(idMetadata);
        sb.append(new String(new char[(fullLength - idMetadata.length()) / 2]).replace("\0", " "));
        sb.append("\n");
        String date = String.format("%10s%02d/%02d/%4d  %02d:%02d:%02d%10s", " ", receipt.getDate().getDayOfMonth(), receipt.getDate().getMonthValue(), receipt.getDate().getYear()
                , receipt.getDate().getHour(), receipt.getDate().getMinute(), receipt.getDate().getSecond(), " ");
        sb.append(date);
        sb.append("\n");
        sb.append(fill);
        sb.append("\n");

        //TODO could eventually add "ITEM     QTY PRC"

        // products
        for (var product : receipt.getProductList().keySet()) {
            sb.append(String.format("%-20s %10dx %7.2f", product.getName(), receipt.getProductList().get(product).getFirst(), receipt.getProductList().get(product).getSecond()));
            sb.append("\n");
        }
        sb.append("\n");
        sb.append(String.format("TOTAL:  %32.2f", receipt.getTotalSum()));
        sb.append("\n");
        sb.append(fill);
        // TODO could eventually add "CASH", "CHANGE"

        // footer
        String servedBy = "You have been served by " + receipt.getCashier().getName().split(" ")[0];
        int servedByLength = (servedBy).length();
        sb.append(new String(new char[(fullLength - servedByLength) / 2]).replace("\0", " "));
        sb.append(servedBy);
        sb.append(new String(new char[(fullLength - servedByLength) / 2]).replace("\0", " "));
        sb.append("\n");
        sb.append(fill);
        sb.append("\n");
        sb.append(new String(new char[15]).replace("\0", " "));
        sb.append("THANK YOU!");
        sb.append(new String(new char[15]).replace("\0", " "));
        sb.append("\n\n");
        sb.append(startEndLine);

        return sb.toString();
    }

    private static String getFormattedReceiptHtml(Receipt receipt) {
        final int fullLength = 40;
        final String startEndLine = new String(new char[fullLength]).replace("\0", "#") + "\n";
        final String fill = new String(new char[fullLength]).replace("\0", "-") + "<br>";
        StringBuilder sb = new StringBuilder();

        sb.append("<p style=text-align:center;>");
        sb.append("\n");
        sb.append(startEndLine);
        sb.append("\n");
        // header
        sb.append("<br><br>");
        sb.append("RECEIPT FROM");
        sb.append("<br>");
        sb.append(receipt.getSupermarket().getName().toUpperCase(Locale.ROOT));
        sb.append("<br><br>");
        sb.append("\n");
        sb.append(fill);
        sb.append("\n");

        // meta data
        String idMetadata = "Receipt Nr. " + receipt.getId();
        sb.append(idMetadata);
        sb.append("<br>");
        String date = String.format("%02d/%02d/%4d  %02d:%02d:%02d", receipt.getDate().getDayOfMonth(), receipt.getDate().getMonthValue(), receipt.getDate().getYear()
                , receipt.getDate().getHour(), receipt.getDate().getMinute(), receipt.getDate().getSecond());
        sb.append(date);
        sb.append("<br>");
        sb.append("\n");
        sb.append(fill);
        sb.append("<br>");
        sb.append("\n");

        // products
        for (var product : receipt.getProductList().keySet()) {
            sb.append(String.format("%-20s  %10dx %7.2f", product.getName(), receipt.getProductList().get(product).getFirst(), receipt.getProductList().get(product).getSecond()));
            sb.append("<br>");
            sb.append("\n");
        }
        sb.append("<br>");
        sb.append("\n");
        sb.append(String.format("TOTAL: %32.2f", receipt.getTotalSum()));
        sb.append("<br>");
        sb.append("\n");
        sb.append(fill);
        sb.append("\n");

        // footer
        String servedBy = "You have been served by " + receipt.getCashier().getName().split(" ")[0];
        sb.append(servedBy);
        sb.append("<br>");
        sb.append("\n");
        sb.append(fill);
        sb.append("<br>");
        sb.append("\n");

        // QR code
        sb.append("<img style=\"width:110px;height:110px;\"" + "src=" + "\"https://barcode.tec-it.com/barcode.ashx?code=MobileQRCode&data=" + getFormattedReceiptQR(receipt, "*") + "\" >");
        sb.append("<br><br>");
        sb.append("\n");

        // continued footer
        sb.append("THANK YOU!");
        sb.append("\n");
        sb.append("<br><br>");
        sb.append(startEndLine);
        sb.append("\n");

        sb.append("</p>");

        return sb.toString();
    }

    private static String getFormattedReceiptQR(Receipt receipt, String divider) {
        final int fullLength = 45;
        final String fill = new String(new char[fullLength]).replace("\0", divider) + "\n";
        StringBuilder sb = new StringBuilder();

        // header
        sb.append("\n\n");
        String header = "RECEIPT FROM " + receipt.getSupermarket().getName().toUpperCase(Locale.ROOT);
        sb.append(new String(new char[(fullLength - header.length()) / 2]).replace("\0", " "));
        sb.append(header);
        sb.append(new String(new char[(fullLength - header.length()) / 2]).replace("\0", " "));
        sb.append(fill);

        // meta data
        String idMetadata = "Receipt Nr. " + receipt.getId();
        sb.append(new String(new char[(fullLength - idMetadata.length()) / 2]).replace("\0", " "));
        sb.append(idMetadata);
        sb.append(new String(new char[(fullLength - idMetadata.length()) / 2]).replace("\0", " "));
        sb.append("\n");
        String date = String.format("%10s%02d/%02d/%4d  %02d:%02d:%02d%10s", " ", receipt.getDate().getDayOfMonth(), receipt.getDate().getMonthValue(), receipt.getDate().getYear()
                , receipt.getDate().getHour(), receipt.getDate().getMinute(), receipt.getDate().getSecond(), " ");
        sb.append(date);
        sb.append(fill);

        // products
        for (var product : receipt.getProductList().keySet()) {
            sb.append(String.format("%-20s %10dx %7.2f ", product.getName(), receipt.getProductList().get(product).getFirst(), receipt.getProductList().get(product).getSecond()));
            sb.append("\n");
        }

        sb.append(String.format("TOTAL:  %32.2f", receipt.getTotalSum()));
        sb.append(fill);

        // footer
        String servedBy = "You have been served by " + receipt.getCashier().getName().split(" ")[0];
        int servedByLength = (servedBy).length();
        sb.append(new String(new char[(fullLength - servedByLength) / 2]).replace("\0", " "));
        sb.append(servedBy);
        sb.append(new String(new char[(fullLength - servedByLength) / 2]).replace("\0", " "));
        sb.append(fill);
        sb.append(new String(new char[15]).replace("\0", " "));
        sb.append("THANK YOU!");
        sb.append(new String(new char[15]).replace("\0", " "));

        return sb.toString();
    }
}
