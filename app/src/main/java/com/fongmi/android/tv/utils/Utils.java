package com.fongmi.android.tv.utils;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.FragmentActivity;

import com.fongmi.android.tv.App;
import com.fongmi.android.tv.server.Server;
import com.permissionx.guolindev.PermissionX;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    public static boolean isEnterKey(KeyEvent event) {
        return event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_SPACE || event.getKeyCode() == KeyEvent.KEYCODE_NUMPAD_ENTER;
    }

    public static boolean isUpKey(KeyEvent event) {
        return event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP || event.getKeyCode() == KeyEvent.KEYCODE_CHANNEL_UP || event.getKeyCode() == KeyEvent.KEYCODE_PAGE_UP || event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PREVIOUS;
    }

    public static boolean isDownKey(KeyEvent event) {
        return event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN || event.getKeyCode() == KeyEvent.KEYCODE_CHANNEL_DOWN || event.getKeyCode() == KeyEvent.KEYCODE_PAGE_DOWN || event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_NEXT;
    }

    public static boolean isLeftKey(KeyEvent event) {
        return event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT;
    }

    public static boolean isRightKey(KeyEvent event) {
        return event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT;
    }

    public static boolean isBackKey(KeyEvent event) {
        return event.getKeyCode() == KeyEvent.KEYCODE_BACK;
    }

    public static boolean isDigitKey(KeyEvent event) {
        return event.getKeyCode() >= KeyEvent.KEYCODE_0 && event.getKeyCode() <= KeyEvent.KEYCODE_9 || event.getKeyCode() >= KeyEvent.KEYCODE_NUMPAD_0 && event.getKeyCode() <= KeyEvent.KEYCODE_NUMPAD_9;
    }

    public static boolean isMenuKey(KeyEvent event) {
        return event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_MENU;
    }

    public static boolean isVideoFormat(String url) {
        return isVideoFormat(url, new HashMap<>());
    }

    public static boolean isVideoFormat(String url, Map<String, String> headers) {
        if (Sniffer.CUSTOM.matcher(url).find()) return true;
        if (headers.containsKey("Accept") && headers.get("Accept").startsWith("image")) return false;
        if (url.contains("url=http") || url.contains(".js") || url.contains(".css") || url.contains(".html")) return false;
        return Sniffer.RULE.matcher(url).find();
    }

    public static boolean isVip(String url) {
        List<String> hosts = Arrays.asList("iqiyi.com", "v.qq.com", "youku.com", "le.com", "tudou.com", "mgtv.com", "sohu.com", "acfun.cn", "bilibili.com", "baofeng.com", "pptv.com");
        for (String host : hosts) if (url.contains(host)) return true;
        return false;
    }

    public static String checkProxy(String url) {
        if (url.startsWith("proxy://")) return url.replace("proxy://", Server.get().getAddress("proxy?"));
        return url;
    }

    public static String checkClan(String text) {
        if (text.contains("/localhost/")) text = text.replace("/localhost/", "/");
        if (text.startsWith("clan")) text = text.replace("clan", "file");
        return text;
    }

    public static String convert(String text) {
        return text.startsWith("file") ? Server.get().getAddress(text) : text;
    }

    public static String convert(String baseUrl, String text) {
        if (TextUtils.isEmpty(text)) return "";
        if (text.startsWith("clan")) return checkClan(text);
        if (text.startsWith(".")) text = text.substring(1);
        if (text.startsWith("/")) text = text.substring(1);
        String last = Uri.parse(baseUrl).getLastPathSegment();
        if (last == null) return Uri.parse(baseUrl).getScheme() + "://" + text;
        int index = baseUrl.lastIndexOf(last);
        return baseUrl.substring(0, index) + text;
    }

    public static String getMd5(String src) {
        try {
            if (TextUtils.isEmpty(src)) return "";
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(src.getBytes());
            BigInteger no = new BigInteger(1, bytes);
            StringBuilder sb = new StringBuilder(no.toString(16));
            while (sb.length() < 32) sb.insert(0, "0");
            return sb.toString().toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    public static String getUUID() {
        return Settings.Secure.getString(App.get().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getBase64(String ext) {
        return Base64.encodeToString(ext.getBytes(), Base64.DEFAULT | Base64.NO_WRAP);
    }

    public static String substring(String text) {
        return substring(text, 1);
    }

    public static String substring(String text, int num) {
        if (text != null && text.length() > num) return text.substring(0, text.length() - num);
        return text;
    }

    public static long format(SimpleDateFormat format, String src) {
        try {
            return format.parse(src).getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getDigit(String text) {
        try {
            if (text.startsWith("上") || text.startsWith("下")) return -1;
            if (text.contains(".")) text = text.substring(0, text.lastIndexOf("."));
            return Integer.parseInt(text.replaceAll("\\D+", ""));
        } catch (Exception e) {
            return -1;
        }
    }

    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) App.get().getSystemService(Context.INPUT_METHOD_SERVICE);
        IBinder windowToken = view.getWindowToken();
        if (imm != null && windowToken != null) {
            imm.hideSoftInputFromWindow(windowToken, 0);
        }
    }

    public static boolean hasPermission(FragmentActivity activity) {
        return PermissionX.isGranted(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
}
