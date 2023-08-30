package com.wb2code.microbox.meta.dialog;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.wb2code.microbox.config.CommonConstants;
import com.wb2code.microbox.entity.ServerConfigEntity;
import com.wb2code.microbox.meta.CustomJTextField;
import com.wb2code.microbox.meta.MicroToolFrame;
import com.wb2code.microbox.meta.panel.ComPanel;
import com.wb2code.microbox.utils.DialogUtil;
import com.wb2code.microbox.utils.SQLiteUtil;
import com.wb2code.microbox.utils.SystemUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

/**
 * @author lwp
 * @date 2023-08-12
 **/
public class KillPortDialog extends JDialog {

    public KillPortDialog(Component parentComponent) {
        super((Frame) SwingUtilities.windowForComponent(parentComponent), "解除端口占用", true);
        final CustomJTextField port = new CustomJTextField(null, new Dimension(100, 30));
        ComPanel panel = new ComPanel(new JLabel("输入端口："), port);
        JButton confirmBtn;
        JButton closeBtn;
        ComPanel btn = new ComPanel(FlowLayout.CENTER, closeBtn = new JButton("关闭"), confirmBtn = new JButton("确认"));
        Box box = Box.createVerticalBox();
        box.add(Box.createVerticalStrut(10));
        box.add(panel);
        box.add(Box.createVerticalStrut(10));
        box.add(btn);
        closeBtn.addActionListener(e -> close());
        confirmBtn.addActionListener(e -> {
            final String text = port.getText();
            if (StrUtil.isBlank(text)) {
                DialogUtil.error("请输入端口号");
                return;
            }
            final Set<String> pidSet = SystemUtil.getPidByPort(text.trim());
            if (CollUtil.isNotEmpty(pidSet)) {
                boolean isOk = false;
                for (final String pid : pidSet) {
                    final boolean isSuccess = SystemUtil.exeCmd(FileUtil.getTmpDir(), "taskkill", "/F", "/pid", pid);
                    if (isSuccess) {
                        final ServerConfigEntity serverConfigEntity = new ServerConfigEntity();
                        serverConfigEntity.setStatus(1);
                        serverConfigEntity.setPid(Long.valueOf(pid));
                        final List<ServerConfigEntity> list = SQLiteUtil.select(serverConfigEntity);
                        if (CollUtil.isNotEmpty(list)) {
                            for (ServerConfigEntity configEntity : list) {
                                configEntity.setPid(-1L);
                                configEntity.setStatus(0);
                                SQLiteUtil.insertOrUpdate(configEntity);
                            }
                        }
                        isOk = true;
                    }
                }
                if (isOk) {
                    MicroToolFrame frame = (MicroToolFrame) parentComponent;
                    frame.getTopPanel().refreshServer(null);
                    frame.getTopPanel().updateUI();
                    close();
                    DialogUtil.success("解除端口" + text + "占用成功");
                }
            } else {
                DialogUtil.error("端口未占用");
            }
        });
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(CommonConstants.SYS_ICON)));
        this.setMinimumSize(new Dimension(300, 150));
        this.setContentPane(new ComPanel(new FlowLayout(FlowLayout.CENTER), box));
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }

    public void close() {
        this.setVisible(false);
        this.dispose();
    }
}
