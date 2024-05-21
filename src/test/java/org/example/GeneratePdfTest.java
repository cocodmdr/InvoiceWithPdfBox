package org.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class GeneratePdfTest {

    static String pdfContent = "";


    private final static PdfConfig config = new PdfConfig();

    static void configurePdf(){
        config.fileName = "output.pdf";
        config.title = "Invoice";
        config.logoPath ="logo.png";
        config.invoiceNumber = "1234567";
        config.paidOnSentence = "Paid on";
        config.invoiceDate = "25-03-2024";
        config.companyDetails = new String[]{"Acme Inc.", "123 Main Street", "Anytown, USA"};
        config.clientDetails = new String[]{"John Doe","456 High Street","Othercity, USA"};
        config.accountStatement = new String[]{"Account Statement No. ","Date of Account Statement: ","This document serves as an invoice issued in the name and on behalf of the manager."};
        config.accountStatementNumber = "12345678";
        config.accountStatementDate = "25/05/2024";
        config.tableData = new Object[][]{{1, "hammer", 15.0, 20.0}, {2, "nails",   5.0, 10.0}, {3, "wood", 45.0, 0.0}, {4, "screws",  59.99, 19.99}};
        config.tableHeaders = new String[]{"Quantity", "Description", "Price", "Tax", "Amount"};
        config.legalDetails = new String[]{"This is a computer-generated invoice and does not require a signature.",
                "For any inquiries regarding this invoice, please contact our customer service at [contact information].",
                "All payments should be made in [currency] to [payment details]."};
    }

    @BeforeAll
    static void setUp() throws IOException{
        configurePdf();
        GeneratePdf pdfGenerator = new GeneratePdf();
        pdfGenerator.createPdf(config);
        PDFTextStripper pdfStripper = new PDFTextStripper();
        try (PDDocument document = PDDocument.load(new File(config.fileName))) {
            pdfContent = pdfStripper.getText(document);
        }
    }

    static void expectPdfContains(String expectedString){
        assertTrue(pdfContent.contains(expectedString), "PDF does not contain the word \"" + expectedString + "\"");
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsATitle() throws IOException {
        expectPdfContains(config.title);
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsInvoiceNumber() {
        expectPdfContains(config.invoiceNumber);
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsCompanyDetails() {
        for(final String detail : config.companyDetails) {
            expectPdfContains(detail);
        }
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsClientsDetails() {
        for(final String detail : config.clientDetails) {
            expectPdfContains(detail);
        }
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsTheAccountStatement() {
        for(final String text : config.accountStatement) {
            expectPdfContains(text);
        }
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsTheAccountStatementNumber() {
        expectPdfContains(config.accountStatementNumber);
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsTheAccountStatementDate() {
        expectPdfContains(config.accountStatementDate);
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsInvoiceDate() {
        expectPdfContains(config.paidOnSentence + " " + config.invoiceDate);
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsCorrectNumberOfPages() throws IOException {
        try (PDDocument document = PDDocument.load(new File(config.fileName))) {
            assertEquals(1, document.getNumberOfPages(), "PDF contains incorrect number of pages");
        }
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsTableHeader() {
        for(final String header : config.tableHeaders) {
            expectPdfContains(header);
        }
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsNumberOfItems() {
        for (final Object[] dataRow : config.tableData) {
            expectPdfContains(String.valueOf(dataRow[0]));
        }
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsDescriptionOfItems() {
        for (final Object[] dataRow : config.tableData) {
            expectPdfContains((String) dataRow[1]);
        }
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsPriceOfItems() {
        for (final Object[] dataRow : config.tableData) {
            expectPdfContains(String.valueOf(dataRow[2]) + " €");
        }
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsTaxPercentageOfItems() {
        for (final Object[] dataRow : config.tableData) {
            expectPdfContains(String.valueOf(dataRow[3]) + " %");
        }
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsTotalPerItems() {
        expectPdfContains("15.0 €");
        expectPdfContains("10.0 €");
        expectPdfContains("135.0 €");
        expectPdfContains("239.96 €");
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsTotalWithoutTax() {
        expectPdfContains("Subtotal");
        expectPdfContains("399.96 €");
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsTaxTotal() {
        expectPdfContains("Total tax");
        expectPdfContains("51.97 €");
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsTotalWithTax() {
        expectPdfContains("Total");
        expectPdfContains("399.96 €");
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsTheLegalMentions() {
        for(final String text : config.legalDetails) {
            expectPdfContains(text);
        }
    }
}