package org.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.structure.Table;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneratePdf {
    private final TableCreator tableCreator = new TableCreator();
    private PDPageContentStream contentStream;
    private PDDocument document;
    private PDPage page;
    private static final float PADDING = 50f;

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
        System.out.println("Page created and added to document");
        contentStream = new PDPageContentStream(document, page);
    }

    public void createPdf() throws IOException {
        createPdf("helloWorld.pdf");
    }

    public void createPdf(String fileName) throws IOException {
        System.out.println("creating PDF");
        addLogo("logo.png");
        addText();
        addTable();

        contentStream.close();
        document.save(fileName);
        document.close();
        System.out.println("PDF saved as: " + fileName);
    }

    private void addText() throws IOException {
        addInvoiceNumber("123456");
        addPaidDate("25-03-2024");
        addCompanyDetails("Acme Inc.", "123 Main St", "Anytown, USA");
        addClientDetails("John Doe", "456 High St", "Othercity, USA");
    }

    private void writeLines(int x, int y, PDType1Font font, int fontSize, String... lines ) throws IOException {
        contentStream.setFont(font, fontSize);
        contentStream.beginText();
        for (final String line : lines) {
            contentStream.newLineAtOffset(x, y); // Start position
            contentStream.showText(line);
            y =- 20;
            x = 0;
        }
        contentStream.endText();
    }

    private void addCompanyDetails(String name, String address, String city) throws IOException {
        writeLines(50, 700, PDType1Font.HELVETICA, 12, name, address, city);
    }

    private void addClientDetails(String name, String address, String city) throws IOException {
        writeLines(350, 650, PDType1Font.HELVETICA, 12, name, address, city);
    }

    private void addInvoiceNumber(String number) throws IOException {
        writeLines(350, 750, PDType1Font.HELVETICA_BOLD, 20, "Invoice " + number);
    }

    private void addPaidDate(String date) throws IOException {
        writeLines(350, 730, PDType1Font.HELVETICA, 12, "Paid on: " + date);
    }

    public void addTable() throws IOException {
        Table table = tableCreator.createTableWithBoughtItems(DATA);
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

    private void addLogo(String logoPath) throws IOException {
        PDImageXObject logoImage = PDImageXObject.createFromFile(logoPath, document);
        int x = 50;
        int y = 700;
        contentStream.drawImage(logoImage, x, y, 100, 100);
    }
}
