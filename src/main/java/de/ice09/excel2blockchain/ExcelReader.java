package de.ice09.excel2blockchain;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelReader {

    public List<Map<String, String>> readExcel() throws IOException {
        List<Map<String, String>> maps = readExcelData(new File("src/main/resources/wintermute_employees.xlsx"));
        return maps.subList(1, maps.size());
    }

    private List<Map<String, String>> readExcelData(File fFile) throws IOException {
        FileInputStream file = new FileInputStream(fFile);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        List<Map<String, String>> data = new ArrayList<>();
        for (Row row : sheet) {
            Map<String, String> rowMap = new HashMap<>();
            for (Cell cell : row) {
                String value = null;
                switch (cell.getCellTypeEnum()) {
                    case STRING: value = cell.getStringCellValue(); break;
                    case NUMERIC: value = String.valueOf(cell.getNumericCellValue()); break;
                    case BOOLEAN: value = String.valueOf(cell.getBooleanCellValue()); break;
                    case FORMULA: value = String.valueOf(cell.getCellFormula()); break;
                }
                rowMap.put(sheet.getRow(0).getCell(cell.getColumnIndex()).getStringCellValue(), value);
            }
            data.add(rowMap);
        }
        return data;
    }

}
