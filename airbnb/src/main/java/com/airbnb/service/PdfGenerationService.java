package com.airbnb.service;

import com.airbnb.dto.BookingDto;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class PdfGenerationService {

    public boolean generateBookingPdf(String fileName, BookingDto dto) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));

            document.open();

            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
            Paragraph title = new Paragraph("Booking Confirmation", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Create a table with 2 columns
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Set table column widths
            float[] columnWidths = {1f, 2f};
            table.setWidths(columnWidths);

            // Add table headers
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            PdfPCell cell;

            cell = new PdfPCell(new Phrase("Field", headFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Details", headFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            // Add table data
            table.addCell("Booking ID");
            table.addCell(Long.toString(dto.getBookingId()));

            table.addCell("Guest Name");
            table.addCell(dto.getGuestName());

            table.addCell("Price per Night");
            table.addCell(Integer.toString(dto.getPrice()));

            table.addCell("Total Price");
            table.addCell(Integer.toString(dto.getTotalPrice()));

            document.add(table);
            document.close();

            return true;
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            return false;
        }
    }
}
