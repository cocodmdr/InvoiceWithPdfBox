package org.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.structure.Table;

import java.awt.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneratePdf {
    private final TableCreator tableCreator = new TableCreator();
    private PDPageContentStream contentStream;
    private PDDocument document;
    private PDPage page;
    int rowNum = 0;
    TableBuilder tableBuilder;
    private static final float PADDING = 50f;

    private final static Color BLUE_DARK = new Color(76, 129, 190);
    private final static Color BLUE_LIGHT_1 = new Color(186, 206, 230);
    private final static Color BLUE_LIGHT_2 = new Color(218, 230, 242);

    private final static Color GRAY_LIGHT_1 = new Color(245, 245, 245);
    private final static Color GRAY_LIGHT_2 = new Color(240, 240, 240);
    private final static Color GRAY_LIGHT_3 = new Color(216, 216, 216);

    private final static Object[][] DATA = new Object[][]{
            {1, "hammer", 15.0},
            {2, "nails",   5.0},
            {3, "wood", 45.0},
            {4, "screws",  59.99}
    };

    public GeneratePdf() throws IOException {
        document = new PDDocument();
        page = new PDPage();
        document.addPage(page);
        tableBuilder = Table.builder();
        System.out.println("Page created and added to document");
        contentStream = new PDPageContentStream(document, page);
    }

    public void createPdf() throws IOException {
        createPdf("helloWorld.pdf");
    }

    public void createPdf(String fileName) throws IOException {
        System.out.println("creating PDF");

        addInvoiceNumber("123456"); // Add invoice number
        addInvoiceDate(); // Add invoice date
        addCompanyDetails("Acme Inc.", "123 Main St", "Anytown, USA"); // Add company details
        addClientDetails("John Doe", "456 High St", "Othercity, USA"); // Add client details
        addTitle("Invoice"); // Add title "Invoice"

        drawTable();

        contentStream.close();
        document.save(fileName);
        document.close();
        System.out.println("PDF saved as: " + fileName);
    }

    private void addTitle(String title) throws IOException {
        System.out.println("Adding title: " + title);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
        contentStream.beginText();
        contentStream.newLineAtOffset(100, 600); // Adjust position as needed
        contentStream.showText(title);
        contentStream.endText();
    }

    private void addCompanyDetails(String name, String address, String city) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 750); // Start position
        contentStream.showText("Name: " + name);
        contentStream.newLineAtOffset(0, -20); // Move down by 20 points
        contentStream.showText("Address: " + address);
        contentStream.newLineAtOffset(0, -20); // Move down by 20 points
        contentStream.showText("City: " + city);
        contentStream.endText();
    }

    private void addClientDetails(String name, String address, String city) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(350, 750); // Start position (right side)
        contentStream.showText("Client Name: " + name);
        contentStream.newLineAtOffset(0, -20); // Move down by 20 points
        contentStream.showText("Client Address: " + address);
        contentStream.newLineAtOffset(0, -20); // Move down by 20 points
        contentStream.showText("Client City: " + city);
        contentStream.endText();
    }

    private void addInvoiceNumber(String number) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(350, 550); // Adjust position as needed
        contentStream.showText("Invoice Number: " + number);
        contentStream.endText();
    }

    private void addInvoiceDate() throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(350, 530); // Adjust position as needed
        contentStream.showText("Invoice Date: " + dateFormat.format(date));
        contentStream.endText();
    }

    public void drawTable() throws IOException {
        // Build the table
        Table table = tableCreator.createTableWithBoughtItems(DATA);

//        float startY = page.getMediaBox().getHeight() - PADDING;
        float startY = 400f;

        TableDrawer.builder()
                        .page(page)
                        .contentStream(contentStream)
                        .table(table)
                        .startX(PADDING)
                        .startY(startY)
                        .endY(PADDING)
                        .build()
                        .draw(() -> document, () -> new PDPage(PDRectangle.A4), PADDING);

    }
}
