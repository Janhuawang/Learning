package com.learn.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Pattern;

/**
 * 基本类型转化
 * <br/> 代理一部容错效果
 *
 * @author WJH
 */
public class NumberUtil {

    /**
     * 积分转换，若有小数保留一位，则无小数 1.034 = 1.0 ,12 = 12
     *
     * @param obj
     * @return
     */
    public static String scoreFormatSingle(Object obj) {
        return new DecimalFormat("#.#").format(parseFloat(obj));
    }

    /**
     * 获取音视频的时长，统一向上取整
     *
     * @param obj
     * @return
     */
    public static int formatMediaDuration(Object obj) {
        return (int) Math.ceil(NumberUtil.parseDouble(obj));
    }

    /**
     * 表明四舍五入，保留digits小数（若是整数型不保留）例如： 90.99 = 91
     *
     * @param obj
     * @param digits 最大保留位数
     * @return
     */
    public static String floatToFractionDigits(Object obj, int digits) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(digits);
        nf.setGroupingUsed(false);
        return nf.format(parseDouble(obj));
    }

    /**
     * 表明四舍五入，保留digits小数
     *
     * @param obj
     * @param max 最大保留位数
     * @param min 最小保留位数
     * @return
     */
    public static String floatToFractionDigitsMaxAndMin(Object obj, int max, int min) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(max);
        nf.setMinimumFractionDigits(min);
        nf.setGroupingUsed(false);
        return nf.format(parseFloat(obj));
    }

    /**
     * 获取格式化的金额，分转化为元，并且123.0为123，123.1为123.1，123.345为123.34，123.3451为123.35
     *
     * @return
     */
    public static String getFormatAmount(String amount) {
        return NumberUtil.floatToFractionDigits(NumberUtil.parseDouble(amount) / 100d, 2);
    }

    /**
     * 表明四舍五入，强制性保留digits位  例如：保留两位时 2.123 = 2.12 | 2.125 = 2.13
     *
     * @param obj
     * @param digits
     * @return
     */
    public static String floatToBigDecimal(Object obj, int digits) {
        BigDecimal b = new BigDecimal(String.valueOf(obj));
        return b.setScale(digits, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 表明四舍五入
     *
     * @param obj
     * @param digits
     * @return
     */
    public static float floatToBigDecimalF(Object obj, int digits) {
        return Float.valueOf(floatToBigDecimal(obj, digits));
    }

    /**
     * object to double
     *
     * @param obj obj
     * @return
     */
    public static double parseDouble(Object obj) {
        double count = 0;
        if (obj != null) {
            if (obj instanceof Integer) {
                count = ((Integer) obj).doubleValue();
            } else if (obj instanceof Long) {
                count = ((Long) obj).doubleValue();
            } else if (obj instanceof BigInteger) {
                count = ((BigInteger) obj).doubleValue();
            } else if (obj instanceof Float) {
                count = ((Float) obj).doubleValue();
            } else if (obj instanceof Double) {
                count = ((Double) obj).doubleValue();
            } else if (obj instanceof BigDecimal) {
                count = ((BigDecimal) obj).doubleValue();
            } else if (obj instanceof Byte) {
                count = ((Byte) obj).doubleValue();
            } else if (obj instanceof String) {
                if (TextUtils.isEmpty((String) obj)) {
                    return count;
                }
                try {
                    count = Double.parseDouble(((String) obj).trim());
                } catch (Exception e) {
                    return count;
                }
            }
        }

        return count;
    }

    /**
     * object to float
     *
     * @param obj obj
     * @return
     */
    public static float parseFloat(Object obj) {
        float count = 0;
        if (obj != null) {
            if (obj instanceof Integer) {
                count = ((Integer) obj).floatValue();
            } else if (obj instanceof Long) {
                count = ((Long) obj).floatValue();
            } else if (obj instanceof BigInteger) {
                count = ((BigInteger) obj).floatValue();
            } else if (obj instanceof Float) {
                count = ((Float) obj).floatValue();
            } else if (obj instanceof Double) {
                count = ((Double) obj).floatValue();
            } else if (obj instanceof BigDecimal) {
                count = ((BigDecimal) obj).floatValue();
            } else if (obj instanceof Byte) {
                count = ((Byte) obj).floatValue();
            } else if (obj instanceof String) {
                if (TextUtils.isEmpty((String) obj)) {
                    return count;
                }
                try {
                    count = Float.parseFloat(((String) obj).trim());
                } catch (Exception e) {
                    return count;
                }
            }
        }

        return count;
    }

    /**
     * object to long
     *
     * @param obj obj
     * @return
     */
    public static long parseLong(Object obj) {
        long count = 0;
        if (obj != null) {
            if (obj instanceof Integer) {
                count = ((Integer) obj).longValue();
            } else if (obj instanceof Long) {
                count = ((Long) obj);
            } else if (obj instanceof BigInteger) {
                count = ((BigInteger) obj).longValue();
            } else if (obj instanceof Float) {
                count = (long) ((Float) obj + 0.5);
            } else if (obj instanceof Double) {
                count = (long) ((Double) obj + 0.5);
            } else if (obj instanceof BigDecimal) {
                count = ((BigDecimal) obj).longValue();
            } else if (obj instanceof Byte) {
                count = ((Byte) obj).longValue();
            } else if (obj instanceof String) {
                if (TextUtils.isEmpty((String) obj)) {
                    return count;
                }
                try {
                    count = Long.parseLong(((String) obj).trim());
                } catch (Exception e) {
                    return count;
                }
            }
        }

        return count;
    }

    /**
     * object to int
     *
     * @param obj obj
     * @return
     */
    public static int parseInt(Object obj) {
        int count = 0;
        if (obj != null) {
            if (obj instanceof Integer) {
                count = ((Integer) obj).intValue();
            } else if (obj instanceof Long) {
                try {
                    count = ((Long) obj).intValue();
                } catch (Exception e) {
                }
            } else if (obj instanceof BigInteger) {
                count = ((BigInteger) obj).intValue();
            } else if (obj instanceof Float) {
                count = (int) ((Float) obj + 0.5);
            } else if (obj instanceof Double) {
                count = (int) ((Double) obj + 0.5);
            } else if (obj instanceof BigDecimal) {
                count = ((BigDecimal) obj).intValue();
            } else if (obj instanceof Byte) {
                count = ((Byte) obj).intValue();
            } else if (obj instanceof Boolean) {
                count = ((Boolean) obj) ? 1 : 0;
            } else if (obj instanceof String) {
                if (TextUtils.isEmpty((String) obj)) {
                    return count;
                }
                try {
                    count = Integer.parseInt(((String) obj).trim());
                } catch (Exception e) {
                    return count;
                }
            }
        }
        return count;
    }

    /**
     * 格式化数字，比如12.1k
     *
     * @param number
     * @return
     */
    public static String formatNumber(String number) {
        return formatNumber(parseInt(number));
    }

    /**
     * 格式化数字，比如12.1k
     *
     * @param number
     * @return
     */
    public static String formatNumber(int number) {
        String formatNumber;
        boolean isLanguage = true;
        if (isLanguage) {
            if (number < 100000) {
                formatNumber = addCommaForNumber(number);
            } else {
                int wan = number / 10000;
                int hundredThousand = number % 10000 / 1000;
                formatNumber = wan + "." + hundredThousand + "万";
            }
        } else {
            if (number < 10000) {
                formatNumber = addCommaForNumber(number);
            } else if (number < 1000000) {
                int thousand = number / 1000;
                int hundred = number % 1000 / 100;
                formatNumber = thousand + "." + hundred + "K";
            } else {
                int million = number / 1000000;
                int hundredThousand = number % 1000000 / 100000;
                formatNumber = million + "." + hundredThousand + "M";
            }
        }
        return formatNumber;
    }

    /**
     * 数字每隔三位添加逗号
     *
     * @param number
     * @return
     */
    @NonNull
    private static String addCommaForNumber(int number) {
        StringBuilder sb = new StringBuilder(String.valueOf(number));
        int length = sb.length();
        int index = length - 3;
        while (index > 0) {
            sb.insert(index, ",");
            index -= 3;
        }
        return sb.toString();
    }

    /**
     * 解析服务端返回的数字，包含小数和整数，将返回值直接设置在TextView中
     *
     * @param value
     * @return
     */
    public static String parseNotNull(String value) {
        return TextUtils.isEmpty(value) ? "0" : value;
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     *
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (!TextUtils.isEmpty(s) && s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    /**
     * 保留最后两位，去掉小数为0的数
     *
     * @param s
     * @return
     */
    public static String getExamScore(String s) {
        return floatToFractionDigits(subZeroAndDot(s), 2);
    }

    /**
     * 采用正则表达式的方式来判断一个字符串是否为数字 - 可以判断正负、整数小数
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        return isInt(str) || isDouble(str);
    }

    public static boolean isInt(String str) {
        return Pattern.compile("^-?[1-9]\\d*$").matcher(str).find();
    }

    public static boolean isDouble(String str) {
        return Pattern.compile("^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$").matcher(str).find();
    }
}
