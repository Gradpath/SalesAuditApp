package com.wendys.salesaudit.utility;

import java.text.SimpleDateFormat;

import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
public class Constants {

    public static final String SQL_FILE       = "resources/sql/dataTable";
    public static final String RSA_PROPERTIES = "resources/application";

    public static final String AUDIT_DATA_SOURCE = "jdbc/RSA_DataSource";
    public static final String RDC_DATA_SOURCE   = "jdbc/RSA_RDCDataSource";
    public static final String CORE_DATA_SOURCE  = "jdbc/RSA_CoretablesDataSource";

    public static final String ADMIN_FUNCTION    = "ADMIN_RSA";
    public static final String EDIT_FUNCTION     = "EDIT_RSA";

    public static final String APP_TYPE_CODE     = "RSA";

    public static final String CO_CODE_CAND      = "CAND";

    //Date formats
    public static final org.joda.time.format.DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("MM/dd/yyyy");
    public static final SimpleDateFormat dateDisplayFormat = new SimpleDateFormat("MM/dd/yyyy");
    public static final SimpleDateFormat dateDBFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat dateDayIdFormat = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat dateSelectFormat = new SimpleDateFormat("EEE, MMMMMMMMM dd, yyyy");

}