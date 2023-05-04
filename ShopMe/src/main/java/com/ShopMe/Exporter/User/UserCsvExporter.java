package com.ShopMe.Exporter.User;

import com.ShopMe.Entity.User;
import com.ShopMe.Exporter.AbstractExporter;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserCsvExporter extends AbstractExporter {
    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {

        super.setResponseHeader(response, "text/csv", ".csv", "users_");

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"User ID", "E-mail", "First Name", "Last Name", "Roles", "Enabled "};

        String[] fieldMapping = {"id", "email", "firstName", "lastname", "roles", "enabled"};// we exculded password and photos

        csvWriter.writeHeader(csvHeader);

        for(User user : listUsers){
            csvWriter.write(user, fieldMapping);
        }

        csvWriter.close();
    }
}