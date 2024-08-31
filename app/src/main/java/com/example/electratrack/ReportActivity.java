package com.example.electratrack;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReportActivity extends AppCompatActivity {

    private DatabaseReference userRef;
    private FirebaseAuth auth;
    private String userID;
    private ImageView back,recent,myImageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        userID = currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userID).child("Transaction");

        Button todayButton = findViewById(R.id.todayReportButton);
        back=findViewById(R.id.back);
        myImageview=findViewById(R.id.myImageView);
        recent=findViewById(R.id.recent);
        Button last10DaysButton = findViewById(R.id.last10DaysReportButton);
        Button last20DaysButton = findViewById(R.id.last20DaysReportButton);
        Button last30DaysButton = findViewById(R.id.last30DaysReportButton);
        Button allButton = findViewById(R.id.allReportButton);

        todayButton.setOnClickListener(v -> generateReportPdf("today"));
        last10DaysButton.setOnClickListener(v -> generateReportPdf("last10days"));
        last20DaysButton.setOnClickListener(v -> generateReportPdf("last20days"));
        last30DaysButton.setOnClickListener(v -> generateReportPdf("last30days"));
        allButton.setOnClickListener(v -> generateReportPdf("all"));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportActivity.this,MainActivity.class));
            }
        });
        recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportActivity.this,recent_trans.class));
            }
        });
        myImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportActivity.this,add_expense.class));
            }
        });
    }

    private void generateReportPdf(String reportType) {
        try {
            File pdfDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            if (pdfDir == null) {
                Toast.makeText(this, "Unable to access external files directory", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pdfDir.exists()) {
                pdfDir.mkdirs();
            }

            String fileName = "report_" + System.currentTimeMillis() + ".pdf";
            File pdfFile = new File(pdfDir, fileName);

            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Expense Report")
                    .setFontSize(24)
                    .setBold()
                    .setFontColor(new DeviceRgb(0, 0, 0))
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

            document.add(new Paragraph("Report Type: " + reportType)
                    .setFontSize(18)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

            float[] columnWidths = {2, 4, 2, 4};
            Table table = new Table(columnWidths);
            table.addCell("Date");
            table.addCell("Category");
            table.addCell("Amount");
            table.addCell("Description");

            fetchReportData(document, table, reportType, pdfFile);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to generate PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void fetchReportData(Document document, Table table, String reportType, File pdfFile) {
        LocalDate startDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        switch (reportType) {
            case "today":
                startDate = LocalDate.now();
                break;
            case "last10days":
                startDate = LocalDate.now().minusDays(10);
                break;
            case "last20days":
                startDate = LocalDate.now().minusDays(20);
                break;
            case "last30days":
                startDate = LocalDate.now().minusDays(30);
                break;
            case "all":
                startDate = LocalDate.of(1970, 1, 1); // Set to earliest possible date to include all records
                break;
        }

        final LocalDate finalStartDate = startDate;

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double totalExpense = 0.0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Transaction transaction = snapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        LocalDate transactionDate = LocalDate.parse(transaction.getDate(), formatter);
                        if (transactionDate.isEqual(finalStartDate) || transactionDate.isAfter(finalStartDate)) {
                            totalExpense += transaction.getAmount();
                            table.addCell(transaction.getDate());
                            table.addCell(transaction.getCategory());
                            table.addCell(String.valueOf(transaction.getAmount()));
                            table.addCell(transaction.getDescription());
                        }
                    }
                }

                document.add(table);
                document.add(new Paragraph("Total Expense: " + totalExpense)
                        .setBold()
                        .setFontSize(18));
                document.close();
                openGeneratedPDF(pdfFile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReportActivity.this, "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGeneratedPDF(File pdfFile) {
        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", pdfFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Grant temporary read permission

        Intent chooser = Intent.createChooser(intent, "Open PDF");
        try {
            startActivity(chooser);
        } catch (Exception e) {
            Toast.makeText(this, "No application found to open PDF", Toast.LENGTH_SHORT).show();
        }
    }


}
