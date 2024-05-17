package org.example;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.cell.TextCell;

import java.awt.*;
import java.io.IOException;
import static org.vandeseer.easytable.settings.HorizontalAlignment.*;
import static org.vandeseer.easytable.settings.VerticalAlignment.*;


public class TableCreator {

    private final static Color BLUE_DARK = new Color(76, 129, 190);
    private final static Color BLUE_LIGHT_1 = new Color(186, 206, 230);
    private final static Color BLUE_LIGHT_2 = new Color(218, 230, 242);

    public TableCreator() {
    }

    Table createTableWithBoughtItems(Object[][] data, String[] dataHeaders) throws IOException {

        final Table.TableBuilder tableBuilder = Table.builder()
                .addColumnsOfWidth(50, 200, 70, 70, 70)
                .fontSize(8)
                .font(new PDType1Font(PDType1Font.HELVETICA.getCOSObject()))
                .borderColor(Color.WHITE);
        addHeader(tableBuilder, dataHeaders);
        addBoughtItems(data, tableBuilder);
        addSubTotalRow(tableBuilder, data);
        addTotalTaxRow(tableBuilder, data);
        addGrandTotalRow(tableBuilder, data);

        return tableBuilder.build();
    }

    private static void addTotalRow(Table.TableBuilder tableBuilder, String text, double total) throws IOException {
        tableBuilder.addRow(Row.builder()
                .add(TextCell.builder().text(text)
                        .colSpan(4)
                        .lineSpacing(1f)
                        .borderWidthTop(1)
                        .textColor(Color.WHITE)
                        .backgroundColor(BLUE_DARK)
                        .fontSize(9)
                        .font(new PDType1Font(PDType1Font.HELVETICA_OBLIQUE.getCOSObject()))
                        .borderWidth(1)
                        .build())
                .add(TextCell.builder().text( String.format("%.2f", total) + " €").backgroundColor(Color.LIGHT_GRAY)
                        .font(new PDType1Font(PDType1Font.HELVETICA_BOLD_OBLIQUE.getCOSObject()))
                        .verticalAlignment(TOP)
                        .borderWidth(1)
                        .build())
                .horizontalAlignment(RIGHT)
                .build());
    }

    private static void addSubTotalRow(Table.TableBuilder tableBuilder, Object[][] data) throws IOException {
        addTotalRow(tableBuilder, "Subtotal", calculateTotal(data));
    }

    private static void addTotalTaxRow(Table.TableBuilder tableBuilder, Object[][] data) throws IOException {
        addTotalRow(tableBuilder,"Total tax", calculateTotalTax(data));
    }

    private static void addGrandTotalRow(Table.TableBuilder tableBuilder, Object[][] data) throws IOException {
        addTotalRow(tableBuilder,"Total", calculateTotal(data) + calculateTotalTax(data));
    }

    private static double calculateTotalTax(Object[][] data) {
        double taxTotal = 0;
        for (final Object[] dataRow : data) {
            double taxPercent = (double) dataRow[3]/100.0;
            taxTotal += getTotalOfRow(dataRow) * taxPercent;
        }
        return taxTotal;
    }

    private static double calculateTotal(Object[][] data) {
        double grandTotal = 0;
        for (final Object[] dataRow : data) {
            grandTotal += getTotalOfRow(dataRow);
        }
        return grandTotal;
    }

    private static double getTotalOfRow(Object[] dataRow) {
            int quantity = (int) dataRow[0];
            double price = (double) dataRow[2];
            return quantity * price;
    }

    private static void addBoughtItems(Object[][] data, Table.TableBuilder tableBuilder) {
        for (int i = 0; i < data.length; i++) {
            final Object[] dataRow = data[i];
            Color color = i % 2 == 0 ? BLUE_LIGHT_1 : BLUE_LIGHT_2;
            addRow(tableBuilder, dataRow, getTotalOfRow(dataRow), color);
        }
    }

    private static void addRow(Table.TableBuilder tableBuilder, Object[] dataRow, double total, Color color ) {
            tableBuilder.addRow(Row.builder()
                    .add(TextCell.builder().text(String.valueOf(dataRow[0])).borderWidth(1).build())
                    .add(TextCell.builder().text(String.valueOf(dataRow[1])).horizontalAlignment(LEFT).borderWidth(1).build())
                    .add(TextCell.builder().text(dataRow[2] + " €").borderWidth(1).build())
                    .add(TextCell.builder().text(dataRow[3] + " %").borderWidth(1).build())
                    .add(TextCell.builder().text(total + " €").borderWidth(1).build())
                    .backgroundColor(color)
                    .horizontalAlignment(RIGHT)
                    .build());
    }

    private static void addHeader(Table.TableBuilder tableBuilder, String[] headers) throws IOException {
        tableBuilder.addRow(Row.builder()
                .add(TextCell.builder().text(String.valueOf(headers[0])).horizontalAlignment(LEFT).borderWidth(1).build())
                .add(TextCell.builder().text(String.valueOf(headers[1])).borderWidth(1).build())
                .add(TextCell.builder().text(String.valueOf(headers[2])).borderWidth(1).build())
                .add(TextCell.builder().text(String.valueOf(headers[3])).borderWidth(1).build())
                .add(TextCell.builder().text(String.valueOf(headers[4])).borderWidth(1).build())
                .backgroundColor(BLUE_DARK)
                .textColor(Color.WHITE)
                .font(new PDType1Font(PDType1Font.HELVETICA_BOLD.getCOSObject()))
                .fontSize(9)
                .horizontalAlignment(CENTER)
                .build());
    }
}