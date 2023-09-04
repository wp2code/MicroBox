package com.wb2code.microbox.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author liu_wp
 * @date 2020/11/13
 * @see
 */
public class SystemUtil {
    private static final String PORT_PATTERN = "^\\d{1,5}$";

    public static List<String> listFileNames(String dirPath, String fileType, String igChar) {
        if (!FileUtil.isDirectory(dirPath)) {
            return null;
        }
        final List<String> fileList = FileUtil.listFileNames(dirPath);
        if (CollUtil.isNotEmpty(fileList) && StrUtil.isNotBlank(fileType)) {
            return fileList.stream().filter(v -> v.lastIndexOf(igChar) <= 0 && FileUtil.getSuffix(v).equals(fileType)).sorted((o1, o2) -> {
                final Date d1 = FileUtil.lastModifiedTime(new File(dirPath, o1));
                final Date d2 = FileUtil.lastModifiedTime(new File(dirPath, o2));
                return DateUtil.compare(d2, d1);
            }).collect(Collectors.toList());
        }
        return fileList;
    }


    /**
     * @param str
     */
    public static void setClipboardString(String str) {
        //获取协同剪贴板，单例
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //封装文本内容
        Transferable trans = new StringSelection(str);
        //把文本内容设置到系统剪贴板上
        clipboard.setContents(trans, null);
    }

    /**
     * @param cmds
     * @throws IOException
     */
    public static Long exeCmd(File directory, BiConsumer<Long, String> consumer, ExecutorService executorService, Charset charset, String... cmds) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(cmds);
        processBuilder.directory(directory);
        final Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), charset != null ? charset : StandardCharsets.UTF_8));
        final Long pid = getProcessId(process);
        CompletableFuture.runAsync(() -> {
            String line;
            while (true) {
                try {
                    if ((line = reader.readLine()) == null) {
                        break;
                    }
                    ;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String finalLine = line;
                if (consumer != null) {
                    consumer.accept(pid, finalLine);
                }
            }
        }, executorService);
        return pid;
    }

    /**
     * @param directory
     * @param cmds
     * @throws IOException
     */
    public static boolean exeCmd(File directory, String... cmds) {
        ProcessBuilder processBuilder = new ProcessBuilder(cmds);
        processBuilder.directory(directory);
        try {
            processBuilder.start();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * @param batName
     */
    public static void runBat(String batName) {
        try {
            final URL resource = getSystemResource(batName + ".bat");
            final File file = new File(resource.toURI());
            String cmd = "cmd /c start " + file.getAbsolutePath();
            RuntimeUtil.exec(cmd);
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * 根据端口获取PID
     *
     * @param port
     * @return
     */
    public static Set<String> getPidByPort(String port) {
        final String result = RuntimeUtil.execForStr("netstat -ano");
        final Set<String> appList = Arrays.stream(result.split("\n")).filter(v -> StrUtil.contains(v, ":" + port)).collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(appList)) {
            Set<String> pidSet = new HashSet<>();
            for (String app : appList) {
                app = StrUtil.trim(app);
                if (!StrUtil.startWithIgnoreCase(app, "TCP")) {
                    continue;
                }
                final char[] chars = app.toCharArray();
                final List<Character> characters = new ArrayList<>();
                boolean isStart = false;
                int j = 0;
                for (int i = chars.length - 1; i >= 0; i--) {
                    final char c = chars[i];
                    if (Character.isDigit(c)) {
                        isStart = true;
                        characters.add(c);
                        j++;
                    } else {
                        if (isStart) {
                            break;
                        }
                    }
                }
                if (CollUtil.isNotEmpty(characters)) {
                    Collections.reverse(characters);
                    final String pid = characters.stream().map(Object::toString).collect(Collectors.joining());
                    pidSet.add(pid);
                }
            }
            return pidSet;
        }
        return null;
    }


    public static String getClipboardString() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable trans = clipboard.getContents(null);
        if (trans != null) {
            if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String clipboardStr = null;
                try {
                    clipboardStr = (String) trans.getTransferData(DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return clipboardStr;
            }
        }
        return null;
    }

    public static void browseWebUrl(String webUrl) {
        try {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(webUrl));
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Long getProcessId(Process p) {
        Long pid = null;
        try {
            //for windows
            if (p.getClass().getName().equals("java.lang.Win32Process") || p.getClass().getName().equals("java.lang.ProcessImpl")) {
                Field f = p.getClass().getDeclaredField("handle");
                f.setAccessible(true);
                long handl = f.getLong(p);
                Kernel32 kernel = Kernel32.INSTANCE;
                WinNT.HANDLE hand = new WinNT.HANDLE();
                hand.setPointer(Pointer.createConstant(handl));
                pid = Long.valueOf(kernel.GetProcessId(hand));
                f.setAccessible(false);
            }
            //for unix based operating systems
            else if (p.getClass().getName().equals("java.lang.UNIXProcess")) {
                Field f = p.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                pid = f.getLong(p);
                f.setAccessible(false);
            }
        } catch (Exception ex) {
        }
        return pid;
    }

    public static URL getSystemResource(String path) {
        try {
            return ClassLoader.getSystemResource(path);
        } catch (Exception e) {
        }
        return null;
    }

    public static boolean isValidPort(String port){
        return port.matches(PORT_PATTERN) && Integer.parseInt(port) >= 1024 && Integer.parseInt(port) <= 65535;
    }
}
