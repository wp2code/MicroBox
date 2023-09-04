package com.wb2code.microbox.utils;

import com.wb2code.microbox.annotation.entity.ServerConfigEntity;
import com.wb2code.microbox.meta.dialog.ChangeJarDialog;
import com.wb2code.microbox.meta.dialog.KillPortDialog;
import com.wb2code.microbox.meta.dialog.NgrokDialog;
import com.wb2code.microbox.meta.dialog.ServerConfigDialog;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Optional;

/**
 * @author lwp
 * @date 2022-10-17
 */
public class DialogUtil {
//    private static final Object sharedFrameKey = DialogUtil.class;
//    private static final Object sharedOwnerFrameKey =
//            new StringBuffer("DialogUtil.customerSharedOwnerFrame");

    /**
     * @param parentComponent
     * @param title
     * @param serverConfig
     * @return
     */
    public static ServerConfigDialog openServerDialog(Component parentComponent, String title, ServerConfigEntity serverConfig) {
        final ServerConfigDialog serverDialog = new ServerConfigDialog(parentComponent, title, Optional.ofNullable(serverConfig).orElse(new ServerConfigEntity()));
        return serverDialog;
    }

    public static ChangeJarDialog openChangeJarDialog(Component parentComponent, String title, ServerConfigEntity serverConfig) {
        final ChangeJarDialog serverDialog = new ChangeJarDialog(parentComponent, title, Optional.ofNullable(serverConfig).orElse(new ServerConfigEntity()));
        return serverDialog;
    }

    public static KillPortDialog openKillPortDialog(Component parentComponent) {
        final KillPortDialog killPortDialog = new KillPortDialog(parentComponent);
        return killPortDialog;
    }

    public static NgrokDialog openNgrokDialog(Component parentComponent, String title) {
        final NgrokDialog killPortDialog = new NgrokDialog(parentComponent, title);
        return killPortDialog;
    }

    public static void error(String msg) {
        JOptionPane.showMessageDialog(null, msg, "错误", JOptionPane.ERROR_MESSAGE, new ImageIcon(Objects.requireNonNull(SystemUtil.getSystemResource(String.format("images/%s.png", "error")))));
    }

    public static void success(String msg) {
        JOptionPane.showMessageDialog(null, msg, "成功", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(Objects.requireNonNull(SystemUtil.getSystemResource(String.format("images/%s.png", "success")))));
    }
}
