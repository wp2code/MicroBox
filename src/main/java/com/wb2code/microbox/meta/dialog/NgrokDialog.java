package com.wb2code.microbox.meta.dialog;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.wb2code.microbox.annotation.entity.NgrokConfigEntity;
import com.wb2code.microbox.annotation.entity.Result;
import com.wb2code.microbox.config.CommonConstants;
import com.wb2code.microbox.enums.NgrokStatusEnum;
import com.wb2code.microbox.meta.CustomJTextField;
import com.wb2code.microbox.meta.label.LinkLabel;
import com.wb2code.microbox.meta.panel.ComPanel;
import com.wb2code.microbox.ngrok.TunnelInfo;
import com.wb2code.microbox.utils.DialogUtil;
import com.wb2code.microbox.utils.NgrokUtil;
import com.wb2code.microbox.utils.SQLiteUtil;
import com.wb2code.microbox.utils.SystemUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @author lwp
 * @date 2023-08-18
 **/
public class NgrokDialog extends JDialog {

    private CustomJTextField authText;
    private JLabel authLabel;
    private CustomJTextField apiAccessText;
    private JLabel apiLabel;
    private final CustomJTextField portText;
    private final ComPanel publicUrlPanel;
    private final Box boxView;
    private boolean isRunning;

    /**
     * @param parentComponent
     */
    public NgrokDialog(Component parentComponent, String title) {
        super((Frame) SwingUtilities.windowForComponent(parentComponent), title, true);
        final NgrokConfigEntity ngrokConfig = getConfig();
        portText = new CustomJTextField(ngrokConfig != null && ngrokConfig.getPort() != null && ngrokConfig.getPort() != -1 ? ngrokConfig.getPort().toString() : null, new Dimension(100, 30));
        ComPanel portPanel = new ComPanel(new JLabel("映射端口："), portText);
        ComPanel panel = new ComPanel(new JLabel("    AuthToken："));
        ComPanel apiPanel = new ComPanel(new JLabel("ApiKeyToken："));
        publicUrlPanel = new ComPanel();
        if (ngrokConfig != null) {
            if (StrUtil.isNotBlank(ngrokConfig.getAuthToken())) {
                authLabel = new JLabel(ngrokConfig.getAuthToken());
                panel.add(authLabel);
            } else {
                authText = getAuthText(ngrokConfig);
            }
            AtomicReference<LinkLabel> saveLabel = new AtomicReference<>();
            final LinkLabel editLink = new LinkLabel(CommonConstants.EDIT_BTN_TEXT, null, e -> {
                if (CommonConstants.EDIT_BTN_TEXT.equals(e.getText())) {
                    panel.remove(authLabel);
                    if (authText != null) {
                        panel.remove(authText);
                    }
                    panel.add(getAuthText(ngrokConfig), 1);
                    saveLabel.set(new LinkLabel("保存", null, Color.BLUE, ee -> {
                        updateById(ngrokConfig.getId(), authText.getText(), null, (x) -> {

                            cancel(panel, saveLabel, authText, authLabel, e);
                        });
                    }));
                    e.setText("取消");
                    e.setForeground(Color.RED);
                    panel.add(saveLabel.get(), 2);
                } else {
                    cancel(panel, saveLabel, authText, authLabel, e);
                }
                updateUI();
            });
            panel.add(editLink);
            if (StrUtil.isNotBlank(ngrokConfig.getAuthToken())) {
                panel.add(editLink);
            }
            if (StrUtil.isNotBlank(ngrokConfig.getApiAccessToken())) {
                apiLabel = new JLabel(ngrokConfig.getApiAccessToken());
                apiPanel.add(apiLabel);
            } else {
                apiAccessText = getApiAccessText(ngrokConfig);
            }
            final AtomicReference<LinkLabel> apiSaveLabel = new AtomicReference<>();
            final LinkLabel editApiLink = new LinkLabel("编辑", null, e -> {
                if (CommonConstants.EDIT_BTN_TEXT.equals(e.getText())) {
                    apiPanel.remove(apiLabel);
                    if (apiAccessText != null) {
                        apiPanel.remove(apiLabel);
                    }
                    apiPanel.add(getApiAccessText(ngrokConfig), 1);
                    apiSaveLabel.set(new LinkLabel("保存", null, Color.BLUE, ee -> {
                        updateById(ngrokConfig.getId(), null, apiAccessText.getText(), (x) -> {
                            cancel(apiPanel, apiSaveLabel, apiAccessText, apiLabel, e);
                        });
                    }));
                    e.setText("取消");
                    e.setForeground(Color.RED);
                    apiPanel.add(apiSaveLabel.get(), 2);
                } else {
                    cancel(apiPanel, apiSaveLabel, apiAccessText, apiLabel, e);
                }
                updateUI();
            });
            if (StrUtil.isNotBlank(ngrokConfig.getApiAccessToken())) {
                apiPanel.add(editApiLink);
                getPublicUrlPanel(ngrokConfig);
            }
        } else {
            authText = getAuthText(null);
            apiAccessText = getApiAccessText(null);
        }
        if (authText != null) {
            panel.add(authText);
        }
        if (apiAccessText != null) {
            apiPanel.add(apiAccessText);
        }
        JButton confirmBtn;
        JButton closeBtn;
        JButton webBtn;
        JButton saveBtn;
        ComPanel btn = new ComPanel(FlowLayout.CENTER, closeBtn = new JButton("关闭"), webBtn = new JButton("注册"), confirmBtn = new JButton("保存并映射"), saveBtn = new JButton("保存"));
        boxView = Box.createVerticalBox();
        boxView.add(Box.createVerticalStrut(10));
        boxView.add(portPanel);
        boxView.add(panel);
        boxView.add(apiPanel);
        if (isRunning) {
            boxView.add(publicUrlPanel, 4);
        }
        boxView.add(Box.createVerticalStrut(10));
        boxView.add(btn);
        closeBtn.addActionListener(e -> close());
        webBtn.addActionListener(e -> {
            SystemUtil.browseWebUrl("https://ngrok.com");
        });
        saveBtn.addActionListener(e -> {
            saveOrStartMap(ngrokConfig, isRunning, false);
        });
        confirmBtn.addActionListener(e -> {
            saveOrStartMap(ngrokConfig, isRunning, true);
        });
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(CommonConstants.SYS_ICON)));
        this.setMinimumSize(new Dimension(500, 200));
        this.setContentPane(new ComPanel(new FlowLayout(FlowLayout.CENTER), boxView));
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }

    public NgrokConfigEntity getConfig() {
        final List<NgrokConfigEntity> entities = SQLiteUtil.getSingleton().select(new NgrokConfigEntity());
        if (CollUtil.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    public void close() {
        this.setVisible(false);
        this.dispose();
    }

    private CustomJTextField getAuthText(NgrokConfigEntity ngrokConfig) {
        if (authText != null) {
            return authText;
        }
        return authText = new CustomJTextField(ngrokConfig != null ? ngrokConfig.getAuthToken() : null, new Dimension(280, 30));
    }

    private CustomJTextField getApiAccessText(NgrokConfigEntity ngrokConfig) {
        if (apiAccessText != null) {
            return apiAccessText;
        }
        return apiAccessText = new CustomJTextField(ngrokConfig != null ? ngrokConfig.getApiAccessToken() : null, new Dimension(280, 30));
    }

    private void cancel(ComPanel commonPanel, AtomicReference<LinkLabel> save, CustomJTextField textField, JLabel jLabel, LinkLabel label) {
        commonPanel.remove(textField);
        commonPanel.remove(save.get());
        commonPanel.add(jLabel, 1);
        label.setText("编辑");
        label.setForeground(Color.BLUE);
    }

    /**
     * @param id
     * @param auth
     * @param apiAccessToken
     */
    private void updateById(Long id, String auth, String apiAccessToken, Consumer<Void> consumer) {
        if (id == null) {
            DialogUtil.error("编辑失败，id为空！");
            return;
        }
        if (StrUtil.isBlank(auth) && StrUtil.isBlank(apiAccessToken)) {
            DialogUtil.error("编辑失败!");
            return;
        }
        final NgrokConfigEntity ngrokConfig = new NgrokConfigEntity();
        ngrokConfig.setId(id);
        ngrokConfig.setAuthToken(auth);
        ngrokConfig.setApiAccessToken(apiAccessToken);
        final boolean isOk = SQLiteUtil.getSingleton().updateByPk(ngrokConfig);
        if (isOk) {
            if (StrUtil.isNotBlank(auth)) {
                authLabel.setText(auth);
                authText.setText(auth);
            } else {
                apiLabel.setText(apiAccessToken);
                apiAccessText.setText(apiAccessToken);
            }
            if (consumer != null) {
                consumer.accept(null);
                updateUI();
            }
        }

    }

    private void updateUI() {
        //添加或删除组件后,更新窗口
        SwingUtilities.updateComponentTreeUI(this);
    }

    /**
     * @param ngrokConfig
     * @param isRunning
     * @param startMap
     */
    private void saveOrStartMap(NgrokConfigEntity ngrokConfig, boolean isRunning, boolean startMap) {
        String authValue = null;
        if (authText != null) {
            authValue = authText.getText();
        } else {
            authValue = authLabel.getText();
        }
        if (StrUtil.isBlank(authValue)) {
            DialogUtil.error("AuthToken不能为空");
            return;
        }
        String apiKeyValue = null;
        if (apiAccessText != null) {
            apiKeyValue = apiAccessText.getText();
        } else {
            apiKeyValue = apiLabel.getText();
        }
        if (StrUtil.isBlank(apiKeyValue)) {
            DialogUtil.error("ApiKeyToken不能为空");
            return;
        }
        final String portValue = portText.getText();
        if (startMap && StrUtil.isBlank(portValue)) {
            DialogUtil.error("映射端口不能为空");
            return;
        }
        if (StrUtil.isNotBlank(portValue) && !SystemUtil.isValidPort(portValue)) {
            DialogUtil.error("映射端口错误");
            return;
        }
        String tunnelName = null;
        boolean configIsDiff = false;
        if (ngrokConfig != null) {
            configIsDiff = !apiKeyValue.equals(ngrokConfig.getApiAccessToken()) || !authValue.equals(ngrokConfig.getAuthToken());
            if (!configIsDiff) {
                if (StrUtil.isBlank(portValue)) {
                    configIsDiff = true;
                } else {
                    configIsDiff = Integer.valueOf(portValue).equals(ngrokConfig.getPort());
                }
            }
            tunnelName = ngrokConfig.getTunnelName();
        } else {
            tunnelName = IdUtil.objectId();
        }
        String publicUrl = null;
        //先停止之前映射
        if (configIsDiff || startMap || isRunning) {
            final Set<String> tunnelIds = NgrokUtil.getTunnelSessionIds(ngrokConfig.getApiAccessToken());
            if (CollUtil.isNotEmpty(tunnelIds)) {
                for (final String id : tunnelIds) {
                    final boolean isStopSession = NgrokUtil.stopSession(ngrokConfig.getApiAccessToken(), id);
                    if (!isStopSession) {
                        DialogUtil.error("停止" + id + "连接异常");
                        return;
                    }
                }
            }
        }
        Integer status = NgrokStatusEnum.UN_RUNNING.getStatus();
        if (startMap) {
            //创建新的映射
            final Result<String> result = NgrokUtil.createHttpNg(authValue.trim(), tunnelName, portValue.trim());
            if (!result.isSuccess()) {
                status = NgrokStatusEnum.RUN_FAIL.getStatus();
            } else {
                publicUrl = result.getData();
                status = NgrokStatusEnum.RUNNING.getStatus();
            }
        }
        //保存或更新映射信息
        final NgrokConfigEntity config = new NgrokConfigEntity();
        config.setAuthToken(authValue);
        config.setApiAccessToken(apiKeyValue);
        config.setPublicUrl(publicUrl);
        if (StrUtil.isNotBlank(portValue)) {
            config.setPort(Integer.valueOf(portValue));
        } else {
            config.setPort(-1);
        }
        config.setStatus(status);
        if (ngrokConfig != null) {
            config.setId(ngrokConfig.getId());
        }
        config.setTunnelName(tunnelName);
        final Result<Boolean> booleanResult = SQLiteUtil.insertOrUpdate(config);
        if (booleanResult.isSuccess()) {
            if (startMap) {
                if (NgrokStatusEnum.RUNNING.getStatus().equals(config.getStatus())) {
                    DialogUtil.success("映射成功！地址：" + publicUrl);
                    getPublicUrlPanel(config);
                    boxView.add(publicUrlPanel, 4);
                    updateUI();
                } else {
                    DialogUtil.error(config.getPort() + "映射失败！");
                }
            } else {
                DialogUtil.success("保存成功！");
            }
        }
    }

    /**
     * @param ngrokConfig
     * @return
     */
    private void getPublicUrlPanel(NgrokConfigEntity ngrokConfig) {
        //判断是否在运行中
        final List<TunnelInfo> tunnelInfos = NgrokUtil.getTunnelList(ngrokConfig.getApiAccessToken());
        if (CollUtil.isNotEmpty(tunnelInfos)) {
            isRunning = true;
            final TunnelInfo tunnelInfo = tunnelInfos.get(0);
            final JLabel labelView = new JLabel(getHostDesc(tunnelInfo, ngrokConfig));
            final LinkLabel stopBtn = new LinkLabel("停止", "", es -> {
                final boolean isStopSuccess = NgrokUtil.stopSession(ngrokConfig.getApiAccessToken(), tunnelInfo.getTunnelSession().getId());
                if (isStopSuccess) {
                    DialogUtil.success("停止映射成功！");
                    publicUrlPanel.removeAll();
                    updateUI();
                }
            });
            JLabel copy = new JLabel(" 复制");
            copy.setForeground(Color.BLUE);
            copy.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            copy.setFont(new Font("Helvetica", Font.PLAIN, 12));
            copy.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    //单击选中
                    if (e.getClickCount() == 1) {
                        String clipboardStr = tunnelInfo.getPublicUrl().trim();
                        SystemUtil.setClipboardString(clipboardStr);
                        copy.setText("已复制");
                    }
                }
            });
            final JLabel labelTitle = new JLabel("映射地址：");
            publicUrlPanel.removeAll();
            publicUrlPanel.add(labelTitle);
            publicUrlPanel.add(labelView);
            publicUrlPanel.add(stopBtn);
            publicUrlPanel.add(copy);
        }
    }

    /**
     * @param tunnelInfo
     * @param ngrokConfig
     * @return
     */
    private String getHostDesc(TunnelInfo tunnelInfo, NgrokConfigEntity ngrokConfig) {
        StringBuilder sb = new StringBuilder();
        sb.append(tunnelInfo.getPublicUrl());
        sb.append(" > ");
        if (StrUtil.isBlank(tunnelInfo.getForwardsTo())) {
            sb.append("http://loclahost:");
            sb.append(ngrokConfig.getPort());
        } else {

            sb.append(tunnelInfo.getForwardsTo());
        }
        return sb.toString();
    }
}
