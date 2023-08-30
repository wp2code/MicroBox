package com.wb2code.microbox.meta.itembar;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.wb2code.microbox.config.CommonConstants;
import com.wb2code.microbox.entity.ServerConfigEntity;
import com.wb2code.microbox.meta.label.AutoLabel;
import com.wb2code.microbox.meta.label.LinkLabel;
import com.wb2code.microbox.meta.panel.ComPanel;
import com.wb2code.microbox.meta.panel.core.TopPanel;
import com.wb2code.microbox.utils.DialogUtil;
import com.wb2code.microbox.utils.LogUtil;
import com.wb2code.microbox.utils.SQLiteUtil;
import com.wb2code.microbox.utils.SystemUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author lwp
 * @date 2022-10-13
 */
@Getter
public class ItemBar extends JPanel {
    private final ServerConfigEntity serverConfig;
    private final AutoLabel statusLabel;
    private final ExecutorService executorService;
    private final TopPanel topPanel;

    public ItemBar(TopPanel topPanel, ServerConfigEntity serverConfig, BiConsumer<ServerConfigEntity, String> consumer, Consumer<ServerConfigEntity> detail, Consumer<ServerConfigEntity> deleteBtn, ExecutorService executorService) {
        this.serverConfig = serverConfig;
        this.topPanel = topPanel;
        this.executorService = executorService;
        AutoLabel name = new AutoLabel(serverConfig.getServerName(), null, 200, 30);
        LinkLabel changeLinkLabel = null;
        if (FileUtil.isDirectory(serverConfig.getServerJarPath())) {
            changeLinkLabel = new LinkLabel("切换jar", null, "change", serverConfig, e -> {
                DialogUtil.openChangeJarDialog(topPanel, "切换【" + serverConfig.getServerName() + "】jar", serverConfig);
            });
            final String jarName = serverConfig.getJarName();
            if (StrUtil.isBlank(jarName)) {
                final List<String> jarList = SystemUtil.listFileNames(serverConfig.getServerJarPath(), "jar", "sources");
                if (CollUtil.isNotEmpty(jarList)) {
                    serverConfig.setJarName(jarList.get(0));
                }
            }
        }
        String jarNameLabelText = serverConfig.getJarName();
        String jarNameLabelTextColor = null;
        if (StrUtil.isBlank(serverConfig.getJarName())) {
            changeLinkLabel = null;
            jarNameLabelText = "~没有jar~";
            jarNameLabelTextColor = "#E53333";
        }
        AutoLabel jarNameLabel = new AutoLabel(jarNameLabelText, jarNameLabelTextColor, 500, 30);
        jarNameLabel.setToolTipText(serverConfig.getFullServerJarPath());
        statusLabel = new AutoLabel(serverConfig.getStatusDesc(), serverConfig.getStatusDescColor(), 50, 30);
        LinkLabel config = new LinkLabel("编辑", null, "toEdit", serverConfig, e -> {
            if (detail != null) {
                detail.accept(serverConfig);
            }
        });

        LinkLabel run = new LinkLabel(runDesc(serverConfig), Color.BLUE, serverConfig.getStatus() == 1 ? "toStop" : "toRun", serverConfig, e -> {
            refresh(e, serverConfig);
            executorService.submit(() -> {
                final Long pid = exe(serverConfig, consumer, getCmd(serverConfig));
                if (serverConfig.getStatus() == 1) {
                    serverConfig.setPid(pid);
                } else {
                    serverConfig.setPid(-1L);
                }
                SQLiteUtil.insertOrUpdate(serverConfig);
            });

        });

        LinkLabel delete = new LinkLabel("删除", null, "delete", serverConfig, e -> {
            if (serverConfig.getStatus() == 1) {
                DialogUtil.error("服务正在运行！不可删除！");
                return;
            }
            if (deleteBtn != null) {
                deleteBtn.accept(serverConfig);
            }
        });
        ComPanel nameP = new ComPanel(name);
        ComPanel jarPathP = new ComPanel(jarNameLabel);
        if (changeLinkLabel != null) {
            jarPathP.add(changeLinkLabel);
        }
        if (StrUtil.isNotBlank(serverConfig.getWebSite())) {
            LinkLabel website = new LinkLabel("访问服务网址", null, "webSite", serverConfig, e -> {
                final String url = Base64.decodeStr(serverConfig.getWebSite());
                try {
                    Desktop.getDesktop().browse(URLUtil.toURI(url));
                } catch (IOException ex) {
                    LogUtil.error("打开网站{}，失败{}", url, ExceptionUtil.getMessage(ex));
                }
            });
            jarPathP.add(website);
        }
        ComPanel statusP = new ComPanel(statusLabel);
        ComPanel runP = new ComPanel(run);
        ComPanel deleteP = new ComPanel(delete);

        ComPanel configP = new ComPanel(config);
        final Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(nameP);
        horizontalBox.add(Box.createHorizontalStrut(10));
        horizontalBox.add(jarPathP);
        horizontalBox.add(Box.createHorizontalStrut(10));
        horizontalBox.add(statusP);
        horizontalBox.add(Box.createHorizontalStrut(20));
        horizontalBox.add(runP);
        horizontalBox.add(configP);
        horizontalBox.add(deleteP);
        horizontalBox.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        this.add(horizontalBox);
    }

    /**
     *
     */
    public Long exe(ServerConfigEntity serverConfig, BiConsumer<ServerConfigEntity, String> consumer, String... cmd) {
        try {
            Charset charset = StandardCharsets.UTF_8;
            if (serverConfig.getStatus() != null && serverConfig.getStatus() == 0) {
                charset = Charset.forName("GBK");
            }
            return SystemUtil.exeCmd(serverConfig.getDirPath(), (pid, log) -> {
                consumer.accept(serverConfig, log);
            }, executorService, charset, cmd);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 刷新
     */
    public void refresh(LinkLabel optLabel, ServerConfigEntity serverConfig) {
        if (serverConfig.getStatus() == 0) {
            final String jarName = serverConfig.getJarName();
            if (StrUtil.isBlank(jarName)) {
                DialogUtil.error("jar不存在");
                return;
            }
            final String serverJarPath = serverConfig.getServerJarPath();
            if (serverConfig.getType() == 1) {
                if (!FileUtil.exist(serverJarPath)) {
                    DialogUtil.error("[" + serverJarPath + "]不存在");
                    return;
                }
            } else {
                if (FileUtil.isDirectory(serverJarPath)) {
                    if (!FileUtil.exist(new File(serverJarPath, jarName))) {
                        DialogUtil.error("[" + jarName + "]不存在");
                        return;
                    }
                } else {
                    DialogUtil.error("[" + serverJarPath + "]不存在");
                    return;
                }
            }
            topPanel.addLogJTextAreaIfAbsent(CommonConstants.COMM_LOG_TEXT_AREA);
            optLabel.setIconName("toStop");
            optLabel.setToolTipText("停止");
            serverConfig.setStatus(1);
        } else if (serverConfig.getStatus() == 1) {
            optLabel.setToolTipText("运行");
            serverConfig.setStatus(0);
            optLabel.setIconName("toRun");

        }
        statusLabel.setLabelText(statusLabel, serverConfig.getStatusDesc(), serverConfig.getStatusDescColor());
    }

    public String runDesc(ServerConfigEntity config) {
        return config.getStatus() != null && config.getStatus() == 1 ? "停止" : "运行";
    }

    /**
     * @param config
     * @return
     */
    private String[] getCmd(ServerConfigEntity config) {
        final ArrayList<String> list = new ArrayList<>();
        if (config.getStatus() == null || config.getStatus() == 0) {
            if (config.getPid() != null) {
                LogUtil.info("【{}】停止！停止程序：【{}】", config.getServerName(), config.getFullServerJarPath());
                list.add("taskkill");
                list.add("/F");
                list.add("/IM");
                list.add(String.valueOf(config.getPid()));
            }
        } else {
            LogUtil.info("【{}】启动！启动程序：【{}】", config.getServerName(), config.getFullServerJarPath());
            list.add("java");
            list.add("-server");
            list.add("-Xms512m");
            list.add("-Xmx512m");
            list.add("-Xmn256m");
            list.add("-XX:-UseLargePages");
            list.add("-Dfile.encoding=UTF-8");
            list.add("-jar");
            list.add(config.getActualServerJarPath());
        }
        return list.stream().toArray(String[]::new);
    }
}
