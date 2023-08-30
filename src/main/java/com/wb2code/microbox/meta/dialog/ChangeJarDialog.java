package com.wb2code.microbox.meta.dialog;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.wb2code.microbox.config.CommonConstants;
import com.wb2code.microbox.entity.ServerConfigEntity;
import com.wb2code.microbox.meta.panel.ComPanel;
import com.wb2code.microbox.meta.panel.core.TopPanel;
import com.wb2code.microbox.utils.DialogUtil;
import com.wb2code.microbox.utils.SQLiteUtil;
import com.wb2code.microbox.utils.SystemUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author lwp
 * @date 2022-11-15
 */
public class ChangeJarDialog extends JDialog {

    public ChangeJarDialog(Component parentComponent, String title, ServerConfigEntity config) {
        super((Frame) SwingUtilities.windowForComponent(parentComponent), title, true);
        if (config.getStatus() == 1) {
            DialogUtil.error("服务正在运行！不可切换！");
            return;
        }
        final List<String> jarList = SystemUtil.listFileNames(config.getServerJarPath(), "jar", "sources");
        if (CollUtil.isEmpty(jarList)) {
            DialogUtil.error("当前路径没有可运行jar包！");
            return;
        }
        JButton confirmBtn;
        JButton closeBtn;
        ComPanel btn = new ComPanel(FlowLayout.CENTER, closeBtn = new JButton("关闭"), confirmBtn = new JButton("确认"));
        final JComboBox<String> comboBox = new JComboBox<>();
        for (String fileName : jarList) {
            comboBox.addItem(fileName);
        }
        final String jarName = config.getJarName();
        if (StrUtil.isNotBlank(jarName)) {
            comboBox.setSelectedItem(jarName);
        }
        ComPanel panel = new ComPanel(new JLabel("选择jar："), comboBox);
        Box box = Box.createVerticalBox();
        box.add(Box.createVerticalStrut(10));
        box.add(panel);
        box.add(Box.createVerticalStrut(10));
        box.add(btn);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(CommonConstants.SYS_ICON)));
        this.setMinimumSize(new Dimension(400, 120));
        this.setContentPane(new ComPanel(new FlowLayout(FlowLayout.CENTER), box));
        ChangeJarDialog changeJarDialog = this;
        confirmBtn.addActionListener(e -> {
            config.setJarName(String.valueOf(comboBox.getSelectedItem()));
            final SQLiteUtil.Result result = SQLiteUtil.insertOrUpdate(config);
            if (result.isSuccess()) {
                ((TopPanel) parentComponent).refreshServer(null);
                close();
                ((TopPanel) parentComponent).updateUI();
            }
        });
        closeBtn.addActionListener(e -> changeJarDialog.close());
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }

    public void close() {
        this.setVisible(false);
        this.dispose();
    }
}
