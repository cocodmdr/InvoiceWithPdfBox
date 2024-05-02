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

    Table createTableWithBoughtItems(Object[][] data) throws IOException {

        final Table.TableBuilder tableBuilder = Table.builder()
                .addColumnsOfWidth(100, 200, 100, 100)
                .fontSize(8)
                .font(new PDType1Font(PDType1Font.HELVETICA.getCOSObject()))
                .borderColor(Color.WHITE);

        addHeader(tableBuilder);
        addBoughtItems(data, tableBuilder);
        addTotalRow(tableBuilder, calculateTotal(data));

        return tableBuilder.build();
    }

    private static void addTotalRow(Table.TableBuilder tableBuilder, double grandTotal) throws IOException {
        tableBuilder.addRow(Row.builder()
                .add(TextCell.builder().text("Total")
                        .colSpan(3)
                        .lineSpacing(1f)
                        .borderWidthTop(1)
                        .textColor(Color.WHITE)
                        .backgroundColor(BLUE_DARK)
                        .fontSize(9)
                        .font(new PDType1Font(PDType1Font.HELVETICA_OBLIQUE.getCOSObject()))
                        .borderWidth(1)
                        .build())
                .add(TextCell.builder().text( String.format("%.2f", grandTotal) + " €").backgroundColor(Color.LIGHT_GRAY)
                        .font(new PDType1Font(PDType1Font.HELVETICA_BOLD_OBLIQUE.getCOSObject()))
                        .verticalAlignment(TOP)
                        .borderWidth(1)
                        .build())
                .horizontalAlignment(RIGHT)
                .build());
    }

    private static double calculateTotal(Object[][] data) {
        double grandTotal = 0;
        for (final Object[] dataRow : data) {
            int quantity = (int) dataRow[0];
            double price = (double) dataRow[2];
            final double total = quantity * price;
            grandTotal += total;
        }
        return grandTotal;
    }

    private static void addBoughtItems(Object[][] data, Table.TableBuilder tableBuilder) {
        for (int i = 0; i < data.length; i++) {
            final Object[] dataRow = data[i];
            int quantity = (int) dataRow[0];
            double price = (double) dataRow[2];
            final double total = quantity * price;

            tableBuilder.addRow(Row.builder()
                    .add(TextCell.builder().text(String.valueOf(dataRow[0])).borderWidth(1).build())
                    .add(TextCell.builder().text(String.valueOf(dataRow[1])).horizontalAlignment(LEFT).borderWidth(1).build())
                    .add(TextCell.builder().text(dataRow[2] + " €").borderWidth(1).build())
                    .add(TextCell.builder().text(total + " €").borderWidth(1).build())
                    .backgroundColor(i % 2 == 0 ? BLUE_LIGHT_1 : BLUE_LIGHT_2)
                    .horizontalAlignment(RIGHT)
                    .build());
        }
    }

    private static void addHeader(Table.TableBuilder tableBuilder) throws IOException {
        tableBuilder.addRow(Row.builder()
                .add(TextCell.builder().text("Quantity").horizontalAlignment(LEFT).borderWidth(1).build())
                .add(TextCell.builder().text("Description").borderWidth(1).build())
                .add(TextCell.builder().text("Price").borderWidth(1).build())
                .add(TextCell.builder().text("Amount").borderWidth(1).build())
                .backgroundColor(BLUE_DARK)
                .textColor(Color.WHITE)
                .font(new PDType1Font(PDType1Font.HELVETICA_BOLD.getCOSObject()))
                .fontSize(9)
                .horizontalAlignment(CENTER)
                .build());
    }
}