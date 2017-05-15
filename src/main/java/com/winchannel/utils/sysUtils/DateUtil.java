package com.winchannel.utils.sysUtils;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {

    private static String DATE_TIME_FORMATE = "yyyy-MM-dd HH:mm:ss";

    public static String getStandDateTime(){
        return  getStandDateTime(new Date());
    }
    
    public static String getStandDateTime(Date date){
        SimpleDateFormat formate = new SimpleDateFormat(DATE_TIME_FORMATE);
        return  formate.format(date);
    }

	public static String getNextDate(String preDate) {
		String nextDate = "";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = formatter.parse(preDate);
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			calendar.add(Calendar.DATE, 1);
			date = calendar.getTime();
			nextDate = formatter.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return nextDate;
	}


	@Test
	public void sadfsa(){
		String baseQuerySql = "SELECT ID,IMG_ID,IMG_URL,ABSOLUTE_PATH FRoM  from VISIT_PHOTO WHERE IMG_ID in ( SELECT b.IMG_ID AS IMG_ID from (  SELECT ROW_NUMBER() over(order by ID) AS rn,IMG_ID AS IMG_ID FROM MS_VISIT_ACVT WHERE FUNC_CODE='FAC_500' ) b )";

		Pattern p = Pattern.compile("[Ff][Rr][Oo][Mm]");
		Matcher m = p.matcher(baseQuerySql);
		if (m.find()){
			String g = m.group();
			int start = m.start();
			System.out.println(start+"-"+g);
			String s = baseQuerySql.substring(start);
			System.out.println(s);
		}



	}


}
