package com.wb2code.microbox.meta.panel.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.wb2code.microbox.config.CommonConstants;
import com.wb2code.microbox.entity.ServerConfigEntity;
import com.wb2code.microbox.meta.MicroToolFrame;
import com.wb2code.microbox.meta.PlaceholdTextField;
import com.wb2code.microbox.meta.itembar.ItemBar;
import com.wb2code.microbox.meta.panel.BasePanel;
import com.wb2code.microbox.meta.panel.ComPanel;
import com.wb2code.microbox.metadata.StatusItem;
import com.wb2code.microbox.utils.DialogUtil;
import com.wb2code.microbox.utils.SQLiteUtil;
import com.wb2code.microbox.utils.SystemUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author lwp
 * @date 2022-10-13
 */
public class TopPanel extends BasePanel {
    private List<ItemBar> itemBarList;
    @Getter
    private final MicroToolFrame frame;
    private final PlaceholdTextField queryText;
    private final JButton queryBtn;
    private final JButton addBtn;
    private final ComPanel serverConfigPanel;
    private JLabel emptyLabel;
    private static ExecutorService executorService = Executors.newCachedThreadPool(r -> new Thread(r, "log-Thread"));

    public TopPanel(MicroToolFrame frame, Dimension dimension) {
        super();
        this.frame = frame;
        this.setPreferredSize(dimension);
        this.setLayout(new BorderLayout());
        ComPanel opt = new ComPanel(new FlowLayout(FlowLayout.CENTER));
        this.queryText = new PlaceholdTextField("搜索服务名称/jar包", null);
        this.queryBtn = new JButton("搜索");
        this.queryText.setPreferredSize(new Dimension(200, 30));
        this.addBtn = new JButton("添加");
        JButton restBtn = new JButton("重置");
        restBtn.setIcon(CommonConstants.QUERY_RESET_ICON);
        JButton refreshBtn = new JButton("刷新程序包");
        refreshBtn.setIcon(CommonConstants.REFRESH_ICON);
        final JComboBox<StatusItem> comboBox = new JComboBox<>();
        comboBox.requestFocus();
        comboBox.addItem(new StatusItem("全部", null));
        comboBox.addItem(new StatusItem("运行中", 1));
        comboBox.addItem(new StatusItem("未运行", 0));
        opt.add(comboBox);
        opt.add(queryText);
        opt.add(queryBtn);
        opt.add(restBtn);
        opt.add(addBtn);
        opt.add(refreshBtn);
        serverConfigPanel = new ComPanel(BoxLayout.Y_AXIS);
        JScrollPane scrollPane = new JScrollPane(serverConfigPanel);
        scrollPane.setBorder(null);
        this.add(opt, BorderLayout.NORTH);
        this.add(scrollPane);
        this.setVisible(true);
        this.queryBtn.setIcon(CommonConstants.QUERY_ICON);
        this.addBtn.setIcon(CommonConstants.ADD_ICON);
        this.queryBtn.addActionListener(e -> {
            final ServerConfigEntity serverConfig = new ServerConfigEntity();
            serverConfig.setServerName(queryText.getText());
            serverConfig.setJarName(queryText.getText());
            serverConfig.setStatus(((StatusItem) comboBox.getSelectedItem()).getValue());
            refreshServer(serverConfig);
            updateUI();
        });
        refreshBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "全量更新目录下未运行的最新程序包", "确认", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                final ServerConfigEntity condition = new ServerConfigEntity();
                condition.setType(0);
                final List<ServerConfigEntity> configEntities = SQLiteUtil.select(condition);
                if (CollUtil.isNotEmpty(configEntities)) {
                    boolean isRefresh = false;
                    for (ServerConfigEntity config : configEntities) {
                        //运行中程序不可变更
                        if (config.getStatus() != null && config.getStatus() == 1) {
                            continue;
                        }
                        boolean currIsRefresh = false;
                        //文件目录可触发变更
                        if (config.getType()!=null && config.getType() == 0) {
                            //文件目录下最新程序包
                            final List<String> jarList = SystemUtil.listFileNames(config.getServerJarPath(), "jar", "sources");
                            if (CollUtil.isNotEmpty(jarList)) {
                                final String newJarName = jarList.get(0);
                                //未变更的文件 不用刷新
                                if (!StrUtil.equals(newJarName, config.getJarName())) {
                                    config.setJarName(jarList.get(0));
                                    currIsRefresh = isRefresh = true;
                                }
                            } else {
                                //不存在则置空
                                if (StrUtil.isNotBlank(config.getJarName())) {
                                    config.setJarName("");
                                    currIsRefresh = isRefresh = true;
                                }
                            }
                        }
                        if (currIsRefresh) {
                            SQLiteUtil.insertOrUpdate(config);
                        }
                    }
                    //刷新程序
                    if (isRefresh) {
                        refreshServer(null);
                        updateUI();
                    }
                }
            }
        });
        this.addBtn.addActionListener(e -> {
            DialogUtil.openServerDialog(frame, "添加服务", null);
        });
        restBtn.addActionListener(e -> {
            queryText.init();
            comboBox.setSelectedIndex(0);
        });
    }


    public void initData() {
        this.refreshServer(null);
    }

    /**
     * @param textArea
     */
    public synchronized  void addLogJTextAreaIfAbsent(String textArea){
        final ContentPanel contentPanel = frame.getContentPanel();
        if (contentPanel != null) {
            try {
                contentPanel.addLogJTextAreaIfAbsent(textArea);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    /**
     * 刷新
     */
    public void refreshServer(ServerConfigEntity query) {
//        SwingUtilities.invokeLater(() -> {
//
//        });
        this.itemBarList = this.getItemBarList(query);
        if (CollUtil.isNotEmpty(itemBarList)) {
            serverConfigPanel.removeAll();
            for (ItemBar itemBar : itemBarList) {
                serverConfigPanel.add(itemBar);
            }
        } else {
            setEmpty();
        }

    }

    public void setEmpty() {
        serverConfigPanel.removeAll();
        final ComPanel comPanel = new ComPanel(empty());
        comPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        serverConfigPanel.add(comPanel);
    }

    public JLabel empty() {
        if (emptyLabel == null) {
            emptyLabel = new JLabel("无数据~");
        }
        emptyLabel.setForeground(Color.RED);
        emptyLabel.setFont(new Font("Helvetica", Font.BOLD, 15));
        return emptyLabel;
    }

    public void clearServer() {
        setEmpty();
    }

    @Override
    public void updateUI() {
        SwingUtilities.invokeLater(() -> {
            super.updateUI();
        });
    }

    private List<ItemBar> getItemBarList(ServerConfigEntity serverConfig) {
        serverConfig = Optional.ofNullable(serverConfig).orElse(new ServerConfigEntity());
        final List<ServerConfigEntity> configEntities = SQLiteUtil.select(serverConfig);
        if (CollUtil.isNotEmpty(configEntities)) {
            return configEntities.stream().map(v -> new ItemBar(this, v, (data, log) -> {
                frame.getContentPanel().printLog(data, log);
            }, data -> {
                DialogUtil.openServerDialog(frame, "编辑服务", data);
            }, data -> {
                int res = JOptionPane.showConfirmDialog(null,
                        "确认删除?【" + data.getServerName() + "】", "确认",
                        JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION) {
                    if (SQLiteUtil.delServerConfig(data.getId())) {
                        refreshServer(null);
                        updateUI();
                    }
                }
            }, executorService)).collect(Collectors.toList());
        }
        return null;
    }
}
