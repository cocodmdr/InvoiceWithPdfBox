package org.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class GeneratePdfTest {

    static String filePath = "output.pdf";
    static String pdfContent = "";
    static String invoiceNumber = "123456";
    static String invoiceDate;

    @BeforeAll
    static void setUp() throws IOException{
        GeneratePdf pdfGenerator = new GeneratePdf();
        pdfGenerator.createPdf(filePath);
        PDFTextStripper pdfStripper = new PDFTextStripper();
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            pdfContent = pdfStripper.getText(document);
        }
        updateDate();
    }

    static void updateDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        invoiceDate = dateFormat.format(date);
    }

    static void expectPdfContains(String expectedString){
        assertTrue(pdfContent.contains(expectedString), "PDF does not contain the word \"" + expectedString + "\"");
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsATitle() throws IOException {
        expectPdfContains("Invoice");
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsCompanyDetails() {
        expectPdfContains("Name: Acme Inc.");
        expectPdfContains("Address: 123 Main St");
        expectPdfContains("City: Anytown, USA");
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsClientsDetails() {
        expectPdfContains("Client Name: John Doe");
        expectPdfContains("Client Address: 456 High St");
        expectPdfContains("Client City: Othercity, USA");
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsInvoiceNumber() {
        expectPdfContains("Invoice Number: " + invoiceNumber);
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsInvoiceDate() {
        expectPdfContains("Invoice Date: " + invoiceDate);
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsCorrectNumberOfPages() throws IOException {
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            assertEquals(1, document.getNumberOfPages(), "PDF contains incorrect number of pages");
        }
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsTableHeader() {
        expectPdfContains("Quantity");
        expectPdfContains("Description");
        expectPdfContains("Price");
        expectPdfContains("Amount");
    }

    @Test
    void GivenAPdfIsGeneratedThenItContainsTableRow() {
        expectPdfContains("1");
        expectPdfContains("2");
        expectPdfContains("3");
        expectPdfContains("4");
        expectPdfContains("hammer");
        expectPdfContains("nails");
        expectPdfContains("wood");
        expectPdfContains("screws");
        expectPdfContains("15.0 €");
        expectPdfContains("5.0 €");
        expectPdfContains("45.0 €");
        expectPdfContains("59.99 €");
        expectPdfContains("15.0 €");
        expectPdfContains("10.0 €");
        expectPdfContains("135.0 €");
        expectPdfContains("239.96 €");
        expectPdfContains("Total");
        expectPdfContains("399.96 €");
    }

    @Test
    void GivenAPdfIsGeneratedWithoutSpecifyingTheNameThenItIsCalledHelloWorldPdf() throws IOException {
        GeneratePdf pdfGenerator = new GeneratePdf();
        pdfGenerator.createPdf();
        PDFTextStripper pdfStripper = new PDFTextStripper();
        try (PDDocument document = PDDocument.load(new File("helloWorld.pdf"))) {
            pdfContent = pdfStripper.getText(document);
        }
    }
}