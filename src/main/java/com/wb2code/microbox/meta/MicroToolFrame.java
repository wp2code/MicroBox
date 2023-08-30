package com.wb2code.microbox.meta;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.wb2code.microbox.config.MicroToolGlobalConfig;
import com.wb2code.microbox.entity.ServerConfigEntity;
import com.wb2code.microbox.meta.panel.MenuBarPanel;
import com.wb2code.microbox.meta.panel.core.BottomPanel;
import com.wb2code.microbox.meta.panel.core.ContentPanel;
import com.wb2code.microbox.meta.panel.core.TopPanel;
import com.wb2code.microbox.utils.SQLiteUtil;
import com.wb2code.microbox.utils.SystemUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author lwp
 * @date 2022-10-12
 */
@Getter
public class MicroToolFrame extends JFrame {

    private final MenuBarPanel menuBarPanel;
    private final TopPanel topPanel;
    //    private final HidePanel hidePanel;
    private final ContentPanel contentPanel;
    private final BottomPanel bottomPanel;
    /**
     * 统计
     */
    private double totalHeight;
    private JSplitPane jSplitPane;

    /**
     * @param config
     */
    public MicroToolFrame(MicroToolGlobalConfig config) throws IOException {
        super(config.getTitle());
        this.setLayout(new BorderLayout());
        this.setPreferredSize(config.getDimension());
        if (StrUtil.isNotBlank(config.getIcon())) {
            this.setIconImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(config.getIcon())));
        }
        final Dimension dimension = config.getDimension();
        //1100 X 720
        this.topPanel = new TopPanel(this, new Dimension((int) dimension.getWidth(), 400));
        this.contentPanel = new ContentPanel(this, new Dimension(420, dimension.height - 20));
        this.bottomPanel = new BottomPanel(this, new Dimension(dimension.width, 20));
        jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        jSplitPane.setOneTouchExpandable(true);
        jSplitPane.add(topPanel, JSplitPane.TOP);
        jSplitPane.add(contentPanel, JSplitPane.BOTTOM);
        jSplitPane.setDividerSize(15);
        jSplitPane.setDividerLocation(300);
        this.menuBarPanel = new MenuBarPanel(this);
        this.setJMenuBar(menuBarPanel.getMenuBar());
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }


    /**
     *
     */
    public void run() {
        this.add(jSplitPane, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);
        this.setVisible(true);
        this.topPanel.initData();
        this.topPanel.updateUI();
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            final ServerConfigEntity serverConfigEntity = new ServerConfigEntity();
            serverConfigEntity.setStatus(1);
            final int count = SQLiteUtil.selectCount(serverConfigEntity);
            if (JOptionPane.showConfirmDialog(this, msg(count), "确认", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (count > 0) {
                    CompletableFuture.supplyAsync(() -> stopAll(true)).thenApply(r -> {
                        System.exit(0);
                        return true;
                    });
                } else {
                    System.exit(0);
                }
            }
        } else {
            super.processWindowEvent(e);
        }
    }

    public Boolean stopAll(boolean haveRun) {
        if (haveRun) {
            final ServerConfigEntity serverConfigEntity = new ServerConfigEntity();
            serverConfigEntity.setStatus(1);
            final List<ServerConfigEntity> list = SQLiteUtil.select(serverConfigEntity);
            if (CollUtil.isNotEmpty(list)) {
                for (ServerConfigEntity configEntity : list) {
                    if (configEntity.getPid() != null && configEntity.getPid() != -1) {
                        final boolean taskKill = SystemUtil.exeCmd(configEntity.getDirPath(), "taskkill", "/F", "/IM", String.valueOf(configEntity.getPid()));
                        if (taskKill) {
                            configEntity.setStatus(0);
                            configEntity.setPid(-1L);
                            SQLiteUtil.insertOrUpdate(configEntity);
                        }
                    }
                }
            }
        }
        return true;
    }

    private String msg(Integer count) {
        if (count != null && count > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("有");
            sb.append(count);
            sb.append("个程序正在运行，确认退出程序？");
            return sb.toString();
        }
        return "确认退出程序?";
    }

    /**
     * 计算最新大小
     *
     * @param winSize
     * @param widthPercentage
     * @param heightPercentage
     * @param isLastOne
     * @return
     */
    private Dimension calculateMinSize(Dimension winSize, Double widthPercentage, Double heightPercentage, boolean isLastOne) {
        final double width = winSize.getWidth() * widthPercentage;
        double hv;
        double height = winSize.getHeight();
        if (isLastOne) {
            hv = NumberUtil.sub(height, totalHeight);
        } else {
            height = winSize.getHeight() * heightPercentage;
            hv = (int) Math.round(height);
            totalHeight = NumberUtil.add(totalHeight, hv);
        }
        return new Dimension((int) Math.round(width), (int) hv);
    }

}
