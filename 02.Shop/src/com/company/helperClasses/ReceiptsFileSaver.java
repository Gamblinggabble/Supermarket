package com.company.helperClasses;

import com.company.models.Receipt;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReceiptsFileSaver implements ReceiptsSaver {

    public static void saveAsFileTxt(Receipt receipt) {
        String filePath = CONSTANTS.pathToStoresDirectories + receipt.getSupermarket().getName() + CONSTANTS.pathToStoreReceiptsDirectories + "\\" + receipt.getId() + CONSTANTS.TXT;

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(getFormattedReceipt(receipt));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readFromFileTxt(String filePath) {
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveAsFileSer(Receipt receipt) {
        String filePath = CONSTANTS.pathToStoresDirectories + receipt.getSupermarket().getName() + CONSTANTS.pathToStoreReceiptsDirectories + receipt.getId() + CONSTANTS.SER;

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

    private static String getFormattedReceipt(Receipt receipt) {
        final String fill = new String(new char[40]).replace("\0", "-") + "\n";
        final int fullLength = 40;
        StringBuilder sb = new StringBuilder();
        // Касовата бележка трябва да съдържат минимум следната информация: пореден номер,
        // касиер, който издава касовата бележка, дата и час на издаване на касовата бележка, списък със
        // стоки, които се включват в касовата бележка включително цената и количеството им и общата
        // стойност, която трябва да се заплати от клиента.

        // header
        sb.append("\n\n");
        sb.append( new String(new char[(fullLength-"RECEIPT FROM".length())/2]).replace("\0", " "));
        sb.append("RECEIPT FROM");
        sb.append( new String(new char[(fullLength-"RECEIPT FROM".length())/2]).replace("\0", " "));
        sb.append("\n");
        int nameLength = receipt.getSupermarket().getName().length();
        sb.append( new String(new char[(fullLength-nameLength)/2]).replace("\0", " "));
        sb.append(receipt.getSupermarket().getName().toUpperCase(Locale.ROOT));
        sb.append( new String(new char[(fullLength-nameLength)/2]).replace("\0", " "));
        sb.append("\n\n");
        sb.append(fill);

        // meta data
        String idMetadata = "Receipt Nr. " + receipt.getId();
        sb.append( new String(new char[(fullLength-idMetadata.length())/2]).replace("\0", " "));
        sb.append(idMetadata);
        sb.append( new String(new char[(fullLength-idMetadata.length())/2]).replace("\0", " "));
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
        sb.append(new String(new char[(fullLength - servedByLength)/2]).replace("\0", " "));
        sb.append(servedBy);
        sb.append(new String(new char[(fullLength - servedByLength)/2]).replace("\0", " "));
        sb.append("\n");
        sb.append(fill);
        sb.append("\n");
        sb.append(new String(new char[15]).replace("\0", " "));
        sb.append("THANK YOU!");
        sb.append(new String(new char[15]).replace("\0", " "));
        sb.append("\n\n");

        return sb.toString();
    }
}
