package com.example.collegeproject;

import model.bookings;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class report {

    public static void generateReport(String dateRange) {
        List<bookings> bookingsList = DBUtils.getAllBookings(dateRange);
        String fileName = "Bookings_Report.pdf";

        double totalRevenue = 0.0;

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(doc, page);

            // Title
            cs.beginText();
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
            cs.newLineAtOffset(200, 750);
            cs.showText("Bookings Report");
            cs.endText();

            // Subtitle
            cs.beginText();
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
            cs.newLineAtOffset(250, 725);
            cs.showText("Date Range: " + dateRange);
            cs.endText();

            // --- START OF MODIFIED SECTION ---

            // 1. Define Column Positions (ID removed, others adjusted)
            float yPosition = 690;
            float margin = 40;
            float col1_x = margin;       // SLOT
            float col2_x = 90;       // VEHICLE NO
            float col3_x = 170;      // VEHICLE TYPE
            float col4_x = 250;      // BOOKING TIME
            float col5_x = 360;      // EXIT TIME
            float col6_x = 470;      // STATUS
            float col7_x = 520;      // REVENUE

            // 2. Draw Table Header (ID removed)
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);

            // -- "ID" column removed --
            cs.beginText(); cs.newLineAtOffset(col1_x, yPosition); cs.showText("SLOT"); cs.endText();
            cs.beginText(); cs.newLineAtOffset(col2_x, yPosition); cs.showText("VEHICLE NO"); cs.endText();
            cs.beginText(); cs.newLineAtOffset(col3_x, yPosition); cs.showText("VEHICLE TYPE"); cs.endText();
            cs.beginText(); cs.newLineAtOffset(col4_x, yPosition); cs.showText("BOOKING TIME"); cs.endText();
            cs.beginText(); cs.newLineAtOffset(col5_x, yPosition); cs.showText("EXIT TIME"); cs.endText();
            cs.beginText(); cs.newLineAtOffset(col6_x, yPosition); cs.showText("STATUS"); cs.endText();
            cs.beginText(); cs.newLineAtOffset(col7_x, yPosition); cs.showText("REVENUE"); cs.endText();

            // Draw a line under header
            yPosition -= 10;
            cs.moveTo(margin, yPosition);
            cs.lineTo(590, yPosition); // Extended line
            cs.stroke();

            // 3. Draw Table Data
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            yPosition -= 20;

            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);

            for (bookings b : bookingsList) {
                if (yPosition < 80) {
                    cs.close();
                    page = new PDPage();
                    doc.addPage(page);
                    cs = new PDPageContentStream(doc, page);
                    yPosition = 700;
                }

                // Draw each piece of data at the correct X-position
                // -- "ID" data removed --
                cs.beginText(); cs.newLineAtOffset(col1_x, yPosition); cs.showText(b.getSlotName()); cs.endText();
                cs.beginText(); cs.newLineAtOffset(col2_x, yPosition); cs.showText(b.getVehicleNumber()); cs.endText();
                cs.beginText(); cs.newLineAtOffset(col3_x, yPosition); cs.showText(b.getVehicleType()); cs.endText();
                cs.beginText(); cs.newLineAtOffset(col4_x, yPosition); cs.showText(sdf.format(b.getBookingTime())); cs.endText();

                String exitTimeStr = (b.getExitTime() != null) ? sdf.format(b.getExitTime()) : "N/A";
                cs.beginText(); cs.newLineAtOffset(col5_x, yPosition); cs.showText(exitTimeStr); cs.endText();

                cs.beginText(); cs.newLineAtOffset(col6_x, yPosition); cs.showText(b.getStatus()); cs.endText();

                String revenueStr = (b.getRevenue() != null) ? String.format(Locale.US, "%.2f", b.getRevenue()) : "N/A";
                cs.beginText(); cs.newLineAtOffset(col7_x, yPosition); cs.showText(revenueStr); cs.endText();

                totalRevenue += (b.getRevenue() != null) ? b.getRevenue() : 0.0;

                yPosition -= 20;
            }

            // --- Draw Total Revenue (Positions updated) ---
            yPosition -= 10;
            cs.moveTo(margin, yPosition);
            cs.lineTo(590, yPosition);
            cs.stroke();
            yPosition -= 20;

            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);

            cs.beginText();
            cs.newLineAtOffset(col6_x - 10, yPosition); // Position "Total:" text (uses col6_x)
            cs.showText("Total:");
            cs.endText();

            cs.beginText();
            cs.newLineAtOffset(col7_x, yPosition); // Position the total amount (uses col7_x)
            cs.showText(String.format(Locale.US, "%.2f", totalRevenue));
            cs.endText();

            // --- END OF MODIFIED SECTION ---

            cs.close();
            doc.save(fileName);
            System.out.println("Report generated successfully: " + fileName);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error generating report. Check your 'bookings' model class methods.");
        }
    }
}