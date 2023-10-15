package com.ShopMe.Exporter.Category;

import com.ShopMe.Entity.Category;
import com.ShopMe.Exporter.AbstractExporter;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class CategoryCsvExporter extends AbstractExporter {
    public void export(List<Category> listCategory, HttpServletResponse response) throws IOException {

        super.setResponseHeader(response, "text/csv", ".csv", "categories_");

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"Category ID", "Category Name"};

        String[] fieldMapping = {"id", "name"};

        csvWriter.writeHeader(csvHeader);

        for(Category category : listCategory){
            category.setName(category.getName().replace("--", " "));
            csvWriter.write(category, fieldMapping);
        }

        csvWriter.close();
    }
}
