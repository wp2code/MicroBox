package com.wb2code.microbox.config;

import lombok.Builder;
import lombok.Getter;

import java.awt.*;

/**
 * 全局配置
 *
 * @author lwp
 * @date 2022-10-12
 */
@Builder
@Getter
public class MicroToolGlobalConfig {
    private String title;
    private String icon;
    private Dimension dimension;
}
