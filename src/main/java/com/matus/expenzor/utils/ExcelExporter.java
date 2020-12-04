package com.matus.expenzor.utils;

import com.matus.expenzor.dto.expense.ExpenseDto;
import com.matus.expenzor.model.Expense;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Expense> expenses;

    public ExcelExporter(List<Expense> expenses) {
        this.expenses = expenses;
        workbook = new XSSFWorkbook();
    }

    private void createHeader(){
        sheet = workbook.createSheet("Expenses");
        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "Date", style);
        createCell(row, 1, "Amount", style);
        createCell(row, 2, "Category", style);
        createCell(row, 3, "Description", style);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle cellStyle){
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(cellStyle);
    }

    private void writeDataLines(){
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY");
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (Expense expense : expenses) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, format.format(expense.getDate()), style);
            createCell(row, columnCount++, expense.getValue(), style);
            createCell(row, columnCount++, expense.getCategory().toString(), style);
            createCell(row, columnCount++, expense.getDescription(), style);

        }
    }

    public void export(HttpServletResponse response) throws IOException {
        createHeader();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
}
