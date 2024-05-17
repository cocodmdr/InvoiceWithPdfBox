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

public class GeneratePdf {
    private final TableCreator tableCreator = new TableCreator();
    private final PDPageContentStream contentStream;
    private final PDDocument document;
    private final PDPage page;
    private static final float PADDING = 50f;

    public GeneratePdf() throws IOException {
        document = new PDDocument();
        page = new PDPage();
        document.addPage(page);
        System.out.println("Page created and added to document");
        contentStream = new PDPageContentStream(document, page);
    }

    public void createPdf(PdfConfig config) throws IOException {
        System.out.println("creating PDF");
        addInformation(config);
        closeAndSaveFile(config);
    }

    private void addInformation(PdfConfig config) throws IOException {
        if (config.logoPath != null) {
            addLogo(config.logoPath);
        }
        addText(config);
        if (config.tableData != null && config.tableHeaders != null) {
            addTable(config.tableData, config.tableHeaders);
        }
    }

    private void closeAndSaveFile(PdfConfig config) throws IOException {
        contentStream.close();
        if (config.fileName != null) {
            document.save(config.fileName);
            System.out.println("PDF saved as: " + config.fileName);
        } else {
            document.save("helloWorld.pdf");
            System.err.println("Warning: No name provided, PDF saved as helloWorld.pdf");
        }
        document.close();
    }

    private void addText(PdfConfig config) throws IOException {
        addInvoiceNumber(config);
        addPaidDate(config);
        addCompanyDetails(config.companyDetails);
        addClientDetails(config.clientDetails);
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

    private void addCompanyDetails(String[] companyDetails) throws IOException {
        if (companyDetails != null) {
            writeLines(50, 700, PDType1Font.HELVETICA, 12, companyDetails);
        } else {
            System.err.println("Warning: No company details provided");
        }
    }

    private void addClientDetails(String[] clientDetails) throws IOException {
        if (clientDetails != null) {
            writeLines(350, 650, PDType1Font.HELVETICA, 12, clientDetails);
        } else {
            System.err.println("Warning: No client details provided");
        }
    }

    private void addInvoiceNumber(PdfConfig config) throws IOException {
        if (config.invoiceNumber != null && config.title != null) {
            writeLines(350, 750, PDType1Font.HELVETICA_BOLD, 20, config.title + " " + config.invoiceNumber);
        } else {
            System.err.println("Warning: No title or invoice number provided");
        }
    }

    private void addPaidDate(PdfConfig config) throws IOException {
        if (config.paidOnSentence != null && config.invoiceDate != null) {
            writeLines(350, 730, PDType1Font.HELVETICA, 12, config.paidOnSentence + " " + config.invoiceDate);
        } else {
            System.err.println("Warning: No paid on sentence or invoice date provided" );
        }
    }

    public void addTable(Object[][] data, String[] tableHeaders) throws IOException {
        Table table = tableCreator.createTableWithBoughtItems(data, tableHeaders);
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
