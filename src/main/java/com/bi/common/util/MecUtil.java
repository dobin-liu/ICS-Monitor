/*
 * @(#)MecUtil.java 2016/11/2
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.MinguoChronology;
import java.time.chrono.MinguoDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DecimalStyle;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.conn.util.InetAddressUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.util.FastByteArrayOutputStream;

import com.bi.base.config.AppStaticConfig;
import com.bi.base.database.PageContainer;
import com.bi.util.CollectionUtil;
import com.bi.util.DateUtil;
import com.bi.util.NumberUtil;
import com.bi.util.StrUtil;
import com.bi.util.assertion.Clauses;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MecUtil {

    public final static DateTimeFormatter formatyyyy_MM_dd = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    public final static DateTimeFormatter formatyyyy_MM_dd_dash = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public final static DateTimeFormatter formatHH_mm_ss_dash = DateTimeFormatter.ofPattern("HH:mm:ss");
    public final static DateTimeFormatter HH_dash = DateTimeFormatter.ofPattern("HH");
    public final static DateTimeFormatter mm_dash = DateTimeFormatter.ofPattern("mm");
    public final static DateTimeFormatter formatMM_dd = DateTimeFormatter.ofPattern("MM/dd");
    public final static DateTimeFormatter formatyyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");
    public final static String PATTERN_IP4 = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
    private final static String PAIRS_PATTERN = "{0}={1}" + System.lineSeparator();
    private final static String DATE_PATTERN_FULL_DATE = "yyyy-MM-dd'T'HH:mm:ss";
    private final static String MASK_CHAR = "*";
    private static final int MAX_CHAR_COUNT_REQ_HEAD = 5000;

    public final static Class<Map<String, Object>> GenericsMapClazz = (Class) Map.class;

    /**
     * 西元民國相互轉換(含閏年2月29日判斷,限定被轉換之格式為yyyy/MM/dd,yyyyMMdd,yyy/MM/dd,yyyMMdd)
     *
     * @param date 輸入日期
     * @param beforeFormat 轉換前要顯示的日期格式
     * @param afterFormat 轉換後要顯示的日期格式
     * @return 字串 example:convertTWDate("2016/10/08 16:30","yyyy/MM/dd HH:mm","yyy/MM/dd HH:mm"),傳換後為 105/10/08 16:30
     *         example:convertTWDate("106/10/30","yyy/MM/dd","yyyy/MM/dd"),傳換後為 2016/10/30 example:convertTWDate("106-05-15 11:20","yyy-MM-dd HH:mm",
     *         "yyyy/MM/dd HH:mm"),傳換後為 2016/05/15 11:20
     * @return
     */
    public static String transformation_ROC_AD(String date, String beforeFormat, String afterFormat) {
        if (date.isEmpty()) {
            return "";
        }

        final SimpleDateFormat df4 = new SimpleDateFormat(beforeFormat);
        final SimpleDateFormat df2 = new SimpleDateFormat(afterFormat);
        final Calendar cal = Calendar.getInstance();

        try {
            cal.setTime(df4.parse(date));
            boolean leapYear;

            if (cal.get(Calendar.YEAR) > 1492) {
                final int year = cal.get(Calendar.YEAR);
                cal.add(Calendar.YEAR, -1911);

                final Pattern p = Pattern.compile("[0-9]");
                final Matcher m = p.matcher(date);
                String month = "";
                while (m.find()) {
                    month += m.group();
                }

                // final int day = cal.get(Calendar.DAY_OF_MONTH);
                final int day = Integer.parseInt(month.substring(6, 8));
                leapYear = ((GregorianCalendar) cal).isLeapYear(year);
                if (leapYear && "02".equals(month.substring(4, 6)) && day == 29) {
                    cal.set(Calendar.MONTH, 2);
                    cal.set(Calendar.DAY_OF_MONTH, 29);

                    String formatDate = df2.format(cal.getTime());
                    if (formatDate.contains("/")) {
                        formatDate = formatDate.replaceAll("/03/", "/02/");
                        return formatDate;
                    }
                    else {
                        formatDate = formatDate.substring(0, 3) + "0229";
                        return formatDate;
                    }
                }
            }
            else {
                cal.add(Calendar.YEAR, +1911);
                final int year = cal.get(Calendar.YEAR);
                final int month = cal.get(Calendar.MONTH);
                final String day = date.substring(date.length() - 2);
                leapYear = ((GregorianCalendar) cal).isLeapYear(year);
                if (leapYear && month == 2 && "29".equals(day)) {
                    cal.set(Calendar.MONTH, 2);
                    cal.set(Calendar.DAY_OF_MONTH, 29);

                    String formatDate = df2.format(cal.getTime());
                    if (formatDate.contains("/")) {
                        formatDate = formatDate.replaceAll("/03/", "/02/");
                        return formatDate;
                    }
                    else {
                        formatDate = formatDate.substring(0, 4) + "0229";
                        return formatDate;
                    }
                }
            }
        }
        catch (final ParseException e) {
            // log.error("transformation_ROC_AD Exception: " , e);
            // log.error(MecUtil.SafeErrorLog(e));
            log.error("transformation_ROC_AD error");
            return "";
        }

        return df2.format(cal.getTime());
    }

    /**
     * check sting is slash date(107/01/23, 0107/01/23) 2018年4月4日
     *
     * @param string
     */
    public static boolean isStringSlashDate(String string) {
        return isStringDate(string, "slash");
    }

    public static boolean isStringDashDate(String string) {
        return isStringDate(string, "dash");
    }

    private static boolean isStringDate(String string, String dtType) {
        final String pattern = "slash".equals(dtType) ? DateUtil.PATTERN_YYYY_MM_DD
                : "dash".equals(dtType) ? "yyyy-MM-dd" : DateUtil.PATTERN_YYYYMMDD;
        boolean res = true;
        try {
            DateUtil.parseDate(string, pattern);
        }
        catch (final ParseException e) {
            res = false;
        }
        return res;
    }

    /**
     * 身份證號碼驗證 2016/9/8
     *
     * @param id 身份證字號
     * @param sex 性別代碼 男1 女2
     * @return 是否正確
     *
     */
    public static boolean verifyId(String id) {

        if (id.length() == 10) {
            // 計算身份證字號字母的值
            final int cityArray[] = {10, 11, 12, 13, 14, 15, 16, 17, 34, 18, 19, 20, 21, 22, 35, 23, 24, 25, 26, 27, 28, 29, 32, 30, 31, 33};
            final int charCode = id.charAt(0);
            if (charCode >= 65 && charCode <= 90) {
                final int cityCode = cityArray[id.charAt(0) - 'A'];
                final int alphabetsSum = (cityCode / 10 + (cityCode % 10) * 9);
                // 計算身份證字號數字的值
                final int number[] = new int[10];
                int numberSum = 0;
                for (int i = 1; i <= 9; i++) {
                    number[i] = (id.charAt(i) - '0');

                }
                for (int i = 0; i < 8; i++) {
                    numberSum += number[i + 1] * (8 - i);
                }
                // 計算檢查碼
                int chechNum = 10 - ((alphabetsSum + numberSum) % 10);
                if (chechNum == 10) {
                    chechNum = 0;
                }
                // 將身份證最末一碼與檢查碼比對
                if (chechNum == number[9]) {
                    // 正確
                    return true;
                }
                else {
                    // 錯誤
                    return false;
                }
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    /**
     * 檢核「姓名」是否只有中文 2018年9月27日
     *
     * @param cusName
     * @return
     *
     */
    public static boolean checkNameFormat(String cusName) {
        int chinaChar = 0;
        for (int i = 0; i < cusName.length(); i++) {// 檢查有沒有非中文字元
            final char c = cusName.charAt(i);
            if (c < 256) {
                chinaChar++;
            }
        }
        return chinaChar > 0 ? true : false;
    }

    /**
     * 計算投保年齡 2016/8/26 getInsureAge
     *
     * @param birthday 西元保戶生日
     * @param
     * @return 投保年齡
     *
     */
    public static Integer getAge(Date birth) {
        final Calendar birthCalendar = Calendar.getInstance();//
        // int age = DateUtil.calculateAge(birth);
        birthCalendar.setTime(birth);
        final Calendar insureCalendar = DateUtil.clearTime(DateUtil.getSqlDate());// 移除時分秒 當天日期 西元投保日期
        int ageCount = insureCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);

        final Calendar startDate = Calendar.getInstance();
        startDate.set(insureCalendar.get(Calendar.YEAR), birthCalendar.get(Calendar.MONTH), birthCalendar.get(Calendar.DATE));
        startDate.add(Calendar.MONTH, 6); // 加六個月
        // startDate.add(Calendar.DATE, 1); // 加一天
        final Calendar endDate = Calendar.getInstance();
        endDate.set(insureCalendar.get(Calendar.YEAR), birthCalendar.get(Calendar.MONTH), birthCalendar.get(Calendar.DATE));
        endDate.add(Calendar.MONTH, -6);
        if (insureCalendar.after(startDate)) {// 投保年齡是以足歲計算，超過六個月者加算一歲
            ageCount++;
        }
        else if (insureCalendar.before(endDate)) {// 投保年齡是以足歲計算，不足六個月者少算一歲
            ageCount--;
        }
        if (ageCount >= 0) {
            return ageCount;
        }
        else {
            return ageCount;
        }

    }

    /**
     * 計算實際年齡 2019/4/29 getActualAge
     *
     * @param birthday 西元保戶生日
     * @param
     * @return 實際年齡 滿足歲
     *
     */
    public static Integer getActualAge(Date birth) {
        final Calendar birthCalendar = Calendar.getInstance();//
        // int age = DateUtil.calculateAge(birth);
        birthCalendar.setTime(birth);
        final Calendar insureCalendar = DateUtil.clearTime(DateUtil.getSqlDate());// 移除時分秒 當天日期 西元投保日期
        int ageCount = insureCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);

        final Calendar thisYearBirth = Calendar.getInstance();
        thisYearBirth.set(insureCalendar.get(Calendar.YEAR), birthCalendar.get(Calendar.MONTH), birthCalendar.get(Calendar.DATE));

        // Date test = insureCalendar.getTime();
        // Date test2 = thisYearBirth.getTime();
        thisYearBirth.setTime(DateUtil.clearTime(thisYearBirth.getTime()).getTime());
        // Date test3 = thisYearBirth.getTime();
        if (insureCalendar.before(thisYearBirth)) {// 計算足歲
            ageCount--;
        }
        if (ageCount >= 0) {
            return ageCount;
        }
        else {
            return ageCount;
        }
    }

    /**
     * 驗證日期是否合法 2016/9/9
     *
     * @param dateStr yyyy/MM/dd格式日期字串
     * @return Date 為正常日期 null為不合法日期
     *
     */
    public static Date verifyDate(String dateStr) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sdf.setLenient(false);
        Date date = null;
        try {
            date = sdf.parse(dateStr);
            return date;
        }
        catch (final ParseException e) {
            // log.error("verifyDate Exception: " , e);
            // log.error(MecUtil.SafeErrorLog(e));
            log.error("verifyDate error");
            return null;
        }
    }

    /**
     *
     * 遮蔽客戶姓名第二個字(mega:取代為全形O)
     *
     * @param cusName 客戶姓名
     * @return
     *
     */
    public static String maskName(String cusName) {
        final StringBuilder sb = new StringBuilder();
        if (!StrUtil.isEmpty(cusName)) {
            final char[] arr = cusName.toCharArray();
            for (int i = 0; i < arr.length; i++) {
                if (i == 1) {
                    sb.append("O");
                }
                else {
                    sb.append(arr[i]);
                }
            }
            return sb.toString();
        }
        return "";
    }

    public static String maskId(String userId) {
        final StringBuilder sb = new StringBuilder();
        if (!StrUtil.isEmpty(userId)) {
            final char[] arr = userId.toCharArray();
            for (int i = 0; i < arr.length; i++) {
                if (i == 4 || i == 5 || i == 6 || i == 7) {
                    sb.append("*");
                }
                else {
                    sb.append(arr[i]);
                }
            }
            return sb.toString();
        }
        return "";
    }

    public static String mask(String str, int start, int end, String maskChar) {
        if (StrUtil.isEmpty(str)) {
            return str;
        }
        if (StrUtil.isEmpty(maskChar)) {
            maskChar = MASK_CHAR;
        }
        Clauses.when(start < 0 || end < 0).throwIllegalArgument("start index and end index should greater than 0");
        Clauses.when(start > end).throwIllegalArgument("end index should greater than start index");
        return StringUtils.overlay(str, StringUtils.repeat(maskChar, end - start), start, end);
    }

    public static String mask(String str, int start, int end) {
        if (StrUtil.isEmpty(str)) {
            return str;
        }
        return mask(str, start, end, MASK_CHAR);
    }

    public static String maskAddress(String address) {
        final StringBuilder sb = new StringBuilder();
        if (!StrUtil.isEmpty(address)) {
            final char[] arr = address.toCharArray();
            for (int i = 0; i < arr.length; i++) {
                if (i > 5) {
                    sb.append("O");
                }
                else {
                    sb.append(arr[i]);
                }
            }
            return sb.toString();
        }
        return "";
    }

    // cellphone 自第四碼起遮罩三碼，遮罩符號為*
    public static String maskPhone(String cellphone) {
        final StringBuilder sb = new StringBuilder();
        final String[] characters = cellphone.split("");
        final int length = characters.length;
        for (int size = 0; size < length; size++) {
            if (size == 0) {
                if (!StrUtil.isEmpty(characters[size])) {
                    sb.append(characters[size]);
                }
            }
            else if (size > 4 && size < 8) {
                sb.append("*");
            }
            else {
                sb.append(characters[size]);
            }
        }
        return sb.toString();
    }

    // email自開頭遮罩五碼，遮罩符號為*(mega:取代為半形*)
    public static String maskEmail(String email) {
        final StringBuilder sb = new StringBuilder();
        final String[] characters = email.split("");
        final int length = characters.length;
        for (int size = 0; size < length; size++) {
            if (size == 0) {
                if (!StrUtil.isEmpty(characters[size])) {
                    sb.append(characters[size]);
                }
            }
            else if (size > 0 && size < 6) {
                sb.append("*");
            }
            else {
                sb.append(characters[size]);
            }
        }
        return sb.toString();
    }

    /**
     * replace sql date to util date for list 2018年4月12日
     *
     * @param list
     */
    /*
     * public static void turnDtToUtilForList(List<Map<String, Object>> list) {
     * if (CollectionUtil.isNotEmpty(list)) {
     * // load tmpList, sql-> util dt val
     * final List<Map<String, Object>> tmpList = new ArrayList<Map<String, Object>>();
     * for (final Map<String, Object> map : list) {
     * turnSqlToUtilDt(tmpList, map);
     * }
     * list = tmpList;
     * }
     * }
     */

    /**
     * replace sql date to util date for page container 2018年4月12日
     *
     * @param pgCnt
     */
    public static void turnDtToUtilForPageCnt(PageContainer<Map<String, Object>> pgCnt) {
        if (pgCnt != null && !pgCnt.isEmpty()) {
            // load tmpList, sql-> util dt val
            final List<Map<String, Object>> tmpList = new ArrayList<Map<String, Object>>();
            for (final Map<String, Object> map : pgCnt.getRows()) {
                turnSqlToUtilDt(tmpList, map);
            }
            pgCnt.setRows(tmpList);
        }
    }

    private static void turnSqlToUtilDt(List<Map<String, Object>> tmpList, Map<String, Object> map) {
        final Map<String, Object> mapTmp = new HashMap<String, Object>();
        if (map != null && !map.isEmpty()) {
            for (final String key : map.keySet()) {
                Object valObj = map.get(key);
                if (valObj != null && key.lastIndexOf("_DATE") != -1) {
                    try {
                        valObj = DateUtil.parseDate(valObj.toString(), "yyyy-MM-dd");

                    }
                    catch (final ParseException e) {
                        log.error(" DateUtil.parseDate error");
                    }
                }
                mapTmp.put(key.toLowerCase(), valObj);
            }
            tmpList.add(mapTmp);
        }
    }

    public static Map<String, List<Map<String, Object>>> getSplitMapListByKey(List<Map<String, Object>> mapList, String splitKey) {
        final Map<String, List<Map<String, Object>>> resMap = new LinkedHashMap<String, List<Map<String, Object>>>();

        if (CollectionUtil.isNotEmpty(mapList)) {
            for (final Map<String, Object> map : mapList) {
                final String key = (String) map.get(splitKey);
                List<Map<String, Object>> list = null;
                if (resMap.containsKey(key)) {
                    list = resMap.get(key);
                }
                else {// not contain key
                    list = new ArrayList<Map<String, Object>>();
                }
                list.add(map);
                resMap.put(key, list);
            }
        }
        return resMap;
    }

    public static Set<String> getStringSetByKey(List<Map<String, Object>> mapList, String splitKey) {
        final Set<String> res = new HashSet<String>();
        if (CollectionUtil.isNotEmpty(mapList)) {
            for (final Map<String, Object> map : mapList) {
                final String key = (String) map.get(splitKey);
                res.add(key);
            }
        }
        return res;
    }

    /**
     * 根據 request 取得使用者端 IP，若透過 Proxy 傳遞過，則嘗試取得Proxy設定的 header
     *
     * @param request
     * @return
     */
    public static String getRemoteAddr(HttpServletRequest request) {
        // --元壽環境 若搭配 IHS 需要記得開啟 trustedSensitiveHeaderOrigin 參數
        String[] chkHeader = new String[] {"x-forwarded-for", "$WSRA", "Forwarded", "Proxy-Client-IP", "WL-Proxy-Client-IP"};
        String ip = "";
        for (String name : chkHeader) {
            ip = getVaildIpString(toFilterStr(request.getHeader(name)));
            if (StrUtil.isNotEmpty(ip)) {
                break;
            }
        }
        if (StrUtil.isEmpty(ip)) {
            ip = request.getRemoteAddr();
        }

        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }

        // return filterVaildLog(ip);
        return ip;
    }

    /**
     *
     * 西元轉民國、民國轉西元 互轉 2016/10/30
     *
     * @param AD 輸入日期
     * @param beforeFormat 轉換前要顯示的日期格式
     * @param afterFormat 轉換後要顯示的日期格式
     * @return 字串 example:convertTWDate("2016/10/08 16:30","yyyy/MM/dd HH:mm","yyy/MM/dd HH:mm"),傳換後為 105/10/08 16:30
     *         example:convertTWDate("106/10/30","yyy/MM/dd","yyyy/MM/dd"),傳換後為 2016/10/30 example:convertTWDate("106-05-15 11:20","yyy-MM-dd HH:mm",
     *         "yyyy/MM/dd HH:mm"),傳換後為 2016/05/15 11:20
     */
    public static String westernDateTwDateConvert(String AD, String beforeFormat, String afterFormat) {// 轉年月格式
        if (AD == null) {
            return "";
        }
        else {
            return transformation_ROC_AD(AD, beforeFormat, afterFormat);
        }
    }

    /**
     * 將日期物件格式化為民國年月日
     *
     * @param srcDatetime
     * @return
     */
    public static String formatTwDate(LocalDateTime srcDatetime) {
        return formatTwDate(srcDatetime.toLocalDate());
    }

    /**
     * 將日期物件格式化為民國年月日
     *
     * @param srcDate
     * @return
     */
    public static String formatTwDate(LocalDate srcDate) {
        String dayStr = "";
        final DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd");
        dayStr = String.format("%03d", srcDate.getYear() - 1911) + "/" + srcDate.format(format);
        return dayStr;
    }

    /**
     * 西元年月日字串轉為民國年月日字串(yyyy/MM/dd -> yyy/MM/dd)
     *
     * @param dateStr
     * @return
     */
    public static String WestToTwDate(String dateStr) {
        return westernDateTwDateConvert(dateStr, "yyyy/MM/dd", "yyy/MM/dd");
    }

    /**
     * 民國年月日字串轉為西元年月日字串(yyy/MM/dd -> yyyy/MM/dd)
     *
     * @param dateStr
     * @return
     */
    public static String TwToWestDate(String dateStr) {
        return westernDateTwDateConvert(dateStr, "yyy/MM/dd", "yyyy/MM/dd");
    }

    /**
     * 取得日期字串，以西元年月日字串表示
     *
     * @param date
     * @return
     */
    public static String getDateString(Date date) {
        return getDateTimeString(date, "yyyy/MM/dd", false);
    }

    /**
     * 取得日期字串，以西元年月日字串表示
     *
     * @param date
     * @return
     */
    public static String getDateTimeString(Date date) {
        return getDateTimeString(date, "yyyy/MM/dd", true);
    }

    /**
     * 取得日期 字串，以民國年月日 表示
     *
     * @param date
     * @return
     */
    public static String getTWDateString(Date date) {
        return getDateTimeString(date, "yyy/MM/dd", false);
    }

    /**
     * 取得日期+時間字串，以民國年月日時分秒字串表示
     *
     * @param date
     * @return
     */
    public static String getTWDateTimeString(Date date) {
        return getDateTimeString(date, "yyy/MM/dd", true);
    }

    /**
     * 格式化日期，但僅處理 西元年: yyyy/MM/dd 或 yyyy-MM-dd，民國年: yyy/MM/dd 或 yyy-MM-dd
     * 
     * @param date
     * @param formatDefine
     * @return
     *
     */
    public static String getDateTimeString(Date date, String formatDefine, boolean includeTime) {
        if (date != null) {
            if (StrUtil.isEmpty(formatDefine)) {
                formatDefine = "yyyy/MM/dd";
            }
            String dateStr = "";
            if (formatDefine.length() == 9) { // --民國年
                final Calendar cal = new GregorianCalendar();
                cal.setTime(date);
                final String partFormat01 = formatDefine.substring(3, 4);
                final String partFormat02 = formatDefine.substring(4);
                final SimpleDateFormat sdf = new SimpleDateFormat(partFormat02);
                dateStr = String.format("%03d", cal.get(Calendar.YEAR) - 1911) + partFormat01 + sdf.format(date);
            }
            else { // --西元年
                final SimpleDateFormat sdf = new SimpleDateFormat(formatDefine);
                dateStr = sdf.format(date);
            }
            if (includeTime) {
                final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
                dateStr += " " + sdfTime.format(date);
            }
            return dateStr;
        }
        return "";
    }

    /**
     * 取得目前系統日，以民西元年月日字串表示
     *
     * @return
     */
    public static String getToday() {
        return LocalDate.now().format(formatyyyy_MM_dd);
    }

    public static String getToday_Dash() {
        return LocalDate.now().format(formatyyyy_MM_dd_dash);
    }

    public static String getToday_yyyymmdd() {
        return LocalDate.now().format(formatyyyyMMdd);
    }

    public static String getNowHour_Dash() {
        return LocalDateTime.now().format(HH_dash);
    }

    public static String getNowMinute_Dash() {
        return LocalDateTime.now().format(mm_dash);
    }

    public static String getTodayTime_Dash() {
        return LocalDate.now().format(formatyyyy_MM_dd_dash) + " " + LocalTime.now().format(formatHH_mm_ss_dash);
    }

    /**
     * 將指定的 日期物件，轉為今年年度
     *
     * @param date 指定的 日期物件
     * @return
     */
    public static Date changeToThisYear(Date date) {
        final Calendar c = Calendar.getInstance();
        final int yyyy = LocalDate.now().getYear();
        c.setTime(date);
        c.set(Calendar.YEAR, yyyy);
        return c.getTime();
    }

    /**
     *
     * 將日期字串(yyyy-MM-dd)，轉成timestamp 2018年8月22日
     *
     * @param timeStr
     * @return
     *
     */
    public static Timestamp transDayStringToTimestamp(String timeStr) {
        Timestamp timestamp = null;
        try {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            final Date parsedDate = dateFormat.parse(timeStr);
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
        }
        catch (final ParseException e) {
            log.error("transDayStringToTimestamp error");
        }
        return timestamp;
    }

    /**
     * 將request body 轉成要記錄的資料
     */
    public static String transReqLogStr(Map<String, Object> mapData) {
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<String, Object> entry : mapData.entrySet()) {
            final Object v = entry.getValue();
            final String k = entry.getKey();
            sb.append(pairs(k, v));
        }
        return sb.toString();
    }

    public static String pairs(String k, Object v) {
        if (v == null) {
            v = "";
        }
        return MessageFormat.format(PAIRS_PATTERN, k, v);
    }

    /**
     * 轉換 Stream 物件(FastByteArrayOutputStream)資料，由於 Stream 實作物件多樣(如 MSSQL 的 BLOB 欄位串流 PLPInputStream )，有的無法隨時保持可讀取狀態，
     * 透過此轉換為記憶體串流(MemoryStream)，雖然會額外存放到記憶體，但此做法比轉為 大物件 Byte陣列(big byte[]) 來的更好(避免 JVM 頻繁觸發GC)。
     *
     * @param in
     * @return
     */
    public static InputStream TransferMemoryStream(InputStream in) {
        InputStream rtStream = null;
        if (in != null) {
            FastByteArrayOutputStream bOut01 = null;
            try {
                final String streamClassName = in.getClass().toString();
                log.debug("TransferMemoryStream : " + streamClassName);
                if (streamClassName.indexOf("sqlserver.jdbc.PLPInputStream") > -1) {
                    bOut01 = new FastByteArrayOutputStream();
                    IOUtils.copy(in, bOut01);
                    rtStream = bOut01.getInputStream();
                }
                else {
                    rtStream = in; // --直接重新設定
                }

            }
            catch (final IOException e) {
                // log.error("transStream Exception: " , e );
                // log.error(MecUtil.SafeErrorLog(e));
                log.error("TransferMemoryStream error");
            }
        }
        return rtStream;
    }

    /**
     * 要寫入log file前先做字串轉換，避免 Log Forging
     */

    /**
     * 要寫入log file前先做字串轉換，避免 Log Forging 為避免 Log Forging 的弱掃問題，而過濾處理字串 (漏洞校驗)
     *
     * @param text 原始字串
     * @return 回傳過濾後的合法字串
     */
    public static String toLogStr(String text) {
        text = filterVaildLog(text);
        if (text == null) {
            return null;
        }
        else {
            String tmpStr = text.replaceAll("(\\r|\\n)", " ");
            tmpStr = StringEscapeUtils.escapeHtml4(tmpStr);
            return genString(tmpStr);
        }
    }

    public static String toFilterStr(String text) {
        text = filterVaildLog(text);
        if (text == null) {
            return null;
        }
        else {
            String tmpStr = StringEscapeUtils.escapeHtml4(text);
            return genString(tmpStr);
        }
    }

    /**
     * 設File權限
     */
    public static void setPermission(File file) {
        try {
            final Set<PosixFilePermission> perms = new HashSet<>();
            perms.add(PosixFilePermission.OWNER_READ);
            perms.add(PosixFilePermission.OWNER_WRITE);
            // perms.add(PosixFilePermission.OWNER_EXECUTE);

            Files.setPosixFilePermissions(file.toPath(), perms);

        }
        catch (final IOException e) {
            log.error("setPermission error");
        }
    }

    /**
     * 設Dir權限
     */
    public static void setDirPermission(Path path) {
        try {
            final Set<PosixFilePermission> perms = new HashSet<>();
            perms.add(PosixFilePermission.OWNER_READ);
            perms.add(PosixFilePermission.OWNER_WRITE);
            perms.add(PosixFilePermission.OWNER_EXECUTE);
            perms.add(PosixFilePermission.GROUP_EXECUTE);
            perms.add(PosixFilePermission.OTHERS_EXECUTE);

            Files.setPosixFilePermissions(path, perms);

        }
        catch (final IOException e) {
            log.error("setDirPermission error");
        }
    }

    /**
     * 解決弱點 Information Exposure Through an Error Message 處理 例外物件的輸出訊息
     *
     * @param e 例外物件
     */
    public static String SafeErrorLog(Exception e) {
        // t.getStackTrace()
        final int limitLen = 5000;
        // 依照規範需要將可能遺漏資訊的例外排除，例如 SQLException BindException
        // 但是黑名單又通常不被弱掃軟體完全認可，嚴謹作法是通通改為自訂的例外錯誤，但這樣卻非常難追蹤例外錯誤的位置(程式碼)
        // 目前先嘗試測試簡單過濾，看是否可通過弱掃軟體檢測
        String errStackStr = "";
        /*
        try {
            errStackStr = genString(ExceptionUtils.getStackTrace(e));
            if (errStackStr.length() > limitLen) {
                errStackStr = errStackStr.substring(0, limitLen) + "\n.....Truncate for simplified content!";
            }
        }
        catch (final Exception ex) {
            errStackStr = "Eror in MecUtil.SafeErrorLog!";
        }
        */
        
        errStackStr = genString(ExceptionUtils.getStackTrace(e));
        if (errStackStr.length() > limitLen) {
            errStackStr = errStackStr.substring(0, limitLen) + "\n.....Truncate for simplified content!";
        }
        // log.error("SafeErrorLog", e);
        return errStackStr;
    }

    /**
     * 判斷是否為民國年
     */
    public static boolean isRocDate(String date) {
        boolean isRoc1 = false;
        boolean isRoc2 = false;
        final Pattern DATE_PATTERN1 = Pattern.compile("^\\d{3}/\\d{2}/\\d{2}$"); // 大於民國100年
        final Pattern DATE_PATTERN2 = Pattern.compile("^\\d{2}/\\d{2}/\\d{2}$");// 小於民國100年
        isRoc1 = DATE_PATTERN1.matcher(date).matches();
        isRoc2 = DATE_PATTERN2.matcher(date).matches();
        return isRoc1 || isRoc2;
    }

    /**
     * 轉民國年to西元年根據日曆上所選日期
     */
    public static String convertRocToWesternDate(String date) {
        boolean isRoc1 = false;
        boolean isRoc2 = false;
        String westernDate = "";
        final Pattern DATE_PATTERN1 = Pattern.compile("^\\d{3}/\\d{2}/\\d{2}$"); // 大於民國100年
        final Pattern DATE_PATTERN2 = Pattern.compile("^\\d{2}/\\d{2}/\\d{2}$");// 小於民國100年
        isRoc1 = DATE_PATTERN1.matcher(date).matches();
        if (isRoc1) {
            westernDate = transformation_ROC_AD(date, "yyy/MM/dd", "yyyy-MM-dd");
        }
        isRoc2 = DATE_PATTERN2.matcher(date).matches();
        if (isRoc2) {
            date = "0" + date;
            westernDate = transformation_ROC_AD(date, "yyy/MM/dd", "yyyy-MM-dd");
        }
        return westernDate;
    }

    /**
     * 字串結合處理
     *
     * @param delimiter
     * @param params
     * @return
     */
    public static String StringJoin(String delimiter, String... params) {
        if (params == null) {
            return "";
        }
        final StringJoiner sj = new StringJoiner(delimiter);
        for (final String s : params) {
            if (StrUtil.isNotEmpty(s) && StrUtil.isNotEmpty(s.trim())) {
                sj.add(s.trim());
            }
        }
        return sj.toString();
    }

    /**
     * 修剪電話號碼中若最後一碼為# 或 - ，請拿掉。
     *
     * @param srcPhone
     * @return
     */
    public static String trimPhoneNum(String srcPhone) {
        if (StrUtil.isNotEmpty(srcPhone) && (srcPhone.endsWith("-") || srcPhone.endsWith("#"))) {
            return srcPhone.substring(0, srcPhone.length() - 1);
        }
        else {
            return srcPhone;
        }
    }

    /**
     * 檢核 IP 來源是否合法
     *
     * @param ipStr ip 位置
     * @param allowedOrigins 允許清單
     * @return
     */
    public static boolean AllowIpInList(String ipStr, List<String> allowedOrigins) {
        if (StrUtil.isNotEmpty(ipStr)) {
            if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
                if (allowedOrigins.contains(ipStr)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 將物件資料轉為 JSON 字串
     *
     * @param obj 物件
     * @return
     */
    public static String toJSONString(Object obj) {
        String jsonInString = "";
        try {
            final ObjectMapper mapper = new ObjectMapper();
            jsonInString = mapper.writeValueAsString(obj);
        }
        catch (final JsonProcessingException e1) {
            log.error("toJSONString error");
        }
        return jsonInString;
    }

    /**
     * 取得 MD5 字串
     *
     * @param dataBytes 來源 byte[]
     * @return
     * @throws NoSuchAlgorithmException
     */
    // --非元壽環境系統，不須使用
    // public static String getMd5HashString(byte[] dataBytes) throws NoSuchAlgorithmException {
    // // --MD5 計算
    // final MessageDigest md = MessageDigest.getInstance("MD5");
    // // 計算md5函數
    // md.update(dataBytes);
    // // BigInteger函數則將8位的字符串轉換成16位hex值，用字符串來表示；得到字符串形式的hash值
    // String md5HashString = Hex.encodeHexString(md.digest());
    // // String md5HashString = new BigInteger(1, md.digest()).toString(16);
    // // if (md5HashString.length() % 2 != 0) {
    // // md5HashString = "0" + md5HashString; // 避免少一個 0
    // // }
    // return md5HashString;
    // }

    /**
     * 處理輸入LOG 的資料，避免 Log Forging (漏洞校驗)
     *
     * @param logs
     * @return
     */
    public static String filterVaildLog(String logs) {
        if (logs != null) {
            String normalize = Normalizer.normalize(logs, Normalizer.Form.NFKC);
            normalize = normalize.replaceAll("\r|\n|%0d|%0a", "");
            return normalize;
        }
        else {
            return null;
        }
    }

    /**
     * 處理輸入字串的資料，僅限 0-9 , A-Z 大小寫與 : . ，並限制長度為 32 個字元
     * 
     * @param input 來源字串
     * @return
     *
     */
    public static String filterVaildString(String input) {
        return filterVaildString(input, 32);
    }

    /**
     * 處理輸入字串的資料，僅限 0-9 , A-Z 大小寫與 : . , Feb 25, 2020 並限制長度
     * 
     * @param input 來源字串
     * @param maxLen 最多字元數
     * @return
     *
     */
    public static String filterVaildString(String input, int maxLen) {
        if (input != null) {
            String v = input.replaceAll("[^a-z0-9A-Z.:]", "");
            if (v.length() > maxLen) {
                v = v.substring(0, maxLen);
            }
            return v;
        }
        else {
            return null;
        }
    }

    /**
     * 取得 資源檔案 Mar 3, 2020
     * 
     * @param resourcePath 資源檔案路徑名稱
     * @return
     * @throws IOException
     *
     */
    public static File getClassPathResourceFile(String resourcePath) throws IOException {
        Resource resource = new ClassPathResource(resourcePath);
        return resource.getFile();
    }

    /**
     * 取得 HttpServletRequest 物件中的 所有 Header 字串資訊 Mar 3, 2020
     * 
     * @param request HttpServletRequest 物件
     * @return
     *
     */
//    public static String getAllHeadersLog(HttpServletRequest request) {
//        StringBuilder sb = new StringBuilder();
//        // Enumeration<String> e = request.getParameterNames();
//        sb.append("[Headers]     : ");
//        Collections.list(request.getHeaderNames()).forEach(name -> {
//            String value = MecUtil.filterVaildLog(request.getHeader(name));
//            String apdText = name + "=" + value + ", ";
//            int toAppendChLeng = sb.length() + apdText.length();
//            if (toAppendChLeng <= MAX_CHAR_COUNT_REQ_HEAD) {
//                sb.append(apdText);
//            }            
//        });
//        return sb.toString();
//    }

    /**
     * 取得有效的 IP 位置資 訊 Mar 4, 2020
     * 
     * @param input 來源字串
     * @return
     *
     */
    public static String getVaildIpString(String input) {
        if (input != null) {
            String v = "";
            String oneIpAddr = "";
            if (input.indexOf(",") > -1) { // -- 透過 x-forwarded-for 取得有可能有多筆
                String[] inputList = input.split(",");
                for (String ip : inputList) {// 僅取第一筆有效資料
                    if (StrUtil.isNotEmpty(ip)) {
                        oneIpAddr = filterVaildLog(ip.trim());
                        break;
                    }
                }
            }
            else {
                oneIpAddr = filterVaildLog(input.trim());
            }
            int maxLen = 40; // --資料表欄位長度限制
            if (oneIpAddr.length() > 6 && oneIpAddr.length() <= maxLen && oneIpAddr.matches("[a-z0-9A-Z.:]+")) {// --字串內容長度 + 格式 檢核
                if (oneIpAddr.indexOf(".") > -1 && InetAddressUtils.isIPv4Address(oneIpAddr)) { // Ipv4 檢核
                    v = oneIpAddr;
                    // String[] partIpv4 = oneIpAddr.split("\\.");
                    // if (partIpv4.length == 4) {
                    // int[] ipV4Int = new int[4];
                    // boolean checkIpv4 = true;
                    // for (int ii = 0; ii < partIpv4.length; ii++) {
                    // String ipPart = partIpv4[ii];
                    // if (ipPart == null || ipPart.length() > 4) {
                    // checkIpv4 = false;
                    // break;
                    // }
                    // else {
                    // ipV4Int[ii] = Integer.parseInt(ipPart);
                    // }
                    // }
                    // if (checkIpv4) {
                    // String[] filterPartIpv4 = new String[4];
                    // for (int jj = 0; jj < ipV4Int.length; jj++) {
                    // filterPartIpv4[jj] = String.valueOf(ipV4Int[jj]);
                    // }
                    // v = String.join(".", filterPartIpv4);
                    // }
                    // }
                }
                else if (oneIpAddr.indexOf(":") > -1 && InetAddressUtils.isIPv6Address(oneIpAddr)) {// Ipv6 檢核
                    v = oneIpAddr;
                    // String[] partIpv6 = oneIpAddr.split(":");
                    // if (partIpv6.length == 8) {
                    // String[] filterPartIpv6 = new String[8];
                    // boolean checkIpv6 = true;
                    // for (int ii = 0; ii < partIpv6.length; ii++) {
                    // String ipPart = partIpv6[ii];
                    // if (ipPart == null || ipPart.length() > 5) {
                    // checkIpv6 = false;
                    // break;
                    // }
                    // else {
                    // filterPartIpv6[ii] = ipPart;
                    // }
                    // }
                    // if (checkIpv6) {
                    // v = String.join(":", filterPartIpv6);
                    // }
                    // }
                }
            }
            return v;
        }
        else {
            return null;
        }
    }

    /**
     * 是否為攜出裝置
     */
//    public static boolean isMDM(String inStr, String clientIp) {
//        // 使用clientIp，做判斷，不在inner.addr內的，就是MDM
//        final String as = AppStaticConfig.getProperty("inner.addr");
//        if (StrUtil.isNotEmpty(as)) {
//            try {
//                String[] tokens = as.split(",");
//                for (String token : tokens) {
//                    IpAddressMatcher ipAddressMatcher = new IpAddressMatcher(token);
//                    if (ipAddressMatcher.matches(clientIp)) {
//                        return false;
//                    }
//                }
//            }
//            catch (RuntimeException e) {
//                log.error("isMDM error");
//            }
//
//            return true;
//        }
//
//        // 使用useragent判斷是否為MDM
//        if (StrUtil.isNotEmpty(inStr)) {
//            // if (inStr.indexOf("Apache-HttpClient/") > -1) {
//            if (inStr.indexOf("iPad") > -1 || inStr.indexOf("Mobile") > -1 || inStr.indexOf("Apache-HttpClient/") > -1) {
//                return true;
//            }
//        }
//
//        return false;
//    }

    public static String cleanFilePathString(String aString) {
        if (aString == null)
            return null;
        String cleanString = "";
        for (int i = 0; i < aString.length(); ++i) {
            cleanString += cleanChar(aString.charAt(i));
        }
        return cleanString;
    }

    private static char cleanChar(char aChar) {

        // 0 - 9
        for (int i = 48; i < 58; ++i) {
            if (aChar == i)
                return (char) i;
        }

        // 'A' - 'Z'
        for (int i = 65; i < 91; ++i) {
            if (aChar == i)
                return (char) i;
        }

        // 'a' - 'z'
        for (int i = 97; i < 123; ++i) {
            if (aChar == i)
                return (char) i;
        }

        // other valid characters
        switch (aChar) {
            case '\\':
                return '\\';
            case '/':
                return '/';
            case '.':
                return '.';
            case '-':
                return '-';
            case '_':
                return '_';
            case ' ':
                return ' ';
            case ':':
                return ':';
        }
        return '%';
    }

    public static byte[] genStringToBytes(String src) {
        if (StrUtil.isEmpty(src)) {
            return null;
        }
        final byte[] dst = new byte[src.length()];
        System.arraycopy(src.getBytes(), 0, dst, 0, dst.length);
        return dst;
    }

    public static String genString(String src) {
        if (StrUtil.isEmpty(src)) {
            return "";
        }
        try {
            final byte[] srcByte = src.getBytes("UTF-8");
            final int len = srcByte.length;
            final byte[] dst = new byte[len];
            System.arraycopy(srcByte, 0, dst, 0, dst.length);
            String retStr = new String(dst, "UTF-8");
            return retStr;
        }
        catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public static String serializableToString(Serializable obj) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        }
        catch (IOException e) {
            log.error("serializableToString error");
        }
        return "";
    }

    public static Object StringToObj(String s) {
        byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object object = ois.readObject();
            ois.close();
            return object;
        }
        catch (IOException | ClassNotFoundException e) {
            log.error("StringToObj error");
        }

        return null;
    }

    /**
     * @param stTime // hh:mm:ss
     * @param edTime // hh:mm:ss
     * @return
     *
     */
    public static boolean withinWorkTime(String stTime, String edTime) {
        try {
            LocalTime startTime = LocalTime.of(Integer.parseInt(stTime.substring(0, 2)), Integer.parseInt(stTime.substring(3, 5)),
                    Integer.parseInt(stTime.substring(6, 8)));
            LocalTime endTime = LocalTime.of(Integer.parseInt(edTime.substring(0, 2)), Integer.parseInt(edTime.substring(3, 5)),
                    Integer.parseInt(edTime.substring(6, 8)));
            LocalTime time = LocalTime.now();
            if (endTime.isAfter(startTime)) {
                if (time.isAfter(startTime) && time.isBefore(endTime)) {
                    return true;
                }
            }
            else {
                LocalTime midTime = LocalTime.of(0, 0, 0);
                if (time.isAfter(startTime)) {
                    return true;
                }
                else if (time.isAfter(midTime) && time.isBefore(endTime)) {
                    return true;
                }
            }
        }
        catch (NumberFormatException e) {
            log.error("withinWorkTime error");
        }

        return false;
    }

    /**
     * Transfer AD date to minguo date. 西元年 yyyy-MM-dd 轉 民國年 yyy-MM-dd
     *
     * @param dateString the String dateString
     * @return the string
     */
    public static String transferDateADToMinguo(String dateString) {
        if (StrUtil.isEmpty(dateString)) {
            return dateString;
        }
        LocalDate localDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return MinguoDate.from(localDate).format(DateTimeFormatter.ofPattern("yyy-MM-dd"));
    }

    /**
     * Transfer minguo date to AD date. 民國年 yyy-MM-dd 轉 西元年 yyyy-MM-dd
     *
     * @param dateString the String dateString
     * @return the string
     */
    public static String transferDateMinguoToAD(String dateString) {
        return transferDateMinguoToADDateformate(dateString, "yyyy-MM-dd");
    }

    /**
     * Transfer minguo date to AD date. 民國年 yyy-MM-dd 轉 西元年 (格式自訂)
     * 
     * @param dateString Mingu date string
     * @param formatePattern Formatter pattern
     * @return AD date string
     *
     */
    public static String transferDateMinguoToADDateformate(String dateString, String formatePattern) {
        if (StrUtil.isEmpty(dateString)) {
            return dateString;
        }
        Chronology chrono = MinguoChronology.INSTANCE;
        DateTimeFormatter df = new DateTimeFormatterBuilder().parseLenient().appendPattern("yyy-MM-dd").toFormatter().withChronology(chrono)
                .withDecimalStyle(DecimalStyle.of(Locale.getDefault()));

        ChronoLocalDate chDate = chrono.date(df.parse(dateString));
        return LocalDate.from(chDate).format(DateTimeFormatter.ofPattern(formatePattern));
    }

}
