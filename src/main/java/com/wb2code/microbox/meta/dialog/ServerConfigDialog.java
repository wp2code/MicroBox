package com.wb2code.microbox.meta.dialog;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.wb2code.microbox.config.CommonConstants;
import com.wb2code.microbox.entity.ServerConfigEntity;
import com.wb2code.microbox.meta.MicroToolFrame;
import com.wb2code.microbox.meta.PlaceholdTextField;
import com.wb2code.microbox.meta.panel.ComPanel;
import com.wb2code.microbox.meta.panel.CustomJFileChooserPanel;
import com.wb2code.microbox.meta.panel.core.TopPanel;
import com.wb2code.microbox.utils.DialogUtil;
import com.wb2code.microbox.utils.SQLiteUtil;
import com.wb2code.microbox.utils.SystemUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

/**
 * @author liu_wp
 * @date 2020/11/10
 * @see
 */
public class ServerConfigDialog extends JDialog {
    private final PlaceholdTextField serverName;
    private final PlaceholdTextField webSite;
    private CustomJFileChooserPanel customJFileChooserPanel;

    /**
     * @param parentComponent
     */
    public ServerConfigDialog(Component parentComponent, String title, ServerConfigEntity config) {
        super((Frame) SwingUtilities.windowForComponent(parentComponent), title, true);
        ComPanel j1 = new ComPanel(new JLabel("服务名称："), serverName = new PlaceholdTextField("服务名称", config.getServerName()));
        serverName.setPreferredSize(new Dimension(180, 30));
        final ComPanel j2 = new ComPanel(BoxLayout.X_AXIS);
        j2.add(new JLabel("服务网址："));
        j2.add(Box.createHorizontalStrut(12));
        j2.add(webSite = new PlaceholdTextField("服务网址", StrUtil.isNotBlank(config.getWebSite()) ? Base64.decodeStr(config.getWebSite()) : null));
//        "服务访问网址（http或https协议）非必填",
        webSite.setPreferredSize(new Dimension(250, 30));
        final ComPanel j3 = new ComPanel(BoxLayout.X_AXIS);
        j3.add(new JLabel(" 服务路径："));
        j3.add(customJFileChooserPanel = new CustomJFileChooserPanel(this, false, null, config.getServerJarPath(), null));
        JButton closeBtn;
        JButton confirmBtn;
        ComPanel j4 = new ComPanel(FlowLayout.CENTER, closeBtn = new JButton("关闭"), confirmBtn = new JButton("确认"));
        Box box = Box.createVerticalBox();
        box.add(j1);
        box.add(j3);
        box.add(j2);
        box.add(Box.createVerticalStrut(10));
        box.add(j4);
        this.setMinimumSize(new Dimension(500, 200));
        this.setContentPane(new ComPanel(new FlowLayout(FlowLayout.CENTER), box));
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(CommonConstants.SYS_ICON)));
        ServerConfigDialog configDialog = this;
        confirmBtn.addActionListener(e -> {
            if (StrUtil.isBlank(serverName.getText())) {
                DialogUtil.error("服务名称不能为空");
                return;
            }
            final String selectValue = customJFileChooserPanel.getSelectValue();
            if (StrUtil.isBlank(selectValue)) {
                DialogUtil.error("启动程序不能为空");
                return;
            }
            ServerConfigEntity newCfg = new ServerConfigEntity();
            newCfg.setId(config.getId());
            newCfg.setServerName(serverName.getText().trim());
            newCfg.setStatus(Optional.ofNullable(config.getStatus()).orElse(0));
            newCfg.setServerJarPath(selectValue.trim());
            final String webSitText = webSite.getText();

            if (config.getStatus() != null && config.getStatus() == 1 && !StrUtil.equals(newCfg.getServerJarPath(), config.getServerJarPath())) {
                DialogUtil.error("服务正在运行！路径不可编辑！");
                return;
            }
            if (FileUtil.isFile(newCfg.getServerJarPath())) {
                newCfg.setJarName(FileUtil.getName(newCfg.getActualServerJarPath()));
            } else if (FileUtil.isDirectory(newCfg.getServerJarPath())) {
                final List<String> jarList = SystemUtil.listFileNames(newCfg.getServerJarPath(), "jar", "sources");
                if (CollUtil.isEmpty(jarList)) {
                    DialogUtil.error("当前路径下没有jar！");
                    return;
                }
            } else {
                DialogUtil.error("路径信息错误！");
                return;
            }
            if (StrUtil.isNotBlank(webSitText)) {
                if (!StrUtil.startWithAny(webSitText, "http://", "https://")) {
                    DialogUtil.error("服务网址错误！");
                    return;
                }
                if (webSitText.length() > CommonConstants.WEB_SITE_MAX_LENGTH) {
                    DialogUtil.error("服务网址太长！");
                    return;
                }
                newCfg.setWebSite(Base64.encode(webSitText.trim()));
            }else{
                newCfg.setWebSite("");
            }
            if (FileUtil.isDirectory(newCfg.getServerJarPath())) {
                newCfg.setType(0);
            } else {
                newCfg.setType(1);
            }
            final SQLiteUtil.Result result = SQLiteUtil.insertOrUpdate(newCfg);
            if (!result.isSuccess()) {
                DialogUtil.error(result.getError());
                return;
            }
            if (parentComponent instanceof MicroToolFrame) {
                final TopPanel topPanel = ((MicroToolFrame) parentComponent).getTopPanel();
                topPanel.refreshServer(null);
                topPanel.updateUI();
                configDialog.close();
            }
        });
        closeBtn.addActionListener(e -> configDialog.close());
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
    }

    public void close() {
        this.setVisible(false);
        this.dispose();
    }


}
