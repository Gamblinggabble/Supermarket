package com.company.helperClasses;

import com.company.models.Supermarket;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;

public class ReportsFileSaver {

    public static void saveAsFileTxt(Supermarket supermarket, LocalDateTime dateOfReport) {
        String date = String.format("%02d-%02d-%4d_%02d-%02d-%02d", dateOfReport.getDayOfMonth(), dateOfReport.getMonthValue(), dateOfReport.getYear(),
                dateOfReport.getHour(), dateOfReport.getMinute(), dateOfReport.getSecond());

        String filePath = CONSTANTS.pathToStoresDirectories + supermarket.getName() + CONSTANTS.pathToStoreReportsDirectories + date + CONSTANTS.TXT;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(getFormattedReport(supermarket, dateOfReport));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readFromFileTxt(String supermarketName, LocalDateTime dateOfReport) {
        String date = String.format("%02d-%02d-%4d_%02d-%02d-%02d", dateOfReport.getDayOfMonth(), dateOfReport.getMonthValue(), dateOfReport.getYear(),
                dateOfReport.getHour(), dateOfReport.getMinute(), dateOfReport.getSecond());
        String filePath = CONSTANTS.pathToStoresDirectories + supermarketName + CONSTANTS.pathToStoreReportsDirectories + date + CONSTANTS.TXT;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFormattedReport(Supermarket supermarket, LocalDateTime dateOfReport) {
        final int fullLength = 40;
        final String fill = new String(new char[40]).replace("\0", "-") + "\n";

        StringBuilder sb = new StringBuilder();

        // header
        sb.append("\n\n");
        sb.append(new String(new char[(fullLength - "REPORT FOR".length()) / 2]).replace("\0", " "));
        sb.append("REPORT FOR");
        sb.append(new String(new char[(fullLength - "REPORT FOR".length()) / 2]).replace("\0", " "));
        sb.append("\n");
        int nameLength = supermarket.getName().length();
        sb.append(new String(new char[(fullLength - nameLength) / 2]).replace("\0", " "));
        sb.append(supermarket.getName().toUpperCase(Locale.ROOT));
        sb.append(new String(new char[(fullLength - nameLength) / 2]).replace("\0", " "));
        sb.append("\n\n");
        sb.append(fill);

        // report date
        String dateStr = String.format("%10s%02d/%02d/%4d %02d:%02d:%02d%10s", " ", dateOfReport.getDayOfMonth(), dateOfReport.getMonthValue(), dateOfReport.getYear(),
                dateOfReport.getHour(), dateOfReport.getMinute(), dateOfReport.getSecond(), " ");
        sb.append(dateStr);
        sb.append("\n");
        sb.append(fill);
        sb.append("\n");

        // report data
        sb.append(ReportsFileSaver.formatReportDateLine("Expenses", '-', supermarket.salariesForAMonth().add(supermarket.deliveriesCost()), fullLength - 3));
        sb.append(ReportsFileSaver.formatReportDateLine("   salaries expenses", '-', supermarket.salariesForAMonth(), fullLength));
        sb.append(ReportsFileSaver.formatReportDateLine("   deliveries costs", '-', supermarket.deliveriesCost(), fullLength));
        sb.append(ReportsFileSaver.formatReportDateLine("Income", '+', supermarket.incomeSoldProducts(), fullLength - 3));
        sb.append(ReportsFileSaver.formatReportDateLine("   food products income", '+', supermarket.incomeSoldProductsByCategory(ProductCategory.FOOD), fullLength));
        sb.append(ReportsFileSaver.formatReportDateLine("   non-food products income", '+', supermarket.incomeSoldProductsByCategory(ProductCategory.NON_FOOD), fullLength));
        sb.append("\n");
        sb.append("\n");
        sb.append(String.format("FINAL SUM:  %28.2f", supermarket.incomeSoldProducts().subtract(supermarket.deliveriesCost().add(supermarket.salariesForAMonth()))));
        sb.append("\n");
        sb.append(fill);

        return sb.toString();
    }

    private static String formatReportDateLine(String dataCategory, char sign, BigDecimal sum, int totalLength) {
        dataCategory += " ";
        String sumStr = String.format("%.2f", sum);

        StringBuilder sb = new StringBuilder();
        sb.append(dataCategory + new String(new char[totalLength - (dataCategory.length() + 1 + sumStr.length())]).replace("\0", " ") + sign + sumStr);
        sb.append("\n");

        return sb.toString();
    }
}
