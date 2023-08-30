package com.wb2code.microbox.meta.panel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.wb2code.microbox.config.CommonConstants;
import com.wb2code.microbox.entity.ServerConfigEntity;
import com.wb2code.microbox.meta.CustomBasicMenuItemUI;
import com.wb2code.microbox.meta.CustomJMenuItem;
import com.wb2code.microbox.meta.MicroToolFrame;
import com.wb2code.microbox.utils.DialogUtil;
import com.wb2code.microbox.utils.SQLiteUtil;
import com.wb2code.microbox.utils.SystemUtil;
import lombok.Getter;

import javax.swing.*;
import java.util.List;

/**
 * @author lwp
 * @date 2022-10-12
 */
@Getter
public class MenuBarPanel extends BasePanel {
    private final JMenuBar menuBar;

    public MenuBarPanel(MicroToolFrame frame) {
        menuBar = new JMenuBar();
        JMenu menu = new JMenu("工具");
        menu.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/tool.png")));
        CustomJMenuItem killAppItem = new CustomJMenuItem("解除端口占用", "unbind", new CustomBasicMenuItemUI(CommonConstants.selectionBackground, CommonConstants.selectionForeground));
        CustomJMenuItem killItem = new CustomJMenuItem("一键Kill（停止java程序）", "stop", new CustomBasicMenuItemUI(CommonConstants.selectionBackground, CommonConstants.selectionForeground));
        final CustomJMenuItem cleanConfig = new CustomJMenuItem("一键重置配置", "reset", new CustomBasicMenuItemUI(CommonConstants.selectionBackground, CommonConstants.selectionForeground));
        menu.add(killAppItem);
        menu.add(killItem);
        menu.add(cleanConfig);
        cleanConfig.addActionListener(e -> {
            int res = JOptionPane.showConfirmDialog(null,
                    "确认重置系统配置?", "确认",
                    JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                if (SQLiteUtil.init(true)) {
                    frame.getTopPanel().clearServer();
                    frame.getTopPanel().updateUI();
                }
            }
        });
        killItem.addActionListener((e) -> {
            int res = JOptionPane.showConfirmDialog(null,
                    "确认强制删除java程序?", "确认",
                    JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                final ServerConfigEntity serverConfigEntity = new ServerConfigEntity();
                serverConfigEntity.setStatus(1);
                final List<ServerConfigEntity> list = SQLiteUtil.select(serverConfigEntity);
                if (CollUtil.isNotEmpty(list)) {
                    for (ServerConfigEntity configEntity : list) {
                        configEntity.setPid(-1L);
                        configEntity.setStatus(0);
                        SQLiteUtil.insertOrUpdate(configEntity);
                    }
                }
                frame.getTopPanel().refreshServer(null);
                frame.getTopPanel().updateUI();
                SystemUtil.exeCmd(FileUtil.getTmpDir(), "taskkill", "/F", "/IM", "java.exe");
            }
        });
        killAppItem.addActionListener((e) -> {
            DialogUtil.openKillPortDialog(frame);
        });
        menuBar.add(menu);
    }
}
