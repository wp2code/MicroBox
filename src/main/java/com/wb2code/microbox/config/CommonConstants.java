package com.wb2code.microbox.config;

import com.wb2code.microbox.utils.SystemUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Objects;

/**
 * @author lwp
 * @date 2022-10-12
 */
public class CommonConstants {
    public final static String SYS_ICON = "images/mictro.png";
    public static final String SQLITE_DEFAULT_DB = "MicroBoxDb";
    public static final String COMM_LOG_TEXT_AREA = "logTextArea";
    public static final Dimension FRAME_DIMENSION = new Dimension(1100, 720);
    public static final String SQLITE_DEFAULT_NAME = "microBox_sb";
    public static final String SQLITE_DEFAULT_PWD = "microBox_sb@";
    public static File CACHE_PROJECT_SELECTED_FILE = new File("");
    public static Color selectionBackground = new Color(0x3992EA);
    public static Color selectionForeground = Color.WHITE;
    public static ImageIcon QUERY_ICON = new ImageIcon(Objects.requireNonNull(SystemUtil.getSystemResource("images/query.png")));
    public static ImageIcon ADD_ICON = new ImageIcon(Objects.requireNonNull(SystemUtil.getSystemResource("images/add.png")));
    public static ImageIcon QUERY_RESET_ICON = new ImageIcon(Objects.requireNonNull(SystemUtil.getSystemResource("images/queryReset.png")));
    public static ImageIcon REFRESH_ICON = new ImageIcon(Objects.requireNonNull(SystemUtil.getSystemResource("images/refresh.png")));

    public static final int WEB_SITE_MAX_LENGTH=500;
}
